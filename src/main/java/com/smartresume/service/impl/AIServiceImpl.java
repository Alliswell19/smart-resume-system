package com.smartresume.service.impl;

import com.smartresume.entity.ResumeOptimizationResult;
import com.smartresume.entity.ResumeParseResult;
import com.smartresume.service.AIService;
import com.smartresume.service.QwenService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIServiceImpl implements AIService {

    @Resource
    private QwenService qwenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResumeParseResult parseResumeText(String rawText) {
        // 构建解析提示词
        String prompt = "请解析以下简历文本，提取结构化信息，包括：姓名、邮箱、电话、技能（包含名称、熟练度、评分）、工作经历（包含公司、职位、开始日期、结束日期）、教育背景（包含学校、专业、学位）。\n\n" +
                "简历文本：" + rawText + "\n\n" +
                "请以JSON格式返回结果，字段名如下：\n" +
                "{\n" +
                "  \"name\": \"姓名\",\n" +
                "  \"email\": \"邮箱\",\n" +
                "  \"phone\": \"电话\",\n" +
                "  \"position\": \"应聘职位\",\n" +
                "  \"skills\": [\n" +
                "    {\n" +
                "      \"name\": \"技能名称\",\n" +
                "      \"level\": \"熟练度\",\n" +
                "      \"score\": 技能评分\n" +
                "    }\n" +
                "  ],\n" +
                "  \"experience\": [\n" +
                "    {\n" +
                "      \"company\": \"公司名称\",\n" +
                "      \"position\": \"职位\",\n" +
                "      \"startDate\": \"开始日期\",\n" +
                "      \"endDate\": \"结束日期\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"education\": [\n" +
                "    {\n" +
                "      \"school\": \"学校名称\",\n" +
                "      \"major\": \"专业\",\n" +
                "      \"degree\": \"学位\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try {
            // 调用Qwen API解析简历
            String response = qwenService.generateText(prompt);

            // 解析JSON响应
            JsonNode root = objectMapper.readTree(response);

            ResumeParseResult result = new ResumeParseResult();
            result.setName(root.path("name").asText());
            result.setEmail(root.path("email").asText());
            result.setPhone(root.path("phone").asText());
            result.setPosition(root.path("position").asText());

            // 解析技能
            List<ResumeParseResult.Skill> skills = new ArrayList<>();
            JsonNode skillsNode = root.path("skills");
            if (skillsNode.isArray()) {
                for (JsonNode skillNode : skillsNode) {
                    ResumeParseResult.Skill skill = new ResumeParseResult.Skill();
                    skill.setName(skillNode.path("name").asText());
                    skill.setLevel(skillNode.path("level").asText());
                    skill.setScore(skillNode.path("score").asInt());
                    skills.add(skill);
                }
            }
            result.setSkillList(skills);

            // 解析工作经历
            List<ResumeParseResult.Experience> experiences = new ArrayList<>();
            JsonNode experienceNode = root.path("experience");
            if (experienceNode.isArray()) {
                for (JsonNode expNode : experienceNode) {
                    ResumeParseResult.Experience exp = new ResumeParseResult.Experience();
                    exp.setCompany(expNode.path("company").asText());
                    exp.setPosition(expNode.path("position").asText());
                    exp.setStartDate(expNode.path("startDate").asText());
                    exp.setEndDate(expNode.path("endDate").asText());
                    experiences.add(exp);
                }
            }
            result.setExperienceList(experiences);

            // 解析教育背景
            List<ResumeParseResult.Education> educations = new ArrayList<>();
            JsonNode educationNode = root.path("education");
            if (educationNode.isArray()) {
                for (JsonNode eduNode : educationNode) {
                    ResumeParseResult.Education edu = new ResumeParseResult.Education();
                    edu.setSchool(eduNode.path("school").asText());
                    edu.setMajor(eduNode.path("major").asText());
                    edu.setDegree(eduNode.path("degree").asText());
                    educations.add(edu);
                }
            }
            result.setEducationList(educations);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            // 如果API调用失败，返回模拟数据
            return getMockParseResult();
        }
    }

    @Override
    public ResumeOptimizationResult getOptimizationSuggestions(Long resumeId) {
        // 构建优化提示词
        String prompt = "请为简历ID " + resumeId + " 生成优化建议，包括：\n" +
                "1. 总体评分（0-100）\n" +
                "2. 优化后的简历内容\n" +
                "3. 具体改进建议（至少3条）\n\n" +
                "请以JSON格式返回结果，字段名如下：\n" +
                "{\n" +
                "  \"overallScore\": 总体评分,\n" +
                "  \"optimizedContent\": \"优化后的简历内容\",\n" +
                "  \"suggestions\": [\"建议1\", \"建议2\", \"建议3\"]\n" +
                "}";

        try {
            // 调用Qwen API获取优化建议
            String response = qwenService.generateText(prompt);

            // 解析JSON响应
            JsonNode root = objectMapper.readTree(response);

            ResumeOptimizationResult result = new ResumeOptimizationResult();
            result.setResumeId(resumeId);
            result.setOverallScore(root.path("overallScore").asInt());
            result.setOptimizedContent(root.path("optimizedContent").asText());

            // 解析建议
            List<String> suggestions = new ArrayList<>();
            JsonNode suggestionsNode = root.path("suggestions");
            if (suggestionsNode.isArray()) {
                for (JsonNode suggestionNode : suggestionsNode) {
                    suggestions.add(suggestionNode.asText());
                }
            }
            result.setSuggestions(suggestions);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            // 如果API调用失败，返回模拟数据
            return getMockOptimizationResult(resumeId);
        }
    }

    // 模拟解析结果
    private ResumeParseResult getMockParseResult() {
        ResumeParseResult result = new ResumeParseResult();
        result.setName("张三");
        result.setEmail("zhangsan@example.com");
        result.setPhone("13800138000");
        result.setPosition("Java开发工程师");

        // 技能
        List<ResumeParseResult.Skill> skills = new ArrayList<>();
        skills.add(new ResumeParseResult.Skill("Java", "精通", 90));
        skills.add(new ResumeParseResult.Skill("Spring Boot", "熟练", 85));
        skills.add(new ResumeParseResult.Skill("MySQL", "熟练", 80));
        result.setSkillList(skills);

        // 工作经历
        List<ResumeParseResult.Experience> experiences = new ArrayList<>();
        ResumeParseResult.Experience exp = new ResumeParseResult.Experience();
        exp.setCompany("ABC科技有限公司");
        exp.setPosition("后端开发工程师");
        exp.setStartDate("2020-03");
        exp.setEndDate("至今");
        experiences.add(exp);
        result.setExperienceList(experiences);

        // 教育背景
        List<ResumeParseResult.Education> educations = new ArrayList<>();
        ResumeParseResult.Education edu = new ResumeParseResult.Education();
        edu.setSchool("北京大学");
        edu.setMajor("计算机科学与技术");
        edu.setDegree("硕士");
        educations.add(edu);
        result.setEducationList(educations);

        return result;
    }

    // 模拟优化结果
    private ResumeOptimizationResult getMockOptimizationResult(Long resumeId) {
        ResumeOptimizationResult result = new ResumeOptimizationResult();
        result.setResumeId(resumeId);
        result.setOverallScore(85);
        result.setOptimizedContent("经过AI优化后的简历内容...");

        List<String> suggestions = new ArrayList<>();
        suggestions.add("建议1：增加量化成果描述，使用具体数字展示工作成就");
        suggestions.add("建议2：突出核心技能，将最重要的技能放在前面");
        suggestions.add("建议3：优化工作经历描述，使用STAR法则（情境、任务、行动、结果）");
        result.setSuggestions(suggestions);

        return result;
    }
}