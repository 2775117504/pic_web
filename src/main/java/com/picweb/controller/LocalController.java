package com.picweb.controller;

import com.picweb.dao.entity.ImageHashEntity;
import com.picweb.service.locallib.TempService;
import com.picweb.service.toolist.NameSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @GetMapping("/temp")
    @ResponseBody
    public Map<String, Object> temp() {
        Map<String, Object> map = new HashMap<>();
        List<String> urls = new ArrayList<>();
        for (ImageHashEntity temp : tempService.getTemps()){
            urls.add(temp.getUrl());
        }
        map.put("tempUrl", nameSort.deepSort(urls, null, true)); //为图片排序并返回所有临时图片
        return map;
    }
    @GetMapping
    @ResponseBody
    public String LocalLibTag(@RequestParam("tag") String tag) {
        return "tag=" + tag;
    }
}
