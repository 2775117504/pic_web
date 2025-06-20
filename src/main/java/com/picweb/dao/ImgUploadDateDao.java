package com.picweb.dao;

import com.picweb.dao.entity.ImgUploadDateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository

public interface ImgUploadDateDao extends JpaRepository<ImgUploadDateEntity, Integer> {
    /**使用 HQL（Hibernate Query Language），而 HQL 并不支持 LIMIT 这个关键字。
    必须将查询标记为原生 SQL 查询
    并把语句写成真正的 SQL（表名、字段名对应数据库结构），而不是 JPQL/HQL（基于实体类和属性名）。*/
    @Query(value = "SELECT * FROM img_upload_date ORDER BY date DESC LIMIT 1", nativeQuery = true)
    ImgUploadDateEntity findLatestByDate();

}
