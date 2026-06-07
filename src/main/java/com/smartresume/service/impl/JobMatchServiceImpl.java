package com.smartresume.service.impl;

import com.smartresume.entity.Job;
import com.smartresume.entity.Resume;
import com.smartresume.service.JobMatchService;
import com.smartresume.service.JobService;
import com.smartresume.service.ResumeService;
import com.smartresume.util.JsonUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobMatchServiceImpl implements JobMatchService {

    @Resource
    private ResumeService resumeService;

    @Resource
    private JobService jobService;

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
            // 获取职位信息
            Job job = jobService.getById(jobId);
            if (job == null) {
                throw new RuntimeException("职位不存在");
            }

            // 获取简历信息
            Resume resume = resumeService.getById(resumeId);
            if (resume == null) {
                throw new RuntimeException("简历不存在");
            }

            result.setResumeName(resume.getName());

            // 提取职位技能要求
            List<String> jobSkills = job.getSkillTags();
            if (jobSkills == null) jobSkills = new ArrayList<>();

            // 提取简历技能
            List<String> resumeSkills = parseSkills(resume.getSkills());

            // 计算技能匹配度
            int skillMatchScore = calculateSkillMatch(jobSkills, resumeSkills);
            result.setMatchedSkills(getMatchedSkills(jobSkills, resumeSkills));
            result.setMissingSkills(getMissingSkills(jobSkills, resumeSkills));

            // 计算经验匹配度
            int jobExpYears = parseExperienceYears(job.getExperienceRequired());
            int resumeExpYears = parseExperienceYears(resume.getExperience());
            int experienceMatchScore = calculateExperienceMatch(jobExpYears, resumeExpYears);
            result.setExperienceMatch(getExperienceMatchText(jobExpYears, resumeExpYears));

            // 计算教育背景匹配度
            int educationMatchScore = calculateEducationMatch(job.getEducationRequired(), resume.getEducation());
            result.setEducationMatch(getEducationMatchText(job.getEducationRequired(), resume.getEducation()));

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
            result.setMatchScore(0);
            result.setMatchedSkills(new ArrayList<>());
            result.setMissingSkills(new ArrayList<>());
            result.setExperienceMatch("未知");
            result.setEducationMatch("未知");
            result.setMatchDetails(Collections.singletonList("错误: " + e.getMessage()));
        }

        return result;
    }

    private List<String> parseSkills(String skills) {
        if (skills == null || skills.trim().isEmpty()) {
            return new ArrayList<>();
        }
        // 尝试解析JSON
        try {
            List<String> list = JsonUtils.parseList(skills, String.class);
            if (list != null) return list;
        } catch (Exception ignored) {}

        // 尝试逗号分隔
        return Arrays.stream(skills.split("[,，;；]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private int parseExperienceYears(String exp) {
        if (exp == null || exp.isEmpty()) return 0;
        // 简单提取数字
        String numeric = exp.replaceAll("[^0-9]", "");
        if (numeric.isEmpty()) return 0;
        try {
            return Integer.parseInt(numeric);
        } catch (NumberFormatException e) {
            return 0;
        }
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
        if (education == null) return 1;
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
}