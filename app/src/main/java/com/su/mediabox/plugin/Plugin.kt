package com.su.mediabox.plugin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.*
import com.su.mediabox.App
import com.su.mediabox.bean.PluginInfo
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.view.activity.BasePluginActivity
import com.su.mediabox.pluginapi.IComponentFactory
import com.su.mediabox.pluginapi.components.IBaseComponent
import com.su.mediabox.util.Util.getSignatures
import com.su.mediabox.util.debug
import com.su.mediabox.util.goActivity
import com.su.mediabox.util.toLiveData
import com.su.mediabox.v2.view.activity.HomeActivity
import dalvik.system.PathClassLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

object PluginManager {

    private val componentFactoryPool = mutableMapOf<String, IComponentFactory>()
    private val componentPool =
        mutableMapOf<String, MutableMap<Class<out IBaseComponent>, IBaseComponent>>()

    /**
     * 最低支持的插件API版本
     */
    private const val minPluginApiVersion = 1

    val pluginDir = App.context.getExternalFilesDir("plugins")!!

    /**
     * Map<[BasePluginActivity.PLUGIN_ID],[PluginInfo]>
     */
    private val pluginDataFlow = MutableStateFlow(mutableMapOf<String, PluginInfo>())
    private val _currentLaunchPlugin = MutableLiveData<PluginInfo?>()
    private val pluginIntent = Intent(Constant.PLUGIN_DEBUG_ACTION)
    private val pluginWorkScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    val pluginLiveData: LiveData<List<PluginInfo>> = pluginDataFlow
        .map {
            it.values.toList()
        }
        .flowOn(Dispatchers.Default)
        .asLiveData()

    val currentLaunchPlugin = _currentLaunchPlugin.toLiveData()

    fun scanPlugin() {
        val packageManager = App.context.packageManager
        pluginWorkScope.launch {
            val plugins = mutableMapOf<String, PluginInfo>()
            //内部安装的插件，如果有外部相同包名的则会被覆盖以方便调试
            pluginDir.listFiles()?.apply {
                Log.d("内部插件数量", "$size")
            }?.forEach { pluginPackage ->
                parsePluginInfo(pluginPackage)?.also { plugins[it.id] = it }
            }
            //扫描已安装的，只在debug模式下有效以方便调试
            debug {
                packageManager.queryIntentActivities(pluginIntent, 0).apply {
                    Log.d("外部插件数量", "$size")
                }.forEach { info ->
                    parsePluginInfo(File(info.activityInfo.applicationInfo.sourceDir))?.also {
                        it.isExternalPlugin = true
                        plugins[it.id] = it
                    }
                }
            }
            pluginDataFlow.value = plugins
        }
    }

    fun parsePluginInfo(pluginPackage: File): PluginInfo? {
        App.context.packageManager.getPackageArchiveInfo(
            pluginPackage.absolutePath,
            PackageManager.GET_META_DATA
        )?.apply {
            return parsePluginInfo(this)
        }
        return null
    }

    fun parsePluginInfo(pluginPackageInfo: PackageInfo): PluginInfo? {
        val pluginApplicationInfo = pluginPackageInfo.applicationInfo
        val packageManager = App.context.packageManager
        //插件API版本标记
        val apiVersion =
            pluginApplicationInfo.metaData?.getInt("media_plugin_api_version", -1) ?: -1
        if (apiVersion == -1)
            return null
        //插件实现的组件工厂类(IComponentFactory)完整包名
        val apiImpl =
            pluginApplicationInfo.metaData?.getString("media_plugin_api_impl") ?: return null

        return PluginInfo(
            apiVersion,
            apiImpl,
            pluginApplicationInfo.packageName,
            pluginApplicationInfo.loadLabel(packageManager).toString(),
            pluginApplicationInfo.loadIcon(packageManager),
            pluginApplicationInfo.sourceDir,
            getSignatures(pluginPackageInfo)
        )
    }

    fun Context.launchPlugin(pluginInfo: PluginInfo?) {
        pluginInfo?.apply {
            _currentLaunchPlugin.value = this
            goActivity<HomeActivity>()
        }
    }

    fun initPluginEnv() {
        _currentLaunchPlugin.value = null
    }

    /**
     * 获取组件工厂实例
     */
    @Throws(Exception::class)
    fun PluginInfo.acquireComponentFactory(): IComponentFactory =
        componentFactoryPool[sourcePath] ?: run {

            //判定API版本
            if (apiVersion < minPluginApiVersion)
                throw RuntimeException("插件API版本过低，请联系插件作者升级")

            val pluginFile = File(sourcePath)
                .apply {
                    if (!exists() || !isFile)
                        throw RuntimeException("插件不存在")
                }

            val classLoader = PathClassLoader(pluginFile.path, App.context.classLoader)

            try {
                val clz = classLoader.loadClass(apiImpl)
                (clz.newInstance() as IComponentFactory).also {
                    componentFactoryPool[sourcePath] = it
                }
            } catch (e: Exception) {
                throw RuntimeException("插件工厂载入错误，请联系插件作者检查元信息")
            }
        }

    @Throws(Exception::class)
    inline fun <reified T : IBaseComponent> acquireComponent() =
        currentLaunchPlugin.value?.acquireComponent(T::class.java)
            ?: throw RuntimeException("当前未启动插件！")

    /**
     * 获取组件实例
     */
    @Throws(Exception::class)
    fun <T : IBaseComponent> PluginInfo.acquireComponent(clazz: Class<T>): T {
        val isSingleton =
            clazz.isAnnotationPresent(IComponentFactory.SingletonComponent::class.java)
        if (isSingleton) {
            //被标注为单例组件，从组件池查找
            componentPool[sourcePath]?.get(clazz)?.also { return it as T }
        }
        return acquireComponentFactory().createComponent(clazz)?.also { component ->
            if (isSingleton)
            //存入组件库
                (componentPool[sourcePath]
                    ?: mutableMapOf<Class<out IBaseComponent>, IBaseComponent>()
                        .also {
                            componentPool[sourcePath] = it
                        })[clazz] = component
        }
            ?: throw RuntimeException("当前插件未提供该组件")
    }

}