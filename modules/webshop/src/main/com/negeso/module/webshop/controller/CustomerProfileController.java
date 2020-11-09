package com.negeso.module.webshop.controller;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.service.CustomerService;
import com.negeso.framework.util.json.JsonSupportForController;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class CustomerProfileController extends JsonSupportForController{

	private final CustomerService customerService;

	@Autowired
	public CustomerProfileController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@RequestMapping(value = "/webshop/api/profile", method = RequestMethod.POST)
	public void save(HttpServletRequest request, HttpServletResponse response) throws IOException{

		Customer customer = customerService.findByUser((User) request.getSession().getAttribute(SessionData.USER_ATTR_NAME));

		String email = ServletRequestUtils.getStringParameter(request, "email", null);
		String displayPrice = ServletRequestUtils.getStringParameter(request, "display_price", null);

		if (email != null)
			customer.setEmail(email);
		if (displayPrice != null)
			customer.setDisplayPrice(displayPrice);

		customerService.update(customer);

		super.writeToResponseExcBody(response, HttpStatus.SC_ACCEPTED);
	}
}
