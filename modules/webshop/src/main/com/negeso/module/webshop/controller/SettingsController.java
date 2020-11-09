package com.negeso.module.webshop.controller;

import com.google.gson.Gson;
import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.service.ParameterService;
import com.negeso.framework.util.json.JsonSupportForController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class SettingsController extends JsonSupportForController {

	private final ParameterService parameterService;

	@Autowired
	public SettingsController(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	@RequestMapping(value = "/webshop/api/settings", method = RequestMethod.GET)
	public void read(HttpServletResponse res) throws IOException {
		List<ConfigurationParameter> parameters = parameterService.findParametersByModule(53L);

		super.writeToResponse(res, new Gson().toJson(parameters));
	}

	@RequestMapping(value = "/webshop/api/settings", method = RequestMethod.POST)
	public void update(HttpServletRequest req, HttpServletResponse res) throws IOException {
		ConfigurationParameter[] configurationParameters = super.buildPojoFromRequest(req, ConfigurationParameter[].class);
		for (ConfigurationParameter param : configurationParameters) {
			ConfigurationParameter fromSession = parameterService.findParameterByName(param.getName());
			fromSession.setValue(param.getValue());
			parameterService.update(fromSession, true);
		}

		super.writeToResponseExcBody(res, 200);
	}
}
