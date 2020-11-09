package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.HibernateDao;
import com.negeso.framework.domain.User;
import com.negeso.module.webshop.entity.Customer;

import java.util.List;

public interface CustomerDao extends HibernateDao<Customer, String> {
	Customer findByLoginJoinContacts(String login);
	Customer findByUserJoinWishlist(User user);
	List<String> loginList();
	Customer merge(Customer customer);
}
