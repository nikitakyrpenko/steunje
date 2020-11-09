package com.negeso.module.webshop.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.negeso.framework.domain.User;
import com.negeso.framework.util.MD5Encryption;
import com.negeso.module.webshop.entity.*;
import com.negeso.module.webshop.service.CustomerService;
import com.negeso.module.webshop.service.GeneratePdfService;
import com.negeso.module.webshop.service.NotificationService;
import com.negeso.framework.util.json.JsonSupportForController;
import com.negeso.module.webshop.service.ProductService;
import com.negeso.module.webshop.util.PathVariable;
import com.restfb.json.JsonObject;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class TestController extends JsonSupportForController {

	private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private GeneratePdfService generatePdfService;

	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ProductService productService;

	public void test(HttpServletRequest req, HttpServletResponse res) throws Exception {

		writeToResponse(res, "" + ((System.currentTimeMillis())));
	}

	@RequestMapping("/webshop/api/test")
	public void staticPage(PrintWriter stream) throws IOException{

		JsonObject jsonObject = new JsonObject();
		try {
			generatePdfService.generatePdf();
		} catch (Exception e) {
			e.printStackTrace();
		}
		jsonObject.put("msg", "ok");
		stream.print(jsonObject.toString());
	}

	@RequestMapping("/webshop/api/test/*")
	public void staticPaged(HttpServletRequest req, PrintWriter stream) throws IOException{

		Customer pavlo = customerService.findByLogin("20047");
		Product one = productService.findOne("000081");
		CartItem cartItem = new CartItem();
		cartItem.setProduct(one);
		cartItem.setQuantity(45);
		cartItem.setCartOwner(pavlo.getUserCode());

		pavlo.getItems().remove(cartItem);
		pavlo.getItems().add(cartItem);

		String st = PathVariable.getStringParameter(req, "d");
		JsonObject jsonObject = new JsonObject();
		jsonObject.put("status", st);
		stream.print(jsonObject.toString());
	}
}
