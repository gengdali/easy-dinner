package com.huizi.easydinner.utils;

import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ZGQYB {
    public static void main(String[] args) {
        // 创建 HttpClient 实例
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建 HttpGet 请求
            HttpGet httpGet = new HttpGet("http://epaper.zqcn.com.cn/");

            // 设置请求头，指定字符编码
            httpGet.addHeader("Accept-Charset", "UTF-8");

            // 执行请求并获取响应
            CloseableHttpResponse response = httpClient.execute(httpGet);
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // 请求成功，获取页面跳转路径
                String responseBody = EntityUtils.toString(entity, "UTF-8");
                Document document = Jsoup.parse(responseBody);
                Element metaRefresh = document.select("meta[http-equiv=refresh]").first();
                if (metaRefresh != null) {
                    String content = metaRefresh.attr("content");
                    String url = content.split(";")[1].trim().split("=")[1];
                } else {
                    System.out.println("No REFRESH meta tag found.");
                }
                // 获取META标签中的URL
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
