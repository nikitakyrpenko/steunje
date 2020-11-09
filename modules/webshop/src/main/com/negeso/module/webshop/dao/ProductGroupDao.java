package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.HibernateDao;
import com.negeso.module.webshop.entity.ProductGroup;

import java.util.List;

public interface ProductGroupDao extends HibernateDao<ProductGroup, String> {
	List<String> primaryKeys();
}
