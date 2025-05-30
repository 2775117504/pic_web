# <div style="text-align: center;font-family: r">*PiLib*</div>

[![](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/2775117504/pic_web)  
- *2025-5-28 by kwtx*
#  项目介绍
## 一、 基于页面的功能介绍
### 1. 主页
    - 上传图片分本地上传和url上传(可凭压缩包批量上传)

    - 上传后在点击提交前下方空白处为预览区域，云端解压后的图片也一众在下方有序排列

    - 对于提交后的图片系统首先会进行基于本地图库的相似性图片搜索查重，重复图片系统
      会于下方预览区图片侧边显示对应的本地图库的相似图，批量图片同理

    - 对于无重复图片系统会送至>>>https://saucenao.com/<<<进行联网二次搜索，每张
      图片基础标签由图集网站提供(基础标签只有以下类别：画师名字，图片角色，图片来源
      作品)

### 2. 登录
    - 暂时想不到用处，先当个摆设

### 3. 暂存区(位于主页左上角，与我的对称)
    - 此页面存放提交的图片，为临时存放区，目的在为每一张图片手动添加标签，支持批量
      操作，右键弹出操作菜单(添加标签，删除)。底部提交按钮提交所有选中图片

### 4. 我的(登录后的右上角显示用户id，点击后进入)
    - 此界面以有序美观方式显示本地图集(包括翻页功能，调整页面图片显示尺寸功能，搜索
      功能，和侧边栏tag选择功能)，可手动添加和更改标签，批量操作，页面图片右上角有
      管理按钮(仅添加标签)，批量操作类似于Windows资源管理器的通过ctrl和shift来选择
      文件
    - 点击一张图片进入单独页面，此页面包括所有此图片标签，外加一个删除功能

## 二、 数据库存储介绍

### 1. 图片hash值数据库 
    - 图片相似性原理为计算每一张图片的hash值，并使用汉明距离计算相似度。
    - 存储图片hash值和图片MD5值，图片MD5值作为主键
    - 相关文档：https://blog.csdn.net/YiWangJiuShiXingFu/article/details/89011495

### 2. 图片标签数据库
    - 存储图片MD5值和对应图片标签
    - 相关文档：https://ask.csdn.net/questions/8270106

#  项目备忘日志
## 1. *2025-5-30 16:05:09*
- 完成进度
  - 初步完成主页识图功能之对上传图片的hash值输出
- 待处理项
  - 主页对每张预览图的垂直居中处理
  - 主页url传入图片的功能
  - 主页异步处理上传图片并在logo右方实时输出进度
  - controller层UploadController.java类中的getInputStream()有一次性流的缺陷，需要处理
  - 主页查重后的相似图片显示于原图侧边，两张图属一个标签内，突出直观性
  - 主页加入耗时(计时器)显示
## 1. *2025-5-31 0:26:16*
- 完成进度
  - 完成对md5和hash值导入数据库的操作，成功创建hash数据库

