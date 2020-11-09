package com.negeso.module.webshop.service;

import com.negeso.framework.Env;
import com.negeso.module.webshop.dao.ProductDao;
import com.negeso.module.webshop.entity.Product;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
public class ProductService {

	private final static String FILE_SEPARATOR = System.getProperty("file.separator");
	private ProductDao productDao;

	public ProductService() {
	}

	@Autowired
	public ProductService(ProductDao productDao) {
		this.productDao = productDao;
	}

	public Product findOne(String id) {
		return this.productDao.findOne(id);
	}

	public Product findOneJoinDiscount(String id) {
		return productDao.findOneJoinDiscount(id);
	}

	@Transactional
	public void insert(Product product) {
		productDao.save(product);
	}

	public Session createSession() {
		return productDao.createSession();
	}

	public List<Product> listByEANCodes(String[] codes) {
		return productDao.listByEANCodes(codes);
	}

	public Product findOneByEAN(String code) {
		return productDao.findOneByEAN(code);
	}

	@Transactional
	public Product update(Product product) {
		return productDao.update(product);
	}

	@Transactional
	public void deleteByCode(String orderCode) {
		productDao.deleteByPrimaryKey(orderCode);
	}
	@Transactional
	public void delete(Product product) {
		productDao.delete(product);
	}

	public List<Product> listSales() {
		return productDao.listSales();
	}

	public void updateIgnorableFields(Product product) {
		productDao.updateIgnorableFields(product);
	}

	//todo use resolve path first
	public String getImageUrl(Product product) {
		return Env.getCommonHostName() + "media/productsImages/" + product.getId() + ".jpg";
	}

	public String resolveImagePath(Product product){
		if (product == null) return null;
		return this.resolveImagePath(product.getId());
	}

	public String resolveImagePath(String id){
		if (id == null) return null;
		String[] extensions = {"jpg", "png", "jpeg"};
		try {
			String imagePath = Env.getRealPath("customers/site/media/productsImages") + FILE_SEPARATOR + id + ".";
			String temp;
			for (String extension : extensions) {
				temp = imagePath + extension;
				File file = new File(temp);
				if (file.exists()){
					return temp;
				}
			}
		} catch (Exception e) {
			return Env.getCommonHostName() + "media/productsImages/" + id + ".jpg";
		}
		return null;
	}

	public String resolveImageUri(Product product){
		if (product == null) return null;
		return this.resolveImageUri(product.getId());
	}

	public String resolveImageUri(String id){
		String imagePath = this.resolveImagePath(id);
		return imagePath != null ? imagePath.substring(imagePath.indexOf(FILE_SEPARATOR + "media")).replace("\\", "/") : null;
	}

	@Transactional
	public List<String> listIds() {
		return productDao.listIds();
	}

	@Transactional
	public List<Product> listProductIds(){
		return productDao.listProductIds();
	}
}
