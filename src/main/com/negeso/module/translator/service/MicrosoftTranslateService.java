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
package com.negeso.module.translator.service;


import com.negeso.framework.Env;
import com.negeso.module.translator.exception.TranslationExeption;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.hibernate.TransactionException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Serhii Matevytskyi
 * @version $Revision: $
 * @TODO
 */


public class MicrosoftTranslateService extends AbstractTranslateService {
	public static final String FROM = "from=";
	public static final String TO = "&to=";
	private static final Logger LOGGER = Logger.getLogger(MicrosoftTranslateService.class);
	private static final String CONTENT_TYPE = "Content-type";
	private static final String APPLICATION_JSON = "application/json";
	private static final String OCP_APIM_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";
	private static final String STM_MS_CLIENT_TO_SECRET_KEYS = "stmMsSecretToClientKeys";
	private static final String END_POINT = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&";
	private static final String TRANSLATIONS = "translations";
	private static final String TEXT = "text";
	private static final String ERROR_GETTING_TRANSLATED_TEXT_FROM_JSON = "Error getting translated text from Json: ";
	private static final String UNABLE_CREATE_JSON_FROM_STRING = "Unable create JSON from string ";
	private static final String UNABLE_TO_CREATE_HTTP_ENTITY = "Unable to create HTTP entity ";
	private static final String ERROR_WHEN_TRANSLATING_S_TO_S = "Error when translating %s to %s: ";
	private static final String UTF_8 = "UTF-8";
	private static final String PROPERTY_S_IS_NOT_CONFIGURED_PROPERLY = "Property %s is not configured properly";
	private static final String TEXT_CAP = "Text";
	private static final String UNABLE_TO_GET_TRANSLATED_TEXT_FROM_JSON = "Unable to get translated text from Json";
	private static final String OCP_APIM_SUBSCRIPTION_REGION = "Ocp-Apim-Subscription-Region";
	private static final String OCP_APIM_SUBSCRIPTION_REGION_VALUE = "westeurope";

	public MicrosoftTranslateService() {
		this.textLengthLimitation = 5000;
		this.isWithoutHtmlTags = true; //always without html tags

	}

	@Override
	public String translateTextPart(String fromLangCode, String toLangCode, String textPart) {
		String url = createUrl(fromLangCode, toLangCode);
		JSONArray jsonFromTextPart = createJson(textPart);
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = createEntityFromJson(jsonFromTextPart);
		setHeaders(httpPost, entity);

		try {

			return postTextToMicrosoftService(client, httpPost);

		} catch (IOException e) {

			LOGGER.error(String.format(ERROR_WHEN_TRANSLATING_S_TO_S, fromLangCode, toLangCode), e);
			throw new TranslationExeption(e);
		}

	}

	private void setHeaders(HttpPost httpPost, StringEntity entity) {
		httpPost.setEntity(entity);
		httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
		httpPost.setHeader(OCP_APIM_SUBSCRIPTION_KEY, getSecretKey());
		httpPost.setHeader(OCP_APIM_SUBSCRIPTION_REGION, OCP_APIM_SUBSCRIPTION_REGION_VALUE);
	}

	private String getSecretKey() {
		String key = Env.getProperty(STM_MS_CLIENT_TO_SECRET_KEYS, "");
		if (StringUtils.isBlank(key)) {
			throw new TransactionException(String.format(PROPERTY_S_IS_NOT_CONFIGURED_PROPERLY, STM_MS_CLIENT_TO_SECRET_KEYS));
		}
		return key;
	}

	private StringEntity createEntityFromJson(JSONArray jsonFromTextPart) {
		StringEntity entity;
		try {
			entity = new StringEntity(jsonFromTextPart.toString(), UTF_8);
			entity.setContentType(APPLICATION_JSON);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(UNABLE_TO_CREATE_HTTP_ENTITY, e);
			throw new TranslationExeption(e);
		}
		return entity;
	}

	private String postTextToMicrosoftService(HttpClient httpClient, HttpPost httpPost) throws IOException {

		HttpResponse execute = httpClient.execute(httpPost);
		BasicResponseHandler handler = new BasicResponseHandler();
		String json = handler.handleResponse(execute);

		return getTranslatedTextFromJson(json);
	}

	private String getTranslatedTextFromJson(String json) {
		try {
			JSONArray jsonArray = new JSONArray(json);
			JSONObject obj = (JSONObject) jsonArray.get(0);
			jsonArray = new JSONArray(obj.getString(TRANSLATIONS));
			obj = (JSONObject) jsonArray.get(0);
			String text = obj.getString(TEXT);
			return text;

		} catch (JSONException e) {
			LOGGER.error(ERROR_GETTING_TRANSLATED_TEXT_FROM_JSON, e);
			throw new TranslationExeption(UNABLE_TO_GET_TRANSLATED_TEXT_FROM_JSON, e);
		}
	}


	private JSONArray createJson(String textPart) {
		JSONObject jsonObject = new JSONObject();
		JSONArray json = new JSONArray();
		try {
			jsonObject.put(TEXT_CAP, textPart);
			json.put(jsonObject);
		} catch (JSONException e) {
			LOGGER.error(UNABLE_CREATE_JSON_FROM_STRING + textPart, e);
			throw new TranslationExeption(e);
		}
		return json;
	}

	private String createUrl(String fromLangCode, String toLangCode) {
		String fromLanguage = FROM + fromLangCode;
		String toLanguage = TO + toLangCode;
		return END_POINT + fromLanguage + toLanguage;
	}

	@Override
	public com.negeso.framework.domain.Language[] getSupportedLaguages() {
		com.negeso.framework.domain.Language[] supportedLaguages = new com.negeso.framework.domain.Language[com.memetix.mst.language.Language.values().length];
		for (int i = 0; i < supportedLaguages.length; i++) {
			supportedLaguages[i] = new com.negeso.framework.domain.Language(null, com.memetix.mst.language.Language.values()[i].name(), com.memetix.mst.language.Language.values()[i].toString());
		}
		return supportedLaguages;
	}


	@Override
	public void setWithoutHtmlTags(boolean isWithoutHtmlTags) {
		// don't set, because will be bad layout
	}
}

