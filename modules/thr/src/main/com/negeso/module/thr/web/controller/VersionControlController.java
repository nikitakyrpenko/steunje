package com.negeso.module.thr.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.service.ParameterService;

public class VersionControlController extends MultiActionController {

	public static final String THR_VERSION_NUMBER = "thrVersionNumber";
	public static final String THR_VERSION_NUMBER_IOS = "thrVersionNumber_iOS";
	public static final String PRODUCT_INFO_URL_PARAMETER = "url_to_get_product_info";
	public static final String PUSH_MARKETING_URL_PARAMETER = "url_to_get_push_marketing";
	public static final String SAVE_ORDER_URL_PARAMETER = "url_to_save_order";
	public static final String GENERATE_PDF_URL_PARAMETER = "url_to_generate_pdf";
	public static final String HELP_URL_PARAMETER = "url_to_help";
	public static final String ADD_TO_CART_URL_PARAMETER = "url_to_add_to_cart";

	private ParameterService parameterService;

	public ModelAndView browse(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ConfigurationParameter thrVersionNumber = parameterService.findParameterByName(THR_VERSION_NUMBER);
		ConfigurationParameter productInfoUrl = parameterService.findParameterByName(PRODUCT_INFO_URL_PARAMETER);
		ConfigurationParameter pushMarketing = parameterService.findParameterByName(PUSH_MARKETING_URL_PARAMETER);
		ConfigurationParameter saveOrder = parameterService.findParameterByName(SAVE_ORDER_URL_PARAMETER);
		ConfigurationParameter generatePdf = parameterService.findParameterByName(GENERATE_PDF_URL_PARAMETER);
		ConfigurationParameter help = parameterService.findParameterByName(HELP_URL_PARAMETER);
		ConfigurationParameter addToCart = parameterService.findParameterByName(ADD_TO_CART_URL_PARAMETER);

		return new PreparedModelAndView("version_control")
		.addToModel(THR_VERSION_NUMBER, thrVersionNumber.getValue())		
		.addToModel(THR_VERSION_NUMBER_IOS, parameterService.findParameterByName(THR_VERSION_NUMBER_IOS).getValue())		
		.addToModel(PRODUCT_INFO_URL_PARAMETER, productInfoUrl.getValue())
		.addToModel(PUSH_MARKETING_URL_PARAMETER, pushMarketing.getValue())
		.addToModel(SAVE_ORDER_URL_PARAMETER, saveOrder.getValue())
		.addToModel(GENERATE_PDF_URL_PARAMETER, generatePdf.getValue())
		.addToModel(HELP_URL_PARAMETER, help.getValue())
		.addToModel(ADD_TO_CART_URL_PARAMETER, addToCart.getValue())
		.get();
	}

	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.saveValue(request, THR_VERSION_NUMBER);
		this.saveValue(request, THR_VERSION_NUMBER_IOS);
		this.saveValue(request, PRODUCT_INFO_URL_PARAMETER);
		this.saveValue(request, PUSH_MARKETING_URL_PARAMETER);
		this.saveValue(request, SAVE_ORDER_URL_PARAMETER);
		this.saveValue(request, GENERATE_PDF_URL_PARAMETER);
		this.saveValue(request, HELP_URL_PARAMETER);
		this.saveValue(request, ADD_TO_CART_URL_PARAMETER);

		return browse(request, response);
	}

	private void saveValue(HttpServletRequest request, String linkToGetValue){
		ConfigurationParameter productInfoUrl = parameterService.findParameterByName(linkToGetValue);
		productInfoUrl.setValue(request.getParameter(linkToGetValue));
		parameterService.save(productInfoUrl, false);
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

}
