package com.example.toygry.one_you.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final OneYouStatusCode statusCode;

    public BaseException(OneYouStatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
    }
}
