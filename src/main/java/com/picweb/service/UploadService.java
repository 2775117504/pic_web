package com.picweb.service;

import com.picweb.dao.ImageHashDao;
import com.picweb.dao.entity.ImageHashEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * =====================================================这是一个上传图片的核心业务服务层(上传服务器的直接实现)==========================================================
 * */
@Service //用于标记一个类是 Spring 组件，表示该类是 Spring 容器中的一个Bean，可以注入到其他Bean中。
public class UploadService {
    /**
     *    判断上传的文件是否为图片的函数
     */
    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    /**
     *    hash(批量)上传图片的函数
     */
    @Autowired
    private ImageHashDao imageHashDao;
    /*public String upload(MultipartFile file, String ahash, String phash, String MD5){
        if (file.isEmpty()) {
            return "文件为空，请选择一个文件上传。";
        }
        if (imageHashDao.existsById(MD5)) {
            return "文件已存在：" + file.getOriginalFilename();
        }
        // 获取所有已存储的图片哈希
        List<ImageHashEntity> all = imageHashDao.findAll();

        Map<String, String> map = new HashMap<>();
        // 循环遍历每个哈希
        for (int i = 0; i < all.size(); i++) {
            ImageHashEntity entity = all.get(i);

            String sqlAHash = entity.getAHash();
            double hamming = ImageaHashService.hammingDistance(sqlAHash, ahash);

            if (hamming >= 65.0) {
                map.put("源路径", entity.getUrl());
                if (i == all.size() - 1) {
                    return "";
                }
            }
        }

        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/"; //获取当前项目路径
            File dir = new File(uploadDir); //创建上传路径
            if (!dir.exists()) { //判断路径是否存在
                dir.mkdirs();
            }

            String filePath = uploadDir + file.getOriginalFilename(); //获取上传文件的保存路径
            file.transferTo(new File(filePath)); //将上传的文件保存到指定路径

            *//**
             * 等价于 ImageHashDao.save(new ImageHashEntity(MD5, hash));
             *//*
            ImageHashEntity imageHashEntity = new ImageHashEntity(MD5, ahash, phash);
            imageHashDao.save(imageHashEntity);

            return "文件上传成功：" + file.getOriginalFilename();

        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败：" + e.getMessage();
        }
    }*/
    public Map<String, Object> upload(MultipartFile file, String ahash, String phash, String MD5){
        Map<String, Object> map = new HashMap<>();
        List<String> urls = new ArrayList<>();
        List<String> hanmings = new ArrayList<>();
        Map<List<String>, List<Double>> UAndH = new HashMap<>();


        if (file.isEmpty()) {
            map.put("Message", "文件为空，请选择一个文件上传。");
            return map;
        }
        if (imageHashDao.existsById(MD5)) {
            map.put("Message", "文件已存在：" + file.getOriginalFilename());
            return map;
        }
        // 获取所有已存储的图片哈希
        List<ImageHashEntity> all = imageHashDao.findAll();

        // 循环遍历每个哈希
        for (int i = 0; i < all.size(); i++) {
            ImageHashEntity entity = all.get(i);

            String sqlPhash = entity.getPhash();
            double hamming = ImageaHashService.hammingDistance(sqlPhash, phash);
//            System.out.println("相似度：" + hamming);
            if (hamming >= 55.0) {
                /*if (entity.getUrl().contains("\\")){
                    String sqlUrl = entity.getUrl().substring(3);
                    urls.add(sqlUrl);
                    hanmings.add(hamming);
                }else {*/
                urls.add(entity.getUrl());
                hanmings.add(String.format("%.2f", hamming)); //保留两位小数
            }
            if (i == all.size() - 1 && !urls.isEmpty()) {

                map.put("SourceUrl", urls);
                map.put("Hanming", hanmings);
                map.put("Message", "匹配到相似图："+  file.getOriginalFilename());
                map.put("ImgName",file.getOriginalFilename());
                System.out.println("map：" + map);
                return map;
            }
        }

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
            ImageHashEntity imageHashEntity = new ImageHashEntity(MD5, ahash, phash, file.getOriginalFilename());
            imageHashDao.save(imageHashEntity);

            map.put("Message", "文件上传成功：" + file.getOriginalFilename());
            return map;

        } catch (IOException e) {
            e.printStackTrace();
            map.put("Message", "文件上传失败：" + e.getMessage());
            return map;
        }
    }
}
