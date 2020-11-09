/*
* @(#)$Id: CategoriesListController.java,v 1.6, 2007-02-07 11:22:03Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.Env;
import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.lite_event.domain.Category;


/**
 *
 * TODO
 * 
 * @version                $Revision: 7$
 * @author                 sbondar
 * 
 */
public class CategoriesListController extends AbstractController {

	private static Logger logger = Logger.getLogger(CategoriesListController.class);
    
	public static String CATEGORY_ID = "categoryId";
	public static String MODE = "mode";
	/*
	?mode=add
	?mode=delete
	?mode=orderUp
	?mode=orderDown
	?mode=save
	?mode=saveAndClose
	?mode=details
	?mode=... blank for subcategories when the request has &category=...
	*/
	public static String TITLE = "title_";
    public static String IS_LEAF = "isLeaf";
    public static String PUBLISH_DATE = "publishDate";
    public static String EXPIRED_DATE = "expiredDate";
    public static String ORDER_UP = "orderUp";
    public static String ORDER_DOWN = "orderDown";
    public static String PARENT_CATEGORY_ID = "parentId";
    
	private CategoryService categoryService;
	
//	private DatabaseResourceBundle messageSource;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("+");   

		request.setAttribute("languageFilter", Language.getItems());     
		
		Category category = null;
		if(			(request.getParameter(CATEGORY_ID)!=null) 
				 && (!"".equals(request.getParameter(CATEGORY_ID))
				 && (0 < new Long(request.getParameter(CATEGORY_ID)))
			)
			) {
			category = categoryService.getCategory(new Long(request.getParameter(CATEGORY_ID)));
		}
		
		if(        "details".equalsIgnoreCase(request.getParameter(MODE)) 
				|| "save".equalsIgnoreCase(request.getParameter(MODE))
				|| "add".equalsIgnoreCase(request.getParameter(MODE))) {
			
			if ("add".equalsIgnoreCase(request.getParameter(MODE))) {
				category = createNewCategory(category);
			} else if ("save".equalsIgnoreCase(request.getParameter(MODE))) {
				category = doSaveCategory(request, category);
			}
			logger.debug("-");
			return viewCategory(request, category);
		}

		if("saveAndClose".equalsIgnoreCase(request.getParameter(MODE))) {
			category = doSaveCategory(request, category);
			category = category.getParentCategory();
		}
		
