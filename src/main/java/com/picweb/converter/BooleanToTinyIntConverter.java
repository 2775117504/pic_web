package com.picweb.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * 将 Boolean 类型转换为数据库中的 TINYINT（0 或 1），实体类和数据库的映射关系
 */
@Converter(autoApply = true)
public class BooleanToTinyIntConverter implements AttributeConverter<Boolean, Integer> {

    /**
     * 将 Boolean 转换为数据库存储值（true -> 1, false -> 0）
     */
    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        return (attribute != null && attribute) ? 1 : 0;
    }

    /**
     * 将数据库中的 TINYINT 值转换为 Boolean 类型
     */
    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        return dbData != null && dbData == 1;
    }
}
