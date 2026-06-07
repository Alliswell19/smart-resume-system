package com.smartresume.controller;

import com.smartresume.common.Result;
import com.smartresume.entity.AsyncTask;
import com.smartresume.service.AsyncTaskService;
import com.smartresume.service.QwenService;
import com.smartresume.service.ResumeService;
import com.smartresume.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/polish")
public class ResumePolishController {

    private static final Logger log = LoggerFactory.getLogger(ResumePolishController.class);

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private QwenService qwenService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 上传简历并润色（异步）
     */
    @PostMapping("/upload-and-polish")
    public Result<Map<String, Object>> uploadAndPolish(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "professional") String type,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        log.info("=========== 上传并润色（异步）===========");

        try {
            // 1. 提取文本
            String originalText = resumeService.extractTextFromPdfOrWord(file);
            log.info("文本提取完成，长度: {} 字符", originalText.length());

            // 2. 创建异步任务
            Long userId = getUserIdFromHeader(authHeader);
            String params = String.format("{\"text\":\"%s\",\"type\":\"%s\"}",
                    originalText.replace("\"", "\\\""), type);
            AsyncTask task = asyncTaskService.createTask("POLISH", userId, null, params);

            // 3. 异步执行 AI 润色
            Long taskId = task.getId();
            new Thread(() -> {
                try {
                    asyncTaskService.markAsProcessing(taskId);
                    log.info("开始处理润色任务: taskId={}", taskId);

                    String polished = qwenService.generateText(buildPolishPrompt(originalText, type));
                    String resultJson = String.format(
                            "{\"originalText\":\"%s\",\"polishedText\":\"%s\",\"type\":\"%s\"}",
                            originalText.replace("\"", "\\\""),
                            polished.replace("\"", "\\\""),
                            type
                    );
                    asyncTaskService.completeTask(taskId, resultJson);
                    log.info("润色任务完成: taskId={}", taskId);
                } catch (Exception e) {
                    log.error("润色任务失败: taskId=" + taskId, e);
                    asyncTaskService.failTask(taskId, e.getMessage());
                }
            }).start();

            // 4. 立即返回 taskId
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", taskId);
            data.put("status", "PROCESSING");
            return Result.success("任务已创建，请轮询结果", data);

        } catch (Exception e) {
            log.error("创建润色任务失败", e);
            return Result.error("创建任务失败: " + e.getMessage());
        }
    }

    /**
     * 润色纯文本（异步）
     */
    @PostMapping("/polish-text")
    public Result<Map<String, Object>> polishText(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            String text = body.get("text");
            String type = body.getOrDefault("type", "professional");

            Long userId = getUserIdFromHeader(authHeader);
            String params = String.format("{\"text\":\"%s\",\"type\":\"%s\"}",
                    text.replace("\"", "\\\""), type);
            AsyncTask task = asyncTaskService.createTask("POLISH", userId, null, params);

            Long taskId = task.getId();
            new Thread(() -> {
                try {
                    asyncTaskService.markAsProcessing(taskId);
                    String polished = qwenService.generateText(buildPolishPrompt(text, type));
                    String resultJson = String.format(
                            "{\"originalText\":\"%s\",\"polishedText\":\"%s\",\"type\":\"%s\"}",
                            text.replace("\"", "\\\""),
                            polished.replace("\"", "\\\""),
                            type
                    );
                    asyncTaskService.completeTask(taskId, resultJson);
                } catch (Exception e) {
                    asyncTaskService.failTask(taskId, e.getMessage());
                }
            }).start();

            Map<String, Object> data = new HashMap<>();
            data.put("taskId", taskId);
            data.put("status", "PROCESSING");
            return Result.success("任务已创建，请轮询结果", data);

        } catch (Exception e) {
            return Result.error("创建任务失败: " + e.getMessage());
        }
    }

    /**
     * 查询任务状态
     */
    @GetMapping("/task/{taskId}")
    public Result<Map<String, Object>> getTaskStatus(@PathVariable Long taskId) {
        AsyncTask task = asyncTaskService.getTaskById(taskId);
        if (task == null) {
            return Result.error(404, "任务不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("taskId", task.getId());
        data.put("status", task.getStatus());
        data.put("statusText", getStatusText(task.getStatus()));
        data.put("result", task.getResult());
        data.put("errorMsg", task.getErrorMsg());
        return Result.success(data);
    }

    // ==================== Prompt 构建 ====================

    private String buildPolishPrompt(String text, String type) {
        String style;
        switch (type) {
            case "concise":
                style = "简洁明了，去除冗余描述，突出重点";
                break;
            case "creative":
                style = "富有创意，用词新颖，展现个性";
                break;
            default:
                style = "专业正式，用词精准，符合职场规范";
                break;
        }
        return String.format(
                "请对以下简历内容进行润色，要求：%s\n\n原文：\n%s\n\n请直接返回润色后的内容，不要添加任何解释或说明。",
                style, text
        );
    }

    private String getStatusText(Integer status) {
        if (status == null) return "UNKNOWN";
        switch (status) {
            case 0:
                return "PENDING";
            case 1:
                return "PROCESSING";
            case 2:
                return "COMPLETED";
            case 3:
                return "FAILED";
            default:
                return "UNKNOWN";
        }
    }

    private Long getUserIdFromHeader(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
            String token = authHeader.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}
