package com.negeso.module.thr.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.domain.Article;
import com.negeso.module.core.service.ParameterService;
import com.negeso.module.thr.bo.ThrProduct;
import com.negeso.module.thr.service.ThrProductService;
import com.restfb.json.JsonObject;

public class GetPushMarketingController extends AbstractController {

	private static final String THR_TEST_LOGINS = "thrTestLogins";
	private ThrProductService thrProductService;
	private ParameterService parameterService;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JsonObject obj = new JsonObject();
		obj.put("enabled", parameterService.findParameterByName(PushMarketingController.THR_PUSH_MARKETING_ENABLED).getValue());
		obj.put("text", Article.findById(PushMarketingController.ARTICLE_ID).getText());
		String login = request.getParameter("login");
		boolean isTestLogin = isTestLogin(login);
		int i = 0;
		for (ThrProduct product : thrProductService.list()) {
			if (product.isVisible() || (isTestLogin && product.isTest())) {
				JsonObject productJsonObject = new JsonObject(product);
				obj.put(Integer.toString(i++), productJsonObject);
			}
		}
		writeToResponse(response, obj.toString());
		return null;
	}

	private boolean isTestLogin(String login) {
		if (StringUtils.isNotBlank(login)) {
			String testLogins = parameterService.findParameterByName(THR_TEST_LOGINS).getValue();
			for (String testLogin : testLogins.split(",|;| ")) {
				if (login.equals(testLogin)) {
					return true;
				}
			}
		}
		return false;
	}

	private void writeToResponse(HttpServletResponse response, String meassage) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(meassage);
		out.close();
	}

	public void setThrProductService(ThrProductService thrProductService) {
		this.thrProductService = thrProductService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

}
