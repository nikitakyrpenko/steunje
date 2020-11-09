/*
 * @(#)$Id: Respondent.java,v 1.1, 2006-06-01 13:46:55Z, Anatoliy Pererva$
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
 * 
 *
 * @version     $Revision: 2$
 * @author      Stanislav Demchenko
 */
public class Respondent {
    
    private int id;
    private int questionnaireId;
    private Integer userId;
    private String email; // result of lookup in table inq_user
    private String ip;
    private Timestamp submitTime;
    
    public int getId() { return id; }
    
    public void setId(int id) { this.id = id; }
    
    public int getQuestionnaireId() { return questionnaireId; }
    
    public void setQuestionnaireId(int parentId) { this.questionnaireId = parentId; }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }	
    
    public String getIpAddress() { return ip; }
    
    public void setIpAddress(String ip) { this.ip = ip; }
    
    public Timestamp getSubmitTime() { return submitTime; }
    
    public void setSubmitTime(Timestamp submitTime) { this.submitTime = submitTime; }
    
    public String toString() { return ToStringBuilder.reflectionToString(this); }
    
}
