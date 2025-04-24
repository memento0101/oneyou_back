package com.example.toygry.one_you.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        return ResponseEntity
                .status(ex.getStatusCode().getHttpStatus())
                .body(new ErrorResponse(
                        ex.getStatusCode().name(),
                        ex.getStatusCode().getMessage()
                ));
    }

    // 예외 응답 객체
    record ErrorResponse(String code, String message) {}

}
