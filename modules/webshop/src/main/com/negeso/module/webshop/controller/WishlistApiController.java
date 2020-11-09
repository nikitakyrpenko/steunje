package com.negeso.module.webshop.controller;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.Product;
import com.negeso.module.webshop.entity.Wishlist;
import com.negeso.module.webshop.service.CustomerService;
import com.negeso.module.webshop.service.ProductService;
import com.negeso.module.webshop.service.WishlistService;
import com.negeso.framework.util.json.JsonSupportForController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class WishlistApiController extends JsonSupportForController {

	private final CustomerService customerService;
	private final ProductService productService;
	private final WishlistService wishlistService;

	@Autowired
	public WishlistApiController(CustomerService customerService, ProductService productService, WishlistService wishlistService) {
		this.customerService = customerService;
		this.productService = productService;
		this.wishlistService = wishlistService;
	}

	@RequestMapping(value = "/webshop/api/wishlist", method = RequestMethod.POST)
	public void add(HttpServletRequest req, HttpServletResponse res, @RequestParam("id") String orderCode) throws IOException {

		Product product = productService.findOne(orderCode);
		Customer customer = customerService.findByUser((User) req.getSession().getAttribute(SessionData.USER_ATTR_NAME));

		if(customer == null){
			super.writeToResponseExcBody(res, 403);
			return;
		}

		if (wishlistService.findOneByCustomerAndProduct(customer, product) != null) {
			super.writeToResponseExcBody(res, 200);
			return;
		}

		Wishlist wishlist = new Wishlist();
		wishlist.setCustomer(customer);
		wishlist.setProduct(product);

		wishlistService.create(wishlist);

		super.writeToResponseExcBody(res, 200);
	}

	@RequestMapping(value = "/webshop/api/wishlist", method = RequestMethod.DELETE)
	public void delete(HttpServletRequest req, HttpServletResponse res, @RequestParam("id") String orderCode) throws IOException {

		Product product = productService.findOne(orderCode);
		Customer customer = customerService.findByUser((User) req.getSession().getAttribute(SessionData.USER_ATTR_NAME));

		wishlistService.deleteBy(customer, product);

		super.writeToResponseExcBody(res, 200);
	}
}
