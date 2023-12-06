package com.huizi.easydinner.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DirectoryToPdfUtils {

    public static void createPdfFromDirectory(String directoryPath, String outputPdfPath) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        PdfWriter writer = new PdfWriter(new FileOutputStream(outputPdfPath));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        processDirectory(directory, document);

        document.close();
    }

    private static void processDirectory(File directory, Document document) throws IOException {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                processDirectory(file, document);
            } else {
                document.add(new Paragraph(readFileContent(file)));
            }
        }
    }

    private static String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             FileChannel channel = fis.getChannel()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = channel.read(ByteBuffer.wrap(buffer))) != -1) {
                content.append(new String(buffer, 0, bytesRead));
            }
        }
        return content.toString();
    }
}
