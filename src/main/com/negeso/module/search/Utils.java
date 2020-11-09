package com.negeso.module.search;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

public class Utils {

	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

	static String fixNull(String value) {
		return value == null ? StringUtils.EMPTY : value;
	}

	static String getDate(long millis) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(millis);
		return formatter.format(date.getTime());
	}
}
