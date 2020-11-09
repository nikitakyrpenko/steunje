package com.negeso.framework;

public class HttpException extends Exception {

	private int statusCode;

	public HttpException(int code, String message) {
		super(message);
		this.statusCode = code;
	}

	public HttpException(int code, String message, Throwable cause) {
		super(message, cause);
		this.statusCode = code;
	}

	public int getStatusCode(){
		return this.statusCode;
	}
}