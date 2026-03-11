package com.smartresume.service.impl;

import com.smartresume.entity.ResumeOptimizationResult;
import com.smartresume.entity.ResumeParseResult;
import com.smartresume.service.ResumeService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

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
        result.setPosition("Java开发工程师");

        // 技能解析示例
        result.getSkillList().add(new ResumeParseResult.Skill("Java", "精通", 90));
        result.getSkillList().add(new ResumeParseResult.Skill("Spring Boot", "熟练", 85));
        result.getSkillList().add(new ResumeParseResult.Skill("MySQL", "熟练", 80));

        // 工作经历示例
        ResumeParseResult.Experience experience = new ResumeParseResult.Experience();
        experience.setCompany("ABC公司");
        experience.setPosition("高级工程师");
        experience.setStartDate("2020-01");
        experience.setEndDate("2023-12");
        experience.setDescription("负责后端系统开发");
        result.getExperienceList().add(experience);

        // 教育背景示例
        ResumeParseResult.Education education = new ResumeParseResult.Education();
        education.setSchool("北京大学");
        education.setMajor("计算机科学与技术");
        education.setDegree("本科");
        education.setStartDate("2016-09");
        education.setEndDate("2020-06");
        education.setGpa(3.8);
        result.getEducationList().add(education);

        return result;
    }

    @Override
    public ResumeOptimizationResult optimizeResume(Long resumeId) {
        ResumeOptimizationResult result = new ResumeOptimizationResult();
        result.setResumeId(resumeId);
        result.setOverallScore(85);
        result.setOptimizedContent("优化后内容...");
        result.setSuggestions(Arrays.asList("建议1", "建议2", "建议3"));

        // 各维度评分
        result.setFormatScore(90);
        result.setContentScore(75);
        result.setKeywordScore(85);
        result.setReadabilityScore(80);

        // 各维度建议
        result.setFormatSuggestions(Arrays.asList("段落格式可以更清晰"));
        result.setContentSuggestions(Arrays.asList("工作内容描述不够具体"));
        result.setKeywordSuggestions(Arrays.asList("缺少关键技能关键词"));

        return result;
    }
}