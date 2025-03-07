package com.ujs.outline;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ujs.outline.mapper")
public class OutlineApplication {

    public static void main(String[] args) {
        SpringApplication.run(OutlineApplication.class, args);
    }

}
