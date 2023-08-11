package com.huizi.easydinner.config;


import com.huizi.easydinner.domain.SwaggerProperties;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger相关配置
 * Created by macro on 2018/4/26.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.huizi.easydinner")
                .title("后台系统")
                .description("后台相关接口文档")
                .contactName("gengwei")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }

}