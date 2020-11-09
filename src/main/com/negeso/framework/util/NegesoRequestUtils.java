/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.util;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.sun.beans.editors.BooleanEditor;
import com.sun.beans.editors.LongEditor;
import com.sun.beans.editors.StringEditor;
import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;

import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;

/**
 *
 * @TODO
 *
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public class NegesoRequestUtils {

	/**
	 * Negeso module id
	 */
	public static final String INPUT_MODULE_ID = "moduleId";
	/**
	 Context entity id
	 */
	public static final String INPUT_ID = "id";
	/**
	 * Action parameter for multiaction controller
	 */
	public static final String INPUT_ACTION = "act";

	public static final String LANG_CODE = "langCode";

	private static Logger logger = Logger.getLogger(NegesoRequestUtils.class);

	public static Long getModuleId(HttpServletRequest request, Long defaultValue) {
		try {
			return ServletRequestUtils.getLongParameter(request, INPUT_MODULE_ID);
		} catch (ServletRequestBindingException e) {
			logger.error("Exception - ", e);
			return defaultValue;
		}
	}

	public static Long getId(HttpServletRequest request, Long defaultValue) {
		return getId(INPUT_ID, request, defaultValue);
	}

	public static Long getId(String paramName, HttpServletRequest request, Long defaultValue) {
		try {
			return ServletRequestUtils.getLongParameter(request, paramName);
		} catch (ServletRequestBindingException e) {
			logger.error("Exception -", e);
			return defaultValue;
		}
	}

	public static Date getTimeStampFromRequest(HttpServletRequest request, String parameterName) throws ParseException {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		if (request.getParameter(parameterName) != null  && !request.getParameter(parameterName).equals("")) {
			return df.parse(request.getParameter(parameterName));
		} else {
			return null;
		}
	}

	public static Language getInterfaceLanguage(HttpServletRequest request) {
		try {
			Long langId = ServletRequestUtils.getLongParameter(request, "lang_id");
			if (langId != null)
				return Language.findById(langId);
		} catch (ServletRequestBindingException e) {
			logger.error("Exception -", e);
		} catch (CriticalException e) {
			logger.error("Exception -", e);
		} catch (ObjectNotFoundException e) {
			logger.error("Exception -", e);
		}
		String langCode = SessionData.getInterfaceLanguageCode(request);
		return Language.getByCode(langCode);
	}

	public static Object bindToRequest(Object bean, HttpServletRequest request){
		new ServletRequestDataBinder(bean).bind(request);
		return bean;
	}

	public static <T>T  bind(T bean, HttpServletRequest request){
		ServletRequestDataBinder binder = new ServletRequestDataBinder(bean);
		binder.registerCustomEditor(Date.class,new CustomDateEditor(new SimpleDateFormat(Env.SIMPLE_DATE_FORMAT), true)  );
		binder.bind(request);
		return bean;
	}

	public static <T>T  bind(T bean, HttpServletRequest request, String dateFormat){
		ServletRequestDataBinder binder = new ServletRequestDataBinder(bean);
		binder.registerCustomEditor(Date.class,new CustomDateEditor(new SimpleDateFormat(dateFormat), true)  );
		binder.registerCustomEditor(Timestamp.class,new CustomTimestampEditor(new SimpleDateFormat(dateFormat), true)  );
		binder.bind(request);
		return bean;
	}

	public static void parseParameters(Map map, String data, String encoding) throws UnsupportedEncodingException {
		if(data != null && data.length() > 0) {
			byte[] bytes = null;

			try {
				if(encoding == null) {
					bytes = data.getBytes();
				} else {
					bytes = data.getBytes(encoding);
				}
			} catch (UnsupportedEncodingException var5) {
				;
			}

			parseParameters(map, bytes, encoding);
		}

	}

	public static void parseParameters(Map map, byte[] data, String encoding) throws UnsupportedEncodingException {
		if(data != null && data.length > 0) {
			boolean pos = false;
			int ix = 0;
			int ox = 0;
			String key = null;
			String value = null;

			while(ix < data.length) {
				byte c = data[ix++];
				switch((char)c) {
					case '%':
						data[ox++] = (byte)((convertHexDigit(data[ix++]) << 4) + convertHexDigit(data[ix++]));
						break;
					case '&':
						value = new String(data, 0, ox, encoding);
						if(key != null) {
							putMapEntry(map, key, value);
							key = null;
						}

						ox = 0;
						break;
					case '+':
						data[ox++] = 32;
						break;
					case '=':
						if(key == null) {
							key = new String(data, 0, ox, encoding);
							ox = 0;
						} else {
							data[ox++] = c;
						}
						break;
					default:
						data[ox++] = c;
				}
			}

			if(key != null) {
				value = new String(data, 0, ox, encoding);
				putMapEntry(map, key, value);
			}
		}

	}

	private static byte convertHexDigit(byte b) {
		return b >= 48 && b <= 57?(byte)(b - 48):(b >= 97 && b <= 102?(byte)(b - 97 + 10):(b >= 65 && b <= 70?(byte)(b - 65 + 10):0));
	}

	private static void putMapEntry(Map map, String name, String value) {
		String[] newValues = null;
		String[] oldValues = (String[])map.get(name);
		if(oldValues == null) {
			newValues = new String[]{value};
		} else {
			newValues = new String[oldValues.length + 1];
			System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
			newValues[oldValues.length] = value;
		}

		map.put(name, newValues);
	}

}

