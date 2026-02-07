// src/main/java/com/smartresume/service/AIService.java
package com.smartresume.service;

import com.smartresume.entity.ResumeOptimizationResult;
import com.smartresume.entity.ResumeParseResult;

/**
 * AI 智能简历服务接口
 * 提供简历文本解析与优化建议功能
 */
public interface AIService {

    /**
     * 解析原始简历文本，提取结构化信息
     * @param rawText 原始简历文本（如 PDF/Word 提取内容）
     * @return 结构化的简历解析结果
     */
    ResumeParseResult parseResumeText(String rawText);

    /**
     * 根据简历 ID 生成优化建议
     * @param resumeId 简历主键 ID
     * @return 优化建议结果
     */
    ResumeOptimizationResult getOptimizationSuggestions(Long resumeId);
}