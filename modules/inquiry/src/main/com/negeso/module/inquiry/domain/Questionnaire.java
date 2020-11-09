/*
 * @(#)$Id: Questionnaire.java,v 1.4, 2006-06-01 13:46:55Z, Anatoliy Pererva$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.inquiry.domain;

import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * @version     $Revision: 5$
 * @author      Stanislav Demchenko
 */
public class Questionnaire {
    
    private int id;
    private int langId;
    private String title;
    private Timestamp publish;
    private Timestamp expired;
	private int introId;
	private int conclusionId;
	private boolean isPublic = true;
	private boolean isMultipage = false;
	private int rfHeight = 22;
	private int rfWidth = 100;
	private boolean rfMultiline = false;
    private boolean showAnswers = true; 
	private boolean barometer = false;
	
    public boolean isBarometer() {
		return barometer;
	}
	
	public void setBarometer(boolean barometer) {
		this.barometer = barometer;
	}
	
	public int getRfHeight() { return rfHeight; }
	public void setRfHeight(int rfHeight) { this.rfHeight = rfHeight; }
	
	public boolean isRfMultiline() { return rfMultiline; }
	public void setRfMultiline(boolean rfMultiline) { this.rfMultiline = rfMultiline; }
	
	public int getRfWidth() { return rfWidth; }
	public void setRfWidth(int rfWidth) { this.rfWidth = rfWidth; }
	
	public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public int getLangId() { return langId; }
    public void setLangId(int langId) { this.langId = langId; }

    public Timestamp getPublish() { return publish; }
    public void setPublish(Timestamp publish) { this.publish = publish; }

    public Timestamp getExpired() { return expired; }
    public void setExpired(Timestamp expired) { this.expired = expired; }

	public int getConclusionId() { return conclusionId; }
	public void setConclusionId(int conclusionId) { this.conclusionId = conclusionId; }

	public int getIntroId() { return introId; }
	public void setIntroId(int introId) { this.introId = introId; }

	public boolean isMultipage() { return isMultipage; }
	public void setMultipage(boolean isMultipage) { this.isMultipage = isMultipage; }

	public boolean isPublic() { return isPublic; }
	public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public boolean isShowAnswers() { return showAnswers; }
    public void setShowAnswers(boolean showAnswers) {  this.showAnswers = showAnswers; }
    
    public String toString() { return  ToStringBuilder.reflectionToString(this); }
    
}
