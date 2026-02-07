package com.smartresume.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class QwenService {

    @Value("${dashscope.api.key}")
    private String apiKey;

    @Value("${dashscope.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateText(String prompt) {
        // 构造请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-max"); // 你可以换成 qwen-turbo 测试更快

        Map<String, Object> input = new HashMap<>();
        input.put("prompt", prompt);
        requestBody.put("input", input);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("output").path("text").asText();
            } else {
                throw new RuntimeException("Qwen API 返回错误: " + response.getStatusCode() + ", body: " + response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("调用 Qwen 失败: " + e.getMessage());
        }
    }
}