/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.mailer;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;

import com.negeso.framework.Env;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SMPTChecker {
	
	public boolean checkSmtpAvailability(){
    	return checkSmtpAvailability(Env.getSmtpHost());
    }
	
	public boolean checkSmtpAvailability(String smtpHost){
    	boolean isSuccess = false;
    	Transport tr = null;
    	try {
    		Properties props = System.getProperties();
    		props.put("mail.smtp.host", smtpHost);
    		Session session = Session.getDefaultInstance(props, null);
    		tr = session.getTransport("smtp");
    		tr.connect();
    		isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				tr.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return isSuccess;
    }
}

