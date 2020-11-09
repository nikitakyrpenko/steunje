package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.AbstractHibernateDao;
import com.negeso.framework.domain.User;
import com.negeso.module.webshop.entity.Customer;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerDaoImpl extends AbstractHibernateDao<Customer, String> implements CustomerDao {
	public CustomerDaoImpl() {
		super(Customer.class);
	}

	@Override
	public Customer findByLoginJoinContacts(String login) {
		return (Customer) super.createCriteria()
				.setFetchMode("shippingContact", FetchMode.JOIN)
				.setFetchMode("billingContact", FetchMode.JOIN)
				.setFetchMode("priceGroup", FetchMode.JOIN)
				.add(Restrictions.eq("userCode", login))
				.uniqueResult()
				;
	}

	@Override
	public Customer findByUserJoinWishlist(User user) {
		return (Customer) super.createCriteria()
				.setFetchMode("wishProducts", FetchMode.JOIN)
				.add(Restrictions.eq("userCode", user.getLogin()))
				.uniqueResult()
		;
	}

	@Override
	public List<String> loginList() {
		return (List<String>) super.getCurrentSession().createQuery("SELECT c.id FROM Customer c").list();
	}

	@Override
	public Customer merge(Customer customer) {
		return (Customer) super.getCurrentSession().merge(customer);
	}
}
