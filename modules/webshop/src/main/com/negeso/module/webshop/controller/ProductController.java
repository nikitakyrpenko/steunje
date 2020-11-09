package com.negeso.module.webshop.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.module.webshop.entity.Product;
import com.negeso.module.webshop.entity.ProductCategory;
import com.negeso.framework.HttpException;
import com.negeso.module.webshop.service.*;
import com.negeso.framework.util.json.JsonSupportForController;
import com.negeso.module.webshop.util.PathVariable;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@SessionAttributes(SessionData.USER_ATTR_NAME)
public class ProductController extends JsonSupportForController {

	private final ProductService productService;
	private final ProductGroupService productGroupService;
	private final MatrixCategoryService matrixCategoryService;
	private final ProductCategoryService productCategoryService;
	private final ManageFilesService manageFilesService;

	@Autowired
	public ProductController(ProductService productService, ProductGroupService productGroupService, MatrixCategoryService matrixCategoryService, ProductCategoryService productCategoryService, ManageFilesService manageFilesService) {
		this.productService = productService;
		this.productGroupService = productGroupService;
		this.matrixCategoryService = matrixCategoryService;
		this.productCategoryService = productCategoryService;
		this.manageFilesService = manageFilesService;
	}

	@RequestMapping(value = {"/webshop/api/product", "/webshop/api/product/*"}, method = RequestMethod.GET)
	public void read(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);
		String orderCode = PathVariable.getStringParameter(request, "/webshop/api/product/");
		if (orderCode == null)
			throw new HttpException(400, "Order code is null");

		Product product = productService.findOne(orderCode);

		JsonObject jsonObject = buildStateFullProduct(product);

		super.writeToResponse(response, jsonObject.toString());
	}

	@RequestMapping(value = {"/webshop/api/products", "/webshop/api/products/*"}, method = RequestMethod.GET)
	public void readAll(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);
		String categoryId = PathVariable.getStringParameter(request, "/webshop/api/products/");
		if (categoryId == null)
			throw new HttpException(400, "Category is null");

		ProductCategory category = productCategoryService.findOne(categoryId);

		List<Product> products = category.getProducts();
		Gson gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.create();

		super.writeToResponse(response, gson.toJson(products));
	}

	@RequestMapping(value = "/webshop/api/products", method = RequestMethod.POST)
	public void create(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);
		Product product = super.buildPojoFromRequest(request, Product.class);

		productService.insert(product);
		JsonObject jsonObject = buildStateFullProduct(product);

		super.writeToResponse(response, jsonObject.toString());
	}

	@RequestMapping(value = {"/webshop/api/products", "/webshop/api/products/*", "/webshop/api/product", "/webshop/api/product/*"}, method = RequestMethod.PUT)
	public void update(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);
		String orderCode = PathVariable.getStringParameter(request, "/webshop/api/products/");
		if (orderCode == null)
			orderCode = PathVariable.getStringParameter(request, "/webshop/api/product/");
		if (orderCode == null)
			throw new HttpException(400, "Order code is null");

		Product product = productService.findOne(orderCode);

		Product productFromRequest = super.buildPojoFromRequest(request, Product.class);
		BeanUtils.copyProperties(productFromRequest, product, "id", "category", "priceListProduct", "packingUnit");//todo move packing unit
		Product saved = productService.update(product);
		productService.updateIgnorableFields(product);
		JsonObject jsonObject = buildStateFullProduct(saved);

		super.writeToResponse(response, jsonObject.toString());
	}

	@RequestMapping(value = {"/webshop/api/products", "/webshop/api/products/*"}, method = RequestMethod.DELETE)
	public void delete(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);
		String orderCode = PathVariable.getStringParameter(request, "/webshop/api/products/");
		if (orderCode == null)
			throw new HttpException(400, "Order code is null");

		productService.deleteByCode(orderCode);

		super.writeToResponseExcBody(response, HttpStatus.SC_ACCEPTED);
	}

	@RequestMapping(value = "/webshop/api/product_attribute", method = RequestMethod.GET)
	public void getAttributes(HttpServletResponse response, @RequestParam("type") String type) throws IOException {
		List<String> list = new ArrayList<String>();
		if (type.equals("group"))
			list = productGroupService.primaryKeys();
		else if (type.equals("matrix"))
			list = matrixCategoryService.primaryKeys();

		String[] array = list.toArray(new String[]{});
		super.writeToResponse(response, new Gson().toJson(array));
	}

	@RequestMapping(value = "/webshop/api/products/attachment", method = RequestMethod.POST)
	public void attachment(HttpServletResponse res, @ModelAttribute(SessionData.USER_ATTR_NAME) User user, @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("filename") String filename) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);

		manageFilesService.save(file, filename);

		super.writeToResponseExcBody(res, 200);
	}

	@RequestMapping(value = "/webshop/api/sales", method = RequestMethod.GET)
	public void getSales(HttpServletResponse res, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException{
		super.hasRole(user, ROLE_ADMIN);

		List<Product> sales = productService.listSales();
		Gson gson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.create();
		super.writeToResponse(res, gson.toJson(sales));
	}

	@RequestMapping(value = "/webshop/api/product_codes", method = RequestMethod.GET)
	@ResponseBody
	public List<Product> getProductCodes(@ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException{

		return productService.listProductIds();
	}

	private JsonObject buildStateFullProduct(Product product) {
		JsonObject json = new JsonObject();
		json.addProperty("id", product.getId());
		json.addProperty("productNumber", product.getProductNumber() == null ? StringUtils.EMPTY : product.getProductNumber());
		json.addProperty("title", product.getTitle());
		json.addProperty("EAN", product.getEan() == null ? StringUtils.EMPTY : product.getEan());
		json.addProperty("group", product.getArticleGroup().getGroup());
		json.addProperty("priceIncVat", product.getPriceIncludeVat());
		json.addProperty("priceExcVat", product.getPriceExcludeVat());
		json.addProperty("stock", product.getStock() == null ? 0 : product.getStock());
		json.addProperty("stockMin", product.getStockMin() == null ? 0 : product.getStockMin());
		json.addProperty("visible", product.getVisible());
		json.addProperty("retailPrice", product.getRetailPriceExcludeVat() == null ? 0.00 : product.getRetailPriceExcludeVat());
		json.addProperty("content", product.getContent() == null ? StringUtils.EMPTY : product.getContent());
		json.addProperty("color", product.getColor() == null ? StringUtils.EMPTY : product.getColor());
		json.addProperty("brand", product.getBrand() == null ? StringUtils.EMPTY : product.getBrand());
		json.addProperty("keepStock", product.getKeepStock());
		json.addProperty("multipleOf", product.getMultipleOf() == null ? 1 : product.getMultipleOf());
		json.addProperty("matrix", product.getMatrixCategory() == null ? StringUtils.EMPTY : product.getMatrixCategory().getTitle());
		json.addProperty("matrixValue", product.getMatrixValue() == null ? StringUtils.EMPTY : product.getMatrixValue());
		json.addProperty("image", Env.getHostName() + (manageFilesService.exists("productsImages/" + product.getId() + ".jpg")
				? "media/productsImages/" + product.getId() + ".jpg"
				: "media/cap.jpg"
		));
		json.addProperty("sale", product.getSale());
		if (manageFilesService.exists("productsPdf/" + product.getId() + ".pdf")) {
			json.addProperty("attachment", Env.getHostName() + "media/productsPdf/" + product.getId() + ".pdf");
		}
		json.addProperty("description", product.getDescription());

		return json;
	}
}
