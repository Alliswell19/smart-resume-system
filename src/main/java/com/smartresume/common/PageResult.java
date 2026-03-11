package com.smartresume.common;

import lombok.Data;
import java.util.List;

/**
 * 分页结果类
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页条数
     */
    private Integer pageSize;
    
    /**
     * 总条数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Integer pages;
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    public PageResult() {
    }

    public PageResult(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        this.pages = (int) Math.ceil((double) total / pageSize);
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < pages;
    }

    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        return new PageResult<>(pageNum, pageSize, total, list);
    }

    /**
     * 转换为Result格式
     */
    public Result<PageResult<T>> toResult() {
        return Result.success(this);
    }
}