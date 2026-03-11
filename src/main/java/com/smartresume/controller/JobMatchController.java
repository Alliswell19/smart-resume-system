package com.smartresume.controller;

import com.smartresume.service.JobMatchService;
import com.smartresume.service.JobMatchService.MatchResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 岗位匹配控制器
 * 处理简历与职位的匹配请求
 */
@RestController
@RequestMapping("/api/job-match")
public class JobMatchController {

    @Resource
    private JobMatchService jobMatchService;

    /**
     * 根据职位ID匹配多个简历
     * @param jobId 职位ID
     * @param resumeIds 简历ID列表
     * @return 匹配结果列表
     */
    @PostMapping("/match-resumes")
    public List<MatchResult> matchResumes(@RequestParam Long jobId, @RequestBody List<Long> resumeIds) {
        return jobMatchService.matchResumes(jobId, resumeIds);
    }

    /**
     * 计算单个简历与职位的匹配度
     * @param jobId 职位ID
     * @param resumeId 简历ID
     * @return 匹配结果
     */
    @GetMapping("/calculate-match")
    public MatchResult calculateMatch(@RequestParam Long jobId, @RequestParam Long resumeId) {
        return jobMatchService.calculateMatch(jobId, resumeId);
    }

    /**
     * 获取推荐职位列表
     * @param resumeId 简历ID
     * @return 推荐职位列表
     */
    @GetMapping("/recommend-jobs")
    public List<JobRecommendation> recommendJobs(@RequestParam Long resumeId) {
        // 模拟推荐结果
        return getMockJobRecommendations(resumeId);
    }

    // 模拟职位推荐结果
    private List<JobRecommendation> getMockJobRecommendations(Long resumeId) {
        List<JobRecommendation> recommendations = new ArrayList<>();
        
        // 推荐职位1
        JobRecommendation job1 = new JobRecommendation();
        job1.setJobId(1L);
        job1.setTitle("前端开发工程师");
        job1.setCompany("腾讯科技");
        job1.setSalary("15-25K");
        job1.setLocation("深圳");
        job1.setMatchScore(90);
        job1.setDescription("负责公司核心产品的前端开发，使用Vue/React技术栈");
        recommendations.add(job1);
        
        // 推荐职位2
        JobRecommendation job2 = new JobRecommendation();
        job2.setJobId(2L);
        job2.setTitle("Java开发工程师");
        job2.setCompany("阿里巴巴");
        job2.setSalary("20-30K");
        job2.setLocation("杭州");
        job2.setMatchScore(85);
        job2.setDescription("负责后端系统的设计和开发，使用Java/Spring Boot技术栈");
        recommendations.add(job2);
        
        // 推荐职位3
        JobRecommendation job3 = new JobRecommendation();
        job3.setJobId(3L);
        job3.setTitle("产品经理");
        job3.setCompany("字节跳动");
        job3.setSalary("18-28K");
        job3.setLocation("北京");
        job3.setMatchScore(80);
        job3.setDescription("负责产品规划和设计，需要有良好的用户体验意识");
        recommendations.add(job3);
        
        return recommendations;
    }

    // 职位推荐类
    public static class JobRecommendation {
        private Long jobId;
        private String title;
        private String company;
        private String salary;
        private String location;
        private int matchScore;
        private String description;

        // Getters and Setters
        public Long getJobId() {
            return jobId;
        }

        public void setJobId(Long jobId) {
            this.jobId = jobId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getSalary() {
            return salary;
        }

        public void setSalary(String salary) {
            this.salary = salary;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getMatchScore() {
            return matchScore;
        }

        public void setMatchScore(int matchScore) {
            this.matchScore = matchScore;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}