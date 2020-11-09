package com.negeso.module.webshop.service;

import com.negeso.module.webshop.dao.ProductGroupDao;
import com.negeso.module.webshop.entity.ProductGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductGroupService {

	private ProductGroupDao productGroupDao;

	@Autowired
	public ProductGroupService(ProductGroupDao productGroupDao) {
		this.productGroupDao = productGroupDao;
	}

	public List<ProductGroup> list(){
		return productGroupDao.findAll();
	}

	public List<String> primaryKeys() {
		return productGroupDao.primaryKeys();
	}
}
