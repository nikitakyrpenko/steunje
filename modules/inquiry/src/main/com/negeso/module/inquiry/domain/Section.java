package com.negeso.module.inquiry.domain;

public class Section {
	int id;
	String name;
	int position;
	Long questionnaireId;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}

	public Long getQuestionnaireId() {
		return questionnaireId;
	}
	

	public void setQuestionnaireId(Long questionnaireId) {
		this.questionnaireId = questionnaireId;
	}
	
	
	
}
