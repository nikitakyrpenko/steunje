package com.negeso.module.thr.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CurrencyFormatter {
	private static final NumberFormat format = new DecimalFormat("#0.00");
	
	public static String format(Double value) {
		return String.format("&euro; %s", format.format(value)).replace(".", ",");
	}
}
