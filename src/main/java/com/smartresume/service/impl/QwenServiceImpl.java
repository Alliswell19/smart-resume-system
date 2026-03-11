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

import java.util.HashMap;
import java.util.Map;

@Service
public class QwenServiceImpl implements QwenService {

    private static final Logger log = LoggerFactory.getLogger(QwenServiceImpl.class);

    @Value("${dashscope.api.key}")
    private String apiKey;

    @Value("${dashscope.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;  // 添加这行

    public QwenServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();  // 初始化
        // 设置超时时间
        ((org.springframework.http.client.SimpleClientHttpRequestFactory) restTemplate.getRequestFactory())
                .setConnectTimeout(60000);
        ((org.springframework.http.client.SimpleClientHttpRequestFactory) restTemplate.getRequestFactory())
                .setReadTimeout(60000);
    }

    @Override
    public String generateText(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-turbo"); // 使用更快的模型

        // 通义千问 API 格式
        Map<String, Object> input = new HashMap<>();
        input.put("messages", new Object[]{
                Map.of("role", "user", "content", prompt)
        });
        requestBody.put("input", input);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("temperature", 0.8);
        parameters.put("max_tokens", 2000);
        requestBody.put("parameters", parameters);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            log.info("调用通义千问 API，URL: {}", apiUrl);
            long startTime = System.currentTimeMillis();

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            long endTime = System.currentTimeMillis();
            log.info("API调用耗时: {} ms", (endTime - startTime));

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(response.getBody());
                // 通义千问的响应格式可能不同，需要根据实际返回调整
                String result = root.path("output").path("text").asText();
                if (result.isEmpty()) {
                    // 尝试其他可能的路径
                    result = root.path("choices").path(0).path("message").path("content").asText();
                }
                log.info("AI响应成功: {}", result.substring(0, Math.min(100, result.length())));
                return result;
            } else {
                throw new RuntimeException("Qwen API 返回错误: " + response.getStatusCode() + ", body: " + response.getBody());
            }
        } catch (Exception e) {
            log.error("调用通义千问失败", e);
            throw new RuntimeException("调用 Qwen 失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String parseResume(String resumeText) {
        // 构建专业的简历解析提示词
        String prompt = "你是一个专业的简历解析助手。请从以下简历文本中提取关键信息，并以JSON格式返回。\n\n" +
                "要求：\n" +
                "1. 提取姓名、电话、邮箱、应聘职位\n" +
                "2. 提取所有技能，用逗号分隔\n" +
                "3. 提取工作经历，包括公司、职位、时间、职责\n" +
                "4. 提取教育背景，包括学校、专业、学历、时间\n\n" +
                "返回格式示例：\n" +
                "{\n" +
                "  \"name\": \"张三\",\n" +
                "  \"phone\": \"13800138000\",\n" +
                "  \"email\": \"zhangsan@example.com\",\n" +
                "  \"position\": \"Java开发工程师\",\n" +
                "  \"skills\": \"Java,Spring,MySQL,Redis\",\n" +
                "  \"experience\": \"2020-2023 科技公司 Java开发工程师\\n负责后端系统开发...\",\n" +
                "  \"education\": \"2016-2020 北京大学 计算机科学与技术 本科\"\n" +
                "}\n\n" +
                "如果某个字段无法提取，请返回空字符串。\n\n" +
                "简历内容：\n" + resumeText;

        return generateText(prompt);
    }
}