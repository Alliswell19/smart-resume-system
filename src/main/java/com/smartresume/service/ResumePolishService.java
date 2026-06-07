package com.smartresume.service;

/**
 * 简历润色服务接口
 */
public interface ResumePolishService {
    
    /**
     * 润色简历文本
     * @param text 原始简历文本
     * @param type 润色类型（professional-专业, concise-简洁, creative-创意）
     * @return 润色后的文本
     */
    String polishResume(String text, String type);
    
    /**
     * 根据职位描述优化简历
     * @param resumeText 简历文本
     * @param jobDescription 职位描述
     * @return 优化后的文本
     */
    String optimizeForJob(String resumeText, String jobDescription);
    
    /**
     * 润色简历的特定部分
     * @param text 简历文本
     * @param section 部分名称（如 skills, experience, summary）
     * @return 润色后的文本
     */
    String polishSection(String text, String section);
}
