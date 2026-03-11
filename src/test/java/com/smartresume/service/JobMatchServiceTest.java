package com.smartresume.service;

import com.smartresume.service.JobMatchService;
import com.smartresume.service.impl.JobMatchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 职位匹配服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class JobMatchServiceTest {

    @InjectMocks
    private JobMatchServiceImpl jobMatchService;

    /**
     * 测试职位匹配计算
     */
    @Test
    public void testCalculateMatch() {
        // 使用模拟数据测试匹配计算
        Long jobId = 1L;
        Long resumeId = 1L;
        
        JobMatchService.MatchResult result = jobMatchService.calculateMatch(jobId, resumeId);
        
        assertNotNull(result, "匹配结果不应为null");
        assertNotNull(result.getJobId(), "职位ID应被设置");
        assertNotNull(result.getResumeId(), "简历ID应被设置");
        assertNotNull(result.getMatchScore(), "总评分应被计算");
        
        // 验证评分范围
        assertTrue(result.getMatchScore() >= 0 && result.getMatchScore() <= 100, "总评分应在0-100之间");
    }

    /**
     * 测试计算匹配度功能
     */
    @Test
    public void testCalculateMatchDetails() {
        Long jobId = 1L;
        Long resumeId = 1L;
        
        JobMatchService.MatchResult result = jobMatchService.calculateMatch(jobId, resumeId);
        
        assertNotNull(result, "匹配结果不应为null");
        assertNotNull(result.getMatchScore(), "匹配分数应被计算");
        assertNotNull(result.getMatchedSkills(), "匹配的技能应被计算");
        assertNotNull(result.getMissingSkills(), "缺失的技能应被计算");
        assertNotNull(result.getExperienceMatch(), "经验匹配情况应被计算");
        assertNotNull(result.getEducationMatch(), "教育匹配情况应被计算");
        assertNotNull(result.getMatchDetails(), "匹配详细信息应被计算");
    }

    /**
     * 测试匹配简历功能
     */
    @Test
    public void testMatchResumes() {
        Long jobId = 1L;
        List<Long> resumeIds = Arrays.asList(1L, 2L, 3L);
        
        List<JobMatchService.MatchResult> result = jobMatchService.matchResumes(jobId, resumeIds);
        
        assertNotNull(result, "匹配结果不应为null");
    }
}
