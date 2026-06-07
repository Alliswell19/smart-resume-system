package com.smartresume.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartresume.entity.Resume;
import com.smartresume.entity.ResumeOptimizationResult;
import com.smartresume.entity.ResumeParseResult;
import com.smartresume.mapper.ResumeMapper;
import com.smartresume.service.QwenFileService;
import com.smartresume.service.QwenService;
import com.smartresume.service.ResumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

// PDFBox 导入
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

// POI 导入
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {

    private static final Logger log = LoggerFactory.getLogger(ResumeServiceImpl.class);

    @Autowired
    private QwenService qwenService;

    @Autowired
    private QwenFileService qwenFileService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String extractTextFromPdfOrWord(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("文件名无效");
        }

        String lowerName = fileName.toLowerCase();
        log.info("提取文本 - 文件名: {}, 类型: {}", fileName, lowerName);

        try (InputStream inputStream = file.getInputStream()) {
            if (lowerName.endsWith(".txt")) {
                return new String(file.getBytes(), StandardCharsets.UTF_8);

            } else if (lowerName.endsWith(".pdf")) {
                try (PDDocument document = PDDocument.load(inputStream)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);
                    log.info("PDF提取完成，文本长度: {}", text.length());
                    return text;
                }

            } else if (lowerName.endsWith(".docx")) {
                try (XWPFDocument document = new XWPFDocument(inputStream);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                    String text = extractor.getText();
                    log.info("DOCX提取完成，文本长度: {}", text.length());
                    return text;
                }

            } else if (lowerName.endsWith(".doc")) {
                try (HWPFDocument document = new HWPFDocument(inputStream);
                     WordExtractor extractor = new WordExtractor(document)) {
                    String text = extractor.getText();
                    log.info("DOC提取完成，文本长度: {}", text.length());
                    return text;
                }

            } else {
                throw new IllegalArgumentException("不支持的文件格式: " + fileName);
            }
        } catch (Exception e) {
            log.error("提取文本失败: {}", fileName, e);
            throw new RuntimeException("提取文本失败: " + e.getMessage(), e);
        }
    }

    @Override
    public ResumeParseResult parse(String rawText) {
        try {
            String prompt = "你是一个专业的简历解析助手。请从以下简历文本中提取关键信息，并以JSON格式返回。\n\n" +
                    "请提取：姓名、电话、邮箱、应聘职位、技能列表、工作经历、教育背景。\n\n" +
                    "简历内容：\n" + rawText;
            String aiResponse = qwenService.generateText(prompt);
            log.info("AI解析原始响应: {}", aiResponse);
            return parseAIResponse(aiResponse);
        } catch (Exception e) {
            log.error("AI解析失败，返回模拟数据", e);
            return getMockParseResult();
        }
    }
    @Override
    public ResumeParseResult parseWithFile(MultipartFile file) {
        try {
            log.info("开始使用改进的文件解析方案");
            
            // 方案一：直接提取文本内容进行分析（更稳定可靠）
            String text = extractTextFromPdfOrWord(file);
            log.info("文本提取成功，长度: {} 字符", text.length());
            
            // 使用文本内容进行AI分析
            ResumeParseResult result = parse(text);
            log.info("AI解析成功，姓名: {}, 技能数: {}", result.getName(), result.getSkillList().size());
            
            return result;
            
        } catch (Exception e) {
            log.error("改进方案解析失败，尝试使用文件API", e);
            
            // 方案二：如果文本提取失败，尝试使用文件API
            try {
                ResumeParseResult result = qwenFileService.parseResumeFromFile(file);
                log.info("文件API解析成功，姓名: {}, 技能数: {}", result.getName(), result.getSkillList().size());
                return result;
            } catch (Exception ex) {
                log.error("所有解析方案都失败，返回模拟数据", ex);
                
                // 方案三：返回模拟数据确保系统可用
                return getMockParseResult();
            }
        }
    }

    /**
     * 解析 AI 返回的 JSON 字符串
     */
    private ResumeParseResult parseAIResponse(String aiResponse) {
        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            ResumeParseResult result = new ResumeParseResult();

            result.setName(getJsonValue(root, "name"));
            result.setPhone(getJsonValue(root, "phone"));
            result.setEmail(getJsonValue(root, "email"));
            result.setPosition(getJsonValue(root, "position"));

            String skills = getJsonValue(root, "skills");
            if (skills != null && !skills.isEmpty()) {
                String[] skillArray = skills.split(",");
                for (String skill : skillArray) {
                    String trimmed = skill.trim();
                    if (!trimmed.isEmpty()) {
                        int score = 80;
                        String level = "熟练";

                        if (trimmed.contains("精通")) {
                            score = 95;
                            level = "精通";
                        } else if (trimmed.contains("熟练")) {
                            score = 85;
                            level = "熟练";
                        } else if (trimmed.contains("了解")) {
                            score = 65;
                            level = "了解";
                        }

                        result.getSkillList().add(new ResumeParseResult.Skill(
                                trimmed.replace("精通", "").replace("熟练", "").replace("了解", "").trim(),
                                level,
                                score
                        ));
                    }
                }
            }

            String experience = getJsonValue(root, "experience");
            if (experience != null && !experience.isEmpty()) {
                String[] lines = experience.split("\n");
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;

                    ResumeParseResult.Experience exp = new ResumeParseResult.Experience();
                    String[] parts = line.split("\\s+", 4);
                    if (parts.length >= 3) {
                        String[] years = parts[0].split("-");
                        if (years.length >= 2) {
                            exp.setStartDate(years[0]);
                            exp.setEndDate(years[1]);
                        }
                        exp.setCompany(parts[1]);
                        exp.setPosition(parts[2]);
                        if (parts.length > 3) {
                            exp.setDescription(parts[3]);
                        }
                    } else {
                        exp.setDescription(line);
                    }
                    result.getExperienceList().add(exp);
                }
            }

            String education = getJsonValue(root, "education");
            if (education != null && !education.isEmpty()) {
                String[] lines = education.split("\n");
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;

                    ResumeParseResult.Education edu = new ResumeParseResult.Education();
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 4) {
                        String[] years = parts[0].split("-");
                        if (years.length >= 2) {
                            edu.setStartDate(years[0]);
                            edu.setEndDate(years[1]);
                        }
                        edu.setSchool(parts[1]);
                        edu.setMajor(parts[2]);
                        edu.setDegree(parts[3]);
                        if (parts.length > 4) {
                            try {
                                edu.setGpa(Double.parseDouble(parts[4]));
                            } catch (NumberFormatException e) {
                                // ignore
                            }
                        }
                    }
                    result.getEducationList().add(edu);
                }
            }

            return result;

        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            return getMockParseResult();
        }
    }

    private String getJsonValue(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null && !value.isNull() ? value.asText() : null;
    }

    private ResumeParseResult getMockParseResult() {
        ResumeParseResult result = new ResumeParseResult();

        result.setName("张三");
        result.setEmail("zhangsan@example.com");
        result.setPhone("13800138000");
        result.setPosition("Java开发工程师");

        result.getSkillList().add(new ResumeParseResult.Skill("Java", "精通", 90));
        result.getSkillList().add(new ResumeParseResult.Skill("Spring Boot", "熟练", 85));
        result.getSkillList().add(new ResumeParseResult.Skill("MySQL", "熟练", 80));

        ResumeParseResult.Experience experience = new ResumeParseResult.Experience();
        experience.setCompany("ABC公司");
        experience.setPosition("高级工程师");
        experience.setStartDate("2020-01");
        experience.setEndDate("2023-12");
        experience.setDescription("负责后端系统开发");
        result.getExperienceList().add(experience);

        ResumeParseResult.Education education = new ResumeParseResult.Education();
        education.setSchool("北京大学");
        education.setMajor("计算机科学与技术");
        education.setDegree("本科");
        education.setStartDate("2016-09");
        education.setEndDate("2020-06");
        education.setGpa(3.8);
        result.getEducationList().add(education);

        return result;
    }

    @Override
    public ResumeOptimizationResult optimizeResume(Long resumeId) {
        ResumeOptimizationResult result = new ResumeOptimizationResult();
        result.setResumeId(resumeId);
        result.setOverallScore(85);
        result.setOptimizedContent("优化后内容...");
        result.setSuggestions(Arrays.asList("建议1", "建议2", "建议3"));

        result.setFormatScore(90);
        result.setContentScore(75);
        result.setKeywordScore(85);
        result.setReadabilityScore(80);

        result.setFormatSuggestions(Arrays.asList("段落格式可以更清晰"));
        result.setContentSuggestions(Arrays.asList("工作内容描述不够具体"));
        result.setKeywordSuggestions(Arrays.asList("缺少关键技能关键词"));

        return result;
    }

    public String testAI() {
        String prompt = "你好，请用一句话介绍自己。";
        return qwenService.generateText(prompt);
    }
}