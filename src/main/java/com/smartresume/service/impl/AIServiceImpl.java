// src/main/java/com/smartresume/service/impl/AIServiceImpl.java
package com.smartresume.service.impl;

import com.smartresume.entity.ResumeOptimizationResult;
import com.smartresume.entity.ResumeParseResult;
import com.smartresume.service.AIService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class AIServiceImpl implements AIService {

    @Override
    public ResumeParseResult parseResumeText(String rawText) {
        // TODO: 实际项目中应调用 NLP 模型或规则引擎解析 rawText
        // 此处为模拟实现

        ResumeParseResult result = new ResumeParseResult();
        result.setName("张三");
        result.setEmail("zhangsan@example.com");
        result.setPhone("13800138000");

        // 技能
        ResumeParseResult.Skill javaSkill = new ResumeParseResult.Skill();
        javaSkill.setName("Java");
        javaSkill.setLevel("精通");
        javaSkill.setScore(90);
        result.setSkills(Arrays.asList(javaSkill));

        // 工作经历
        ResumeParseResult.Experience exp = new ResumeParseResult.Experience();
        exp.setCompany("ABC科技有限公司");
        exp.setPosition("后端开发工程师");
        exp.setStartDate("2020-03");
        exp.setEndDate("至今");
        result.setExperience(Arrays.asList(exp));

        // 教育背景（新增字段）
        ResumeParseResult.Education edu = new ResumeParseResult.Education();
        edu.setSchool("北京大学");
        edu.setDegree("硕士");
        edu.setMajor("计算机科学与技术");
        result.setEducation(Arrays.asList(edu));

        return result;
    }

    @Override
    public ResumeOptimizationResult getOptimizationSuggestions(Long resumeId) {
        ResumeOptimizationResult result = new ResumeOptimizationResult();

        result.setResumeId(resumeId);                     // ✅ 现在能正常调用
        result.setOverallScore(85);
        result.setOptimizedContent("经过AI优化后的简历内容...");
        result.setSuggestions(Arrays.asList(
                "建议1：增加量化成果描述",
                "建议2：突出核心技能"
        ));

        return result;
    }
}