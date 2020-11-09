/*
 * @(#)Id: BannerProductPageChooserController.java, 21.01.2008 19:07:19, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.web.controller;

import java.io.OutputStream;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.w3c.dom.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.site_map.PageDescriptor;
import com.negeso.framework.site_map.SiteMapBuilder;
import com.negeso.module.banner_module.service.BannerService;


/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class BannerProductPageChooserController extends MultiActionController {

	private static Logger logger = Logger.getLogger(BannerProductPageChooserController.class);
	
	private static BannerService bannerService;
	
	private static final int NOT_SELECTED = 0;
	private static final int SELECTED_FOR_SOME_DESCENDANTS = 1;
	private static final int SELECTED_FOR_ALL_DESCENDANTS = 2;
	
	@SuppressWarnings("unchecked")
	public ModelAndView getProductPages(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.debug("+");
        response.setContentType("text/xml");
        
        Language lang = (Language) request.getSession().getAttribute("language_object");
		Long langId = lang.getId();
		if (lang == null){
			logger.error("cannot find site language");
			return null;
		}
		
		Long pmCategoryId;
		Long bannerId;
        try{
        	pmCategoryId = ServletRequestUtils.getRequiredLongParameter(request, "id");
        	bannerId = ServletRequestUtils.getLongParameter(request, "banner_id");
        }catch(ServletRequestBindingException e){
        	logger.error(e.getMessage());
        	return null;
        }
        
        Element pmElement = Xbuilder.createTopEl("product-pages");
        Connection conn = null;
        try{
        	PageDescriptor categoryPageDescriptor = 
	        		SiteMapBuilder.getInstance().getHandlers()
												.get("product")
												.getPages(langId, pmCategoryId);
        	
            Xbuilder.setAttr(pmElement, "page", categoryPageDescriptor.getHref());        	        	
        	pmElement.setAttribute("category-id", String.valueOf(pmCategoryId));
        	
        	if (categoryPageDescriptor != null){
        		if (!categoryPageDescriptor.isLeaf()){
        			pmElement.setAttribute("type", "category");
        			PageDescriptor subCategories = null;
    				try{
    					subCategories = SiteMapBuilder.getInstance().getHandlers()
    																  .get("product")
    																  .getPages(langId, categoryPageDescriptor.getId());
    				}catch(CriticalException e){
    					throw new Exception("cannot find child subcategories");
    				}
    				
        			if (subCategories != null && subCategories.getContent() != null){
    					for (PageDescriptor cat : subCategories.getContent()) {
    						Element e = Xbuilder.addEl(pmElement, "category", null);
    						e.setAttribute("id", String.valueOf(cat.getId()));
    						e.setAttribute("title", cat.getTitle());
    						e.setAttribute("is-leaf", String.valueOf(cat.isLeaf()));
    						if (cat.getId() != null && bannerService.findBanner2PageByProductCategoryId(bannerId, cat.getId()) != null){
    							e.setAttribute("selected", "true");
    						}
    						pmElement.appendChild(e);
    						
    						e.setAttribute("status", String.valueOf(getCategoryStatus(langId, bannerId, cat)));
    					}				
        			}
        		}else{
        			pmElement.setAttribute("type", "product");
        			PageDescriptor products = null;
    				try{
    					products = SiteMapBuilder.getInstance().getHandlers()
    																  .get("product")
    																  .getPages(langId, categoryPageDescriptor.getId());
    				}catch(CriticalException e){
    					throw new Exception("cannot find child products");
    				}
    				if (products != null && products.getContent() != null){
    					for (PageDescriptor product : products.getContent()) {
    						Element el = Xbuilder.addEl(pmElement, "product", null);
    						el.setAttribute("id", product.getId()+"");
    						el.setAttribute("title", product.getTitle());
    						el.setAttribute("category-id", categoryPageDescriptor.getId()+"");
    						if (el.getAttribute("id") != null && bannerService.findBanner2PageByProductId(bannerId, product.getId()) != null){
	        					el.setAttribute("selected", "true");
	        				}
    					}
    				}
        		}
        	}
        }catch(Exception e){
        	logger.error("- " + e.getMessage());
        	return null;
        }finally{
        	DBHelper.close(conn);
        }
        OutputStream o = null;
        try {
			o = response.getOutputStream();
            Transformer transformer = XmlHelper.newTransformer();
            transformer.transform(new DOMSource(pmElement), new StreamResult(o));
            o.flush();
        } catch (Exception e) {
        	logger.error(e.getMessage());
        } finally {
        	IOUtils.closeQuietly(o);
        }
        logger.debug("-");
        return null;
    }
	
	@SuppressWarnings("unchecked")
	public static int getCategoryStatus(Long langId, Long bannerId, PageDescriptor pmCategoryPageDescriptor) throws Exception{
		
		try{			
			if (pmCategoryPageDescriptor.isLeaf()){
				return getProductCategoryStatus(langId, bannerId, pmCategoryPageDescriptor);
			}else{
				PageDescriptor subCategories = null;
				try{
					subCategories = SiteMapBuilder.getInstance().getHandlers()
																  .get("product")
																  .getPages(langId, pmCategoryPageDescriptor.getId());
				}catch(CriticalException e){
					throw new Exception("cannot find child subcategories");
				}
				int count = 0;
				int statCount = 0;
				if (subCategories != null && subCategories.getContent() != null){
					for (PageDescriptor subCategoryPageDescriptor : subCategories.getContent()) {
						count += 3;
						statCount += 
							(subCategoryPageDescriptor.isLeaf()?getCategoryStatus(langId, bannerId, subCategoryPageDescriptor):
								getProductCategoryStatus(bannerId, langId, subCategoryPageDescriptor)) + 
								(bannerService.findBanner2PageByProductCategoryId(bannerId, subCategoryPageDescriptor.getId()) != null?1:0);
						
					}
					if (statCount == 0)
						return NOT_SELECTED;
					if (statCount == count){
						return SELECTED_FOR_ALL_DESCENDANTS;
					}
					return SELECTED_FOR_SOME_DESCENDANTS;
				}else
					return NOT_SELECTED;
			}
		}catch(Exception e){
			logger.error("error with getting category status, id=" + pmCategoryPageDescriptor.getId());
			return NOT_SELECTED;
		}
	}
	
	private static int getProductCategoryStatus(Long langId, Long bannerId, PageDescriptor pmCategoryPageDescriptor) throws Exception{
		PageDescriptor products = null;
		try{
			products = SiteMapBuilder.getInstance().getHandlers()
														  .get("product")
														  .getPages(langId, pmCategoryPageDescriptor.getId());
		}catch(CriticalException e){
			throw new Exception("cannot find child products");
		}
		if (products != null && products.getContent() != null){
			int count = products.getContent().size();
			for (PageDescriptor productPageDescriptor : products.getContent()) {
				if (bannerService.findBanner2PageByProductId(bannerId, productPageDescriptor.getId()) != null){
					count--;
				}
			}
			if (count == products.getContent().size())
				return NOT_SELECTED;
			return (count == 0 ? SELECTED_FOR_ALL_DESCENDANTS : SELECTED_FOR_SOME_DESCENDANTS);
		}else{
			return NOT_SELECTED;
		}
		
	}
	

	public void setBannerService(BannerService bannerService) {
		this.bannerService = bannerService;
	}
}
