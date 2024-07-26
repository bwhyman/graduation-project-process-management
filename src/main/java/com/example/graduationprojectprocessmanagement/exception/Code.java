package com.example.graduationprojectprocessmanagement.exception;

public enum Code {
    LOGIN_ERROR(400, "用户名密码错误"),
    BAD_REQUEST(400, "请求错误"),
    UNAUTHORIZED(401, "未登录"),
    TOKEN_EXPIRED(403, "过期请重新登录"),
    FORBIDDEN(403, "无权限"),
    NOT_START(201, "未开始"),
    QUANTITY_FULL(210, "导师数量已满，请重新选择"),
    REPEAT_SELECTION(211, "导师不可重复选择");
    public static int ERROR = 400;

    private final int code;
    private final String message;

    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
