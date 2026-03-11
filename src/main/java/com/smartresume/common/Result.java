package com.smartresume.common;

import lombok.Data;

/**
 * 统一响应结果类
 * @param <T> 数据类型
 */
@Data
public class Result<T> {
    
    /**
     * 状态码：
     * 200 - 成功
     * 400 - 参数错误
     * 401 - 未授权/未登录
     * 403 - 禁止访问
     * 404 - 资源不存在
     * 500 - 服务器内部错误
     */
    private Integer code;
    
    /**
     * 提示信息
     */
    private String message;
    
    /**
     * 返回数据
     */
    private T data;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 时间戳
     */
    private Long timestamp;

    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 成功响应 ====================

    /**
     * 成功（无数据）
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setSuccess(true);
        return result;
    }

    /**
     * 成功（有数据）
     * @param data 返回数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    /**
     * 成功（自定义消息）
     * @param message 成功消息
     * @param data 返回数据
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    // ==================== 失败响应 ====================

    /**
     * 失败（默认错误码400）
     * @param message 错误信息
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(400);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }

    /**
     * 失败（自定义错误码）
     * @param code 错误码
     * @param message 错误信息
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }

    /**
     * 参数错误（400）
     */
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }

    /**
     * 未授权（401）
     */
    public static <T> Result<T> unauthorized(String message) {
        return error(401, message == null ? "请先登录" : message);
    }

    /**
     * 禁止访问（403）
     */
    public static <T> Result<T> forbidden(String message) {
        return error(403, message == null ? "没有权限" : message);
    }

    /**
     * 资源不存在（404）
     */
    public static <T> Result<T> notFound(String message) {
        return error(404, message == null ? "资源不存在" : message);
    }

    /**
     * 服务器错误（500）
     */
    public static <T> Result<T> serverError(String message) {
        return error(500, message == null ? "服务器内部错误" : message);
    }

    // ==================== 便捷判断 ====================

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.success != null && this.success;
    }

    /**
     * 判断是否失败
     */
    public boolean isError() {
        return !isSuccess();
    }
}