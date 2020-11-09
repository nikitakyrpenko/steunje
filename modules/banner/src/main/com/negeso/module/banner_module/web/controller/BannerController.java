/*
 * @(#)Id: BannerController.java, 17.12.2007 18:17:42, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.banner_module.bo.Banner;
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
public class BannerController extends SimpleFormController {
	
	private BannerService bannerService;
	private BannerTypeService bannerTypeService;
	
	public void setBannerTypeService(BannerTypeService bannerTypeService) {
		this.bannerTypeService = bannerTypeService;
	}

	public BannerController(){
		setCommandClass(Banner.class);
		setCommandName("banner");
	}
	
	public void setBannerService(BannerService bannerService){
		this.bannerService = bannerService;
	}

	@Override
	protected void initBinder(HttpServletRequest req, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
	}
	
	public ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) 																							throws Exception {
		logger.debug("+");
		Banner banner = (Banner) command;
		
		List<String> errorCodes = validate(banner);
		if (errorCodes.size() != 0){
			request.setAttribute("errs", errorCodes);
			return prepareView(request, banner);
		}
		
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		Language lang = (Language) request.getSession().getAttribute("language_object");
		if (lang == null){
			logger.error("cannot find site language");
			lang = Language.getDefaultLanguage();
		}
		Long langId = lang.getId();

		params = BannerCategoryListController.getSessionViewParams(request);
			
		if (banner.getId() < 0){
			bannerService.createBanner(banner);
		}else{
			bannerService.updateBanner(banner);
		}
		
		String sGroups = ServletRequestUtils.getStringParameter(request, "bm_groups", "");
		try{
			bannerService.updateBanner2Group(banner.getId(), sGroups.split(";"));
		}catch(Exception e){
			logger.error("error during update banner to groups: " + e.getMessage());
		}

		String sPages = ServletRequestUtils.getStringParameter(request, "bm_pages", "");
		try {
			bannerService.updateBanner2Page(banner.getId(), sPages.split("#"), langId);
		} catch(Exception e) {
			logger.error("error during update banner to pages: " + e.getMessage());
		}
			
		if (banner.getCategoryId() != null ){
			request.setAttribute("parentId", banner.getCategoryId().toString());
		}
		
		String saveAndClose = ServletRequestUtils.getStringParameter(request, "save_and_close", null);
		if (saveAndClose != null && saveAndClose.trim().toLowerCase().equals("true")){
			RequestUtil.getHistoryStack(request).goBack();
			request.setAttribute("bannerclicks", bannerService.getBannerClicksByCategoryId(banner.getCategoryId()));
			request.setAttribute("bannerviews", bannerService.getBannerViewsByCategoryId(banner.getCategoryId()));
			logger.debug("-");
			return new ModelAndView("bm_bannerlist", "bannerlist", bannerService.findParametrizedBannerList(banner.getCategoryId(), params));
		}else{
			return prepareView(request, banner);
		}
	}
	
	private ModelAndView prepareView(HttpServletRequest request, Banner banner){
		Banner b = bannerService.findById(banner.getId());
		try {
			request.setAttribute("bannertype", bannerTypeService.findByBannerId(banner.getId()));
			request.setAttribute("bannertypes", bannerTypeService.findAllTypes());
			request.setAttribute("bannergroups", bannerService.findStringBannerGroupIds(banner.getId()));
			request.setAttribute("bannerpages", bannerService.findStringBannerPagesIds(banner.getId()));
			RequestUtil.getHistoryStack(request).goBack();
			RequestUtil.getHistoryStack(request).push( new Link(b.getTitle(), 
					"/admin/bm_bannerlist.html&action=editBanner&bannerId=" + banner.getId(), false) );
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ModelAndView("bm_editbanner", "banner", b);
	}
	
	private List<String> validate(Banner banner){
		List<String> errorCodes = new ArrayList<String>();
		if (banner.getTitle() == null || banner.getTitle().trim().length() == 0){
			errorCodes.add("BM.TYPE_TITLE_EMPTY");
		}
		if (banner.getActivated() && banner.getExpiredDate() != null && banner.getExpiredDate().before(new Date())){
			errorCodes.add("BM.BANNER_EXPIRED");
		}
		return errorCodes;
	}
}
