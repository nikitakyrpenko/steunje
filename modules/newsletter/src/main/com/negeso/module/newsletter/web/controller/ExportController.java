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
package com.negeso.module.newsletter.web.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import com.negeso.module.newsletter.bo.SubscriberAttributeValue;
import com.negeso.module.newsletter.service.SubscriberService;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class ExportController extends AbstractController{
	
	private SubscriberService subscriberService = null;
	
	public static final char DELIMETER = ';';
	public static final String lineSeparator = System.getProperty ( "line.separator" );
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		StringBuffer sb = prepareData();
		makeAttachment(response, sb);
		return null;
	}

	private void makeAttachment(HttpServletResponse response, StringBuffer sb)
			throws IOException, FileNotFoundException {
		
		response.setBufferSize(sb.length());
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=\"subscriber_export.csv\"");
		response.setContentLength(sb.length());
		
		Writer writer = response.getWriter();
		writer.write(sb.toString());
		writer.close();
	}

	private StringBuffer prepareData() {
		List<SubscriberAttributeType> attributesTypes = subscriberService.listAttributeTypes();
		List<Subscriber> subscribers = subscriberService.listAllSubscribers();
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (SubscriberAttributeType attributeType : attributesTypes) {
			sb.append(attributeType.getKey());
			if (++i < attributesTypes.size()) {
				sb.append(DELIMETER);
			}
		}
		sb.append(lineSeparator).append(lineSeparator);
		int j = 0;
		for (Subscriber subscriber : subscribers) {
			i = 0;
			for (SubscriberAttributeType attributeType : attributesTypes) {
				for (SubscriberAttributeValue value : subscriber.getAttributes()) {
					if (value.getSubscriberAttributeType().getId().equals(attributeType.getId())) {
						sb.append(value.getValue());
						break;
					}
				}
				if (++i < attributesTypes.size()) {
					sb.append(DELIMETER);
				}
			}
			if (++j < subscribers.size()) {
				sb.append(lineSeparator);
			}
		}
		return sb;
	}

	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

}

