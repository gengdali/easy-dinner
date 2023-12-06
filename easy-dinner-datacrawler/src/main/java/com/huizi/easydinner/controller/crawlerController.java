package com.huizi.easydinner.controller;


import com.huizi.easydinner.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * <p>
 * 账号表 前端控制器
 * </p>
 *
 * @author gw
 * @since 2023-05-19
 */
@Api(tags = "爬虫")
@RestController
@RequestMapping("/crawler")
public class crawlerController {

    @ApiOperation(value = "爬取数据")
    @PostMapping(value = "/crawldata")
    public CommonResult crawldata(@RequestParam String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept-Charset", "UTF-8");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            // 请求成功，获取页面跳转路径
            String responseBody = null;
            try {
                responseBody = EntityUtils.toString(entity, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(responseBody);
        }

        return CommonResult.success("爬取成功");
    }

}
