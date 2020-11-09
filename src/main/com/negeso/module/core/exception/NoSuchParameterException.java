package com.negeso.module.core.exception;


public class NoSuchParameterException extends RuntimeException{
	
	/**
     * Constructs a NoSuchParameterException without any detail message.
     */
    public NoSuchParameterException() {
        super();
    }

    /**
     * Constructs a NoSuchParameterException with the specified
     * detail message. A detail message is a String that describes
     * this particular exception.
     *
     * @param msg     the detail message.
     */
    public NoSuchParameterException(String param) {
    	super("Parameter " + param + " cannot be found in database. Please add");
    }


}
