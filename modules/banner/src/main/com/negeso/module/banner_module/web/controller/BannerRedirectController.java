/*
 * @(#)Id: BannerRedirectController.java, 04.01.2008 16:27:58, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;

import com.negeso.module.banner_module.bo.Banner;
import com.negeso.module.banner_module.service.BannerService;


/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class BannerRedirectController extends MultiActionController{
	
	private static Logger logger = Logger.getLogger(BannerRedirectController.class);
	
	private BannerService bannerService;

	public ModelAndView redirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("+");
		
		long bannerId = ServletRequestUtils.getLongParameter(request, "id", -1);
		if (bannerId > 0) {
			Banner banner = bannerService.findById(bannerId);
			if (banner != null) {
				bannerService.incBannerClick(banner);
				return new ModelAndView(new RedirectView(banner.getUrl()));
			}
			logger.error("Banner is null. It seems that the banner was removed by admin, " +
				"but the page from the visitor part wasn't refreshed");
		} else {
			logger.error("- during banner redirection bannerId is null");
		}
		return new ModelAndView("redirect:");
	}

	public void setBannerService(BannerService bannerService) {
		this.bannerService = bannerService;
	};	
	
}
