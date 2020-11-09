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

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

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
public class LinkedinPublisher implements SocialNetworkPublisher{
	
	private static Logger logger = Logger.getLogger(LinkedinPublisher.class);
	
	private static final String PUBLISH_URL = "http://api.linkedin.com/v1/people/~/shares";
	private static final String API_KEY = "apiKey";
	private static final String SECRET_KEY = "secretKey";
	private static final String OAUTH_USER_TOKEN = "oauthUserToken";
	private static final String OAUTH_USER_SECRET = "oauthUserSecret";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void publish(String title, String text, String url, String imageUrl,
			SocialNetwork socialNetwork) throws SocialNetworkException {
		
		try {
			if (StringUtils.isNotBlank(socialNetwork.getParams().get(API_KEY).getValue())
					&& StringUtils.isNotBlank(socialNetwork.getParams().get(SECRET_KEY).getValue())
					&& StringUtils.isNotBlank(socialNetwork.getParams().get(OAUTH_USER_TOKEN).getValue())
					&& StringUtils.isNotBlank(socialNetwork.getParams().get(OAUTH_USER_SECRET).getValue())) {
				OAuthService service = new ServiceBuilder()
						.provider(LinkedInApi.withScopes("rw_nus"))
						.apiKey(socialNetwork.getParams().get(API_KEY).getValue())
						.apiSecret(socialNetwork.getParams().get(SECRET_KEY).getValue())
					.build();
					
					
					Token accessToken = new Token(socialNetwork.getParams().get(OAUTH_USER_TOKEN).getValue(), socialNetwork.getParams().get(OAUTH_USER_SECRET).getValue());
					OAuthRequest request = new OAuthRequest(Verb.POST, PUBLISH_URL);
					request.addHeader("Content-Type", "application/json");
					request.addHeader("x-li-format", "json");
					
					HashMap jsonMap = new HashMap();
					JSONObject contentObject = new JSONObject();
					contentObject.put("title", title);
					text = text.replaceAll("\\<.*?>","");
					contentObject.put("description", text);
					contentObject.put("submitted-url", url);
					contentObject.put("submitted-image-url", imageUrl);
					
					jsonMap.put("content", contentObject);
					
					JSONObject visibilityObject = new JSONObject();
					visibilityObject.put("code", "anyone");
					
					jsonMap.put("visibility", visibilityObject);
					
					request.addPayload(JSONValue.toJSONString(jsonMap));
					
					service.signRequest(accessToken, request);
					Response response = request.send();
					logger.debug(response.getBody());
					logger.debug(response.getHeaders().toString());
					if (response.getCode() != 201) {
						throw new SocialNetworkException(String.format("%s %s", socialNetwork.getTitle(), ((JSONObject)JSONValue.parse(response.getBody())).get("message").toString()));
					}
					
			} else {
				throw new SocialNetworkException(String.format("%s %s", socialNetwork.getTitle(), "Some of requested params is empty"));
			}
		} catch (Exception e) {
			throw new SocialNetworkException(e);
		}
	}

}

