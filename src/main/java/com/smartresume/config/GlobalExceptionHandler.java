package com.smartresume.config;

import com.smartresume.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理非法参数异常（可安全暴露参数错误信息）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        return Result.error(500, "系统内部错误，请稍后重试");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.error(500, "服务器内部错误，请稍后重试");
    }

    /**
     * 兜底：处理所有未捕获异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("未捕获异常", e);
        return Result.error(500, "服务器内部错误，请稍后重试");
    }
}
