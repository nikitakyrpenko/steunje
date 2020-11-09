package com.negeso.framework.util.json;

import com.google.gson.annotations.Expose;
import com.negeso.framework.HttpException;

public class JsonResponse {
	@Expose
	private boolean success = true;
	@Expose
	private String message;
	@Expose
	private int statusCode;
	@Expose
	private int status;

	public JsonResponse() {
		this.statusCode = 200;
	}

	public JsonResponse(Exception ex) {
		this.success = false;
		this.status = 1;
		if (ex instanceof HttpException){
			HttpException httpEx = (HttpException) ex;
			this.statusCode = httpEx.getStatusCode();
			this.message = this.statusCode == 401 ? "Helaas, de opgegeven inloggegevens kloppen niet!" : ex.getMessage();
		} else {
			this.message = ex.getMessage();
			this.statusCode = 400;
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String reason) {
		this.message = reason;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
