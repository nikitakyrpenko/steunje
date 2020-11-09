package com.negeso.module.webshop.util;

import javax.servlet.http.HttpServletRequest;

@Deprecated
public class PathVariable {
	public static String getStringParameter(HttpServletRequest req, String baseUri){
		String uri = req.getRequestURI();
		int startIndex = uri.lastIndexOf(baseUri);
		return startIndex == -1 ? null : uri.substring(uri.lastIndexOf('/') + 1);
	}
}
