package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.HibernateDao;
import com.negeso.module.webshop.entity.MatrixCategory;

import java.util.List;

public interface MatrixCategoryDao extends HibernateDao<MatrixCategory, String>{
	List<String> primaryKeys();
}
