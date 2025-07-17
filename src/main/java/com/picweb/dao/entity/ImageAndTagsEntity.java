package com.picweb.dao.entity;

import javax.persistence.*;


@Entity
@Table(name = "image_tags")
public class ImageAndTagsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //表示主键由数据库自动生成!!!
    private String itId;
    @Column(name = "image_id")
    private String imageHash;
    @Column(name = "tag_id")
    private Integer tagId;

    public ImageAndTagsEntity(String imageHash, Integer tagId) {
        this.imageHash = imageHash;
        this.tagId = tagId;
    }

    public ImageAndTagsEntity() {

    }

    public String getItId() {
        return itId;
    }

    public void setItId(String itId) {
        this.itId = itId;
    }

    public String getImageHash() {
        return imageHash;
    }

    public void setImageHash(String imageHash) {
        this.imageHash = imageHash;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }
}
