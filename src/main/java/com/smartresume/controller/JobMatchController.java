package com.smartresume.controller;

import com.smartresume.common.Result;
import com.smartresume.entity.Job;
import com.smartresume.service.JobMatchService;
import com.smartresume.service.JobMatchService.MatchResult;
import com.smartresume.service.JobService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 岗位匹配控制器
 * 处理简历与职位的匹配请求
 */
@RestController
@RequestMapping("/api/job-match")
public class JobMatchController {

    @Resource
    private JobMatchService jobMatchService;

    @Resource
    private JobService jobService;

    /**
     * 根据职位ID匹配多个简历
     * @param jobId 职位ID
     * @param resumeIds 简历ID列表
     * @return 匹配结果列表
     */
    @PostMapping("/match-resumes")
    public Result<List<MatchResult>> matchResumes(@RequestParam Long jobId, @RequestBody List<Long> resumeIds) {
        try {
            List<MatchResult> results = jobMatchService.matchResumes(jobId, resumeIds);
            return Result.success(results);
        } catch (Exception e) {
            return Result.error(500, "匹配失败: " + e.getMessage());
        }
    }

    /**
     * 计算单个简历与职位的匹配度
     * @param jobId 职位ID
     * @param resumeId 简历ID
     * @return 匹配结果
     */
    @GetMapping("/calculate-match")
    public Result<MatchResult> calculateMatch(@RequestParam Long jobId, @RequestParam Long resumeId) {
        try {
            MatchResult result = jobMatchService.calculateMatch(jobId, resumeId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "匹配计算失败: " + e.getMessage());
        }
    }

    /**
     * 获取推荐职位列表
     * @param resumeId 简历ID
     * @return 推荐职位列表
     */
    @GetMapping("/recommend-jobs")
    public Map<String, Object> recommendJobs(@RequestParam Long resumeId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<JobRecommendation> recommendations = getMockJobRecommendations(resumeId);
            result.put("success", true);
            result.put("data", recommendations);
            result.put("message", "推荐职位获取成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "推荐职位获取失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取职位列表
     */
    @GetMapping("/jobs")
    public Map<String, Object> getJobs(@RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String location,
                                       @RequestParam(required = false) String jobType,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 转换jobType为枚举类型
            Job.WorkType workType = null;
            if (jobType != null) {
                try {
                    workType = Job.WorkType.valueOf(jobType.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // 如果无法转换，保持为null
                }
            }
            
            // 使用正确的服务方法
            var jobPage = jobService.getJobList(page, pageSize, keyword, location, workType, Job.PublishStatus.PUBLISHED);
            
            result.put("success", true);
            result.put("data", jobPage.getRecords());
            result.put("total", jobPage.getTotal());
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("message", "职位列表获取成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "职位列表获取失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取职位详情
     */
    @GetMapping("/jobs/{id}")
    public Map<String, Object> getJobDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Job job = jobService.getById(id);
            if (job != null && job.getPublishStatus() == Job.PublishStatus.PUBLISHED) {
                result.put("success", true);
                result.put("data", job);
                result.put("message", "职位详情获取成功");
            } else {
                result.put("success", false);
                result.put("message", "职位不存在或未发布");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "职位详情获取失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 申请职位
     */
    @PostMapping("/apply")
    public Map<String, Object> applyJob(@RequestParam Long jobId, 
                                       @RequestParam Long resumeId,
                                       @RequestParam(required = false) String coverLetter) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 模拟申请逻辑
            result.put("success", true);
            result.put("data", Map.of(
                "applicationId", System.currentTimeMillis(),
                "status", "pending",
                "applyTime", new java.util.Date()
            ));
            result.put("message", "职位申请成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "职位申请失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取职位统计
     */
    @GetMapping("/statistics")
    public Map<String, Object> getJobStatistics() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> statistics = jobService.getJobStatistics();
            result.put("success", true);
            result.put("data", statistics);
            result.put("message", "职位统计获取成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "职位统计获取失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 匹配职位
     */
    @PostMapping("/match/{resumeId}")
    public Map<String, Object> matchJobs(@PathVariable Long resumeId, @RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用服务进行职位匹配
            List<JobRecommendation> recommendations = getMockJobRecommendations(resumeId);
            result.put("success", true);
            result.put("data", recommendations);
            result.put("message", "职位匹配成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "职位匹配失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取职位列表
     */
    @GetMapping("/job/list")
    public Map<String, Object> getJobList(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String location,
                                         @RequestParam(required = false) String jobType,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用getJobs方法，保持功能一致性
            return getJobs(keyword, location, jobType, page, pageSize);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取职位列表失败: " + e.getMessage());
        }
        return result;
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