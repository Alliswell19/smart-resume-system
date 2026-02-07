package com.smartresume.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockQwenController {

    @PostMapping("/api/ai/parse-resume")
    public String parseResume(@RequestBody String body) {
        // 返回固定 JSON（模拟 Qwen 响应）
        return "{\n" +
                "  \"name\": \"张三\",\n" +
                "  \"phone\": \"13800138000\",\n" +
                "  \"email\": \"zhangsan@example.com\",\n" +
                "  \"skills\": [\"Java\", \"Spring Boot\", \"MySQL\"],\n" +
                "  \"experience\": [\n" +
                "    {\n" +
                "      \"company\": \"阿里巴巴\",\n" +
                "      \"role\": \"后端开发工程师\",\n" +
                "      \"duration\": \"2020-2024\",\n" +
                "      \"description\": \"负责电商系统后端开发\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"education\": [\n" +
                "    {\n" +
                "      \"school\": \"北京大学\",\n" +
                "      \"degree\": \"本科\",\n" +
                "      \"major\": \"计算机科学与技术\",\n" +
                "      \"duration\": \"2016-2020\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}