package com.picweb.service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ImageAndUrl {
    // 获取指定路径下所有图片的绝对路径（包括子目录）
    public static List<BufferedImage> getAllImagePaths(String url) throws IOException {
        List<String> imagePaths = new ArrayList<>();
        List<BufferedImage> image = new ArrayList<>();
        Path rootPath = Paths.get(url);

        if (!Files.exists(rootPath)) {
            throw new IllegalArgumentException("路径不存在: " + url);
        }

        // 遍历整个目录树，并筛选出图片文件
        Files.walk(rootPath)
                .filter(path -> !Files.isDirectory(path))  // 排除目录
                .filter(ImageAndUrl::isImageFile)       // 筛选图片文件
                .forEach(path -> imagePaths.add(path.toAbsolutePath().toString()));

        for (String imagePath : imagePaths){
            File file = new File(imagePath);
            image.add(ImageIO.read(file));
        }
        return image;
    }

    // 判断是否是图片文件（支持常见格式）
    public static boolean isImageFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".jpg") ||
                fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") ||
                fileName.endsWith(".gif") ||
                fileName.endsWith(".bmp") ||
                fileName.endsWith(".webp");
    }

    // 示例 main 方法测试
    public static void main(String[] args) {
        try {
            String folderPath = "C:\\Users\\lenovo\\Desktop\\图包"; // 替换为你的文件夹路径
            List<BufferedImage> images = getAllImagePaths(folderPath);

            System.out.println("找到 " + images.size() + " 张图片：");
            for (BufferedImage path : images) {
                System.out.println(path);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
