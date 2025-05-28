package com.picweb.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class UploadController {
    @GetMapping("/")
    public String redirectToUploadPage() {
        return "redirect:/upload.html";
    }

    // 上传页面
    @RequestMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    // 文件上传处理
    @PostMapping("/upload")
    @ResponseBody
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "文件为空，请选择一个文件上传。";
        }

        try {
            // 创建上传目录（如果不存在）
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 保存上传文件到指定路径
            String filePath = uploadDir + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            return "文件上传成功：" + file.getOriginalFilename() + "，保存路径：" + filePath;

        } catch (IOException e) {
            // 捕获并处理IO异常
            e.printStackTrace();
            return "文件上传失败：" + e.getMessage();
        }
    }
}
