package com.smartresume.controller;

import com.smartresume.common.Result;
import com.smartresume.entity.Resume;
import com.smartresume.entity.ResumeParseResult;
import com.smartresume.service.QwenFileService;
import com.smartresume.service.QwenService;
import com.smartresume.service.ResumeService;
import com.smartresume.service.impl.ResumeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 简历解析控制器（支持文件上传 + 纯文本解析）
 */
@RestController
@RequestMapping("/api/resume-parse")
public class ResumeController {
    @Autowired
    private QwenService qwenService;

    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);

    private final ResumeService resumeService;

    @Autowired
    private QwenFileService qwenFileService;  // 添加这行

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * 仅提取文本（用于文件上传）
     */
    @PostMapping("/extract")
    public ResponseEntity<String> extractText(@RequestParam("file") MultipartFile file) {
        validateFile(file, false); // 允许 PDF/DOCX

        try {
            String text = resumeService.extractTextFromPdfOrWord(file);
            return ResponseEntity.ok(text != null ? text : "");
        } catch (Exception e) {
            log.error("文本提取失败: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(500).body("提取失败: " + e.getMessage());
        }
    }

    /**
     * 通过已提取的纯文本进行 AI 结构化解析（前端传字符串）
     */
    @PostMapping("/parse")
    public ResponseEntity<ResumeParseResult> parseFromText(@RequestBody String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            throw new IllegalArgumentException("简历文本不能为空");
        }
        try {
            ResumeParseResult result = resumeService.parse(rawText.trim());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("AI解析失败", e);
            throw new RuntimeException("AI解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传文件并解析（旧版：先提取文本再AI分析）
     */
    @PostMapping("/upload-parse")
    public ResponseEntity<ResumeParseResult> parseUploadedResume(@RequestParam("file") MultipartFile file) {
        log.info("========== 收到上传解析请求（旧版） ==========");
        log.info("文件名: {}", file.getOriginalFilename());
        log.info("文件大小: {} bytes", file.getSize());

        validateFile(file, true);
        try {
            log.info("开始提取文本...");
            String text = resumeService.extractTextFromPdfOrWord(file);
            log.info("文本提取完成，长度: {} 字符", text.length());

            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("无法从文件中提取有效文本");
            }

            log.info("开始调用AI解析...");
            ResumeParseResult result = resumeService.parse(text.trim());
            log.info("AI解析完成，姓名: {}, 技能数: {}", result.getName(), result.getSkillList().size());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("文件解析失败", e);
            throw new RuntimeException("文件解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用通义千问文件API直接解析上传的简历（新版，真正的AI分析）
     */
    @PostMapping("/upload-parse-v2")
    public ResponseEntity<ResumeParseResult> parseUploadedResumeV2(@RequestParam("file") MultipartFile file) {
        log.info("========== 收到文件上传解析请求（新版文件API） ==========");
        log.info("文件名: {}, 大小: {}", file.getOriginalFilename(), file.getSize());

        validateFile(file, true);

        try {
            log.info("开始调用通义千问文件API...");
            ResumeParseResult result = resumeService.parseWithFile(file);
            log.info("文件API解析完成，姓名: {}, 技能数: {}", result.getName(), result.getSkillList().size());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("文件API解析失败", e);
            throw new RuntimeException("文件解析失败: " + e.getMessage(), e);
        }
    }

    // ---------------- 工具方法 ----------------

    private void validateFile(MultipartFile file, boolean checkExtension) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择一个文件");
        }
        if (file.getSize() > 10 * 1024 * 1024) { // 改为10MB
            throw new IllegalArgumentException("文件大小不能超过 10MB");
        }
        if (!checkExtension) return;

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("文件名无效");
        }
        String lowerName = filename.toLowerCase();
        if (!lowerName.endsWith(".pdf") && !lowerName.endsWith(".docx") && !lowerName.endsWith(".doc") && !lowerName.endsWith(".txt")) {
            throw new IllegalArgumentException("仅支持 PDF、DOCX、DOC 或 TXT 格式");
        }
    }

    @GetMapping("/test-ai")
    public ResponseEntity<String> testAI() {
        try {
            String result = qwenService.generateText("你好，请用一句话介绍自己。");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("测试失败: " + e.getMessage());
        }
    }

    @GetMapping("/test-qwen-file")
    public ResponseEntity<String> testQwenFile() {
        try {
            String result = qwenFileService.testApi();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("文件API测试失败: " + e.getMessage());
        }
    }

    @GetMapping("/test-qwen")
    public String testQwen() {
        try {
            return qwenService.generateText("请回复'测试成功'");
        } catch (Exception e) {
            return "测试失败: " + e.getMessage();
        }
    }

    // ==================== 简历管理CRUD接口 ====================

    /**
     * 获取所有简历（简化版，无分页）
     */
    @GetMapping("/all")
    public Result<List<Resume>> getAllResumes() {
        try {
            List<Resume> resumes = resumeService.list();
            return Result.success(resumes);
        } catch (Exception e) {
            log.error("获取简历列表失败", e);
            return Result.error("获取简历列表失败: " + e.getMessage());
        }
    }

    /**
     * 快速获取简历信息（简化版）
     */
    @GetMapping("/info/{id}")
    public Result<Resume> getResumeInfo(@PathVariable Long id) {
        try {
            Resume resume = resumeService.getById(id);
            if (resume == null) {
                return Result.error(404, "简历不存在");
            }
            return Result.success(resume);
        } catch (Exception e) {
            log.error("获取简历信息失败", e);
            return Result.error("获取简历信息失败: " + e.getMessage());
        }
    }

}

