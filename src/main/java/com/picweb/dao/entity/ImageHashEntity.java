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
    private String hash;
//  构造函数
    public ImageHashEntity(String MD5, String hash) {
        this.MD5 = MD5;
        this.hash = hash;
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
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
