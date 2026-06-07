package com.smartresume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("resume_skill")
public class ResumeSkill {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    
    private String skillName;
    
    private String skillLevel;
    
    private Integer skillScore;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}