package com.negeso.module.webshop.interceptor;

import com.google.gson.Gson;
import com.negeso.framework.HttpException;
import com.negeso.framework.util.json.JsonResponse;
import com.negeso.framework.util.json.JsonSupportForController;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiExceptionResolver implements HandlerExceptionResolver {
	private final static Logger logger = Logger.getLogger(ApiExceptionResolver.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception ex) {
		this.logException(httpServletRequest, ex);

		if (handler instanceof JsonSupportForController){
			JsonSupportForController controller = (JsonSupportForController) handler;
			try {
				JsonResponse resp = new JsonResponse(ex);
				controller.writeErrorToResponse(httpServletResponse, new Gson().toJson(resp), resp.getStatusCode());
			} catch (IOException e) {
				logger.fatal(e);
			}
		}
		JsonResponse resp = new JsonResponse(ex);
		try {
			JsonSupportForController.writeError(httpServletResponse, new Gson().toJson(resp), resp.getStatusCode());
		} catch (IOException e) {
			logger.error("e", e);
		}

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.clear();
		return modelAndView;
	}

	private void logException(HttpServletRequest httpServletRequest, Exception ex) {
		String cause = "Can't execute request. ";
		if (httpServletRequest != null && httpServletRequest.getRequestURI() != null){
			cause = cause + httpServletRequest.getRequestURI() + ": " + ex.getMessage();
		}
		if (ex instanceof HttpException){
			logger.error(cause);
		}else {
			logger.error(cause, ex);
		}
	}
}
