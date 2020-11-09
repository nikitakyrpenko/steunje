package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.AbstractHibernateDao;
import com.negeso.module.webshop.entity.ProductCategory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;

@Repository
public class ProductCategoryDaoImpl extends AbstractHibernateDao<ProductCategory, String> implements ProductCategoryDao {
	public ProductCategoryDaoImpl() {
		super(ProductCategory.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public ProductCategory findOneLastRootCategory() {

		List<ProductCategory> productCategory = (List<ProductCategory>) super.createCriteria()
				.setFetchMode("childCategories", FetchMode.JOIN)
				.add(Restrictions.isNull("parentCategory"))
				.addOrder(Order.desc("creationDate"))
				.addOrder(Order.asc("title"))
				.list();

		ProductCategory c = new ProductCategory("Unnecessary");
		c.setChildCategories(new LinkedHashSet<ProductCategory>(productCategory));

		return c;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ProductCategory> findAllRootCategories() {
		return (List<ProductCategory>) super.createCriteria()
				.add(Restrictions.isNull("parentCategory"))
//				.add(Restrictions.eq("visible", Boolean.TRUE))
				.addOrder(Order.asc("orderNumber"))
				.addOrder(Order.asc("title"))
				.addOrder(Order.desc("creationDate"))
				.list();
	}

	@Override
	public List<ProductCategory> findBrothers(ProductCategory category) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProductCategory> findBrothers(String id) {
		ProductCategory one = super.findOne(id);

		return super.createCriteria()
				.add(Restrictions.eq("parentCategory", one.getParentCategory()))
				.list()
				;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProductCategory> findAllByParentName(String name) {
		return (List<ProductCategory>) super.createCriteria()
				.add(Restrictions.eq("parentCategory.id", name))
				.addOrder(Order.asc("orderNumber"))
				.addOrder(Order.asc("title"))
				.addOrder(Order.desc("creationDate"))
				.list();
	}

	@Override
	public ProductCategory findOneJoinProducts(String categoryId) {
		return (ProductCategory) super.createCriteria()
				.createAlias("products", "products", Criteria.LEFT_JOIN)
				.setFetchMode("products", FetchMode.JOIN)
				.add(Restrictions.eq("id", categoryId))
				.addOrder(Order.asc("products.orderNumber"))
				.uniqueResult()
				;
	}

}
