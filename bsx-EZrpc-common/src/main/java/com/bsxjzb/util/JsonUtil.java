package com.bsxjzb.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(dateFormat);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String objectToJson(Object o) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return json;
    }

    public static <T> T jsonToObject(String json, Class<?> clazz) {
        T res = null;
        JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
        try {
            res = objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return res;
    }
}
