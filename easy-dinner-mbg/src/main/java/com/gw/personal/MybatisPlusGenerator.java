package com.gw.personal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Collections;

/**
 * @PROJECT_NAME: personal
 * @DESCRIPTION:mybatis-plus代码生成器
 * @AUTHOR: 12615
 * @DATE: 2022/12/15 17:58
 */
public class MybatisPlusGenerator {

    public static void main(String[] args) {
        final String url = "jdbc:mysql://localhost:3306/personal?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
        final String user = "root";
        final String password = "123456";
        FastAutoGenerator.create(url, user, password)
                .globalConfig(builder -> {
                    builder.author("gw") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .disableOpenDir() //禁止打开输出目录
                            .outputDir(System.getProperty("user.dir") + "/personal-admin/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.gw.personal") // 设置父包名
                            .controller("controller")
                            .service("service")
                            .serviceImpl("service.impl")
                            .mapper("mapper")
                            .xml("mapper.xml")
                            .entity("entity") //设置entity包名
                            .other("dto") // 设置dto包名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, System.getProperty("user.dir") + "/personal-admin/src/main/resources/mapper")); // 设置mapperXml生成路径

                })
                .strategyConfig(builder -> {
                    builder/*.addInclude("t_simple") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_") // 设置过滤表前缀*/
                            .entityBuilder().addTableFills(
                            new Column("create_time", FieldFill.INSERT), new Column("update_time", FieldFill.INSERT_UPDATE))
                            .idType(IdType.AUTO)
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel);
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
