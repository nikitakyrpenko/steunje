package com.negeso.module.inquiry;

public class EmailNotUniqueException extends RuntimeException {
	
	private final String email;
	
	public EmailNotUniqueException(String email){
		super("Duplicate email: " + email);
		this.email = email;
	}
	
	public String getEmail() { return email; }
	
}
