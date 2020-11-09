package com.negeso.module.webshop.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.negeso.framework.HttpException;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.framework.util.hibernate.HibernateUtil;
import com.negeso.framework.util.json.JsonSupportForController;
import com.negeso.framework.util.spring.interceptor.Unsecured;
import com.negeso.module.webshop.entity.CartItem;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.Product;
import com.negeso.module.webshop.service.CartItemService;
import com.negeso.module.webshop.service.CustomerService;
import com.negeso.module.webshop.service.OrderService;
import com.negeso.module.webshop.service.ProductService;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;

@Controller
public class CartApiController extends JsonSupportForController {
	private final static Logger logger = Logger.getLogger(CartApiController.class);
	public static Gson gson = new Gson();
	private final ProductService productService;
	private final CustomerService customerService;
	private final CartItemService cartItemService;
	private final OrderService orderService;

	@Autowired
	public CartApiController(ProductService productService, CustomerService customerService, CartItemService cartItemService, OrderService orderService) {
		this.productService = productService;
		this.customerService = customerService;
		this.cartItemService = cartItemService;
		this.orderService = orderService;
	}




	@Unsecured
	@RequestMapping(value = "/webshop/api/cart", method = RequestMethod.GET)
	public void getCart(HttpServletRequest req, HttpServletResponse res) throws IOException, HttpException {
		User user = this.resolveUserFromSession(req);
		Customer customer = customerService.findByLogin(user.getLogin());

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		super.writeToResponse(res, gson.toJson(customer.getItems()));
	}

	@Unsecured
	@RequestMapping(value = "/webshop/api/cart", method = RequestMethod.PUT)
	public void putCartOrCartItem(HttpServletRequest req, HttpServletResponse res, @RequestParam(value = "strategy", required = false) CartItemService.QuantityStrategy strategy) throws IOException, HttpException {
		User user = this.resolveUserFromSession(req);
		CartItem cartItem = super.buildPojoFromRequest(req, CartItem.class);
		String productCode = cartItem.getProduct().getId();
		Product proxyProduct = productService.findOneJoinDiscount(productCode);
		Product product = HibernateUtil.initAndUnproxy(proxyProduct);
		if (product == null) {
			throw new HttpException(HttpStatus.SC_NOT_FOUND, "Requested product wasn't found: " + productCode);
		}

		String login = user.getLogin();
		Customer customer = customerService.findByLogin(login);
		if (customer == null)
			throw new HttpException(HttpStatus.SC_NOT_FOUND, "Customer not found by login: " + login);

		CartItem item = cartItemService.findOneByProductIdAndCustomerId(product.getId(), customer.getUserCode());

		if (item == null) {
			cartItem.setCartOwner(customer.getUserCode());
			cartItem.setProduct(product);
			cartItemService.create(cartItem);
		} else {
			Integer newQuantity = cartItemService.calculateQuantity(item.getQuantity(), cartItem.getQuantity(), strategy);
			item.setQuantity(newQuantity);
			cartItemService.update(item);
		}

		super.writeToResponse(res, gson.toJson(orderService.calculateOrderDetails(customer)));
	}

	@Unsecured
	@RequestMapping(value = "/webshop/api/cart", method = RequestMethod.DELETE)
	public void delete(HttpServletRequest req, HttpServletResponse res, @RequestParam(value = "id", required = false) String productId) throws IOException, HttpException {
		User user = this.resolveUserFromSession(req);
		Customer customer = customerService.findByLogin(user.getLogin());

		if (productId == null) {
			cartItemService.deleteAllByCustomerId(customer.getUserCode());
			super.writeToResponseExcBody(res, HttpStatus.SC_OK);

			return;
		}

		cartItemService.deleteOneByProductIdAndCustomerId(productId, customer.getUserCode());

		super.writeToResponseExcBody(res, HttpStatus.SC_OK);
	}


	private User resolveUserFromSession(HttpServletRequest req) throws HttpException {
		User user = (User) req.getSession().getAttribute(SessionData.USER_ATTR_NAME);
		if (user == null)
			throw new HttpException(401, "Unauthenticated");
		return user;
	}
}
