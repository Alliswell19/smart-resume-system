package com.smartresume.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("resume_experience")
public class ResumeExperience {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    
    private String company;
    
    private String position;
    
    private String startDate;
    
    private String endDate;
    
    private String description;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}