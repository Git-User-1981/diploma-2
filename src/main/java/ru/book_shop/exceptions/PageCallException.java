package ru.book_shop.exceptions;

import lombok.Getter;

@Getter
public class PageCallException extends RuntimeException {
    private final Exception originalError;

    public PageCallException(String errorMessage) {
        this(errorMessage, null);
    }

    public PageCallException(String errorMessage, Exception e) {
        super(errorMessage);
        originalError = e;
    }

}
