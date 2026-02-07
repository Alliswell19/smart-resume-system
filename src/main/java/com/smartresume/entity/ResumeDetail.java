package com.smartresume.entity;

import lombok.Data;
import java.util.List;

@Data
public class ResumeDetail {
    private Long id;
    private Long resumeId;

    private String name;
    private String phone;
    private String email;
    private List<String> educationList;     // 学历列表，JSON 字符串存储
    private List<String> workExperienceList; // 工作经历列表，JSON 字符串存储
    private List<String> skills;            // 技能标签
    private String summary;                 // 自我评价
}