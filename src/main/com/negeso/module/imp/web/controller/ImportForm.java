/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.imp.web.controller;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class ImportForm {

    private MultipartFile importFile;
    private String importerId;

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

}
