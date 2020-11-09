/*
 * @(#)$Id: Option.java,v 1.3, 2006-06-01 13:46:54Z, Anatoliy Pererva$
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
 * 
 *
 * @version     $Revision: 4$
 * @author      Stanislav Demchenko
 */
public class Option {
    
    private int id;
    private int parentId;
    private String title;
    private int position;

    public int getId() { return id; }
    
    public void setId(int id) { this.id = id; }
    
    public void setTitle(String title) { this.title = title; }
    
    public String getTitle() { return title; }
    
    public void setParentId(int parentId) { this.parentId = parentId; }
    
    public int getParentId() { return parentId; }

    public int getPosition() { return position; }
    
    public void setPosition(int position) { this.position = position; }
    
    public String toString() { return ToStringBuilder.reflectionToString(this); }
    
}
