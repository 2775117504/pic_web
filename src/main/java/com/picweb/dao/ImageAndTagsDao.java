package com.picweb.dao;

import com.picweb.dao.entity.ImageAndTagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageAndTagsDao extends JpaRepository<ImageAndTagsEntity, String> {
    @Query("SELECT i FROM ImageAndTagsEntity i WHERE i.imageHash = ?1")
    List<ImageAndTagsEntity> findByImageHash(String imageHash);
}
