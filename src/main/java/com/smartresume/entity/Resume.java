package com.smartresume.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Resume {

    private Long id;
    private Long userId;
    private String originalText;
    private String optimizedText;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadTime;
    private Boolean isParsed; // ←←← 必须是 isParsed
}