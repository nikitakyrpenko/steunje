/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.form_manager.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.negeso.framework.dao.Entity;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class FormArchive implements Entity{
	
	private static final long serialVersionUID = 6567757777901714724L;	

	private Long id = null;
	private Long formId = null;
	private Timestamp sentDate = null;
	private List<FormArchiveValue> fields = new ArrayList<FormArchiveValue>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}	

	public Timestamp getSentDate() {
		return sentDate;
	}

	public void setSentDate(Timestamp sentDate) {
		this.sentDate = sentDate;
	}

	public List<FormArchiveValue> getFields() {
		return fields;
	}

	public void setFields(List<FormArchiveValue> fields) {
		this.fields = fields;
	}

}
