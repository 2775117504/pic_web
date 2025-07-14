package com.picweb.dao.entity;

import javax.persistence.*;

@Entity
@Table(name = "tages")
public class TagsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //表示主键由数据库自动生成!!!
    private Integer id;
    @Column(name = "name")
    private String tagName;

    public TagsEntity(String tagName) {
        this.tagName = tagName;
    }
    public TagsEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
