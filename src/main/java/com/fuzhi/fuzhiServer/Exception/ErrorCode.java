package com.fuzhi.fuzhiServer.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {


    // 通用错误
    UNKNOWN_ERROR(1, "未知错误", 500),
    INVALID_PARAMETER(2, "参数无效", 400),
    DATA_NOT_FOUND(3, "数据不存在", 404),
    DATA_ALREADY_EXISTS(4, "数据已存在", 409),

    // 用户模块
    USER_NOT_FOUND(1001, "用户不存在", 404),
    USER_ALREADY_EXISTS(1002, "用户已存在", 409),
    WRONG_PASSWORD(1003, "密码错误", 401),
    USER_DISABLED(1004, "用户已被禁用", 403),
    USER_NOT_LOGIN(1005, "用户未登录", 401),
    USERNAME_OR_PASSWORD_ERROR(1006, "用户名或密码错误", 401),

    // 文件模块
    FILE_EMPTY(2001, "上传的文件不能为空", 400),
    FILE_UPLOAD_FAILED(2002, "文件上传失败", 500),
    FILE_NOT_FOUND(2003, "文件不存在", 404),
    FILE_SIZE_EXCEEDED(2004, "文件大小超出限制", 400),
    FILE_READ_ERROR(2005, "文件读取失败", 500),
    FILE_WRITE_ERROR(2006, "文件写入失败", 500),

    // 皮肤分析模块
    SKIN_ANALYSIS_FAILED(3001, "皮肤分析失败", 500),
    SKIN_ANALYSIS_NOT_FOUND(3002, "皮肤分析结果不存在", 404),
    SKIN_ANALYSIS_INVALID(3003, "皮肤分析数据无效", 400),

    // 权限模块
    PERMISSION_DENIED(4001, "权限不足", 403),
    TOKEN_INVALID(4002, "Token无效", 401),
    TOKEN_EXPIRED(4003, "Token已过期", 401),

    // 系统错误
    INTERNAL_SERVER_ERROR(9901, "服务器内部错误", 500),
    DATABASE_ERROR(9902, "数据库错误", 500),
    NETWORK_ERROR(9903, "网络错误", 500);


    private final int code;
    private final String message;
    private final int httpStatus;


    // 根据错误码获取枚举对象
    public static ErrorCode fromCode(int code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("未知的错误码: " + code);
    }

}
