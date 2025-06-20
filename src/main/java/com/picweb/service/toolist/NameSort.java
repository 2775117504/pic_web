package com.picweb.service.toolist;

import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class NameSort {

    /**
     * =================================================对任意嵌套结构的数据进行自适应排序=====================================================
     *
     * @param data  数据对象，可以是 Map、List 或其嵌套结构
     * @param key   排序依据的字段名（为 null 时按 Map 的 key 排序）
     * @param asc   是否升序排序
     * @return      排序后的结果
     */
    public  Object deepSort(Object data, String key, boolean asc) {
        // 处理 List 类型
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            List<Object> sortedList = new ArrayList<>(list);

            // 如果列表中的元素是复杂结构，则递归处理每个元素
            for (int i = 0; i < sortedList.size(); i++) {
                Object item = sortedList.get(i);
                if (item instanceof Map || item instanceof List) {
                    sortedList.set(i, deepSort(item, key, asc));
                }
            }

            // 如果指定了 key，则按 key 排序
            sortedList.sort((o1, o2) -> compareObjects(o1, o2, key, asc));
            if (!asc) Collections.reverse(sortedList);
            return sortedList;

            // 处理 Map 类型
        } else if (data instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) data;
            Map<String, Object> sortedMap = new LinkedHashMap<>();

            List<String> keys = new ArrayList<>();
            for (Object k : map.keySet()) {
                if (k instanceof String) {
                    keys.add((String) k);
                }
            }

            // 按 key 字典序排序
            Collections.sort(keys);

            // 递归处理每个值
            for (String k : keys) {
                Object value = map.get(k);
                if (value instanceof Map || value instanceof List) {
                    sortedMap.put(k, deepSort(value, null, true)); // 继续递归处理
                } else {
                    sortedMap.put(k, value);
                }
            }

            return sortedMap;
        }

        // 其他基础类型直接返回
        return data;
    }

    /**
     * 比较两个对象的大小，支持 Map 和基础类型
     */
    private static int compareObjects(Object o1, Object o2, String key, boolean asc) {
        Comparable val1 = extractValue(o1, key);
        Comparable val2 = extractValue(o2, key);

        if (val1 == null && val2 == null) return 0;
        if (val1 == null) return -1;
        if (val2 == null) return 1;

        return asc ? val1.compareTo(val2) : val2.compareTo(val1);
    }

    /**
     * 提取对象中指定字段的值
     */
    private static Comparable extractValue(Object obj, String key) {
        if (obj instanceof Map && key != null) {
            return (Comparable) ((Map<?, ?>) obj).get(key);
        } else if (obj instanceof List || obj instanceof Map) {
            return null; // 不对集合本身比较
        } else {
            return (Comparable) obj;
        }
    }
}
