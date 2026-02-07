package com.smartresume.mapper;

import com.smartresume.entity.Resume;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResumeMapper {

    /**
     * 插入简历记录（自动生成 ID）
     */
    int insert(Resume resume);

    /**
     * 根据 ID 查询简历
     */
    Resume selectById(Long id);

    /**
     * 根据用户 ID 查询该用户的所有简历
     */
    List<Resume> selectByUserId(Long userId);

    /**
     * 更新简历（主要用于保存优化后的内容）
     */
    int update(Resume resume);

    /**
     * 删除简历（可选）
     */
    int deleteById(Long id);
}