package com.negeso.module.webshop.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.negeso.framework.util.http.UnsecuredHttpClient;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CashDeskHttpClient {
	public static final String API_BASE_URL = "https://api.kassacompleet.nl";
	public static final String RETURN_URL = "http://www.batentrading.nl/webshop/api/ideal/new_order";
	public static final String API_KEY = "8ffc2791682c4d0e8f402de37fa49361";

	private static Gson gson = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();

	public static HttpClient createClient() {
		return UnsecuredHttpClient.verifiedClient(new DefaultHttpClient());
	}

	public static HttpPost createPost(String uri) {
		String stringToEncode = API_KEY + ':';
		String authorization = Base64.encode(stringToEncode.getBytes());
		HttpPost preparedRequest = new HttpPost(API_BASE_URL + uri);
		preparedRequest.setHeader("Authorization", "Basic " + authorization);

		return preparedRequest;
	}

	public static HttpGet createGet(String uri) {
		String stringToEncode = API_KEY + ':';
		String authorization = Base64.encode(stringToEncode.getBytes());
		HttpGet preparedRequest = new HttpGet(API_BASE_URL + uri);
		preparedRequest.setHeader("Authorization", "Basic " + authorization);

		return preparedRequest;
	}

	public static <T> T createAndExecute(String uri, String method, Class<T> clazz, StringEntity postingString) {
		HttpClient client = createClient();

		String url = API_BASE_URL + uri;
		HttpRequestBase preparedRequest;

		if ("GET".equals(method)) {
			preparedRequest = new HttpGet(url);
		} else if ("POST".equals(method)) {
			preparedRequest = new HttpPost(url);
			if (postingString != null) {
				preparedRequest.setHeader("Content-type", "application/json");
				((HttpEntityEnclosingRequestBase) preparedRequest).setEntity(postingString);
			}
		} else {
			return null;
		}

		try {
			String stringToEncode = API_KEY + ':';
			String authorization = Base64.encode(stringToEncode.getBytes());
			preparedRequest.setHeader("Authorization", "Basic " + authorization);
			HttpResponse response = client.execute(preparedRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			return gson.fromJson(reader, clazz);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T createAndExecute(String uri, String method, Class<T> clazz) {
		return createAndExecute(uri, method, clazz, null);
	}


}
