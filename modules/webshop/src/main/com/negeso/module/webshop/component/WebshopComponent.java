package com.negeso.module.webshop.component;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.jaxb.GermanPriceAdapter;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.module.webshop.entity.*;
import com.negeso.module.webshop.service.*;
import com.negeso.module.webshop.util.ProductCategoryXmlHelper;
import com.negeso.module.webshop.util.ProductXmlHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component("webshop-main_component")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class WebshopComponent extends AbstractPageComponent {
	private final static Logger logger = Logger.getLogger(WebshopComponent.class);

	private final ProductCategoryService productCategoryService;
	private final ProductService productService;
	private final CustomerService customerService;
	private final ProductXmlHelperService productXmlHelperService;
	private final DiscountService discountService;
	private final WishlistService wishlistService;

	@Autowired
	public WebshopComponent(ProductCategoryService productCategoryService, ProductService productService, CustomerService customerService, ProductXmlHelperService productXmlHelperService, DiscountService discountService, WishlistService wishlistService) {
		this.productCategoryService = productCategoryService;
		this.productService = productService;
		this.customerService = customerService;
		this.productXmlHelperService = productXmlHelperService;
		this.discountService = discountService;
		this.wishlistService = wishlistService;
	}

	@Override
	public Element getElement(Document document, RequestContext request, Map parameters) {
		Element webshopRootElement = Xbuilder.createEl(document, "webshop", null);

		User user = request.getSession().getUser();
		Customer customer = null;
		if (user != null){
			customer = customerService.findByUser(user);
		}

		try {
			String categoryId = request.getString("pmCatId", request.getString("category", null));
			String productCode = request.getString("pmProductId", request.getString("product", null));

			if (categoryId != null) {
				ProductCategory category = productCategoryService.findOneJoinProducts(categoryId);
				if (category == null)
					return webshopRootElement;
				this.buildChainCategoriesIncProducts(webshopRootElement, category, customer);
			} else if (productCode != null) {
				Product product = productService.findOne(productCode);
				if (product == null || !product.isProductVisibleFor(customer)){
					webshopRootElement.setAttribute("hidden", "true");
					return webshopRootElement;
				}
				this.buildProductDetails(webshopRootElement, product, customer);
			} else {
				List<ProductCategory> rootCategories = productCategoryService.findAllRootCategories(true);
				if (rootCategories == null || rootCategories.isEmpty())
					return webshopRootElement;
				this.buildRootCategoriesExcProducts(webshopRootElement, rootCategories, customer);
			}
		} catch (Exception e) {
			logger.error("Failed to build component", e);
			return webshopRootElement;
		}

		return webshopRootElement;
	}

	private void buildProductDetails(Element webshopRootElement, Product one, Customer customer) {
		ProductCategoryXmlHelper.buildDownUpChain(webshopRootElement, one.getCategory(), customer);
		Element productEl = Xbuilder.addBeanJAXB(webshopRootElement, one);
		try {
			BigDecimal discount = discountService.determineDiscount(customer, new OrderItem(1, one));
			if (discount == null)
				discount = BigDecimal.ZERO;
			BigDecimal discountAmount = one.getPriceExcludeVat().multiply(discount);
			BigDecimal priceAfterDiscount = one.getPriceExcludeVat().subtract(discountAmount);
			productEl.setAttribute("priceAfterDiscount", new GermanPriceAdapter().marshal(priceAfterDiscount));
			productEl.setAttribute("in-wishlist", String.valueOf(wishlistService.findOneByCustomerAndProduct(customer, one) != null));
		} catch (Exception e) {
			logger.error("Unable to append priceAfterDiscount attribute");
		}
	}

	private void appendProductUrl(Element productEl, Product one) {
		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			ProductXmlHelper.appendProductUrl(conn, productEl, one);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBHelper.close(conn);
		}
	}


	private void buildRootCategoriesExcProducts(Element webshopRootElement, ProductCategory one) {
		ProductCategoryXmlHelper.buildRootSubcategories(webshopRootElement, one);
	}

	private void buildRootCategoriesExcProducts(Element webshopRootElement, List<ProductCategory> categories, Customer customer) {
		ProductCategoryXmlHelper.buildRootSubcategories(webshopRootElement, categories, customer);
	}

	private void buildSubCategoriesExcProducts(Element webshopRootElement, ProductCategory one, Customer customer) {
		ProductCategoryXmlHelper.buildSubCategories(one, webshopRootElement, customer);
	}

	private void buildSubCategoriesExcProducts(Element webshopRootElement, ProductCategory one) {
		this.buildSubCategoriesExcProducts(webshopRootElement, one, null);
	}

	private void buildChainCategoriesIncProducts(Element webshopRootElement, ProductCategory one){
		this.buildChainCategoriesIncProducts(webshopRootElement, one, null);
	}

	private void buildChainCategoriesIncProducts(Element webshopRootElement, ProductCategory one, Customer customer) {
		Element element = ProductCategoryXmlHelper.buildDownUpChain(webshopRootElement, one, customer);
		if (one.getChildCategories() != null && !one.getChildCategories().isEmpty())
			buildSubCategoriesExcProducts(element, one, customer);
		List<Product> products = one.getProducts();
		if (products != null && !products.isEmpty()){
			productXmlHelperService.appendFromCollection(element, products, customer);

		}
	}
}
