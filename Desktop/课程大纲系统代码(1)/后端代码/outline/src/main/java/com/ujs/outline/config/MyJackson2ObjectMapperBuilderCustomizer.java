package com.ujs.outline.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.core.Ordered;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

/**
 * 配置jackson objectMapper
 */
@Component
public class MyJackson2ObjectMapperBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer, Ordered {
    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        //枚举序列化时输出字符
        jacksonObjectMapperBuilder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        jacksonObjectMapperBuilder.featuresToEnable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        jacksonObjectMapperBuilder.timeZone(TimeZone.getTimeZone("GMT+8"));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}