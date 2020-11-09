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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class TimestampAdapter extends XmlAdapter<String, Timestamp> {

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public Timestamp unmarshal(String date) throws Exception {
        return null;
    }

    public String marshal(Timestamp date) throws Exception {
        return date == null ? null : df.format(date);
    }
}

