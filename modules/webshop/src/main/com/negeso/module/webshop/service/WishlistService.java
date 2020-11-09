package com.negeso.module.webshop.service;

import com.negeso.module.webshop.dao.WishlistDao;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.Product;
import com.negeso.module.webshop.entity.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService {

	private WishlistDao wishlistDao;

	public WishlistService(){}
	@Autowired
	public WishlistService(WishlistDao wishlistDao) {
		this.wishlistDao = wishlistDao;
	}

	@Transactional
	public void create(Wishlist wishlist) {
		wishlistDao.save(wishlist);
	}

	@Transactional
	public void delete(Wishlist wishlist) {
		wishlistDao.delete(wishlist);
	}

	@Transactional
	public void deleteBy(Customer customer, Product product) {
		wishlistDao.deleteBy(customer, product);
	}

	@Transactional
	public Wishlist findOneByCustomerAndProduct(Customer customer, Product product) {
		return wishlistDao.findOneByCustomerAndProduct(customer, product);
	}
}
