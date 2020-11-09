/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.translator.domain;

import com.negeso.framework.dao.Entity;

import java.sql.Timestamp;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class Log implements Entity {

	private static final long serialVersionUID = -284767921139993113L;
	
	private Long id;
	private String fromLangCode;
	private String toLangCode;
	private Timestamp date = new Timestamp(System.currentTimeMillis());
	private Long charsNumber = 0L;
	private String url;
	private String nameOfTranslator;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFromLangCode() {
		return fromLangCode;
	}

	public void setFromLangCode(String fromLangCode) {
		this.fromLangCode = fromLangCode;
	}

	public String getToLangCode() {
		return toLangCode;
	}

	public void setToLangCode(String toLangCode) {
		this.toLangCode = toLangCode;
	}

	public Long getCharsNumber() {
		return charsNumber;
	}

	public void setCharsNumber(Long charsNumber) {
		this.charsNumber = charsNumber;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNameOfTranslator() {
		return nameOfTranslator;
	}

	public void setNameOfTranslator(String nameOfTranslator) {
		this.nameOfTranslator = nameOfTranslator;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}
}
