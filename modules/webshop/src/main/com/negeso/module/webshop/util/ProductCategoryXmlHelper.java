package com.negeso.module.webshop.util;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.friendly_url.FriendlyUrlService;
import com.negeso.framework.friendly_url.UrlEntityType;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.ProductCategory;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.util.List;

public class ProductCategoryXmlHelper {

	public static Element buildDownUpChain(Element root, ProductCategory category, Customer customer) {
		ProductCategory parentCategory = category.getParentCategory();
		if (parentCategory == null) {

			return ProductCategoryXmlHelper.createRootCategory(root, category, customer);
		} else {
			Element parent = buildDownUpChain(root, parentCategory, customer);
			Element child = null;
			for (ProductCategory productCategory : parentCategory.getChildCategories()) {
				if (productCategory.isCategoryVisibleFor(customer)) {
					String id = productCategory.getId();
					Element rootChild = ProductCategoryXmlHelper.createCategoryDetails(parent, productCategory, customer);
					parent.appendChild(rootChild);

					if (id.equals(category.getId())) {
						child = rootChild;
						child.setAttribute("selected", Boolean.TRUE.toString());
					}
				}
			}

			return child;
		}
	}

	public static Element buildRootSubcategories(Element root, ProductCategory category) {
		Element rootCategory = ProductCategoryXmlHelper.createRootCategory(root, category);
		buildSubCategories(category, rootCategory);

		return rootCategory;
	}

	public static Element buildRootSubcategories(Element root, List<ProductCategory> categories, Customer customer) {
		for (ProductCategory category : categories) {
			if (category.isCategoryVisibleFor(customer)) {
				Element categoryDetails = createCategoryDetails(root, category, customer);
				root.appendChild(categoryDetails);
			}
		}

		return root;
	}

	public static Element buildRootSubcategories(Element root, List<ProductCategory> categories) {
		return ProductCategoryXmlHelper.buildRootSubcategories(root, categories, null);
	}

	public static void buildSubCategories(ProductCategory category, Element rootCategoryElement, Customer customer) {
		for (ProductCategory productCategory : category.getChildCategories()) {
			if (!productCategory.isCategoryVisibleFor(customer)) continue;
			Element categoryDetails = createCategoryDetails(rootCategoryElement, productCategory, customer);
			rootCategoryElement.appendChild(categoryDetails);
		}
	}

	public static void buildSubCategories(ProductCategory category, Element rootCategoryElement) {
		ProductCategoryXmlHelper.buildSubCategories(category, rootCategoryElement, null);
	}

	private static Element createCategoryDetails(Element parent, ProductCategory category, Customer customer) {
		Element rootChild = Xbuilder.createEl(parent, "category", null);
		rootChild.setAttribute("title", category.getTitle());
		rootChild.setAttribute("image", category.getImage());
		appendUrl(rootChild, category, customer);
		return rootChild;
	}

	private static Element createCategoryDetails(Element parent, ProductCategory category) {
		return ProductCategoryXmlHelper.createCategoryDetails(parent, category, null);
	}

	private static Element createRootCategory(Element root, ProductCategory category, Customer customer) {
		Element rootCategory = Xbuilder.createEl(root, "categories", null);
		rootCategory.setAttribute("title", category.getTitle());
		ProductCategoryXmlHelper.appendUrl(rootCategory, category, customer);
		root.appendChild(rootCategory);
		return rootCategory;
	}

	private static Element createRootCategory(Element root, ProductCategory category) {
		return createRootCategory(root, category, null);
	}

	private static void appendUrl(Element el, ProductCategory category, Customer customer) {
		Connection conn = null;
		String url = "/webshop_nl/";
		try {
			conn = DBHelper.getConnection();
			String uri = FriendlyUrlService.find(conn, category.getId(), 1, UrlEntityType.PRODUCT_CATEGORY);
			if (uri == null)
				throw new NullPointerException();
			if (customer != null && customer.getUser() != null && "administrator".equals(customer.getUser().getType())) {
				url = "/admin" + url;
			}
			url = url + uri + ".html";
		} catch (Exception e) {
			url = "/webshop_nl.html?pmCatId=" + category.getId();
		} finally {
			DBHelper.close(conn);
		}

		el.setAttribute("url", url);
	}

	private static void appendUrl(Element el, ProductCategory category) {
		ProductCategoryXmlHelper.appendUrl(el, category, null);
	}

}
