package com.smartresume.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartresume.entity.Resume;
import com.smartresume.entity.ResumeParseResult;
import com.smartresume.entity.User;
import com.smartresume.mapper.ResumeMapper;
import com.smartresume.mapper.UserMapper;
import com.smartresume.service.QwenService;
import com.smartresume.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// PDFBox 导入
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

// POI 导入
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

@RestController
@RequestMapping("/api/resume")
public class ResumeCrudController {

    private static final Logger log = LoggerFactory.getLogger(ResumeCrudController.class);

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private QwenService qwenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 文件上传目录
    private final String UPLOAD_DIR = "uploads/";

    /**
     * 获取简历列表（分页）
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getResumeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "未登录或token无效");
                response.put("code", 401);
                return ResponseEntity.status(401).body(response);
            }

            QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("is_deleted", 0);

            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.and(wrapper ->
                        wrapper.like("name", keyword)
                                .or()
                                .like("email", keyword)
                                .or()
                                .like("position", keyword)
                                .or()
                                .like("skills", keyword)
                                .or()
                                .like("file_name", keyword)
                );
            }

            if (status != null) {
                if ("parsed".equals(status)) {
                    queryWrapper.eq("parse_status", 2);
                } else if ("pending".equals(status)) {
                    queryWrapper.eq("parse_status", 0);
                } else if ("failed".equals(status)) {
                    queryWrapper.eq("parse_status", 3);
                }
            }

            queryWrapper.orderByDesc("create_time");

            Page<Resume> pageObj = new Page<>(page, pageSize);
            Page<Resume> resultPage = resumeMapper.selectPage(pageObj, queryWrapper);

            Map<String, Object> data = new HashMap<>();
            data.put("list", resultPage.getRecords());
            data.put("total", resultPage.getTotal());
            data.put("page", resultPage.getCurrent());
            data.put("pageSize", resultPage.getSize());

            response.put("success", true);
            response.put("data", data);
            response.put("message", "查询成功");
            response.put("code", 200);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("获取简历列表失败", e);
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取简历详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getResumeDetail(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "未登录或token无效");
                response.put("code", 401);
                return ResponseEntity.status(401).body(response);
            }

            Resume resume = resumeMapper.selectById(id);
            if (resume == null) {
                response.put("success", false);
                response.put("message", "简历不存在");
                response.put("code", 404);
                return ResponseEntity.status(404).body(response);
            }

            if (!resume.getUserId().equals(userId)) {
                response.put("success", false);
                response.put("message", "无权访问该简历");
                response.put("code", 403);
                return ResponseEntity.status(403).body(response);
            }

            resume.setViewCount(resume.getViewCount() + 1);
            resumeMapper.updateById(resume);

            response.put("success", true);
            response.put("data", resume);
            response.put("message", "查询成功");
            response.put("code", 200);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("获取简历详情失败", e);
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 上传简历
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "未登录或token无效");
                response.put("code", 401);
                return ResponseEntity.status(401).body(response);
            }

            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "文件不能为空");
                response.put("code", 400);
                return ResponseEntity.status(400).body(response);
            }

            if (file.getSize() > 10 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "文件大小不能超过10MB");
                response.put("code", 400);
                return ResponseEntity.status(400).body(response);
            }

            String fileName = file.getOriginalFilename();
            String fileType = "unknown";
            if (fileName != null) {
                String lowerName = fileName.toLowerCase();
                if (lowerName.endsWith(".pdf")) {
                    fileType = "pdf";
                } else if (lowerName.endsWith(".doc")) {
                    fileType = "doc";
                } else if (lowerName.endsWith(".docx")) {
                    fileType = "docx";
                } else if (lowerName.endsWith(".txt")) {
                    fileType = "txt";
                } else {
                    response.put("success", false);
                    response.put("message", "不支持的文件格式");
                    response.put("code", 400);
                    return ResponseEntity.status(400).body(response);
                }
            }

            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Resume resume = new Resume();
            resume.setUserId(userId);
            resume.setFileName(fileName);
            resume.setFilePath(uniqueFileName);
            resume.setFileSize(file.getSize());
            resume.setFileType(fileType);
            resume.setParseStatus(0);
            resume.setViewCount(0);
            resume.setScore(0);
            resume.setIsParsed(false);
            resume.setCreateTime(LocalDateTime.now());
            resume.setUpdateTime(LocalDateTime.now());
            resume.setIsDeleted(false);

            resumeMapper.insert(resume);

            response.put("success", true);
            response.put("data", resume);
            response.put("message", "上传成功");
            response.put("code", 200);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("上传简历失败", e);
            response.put("success", false);
            response.put("message", "上传失败: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 删除简历
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteResume(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "未登录或token无效");
                response.put("code", 401);
                return ResponseEntity.status(401).body(response);
            }

            Resume resume = resumeMapper.selectById(id);
            if (resume == null) {
                response.put("success", false);
                response.put("message", "简历不存在");
                response.put("code", 404);
                return ResponseEntity.status(404).body(response);
            }

            if (!resume.getUserId().equals(userId)) {
                response.put("success", false);
                response.put("message", "无权删除该简历");
                response.put("code", 403);
                return ResponseEntity.status(403).body(response);
            }

            if (resume.getFilePath() != null && !resume.getFilePath().isEmpty()) {
                try {
                    Path filePath = Paths.get(UPLOAD_DIR).resolve(resume.getFilePath());
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    log.warn("删除物理文件失败: {}", e.getMessage());
                }
            }

            resumeMapper.deleteById(id);

            response.put("success", true);
            response.put("message", "删除成功");
            response.put("code", 200);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("删除简历失败", e);
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 解析简历 - 调用 AI 服务提取信息
     */
    @PostMapping("/{id}/parse")
    public ResponseEntity<Map<String, Object>> parseResume(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "未登录");
                response.put("code", 401);
                return ResponseEntity.status(401).body(response);
            }

            Resume resume = resumeMapper.selectById(id);
            if (resume == null || !resume.getUserId().equals(userId)) {
                response.put("success", false);
                response.put("message", "简历不存在");
                response.put("code", 404);
                return ResponseEntity.status(404).body(response);
            }

            // 1. 读取文件内容
            String fileContent = extractTextFromFile(resume);
            if (fileContent == null || fileContent.isEmpty()) {
                response.put("success", false);
                response.put("message", "无法读取文件内容");
                return ResponseEntity.status(400).body(response);
            }

            log.info("成功提取文件内容，长度: {} 字符", fileContent.length());

            // 2. 调用 AI 服务解析简历
            String aiResponse = qwenService.parseResume(fileContent);
            log.info("AI解析结果: {}", aiResponse);

            // 3. 解析 AI 返回的 JSON
            ResumeParseResult parseResult = objectMapper.readValue(aiResponse, ResumeParseResult.class);

            // 4. 更新简历信息
            if (parseResult.getName() != null && !parseResult.getName().isEmpty()) {
                resume.setName(parseResult.getName());
            }
            if (parseResult.getEmail() != null && !parseResult.getEmail().isEmpty()) {
                resume.setEmail(parseResult.getEmail());
            }
            if (parseResult.getPhone() != null && !parseResult.getPhone().isEmpty()) {
                resume.setPhone(parseResult.getPhone());
            }
            if (parseResult.getPosition() != null && !parseResult.getPosition().isEmpty()) {
                resume.setPosition(parseResult.getPosition());
            }

            // 处理技能列表 -> 字符串
            if (parseResult.getSkillList() != null && !parseResult.getSkillList().isEmpty()) {
                parseResult.setSkillList(parseResult.getSkillList());
                resume.setSkills(parseResult.getSkills());
            }

            // 处理工作经验列表 -> 字符串
            if (parseResult.getExperienceList() != null && !parseResult.getExperienceList().isEmpty()) {
                parseResult.setExperienceList(parseResult.getExperienceList());
                resume.setExperience(parseResult.getExperience());
            }

            // 处理教育背景列表 -> 字符串
            if (parseResult.getEducationList() != null && !parseResult.getEducationList().isEmpty()) {
                parseResult.setEducationList(parseResult.getEducationList());
                resume.setEducation(parseResult.getEducation());
            }

            resume.setParseStatus(2); // 解析成功
            resume.setIsParsed(true);
            resume.setParseResult(aiResponse);
            resume.setUpdateTime(LocalDateTime.now());

            resumeMapper.updateById(resume);

            response.put("success", true);
            response.put("message", "解析成功");
            response.put("data", resume);
            response.put("code", 200);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("解析失败", e);
            response.put("success", false);
            response.put("message", "解析失败: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 从文件中提取文本
     */
    private String extractTextFromFile(Resume resume) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(resume.getFilePath());
            File file = filePath.toFile();

            if (!file.exists()) {
                log.error("文件不存在: {}", filePath);
                return null;
            }

            log.info("开始提取文件文本: {}, 类型: {}", resume.getFileName(), resume.getFileType());

            if ("txt".equals(resume.getFileType())) {
                // 读取 TXT 文件
                return Files.readString(filePath);

            } else if ("pdf".equals(resume.getFileType())) {
                return extractTextFromPdf(file);

            } else if ("docx".equals(resume.getFileType())) {
                return extractTextFromDocx(file);

            } else if ("doc".equals(resume.getFileType())) {
                return extractTextFromDoc(file);
            }
        } catch (Exception e) {
            log.error("提取文本失败", e);
        }
        return null;
    }

    /**
     * 提取 PDF 文本
     */
    private String extractTextFromPdf(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception e) {
            log.error("提取PDF文本失败", e);
            return null;
        }
    }

    /**
     * 提取 DOCX 文本
     */
    private String extractTextFromDocx(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        } catch (Exception e) {
            log.error("提取DOCX文本失败", e);
            return null;
        }
    }

    /**
     * 提取 DOC 文本
     */
    private String extractTextFromDoc(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             HWPFDocument document = new HWPFDocument(fis);
             WordExtractor extractor = new WordExtractor(document)) {
            return extractor.getText();
        } catch (Exception e) {
            log.error("提取DOC文本失败", e);
            return null;
        }
    }

    /**
     * 下载简历文件
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadResume(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }

            Resume resume = resumeMapper.selectById(id);
            if (resume == null || !resume.getUserId().equals(userId)) {
                return ResponseEntity.status(404).build();
            }

            if (resume.getFilePath() == null || resume.getFilePath().isEmpty()) {
                return ResponseEntity.status(404).build();
            }

            Path filePath = Paths.get(UPLOAD_DIR).resolve(resume.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.status(404).build();
            }

            String contentDisposition = "attachment; filename=\"" +
                    new String(resume.getFileName().getBytes("UTF-8"), "ISO-8859-1") + "\"";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            log.error("下载文件失败", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 预览简历文件
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> previewResume(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }

            Resume resume = resumeMapper.selectById(id);
            if (resume == null || !resume.getUserId().equals(userId)) {
                return ResponseEntity.status(404).build();
            }

            if (resume.getFilePath() == null || resume.getFilePath().isEmpty()) {
                return ResponseEntity.status(404).build();
            }

            Path filePath = Paths.get(UPLOAD_DIR).resolve(resume.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.status(404).build();
            }

            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            String fileName = resume.getFileName().toLowerCase();

            if (fileName.endsWith(".pdf")) {
                mediaType = MediaType.APPLICATION_PDF;
            } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                mediaType = MediaType.parseMediaType("application/msword");
            } else if (fileName.endsWith(".txt")) {
                mediaType = MediaType.TEXT_PLAIN;
            }

            String contentDisposition = "inline; filename=\"" +
                    new String(resume.getFileName().getBytes("UTF-8"), "ISO-8859-1") + "\"";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentType(mediaType)
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            log.error("预览文件失败", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取简历统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getResumeStats(
            @RequestHeader(value = "Authorization", required = false) String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "未登录或token无效");
                response.put("code", 401);
                return ResponseEntity.status(401).body(response);
            }

            QueryWrapper<Resume> totalWrapper = new QueryWrapper<>();
            totalWrapper.eq("user_id", userId).eq("is_deleted", 0);
            Long total = resumeMapper.selectCount(totalWrapper);

            QueryWrapper<Resume> parsedWrapper = new QueryWrapper<>();
            parsedWrapper.eq("user_id", userId).eq("is_deleted", 0).eq("parse_status", 2);
            Long parsed = resumeMapper.selectCount(parsedWrapper);

            QueryWrapper<Resume> pendingWrapper = new QueryWrapper<>();
            pendingWrapper.eq("user_id", userId).eq("is_deleted", 0).eq("parse_status", 0);
            Long pending = resumeMapper.selectCount(pendingWrapper);

            QueryWrapper<Resume> failedWrapper = new QueryWrapper<>();
            failedWrapper.eq("user_id", userId).eq("is_deleted", 0).eq("parse_status", 3);
            Long failed = resumeMapper.selectCount(failedWrapper);

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", total);
            stats.put("parsed", parsed);
            stats.put("pending", pending);
            stats.put("failed", failed);

            response.put("success", true);
            response.put("data", stats);
            response.put("message", "查询成功");
            response.put("code", 200);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            response.put("code", 500);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 从token中获取userId
     */
    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(jwtToken);
            return getUserIdByUsername(username);
        } catch (Exception e) {
            log.error("解析token失败", e);
            return null;
        }
    }

    /**
     * 根据用户名获取用户ID
     */
    private Long getUserIdByUsername(String username) {
        if (username == null) return null;

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        return user != null ? user.getId() : null;
    }
}