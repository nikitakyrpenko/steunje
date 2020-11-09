package com.negeso.framework.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class GermanPriceAdapter extends XmlAdapter<String, BigDecimal> {

	private NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);

	public GermanPriceAdapter() {
		this.format = NumberFormat.getCurrencyInstance(Locale.GERMANY);
		DecimalFormat f = (DecimalFormat) format;
		f.setPositivePrefix("\u20ac ");
		f.setNegativePrefix("-\u20ac ");
		f.setPositiveSuffix("");
		f.setNegativePrefix("");
	}

	public BigDecimal unmarshal(String price) throws Exception {
		return null;
	}

	public String marshal(BigDecimal price) throws Exception {
		return price == null ? null : format.format(price);
	}
}
