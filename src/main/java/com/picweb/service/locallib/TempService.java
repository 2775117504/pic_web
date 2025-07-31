package com.picweb.service.locallib;

import com.fasterxml.jackson.core.type.TypeReference;
import com.picweb.dao.ImageAndTagsDao;
import com.picweb.dao.ImageHashDao;
import com.picweb.dao.ImgUploadDateDao;
import com.picweb.dao.TagsDao;
import com.picweb.dao.entity.ImageAndTagsEntity;
import com.picweb.dao.entity.ImageHashEntity;
import com.picweb.dao.entity.ImgUploadDateEntity;
import com.picweb.dao.entity.TagsEntity;
import com.picweb.service.common.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Autowired
    private TagsDao tagsDao;
    @Autowired
    private ImageAndTagsDao imageAndTagsDao;
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
        //PageRequest.of(页码, 每页数量, 排序规则)
        Pageable pageable = PageRequest.of(0, 28, Sort.by("imgDateId").ascending());
        // 缓存未命中，从数据库查询数据
        Page<ImageHashEntity> page = imageHashDao.findByImgDateId(dateNum, pageable);
        List<ImageHashEntity> result = page.getContent(); // 获取当前页码查询的数据集
        System.out.println("输出的28张图"+result);
        // 如果有结果，写入缓存（1小时过期）
        if (result != null && !result.isEmpty()) {
            System.out.println("从数据库中查询，并写入缓存");
            redisCacheService.set(cacheKey, result, 3600, TimeUnit.SECONDS);
        }

        return result;
    }
    // 上传至标签数据库
    public void uploadTag(String tag, List<String> md5s){
        TagsEntity tagsEntity=new TagsEntity(tag);
        tagsDao.save(tagsEntity);
        for (String md5:md5s) {
            ImageAndTagsEntity imageAndTagsEntity=new ImageAndTagsEntity(md5,tagsEntity.getId());
            imageAndTagsDao.save(imageAndTagsEntity);
            System.out.println("成功把tag和其相对应的图片md5存入数据库！");
        }
    }
    //获取目前tagName列表
    public List<String> getTagsList() {
        List<TagsEntity> tags = tagsDao.findAll();
        List<String> tagNames=new ArrayList<>();
        for (TagsEntity tag:tags) {
            tagNames.add(tag.getTagName());
        }
        return tagNames;
    }
    //获取目前tagId列表
    public List<Integer> getTagsIdList() {
        List<TagsEntity> tags = tagsDao.findAll();
        List<Integer> tagIds=new ArrayList<>();
        for (TagsEntity tag:tags) {
            tagIds.add(tag.getId());
        }
        return tagIds;
    }
    //获取tag对应的图片列表
    public List<String> getImageMD5s(Integer tagId) {
        return imageAndTagsDao.findImageHashByTagId(tagId);
    }
    //获取tag对应的图片列表
    public String getTImageUrls(String MD5) {
        return imageHashDao.findImageUrlByMD5(MD5);
    }

}
