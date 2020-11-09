package com.negeso.module.webshop.util;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.friendly_url.FriendlyUrlService;
import com.negeso.framework.friendly_url.UrlEntityType;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.util.hibernate.HibernateUtil;
import com.negeso.module.webshop.entity.MatrixCategory;
import com.negeso.module.webshop.entity.Product;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductXmlHelper {

	@Deprecated
	public static void appendFromCollection(Element parent, List<Product> products) {
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
					if (unpoxied.getVisible()){
						Element productEl = Xbuilder.addBeanJAXB(el, unpoxied);
						appendProductUrl(conn, productEl, unpoxied);
					}
				}
				parent.appendChild(el);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBHelper.close(conn);
		}
	}

	public static void appendProductUrl(Connection conn, Element productEl, Product one) {
		String url;
		try {
			String uri = FriendlyUrlService.find(conn, one.getId(), 1, UrlEntityType.PRODUCT);
			if (uri == null)
				throw new NullPointerException();
			url = uri + ".html";
		} catch (Exception e){
			url = "/webshop_nl.html?pmProductId=" + one.getId();
		}

		productEl.setAttribute("url", url);
	}
}
