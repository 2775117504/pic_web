package com.picweb.dao.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * ==============================================上传时间戳表对应的实体类==================================================
 */
@Entity //  用于标记一个类是 JPA 实体类，表示该类映射到数据库中的表。
@Table(name = "img_upload_date")
public class ImgUploadDateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //表示主键由数据库自动生成!!!
    private Integer id;
    @Column(name = "date", updatable = false,insertable = false) //表示该字段由数据库自动管理，Java 程序不参与插入或更新！！  //nullable = true
//    @Temporal(TemporalType.TIMESTAMP)  //用于将 Date 类型正确映射到数据库的 TIMESTAMP 或 DATETIME 类型。
    private LocalDateTime date;

    public ImgUploadDateEntity(Integer  id, LocalDateTime date) {
        this.id = id;
        this.date = date;
    }

    public ImgUploadDateEntity() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
