package com.fuzhi.fuzhiServer.Exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.fuzhi.fuzhiServer.DTO.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;


@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        log.error("BusinessException occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = ApiResponse.error(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getHttpStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        log.error("General Exception occurred: {}", ex.getMessage(), ex);
        return ApiResponse.buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException occurred: {}", ex.getMessage(), ex);
        return ApiResponse.buildErrorResponse(ErrorCode.INVALID_PARAMETER);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("MethodArgumentTypeMismatchException occurred: {}", ex.getMessage(), ex);
        return ApiResponse.buildErrorResponse(ErrorCode.INVALID_PARAMETER);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException occurred: {}", ex.getMessage(), ex);
        return ApiResponse.buildErrorResponse(ErrorCode.INVALID_PARAMETER);
    }

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<ApiResponse<String>> handleNotLoginException(NotLoginException e) {
        log.error("NotLoginException occurred: {}", e.getMessage(), e);
        return ApiResponse.buildErrorResponse(ErrorCode.USER_NOT_LOGIN);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<String>> handleIOException(IOException ex) {
        log.error("IOException occurred: {}", ex.getMessage(), ex);
        return ApiResponse.buildErrorResponse(ErrorCode.FILE_READ_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("AccessDeniedException occurred: {}", ex.getMessage(), ex);
        return ApiResponse.buildErrorResponse(ErrorCode.FILE_READ_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error("MaxUploadSizeExceededException occurred: {}", ex.getMessage(), ex);
        return ApiResponse.buildErrorResponse(ErrorCode.FILE_SIZE_EXCEEDED);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("MissingServletRequestParameterException occurred: {}", ex.getMessage(), ex);
        return ApiResponse.buildErrorResponse(ErrorCode.INVALID_PARAMETER);
    }
}
