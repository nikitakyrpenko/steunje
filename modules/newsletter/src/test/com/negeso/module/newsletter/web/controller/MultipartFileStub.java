package com.negeso.module.newsletter.web.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class MultipartFileStub implements MultipartFile {

	private String fileData="";
	private String fileName;
	
	public MultipartFileStub() {}
	
	public MultipartFileStub(String fileData) {
		super();
		this.fileData = fileData;
	}
	
	

	public MultipartFileStub(String fileData, String fileName) {
		super();
		this.fileData = fileData;
		this.fileName = fileName;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return fileData.getBytes();
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(fileData.getBytes());
		return bais;
	}

	@Override
	public String getName() {
		return fileName;
	}

	@Override
	public String getOriginalFilename() {
		return fileName;
	}

	@Override
	public long getSize() {
		return fileData.getBytes().length;
	}

	@Override
	public boolean isEmpty() {
		if (fileData == null) return true;
		return (fileData.getBytes().length==0);
	}

	@Override
	public void transferTo(File arg0) throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
		
	}

}
