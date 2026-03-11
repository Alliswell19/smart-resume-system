package com.smartresume.entity;

import java.util.ArrayList;
import java.util.List;

public class ResumeParseResult {
    private String name;
    private String email;
    private String phone;
    private String position;
    private String skills;          // 简单文本格式的技能
    private String experience;       // 简单文本格式的工作经历
    private String education;        // 简单文本格式的教育背景

    // 用于详细展示的技能列表
    private List<Skill> skillList = new ArrayList<>();

    // 用于详细展示的工作经历列表
    private List<Experience> experienceList = new ArrayList<>();

    // 教育背景列表
    private List<Education> educationList = new ArrayList<>();

    public static class Skill {
        private String name;
        private String level;
        private int score;

        public Skill() {}

        public Skill(String name, String level, int score) {
            this.name = name;
            this.level = level;
            this.score = score;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
    }

    public static class Experience {
        private String company;
        private String position;
        private String startDate;
        private String endDate;
        private String description;

        // Getters and Setters
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }

        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class Education {
        private String school;
        private String major;
        private String degree;
        private String startDate;
        private String endDate;
        private double gpa;

        // Getters and Setters
        public String getSchool() { return school; }
        public void setSchool(String school) { this.school = school; }

        public String getMajor() { return major; }
        public void setMajor(String major) { this.major = major; }

        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }

        public double getGpa() { return gpa; }
        public void setGpa(double gpa) { this.gpa = gpa; }
    }

    // ========== 基本字段的 Getter/Setter ==========

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    // ========== 列表字段的 Getter/Setter ==========

    public List<Skill> getSkillList() { return skillList; }

    public void setSkillList(List<Skill> skillList) {
        this.skillList = skillList;
        // 同时生成简单的技能字符串
        StringBuilder sb = new StringBuilder();
        for (Skill skill : skillList) {
            sb.append(skill.getName()).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        this.skills = sb.toString();
    }

    public List<Experience> getExperienceList() { return experienceList; }

    public void setExperienceList(List<Experience> experienceList) {
        this.experienceList = experienceList;
        // 同时生成简单的工作经历字符串
        StringBuilder sb = new StringBuilder();
        for (Experience exp : experienceList) {
            sb.append(exp.getCompany()).append(" ")
                    .append(exp.getPosition()).append(" ")
                    .append(exp.getStartDate()).append("-")
                    .append(exp.getEndDate()).append("\n");
        }
        this.experience = sb.toString();
    }

    public List<Education> getEducationList() { return educationList; }

    public void setEducationList(List<Education> educationList) {
        this.educationList = educationList;
        // 同时生成简单的教育背景字符串
        StringBuilder sb = new StringBuilder();
        for (Education edu : educationList) {
            sb.append(edu.getSchool()).append(" ")
                    .append(edu.getMajor()).append(" ")
                    .append(edu.getDegree()).append("\n");
        }
        this.education = sb.toString();
    }
}