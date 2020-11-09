package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.HibernateDao;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.PriceListProductGroup2Customer;
import com.negeso.module.webshop.entity.ProductGroup;

import java.util.List;

public interface PriceListProductGroup2CustomerDao extends HibernateDao<PriceListProductGroup2Customer, String> {
	@Deprecated
	PriceListProductGroup2Customer findOneByCustomerAndProductGroup(Customer customer, ProductGroup group);
	List<PriceListProductGroup2Customer> findAllByCustomerAndProductGroup(Customer customer, ProductGroup group);
}
