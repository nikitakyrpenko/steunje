package com.negeso.module.webshop.exception.service;

public class ProductRetrievingException extends RuntimeException{

    public ProductRetrievingException() {super();}

    public ProductRetrievingException(String message) {super(message);}

    public ProductRetrievingException(Throwable cause) {super(cause);}

    public ProductRetrievingException(String message, Throwable cause) {super(message, cause);}

    protected ProductRetrievingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);}
}
