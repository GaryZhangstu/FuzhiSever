package com.fuzhi.fuzhisever.DTO;

import com.fuzhi.fuzhisever.Exception.ErrorCode;
import lombok.Data;

import org.springframework.http.HttpStatus;


import lombok.AllArgsConstructor;
import lombok.Builder;


@Data
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private PageInfo pageInfo;

    // 静态工厂方法：成功响应
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .build();
    }

    // 静态工厂方法：带自定义消息的成功响应
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    // 静态工厂方法：带分页信息的成功响应
    public static <T> ApiResponse<T> success(T data, PageInfo pageInfo) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .pageInfo(pageInfo)
                .build();
    }

    // 静态工厂方法：错误响应
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    // 静态工厂方法：基于 HTTP 状态码的错误响应
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .code(status.value())
                .message(message)
                .data(null)
                .build();
    }

    // 静态工厂方法：基于错误码枚举的错误响应
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(null)
                .build();
    }

    // 分页信息类
    @Data
    @AllArgsConstructor
    @Builder
    public static class PageInfo {
        private int page;
        private int size;
        private long total;
    }
}