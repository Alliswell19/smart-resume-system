package com.smartresume.service;

import com.smartresume.common.Result;
import com.smartresume.entity.ResumeOptimizationResult;
import com.smartresume.entity.ResumeParseResult;
import com.smartresume.service.impl.AIServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AI服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class AIServiceTest {

    @InjectMocks
    private AIServiceImpl aiService;

    /**
     * 测试简历解析功能
     */
    @Test
    public void testParseResumeText() {
        String rawText = "姓名：张三\n邮箱：zhangsan@example.com\n电话：13800138000\n技能：Java, Spring Boot, MySQL\n工作经历：2020-2023 某公司 后端开发\n教育背景：北京大学 计算机科学与技术 硕士";
        
        ResumeParseResult result = aiService.parseResumeText(rawText);
        
        assertNotNull(result, "解析结果不应为null");
        assertNotNull(result.getEmail(), "邮箱应被解析");
        assertNotNull(result.getPhone(), "电话应被解析");
        assertNotNull(result.getSkills(), "技能应被解析");
        assertNotNull(result.getExperience(), "工作经历应被解析");
        assertNotNull(result.getEducation(), "学历应被解析");
    }

    /**
     * 测试生成优化建议
     */
    @Test
    public void testGetOptimizationSuggestions() {
        Long resumeId = 1L;
        
        ResumeOptimizationResult result = aiService.getOptimizationSuggestions(resumeId);
        
        assertNotNull(result, "建议结果不应为null");
        assertNotNull(result.getOverallScore(), "总体评分应被计算");
        assertNotNull(result.getOptimizedContent(), "优化后的简历内容应被生成");
        assertNotNull(result.getSuggestions(), "优化建议应被生成");
    }

    /**
     * 测试空文本解析
     */
    @Test
    public void testParseEmptyText() {
        ResumeParseResult result = aiService.parseResumeText("");
        
        assertNotNull(result, "解析结果不应为null");
        // 即使是空文本，也应该返回默认值
        assertNotNull(result.getName(), "姓名应返回默认值");
    }
}
