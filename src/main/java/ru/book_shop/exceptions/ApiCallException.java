package ru.book_shop.exceptions;

import lombok.Getter;

@Getter
public class ApiCallException extends RuntimeException {
    private final Exception originalError;
    private final int statusCode;

    public ApiCallException(String errorMessage) {
        this(errorMessage, null, 400);
    }

    public ApiCallException(String errorMessage, Exception e) {
        this(errorMessage, e, 400);
    }

    public ApiCallException(String errorMessage, Exception e, int statusCode) {
        super(errorMessage);
        this.originalError = e;
        this.statusCode = statusCode;
    }

}
