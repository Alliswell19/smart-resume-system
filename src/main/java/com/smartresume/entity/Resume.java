package com.smartresume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("resume")
public class Resume {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;           // 所属用户ID

    private String name;            // 姓名

    private String email;           // 邮箱

    private String phone;           // 电话

    private String position;        // 应聘职位

    private String skills;          // 技能（可能是JSON或逗号分隔）

    private String experience;      // 工作经验（可能是JSON）

    private String education;       // 教育背景（可能是JSON）

    private String fileName;        // 文件名

    private String filePath;        // 文件路径

    private Long fileSize;          // 文件大小

    private String fileType;        // 文件类型

    private String originalText;    // 原始文本内容

    private Boolean isParsed;       // 是否已解析（兼容旧字段）

    private Integer parseStatus;    // 解析状态 0-待解析 1-解析中 2-成功 3-失败

    private String parseResult;     // 解析结果

    private Integer score;          // 综合评分

    private Integer viewCount;      // 浏览次数

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;  // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;  // 更新时间

    @TableLogic
    private Boolean isDeleted;      // 逻辑删除
}