package com.fuzhi.fuzhisever.Exception;

import cn.dev33.satoken.exception.NotLoginException;

import com.fuzhi.fuzhisever.DTO.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        ApiResponse<?> response = ApiResponse.error(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        ApiResponse<?> response = ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {



        return new ResponseEntity<>(
                ApiResponse.error(ErrorCode.INVALID_PARAMETER),
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {


        return new ResponseEntity<>(
                ApiResponse.error(ErrorCode.INVALID_PARAMETER),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {


        return new ResponseEntity<>(
                ApiResponse.error(ErrorCode.INVALID_PARAMETER),
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<ApiResponse<?>> handleNotLoginException(NotLoginException e) {
        return new ResponseEntity<>(
                ApiResponse.error(ErrorCode.USER_NOT_LOGIN),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<?>> handleIOException(IOException ex) {


        return new ResponseEntity<>(
                ApiResponse.error(ErrorCode.FILE_UPLOAD_FAILED),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {


        return new ResponseEntity<>(
                ApiResponse.error(ErrorCode.PERMISSION_DENIED),
                HttpStatus.FORBIDDEN
        );
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {


        return new ResponseEntity<>(
                ApiResponse.error(ErrorCode.FILE_SIZE_EXCEEDED),
                HttpStatus.BAD_REQUEST
        );
    }

}
