package com.negeso.framework.jaxb;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.negeso.framework.Env;

public class RoundDateAdapter extends XmlAdapter<String, Date> {

	public Date unmarshal(String date) throws Exception {
		return null;
	}

	public String marshal(Date date) throws Exception {
		return date == null ? null : Env.formatRoundDate(date);
	}

}
