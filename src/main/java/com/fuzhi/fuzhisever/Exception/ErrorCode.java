package com.fuzhi.fuzhisever.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {


    // 通用错误
    UNKNOWN_ERROR(50001, "未知错误"),
    INVALID_PARAMETER(40001, "参数无效"),
    DATA_NOT_FOUND(40401, "数据不存在"),
    DATA_ALREADY_EXISTS(40901, "数据已存在"),

    // 用户模块
    USER_NOT_FOUND(40402, "用户不存在"),
    USER_ALREADY_EXISTS(40902, "用户已存在"),
    WRONG_PASSWORD(40101, "密码错误"),
    USER_DISABLED(40301, "用户已被禁用"),
    USER_NOT_LOGIN(40102, "用户未登录"),

    // 文件模块
    FILE_EMPTY(40003, "上传的文件不能为空"),
    FILE_UPLOAD_FAILED(50002, "文件上传失败"),
    FILE_NOT_FOUND(40403, "文件不存在"),
    FILE_SIZE_EXCEEDED(40004, "文件大小超出限制"),
    FILE_READ_ERROR(2005, "文件读取失败"),
    FILE_WRITE_ERROR(2006, "文件写入失败"),

    // 皮肤分析模块
    SKIN_ANALYSIS_FAILED(50003, "皮肤分析失败"),
    SKIN_ANALYSIS_NOT_FOUND(40404, "皮肤分析结果不存在"),
    SKIN_ANALYSIS_INVALID(40005, "皮肤分析数据无效"),

    // 权限模块
    PERMISSION_DENIED(40302, "权限不足"),
    TOKEN_INVALID(40103, "Token无效"),
    TOKEN_EXPIRED(40104, "Token已过期"),

    // 系统错误
    INTERNAL_SERVER_ERROR(50004, "服务器内部错误"),
    DATABASE_ERROR(50005, "数据库错误"),
    NETWORK_ERROR(50006, "网络错误");



    private final int code;
    private final String message;

    /**
     * 获取 HTTP 状态码
     */
    public int getHttpStatus() {
        return code / 100;
    }

    public static ErrorCode fromCode(int code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("未知的错误码: " + code);
    }

}
