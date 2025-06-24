package com.picweb.service.locallib;

import com.fasterxml.jackson.core.type.TypeReference;
import com.picweb.dao.ImageHashDao;
import com.picweb.dao.ImgUploadDateDao;
import com.picweb.dao.entity.ImageHashEntity;
import com.picweb.dao.entity.ImgUploadDateEntity;
import com.picweb.service.common.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TempService {
    @Autowired
    private ImageHashDao imageHashDao;
    @Autowired
    private ImgUploadDateDao imgUploadDateDao;
    @Autowired
    private RedisCacheService redisCacheService;
    //获取所有临时图片的时间戳
    public List<ImgUploadDateEntity> getDates(){
        return imgUploadDateDao.findAll();
    }
    /** //根据日期编号id获取对应时间戳的临时图片
    public List<ImageHashEntity> getTempImages(Integer dateNum){
        return imageHashDao.findByImgDateId(dateNum);
    }*/

    // 带缓存的方法：根据日期 ID 获取临时图片列表
    public List<ImageHashEntity> getTempImages(Integer dateNum) {
        String cacheKey = "temp_images:date_id:" + dateNum;

        // 尝试从缓存中读取数据
        List<ImageHashEntity> cached = redisCacheService.getGeneric(cacheKey, new TypeReference<List<ImageHashEntity>>() {});
        if (cached != null && !cached.isEmpty()) {
            System.out.println("从缓存中获取数据");
            return cached;
        }

        // 缓存未命中，从数据库查询数据
        List<ImageHashEntity> result = imageHashDao.findByImgDateId(dateNum);

        // 如果有结果，写入缓存（1小时过期）
        if (result != null && !result.isEmpty()) {
            System.out.println("从数据库中查询，并写入缓存");
            redisCacheService.set(cacheKey, result, 3600, TimeUnit.SECONDS);
        }

        return result;
    }
}
