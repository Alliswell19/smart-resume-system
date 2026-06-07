package com.smartresume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("resume_education")
public class ResumeEducation {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    
    private String school;
    
    private String major;
    
    private String degree;
    
    private String startDate;
    
    private String endDate;
    
    private BigDecimal gpa;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}