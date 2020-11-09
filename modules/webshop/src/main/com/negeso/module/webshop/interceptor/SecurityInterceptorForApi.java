package com.negeso.module.webshop.interceptor;

import com.google.gson.Gson;
import com.negeso.framework.HttpException;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.util.MD5Encryption;
import com.negeso.framework.util.json.JsonResponse;
import com.negeso.framework.util.json.JsonSupportForController;
import com.negeso.framework.util.spring.interceptor.Unsecured;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityInterceptorForApi extends HandlerInterceptorAdapter {

	private static final String BASIC_PREFIX = "Basic ";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		assignHeaders(response);

		if (handler instanceof HandlerMethod && ((HandlerMethod) handler).getMethodAnnotation(Unsecured.class) != null)
			return true;

		User user;
		if (request.getParameter("logout") != null) {
			this.invalidateSession(request);
			JsonSupportForController.write(response, new Gson().toJson(new JsonResponse()));
			return false;
		} else {
			user = (User) request.getSession().getAttribute(SessionData.USER_ATTR_NAME);
			if (user != null) {
				return true;
			}
		}

		String[] userDetails = this.getLoginPasswordPair(request);
		if (userDetails == null && request.getParameter("fld_login") != null)
			userDetails = new String[]{request.getParameter("fld_login"), request.getParameter("fld_password")};

		if (userDetails != null) {
			user = this.checkCredentials(userDetails);
			request.getSession().setAttribute(SessionData.USER_ATTR_NAME, user);
			if (user == null)
				throw new HttpException(400, "Onjuiste inloggegevens");
		} else {
			throw new HttpException(401, "Unauthenticated");
		}
		return true;
	}

	private void assignHeaders(HttpServletResponse response) {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

	private String[] getLoginPasswordPair(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		if (authorization == null)
			return null;
		if (authorization.startsWith(BASIC_PREFIX))
			authorization = authorization.substring(BASIC_PREFIX.length());
		final byte[] decodedBytes = Base64.decodeBase64(authorization.getBytes());
		final String pair = new String(decodedBytes);
		return pair.split(":", 2);
	}

	private void invalidateSession(HttpServletRequest request) throws IOException {
		request.getSession().invalidate();
	}

	private User checkCredentials(String[] loginPasswordPair) {
		String md5Password = MD5Encryption.md5(loginPasswordPair[1]);
		try {
			User user = User.findByLogin(loginPasswordPair[0]);
			if (user.getPassword().equals(md5Password))
				return user;
		} catch (ObjectNotFoundException e) {
			return null;
		}

		return null;
	}
}