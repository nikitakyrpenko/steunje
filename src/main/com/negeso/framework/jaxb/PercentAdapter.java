package com.negeso.framework.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class PercentAdapter extends XmlAdapter<String, BigDecimal> {

	private NumberFormat format = NumberFormat.getPercentInstance(Locale.GERMANY);

	public BigDecimal unmarshal(String percent) throws Exception {
		return null;
	}

	public String marshal(BigDecimal percent) throws Exception {
		return percent == null ? null : format.format(percent);
	}
}