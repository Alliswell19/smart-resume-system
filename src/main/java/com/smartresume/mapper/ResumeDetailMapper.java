package com.smartresume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartresume.entity.ResumeDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResumeDetailMapper extends BaseMapper<ResumeDetail> {
    // 自定义查询方法（可选）
}