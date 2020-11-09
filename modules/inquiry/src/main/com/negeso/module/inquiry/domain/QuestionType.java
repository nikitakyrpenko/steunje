/*
 * @(#)$Id: QuestionType.java,v 1.4, 2006-06-01 13:46:55Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.inquiry.domain;

/** Types of question enum (text field, radio, or multiple selection) */
public enum QuestionType {
    
    TEXT, TEXTAREA, RADIO, DROPDOWN, CHECKBOX, DEPARTMENT, SCORE; //DEPARTMENT, SCORE - ONLY FOR BAROMETER!!!!!
    
    public final static QuestionType getTypeByName(String name) {
        for (QuestionType qtype : QuestionType.values()) {
            if(qtype.getName().equals(name)) return qtype;
        }
        throw new IllegalArgumentException("Unknown question type: " + name);
    }

    public String getName() { return toString().toLowerCase(); }

}
