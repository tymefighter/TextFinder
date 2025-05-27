package com.textFinder.exceptions;

public class FailedSearchException extends RuntimeException {

    public FailedSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}

