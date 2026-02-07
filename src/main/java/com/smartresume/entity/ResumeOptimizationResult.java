package com.smartresume.entity;

import java.util.List;

/**
 * 简历优化结果实体
 * 存储 AI 优化后的简历内容、评分和建议
 */
public class ResumeOptimizationResult {
    private Long resumeId;                    // ← 统一使用 resumeId，不再用 originalResumeId
    private String optimizedContent;
    private List<String> suggestions;
    private Integer overallScore;

    // ==================== Getter 和 Setter ====================

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getOptimizedContent() {
        return optimizedContent;
    }

    public void setOptimizedContent(String optimizedContent) {
        this.optimizedContent = optimizedContent;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }
}