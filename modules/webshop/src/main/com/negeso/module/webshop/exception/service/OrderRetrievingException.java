package com.negeso.module.webshop.exception.service;

public class OrderRetrievingException extends RuntimeException {

    public OrderRetrievingException () {super();}

    public OrderRetrievingException (String message) {super(message);}

    public OrderRetrievingException (Throwable cause) {super(cause);}

    public OrderRetrievingException (String message, Throwable cause) {super(message, cause);}

    public OrderRetrievingException (String message, Throwable cause, boolean enabledSupression, boolean writableStackTrace) {
        super(message, cause, enabledSupression, writableStackTrace);}
}
