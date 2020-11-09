package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.AbstractHibernateDao;
import com.negeso.module.webshop.entity.MatrixCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MatrixCategoryDaoImpl extends AbstractHibernateDao<MatrixCategory, String> implements MatrixCategoryDao {
	public MatrixCategoryDaoImpl() {
		super(MatrixCategory.class);
	}

	@Override
	public List<String> primaryKeys() {
		return (List<String>) super.getCurrentSession().createQuery("SELECT table.title FROM MatrixCategory table order by table.title").list();
	}
}
