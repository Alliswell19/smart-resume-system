package com.smartresume.service.impl;

import com.smartresume.service.JobMatchService;
import com.smartresume.service.ResumeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class JobMatchServiceImpl implements JobMatchService {

    @Resource
    private ResumeService resumeService;

    @Override
    public List<MatchResult> matchResumes(Long jobId, List<Long> resumeIds) {
        List<MatchResult> results = new ArrayList<>();
        for (Long resumeId : resumeIds) {
            MatchResult result = calculateMatch(jobId, resumeId);
            results.add(result);
        }
        // 按匹配度排序
        results.sort((r1, r2) -> Integer.compare(r2.getMatchScore(), r1.getMatchScore()));
        return results;
    }

    @Override
    public MatchResult calculateMatch(Long jobId, Long resumeId) {
        MatchResult result = new MatchResult();
        result.setJobId(jobId);
        result.setResumeId(resumeId);

        try {
            // 模拟职位要求（实际项目中应该从数据库获取）
            JobRequirement jobRequirement = getJobRequirement(jobId);
            
            // 获取简历信息（实际项目中应该从数据库获取）
            ResumeInfo resumeInfo = getResumeInfo(resumeId);
            result.setResumeName(resumeInfo.getName());

            // 计算技能匹配度
            int skillMatchScore = calculateSkillMatch(jobRequirement.getSkills(), resumeInfo.getSkills());
            result.setMatchedSkills(getMatchedSkills(jobRequirement.getSkills(), resumeInfo.getSkills()));
            result.setMissingSkills(getMissingSkills(jobRequirement.getSkills(), resumeInfo.getSkills()));

            // 计算经验匹配度
            int experienceMatchScore = calculateExperienceMatch(jobRequirement.getExperienceYears(), resumeInfo.getExperienceYears());
            result.setExperienceMatch(getExperienceMatchText(jobRequirement.getExperienceYears(), resumeInfo.getExperienceYears()));

            // 计算教育背景匹配度
            int educationMatchScore = calculateEducationMatch(jobRequirement.getEducationLevel(), resumeInfo.getEducationLevel());
            result.setEducationMatch(getEducationMatchText(jobRequirement.getEducationLevel(), resumeInfo.getEducationLevel()));

            // 计算总匹配度（权重：技能60%，经验25%，教育15%）
            int totalScore = (int) (skillMatchScore * 0.6 + experienceMatchScore * 0.25 + educationMatchScore * 0.15);
            result.setMatchScore(totalScore);

            // 添加匹配详细信息
            List<String> matchDetails = new ArrayList<>();
            matchDetails.add("技能匹配度: " + skillMatchScore + "%");
            matchDetails.add("经验匹配度: " + experienceMatchScore + "%");
            matchDetails.add("教育背景匹配度: " + educationMatchScore + "%");
            matchDetails.add("总匹配度: " + totalScore + "%");
            result.setMatchDetails(matchDetails);

        } catch (Exception e) {
            e.printStackTrace();
            // 如果计算失败，返回默认结果
            result.setMatchScore(0);
            result.setMatchedSkills(new ArrayList<>());
            result.setMissingSkills(new ArrayList<>());
            result.setExperienceMatch("未知");
            result.setEducationMatch("未知");
            result.setMatchDetails(new ArrayList<>());
        }

        return result;
    }

    // 模拟获取职位要求
    private JobRequirement getJobRequirement(Long jobId) {
        JobRequirement requirement = new JobRequirement();
        
        // 根据不同的jobId返回不同的职位要求
        switch (jobId.intValue()) {
            case 1:
                requirement.setTitle("前端开发工程师");
                requirement.setSkills(List.of("Vue", "React", "JavaScript", "HTML/CSS", "TypeScript"));
                requirement.setExperienceYears(3);
                requirement.setEducationLevel("本科");
                break;
            case 2:
                requirement.setTitle("Java开发工程师");
                requirement.setSkills(List.of("Java", "Spring Boot", "MySQL", "Redis", "Spring Cloud"));
                requirement.setExperienceYears(5);
                requirement.setEducationLevel("本科");
                break;
            case 3:
                requirement.setTitle("产品经理");
                requirement.setSkills(List.of("Axure", "产品设计", "用户调研", "数据分析", "项目管理"));
                requirement.setExperienceYears(3);
                requirement.setEducationLevel("本科");
                break;
            default:
                requirement.setTitle("默认职位");
                requirement.setSkills(List.of("Java", "Python", "SQL"));
                requirement.setExperienceYears(2);
                requirement.setEducationLevel("本科");
        }
        
        return requirement;
    }

    // 模拟获取简历信息
    private ResumeInfo getResumeInfo(Long resumeId) {
        ResumeInfo info = new ResumeInfo();
        
        // 根据不同的resumeId返回不同的简历信息
        switch (resumeId.intValue()) {
            case 1:
                info.setName("张三");
                info.setSkills(List.of("Vue", "JavaScript", "HTML/CSS", "React"));
                info.setExperienceYears(5);
                info.setEducationLevel("本科");
                break;
            case 2:
                info.setName("李四");
                info.setSkills(List.of("Java", "Spring Boot", "MySQL"));
                info.setExperienceYears(3);
                info.setEducationLevel("硕士");
                break;
            case 3:
                info.setName("王五");
                info.setSkills(List.of("Axure", "产品设计", "用户调研"));
                info.setExperienceYears(2);
                info.setEducationLevel("本科");
                break;
            default:
                info.setName("默认简历");
                info.setSkills(List.of("Java", "SQL"));
                info.setExperienceYears(1);
                info.setEducationLevel("本科");
        }
        
        return info;
    }

    // 计算技能匹配度
    private int calculateSkillMatch(List<String> jobSkills, List<String> resumeSkills) {
        if (jobSkills == null || jobSkills.isEmpty()) {
            return 100;
        }
        
        int matchedCount = 0;
        for (String jobSkill : jobSkills) {
            for (String resumeSkill : resumeSkills) {
                if (jobSkill.toLowerCase().contains(resumeSkill.toLowerCase()) || 
                    resumeSkill.toLowerCase().contains(jobSkill.toLowerCase())) {
                    matchedCount++;
                    break;
                }
            }
        }
        
        return (int) ((double) matchedCount / jobSkills.size() * 100);
    }

    // 获取匹配的技能
    private List<String> getMatchedSkills(List<String> jobSkills, List<String> resumeSkills) {
        List<String> matchedSkills = new ArrayList<>();
        for (String jobSkill : jobSkills) {
            for (String resumeSkill : resumeSkills) {
                if (jobSkill.toLowerCase().contains(resumeSkill.toLowerCase()) || 
                    resumeSkill.toLowerCase().contains(jobSkill.toLowerCase())) {
                    matchedSkills.add(jobSkill);
                    break;
                }
            }
        }
        return matchedSkills;
    }

    // 获取缺失的技能
    private List<String> getMissingSkills(List<String> jobSkills, List<String> resumeSkills) {
        List<String> missingSkills = new ArrayList<>();
        for (String jobSkill : jobSkills) {
            boolean matched = false;
            for (String resumeSkill : resumeSkills) {
                if (jobSkill.toLowerCase().contains(resumeSkill.toLowerCase()) || 
                    resumeSkill.toLowerCase().contains(jobSkill.toLowerCase())) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                missingSkills.add(jobSkill);
            }
        }
        return missingSkills;
    }

    // 计算经验匹配度
    private int calculateExperienceMatch(int jobExperience, int resumeExperience) {
        if (resumeExperience >= jobExperience) {
            return 100;
        } else if (resumeExperience >= jobExperience - 1) {
            return 80;
        } else if (resumeExperience >= jobExperience - 2) {
            return 60;
        } else {
            return 40;
        }
    }

    // 获取经验匹配文本
    private String getExperienceMatchText(int jobExperience, int resumeExperience) {
        if (resumeExperience > jobExperience) {
            return "超出要求";
        } else if (resumeExperience == jobExperience) {
            return "符合要求";
        } else if (resumeExperience >= jobExperience - 1) {
            return "接近要求";
        } else {
            return "不符合要求";
        }
    }

    // 计算教育背景匹配度
    private int calculateEducationMatch(String jobEducation, String resumeEducation) {
        // 教育背景等级
        int jobLevel = getEducationLevel(jobEducation);
        int resumeLevel = getEducationLevel(resumeEducation);
        
        if (resumeLevel >= jobLevel) {
            return 100;
        } else if (resumeLevel == jobLevel - 1) {
            return 80;
        } else {
            return 60;
        }
    }

    // 获取教育背景匹配文本
    private String getEducationMatchText(String jobEducation, String resumeEducation) {
        int jobLevel = getEducationLevel(jobEducation);
        int resumeLevel = getEducationLevel(resumeEducation);
        
        if (resumeLevel > jobLevel) {
            return "超出要求";
        } else if (resumeLevel == jobLevel) {
            return "符合要求";
        } else {
            return "不符合要求";
        }
    }

    // 获取教育背景等级
    private int getEducationLevel(String education) {
        switch (education.toLowerCase()) {
            case "博士":
                return 5;
            case "硕士":
                return 4;
            case "本科":
                return 3;
            case "大专":
                return 2;
            default:
                return 1;
        }
    }

    // 职位要求类
    private static class JobRequirement {
        private String title;
        private List<String> skills;
        private int experienceYears;
        private String educationLevel;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getSkills() {
            return skills;
        }

        public void setSkills(List<String> skills) {
            this.skills = skills;
        }

        public int getExperienceYears() {
            return experienceYears;
        }

        public void setExperienceYears(int experienceYears) {
            this.experienceYears = experienceYears;
        }

        public String getEducationLevel() {
            return educationLevel;
        }

        public void setEducationLevel(String educationLevel) {
            this.educationLevel = educationLevel;
        }
    }

    // 简历信息类
    private static class ResumeInfo {
        private String name;
        private List<String> skills;
        private int experienceYears;
        private String educationLevel;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getSkills() {
            return skills;
        }

        public void setSkills(List<String> skills) {
            this.skills = skills;
        }

        public int getExperienceYears() {
            return experienceYears;
        }

        public void setExperienceYears(int experienceYears) {
            this.experienceYears = experienceYears;
        }

        public String getEducationLevel() {
            return educationLevel;
        }

        public void setEducationLevel(String educationLevel) {
            this.educationLevel = educationLevel;
        }
    }
}