package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.HibernateDao;
import com.negeso.module.webshop.entity.ProductCategory;

import java.util.List;

public interface ProductCategoryDao extends HibernateDao<ProductCategory, String> {
	ProductCategory findOneLastRootCategory();
	List<ProductCategory> findAllRootCategories();
	List<ProductCategory> findBrothers(ProductCategory category);
	List<ProductCategory> findBrothers(String id);
	List<ProductCategory> findAllByParentName(String name);
	ProductCategory findOneJoinProducts(String categoryId);
}
