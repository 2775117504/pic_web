package com.picweb.dao;

import com.picweb.dao.entity.ImageHashEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Spring Data JPA 已经为所有继承 JpaRepository 的接口自动加上了 @Repository 注解。
 * 也就是说，只要你的接口继承了 JpaRepository，Spring 就会自动把它识别为一个 Repository Bean，
 * 并注入到容器中，无需手动添加 @Repository。所以添加 @Repository 注解可加可不加。
 */
/**
 *           方法  ———————————————————————  功能
 *       save(imageHash)             保存或更新一条记录
 *       findById(md5)               根据 MD5 查询记录
 *       findAll()                   查询所有记录
 *       deleteById(md5)             根据 MD5 删除记录
 *       existsById(md5)             判断是否存在某条记录
 * ！！！不需要手动写实现方法，因为 Spring Data JPA 已经帮你自动实现了这些常用的数据库操作方法！！！
 */

/**
 * ======================作为一个DAO接口，与数据库进行交互，操作 ImageHashEntity 实体类对应的数据表。==================
 */
@Repository //  用于标记一个类是 Spring 容器中的一个Bean，可以注入到其他Bean中。
public interface ImageHashDao extends JpaRepository<ImageHashEntity, String> {

    /**
     * ImageHashEntity：表示这个 DAO 操作的实体类。
     * String：表示该实体类的主键类型（即 @Id 字段的类型）。
     */
    /**    自定义方法实现:
     *     // 自动根据 MD5 查询 hash 值
     *     ImageHashEntity findByMD5(String MD5);
     *
     *     // 查询所有 hash 值
     *     List<ImageHashEntity> findAllByHash(String hash);
     */
//    public boolean existsById(String md5);
//    public void deleteById(String md5);
//    public ImageHashEntity save(ImageHashEntity imageHashEntity);

}
