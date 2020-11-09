package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.HibernateDao;
import com.negeso.module.webshop.entity.Product;

import java.util.List;

public interface ProductDao extends HibernateDao<Product, String> {
	Product findOneJoinDiscount(String id);
	List<Product> listByEANCodes(String[] codes);
	Product findOneByEAN(String code);
	List<Product> listSales();
	void updateIgnorableFields(Product product);
	List<String> listIds();
	List<Product> listProductIds();
}
