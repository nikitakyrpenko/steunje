package com.negeso.module.baten.web.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.negeso.module.baten.entity.Api;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

public class GetAllApiController extends AbstractJsonController{

	private final String JSON = "application/json";
	private final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
	private final String HTML = "text/html";


	public GetAllApiController(){
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String url = req.getScheme() +  "://" + req.getServerName();

		ArrayList<Api> apis = new ArrayList<Api>();
		apis.add(new Api().url("/article-info").generatedUrl(url).consumes(FORM_URL_ENCODED).produces(JSON).expectedKeys(new String[]{"fld_login", "fld_password", "fld_product_number[]", "fld_product_count[]", "invalidateSession"}).description("Expects fld_login and fld_password if user unauthorized and expects fld_count[] and fld_product_number[] if user authorized. Returns article info"));
		apis.add(new Api().url("/addtocart").consumes(FORM_URL_ENCODED).produces(JSON).expectedKeys(new String[]{"fld_product_number[]", "fld_product_count[]"}).description("Returns message. Adds nothing to DB."));
		apis.add(new Api().url("/article-info-url").produces(JSON).description("Returns url to get article info."));
		apis.add(new Api().url("/save_order").consumes(JSON).produces(HTML).description("Unknown. Produces response with Content-type: text/html in json format. "));
		apis.add(new Api().url("/thr_generate_pdf").description("Unknown."));
		apis.add(new Api().url("/get_push_marketing").produces(JSON).description("Returns objects of push marketing."));
		apis.add(new Api().url("/thr_get_version").produces(JSON).description("Returns min version."));
		apis.add(new Api().url("/thr_get_ios_version").produces(JSON).description("Returns min ios version."));

		Gson gson = new GsonBuilder().serializeNulls().create();
		this.writeToResponse(res, gson.toJson(apis));
		return null;
	}
}
