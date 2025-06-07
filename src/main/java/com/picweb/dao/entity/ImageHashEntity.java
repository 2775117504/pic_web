package com.picweb.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * =================================hash值表对应的实体类===================================
 */
@Entity //  用于标记一个类是 JPA 实体类，表示该类映射到数据库中的表。
@Table(name = "md5_hash_url")
public class ImageHashEntity {
    @Id
    private String MD5;
    private String ahash;
    private String phash;



    private String url;
//  构造函数
public ImageHashEntity(String MD5, String ahash, String phash, String url) {
    this.MD5 = MD5;
    this.ahash = ahash;
    this.phash = phash;
    this.url = url;
}

    public ImageHashEntity(String MD5, String ahash, String phash) {
        this.MD5 = MD5;
        this.ahash = ahash;
        this.phash = phash;
    }

    public ImageHashEntity() {

    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getHash() {
        return ahash;
    }

    public void setHash(String hash) {
        this.ahash = hash;
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
}
