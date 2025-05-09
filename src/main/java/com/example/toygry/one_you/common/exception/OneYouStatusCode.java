package com.example.toygry.one_you.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OneYouStatusCode {
    UserNotFound(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    LectureNotFound(HttpStatus.NOT_FOUND, "강의를 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String message;

    OneYouStatusCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
