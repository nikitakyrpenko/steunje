package com.negeso.module.webshop.exception.service;

public class UserRetrievingException extends RuntimeException {

    public UserRetrievingException() {
        super();
    }

    public UserRetrievingException(String message) {
        super(message);
    }

    public UserRetrievingException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserRetrievingException(Throwable cause) {
        super(cause);
    }

    protected UserRetrievingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
