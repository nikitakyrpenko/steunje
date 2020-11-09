package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.AbstractHibernateDao;
import com.negeso.module.webshop.entity.ProductGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductGroupDaoImpl extends AbstractHibernateDao<ProductGroup, String> implements ProductGroupDao {

	public ProductGroupDaoImpl() {
		super(ProductGroup.class);
	}

	@Override
	public List<String> primaryKeys() {
		return (List<String>) super.getCurrentSession().createQuery("SELECT table.group FROM ProductGroup table order by table.group").list();
	}
}
