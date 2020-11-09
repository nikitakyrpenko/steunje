package com.negeso.module.webshop.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.negeso.framework.HttpException;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.framework.util.json.JsonSupportForController;
import com.negeso.module.webshop.entity.ProductCategory;
import com.negeso.module.webshop.service.CustomerService;
import com.negeso.module.webshop.service.ProductCategoryService;
import com.negeso.module.webshop.util.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Controller
@SessionAttributes(SessionData.USER_ATTR_NAME)
public class CategoriesController extends JsonSupportForController {

	private final ProductCategoryService categoriesService;
	private final CustomerService customerService;
	private Gson gson = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.create();

	@Autowired
	public CategoriesController(ProductCategoryService categoriesService, CustomerService customerService) {
		this.categoriesService = categoriesService;
		this.customerService = customerService;
	}

	@RequestMapping(value = {"/webshop/api/categories", "/webshop/api/categories/*"}, method = RequestMethod.GET)
	public void get(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);
		String name = PathVariable.getStringParameter(request, "/webshop/api/categories/");

		if (name == null) {
			List<ProductCategory> root = categoriesService.findAllRootCategories(true);
			super.writeToResponse(response, gson.toJson(root));
		} else {
			List<ProductCategory> root = categoriesService.findAllByParent(name);
			super.writeToResponse(response, gson.toJson(root));
		}
	}

	@RequestMapping(value = "/webshop/api/category/{name}", method = RequestMethod.GET)
	public void read(HttpServletRequest request, HttpServletResponse response, @org.springframework.web.bind.annotation.PathVariable("name") String name, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);

		ProductCategory category = categoriesService.findOne(name);
		ProductCategory parentCategory = category.getParentCategory() == null ? null : category.getParentCategory();
		List<String> customersLogin = customerService.loginList();
		JsonElement jsonCategory = gson.toJsonTree(category);
		JsonElement jsonParentCategory = gson.toJsonTree(parentCategory);
		JsonObject jsonObject = jsonCategory.getAsJsonObject();
		JsonElement jsonElement = gson.toJsonTree(customersLogin);
		jsonObject.add("customers", jsonElement);
		jsonObject.add("parentCategory", jsonParentCategory);
		super.writeToResponse(response, jsonObject.toString());
	}

	@RequestMapping(value = "/webshop/api/categories", method = RequestMethod.POST)
	public void create(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);

		ProductCategory categoryToCreate = super.buildPojoFromRequest(request, ProductCategory.class);

		if (categoryToCreate.getParentCategory() != null && categoryToCreate.getParentCategory().getId() != null) {
			ProductCategory parentCategory = categoriesService.findOne(categoryToCreate.getParentCategory().getId());
			categoryToCreate.setParentCategory(parentCategory);
		}

		categoryToCreate.setId(this.generateUniqueName(categoryToCreate));
		categoryToCreate.setCreationDate(new Timestamp(System.currentTimeMillis()));

		categoriesService.insert(categoryToCreate);

		super.writeToResponse(response, gson.toJson(categoryToCreate));
	}

	@RequestMapping(value = "/webshop/api/categories/*", method = RequestMethod.PUT)
	public void update(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);
		String name = PathVariable.getStringParameter(request, "/webshop/api/categories/");
		if (name == null)
			throw new HttpException(402, "Name couldn't be null");

		ProductCategory category = super.buildPojoFromRequest(request, ProductCategory.class);
		ProductCategory db = categoriesService.findOne(name);
		db.setTitle(category.getTitle());
		db.setVisible(category.getVisible());
		db.setVisibleTo(category.getVisibleTo());

		super.writeToResponse(response, gson.toJson(db));
	}

	@RequestMapping(value = "/webshop/api/categories/*", method = RequestMethod.DELETE)
	public void delete(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionData.USER_ATTR_NAME) User user) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);
		String name = PathVariable.getStringParameter(request, "/webshop/api/categories/");
		if (name == null)
			throw new HttpException(402, "Name couldn't be null");

		ProductCategory one = categoriesService.findOne(name);
		categoriesService.delete(one);
		super.writeToResponse(response, gson.toJson(one));
	}

	private String generateUniqueName(ProductCategory category) {
		int level = 1;

		if (category.getParentCategory() != null) {
			String name = category.getParentCategory().getId();
			level = Integer.valueOf(name.substring(name.length() - 1)) + 1;
		}

		return category.getTitle().replaceAll("[!@#$%^&* .]", "_").toLowerCase() +
				"-" +
				level
				;
	}

	@RequestMapping(value = "/webshop/api/categories", method = RequestMethod.PUT)
	public void changeOrder(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionData.USER_ATTR_NAME) User user, @RequestParam("action") String action) throws IOException, HttpException {
		super.hasRole(user, ROLE_ADMIN);
		if ("changeOrder".equals(action)) {
			this.changeOrder(request, response);
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	private void changeOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String categoryId = ServletRequestUtils.getStringParameter(request, "objId", null);
		String direction = ServletRequestUtils.getStringParameter(request, "direction", null);

		categoriesService.changeDirection(categoryId, direction);
		ProductCategory one = categoriesService.findOne(categoryId);

		List<ProductCategory> root = one.getParentCategory() == null
				? categoriesService.findAllRootCategories(true)
				: categoriesService.findAllByParent(one.getParentCategory().getId());

		super.writeToResponse(response, gson.toJson(root));
	}

}
