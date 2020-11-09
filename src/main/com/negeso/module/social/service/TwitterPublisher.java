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

import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.negeso.module.social.SocialNetworkException;
import com.negeso.module.social.bo.SocialNetwork;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class TwitterPublisher implements SocialNetworkPublisher {
	
	private static final String CUSTOMER_KEY = "consumerKey",
								CUSTOMER_SECRET = "consumerSecret",
								ACCESS_TOKEN = "accessToken",
								ACCESS_TOKEN_SECRET = "accessTokenSecret";

	@Override
	public void publish(String title, String text, String url, String imageUrl, SocialNetwork socialNetwork) throws SocialNetworkException {
		try {
			if (StringUtils.isNotBlank(socialNetwork.getParams().get(CUSTOMER_KEY).getValue())
					&& StringUtils.isNotBlank(socialNetwork.getParams().get(CUSTOMER_SECRET).getValue())
					&& StringUtils.isNotBlank(socialNetwork.getParams().get(ACCESS_TOKEN).getValue())
					&& StringUtils.isNotBlank(socialNetwork.getParams().get(ACCESS_TOKEN_SECRET).getValue())) {				
				
				ConfigurationBuilder cb = new ConfigurationBuilder()
				    .setOAuthConsumerKey(socialNetwork.getParams().get(CUSTOMER_KEY).getValue())
				    .setOAuthConsumerSecret(socialNetwork.getParams().get(CUSTOMER_SECRET).getValue())
				    .setOAuthAccessToken(socialNetwork.getParams().get(ACCESS_TOKEN).getValue())
				    .setOAuthAccessTokenSecret(socialNetwork.getParams().get(ACCESS_TOKEN_SECRET).getValue());
				TwitterFactory tf = new TwitterFactory(cb.build());
				twitter4j.Twitter twitter = tf.getInstance();
				twitter.updateStatus(text);
			} else {
				throw new SocialNetworkException("Some of requested params is empty");
			}
		} catch (Exception e) {
			throw new SocialNetworkException(e);
		}
	}

}

