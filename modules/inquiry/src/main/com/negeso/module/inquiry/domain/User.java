package com.negeso.module.inquiry.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class User implements Serializable{
	
	private static final long serialVersionUID = -4047514612265294950L;
	
	private int id;
	private int quizId;
	private String email;
	private String password;
	
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getQuestionnaireId() { return quizId; }
    public void setQuestionnaireId(int quizId) { this.quizId = quizId; }
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	
	public String toString() { return ToStringBuilder.reflectionToString(this); }

}
