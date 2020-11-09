package com.negeso.module.webshop.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.framework.util.MD5Encryption;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.framework.HttpException;
import com.negeso.module.webshop.service.CustomerService;
import com.negeso.framework.util.json.JsonSupportForController;
import com.negeso.module.webshop.util.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@SessionAttributes(SessionData.USER_ATTR_NAME)
public class CustomerApiController extends JsonSupportForController{

	private final CustomerService customerService;

	@Autowired
	public CustomerApiController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@RequestMapping(value = "/webshop/api/users", method = RequestMethod.GET)
	public void getAll(HttpServletResponse res, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException{
		super.hasRole(user, ROLE_ADMIN);

		List<Customer> list = customerService.list();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		super.writeToResponse(res, gson.toJson(list));
	}

	@RequestMapping(value = {"/webshop/api/user", "/webshop/api/user/*"}, method = RequestMethod.GET)
	public void getOne(HttpServletRequest req, HttpServletResponse res, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException{
		super.hasRole(user, ROLE_ADMIN);

		String param = PathVariable.getStringParameter(req, "/webshop/api/user/");
		if (param == null)
			throw new HttpException(400, "User code is null");

		Customer customer = customerService.findByLogin(param);
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

		super.writeToResponse(res, gson.toJson(customer));
	}

	@RequestMapping(value = "/webshop/api/user", method = RequestMethod.POST)
	public void create(HttpServletRequest req, HttpServletResponse res, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException{
		super.hasRole(user, ROLE_ADMIN);

		Customer customerToCreate = super.buildPojoFromRequest(req, Customer.class);

		try {
			User userToCreate = new User();
			userToCreate.setLogin(customerToCreate.getUserCode());
			userToCreate.setPassword(MD5Encryption.md5(customerToCreate.getUser().getGuid()));
			customerToCreate.setUser(userToCreate);
			customerService.create(customerToCreate);
		} catch (Exception e) {
			throw new HttpException(400, "Unable to save user");
		}
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

		super.writeToResponse(res, gson.toJson(customerToCreate));
	}

	@RequestMapping(value = {"/webshop/api/user", "/webshop/api/user/*"}, method = RequestMethod.PUT)
	public void put(HttpServletRequest req, HttpServletResponse res, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException{
		super.hasRole(user, ROLE_ADMIN);

		String param = PathVariable.getStringParameter(req, "/webshop/api/user/");
		if (param == null)
			throw new HttpException(400, "User code is null");

		Customer customerToUpdate = super.buildPojoFromRequest(req, Customer.class);
		Customer customer = customerService.findByLogin(param);
		customer.setEmail(customerToUpdate.getEmail());
		customer.setPostPayAllowed(customerToUpdate.getPostPayAllowed());
		customerService.update(customer);
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

		super.writeToResponse(res, gson.toJson(customer));
	}

	@RequestMapping(value = {"/webshop/api/user", "/webshop/api/user/*", "/webshop/api/users", "/webshop/api/users/*"}, method = RequestMethod.DELETE)
	public void delete(HttpServletRequest req, HttpServletResponse res, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException{
		super.hasRole(user, ROLE_ADMIN);

		String param = PathVariable.getStringParameter(req, "/webshop/api/users/");
		if (param == null)
			param = PathVariable.getStringParameter(req, "/webshop/api/user/");
		if (param == null)
			throw new HttpException(400, "User code is null");

		Customer customer = customerService.findByLogin(param);
		customerService.delete(customer);

		super.writeToResponseExcBody(res, 200);
	}
}
