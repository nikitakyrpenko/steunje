package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.AbstractHibernateDao;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.PriceListProductGroup2Customer;
import com.negeso.module.webshop.entity.ProductGroup;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceListProductGroup2CustomerDaoImpl extends AbstractHibernateDao<PriceListProductGroup2Customer, String> implements PriceListProductGroup2CustomerDao {
	public PriceListProductGroup2CustomerDaoImpl(){
		super(PriceListProductGroup2Customer.class);
	}

	public PriceListProductGroup2Customer findOneByCustomerAndProductGroup(Customer customer, ProductGroup group){
		return (PriceListProductGroup2Customer) super.createCriteria()
				.add(Restrictions.eq("customer", customer))
				.add(Restrictions.eq("productGroup", group))
				.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PriceListProductGroup2Customer> findAllByCustomerAndProductGroup(Customer customer, ProductGroup group) {
		return (List<PriceListProductGroup2Customer>) super.createCriteria()
				.add(Restrictions.eq("customer", customer))
				.add(Restrictions.eq("productGroup", group))
				.addOrder(Order.desc("sinceCount"))
				.list();
	}
}
