package com.smartresume.entity;

import lombok.Data;
import java.util.List;

@Data
public class ResumeOptimizationResult {
    private Long resumeId;
    private int overallScore;
    private String optimizedContent;
    private List<String> suggestions;
    
    // 各个维度的评分
    private Integer formatScore;
    private Integer contentScore;
    private Integer keywordScore;
    private Integer readabilityScore;
    
    // 改进建议
    private List<String> formatSuggestions;
    private List<String> contentSuggestions;
    private List<String> keywordSuggestions;
}