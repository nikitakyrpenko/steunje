package com.negeso.framework.jaxb;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DecimalFormatAdapter extends XmlAdapter<String, BigDecimal>{

	@Override
	public String marshal(BigDecimal value) throws Exception {
		return value == null ? null : new DecimalFormat("###.##").format(value);
	}

	@Override
	public BigDecimal unmarshal(String v) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
