package com.smartresume.service.impl;

import com.smartresume.entity.ResumeOptimizationResult;
import com.smartresume.entity.ResumeParseResult;
import com.smartresume.service.ResumeService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Override
    public String extractTextFromPdfOrWord(MultipartFile file) throws Exception {
        // 实际项目中应使用Apache Tika/PDFBox等库实现
        // 此处为模拟实现
        return "模拟提取的文本内容";
    }

    @Override
    public ResumeParseResult parse(String rawText) {
        ResumeParseResult result = new ResumeParseResult();

        // 模拟解析逻辑
        result.setName("张三");
        result.setEmail("zhangsan@example.com");
        result.setPhone("13800138000");

        // 技能解析示例
        result.getSkills().add(new ResumeParseResult.Skill("Java", "精通", 90));
        result.getSkills().add(new ResumeParseResult.Skill("Spring Boot", "熟练", 85));

        // 工作经历示例
        ResumeParseResult.Experience experience = new ResumeParseResult.Experience();
        experience.setCompany("ABC公司");
        experience.setPosition("高级工程师");
        experience.setStartDate("2020-01");
        experience.setEndDate("2023-12");
        result.getExperience().add(experience);

        return result;
    }

    @Override
    public ResumeOptimizationResult optimizeResume(Long resumeId) {
        ResumeOptimizationResult result = new ResumeOptimizationResult();
        result.setResumeId(resumeId);                    // ✅ 改为 setResumeId
        result.setOverallScore(85);
        result.setOptimizedContent("优化后内容...");
        result.setSuggestions(Arrays.asList("建议..."));
        return result;
    }
}