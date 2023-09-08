package com.huizi.easydinner.util;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @PROJECT_NAME: easy-dinner
 * @DESCRIPTION:上传文件工具类
 * @AUTHOR: 12615
 * @DATE: 2023/9/8 10:58
 */
public class FileUtils {

    public static void uploadByFilePath(MultipartFile file, String filePath) throws FileUploadException {
        if (file.isEmpty()) {
            throw new FileUploadException("上传文件为空");
        }
        // 获取文件名
        String originalFileName = file.getOriginalFilename();
        // 生成UUID作为文件名
        long timestamp = System.currentTimeMillis();
        // 获取文件扩展名
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // 拼接新文件名
        String fileName = String.valueOf(timestamp) + extension;
        File dest = null;
        dest = new File(filePath + "/" + fileName);

        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            // 保存文件
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downLoadByFilePath(String filePath) throws FileUploadException {
        
    }
}
