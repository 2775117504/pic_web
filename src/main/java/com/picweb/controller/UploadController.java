package com.picweb.controller;

import com.picweb.dao.ImageHashDao;
import com.picweb.dao.ImgUploadDateDao;
import com.picweb.dao.entity.ImageHashEntity;
import com.picweb.dao.entity.ImgUploadDateEntity;
import com.picweb.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller     /*用于标记一个类是 Spring MVC 控制器（Controller），主要负责接收和处理 HTTP 请求，不会自动转为响应体*/

public class UploadController {
    //  重定向到上传页面
    @GetMapping("/")
    public String redirectToUploadPage() {
        return "redirect:/upload";
    }

    //  上传页面
    @GetMapping("/upload")
    public String uploadPage() {
        System.out.println("/////////////////////////////////////////////////////////////////////////////////////////////////////////////");
        System.out.println("刷新了一次主页");
        return "upload";
    }

    /**
     * **********************************  Controller层 动态返回图片  !!!!!!!!!!!!!!!!  ***********************************
     */
    @GetMapping("/im/{user}")
    public ResponseEntity<Resource> getImg(@PathVariable String user) throws IOException { //URL 中有具体资源名用@PathVariable
        System.out.println("这是：@GetMapping(\"/im/{user}\")"+user);

        int lastIndex = user.lastIndexOf('\\');
        if (lastIndex != -1) {
            String headPath = user.substring(0, lastIndex);
            String fileName = user.substring(lastIndex + 1);
        }
        // 定义支持的图片扩展名和对应 MediaType
        Map<String, MediaType> imageExt = new HashMap<>();
        imageExt.put(".jpg", MediaType.IMAGE_JPEG);
        imageExt.put(".jpeg", MediaType.IMAGE_JPEG);
        imageExt.put(".png", MediaType.IMAGE_PNG);
        imageExt.put(".gif", MediaType.IMAGE_GIF);
        imageExt.put(".bmp", MediaType.valueOf("image/bmp"));
        imageExt.put(".webp", MediaType.valueOf("image/webp"));

        // 尝试匹配支持的图片格式
        MediaType mediaType = null;
        Path imagePath = null;  //传统 File 类的 Plus 版本！

        for (Map.Entry<String, MediaType> entry : imageExt.entrySet()) {
            String ext = entry.getKey(); //获取键
            Path tryPath = Paths.get(user);
            if (Files.exists(tryPath)) {  //判断文件是否存在
                imagePath = tryPath;
                mediaType = entry.getValue();
                break;
            }
        }
        // 如果没有找到任何匹配的图片文件
        if (imagePath == null || mediaType == null) {
            throw new FileNotFoundException("找不到该用户对应的图片：" + user);
        }
        // 使用 Resource 包装文件输入流
        Resource resource = new InputStreamResource(Files.newInputStream(imagePath));
        // 返回响应
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }



    @Autowired             /*自动注入使用service层的uploadService类*/
    private UploadService uploadService;
    @Autowired
    private ImportService importService;
    //  文件上传处理
    @Autowired
    private ImageaHashService imageaHashService;
    @Autowired
    private ImageMD5Service imageMD5Service;
    @Autowired
    private ImagepHashService imagepHashService;
    @Autowired
    private SseController sseController;

    @Autowired
    private AsyncService asyncService;
    @Autowired
    private ImportImageMd5Service importImageMd5Service;

