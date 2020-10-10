package com.china.unicom.mqtt.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;

/**
 * @Author: lifei
 * @Description:
 * @Date: 2020/9/17
 */
@Data
public class JsonObjectMapper {
    private static ObjectMapper objectMapper = buildObjectMapper();

    public JsonObjectMapper() {

    }

    public static ObjectMapper getInstance() {
        return objectMapper;
    }

    public static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }
}
