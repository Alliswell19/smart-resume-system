package com.smartresume.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartresume.entity.Resume;
import com.smartresume.entity.ResumeOptimizationResult;
import com.smartresume.entity.ResumeParseResult;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService extends IService<Resume> {
    String extractTextFromPdfOrWord(MultipartFile file) throws Exception;
    ResumeParseResult parse(String rawText);
    ResumeParseResult parseWithFile(MultipartFile file);
    ResumeOptimizationResult optimizeResume(Long resumeId);
}