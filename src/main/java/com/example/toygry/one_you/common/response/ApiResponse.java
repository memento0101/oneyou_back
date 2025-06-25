package com.example.toygry.one_you.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    private final boolean success;
    private final String code;
    private final String message;
    private final T data;


    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, HttpStatus.OK.name(), "success", data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, HttpStatus.OK.name(), message, null);
    }

    public static <T> ApiResponse<T> fail(HttpStatus status, String message) {
        return new ApiResponse<>(false, status.name(), message, null);

    }

    public static <T> ApiResponse<T> fail(T data, HttpStatus status) {
        return new ApiResponse<>(false, status.name(), "fail", data);

    }
}
