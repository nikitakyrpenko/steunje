package com.negeso.module.webshop.exception;

public class RetrieveException extends RuntimeException{

    public RetrieveException(String message) {
        super(message);
    }

    public RetrieveException(String message, Throwable cause) {
        super(message, cause);
    }
}
