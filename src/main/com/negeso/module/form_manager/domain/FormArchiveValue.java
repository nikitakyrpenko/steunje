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


/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class FormArchiveValue {
	
	private Long id = null;
	private FormField formField = null;
	private FormArchive formArchive = null;
	private String value = null;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FormField getFormField() {
		return formField;
	}

	public void setFormField(FormField formField) {
		this.formField = formField;
	}

	public FormArchive getFormArchive() {
		return formArchive;
	}

	public void setFormArchive(FormArchive formArchive) {
		this.formArchive = formArchive;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
