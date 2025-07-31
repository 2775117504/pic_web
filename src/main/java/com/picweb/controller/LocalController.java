package com.picweb.controller;

import com.picweb.dao.entity.ImageHashEntity;
import com.picweb.dao.entity.ImgUploadDateEntity;
import com.picweb.service.locallib.TempService;
import com.picweb.service.toolist.NameSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/LocalLib")
public class LocalController {
    @GetMapping("/")
    public String LocalLib() {
        return "LocalLib";
    }
    @Autowired
    private TempService tempService;
    @Autowired
    private NameSort nameSort;
    // 呈现临时图片的接口
    @GetMapping("/temp")
    @ResponseBody
    public Map<Object, Object> temp(@RequestParam(name = "dateNum", required = false) Integer dateNum,
                                    @RequestParam(name = "pageNum", defaultValue = "1") int pageNum) {   // RequestParam注解用于将 请求参数 绑定到方法参数上。url带问号
        Map<Object, Object> map = new HashMap<>();
        // 获取所有时间戳并发送给前端
        if (dateNum == null){
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 修改为使用DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //java11新版语言特性，进行时间格式化
            for (ImgUploadDateEntity date: tempService.getDates()){
                map.put(date.getId(),formatter.format(date.getDate())); // 将LocalDateTime转换为字符串
            }
//        map.put("tempUrl", nameSort.deepSort(urls, null, true)); //为图片排序并返回所有临时图片
            return map;
        // 获取指定时间戳的图片并返回给前端
        }else {
            List<List> urlsAndMD5s = new ArrayList<>();
            List<ImageHashEntity> dateImgs = tempService.getTempImages(dateNum, pageNum);
            for (ImageHashEntity dateImg: dateImgs){
                List<String> urlAndMD5 = new ArrayList<>();
                urlAndMD5.add(dateImg.getUrl());
                urlAndMD5.add(dateImg.getMD5());
                urlsAndMD5s.add(urlAndMD5);
            }
            System.out.println(urlsAndMD5s);
            map.put("urlsAndMD5s", urlsAndMD5s);
            return map;
        }
    }
    // 添加标签
    @PostMapping("/addTag")
    @ResponseBody
    public String addTag(@RequestBody Map<String, Object> tag) { //RequestBody通常用于处理json数据，将json数据映射为Map对象
        System.out.println("传入的tag有: "+tag.get("tags"));
        System.out.println("被选中图片的md5有: "+tag.get("md5s"));
        tempService.uploadTag(tag.get("tags").toString(), (List<String>) tag.get("md5s"));
        return "ok";
    }


    @GetMapping("/refreshTag")
    @ResponseBody
    public Map<Object, Object> LocalLibTag() {
        Map<Object, Object> map = new HashMap<>();
        int i = 0;
        for (Integer id: tempService.getTagsIdList()){
            map.put(id, tempService.getTagsList().get(i));
            i++;
        }
        return map;
    }

    @GetMapping("/tag/{key}")
    @ResponseBody
    public List<String> tag(@PathVariable Integer key) {
        List<String> md5s = tempService.getImageMD5s(key);
        List<String> urls = new ArrayList<>();
        for (String md5: md5s){
            urls.add(tempService.getTImageUrls(md5));
        }
        return urls;
    }
}
