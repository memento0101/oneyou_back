package com.example.toygry.one_you.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OneYouStatusCode {
    // 일반 에러
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "해당 정보를 찾을 수 없습니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN,"access denied"),
    
    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "본인만 수정/삭제 가능합니다."),
    
    // 강의 관련 에러
    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "강의를 찾을 수 없습니다."),
    LECTURE_FORBIDDEN(HttpStatus.FORBIDDEN, "수강 가능한 강의가 아닙니다."),
    
    // 수강후기 관련 에러
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 강의에 대한 후기를 작성하셨습니다."),
    REVIEW_NOT_ELIGIBLE(HttpStatus.FORBIDDEN, "강의를 50% 이상 수강한 후 후기를 작성할 수 있습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "후기를 찾을 수 없습니다."),
    
    // 공지사항 관련 에러
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "공지사항을 찾을 수 없습니다."),

    // 수강 피드백 관련 에러
    SUBMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "학생 피드백을 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String message;

    OneYouStatusCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
