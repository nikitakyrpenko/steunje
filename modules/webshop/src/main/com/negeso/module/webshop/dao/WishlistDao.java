package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.HibernateDao;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.Product;
import com.negeso.module.webshop.entity.Wishlist;

public interface WishlistDao extends HibernateDao<Wishlist, Integer> {
	void deleteBy(Customer customer, Product product);
	Wishlist findOneByCustomerAndProduct(Customer customer, Product product);
}
