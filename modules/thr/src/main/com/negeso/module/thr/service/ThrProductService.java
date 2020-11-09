package com.negeso.module.thr.service;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.module.thr.bo.ThrProduct;
import com.negeso.module.thr.dao.ThrProductDao;

public class ThrProductService {
	private static final String ORDER_NUMBER = "orderNumber";
	private ThrProductDao thrProductDao;

	public ThrProduct findById(Long id) {
		return thrProductDao.read(id);
	}

	public List<ThrProduct> list() {
		return thrProductDao.readByCriteriaSorted(Order.asc(ORDER_NUMBER));
	}

	@Transactional
	public void createOrUpdate(ThrProduct thrProduct) {
		thrProductDao.createOrUpdate(thrProduct);
	}

	@Transactional
	public void delete(ThrProduct thrProduct) {
		thrProductDao.delete(thrProduct);
		List<ThrProduct> list =  thrProductDao.readByCriteriaSorted(Order.asc(ORDER_NUMBER), Restrictions.gt(ORDER_NUMBER, thrProduct.getOrderNumber()));
		for (ThrProduct product : list) {
			product.setOrderNumber(product.getOrderNumber() - 1);
		}
		thrProductDao.updateAll(list);
	}

	public void setThrProductDao(ThrProductDao thrProductDao) {
		this.thrProductDao = thrProductDao;
	}

	public Integer getNextOrderNumber() {
		List<ThrProduct> list =  thrProductDao.readByCriteriaSorted(Order.desc(ORDER_NUMBER));
		if (!list.isEmpty()) {
			return list.get(0).getOrderNumber() + 1;
		}
		return 0;
	}
	
	@Transactional
	public void moveUp(ThrProduct thrProduct) {
		Integer curOrderNumber = thrProduct.getOrderNumber();
		List<ThrProduct> list =  thrProductDao.readByCriteriaSorted(Order.desc(ORDER_NUMBER), Restrictions.lt(ORDER_NUMBER, curOrderNumber));
		if (!list.isEmpty()) {
			thrProduct.setOrderNumber(list.get(0).getOrderNumber());
			list.get(0).setOrderNumber(curOrderNumber);
			thrProductDao.createOrUpdate(thrProduct);
			thrProductDao.createOrUpdate(list.get(0));
		}
	}
	
	@Transactional
	public void moveDown(ThrProduct thrProduct) {
		Integer curOrderNumber = thrProduct.getOrderNumber();
		List<ThrProduct> list =  thrProductDao.readByCriteriaSorted(Order.asc(ORDER_NUMBER), Restrictions.gt(ORDER_NUMBER, curOrderNumber));
		if (!list.isEmpty()) {
			thrProduct.setOrderNumber(list.get(0).getOrderNumber());
			list.get(0).setOrderNumber(curOrderNumber);
			thrProductDao.createOrUpdate(thrProduct);
			thrProductDao.createOrUpdate(list.get(0));
		}
	} 
}
