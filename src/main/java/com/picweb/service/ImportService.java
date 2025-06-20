package com.picweb.service;

import com.picweb.dao.ImageHashDao;
import com.picweb.dao.entity.ImageHashEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * ===============================================这是一个导入图源的核心业务服务层(图片数据导入数据库的直接实现)=======================================================
 * */
@Service //用于标记一个类是 Spring 组件，表示该类是 Spring 容器中的一个Bean，可以注入到其他Bean中。
public class ImportService {
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
    public Map<String, Object> upload(BufferedImage file, String ahash, String phash, String MD5, String url, String headurl, Integer TotalCount){
        Map<String, Object> map = new HashMap<>();

        /**
         * if (file.isEmpty()) {
         *             return "文件为空，请选择一个文件上传。"; //这里返回后直接跳出函数，不执行之后的语句
         *         }
         */
//            String uploadDir = System.getProperty("user.dir") + "/uploads/"; //获取当前项目路径
//            File dir = new File(uploadDir); //创建上传路径
//            if (!dir.exists()) { //判断路径是否存在
//                dir.mkdirs();
//            }
        if (imageHashDao.existsById(MD5)) {
            map.put("Message", "已存在文件: " + url);
            map.put("TotalCount", TotalCount);
            return map;
        }
        Integer head=headurl.length()+1 ;
        // 保存图源信息到到数据库
        ImageHashEntity imageHashEntity = new ImageHashEntity(MD5, ahash, phash,url,head,false);
        imageHashDao.save(imageHashEntity);
        map.put("Message", "已导入图源: " + url);
        map.put("TotalCount", TotalCount);
        return map;
    }
}
