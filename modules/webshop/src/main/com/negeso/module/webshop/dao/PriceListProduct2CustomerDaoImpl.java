package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.AbstractHibernateDao;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.PriceListProduct2Customer;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceListProduct2CustomerDaoImpl extends AbstractHibernateDao<PriceListProduct2Customer, String> implements PriceListProduct2CustomerDao {

	public PriceListProduct2CustomerDaoImpl() {
		super(PriceListProduct2Customer.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PriceListProduct2Customer> findAllByCustomerAndProduct(Customer customer, String productId) {
		return (List<PriceListProduct2Customer>) super.createCriteria()
				.add(Restrictions.eq("customer", customer))
				.add(Restrictions.eq("productId", productId))
				.addOrder(Order.desc("sinceCount"))
				.list()
				;
	}
}
