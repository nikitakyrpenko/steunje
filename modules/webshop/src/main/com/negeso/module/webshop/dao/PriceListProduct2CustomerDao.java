package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.HibernateDao;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.PriceListProduct2Customer;

import java.util.List;

public interface PriceListProduct2CustomerDao extends HibernateDao<PriceListProduct2Customer, String> {
	List<PriceListProduct2Customer> findAllByCustomerAndProduct(Customer customer, String productId);
}
