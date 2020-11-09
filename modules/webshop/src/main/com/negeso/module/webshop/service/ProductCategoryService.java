package com.negeso.module.webshop.service;

import com.negeso.framework.Env;
import com.negeso.module.webshop.dao.ProductCategoryDao;
import com.negeso.module.webshop.entity.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.util.List;

@Service
public class ProductCategoryService {

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	private ProductCategoryDao productCategoryDao;

	public ProductCategoryService() {
	}

	@Autowired
	public ProductCategoryService(ProductCategoryDao productCategoryDao) {
		this.productCategoryDao = productCategoryDao;
	}

	public List<ProductCategory> findAll() {
		return this.findAllRootCategories(false);
	}

	public ProductCategory findOne(String id) {
		return productCategoryDao.findOne(id);
	}

	public ProductCategory findOneByLastImportDate() {
		return productCategoryDao.findOneLastRootCategory();
	}

	public List<ProductCategory> loadBrothers(String id) {
		return productCategoryDao.findBrothers(id);
	}

	public List<ProductCategory> findAllRootCategories(boolean rootOnly) {
		if (rootOnly)
			return productCategoryDao.findAllRootCategories();
		else
			return productCategoryDao.findAll();
	}

	public void insert(ProductCategory root) {
		productCategoryDao.save(root);
	}

	public List<ProductCategory> findAllByParent(String name) {
		return productCategoryDao.findAllByParentName(name);
	}

	public void deleteById(String name) {
		productCategoryDao.deleteByPrimaryKey(name);
	}

	public void delete(ProductCategory one) {
		productCategoryDao.delete(one);
	}

	public ProductCategory findOneJoinProducts(String categoryId) {
		return productCategoryDao.findOneJoinProducts(categoryId);
	}

	@Transactional
	public void changeDirection(String id, String direction) {
		Assert.notNull(id);
		Assert.notNull(direction);

		ProductCategory category = productCategoryDao.findOne(id);
		if (category != null) {
			List<ProductCategory> allByParentName = category.getParentCategory() == null
					? productCategoryDao.findAllRootCategories()
					: productCategoryDao.findAllByParentName(category.getParentCategory().getId());
			boolean skip = false;
			for (int i = 0; i < allByParentName.size(); i++) {
				ProductCategory c = allByParentName.get(i);
				if (c.getId().equals(id)) {
					if (direction.equals("UP")) {
						c.setOrderNumber(i - 1);
						allByParentName.get(i - 1).setOrderNumber(i);
					} else {
						c.setOrderNumber(i + 1);
						allByParentName.get(i + 1).setOrderNumber(i);
						skip = true;
					}
				} else {
					if (skip) {
						skip = false;
					} else {
						c.setOrderNumber(i);
					}
				}
			}

		}
	}

	public String resolveImagePath(String id){
		if (id == null) return null;
		String[] extensions = {"png", "jpg", "jpeg"};
		try {
			String imagePath = Env.getRealPath("customers/site/media/productsCategory") + FILE_SEPARATOR + id + ".";
			String temp;
			for (String extension : extensions) {
				temp = imagePath + extension;
				File file = new File(temp);
				if (file.exists()){
					return temp;
				}
			}
		} catch (Exception e) {
			return Env.getCommonHostName() + "media/productsCategory/" + id + ".png";
		}
		return null;
	}

	public String resolveImageUri(String id) {
		String imagePath = this.resolveImagePath(id);
		return imagePath != null ? imagePath.substring(imagePath.indexOf(FILE_SEPARATOR + "media")).replace("\\", "/") : null;
	}
}
