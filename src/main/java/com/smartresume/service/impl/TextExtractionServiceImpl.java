package com.smartresume.service.impl;

import com.smartresume.service.TextExtractionService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TextExtractionServiceImpl implements TextExtractionService {
    
    @Override
    public String extractTextFromPdfOrWord(MultipartFile file) throws Exception {
        // 简化版本，先返回模拟数据
        if (file.getOriginalFilename().endsWith(".pdf")) {
            return "PDF文件内容模拟提取...";
        } else if (file.getOriginalFilename().endsWith(".docx") || 
                   file.getOriginalFilename().endsWith(".doc")) {
            return "Word文件内容模拟提取...";
        }
        return "文件内容提取成功";
    }
}