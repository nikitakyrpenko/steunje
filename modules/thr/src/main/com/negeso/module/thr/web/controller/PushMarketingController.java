package com.negeso.module.thr.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.domain.Article;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.service.ParameterService;
import com.negeso.module.thr.bo.ThrProduct;
import com.negeso.module.thr.service.ThrProductService;

public class PushMarketingController extends MultiActionController {

	public static final String THR_IMAGE_HEIGHT = "thrImageHeight";

	public static final String THR_IMAGE_WIDTH = "thrImageWidth";

	public static final String THR_PUSH_MARKETING_ENABLED = "thrPushMarketingEnabled";

	public static final Long ARTICLE_ID = 19L;

	private ThrProductService thrProductService;
	private ParameterService parameterService;

	public ModelAndView browse(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new PreparedModelAndView("push_marketing").addToModel("article", Article.findById(ARTICLE_ID))
				.addToModel("products", thrProductService.list())
				.addToModel(THR_IMAGE_WIDTH, parameterService.findParameterByName(THR_IMAGE_WIDTH).getValue())
				.addToModel(THR_IMAGE_HEIGHT, parameterService.findParameterByName(THR_IMAGE_HEIGHT).getValue())
				.addToModel(THR_PUSH_MARKETING_ENABLED, parameterService.findParameterByName(THR_PUSH_MARKETING_ENABLED)).get();
	}

	public ModelAndView setEnabled(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ConfigurationParameter thrPushMarketingEnabled = parameterService.findParameterByName(THR_PUSH_MARKETING_ENABLED);
		thrPushMarketingEnabled.setValue(request.getParameter("value"));
		parameterService.save(thrPushMarketingEnabled, false);
		return null;
	}

	public ModelAndView addOrEditProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = NegesoRequestUtils.getId(request, 0L);
		ThrProduct product = null;
		if (id > 0) {
			product = thrProductService.findById(id);
		} else {
			product = new ThrProduct();
		}
		return new PreparedModelAndView("edit_product_dialog").addToModel("product", product)
				.addToModel(THR_IMAGE_WIDTH, parameterService.findParameterByName(THR_IMAGE_WIDTH).getValue())
				.addToModel(THR_IMAGE_HEIGHT, parameterService.findParameterByName(THR_IMAGE_HEIGHT).getValue()).get();
	}

	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = NegesoRequestUtils.getId(request, 0L);
		ThrProduct product = null;
		if (id > 0) {
			product = thrProductService.findById(id);
		} else {
			product = new ThrProduct();
			product.setOrderNumber(thrProductService.getNextOrderNumber());
		}
		NegesoRequestUtils.bind(product, request);
		thrProductService.createOrUpdate(product);
		return null;
	}
	
	public ModelAndView move(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = NegesoRequestUtils.getId(request, 0L);
		ThrProduct product = null;
		if (id > 0) {
			product = thrProductService.findById(id);
			if (product != null) {
				if (Boolean.valueOf(request.getParameter("direction"))) {
					thrProductService.moveUp(product);
					return null;
				} else {
					thrProductService.moveDown(product);
					return null;
				}
			}
		}
		writeToResponse(response, "Product not found by id " + id);
		return null;
	}
	
	private void writeToResponse(HttpServletResponse response, String meassage) throws IOException {
		response.setContentType("text/html");
		response.setStatus(401);
		PrintWriter out = response.getWriter();
		out.print(meassage);
		out.close();
	}
	
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = NegesoRequestUtils.getId(request, 0L);
		if (id > 0) {
			ThrProduct product = thrProductService.findById(id);
			if (product != null) {
				thrProductService.delete(product);
			}
		}
		return null;
	}

	public void setThrProductService(ThrProductService thrProductService) {
		this.thrProductService = thrProductService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

}
