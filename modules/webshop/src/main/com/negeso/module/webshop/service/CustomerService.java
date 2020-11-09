package com.negeso.module.webshop.service;

import com.negeso.framework.domain.User;
import com.negeso.module.webshop.dao.CustomerDao;
import com.negeso.module.webshop.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

	private CustomerDao customerDao;

	public CustomerService(){}

	@Autowired
	public CustomerService(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	public Customer findByLogin(String login){
		return customerDao.findOne(login);
	}

	public Customer findByUser(User user){
		return customerDao.findOne(user.getLogin());
	}

	public Customer findByUserJoinContacts(String login) {
		return customerDao.findByLoginJoinContacts(login);
	}

	public Customer findByUserJoinContacts(User user) {
		return customerDao.findByLoginJoinContacts(user.getLogin());
	}

	@Transactional
	public void update(Customer customer) {
		customerDao.update(customer);
	}

	public void create(Customer customer) {
		customerDao.save(customer);
	}

	@Transactional
	public void createSafety(Customer customer) {
		customerDao.merge(customer);
	}

	public Customer findByUserJoinWishlist(User user) {
		return customerDao.findByUserJoinWishlist(user);
	}

	public List<Customer> list() {
		return customerDao.findAll();
	}

	@Transactional
	public List<String> loginList() {
		return customerDao.loginList();
	}

	public void delete(Customer customer) {
		customerDao.delete(customer);
	}
}
