/*
 * @(#)Id: ImportSubscribersForm.java, 27.02.2008 14:52:14, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.bo;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class ImportSubscribersForm {
	
	private MultipartFile importFile;
	
	private String delimiter;
	
    private String importerId;

    private String singleEmail;
    
    private Long langId;

	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public void setImportFile(MultipartFile importFile) {
        this.importFile = importFile;
    }

    public MultipartFile getImportFile() {
        return importFile;
    }	
    
    @Override
    public String toString() {
    	return ReflectionToStringBuilder.toString(this);
    }

	public String getImporterId() {
		return importerId;
	}

	public void setImporterId(String importerId) {
		this.importerId = importerId;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

    public String getSingleEmail() {
        return singleEmail;
    }

    public void setSingleEmail(String singleEmail) {
        this.singleEmail = singleEmail;
    }
}
