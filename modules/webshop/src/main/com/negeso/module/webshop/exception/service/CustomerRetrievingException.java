package com.negeso.module.webshop.exception.service;

public class CustomerRetrievingException extends RuntimeException{

    public CustomerRetrievingException() {
        super();
    }

    public CustomerRetrievingException(String message) {
        super(message);
    }

    public CustomerRetrievingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerRetrievingException(Throwable cause) {
        super(cause);
    }

    protected CustomerRetrievingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
