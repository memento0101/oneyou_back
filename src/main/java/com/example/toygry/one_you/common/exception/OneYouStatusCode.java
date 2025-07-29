package com.example.toygry.one_you.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OneYouStatusCode {
    UserNotFound(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    LectureNotFound(HttpStatus.NOT_FOUND, "강의를 찾을 수 없습니다."),
    ReviewNotFount(HttpStatus.NOT_FOUND, "수강평을 찾을 수 없습니다."),
    UserForbidden(HttpStatus.FORBIDDEN, "본인만 수정/삭제 가능합니다."),
    BadRequest(HttpStatus.BAD_REQUEST, "누락 값 혹은 로그인 상태를 확인 해 주세요."),
    LectureForbidden(HttpStatus.FORBIDDEN, "수강 가능한 강의가 입니다");

    private final HttpStatus httpStatus;
    private final String message;

    OneYouStatusCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
