package com.picweb.controller;

import com.picweb.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
    private ImageHashService imageHashService;
    @Autowired
    private ImageMD5Service imageMD5Service;
    @Autowired
    private SseController sseController;

    @Autowired
    private AsyncService asyncService;
    /*==========================================导入图片数据至数据库的控制器===========================================*/
    @PostMapping ("/imageSourceUpload")
    @ResponseBody
    public String ImageSourceUpload(@RequestParam("ImageSource") MultipartFile[] files,  //<<<-----RequestParam连个值对应前端formdata的两个键(key),没错还可以这么用
                                    @RequestParam("relativePath") String[] relativePaths) throws IOException {

        for (int i = 0; i < files.length && i < relativePaths.length; i++) {
            MultipartFile file = files[i];
            String relativePath = relativePaths[i];

            asyncService.ImportAsync(file, relativePath);
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
/*==================================================上传图片的控制器=====================================================*/
    @PostMapping("/imageUpload")
    @ResponseBody          /*将方法的返回值直接写入 HTTP 响应体中，而不是作为视图名称解析。*/
    public String ImageUpdate(@RequestParam("Image") MultipartFile[] files,
                              @RequestParam("Url") String url) throws IOException {
        StringBuilder result = new StringBuilder();  /*StringBuilder 是用来高效拼接字符串的工具类*/
/**
           例子
 * //        StringBuilder sb = new StringBuilder();
 * //        sb.append("Hello");
 * //        sb.append(" ");
 * //        sb.append("World");
 * //        System.out.println(sb.toString()); // 输出：Hello World
**/

        for (MultipartFile file : files) {
            if (!uploadService.isImageFile(file)) { // 判断是否为图片文件并过滤
                continue;
            }
            /**
             * 调用service层calculateMD5方法来获取图片的MD5值
             */
            String md5 = imageMD5Service.calculateMD5(file);

            /**
             * 因为函数无法处理MultipartFile类型文件，所以先转换成BufferedImage类型
             * 此处的getInputStream()为一次性流，待优化。
             * byte[] fileBytes = file.getBytes(); // 把整个文件读入内存
             * 暂时以把处理MD5放在前作为临时手段
             */
            BufferedImage image=null;
            try {
                image = ImageIO.read(file.getInputStream());
            } catch (IOException e) {
                System.out.println("图片格式转换出错!!!");
                throw new RuntimeException(e);
            }

            /**
             * 调用service层averageHash方法来获取图片的hash值
             */
            String hash = imageHashService.averageHash(image);
            /**
             *调用service层upload方法
             **/
            String res = uploadService.upload(file,hash,md5);
            sseController.sendMessageToAllClients(res);

        }
        /**
         *sse所有消息逐步推送完成后的最终输出
         */
        sseController.sendMessageToAllClients("图片上传完成！");
        return "OK";
    }
}