    /*======================================================================以拖拽形式上传图片的控制器=================================================================*/
    @PostMapping("/imageSourceUpload")
    @ResponseBody
    public String ImageSourceUpload(@RequestParam("ImageSource") MultipartFile[] files,  //<<<-----RequestParam连个值对应前端formdata的两个键(key),没错还可以这么用（仅表单提交）
                                    @RequestParam("relativePath") String[] relativePaths) throws Exception {
        for (int i = 0; i < files.length && i < relativePaths.length; i++) {

            MultipartFile file =  files[i];
            String relativePath = relativePaths[i];

            asyncService.UploadAsync(file, relativePath, files.length);
        }
            /*
            StringBuilder  systemResult = new StringBuilder(); ////后端控制台: 打印至后端控制台

            if (!file.isEmpty()) {
                *//**
         * 调用service层calculateMD5方法来获取图片的MD5值
         *//*
                String md5 = imageMD5Service.calculateMD5(file);
                *//**
         * 因为函数无法处理MultipartFile类型文件，所以先转换成BufferedImage类型
         * 此处的getInputStream()为一次性流，待优化。
         * byte[] fileBytes = file.getBytes(); // 把整个文件读入内存
         * 暂时以把处理MD5放在前作为临时手段
         *//*
                BufferedImage image=null;
                try {
                    image = ImageIO.read(file.getInputStream());
                } catch (IOException e) {
                    System.out.println("图片格式转换出错!!!");
                    throw new RuntimeException(e);
                }
                *//**
         * 调用service层averageHash方法来获取图片的hash值
         *//*
                String hash = imageHashService.averageHash(image);

                String res = importService.upload(file,hash,md5,relativePath);

                systemResult.append(res); ////后端控制台: 打印至后端控制台
                *//**
         * 调取SseController控制类层的函数，与前端的sse通信
         *//*
                sseController.sendMessageToAllClients(res);
                System.out.println(systemResult); ////后端控制台: 打印至后端控制台
            }*/

        sseController.sendMessageToAllClients("图源导入完成！"); //sse所有消息逐步推送完成后的最终输出
        System.out.println("图源导入完成!");////后端控制台: 打印至后端控制台
        return "OK";  // 返回 OK 表示请求成功，不用于前端显示

    }
/*====================================================================导入图源的控制器========================================================================*/
    @Autowired
    private ImgUploadDateDao imgUploadDateDao;
    @PostMapping("/imageUpload")
    @ResponseBody          /*将方法的返回值直接写入 HTTP 响应体中，而不是作为视图名称解析。*/
    public String ImageUpdate(@RequestParam(name="Image", required = false) MultipartFile[] files,  //设置false为非必要性传入参数
                              @RequestParam(name="Url", required = false) String url) throws Exception {

/**
 例子
 *         StringBuilder result = new StringBuilder();  //StringBuilder 是用来高效拼接字符串的工具类
 *         StringBuilder sb = new StringBuilder();
 *         sb.append("Hello");
 *         sb.append(" ");
 *         sb.append("World");
 *         System.out.println(sb.toString()); // 输出：Hello World
 **/
        //url绝对路径形式导入图源
        if (url != null && !url.isEmpty()) {
            System.out.println("进入了url不为空的if语句："+url);
            List<String> imagePaths = new ArrayList<>();

            Path rootPath = Paths.get(url);

            if (!Files.exists(rootPath)) {
                throw new IllegalArgumentException("路径不存在: " + url);
            }

            // 遍历整个目录树，并筛选出图片文件
            Files.walk(rootPath)
                    .filter(path -> !Files.isDirectory(path))  // 排除目录
                    .filter(ImageAndUrl::isImageFile)       // 筛选图片文件
                    .forEach(path -> imagePaths.add(path.toAbsolutePath().toString()));
            if (imagePaths.isEmpty()) {
                System.out.println("该目录没有图片文件");
                return "该目录没有图片文件";
            }
            Integer TotalCount = imagePaths.size();
            for (String imagePath : imagePaths) {
                File file = new File(imagePath);
                BufferedImage image =ImageIO.read(file);
                asyncService.ImportAsync(image, imagePath,importImageMd5Service.getMD5(file),url,TotalCount);
            }
        }

 /*================================================================以选择文件方式上传图片的控制器=============================================================*/
        else if (files != null && Arrays.stream(files).anyMatch(file -> !file.isEmpty())) {
            System.out.println("进入了文件不为空的elseif语句："+files);
            System.out.println("进入了文件不为空的elseif语句："+files.length);
            Integer TotalCount = files.length; //总图片数量
            ImgUploadDateEntity Entity = new ImgUploadDateEntity(); //为每一个上传批次添加一个时间戳
            imgUploadDateDao.save(Entity);
            for (MultipartFile file : files) {
                if (!uploadService.isImageFile(file)) { // 判断是否为图片文件并过滤
                    continue;
                }

                /**
                 * 因为函数无法处理MultipartFile类型文件，所以先转换成BufferedImage类型
                 * 此处的getInputStream()为一次性流，待优化。
                 * byte[] fileBytes = file.getBytes(); // 把整个文件读入内存
                 * 暂时以把处理MD5放在前作为临时手段
                 */
                BufferedImage image = ImageIO.read(file.getInputStream());
                /**
                 * 调用service层calculateMD5方法来获取图片的MD5值
                 */
                String md5 = imageMD5Service.calculateMD5(file);
                /**
                 * 调用service层averageHash方法来获取图片的hash值
                 */
                String ahash = imageaHashService.averageHash(image);
                String phash = imagepHashService.perceptualHash(image);
                /**
                 *调用service层upload方法
                 **/
                Map<String, Object> res = uploadService.upload(file, ahash, phash, md5,TotalCount);
                sseController.sendMessageToAllClients(res);
            }
        }else {
            System.out.println("进入全都为空的else语句"+url);
        }
        /**
        *sse所有消息逐步推送完成后的最终输出
        */
//        sseController.sendMessageToAllClients("图片上传完成！");
        return "OK";
    }

/**
    ============================================================重新上传图片的控制器=====================================================================
*/
    @Autowired
    public ImageHashDao imageHashDao;
    @PostMapping("/imageReUpload")
    @ResponseBody          /*将方法的返回值直接写入 HTTP 响应体中，而不是作为视图名称解析。*/
    public Map<String, Object> ImageReUpdate(@RequestParam(name="Image", required = false) MultipartFile file) throws Exception {
        BufferedImage image = ImageIO.read(file.getInputStream());
        /**
         * 调用service层calculateMD5方法来获取图片的MD5值
         */
        String md5 = imageMD5Service.calculateMD5(file);
        /**
         * 调用service层averageHash方法来获取图片的hash值
         */
        String ahash = imageaHashService.averageHash(image);
        String phash = imagepHashService.perceptualHash(image);
        /**
         *调用service层upload方法
         **/
        Map<String, Object> map = new HashMap<>();
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/"; //获取当前项目路径
            File dir = new File(uploadDir); //创建上传路径
            if (!dir.exists()) { //判断路径是否存在
                dir.mkdirs();
            }

            String filePath = uploadDir + file.getOriginalFilename(); //获取上传文件的保存路径
            file.transferTo(new File(filePath)); //将上传的文件保存到指定路径

            /**
             * 等价于 ImageHashDao.save(new ImageHashEntity(MD5, hash));
             */
            ImageHashEntity imageHashEntity = new ImageHashEntity(md5, ahash, phash, file.getOriginalFilename(), false);
            /**
             从时间戳表中获取最新条数据的主键id并添加进图片数据表的外键中
             */
            ImgUploadDateEntity img_date = imgUploadDateDao.findLatestByDate();
            Integer date_id = img_date.getId();
            imageHashEntity.setImgDateId(date_id);
            imageHashDao.save(imageHashEntity);

            map.put("Message", "重上传成功：" + file.getOriginalFilename());
            sseController.sendMessageToAllClients(map);

        } catch (IOException e) {
            e.printStackTrace();
            map.put("Message", "重上传失败：" + e.getMessage());
            sseController.sendMessageToAllClients(map);
        }
        return map;
    }
}
