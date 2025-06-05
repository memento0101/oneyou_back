package com.example.toygry.one_you.common.exception;

import com.example.toygry.one_you.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBaseException(BaseException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getStatusCode().name(),
                ex.getStatusCode().getMessage()
        );

        return ResponseEntity
                .status(ex.getStatusCode().getHttpStatus())
                .body(ApiResponse.fail(error));
    }

    // 예외 응답 객체
    public record ErrorResponse(String code, String message) {}
}
