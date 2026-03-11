package com.smartresume.common;

/**
 * 响应状态码枚举
 */
public enum ResultCode {
    
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "请先登录"),
    FORBIDDEN(403, "没有权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    
    // 业务错误
    USERNAME_EXISTS(1001, "用户名已存在"),
    EMAIL_EXISTS(1002, "邮箱已被注册"),
    PHONE_EXISTS(1003, "手机号已被注册"),
    USER_NOT_FOUND(1004, "用户不存在"),
    PASSWORD_ERROR(1005, "密码错误"),
    ACCOUNT_DISABLED(1006, "账号已被禁用"),
    
    // 文件错误
    FILE_EMPTY(2001, "文件不能为空"),
    FILE_TOO_LARGE(2002, "文件大小超过限制"),
    FILE_TYPE_ERROR(2003, "文件类型不支持"),
    FILE_UPLOAD_FAILED(2004, "文件上传失败"),
    FILE_PARSE_FAILED(2005, "文件解析失败"),
    
    // 服务器错误
    SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 通过code获取枚举
     */
    public static ResultCode fromCode(Integer code) {
        for (ResultCode resultCode : ResultCode.values()) {
            if (resultCode.getCode().equals(code)) {
                return resultCode;
            }
        }
        return null;
    }
}