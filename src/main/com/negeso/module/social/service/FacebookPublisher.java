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
package com.negeso.module.social.service;

import org.apache.commons.lang.StringUtils;

import com.negeso.module.social.SocialNetworkException;
import com.negeso.module.social.bo.SocialNetwork;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FacebookPublisher implements SocialNetworkPublisher{
	
	private static final String ACCESS_TOKEN = "accessToken";

	@Override
	public void publish(String title, String text, String url, String imageUrl, SocialNetwork socialNetwork) throws SocialNetworkException {
		try {
			if (StringUtils.isNotBlank(socialNetwork.getParams().get(ACCESS_TOKEN).getValue())) {
				FacebookClient facebookClient = new DefaultFacebookClient(socialNetwork.getParams().get(ACCESS_TOKEN).getValue());
				facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", text));
			} else {
				throw new SocialNetworkException("Some of requested params is empty");
			}
		} catch (Exception e) {
			throw new SocialNetworkException(e);
		}
	}

}

