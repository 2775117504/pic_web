package com.picweb.dao.entity;

import com.picweb.converter.BooleanToTinyIntConverter;

import javax.persistence.*;

/**
 * =================================hash值表对应的实体类===================================
 */
@Entity //  用于标记一个类是 JPA 实体类，表示该类映射到数据库中的表。
@Table(name = "images")
public class ImageHashEntity {
    @Id
    private String MD5;
    private String ahash;
    private String phash;
    private String url;
    private Integer headurl;
    @Column(name = "temp")
    @Convert(converter = BooleanToTinyIntConverter.class) // 用于将布尔值转换为 TinyInt 类型
    private Boolean temp;

    @Column(name = "img_date_id")
    private Integer img_date_id;
//  构造函数
public ImageHashEntity(String MD5, String ahash, String phash, String url,  Integer headurl) {
    this.MD5 = MD5;
    this.ahash = ahash;
    this.phash = phash;
    this.url = url;
    this.headurl = headurl;
}

    public ImageHashEntity(String MD5, String ahash, String phash,  String url, Boolean temp) {
        this.MD5 = MD5;
        this.ahash = ahash;
        this.phash = phash;
        this.url = url;
        this.temp = temp;
    }

    public ImageHashEntity(String md5, String ahash, String phash, String url, Integer head, boolean b) {
        this.MD5 = md5;
        this.ahash = ahash;
        this.phash = phash;
        this.url = url;
        this.headurl = head;
    }
    public ImageHashEntity() {

    }


    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getAHash() {
        return ahash;
    }

    public void setAHash(String ahash) {
        this.ahash = ahash;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhash() {
        return phash;
    }

    public void setPhash(String phash) {
        this.phash = phash;
    }

    public Integer getHeadurl() {
        return headurl;
    }

    public Boolean getTemp() {
        return temp;
    }

    public void setTemp(Boolean temp) {
        this.temp = temp;
    }

    public Integer getImg_date_id() {
        return img_date_id;
    }

    public void setImg_date_id(Integer img_date_id) {
        this.img_date_id = img_date_id;
    }
}
