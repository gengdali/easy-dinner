package com.huizi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @PROJECT_NAME: personal
 * @DESCRIPTION:
 * @AUTHOR: 12615
 * @DATE: 2023/5/18 11:03
 */
@SpringBootApplication
@MapperScan("com.huizi.easydinner.*.mapper")
@EnableConfigurationProperties
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
