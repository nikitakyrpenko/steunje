package com.negeso.module.webshop.service;

import com.negeso.module.webshop.dao.MatrixCategoryDao;
import com.negeso.module.webshop.entity.MatrixCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatrixCategoryService {

	private MatrixCategoryDao matrixCategoryDao;

	@Autowired
	public MatrixCategoryService(MatrixCategoryDao matrixCategoryDao) {
		this.matrixCategoryDao = matrixCategoryDao;
	}

	public List<MatrixCategory> list(){
		return matrixCategoryDao.findAll();
	}

	public List<String> primaryKeys() {
		return matrixCategoryDao.primaryKeys();
	}
}

