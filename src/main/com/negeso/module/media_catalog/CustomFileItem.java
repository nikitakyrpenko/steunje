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
package com.negeso.module.media_catalog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */

public class CustomFileItem implements FileItem{


	private static final long serialVersionUID = 6443681825992027630L;
	
	private FileItem fileItem;
	private String name;
	
	public CustomFileItem(FileItem fileItem, String name) {
		super();
		this.fileItem = fileItem;
		this.name = name;
	}

	@Override
	public void delete() {
		fileItem.delete();
	}

	@Override
	public byte[] get() {
		return fileItem.get();
	}

	@Override
	public String getContentType() {
		return fileItem.getContentType();
	}

	@Override
	public String getFieldName() {
		return fileItem.getFieldName();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return fileItem.getInputStream();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return fileItem.getOutputStream();
	}

	@Override
	public long getSize() {
		return fileItem.getSize();
	}

	@Override
	public String getString() {
		return fileItem.toString();
	}

	@Override
	public String getString(String str) throws UnsupportedEncodingException {
		return fileItem.getString(str);
	}

	@Override
	public boolean isFormField() {
		return fileItem.isFormField();
	}

	@Override
	public boolean isInMemory() {
		return fileItem.isInMemory();
	}

	@Override
	public void setFieldName(String fieldName) {
		fileItem.setFieldName(fieldName);
	}

	@Override
	public void setFormField(boolean isformField) {
		fileItem.setFormField(isformField);
	}

	@Override
	public void write(File file) throws Exception {
		fileItem.write(file);
	}

	public void setName(String name) {
		this.name = name;
	}

//	@Override
	public FileItemHeaders getHeaders() {
		return null;
	}

//	@Override
	public void setHeaders(FileItemHeaders fileItemHeaders) {

	}
}

