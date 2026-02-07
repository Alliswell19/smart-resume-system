package com.smartresume.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DashScopeClient {

    @Value("${dashscope.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public DashScopeClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callQwen(String prompt) {
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); // 使用Bearer认证

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-turbo");

        // 构造 input 字段（使用 Java 8 兼容写法）
        Map<String, Object> input = new HashMap<>();
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        input.put("messages", messages);

        requestBody.put("input", input);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Qwen API请求失败: " + response.getStatusCode());
        }
        return response.getBody();
    }
}