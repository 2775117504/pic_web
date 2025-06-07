package com.picweb.service;

import com.picweb.controller.SseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class AsyncService {
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
    @Async("taskExecutors")
    public void ImportAsync(MultipartFile file,String relativePath) throws Exception {

            StringBuilder  systemResult = new StringBuilder(); ////后端控制台: 打印至后端控制台

            if (!file.isEmpty()) {
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
                    BufferedImage image = ImageIO.read(file.getInputStream());
                    /**
                     * 调用service层averageHash方法来获取图片的hash值
                     */
                    String ahash = imageaHashService.averageHash(image);
                    String phash = imagepHashService.perceptualHash(image);

                    String res = importService.upload(file, ahash, phash,md5, relativePath);

                    systemResult.append(res); ////后端控制台: 打印至后端控制台
                    /**
                     * 调取SseController控制类层的函数，与前端的sse通信
                     */
                    sseController.sendMessageToAllClients(res);
                    System.out.println(systemResult); ////后端控制台: 打印至后端控制台
            }
    }
}
