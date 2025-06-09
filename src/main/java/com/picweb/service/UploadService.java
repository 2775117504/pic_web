package com.picweb.service;

import com.picweb.dao.ImageHashDao;
import com.picweb.dao.entity.ImageHashEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * =========================这是一个上传图片的核心业务服务层(上传服务器的直接实现)==========================
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
    public String upload(MultipartFile file, String ahash, String phash, String MD5){
        if (file.isEmpty()) {
            return "文件为空，请选择一个文件上传。";
        }
        if (imageHashDao.existsById(MD5)) {
            return "文件已存在：" + file.getOriginalFilename();
        }
        // 获取所有已存储的图片哈希
        List<ImageHashEntity> all = imageHashDao.findAll();

        // 循环遍历每个哈希
        for (ImageHashEntity entity : all) {
            String sqlAHash = entity.getAHash();
            double hamming = ImageaHashService.hammingDistance(sqlAHash, ahash);
            if ( hamming >= 80.0 ){
                return "相似图:"+entity.getUrl()+"相似度:"+hamming;
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
            ImageHashEntity imageHashEntity = new ImageHashEntity(MD5, ahash, phash);
            imageHashDao.save(imageHashEntity);

            return "文件上传成功：" + file.getOriginalFilename();

        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败：" + e.getMessage();
        }
    }
}
