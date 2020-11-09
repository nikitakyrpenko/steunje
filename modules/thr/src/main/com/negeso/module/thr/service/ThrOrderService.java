package com.negeso.module.thr.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import com.negeso.module.thr.bo.ThrJsonConsts;
import com.negeso.module.thr.bo.ThrOrder;
import com.negeso.module.thr.dao.ThrOrderDao;
import com.negeso.module.thr.filter.OrderFilter;

public class ThrOrderService {
	private ThrOrderDao thrOrderDao;
	
	@Transactional
	public void createOrUpdate(ThrOrder order) {
		thrOrderDao.createOrUpdate(order);
	}
	
	public void setThrOrderDao(ThrOrderDao thrOrderDao) {
		this.thrOrderDao = thrOrderDao;
	}

	public ThrOrder findByOrderNumber(String orderNumber) {
		List<ThrOrder> list = thrOrderDao.readByCriteria(Restrictions.eq(ThrJsonConsts.THR_JSON_ORDER_NUMBER, orderNumber));
		return list.isEmpty() ? null : list.get(0);
	}
	
	public List<String> listLogins() {
		return thrOrderDao.listLogins();
	}
	
	public ThrOrder findById(Long id) {
		return thrOrderDao.read(id);
	}

	public List<ThrOrder> list(OrderFilter filter) {
		Criteria criteria = thrOrderDao.getCriteria();
		if (filter.getStartDate() != null) {
			criteria.add(Restrictions.ge("date", filter.getStartDate()));
		}
		if (filter.getEndDate() != null) {
			criteria.add(Restrictions.lt("date", filter.getEndDate()));
		}
		if (StringUtils.isNotBlank(filter.getLogin())) {
			criteria.add(Restrictions.eq("login", filter.getLogin()));
		}
		if (StringUtils.isNotBlank(filter.getBarCode())) {
			criteria.createAlias("products", "products");
			criteria.add(Restrictions.like("products.barCode", filter.getBarCode(), MatchMode.START));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		}
		criteria.addOrder(Order.desc("date"));
		return criteria.list();
	}
}
