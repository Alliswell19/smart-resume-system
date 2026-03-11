package com.smartresume.service;

public interface QwenService {
    String generateText(String prompt);
    String parseResume(String resumeText);
}