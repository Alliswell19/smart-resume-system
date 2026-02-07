package com.smartresume.service;

import com.smartresume.entity.ResumeOptimizationResult;
import com.smartresume.entity.ResumeParseResult;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {
    String extractTextFromPdfOrWord(MultipartFile file) throws Exception;
    ResumeParseResult parse(String rawText);
    ResumeOptimizationResult optimizeResume(Long resumeId); // ← 必须实现
}