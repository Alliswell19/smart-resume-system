// src/main/java/com/smartresume/entity/ResumeParseResult.java
package com.smartresume.entity;

import java.util.ArrayList;
import java.util.List;

public class ResumeParseResult {
    private String name;
    private String email;
    private String phone;
    private List<Skill> skills = new ArrayList<>();
    private List<Experience> experience = new ArrayList<>();
    private List<Education> education = new ArrayList<>(); // ← 新增

    // ===== Getter / Setter =====
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }

    public List<Experience> getExperience() { return experience; }
    public void setExperience(List<Experience> experience) { this.experience = experience; }

    public List<Education> getEducation() { return education; } // ← 新增
    public void setEducation(List<Education> education) { this.education = education; } // ← 新增

    // ===== 嵌套类：Skill =====
    public static class Skill {
        private String name;
        private String level;
        private int score;

        public Skill() {} // ← 无参构造（必须！）

        public Skill(String name, String level, int score) {
            this.name = name;
            this.level = level;
            this.score = score;
        }

        // Getters & Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
    }

    // ===== 嵌套类：Experience =====
    public static class Experience {
        private String company;
        private String position;
        private String startDate;
        private String endDate;

        public Experience() {}

        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
    }

    // ===== 嵌套类：Education（新增）=====
    public static class Education {
        private String school;
        private String degree;
        private String major;

        public Education() {}

        public String getSchool() { return school; }
        public void setSchool(String school) { this.school = school; }
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }
        public String getMajor() { return major; }
        public void setMajor(String major) { this.major = major; }
    }
}