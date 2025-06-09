package com.picweb.controller;

import com.picweb.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        System.out.println("刷新了一次主页");
        return "upload";
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

    /*======================================================================上传图片的控制器=================================================================*/
    @PostMapping("/imageSourceUpload")
    @ResponseBody
    public String ImageSourceUpload(@RequestParam("ImageSource") MultipartFile[] files,  //<<<-----RequestParam连个值对应前端formdata的两个键(key),没错还可以这么用
                                    @RequestParam("relativePath") String[] relativePaths) throws Exception {

        for (int i = 0; i < files.length && i < relativePaths.length; i++) {

            MultipartFile file =  files[i];
            String relativePath = relativePaths[i];

            asyncService.UploadAsync(file, relativePath);
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

    /*==================================================导入图源的控制器=====================================================*/
    @PostMapping("/imageUpload")
    @ResponseBody          /*将方法的返回值直接写入 HTTP 响应体中，而不是作为视图名称解析。*/
    public String ImageUpdate(@RequestParam("Image") MultipartFile[] files,
                              @RequestParam("Url") String url) throws Exception {

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
            for (String imagePath : imagePaths) {
                File file = new File(imagePath);
                BufferedImage image = ImageIO.read(file);
                asyncService.ImportAsync(image, imagePath,importImageMd5Service.getMD5(file));
            }
        }

        /*===============================================上传图片的控制器===================================================*/
        else if (files != null && Arrays.stream(files).anyMatch(file -> !file.isEmpty())) {
            System.out.println("进入了文件不为空的elseif语句："+files);
            System.out.println("进入了文件不为空的elseif语句："+files.length);
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
                String res = uploadService.upload(file, ahash, phash, md5);
                sseController.sendMessageToAllClients(res);
            }
        }else {
            System.out.println("进入全都为空的else语句"+url);
        }
        /**
        *sse所有消息逐步推送完成后的最终输出
        */
        sseController.sendMessageToAllClients("图片上传完成！");
        return "OK";
    }
}
