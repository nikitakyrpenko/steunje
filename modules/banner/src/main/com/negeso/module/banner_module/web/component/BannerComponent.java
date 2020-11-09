/*
 * @(#)Id: BannerComponent.java, 20.12.2007 15:56:30, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.web.component;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.SpringConstants;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.module.engine.ModuleEngine;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.module.banner_module.bo.Banner;
import com.negeso.module.banner_module.bo.BannerType;
import com.negeso.module.banner_module.service.BannerService;
import com.negeso.module.banner_module.service.BannerTypeService;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class BannerComponent extends AbstractPageComponent{

	private static Logger logger = Logger.getLogger(BannerComponent.class);
		
	public Element getElement(Document document, RequestContext request,
			Map parameters) {
		Element bannerElement = Xbuilder.createEl(document, ModuleConstants.BANNER_MODULE, null);
		
		if (ModuleEngine.getInstance().isModuleExpired(ModuleConstants.BANNER_MODULE)) {
			return bannerElement;
		}		
		
		BannerService bannerService = (BannerService) request.getService(
				ModuleConstants.BANNER_MODULE, SpringConstants.BANNER_MODULE_BANNER_SERVICE);
		BannerTypeService bannerTypeService = (BannerTypeService) request.getService(
				ModuleConstants.BANNER_MODULE, SpringConstants.BANNER_MODULE_TYPE_SERVICE);
	
		Language lang = (Language) request.getSession().getAttribute("language_object");
		if (lang == null){
			logger.error("cannot find site language");
			lang = Language.getDefaultLanguage();
		}
		Long langId = lang.getId();
		
		Long pmCatId = request.getLong("pmCatId");
		Long pmProdId = request.getLong("pmProductId");
		
		Long userId = null;
		
		int pageId = request.getSession().getPageId();
		
		User user = request.getSession().getUser();
		if (user != null){
			userId = user.getId();
		}
		
		
		try{
			List<Banner> banners = bannerService.findBanners(Long.parseLong(String.valueOf(pageId)), userId, pmCatId, pmProdId, langId);
			for (Banner banner : banners){
				if (banner != null){
					BannerType type = bannerTypeService.findById(banner.getBannerTypeId());
					BannerXMLBuilder.buildXML(bannerElement, banner, type);
				}
			}
		}catch(Exception e){
			bannerElement.setAttribute("error", e.getMessage());
			logger.error("error", e);
		}
		return bannerElement;
	}
}
