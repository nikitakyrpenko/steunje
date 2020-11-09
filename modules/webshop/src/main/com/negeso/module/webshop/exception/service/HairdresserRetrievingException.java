package com.negeso.module.webshop.exception.service;

public class HairdresserRetrievingException extends RuntimeException{

    public HairdresserRetrievingException() {
        super();
    }

    public HairdresserRetrievingException(String message) {
        super(message);
    }

    public HairdresserRetrievingException(String message, Throwable cause) {
        super(message, cause);
    }

    public HairdresserRetrievingException(Throwable cause) {
        super(cause);
    }

    protected HairdresserRetrievingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
