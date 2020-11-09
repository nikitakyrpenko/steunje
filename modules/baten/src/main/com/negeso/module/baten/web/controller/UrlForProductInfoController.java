package com.negeso.module.baten.web.controller;

import com.google.gson.Gson;
import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.service.ParameterService;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class UrlForProductInfoController extends AbstractJsonController{

	private static final String PRODUCT_INFO_URL_PARAMETER = "url_to_get_product_info";
	private static final String PUSH_MARKETING_URL_PARAMETER = "url_to_get_push_marketing";
	private static final String SAVE_ORDER_URL_PARAMETER = "url_to_save_order";
	private static final String GENERATE_PDF_URL_PARAMETER = "url_to_generate_pdf";
	private static final String HELP_URL_PARAMETER = "url_to_help";
	private static final String ADD_TO_CART_URL_PARAMETER = "url_to_add_to_cart";

	private ParameterService parameterService;

	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> urls = new HashMap<String, String>();
		this.apiForMobileDevs(urls, PRODUCT_INFO_URL_PARAMETER);
		this.apiForMobileDevs(urls, PUSH_MARKETING_URL_PARAMETER);
		this.apiForMobileDevs(urls, SAVE_ORDER_URL_PARAMETER);
		this.apiForMobileDevs(urls, GENERATE_PDF_URL_PARAMETER);
		this.apiForMobileDevs(urls, HELP_URL_PARAMETER);
		this.apiForMobileDevs(urls, ADD_TO_CART_URL_PARAMETER);

		super.writeToResponse(response, new Gson().toJson(urls));
		return null;
	}

	private void apiForMobileDevs(Map<String, String> urls, String findByProperty){
		ConfigurationParameter urlParameter = parameterService.findParameterByName(findByProperty);
		urls.put(findByProperty, urlParameter.getValue());
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}
}
