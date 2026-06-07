package com.smartresume.service.impl;

import com.smartresume.service.TextExtractionService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.HWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class TextExtractionServiceImpl implements TextExtractionService {
    
    private static final Logger log = LoggerFactory.getLogger(TextExtractionServiceImpl.class);
    
    @Override
    public String extractTextFromPdfOrWord(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        log.info("开始提取文件内容，文件名: {}", fileName);
        
        if (fileName == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        String lowerCaseName = fileName.toLowerCase();
        
        try (InputStream inputStream = file.getInputStream()) {
            if (lowerCaseName.endsWith(".pdf")) {
                return extractTextFromPdf(inputStream);
            } else if (lowerCaseName.endsWith(".docx")) {
                return extractTextFromDocx(inputStream);
            } else if (lowerCaseName.endsWith(".doc")) {
                return extractTextFromDoc(inputStream);
            } else if (lowerCaseName.endsWith(".txt")) {
                return extractTextFromTxt(inputStream);
            } else {
                throw new IllegalArgumentException("不支持的文件类型: " + fileName);
            }
        }
    }
    
    /**
     * 提取PDF文件内容
     */
    private String extractTextFromPdf(InputStream inputStream) throws Exception {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            String text = pdfStripper.getText(document);
            log.info("PDF文件内容提取成功，字符数: {}", text.length());
            return text.trim();
        } catch (Exception e) {
            log.error("PDF文件内容提取失败", e);
            throw new Exception("PDF文件内容提取失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 提取DOCX文件内容
     */
    private String extractTextFromDocx(InputStream inputStream) throws Exception {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String text = extractor.getText();
            log.info("DOCX文件内容提取成功，字符数: {}", text.length());
            return text.trim();
        } catch (Exception e) {
            log.error("DOCX文件内容提取失败", e);
            throw new Exception("DOCX文件内容提取失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 提取DOC文件内容
     */
    private String extractTextFromDoc(InputStream inputStream) throws Exception {
        try (HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {
            String text = extractor.getText();
            log.info("DOC文件内容提取成功，字符数: {}", text.length());
            return text.trim();
        } catch (Exception e) {
            log.error("DOC文件内容提取失败", e);
            throw new Exception("DOC文件内容提取失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 提取TXT文件内容
     */
    private String extractTextFromTxt(InputStream inputStream) throws Exception {
        try {
            byte[] bytes = inputStream.readAllBytes();
            String text = new String(bytes, "UTF-8");
            log.info("TXT文件内容提取成功，字符数: {}", text.length());
            return text.trim();
        } catch (Exception e) {
            log.error("TXT文件内容提取失败", e);
            throw new Exception("TXT文件内容提取失败: " + e.getMessage(), e);
        }
    }
}