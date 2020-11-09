package com.negeso.module.webshop.service;

import com.negeso.module.webshop.dao.CartItemDao;
import com.negeso.module.webshop.entity.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartItemService {
	public enum QuantityStrategy {ADD, REPLACE}
	private CartItemDao cartItemDao;

	public CartItemService() {
	}

	@Autowired
	public CartItemService(CartItemDao cartItemDao) {
		this.cartItemDao = cartItemDao;
	}

	@Transactional
	public void create(CartItem item){
		cartItemDao.save(item);
	}

	@Transactional
	public void deleteOneByProductIdAndCustomerId(String productId, String customerId){
		this.cartItemDao.deleteOneByProductIdAndCustomerId(productId, customerId);
	}

	@Transactional
	public void deleteAllByCustomerId(String userCode) {
		this.cartItemDao.deleteAllByCustomerId(userCode);
	}

	@Transactional
	public CartItem findOneByProductIdAndCustomerId(String productId, String customerId) {
		return cartItemDao.findOneByProductIdAndCustomerId(productId, customerId);
	}

	@Transactional
	public void update(CartItem item) {
		cartItemDao.update(item);
	}

	public Integer calculateQuantity(Integer oldQuantity, Integer newQuantity, QuantityStrategy strategy) {
		return strategy == null || strategy == QuantityStrategy.REPLACE ? newQuantity : newQuantity + oldQuantity;
	}
}
