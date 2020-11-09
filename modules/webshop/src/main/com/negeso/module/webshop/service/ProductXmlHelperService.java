package com.negeso.module.webshop.service;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.friendly_url.FriendlyUrlService;
import com.negeso.framework.friendly_url.UrlEntityType;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.jaxb.GermanPriceAdapter;
import com.negeso.framework.util.hibernate.HibernateUtil;
import com.negeso.module.webshop.entity.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductXmlHelperService {

	private final static Logger logger = Logger.getLogger(ProductXmlHelperService.class);

	private final DiscountService discountService;
	private final WishlistService wishlistService;

	@Autowired
	public ProductXmlHelperService(DiscountService discountService, WishlistService wishlistService) {
		this.discountService = discountService;
		this.wishlistService = wishlistService;
	}

	public void appendFromCollection(Element parent, List<Product> products, Customer customer) {
		Map<MatrixCategory, List<Product>> matrix2Products = new LinkedHashMap<MatrixCategory, List<Product>>();
		for (Product product : products) {
			List<Product> productsInMap = matrix2Products.get(product.getMatrixCategory());
			if (productsInMap == null)
				productsInMap = new ArrayList<Product>();

			productsInMap.add(product);
			matrix2Products.put(product.getMatrixCategory(), productsInMap);
		}

		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			for (Map.Entry<MatrixCategory, List<Product>> entry : matrix2Products.entrySet()) {
				Element el = Xbuilder.createEl(parent, "matrix", null);
				el.setAttribute("title", entry.getKey() == null || "".equals(entry.getKey().getTitle()) ? "not-a-matrix" : entry.getKey().getTitle());
				for (Product product : entry.getValue()) {
					Product unpoxied = HibernateUtil.initAndUnproxy(product);
					if (unpoxied.getVisible()) {
						Element productEl = Xbuilder.addBeanJAXB(el, unpoxied);
						try {
							BigDecimal discount = discountService.determineDiscount(customer, new OrderItem(1, product));
							if (discount == null)
								discount = BigDecimal.ZERO;
							BigDecimal priceAfterDiscount = product.getPriceExcludeVat().subtract(product.getPriceExcludeVat().multiply(discount));
							productEl.setAttribute("priceAfterDiscount", new GermanPriceAdapter().marshal(priceAfterDiscount));
						} catch (Exception e) {
							productEl.setAttribute("priceAfterDiscount", null);
						}
						this.appendProductUrl(conn, productEl, unpoxied);
						productEl.setAttribute("in-wishlist", String.valueOf(this.inWishList(customer, product)));
					}
				}
				parent.appendChild(el);
			}
		} catch (SQLException e) {
			logger.error("-e", e);
		} finally {
			DBHelper.close(conn);
		}
	}

	private void appendProductUrl(Connection conn, Element productEl, Product one) {
		String url;
		try {
			String uri = FriendlyUrlService.find(conn, one.getId(), 1, UrlEntityType.PRODUCT);
			if (uri == null)
				throw new NullPointerException();
			url = uri + ".html";
		} catch (Exception e) {
			url = "/webshop_nl.html?pmProductId=" + one.getId();
		}

		productEl.setAttribute("url", url);
	}

	private boolean inWishList(Customer customer, Product product) {
		return wishlistService.findOneByCustomerAndProduct(customer, product) != null;
	}
}
