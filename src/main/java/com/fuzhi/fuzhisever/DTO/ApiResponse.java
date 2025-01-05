package com.fuzhi.fuzhisever.DTO;

import com.fuzhi.fuzhisever.Exception.ErrorCode;
import lombok.Data;

import org.springframework.http.HttpStatus;


import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.ResponseEntity;


@Data
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private int code;
    private String msg;
    private T data;
    private PageInfo pageInfo;

    //成功响应
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .msg("Success")
                .data(data)
                .build();
    }

    // 带自定义消息的成功响应
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .msg(message)
                .data(data)
                .build();
    }

    // 带分页信息的成功响应
    public static <T> ApiResponse<T> success(T data, PageInfo pageInfo) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .msg("Success")
                .data(data)
                .pageInfo(pageInfo)
                .build();
    }

    // ：错误响应
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .msg(message)
                .data(null)
                .build();
    }

    // 基于 HTTP 状态码的错误响应
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .code(status.value())
                .msg(message)
                .data(null)
                .build();
    }

    // 基于错误码枚举的错误响应
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .code(errorCode.getCode())
                .msg(errorCode.getMessage())
                .data(null)
                .build();
    }
    public static ResponseEntity<ApiResponse<?>> buildErrorResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponse.error(errorCode));
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