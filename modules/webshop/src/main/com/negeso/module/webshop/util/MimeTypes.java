package com.negeso.module.webshop.util;

import java.util.Properties;

public class MimeTypes {

	private static Properties props = new Properties();

	public MimeTypes() {
		props.put("image/jpeg", "jpg");
		props.put("image/jpg", "jpg");
		props.put("image/png", "png");
		props.put("application/pdf", "pdf");
	}

	public static String extensionOf(String s){
		return (String) props.get(s);
	}
}
