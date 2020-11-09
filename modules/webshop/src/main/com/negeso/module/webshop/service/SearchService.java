package com.negeso.module.webshop.service;

import com.negeso.module.search.SearchProducer;
import com.negeso.module.webshop.search.CategorySearchProducer;
import com.negeso.module.webshop.search.ProductSearchProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SearchService {

	@Autowired
	private ProductService productService;
	@Autowired
	private ProductCategoryService productCategoryService;

	@PostConstruct
	public void init(){
		ProductSearchProducer productSearchProducer = new ProductSearchProducer();
		productSearchProducer.setProductService(productService);
		SearchProducer.getSearchableList().add(productSearchProducer);

		CategorySearchProducer categorySearchProducer = new CategorySearchProducer();
		categorySearchProducer.setProductCategoryService(productCategoryService);
		SearchProducer.getSearchableList().add(categorySearchProducer);
	}
}
