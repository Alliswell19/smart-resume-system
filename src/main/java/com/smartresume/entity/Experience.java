// src/main/java/com/smartresume/smartresumesystem/entity/Experience.java
package com.smartresume.entity;

public class Experience {
    private String company;
    private String role;        // 或 position / title
    private String duration;    // 如 "2020-2024"
    private String description;

    // Getters and Setters
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}