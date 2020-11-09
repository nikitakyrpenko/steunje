/*
 * @(#)$Id: Question.java,v 1.5, 2006-06-01 13:46:54Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.inquiry.domain;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * @version     $Revision: 6$
 * @author      Stanislav Demchenko
 */
public class Question {
	
    private int id;
    private String title;
    private QuestionType type = QuestionType.RADIO;
    private boolean required = true;
    private boolean allowRemark;
    private int parentId;
    private int position;
    private String alternative;
	private String explanation;
	private String optionsLayout;
	private int aoHeight=22;
	private int aoWidth=100;
	private boolean aoMultiline=false;
	private Long sectionId = null;

    public Long getSectionId() {
		return sectionId;
	}
	
	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}
	
	public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public void setType(QuestionType type) { this.type = type; }
    public QuestionType getType() { return type; }
    
    public boolean getRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    
    public boolean getAllowRemark() { return allowRemark; }
    public void setAllowRemark(boolean allowRemark) { this.allowRemark = allowRemark; }
    
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }
    
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public String getAlternative() { return alternative; }
    public void setAlternative(String alternative) { this.alternative = alternative; }

	public String getExplanation() { return explanation; }
	public void setExplanation(String explanation) { this.explanation = explanation; }

	public String getOptionsLayout() { return optionsLayout; }
	public void setOptionsLayout(String layout) { this.optionsLayout = layout; }

	public int getAoHeight() { return aoHeight; }
	public void setAoHeight(int aoHeight) { this.aoHeight = aoHeight; }
	
	public boolean isAoMultiline() { return aoMultiline; }
	public void setAoMultiline(boolean aoMultiline) { this.aoMultiline = aoMultiline; }
	
	public int getAoWidth() { return aoWidth; }
	public void setAoWidth(int aoWidth) { this.aoWidth = aoWidth; }
	
    public String toString() { return ToStringBuilder.reflectionToString(this); }
    
}
