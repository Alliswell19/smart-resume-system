package com.smartresume.service;

import java.util.List;

/**
 * 岗位匹配服务接口
 * 提供简历与职位的匹配功能
 */
public interface JobMatchService {

    /**
     * 根据职位要求匹配简历
     * @param jobId 职位ID
     * @param resumeIds 简历ID列表
     * @return 匹配结果列表，包含匹配度和详细信息
     */
    List<MatchResult> matchResumes(Long jobId, List<Long> resumeIds);

    /**
     * 计算单个简历与职位的匹配度
     * @param jobId 职位ID
     * @param resumeId 简历ID
     * @return 匹配结果
     */
    MatchResult calculateMatch(Long jobId, Long resumeId);

    /**
     * 匹配结果类
     */
    class MatchResult {
        private Long jobId;
        private Long resumeId;
        private String resumeName;
        private int matchScore; // 匹配度评分（0-100）
        private List<String> matchedSkills; // 匹配的技能
        private List<String> missingSkills; // 缺失的技能
        private String experienceMatch; // 经验匹配情况
        private String educationMatch; // 教育匹配情况
        private List<String> matchDetails; // 匹配详细信息

        // Getters and Setters
        public Long getJobId() {
            return jobId;
        }

        public void setJobId(Long jobId) {
            this.jobId = jobId;
        }

        public Long getResumeId() {
            return resumeId;
        }

        public void setResumeId(Long resumeId) {
            this.resumeId = resumeId;
        }

        public String getResumeName() {
            return resumeName;
        }

        public void setResumeName(String resumeName) {
            this.resumeName = resumeName;
        }

        public int getMatchScore() {
            return matchScore;
        }

        public void setMatchScore(int matchScore) {
            this.matchScore = matchScore;
        }

        public List<String> getMatchedSkills() {
            return matchedSkills;
        }

        public void setMatchedSkills(List<String> matchedSkills) {
            this.matchedSkills = matchedSkills;
        }

        public List<String> getMissingSkills() {
            return missingSkills;
        }

        public void setMissingSkills(List<String> missingSkills) {
            this.missingSkills = missingSkills;
        }

        public String getExperienceMatch() {
            return experienceMatch;
        }

        public void setExperienceMatch(String experienceMatch) {
            this.experienceMatch = experienceMatch;
        }

        public String getEducationMatch() {
            return educationMatch;
        }

        public void setEducationMatch(String educationMatch) {
            this.educationMatch = educationMatch;
        }

        public List<String> getMatchDetails() {
            return matchDetails;
        }

        public void setMatchDetails(List<String> matchDetails) {
            this.matchDetails = matchDetails;
        }
    }
}