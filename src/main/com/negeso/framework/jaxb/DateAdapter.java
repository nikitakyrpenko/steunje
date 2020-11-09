/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.jaxb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.negeso.framework.Env;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class DateAdapter extends XmlAdapter<String, Date> {

    DateFormat df =  new SimpleDateFormat(Env.SIMPLE_DATE_FORMAT);

    public Date unmarshal(String date) throws Exception {
        return null;
    }

    public String marshal(Date date) throws Exception {
        return date == null ? null : df.format(date);
    }
}

