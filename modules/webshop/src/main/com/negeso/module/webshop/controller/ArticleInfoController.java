package com.negeso.module.webshop.controller;

import com.google.gson.Gson;
import com.negeso.framework.HttpException;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.util.json.JsonSupportForController;
import com.negeso.module.core.service.ParameterService;
import com.negeso.module.webshop.bo.ArticleInfo;
import com.negeso.module.webshop.bo.ArticleInfoRequest;
import com.negeso.module.webshop.bo.JsonOrder;
import com.negeso.module.webshop.entity.CartItem;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.Product;
import com.negeso.module.webshop.service.CartItemService;
import com.negeso.module.webshop.service.CustomerService;
import com.negeso.module.webshop.service.ProductService;
import com.restfb.json.JsonObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ArticleInfoController extends JsonSupportForController {
	private final static Logger logger = Logger.getLogger(ArticleInfoController.class);

	private final ProductService productService;
	private final CustomerService customerService;
	private final ParameterService parameterService;
	private final CartItemService cartItemService;

	@Autowired
	public ArticleInfoController(ProductService productService, CustomerService customerService, ParameterService parameterService, CartItemService cartItemService) {
		this.productService = productService;
		this.customerService = customerService;
		this.parameterService = parameterService;
		this.cartItemService = cartItemService;
	}

	@RequestMapping("/webshop/api/article-info")
	public void getProductInfo(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String[] codes = ServletRequestUtils.getStringParameters(req, "fld_product_number[]");

		List<Product> products = this.productService.listByEANCodes(codes);
		JsonObject json = this.jsonStructureLikeThr(products);

		super.writeToResponse(res, json.toString());
	}

	@RequestMapping("/webshop/api/add-to-cart")
	public void isPossibleToAddToCart(HttpServletRequest request, HttpServletResponse res) throws IOException {
		String[] fldProductNumbers = ServletRequestUtils.getStringParameters(request, "fld_product_number[]");
		StringBuilder sb = new StringBuilder();
		int total = 0;
		String error = null;
		for (String fldProductNumber : fldProductNumbers) {
			Product product = this.productService.findOneByEAN(fldProductNumber);
			if (product == null)
				error = "Niet gevonden";
			else if (!product.getVisible())
				error = "Niet meer bestelbaar";
			total++;
			sb
					.append("- ")
					.append(error != null ? "ERROR " + fldProductNumber + " " + error : "OK " + fldProductNumber + " ")
			;
		}

		JsonObject json = new JsonObject();
		json.put("total", total);
		json.put("message", sb.toString());
		json.put("status", 2);
		json.put("success", true);

		super.writeToResponse(res, json.toString());
	}

	@RequestMapping("/webshop/api/buy")
	public void makeOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String responseMessage = "{\"success\":true}";
		try {
			String stringOrder = request.getParameter("order");
			logger.error(stringOrder); //TODO delete it after test
			JsonOrder jsonOrder = new Gson().fromJson(stringOrder, JsonOrder.class);

			User user = (User) request.getSession().getAttribute(SessionData.USER_ATTR_NAME);
			Customer customer = customerService.findByUser(user);
			if (customer == null) {
				logger.error("customer is null");
				customer = new Customer();
				customer.setUserCode(user.getLogin());
				customer.setUser(user);
				customerService.createSafety(customer);
			}

			for (ArticleInfoRequest articleInfoRequest : jsonOrder.getProducts()) {
				Product product = productService.findOneByEAN(articleInfoRequest.getBarcode());
				if (product == null) continue;

				CartItem item = cartItemService.findOneByProductIdAndCustomerId(product.getId(), customer.getUserCode());
				if (item == null) {
					item = new CartItem(product, articleInfoRequest.getCount(), customer.getUserCode());
					cartItemService.create(item);
				} else {
					Integer newQuantity = cartItemService.calculateQuantity(item.getQuantity(), articleInfoRequest.getCount(), CartItemService.QuantityStrategy.ADD);
					item.setQuantity(newQuantity);
					cartItemService.update(item);
				}
			}

		} catch (Exception e) {
			logger.error("Error", e);
			response.setContentType("text/html");
			response.setStatus(500);
			responseMessage = e.getMessage();
		}
		PrintWriter out = response.getWriter();
		out.print(responseMessage);
		out.close();
	}

	@RequestMapping("/webshop/api/push_marketing")
	public void pushMarketing(HttpServletResponse res) throws IOException, HttpException {
		JsonObject obj = new JsonObject();
		obj.put("enabled", parameterService.findParameterByName("thrPushMarketingEnabled").getValue());
		obj.put("text", parameterService.findParameterByName("ws.push_marketing_title").getValue());

		List<Product> sales = productService.listSales();
		if (sales != null && sales.size() > 0) {
			Collections.shuffle(sales);

			int i = 0;
			do {
				Product product = sales.get(i);
				if (product.getVisible()) {
					JsonObject productJsonObject = this.buildStructureLikeThrForPushMarketing(i + 1, product);
					obj.put(Integer.toString(i), productJsonObject);
				}
				i++;
			} while (i < 2 && sales.size() >= 2);
		}
		super.writeToResponse(res, obj.toString());
	}

	private JsonObject buildStructureLikeThrForPushMarketing(int order, Product product) {
		JsonObject productJsonObject = new JsonObject();
		productJsonObject.put("id", product.getId());
		productJsonObject.put("title", product.getTitle());
		productJsonObject.put("price", product.getPriceExcludeVat());
		productJsonObject.put("visible", product.getVisible());
		productJsonObject.put("test", false);
		productJsonObject.put("description", product.getTitle());
		productJsonObject.put("orderNumber", order);
		productJsonObject.put("barCode", product.getEan());
		productJsonObject.put("image", productService.getImageUrl(product));
		productJsonObject.put("show", "y");

		return productJsonObject;
	}

	private JsonObject jsonStructureLikeThr(List<Product> products) {
		JsonObject complicated = new JsonObject();
		int i = 0;
		for (Product product : products) {
			if (product.getVisible()) {
				ArticleInfo bean = new ArticleInfo(product);
				JsonObject productJsonObject = new JsonObject(bean);
				complicated.put(Integer.toString(i++), productJsonObject);
			}
		}

		complicated.put("success", true);
		complicated.put("status", 2);
		complicated.put("message", "Producten.");
		complicated.put("total", products.size());

		return complicated;
	}
}
