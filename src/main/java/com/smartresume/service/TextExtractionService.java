// src/main/java/com/smartresume/service/TextExtractionService.java
package com.smartresume.service;

import org.springframework.web.multipart.MultipartFile;

public interface TextExtractionService {
    String extractTextFromPdfOrWord(MultipartFile file) throws Exception;
}