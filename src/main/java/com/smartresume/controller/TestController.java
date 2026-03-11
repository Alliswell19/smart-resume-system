package com.smartresume.controller;

import com.smartresume.common.Result;
import com.smartresume.service.QwenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired(required = false)  // 设为非必须，避免服务未实现时报错
    private QwenService qwenService;

    @GetMapping("/hello-qwen")
    public Result helloQwen() {
        try {
            if (qwenService == null) {
                return Result.error("Qwen服务未启用");
            }
            String prompt = "你好，请用一句话介绍通义千问。";
            String response = qwenService.generateText(prompt);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("服务暂不可用: " + e.getMessage());
        }
    }
}