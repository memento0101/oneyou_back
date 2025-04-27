package com.example.toygry.one_you.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final OneYouStatusCode statusCode;
    private final String message;

    public BaseException(OneYouStatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
        this.message = statusCode.getMessage();
    }

    public BaseException(OneYouStatusCode statusCode, String message) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
        this.message = message;
    }


}
