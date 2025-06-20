package com.picweb.service;

import com.picweb.dao.ImageHashDao;
import com.picweb.dao.ImgUploadDateDao;
import com.picweb.dao.entity.ImageHashEntity;
import com.picweb.dao.entity.ImgUploadDateEntity;
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

    public UploadService(ImageHashDao imageHashDao) {
        this.imageHashDao = imageHashDao;
    }
    private final ImageHashDao imageHashDao;
    @Autowired
    private ImgUploadDateDao imgUploadDateDao;
    public Map<String, Object> upload(MultipartFile file, String ahash, String phash, String MD5, Integer TotalCount){
        Map<String, Object> map = new HashMap<>();
        List<String> urls = new ArrayList<>();
        List<String> hanmings = new ArrayList<>();
        Map<List<String>, List<Double>> UAndH = new HashMap<>();


        if (file.isEmpty()) {
            map.put("Message", "文件为空，请选择一个文件上传。");
            return map;
        }
        if (imageHashDao.existsById(MD5)) {
            map.put("Message", "已存在文件: " + file.getOriginalFilename());
            map.put("TotalCount", TotalCount);
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
                map.put("Message", "匹配到相似图: "+  file.getOriginalFilename());
                map.put("ImgName",file.getOriginalFilename());
                map.put("TotalCount", TotalCount);
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
            ImageHashEntity imageHashEntity = new ImageHashEntity(MD5, ahash, phash, file.getOriginalFilename(), false);
            /**
            从时间戳表中获取最新条数据的主键id并添加进图片数据表的外键中
            */
            ImgUploadDateEntity img_date = imgUploadDateDao.findLatestByDate();
            Integer date_id = img_date.getId();
            imageHashEntity.setImg_date_id(date_id);
            imageHashDao.save(imageHashEntity);

            map.put("Message", "上传成功: " + file.getOriginalFilename());
            map.put("TotalCount", TotalCount);
            return map;

        } catch (IOException e) {
            e.printStackTrace();
            map.put("Message", "上传失败: " + e.getMessage());
            map.put("TotalCount", TotalCount);
            return map;
        }
    }
}
