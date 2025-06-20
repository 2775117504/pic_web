package com.picweb.service.locallib;

import com.picweb.dao.ImageHashDao;
import com.picweb.dao.entity.ImageHashEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TempService {
    @Autowired
    private ImageHashDao imageHashDao;
    public List<ImageHashEntity> getTemps(){
        List<ImageHashEntity> temps = imageHashDao.findAllTempImg();
        return temps;
    }
}
