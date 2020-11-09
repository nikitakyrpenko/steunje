package com.negeso.module.webshop.exception;

public class JsonToDtoParsingException extends RuntimeException{

    public JsonToDtoParsingException(String message) {
        super(message);
    }

    public JsonToDtoParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
