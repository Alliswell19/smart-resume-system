package com.smartresume.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> parseJsonToMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("JSON解析失败: " + json, e);
        }
    }

    public static <T> List<T> parseList(String json, Class<T> clazz) {
        try {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(json, listType);
        } catch (Exception e) {
            return null;
        }
    }
}