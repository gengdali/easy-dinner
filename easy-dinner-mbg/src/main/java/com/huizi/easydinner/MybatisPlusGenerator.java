package com.huizi.easydinner;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;
import java.util.Scanner;


/**
 * @PROJECT_NAME: personal
 * @DESCRIPTION:mybatis-plus代码生成器
 * @AUTHOR: 12615
 * @DATE: 2022/12/15 17:58
 */

public class MybatisPlusGenerator {

    public static void main(String[] args) {

        String moduleName = scanner("模块名");
        String childrenSortPackageName = scanner("子级分类包名");
        String[] tableNames = scanner("表名，多个英文逗号分割").split(",");
        Props props = new Props("generator.properties");
        String driverName = props.getStr("dataSource.driverName");

        String username = props.getStr("dataSource.username");

        String url = props.getStr("dataSource.url");

        String basePackage = props.getStr("package.base");

        String password = props.getStr("dataSource.password");
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig.Builder(url, username, password).dbQuery(new MySqlQuery());

        FastAutoGenerator.create(dataSourceConfigBuilder)
                .globalConfig(builder -> {
                    builder.author("gengwei") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .dateType(DateType.TIME_PACK)//时间策略
                            .commentDate("yyyy-MM-dd") //格式化时间格式
                            .disableOpenDir() //禁止打开输出目录
                            .outputDir(System.getProperty("user.dir") + "/" + childrenSortPackageName + "/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent(basePackage) // 设置父包名
                            .controller("controller")
                            .service("service")
                            .serviceImpl("service.impl")
                            .mapper("mapper")
                            .xml("mapper.xml")
                            .entity("entity") //设置entity包名
                            .other("dto") // 设置dto包名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, System.getProperty("user.dir") + "/easy-dinner-admin/src/main/java/com/huizi/easydinner/mapper/xml")); // 设置mapperXml生成路径

                })
                .strategyConfig(builder -> {
                    builder.addInclude(tableNames) // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀*//*
                    builder.entityBuilder()
                            .idType(IdType.AUTO)
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .enableLombok();
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }


    /**
     * 读取控制台内容信息
     */
    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(("请输入" + tip + "："));
        if (scanner.hasNext()) {
            String next = scanner.next();
            if (StrUtil.isNotEmpty(next)) {
                return next;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }
}

