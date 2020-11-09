package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.HibernateDao;
import com.negeso.module.webshop.bo.JsonResponseOrder;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.Order;
import com.negeso.module.webshop.entity.Product;

import java.util.List;

public interface OrderDao extends HibernateDao<Order, Integer> {
	Integer getNextSeq();
	List<Order> findAllByCustomer(Customer customer);
	Order findOneByCustomer(Integer id, Customer customer);
	Order findOneByIdealId(String idealTransactionId);

	Order findOneForResponse(Integer id);

	Order findOneByProducts(Customer customer, List<Product> products);

	List<Order> findAllOpenedByCustomer(Customer customer);
}
