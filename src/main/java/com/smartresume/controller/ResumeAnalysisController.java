package com.smartresume.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartresume.common.Result;
import com.smartresume.entity.Resume;
import com.smartresume.service.QwenFileService;
import com.smartresume.service.QwenService;
import com.smartresume.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/resume-analysis")
@CrossOrigin(origins = "*")
public class ResumeAnalysisController {

    private static final Logger log = LoggerFactory.getLogger(ResumeAnalysisController.class);

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private QwenService qwenService;

    @Autowired
    private QwenFileService qwenFileService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/deep-analyze/{resumeId}")
    public Result<Map<String, Object>> deepAnalyze(@PathVariable Long resumeId) {
        log.info("开始深度解析简历: resumeId={}", resumeId);

        try {
            Resume resume = resumeService.getById(resumeId);
            if (resume == null) {
                return Result.error(404, "简历不存在");
            }

            String resumeText = resume.getOriginalText();
            if (resumeText == null || resumeText.trim().isEmpty()) {
                resumeText = resume.getParseResult();
            }
            if (resumeText == null || resumeText.trim().isEmpty()) {
                return Result.error(400, "简历内容为空，无法分析");
            }

            List<String> options = Arrays.asList("optimization", "career", "salary", "interview");
            Object detailedResult = qwenFileService.analyzeResumeDetailed(resumeText, options);

            Map<String, Object> resultMap = new HashMap<>();
            if (detailedResult instanceof Map) {
                Map<?, ?> rawMap = (Map<?, ?>) detailedResult;
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    if (entry.getKey() instanceof String) {
                        resultMap.put((String) entry.getKey(), entry.getValue());
                    }
                }
            } else {
                resultMap = new HashMap<>();
                resultMap.put("analysis", detailedResult);
            }

            resultMap.put("resumeId", resumeId);

            if (!resultMap.containsKey("score")) {
                Map<String, Object> defaultScore = new HashMap<>();
                defaultScore.put("overallScore", 75);
                defaultScore.put("contentScore", 70);
                defaultScore.put("formatScore", 72);
                defaultScore.put("languageScore", 73);
                defaultScore.put("competitivenessScore", 71);
                resultMap.put("score", defaultScore);
            }

            log.info("简历深度解析完成: resumeId={}", resumeId);
            return Result.success(resultMap);

        } catch (Exception e) {
            log.error("深度解析失败: resumeId=" + resumeId, e);
            return Result.error("深度解析失败: " + e.getMessage());
        }
    }
}
