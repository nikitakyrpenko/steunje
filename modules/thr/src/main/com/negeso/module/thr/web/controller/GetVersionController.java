package com.negeso.module.thr.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.service.ParameterService;
import com.restfb.json.JsonObject;

public class GetVersionController extends AbstractController {
	
	protected ParameterService parameterService;
	protected String versionParamName = VersionControlController.THR_VERSION_NUMBER;
	
	public GetVersionController() {		
	}
	
	public GetVersionController(String versionParamName) {
		this.versionParamName =  versionParamName;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ConfigurationParameter thrVersionNumber = parameterService.findParameterByName(versionParamName);
		JsonObject obj = new JsonObject();
		obj.put(VersionControlController.THR_VERSION_NUMBER, thrVersionNumber.getValue());
		writeToResponse(response, obj.toString());
		return null;
	}
	
	private void writeToResponse(HttpServletResponse response, String meassage) throws IOException {
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print(meassage);
        out.close();
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}
}
