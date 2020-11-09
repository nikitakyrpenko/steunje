/*
 * @(#)$Id: CategoryService.java,v 1.5, 2007-02-06 13:26:39Z, Vyacheslav Zapadnyuk$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.lite_event;


import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import com.negeso.framework.Env;
import com.negeso.module.lite_event.domain.Category;

/**
 * 
 * @TODO
 * 
 * @author		Vyacheslav Zapadnyuk
 * @version		$Revision: 6$
 *
 */
public class CategoryService extends HibernateDaoSupport {
	
    private static Logger logger = Logger.getLogger(CategoryService.class);
    
	public List<Category> getCategories() {
    	logger.debug("+-");
    	List<Category> tmp =  (List<Category>) this.getHibernateTemplate().find( 
    		"from Category c where c.siteId = " + Env.getSiteId() + 
    		" order by c.orderNumber ASC" 
    	); 
    	logger.debug("tmp = " + tmp.size());
    	return tmp;
	}

	public List<Category> getTopCategories() {
		logger.debug("+-");
		List<Category> tmp =  (List<Category>) this.getHibernateTemplate().find( 
				"from Category c where c.siteId = " + Env.getSiteId() +
				"and c.parentCategory is null" +
				" order by c.orderNumber ASC" 
		); 
		logger.debug("tmp = " + tmp.size());
		return tmp;
	}

	public void deleteCategory(Long categoryId) {
    	logger.debug("+-");
    	this.getHibernateTemplate().delete(
    			this.getHibernateTemplate().load(Category.class, new Long(categoryId)));
	}

	public Category getCategory(Long categoryId) {
		logger.debug("+-");
		return (Category) this.getHibernateTemplate().load(Category.class, categoryId);
	}

	public Category getParentCategory(Long categoryId) {
		logger.debug("+-");
		return (Category) this.getHibernateTemplate().load(Category.class, categoryId);
	}
	
	public Category updateCategory(Category category) {
		logger.debug("+");
		this.getHibernateTemplate().update(category);	
		logger.debug("-");		
		return category;
	}
	
	public Category saveCategory(Category c) {
		this.getHibernateTemplate().save(c);
		return c;
	}
	
	public void changeOrder(Long categoryId, String orderDirection) {
		logger.debug("+");
		Category category = (Category) this.getHibernateTemplate().load(Category.class, categoryId);
		Long parentCategoryId = category.getParentCategory()==null?null:category.getParentCategory().getId(); 
		Integer newOrderNumber = category.getOrderNumber();
		if( CategoriesListController.ORDER_DOWN.equalsIgnoreCase(orderDirection) ) {
			if(category.getOrderNumber() <= findMaxOrderNumber(parentCategoryId) ){
				do {
					newOrderNumber++;
				} while(!isExistingOrderNumber(parentCategoryId, newOrderNumber)
						&& changeOthersItemsOrder(parentCategoryId, category.getOrderNumber(), newOrderNumber));
			} else {
				logger.debug("-");
				return;
			}
		} else if(CategoriesListController.ORDER_UP.equalsIgnoreCase(orderDirection)) {
			if( category.getOrderNumber() >= findMinOrderNumber(parentCategoryId) ) {
				do {
					newOrderNumber--;
				} while(!isExistingOrderNumber(parentCategoryId, newOrderNumber)
						&& changeOthersItemsOrder(parentCategoryId, category.getOrderNumber(), newOrderNumber));
			} else {
				logger.debug("-");
				return;
			}
		} else {
			logger.debug("-");
			return;
		}
		changeOthersItemsOrder(parentCategoryId, category.getOrderNumber(), newOrderNumber);
		category.setOrderNumber(newOrderNumber);
		this.getHibernateTemplate().update(category);
		logger.debug("-");		
	}
	
	public Integer findMaxOrderNumber(Long parentCategoryId) {
		logger.debug("+");
		Integer max = (Integer) this.getHibernateTemplate().find( 
				"select max(c.orderNumber) from Category c where c.parentCategory.id = " + parentCategoryId
		).get(0);
		if(max == null) {
			max = 0;
		}
		logger.debug("-" + " max="+max);
		return max;
	}
	
	public Integer findMinOrderNumber(Long parentCategoryId) {
		logger.debug("+");
		Integer min = (Integer) this.getHibernateTemplate().find( 
				"select min(c.orderNumber) from Category c where c.parentCategory.id = " + parentCategoryId
		).get(0);
		logger.debug("-" + " min="+min);
		return min;
	}

	public boolean isExistingOrderNumber(Long parentCategoryId, Integer orderNumber) {
		logger.debug("+");
		List arr = (List) this.getHibernateTemplate().find( 
				"select c.id from Category c where c.parentCategory.id = " + parentCategoryId +
				" AND c.orderNumber = " + orderNumber
		);
		if(arr.size() > 0) {
			logger.debug("- true");
			return true;
		}
		logger.debug("- false");
		return false;
	}

	public boolean changeOthersItemsOrder(Long parentCategoryId, Integer currentOrderNumber, Integer newOrderNumber) {
		logger.debug("+");
		List <Category> categories = this.getHibernateTemplate().find(
				"from Category c where c.parentCategory.id = " + parentCategoryId +
				" and c.orderNumber" + " = " + newOrderNumber +
				" order by order_number ASC");
		if(categories.size()==0) {
			logger.debug("- no other categories with the same ordernumber");
			return false;
		} else {
			for(Category category: categories) {
				category.setOrderNumber(currentOrderNumber);
				this.getHibernateTemplate().update(category);
			}
		}
		logger.debug("-");
		return true;
	}
	
}