package com.smartresume.controller;

import com.smartresume.service.QwenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private QwenService qwenService;

    @GetMapping("/hello-qwen")
    public String helloQwen() {
        String prompt = "你好，请用一句话介绍通义千问。";
        return qwenService.generateText(prompt);
    }
}