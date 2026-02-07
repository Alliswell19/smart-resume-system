package com.smartresume.controller;

import com.smartresume.entity.ResumeParseResult;
import com.smartresume.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 简历解析控制器（支持文件上传 + 纯文本解析）
 */
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private static final Logger log = LoggerFactory.getLogger(ResumeController.class);

    private final ResumeService resumeService;

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
     * （可选）直接上传文件并解析（如果你需要一步完成）
     */
    @PostMapping("/upload-parse")
    public ResponseEntity<ResumeParseResult> parseUploadedResume(@RequestParam("file") MultipartFile file) {
        validateFile(file, true);
        try {
            String text = resumeService.extractTextFromPdfOrWord(file);
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("无法从文件中提取有效文本");
            }
            ResumeParseResult result = resumeService.parse(text.trim());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("文件解析失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("文件解析失败: " + e.getMessage(), e);
        }
    }

    // ---------------- 工具方法 ----------------

    private void validateFile(MultipartFile file, boolean checkExtension) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择一个文件");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("文件大小不能超过 5MB");
        }
        if (!checkExtension) return;

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("文件名无效");
        }
        String lowerName = filename.toLowerCase();
        if (!lowerName.endsWith(".pdf") && !lowerName.endsWith(".docx") && !lowerName.endsWith(".doc")) {
            throw new IllegalArgumentException("仅支持 PDF、DOCX 或 DOC 格式");
        }
    }
}