package com.smartresume.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartresume.service.QwenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QwenServiceImpl implements QwenService {

    private static final Logger log = LoggerFactory.getLogger(QwenServiceImpl.class);

    @Value("${dashscope.api.key}")
    private String apiKey;

    @Value("${dashscope.api.chat-url}")
    private String chatUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public QwenServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        ((org.springframework.http.client.SimpleClientHttpRequestFactory) restTemplate.getRequestFactory())
                .setConnectTimeout(120000);
        ((org.springframework.http.client.SimpleClientHttpRequestFactory) restTemplate.getRequestFactory())
                .setReadTimeout(120000);
    }

    @Override
    public String generateText(String prompt) {
        log.info("========== 调用通义千问 API ==========");
        log.info("API Key: {}", apiKey);
        log.info("Prompt: {}", prompt);

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen3.5-plus");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            requestBody.put("messages", messages);

            // 可选参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("temperature", 0.8);
            parameters.put("max_tokens", 3000);
            requestBody.put("parameters", parameters);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("请求体: {}", objectMapper.writeValueAsString(requestBody));

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("响应: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            } else {
                return "错误: " + response.getBody();
            }
        } catch (Exception e) {
            log.error("调用失败", e);
            return "失败: " + e.getMessage();
        }
    }

    @Override
    public String parseResume(String resumeText) {
        String prompt = "你是一个专业的简历解析助手。请从以下简历文本中提取关键信息，并以JSON格式返回。\n\n" +
                "要求返回的JSON必须包含以下字段：\n" +
                "1. name: 姓名\n" +
                "2. phone: 电话\n" +
                "3. email: 邮箱\n" +
                "4. position: 应聘职位\n" +
                "5. skills: 技能列表，用逗号分隔，可以包含熟练度（例如：Java(精通), Spring Boot(熟练)）\n" +
                "6. experience: 工作经历，每行一个，格式为：开始年份-结束年份 公司 职位 职责描述\n" +
                "7. education: 教育背景，每行一个，格式为：开始年份-结束年份 学校 专业 学历 GPA\n\n" +
                "返回格式示例：\n" +
                "{\n" +
                "  \"name\": \"张三\",\n" +
                "  \"phone\": \"13800138000\",\n" +
                "  \"email\": \"zhangsan@example.com\",\n" +
                "  \"position\": \"Java开发工程师\",\n" +
                "  \"skills\": \"Java(精通),Spring Boot(熟练),MySQL(熟练),Redis(了解)\",\n" +
                "  \"experience\": \"2020-2023 科技公司 Java开发工程师 负责微服务架构设计\\n2018-2020 软件公司 初级开发 参与项目开发\",\n" +
                "  \"education\": \"2016-2020 北京大学 计算机科学与技术 本科 3.8\\n2013-2016 北京四中 高中 理科\"\n" +
                "}\n\n" +
                "注意事项：\n" +
                "1. 如果某个字段无法提取，请返回空字符串\n" +
                "2. 请确保返回的是合法的JSON格式\n" +
                "3. 不要包含任何额外的说明文字\n\n" +
                "简历内容：\n" + resumeText;

        return generateText(prompt);
    }
}