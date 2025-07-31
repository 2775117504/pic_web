# <div style="text-align: center;font-family: r">*PiLib*</div>

[![](https://img.shields.io/badge/项目地址-pic_web-blue.svg)](https://github.com/2775117504/pic_web)
![](https://img.shields.io/badge/language-java-orange.svg)

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
    - 供网络用户存储使用（最后设计增加此功能）
    - 非登录状态供本地私人使用

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

## 三、使用流程
### 1. 使用者为私人的本地流程
    - 非登录状态
    - Select Image左侧空白处实行拖拽上传一个Image Source(图源)，选择一个临时性图库，此图库上传后包含的所有图片的数
      据(包括本地绝对路径url)同样存于数据库。(此操作本质不是上传图片至服务器，而是一个获取本地图源包括md5，hash等存储
      数据至数据库的步骤)
    - 其次点击下方Select Image上传所需要查重的图片进行相似性搜索查重，加载出预览图点击提交，系统查出
      重复的图片其右边显示出对应的来自图源的相似图片，如果来自旧有图源，那么则输出他的tag
      以便核实。
    - 悬停于右上角我的选择第一项“本地用户”，进入图源页面，也就是“本地图库”的展示。
      

#  项目备忘日志
## 1. *2025-5-30 16:05:09*
- 完成进度
  - 初步完成主页识图功能之对上传图片的hash值输出
- 待处理项
  - 主页对每张预览图的垂直居中处理
  - 主页url传入图片的功能
  - 主页异步处理上传图片并在logo右方实时输出进度
  -  controller层UploadController.java类中的getInputStream()有一次性流的缺陷，需要处理
  - 主页查重后的相似图片显示于原图侧边，两张图属一个标签内，突出直观性
  - 主页加入耗时(计时器)显示
  - 主页加入以拖拽形式上传文件(可拖拽文件夹)
## 2. *2025-5-31 0:26:16*
- 完成进度
  - 完成对md5和hash值导入数据库的操作，成功创建hash数据库
## 3. *2025-6-5 23:44:56*
- 完成进度
  - 使用fetch实现前端异步请求，使用sse逐步推送技术，实现对图片上传的的实时进度显示。
- 待处理项
  - 图片导入后端异步通过async技术实现，但存在设置多少线程池就只能传入多少文件的问题，需要解决，且最大一次性传入文件数为100.
## 4. *2025-6-13 20:12:48*
- 完成进度
- 待处理项
  - 针对List<ImageHashEntity> all = imageHashDao.findAll();使用 局部敏感哈希（LSH）->LSH 可以将相似的哈希值映射到同一个桶中,查询时只需查找一个桶内的数据，大幅减少比对数量
  - 以缩略图形式展示图片，减少浏览器内存占用 
  - 针对第一条待处理想或加入向量索引ANN
  - 部署 CNN深度学习模型
  - 待支持webp格式图片 ✔ 
  - 本地路径包含[]符号时上传出错

#  项目bug日志
## 1. *2025-7-27 23:05:33*
- *异步竞态问题（Race Condition）*：
  - *问题描述*：当你切换标签过快时，前一个网络请求还未完成， 后一个请求到达后渲染内容，<br>
              却最终混入到上一个的响应内容，造成图片渲染错乱。
  - *解决方案*：引入abortController无法解决问题❌，所以引入全局变量解决<br>
            使用一个全局请求编号 currentRequestId，为每次标签切换分配唯一编号:<br>
            let currentRequestId = 0;<br>
            点击后全局变量自增,赋值给当前请求编号:<br>
            const thisRequestId = ++currentRequestId;<br>
            每个图片请求回来时，先判断：<br>
            if (thisRequestId !== currentRequestId) return;<br>

#  项目开发日志
## 1. *2025-7-27 23:05:33*
### 分页设计:
1.     //dao层启用jpa内置的分页功能
       Page<ImageHashEntity> findByImgDateId(Integer imgDateId, Pageable pageable);
2.     // 创建分页参数：第几页（从0开始）、每页条数、排序
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
3.     //调用dao层
        Page<ImageHashEntity> page = imageHashRepository.findByImgDateId(5, pageable);
4.     //拿数据
        List<ImageHashEntity> list = page.getContent();  // 当前页数据
        long totalElements = page.getTotalElements();    // 总数据条数
        int totalPages = page.getTotalPages();           // 总页数
- tip:
  -     //按多个字段排序:	
        SSort.by("field1").ascending().and(Sort.by("field2").descending())
  -     //前端用户动态改变排序方式:
        Sort sort = direction.equalsIgnoreCase("desc") ?
        Sort.by("url").descending() :
        Sort.by("url").ascending();


            


