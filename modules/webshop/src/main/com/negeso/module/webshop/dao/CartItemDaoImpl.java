package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.AbstractHibernateDao;
import com.negeso.module.webshop.entity.CartItem;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class CartItemDaoImpl extends AbstractHibernateDao<CartItem, Integer> implements CartItemDao {
	public CartItemDaoImpl() {
		super(CartItem.class);
	}

	@Override
	public CartItem findOneByProductIdAndCustomerId(String productId, String customerId) {
		return (CartItem) super.createCriteria()
				.createAlias("product", "product")
				.add(Restrictions.eq("product.id", productId))
				.add(Restrictions.eq("cartOwner", customerId))
				.uniqueResult()
				;
	}

	@Override
	public void deleteOneByProductIdAndCustomerId(String productId, String customerId) {
		CartItem item = this.findOneByProductIdAndCustomerId(productId, customerId);
		super.delete(item);
	}

	@Override
	public void deleteAllByCustomerId(String userCode) {
		String query = "delete from CartItem item where item.cartOwner = ?";
		super.getCurrentSession()
				.createQuery(query)
				.setParameter(0, userCode)
				.executeUpdate()
				;
	}
}