		if ( (category != null) &&
				(  ORDER_UP.equalsIgnoreCase(request.getParameter(MODE))
				|| ORDER_DOWN.equals(request.getParameter(MODE))) ) {			
			categoryService.changeOrder( 
					category.getId(), request.getParameter(CategoriesListController.MODE) );
			if(category.getParentCategory() == null) {
				category = null;
			} else {
				category = category.getParentCategory();
			}
		} else if ( (category != null) && ("delete".equalsIgnoreCase(request.getParameter(MODE)))) {
			Long deleteId = category.getId();
			category = category.getParentCategory();
			categoryService.deleteCategory( deleteId );
		}			
		if(category != null) {
			request.setAttribute("currentCategory", category);
			logger.debug("-");
			return browseCategory(request, category);
		}
		logger.debug("-");
		return browseTopCategories(request, categoryService);
	}

	public static ModelAndView browseTopCategories(HttpServletRequest request, CategoryService categoryService) {
		logger.debug("+-");       		
		RequestUtil.getHistoryStack(request).push( new Link("TOP_CATEGORIES.link", "/admin/categorieslist.html", 0) );
		return new ModelAndView("em_categories", "categories", categoryService.getTopCategories());
	}

	public static ModelAndView browseCategory(HttpServletRequest request, Category category) {
		logger.debug("+-");       		
		RequestUtil.getHistoryStack(request).push( new Link(
				category.getDefaultTitle(), 
				"/admin/categorieslist.html?mode=&categoryId="+category.getId(), 
				false, 
				category.getLevel() * 2 + 1
			) );
		return new ModelAndView("em_categories", "categories", category.getSubCategories());
	}
	
	public static ModelAndView viewCategory(HttpServletRequest request, Category category) {
		logger.debug("+-");       		
		if(category.getId()==null) {
			RequestUtil.getHistoryStack(request).push( new Link(
						"NEW_CATEGORY.link", 
						"/admin/category_details.html?mode=details", 
						category.getLevel() * 2)
					);
		} else {
			RequestUtil.getHistoryStack(request).push( new Link(
					category.getDefaultTitle(), 
					"/admin/category_details.html?mode=details&categoryId="+category.getId().toString(), 
					false, 
					category.getLevel() * 2
			) );
		}
		return new ModelAndView("em_category_details", "category", category);
	}
	
	
	public CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public Category createNewCategory(Category parentCategory) {
		Category newCategory = new Category();
		newCategory.setSiteId(Env.getSiteId());
		newCategory.setPublishDate(new Date());
		newCategory.setExpiredDate(
				parentCategory==null ? null : parentCategory.getExpiredDate()
			);
		newCategory.setOrderNumber(
				categoryService.findMaxOrderNumber( parentCategory==null?null:parentCategory.getId() )+1);
		newCategory.setParentCategory(parentCategory);
		newCategory.setLeaf(false);

		for (Language lang: Language.getItems()) {
			newCategory.getTitles().put(lang.getId(), "");
		}

		return newCategory;
	}
	public Category loadCategoryFromRequest(HttpServletRequest request, Category category) {
		logger.debug("+");
		if( (request.getParameter(IS_LEAF)!=null)
				&&("on".equalsIgnoreCase(request.getParameter(IS_LEAF))
					|| "true".equalsIgnoreCase(request.getParameter(IS_LEAF)))) {
				category.setLeaf(true);
			} else {
				category.setLeaf(false);
			}
		if(StringUtils.isNotBlank(request.getParameter(PUBLISH_DATE))) {
			try {
				category.setPublishDate(Env.parseSimpleRoundDate(request.getParameter(PUBLISH_DATE)));
			} catch(ParseException e) {
				logger.error("Can not parse " + PUBLISH_DATE + " = " + request.getParameter(PUBLISH_DATE) + "; "+ e);
			}
		} else {
			category.setPublishDate(null);
		}
		if(StringUtils.isNotBlank(request.getParameter(EXPIRED_DATE))) {
			try {
				category.setExpiredDate(Env.parseSimpleRoundDate(request.getParameter(EXPIRED_DATE)));
			} catch(ParseException e) {
				logger.error("Can not parse " + EXPIRED_DATE + ": " + request.getParameter(EXPIRED_DATE) + "; "+ e);
			}
		} else {
			category.setExpiredDate(null);
		}

		if(	(request.getParameter(PARENT_CATEGORY_ID)!=null) 
					&& (!"".equals(request.getParameter(PARENT_CATEGORY_ID))
				)
			) {
			try {
				category.setParentCategory(categoryService.getCategory(
						new Long(request.getParameter(PARENT_CATEGORY_ID)))
					);
			} catch(Exception e) {
				logger.error(e);
			}
		}
		
		for (Language lang: Language.getItems()) {
			if(request.getParameter(TITLE+lang.getCode())!=null) {
				category.getTitles().put(lang.getId(), request.getParameter(TITLE+lang.getCode()));
			} else {
				category.getTitles().put(lang.getId(), "");
			}
		}

		category.setSiteId(Env.getSiteId());
		
		logger.debug("-");
		return category;
	}
	
	private Category doSaveCategory(HttpServletRequest request, Category category) {
		logger.debug("+");
		Session session = this.categoryService.getHibernateTemplate().getSessionFactory().getCurrentSession();
		Transaction transaction = null;
	    try {
	           transaction = session.beginTransaction();
	           if(category==null) {
		   			category = categoryService.saveCategory(
		   						loadCategoryFromRequest(request, new Category())
		   					);
		   			createMonthlyCategories(request, category);
		   		} else {
		   			category = categoryService.updateCategory(
		   						loadCategoryFromRequest(request, category)
		   					);
		   		}
	           transaction.commit();
	    } catch (Exception e) {
	           if (transaction != null) {
	             transaction.rollback();
	           }
	           logger.error("Exception", e);
	    }
		RequestUtil.getHistoryStack(request).goBack();
		logger.debug("-");
		return category;
	}
	
	private void createMonthlyCategories(HttpServletRequest request, Category parentCategory) {
		logger.debug("+");
		if ( Env.getProperty("event.createMonthlySubcategories","true").equalsIgnoreCase("true") 
				&& parentCategory.getParentCategory() == null ) {
			Calendar now = Calendar.getInstance();
			for (int i=0; i < 12; i++) {
				Category category = new Category(i, parentCategory);
				for (Language language: Language.getItems()) {
					SimpleDateFormat dateFormater = new SimpleDateFormat("MMMM yyyy", new Locale(language.getCode()));					
					now.set(now.get(Calendar.YEAR), i, 1);					
					category.getTitles().put(language.getId(), dateFormater.format(now.getTime()));
				}
				categoryService.saveCategory(category);
			}
		}

		logger.debug("+");
	}

}