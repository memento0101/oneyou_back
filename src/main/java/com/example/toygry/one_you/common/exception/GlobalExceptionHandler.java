package com.example.toygry.one_you.common.exception;

import com.example.toygry.one_you.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException ex) {

        return ResponseEntity
                .status(ex.getStatusCode().getHttpStatus())
                .body(ApiResponse.fail(ex.getStatusCode().getHttpStatus(), ex.getMessage()));
    }
}
