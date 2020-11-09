package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.AbstractHibernateDao;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.Product;
import com.negeso.module.webshop.entity.Wishlist;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class WishlistDaoImpl extends AbstractHibernateDao<Wishlist, Integer> implements WishlistDao {

	public WishlistDaoImpl() {
		super(Wishlist.class);
	}


	@Override
	public void deleteBy(Customer customer, Product product) {
		Wishlist wishlist = (Wishlist) super.createCriteria()
				.add(Restrictions.eq("customer", customer))
				.add(Restrictions.eq("product", product))
				.uniqueResult();

		super.delete(wishlist);
	}

	@Override
	public Wishlist findOneByCustomerAndProduct(Customer customer, Product product) {
		return (Wishlist) super.createCriteria()
				.add(Restrictions.eq("customer", customer))
				.add(Restrictions.eq("product", product))
				.uniqueResult();
	}
}
