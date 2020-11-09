package com.negeso.module.baten.web;

import java.util.List;

public class ResponseJsonWrapper<T> {

	private boolean success;
	private int status;
	private String message;

	private List<T> body;

	public ResponseJsonWrapper(List<T> body) {
		this.body = body;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ResponseJsonWrapper<T> addMessage(String message){
		this.message = message;
		return this;
	}

	public ResponseJsonWrapper<T> addStatus(int status){
		this.status = status;
		this.success = status == 2;
		return this;
	}

	public List<T> getBody() {
		return body;
	}
}
