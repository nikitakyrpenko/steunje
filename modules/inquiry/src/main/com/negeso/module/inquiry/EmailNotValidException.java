package com.negeso.module.inquiry;

public class EmailNotValidException extends RuntimeException {
	
	
	private final String email;

	public EmailNotValidException(String email) {
		super("Invalid email: " + email);
		this.email = email;
	}
	
	public String getEmail() { return email; }

}
