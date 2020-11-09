package com.negeso.module.baten.web.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.util.MD5Encryption;
import com.negeso.module.baten.entity.ArticleInfo;
import com.negeso.module.baten.web.ResponseJsonWrapper;
import com.restfb.json.JsonObject;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJsonController extends AbstractController {


	private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	protected ModelAndView invalidateSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().invalidate();
		this.writeToResponse(response, HttpStatus.SC_OK, new ArrayList<ArticleInfo>(0));
		return null;
	}

	protected User checkCredentials(HttpServletRequest request) {
		if (request == null)
			return null;
		String login, password, md5Password;
		try {
			login = ServletRequestUtils.getRequiredStringParameter(request, "fld_login");
			password = ServletRequestUtils.getRequiredStringParameter(request, "fld_password");
			md5Password = MD5Encryption.md5(password);
		} catch (ServletRequestBindingException e) {
			return null;
		}
		try {
			User user = User.findByLogin(login);
			if (user.getPassword().equals(md5Password))
				return user;
		} catch (ObjectNotFoundException e) {
			return null;
		}

		return null;
	}

	protected void writeToResponse(HttpServletResponse response, int statusCode, List<ArticleInfo> obj) throws IOException {
		response.setStatus(statusCode);
		boolean jsonStatusSuccess = statusCode>=200&&statusCode<300;

		ResponseJsonWrapper<ArticleInfo> res = new ResponseJsonWrapper<ArticleInfo>(obj)
				.addStatus(jsonStatusSuccess?2:1)
				.addMessage(jsonStatusSuccess?"Producten.":"Helaas, de opgegeven inloggegevens kloppen niet!");

		this.writeToResponse(response, this.jsonStructureLikeThr(res).toString());
	}

	protected void writeToResponse(HttpServletResponse response, ResponseJsonWrapper<ArticleInfo> body) throws IOException {
		response.setStatus(body.getStatus()*100);

		this.writeToResponse(response, this.jsonStructureLikeThr(body).toString());
	}

	protected void writeToResponse(HttpServletResponse response, String message) throws IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(message);
		out.close();
	}

	private JsonObject jsonStructureLikeThr(ResponseJsonWrapper<ArticleInfo> simple){
		JsonObject complicated = new JsonObject();
		int i = 0;
		for (ArticleInfo product : simple.getBody()) {
			if (product.isAvailable()) {
				JsonObject productJsonObject = new JsonObject(product);
				complicated.put(Integer.toString(i++), productJsonObject);
			}
		}

		complicated.put("success", simple.isSuccess());
		complicated.put("status", simple.getStatus());
		complicated.put("message", simple.getMessage());
		complicated.put("total", simple.getBody().size());

		return complicated;
	}
}
