/*
 * @(#)$Id: MailSender.java,v 1.0, 2007-01-23 10:23:42Z, Svetlana Bondar$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.lite_event;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.mailer.SMPTChecker;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public class MailSender {

	private static Logger logger = Logger.getLogger(MailSender.class);
	
	private String mailto;
	private String subject; 
	private Map bodyAttributes;
	private String emailBodyTemplate;

	private String getEmailBody() {
		logger.debug("+-");
		Element el = Xbuilder.createTopEl("body");
		
		for (Object key : bodyAttributes.keySet()) {
			Xbuilder.setAttr(el, (String)key, bodyAttributes.get(key));
		  }
		
    	return XmlHelper.transformToString(
    		el.getOwnerDocument(), 
    		Env.getSite().getXslTemplates(emailBodyTemplate), 
    		"text"
    	);
	}
	
	public void sendMail() throws Exception {
		logger.debug("+");
		
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(Env.getValidSmtpHost());
		sender.setDefaultEncoding("UTF-8");

		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom(Env.getDefaultEmail());
		mail.setTo(mailto);
		mail.setSubject(subject);
		mail.setText(getEmailBody());

		sender.send(mail);
		logger.info("Mail is sent: " + mailto);
		logger.debug("-"); 
	}

	public void setBodyAttributes(Map bodyAttributes) {
		this.bodyAttributes = bodyAttributes;
	}

	public void setMailto(String mailto) {
		this.mailto = mailto;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setEmailBodyTemplate(String emailBodyTemplate) {
		this.emailBodyTemplate = emailBodyTemplate;
	}
	
}

