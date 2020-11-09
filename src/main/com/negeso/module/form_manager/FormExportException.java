package com.negeso.module.form_manager;

public class FormExportException extends Exception{
	public FormExportException(){
	}

	public FormExportException(String message, Throwable cause){
		super(message, cause);
	}

	public FormExportException(Throwable cause){
		super(cause);
	}
}