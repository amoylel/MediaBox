<p align="center">
<img src="image/cover.png" width="150">
</p>
<div align="center">
    <h1>媒体盒子</h1>
    <p>
        <a href="https://github.com/RyensX/MediaBox/releases/latest" style="text-decoration:none">
            <img src="https://img.shields.io/github/v/release/RyensX/MediaBox?display_name=release" alt="GitHub release (latest by date)"/>
        </a>
        <a href="https://github.com/RyensX/MediaBox/releases/latest" style="text-decoration:none" >
            <img src="https://img.shields.io/github/downloads/RyensX/MediaBox/total" alt="GitHub all downloads"/>
        </a>
        <a href="https://img.shields.io/badge/Android-5.0%2B-brightgreen" style="text-decoration:none" >
            <img src="https://img.shields.io/badge/Android-5.0%2B-brightgreen" alt="Android version"/>
        </a>
        <a href="LICENSE" style="text-decoration:none" >
            <img src="https://img.shields.io/github/license/RyensX/MediaBox" alt="GitHub license"/>
        </a>
	</p>
</div>

<p align="center"><font size="4">规范化媒体浏览器，不含广告，免费开源，便于学习Android开发。</font></p>

----

## 原理
根据[**插件API**](https://github.com/RyensX/MediaBoxPlugin)实现各个组件解析外部数据然后通过本项目的用户界面**规范化**展示媒体数据（和浏览器差不多,类似内容受限的浏览器）

## 功能
### 视频
1. 支持显示**排行榜**
2. 支持显示**每日更新**的番剧
3. 支持**分类查看**动漫
4. 支持**双指缩放**、**移动**、**旋转**视频
5. 支持视频**投屏**到电视
6. 支持部分视频**显示**、**发送弹幕**（需要数据源支持弹幕）
7. 支持输入某站弹幕链接播放网络弹幕（ 例如https://api.bilibili.com/x/v1/dm/list.so?oid=97495910 ）
8. 支持**追番**（数据保存在本地）
9. 支持显示**观看历史**记录
10. 支持显示**搜索历史**记录
11. 支持改变视频**播放速度**
12. 支持改变**视频**显示**比例**（16:9, 4:3, 全屏等）
13. 支持**本地记忆**视频**播放进度**
14. ......

### 其他媒体资源
暂未支持

## 使用效果
见[**插件API**](https://github.com/RyensX/MediaBoxPlugin)下的各个示例

## 安全说明

**请勿**私自**传播APK**安装包，Github仓库为唯一长期仓库，**请仅在Github仓库下载安装包**，请勿下载来历不明的插件，谨防恶意代码！

## 应用主要权限说明

### 存储

1. 读取存储卡中的内容：缓存动漫功能需要读取本地存储卡中缓存的视频文件
2. 修改或删除存储卡中的内容：缓存动漫功能需要修改记录缓存信息的xml文件

### 电话

1. 读取设备通话状态和识别码：友盟+SDK需要收集您的设备Mac地址、唯一设备识别码以提供统计分析服务

### 位置信息

1. 访问大致、确切位置：友盟+SDK会通过地理位置校准报表数据准确性，提供基础反作弊能力

### 其它应用功能

2. 防止手机休眠：投屏到电视功能需要
3. 允许接收WLAN多播：投屏到电视功能需要

## 附加说明

App本身不提供任何数据，请用户自行添加各类插件学习和体验

## 免责声明

1. 此软件**只提供数据展示**，**不提供原始数据**，和普通浏览器功能类似。
2. 此软件显示的所有内容，其**版权**均**归原作者**所有。
3. 此软件**仅可用作学习交流**，未经授权，**禁止用于其他用途**，请在下载**24小时内删除**。
4. 因使用此软件产生的版权问题，软件作者概不负责。

## 相关项目
- 本项目基于[Imomoe](https://github.com/SkyD666/Imomoe)
- [插件API](https://github.com/RyensX/MediaBoxPlugin)

## 构建相关
- [secret.gradle](doc/about_secret.gradle.md)文件

## 许可证

使用此软件代码需**遵循以下许可证协议**

[**GNU General Public License v3.0**](LICENSE)