package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.HibernateDao;
import com.negeso.module.webshop.entity.CartItem;

public interface CartItemDao extends HibernateDao<CartItem, Integer> {
	CartItem findOneByProductIdAndCustomerId(String productId, String customerId);
	void deleteOneByProductIdAndCustomerId(String productId, String customerId);
	void deleteAllByCustomerId(String userCode);
}
