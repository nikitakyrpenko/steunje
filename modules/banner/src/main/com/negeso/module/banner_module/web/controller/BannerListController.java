/*
 * @(#)Id: BannerListController.java, 17.12.2007 16:01:15, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.banner_module.web.controller;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.Env;
import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.site_map.PageDescriptor;
import com.negeso.framework.site_map.PagesHandler;
import com.negeso.framework.site_map.SiteMapBuilder;
import com.negeso.module.banner_module.bo.Banner;
import com.negeso.module.banner_module.bo.BannerType;
import com.negeso.module.banner_module.bo.CalendarPeriod;
import com.negeso.module.banner_module.service.BannerService;
import com.negeso.module.banner_module.service.BannerTypeService;
import com.negeso.module.user.domain.Group;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class BannerListController extends MultiActionController {
	
	private static Logger logger = Logger.getLogger(BannerListController.class);
	
	private static final String DEFAULT_STAT_PERIOD = "month";
	
	private BannerService bannerService;
	private BannerTypeService bannerTypeService;
	
	public ModelAndView addBanner(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("+");
		
		long parentId = ServletRequestUtils.getRequiredLongParameter(request, "parentId");
		
		Banner banner = new Banner();
		banner.setCategoryId(parentId);
		request.setAttribute("parentId", parentId);
		
		request.setAttribute("bannergroups", "");
		request.setAttribute("bannerpages", "####");
		
		List<BannerType> types = bannerTypeService.findAllTypes();
		if (types == null || types.isEmpty()){
			logger.debug("- banner cannot be created, no type");
			request.setAttribute("error_message", "BM.BANNER_WITHOUT_TYPE");
			return prepareView(request, banner);
		}
		request.setAttribute("bannertypes", types);
		
		RequestUtil.getHistoryStack(request).push( new Link("New banner", 
					"/admin/bm_bannerlist.html&action=addBanner&parentId=" + parentId, false) );
		logger.debug("-");
		return new ModelAndView("bm_editbanner", "banner", banner);
	}
	
	private ModelAndView prepareView(HttpServletRequest request, Banner banner){
		Map<String, Object> params = BannerCategoryListController.getSessionViewParams(request);
		request.setAttribute("parentId", banner.getCategoryId());
		request.setAttribute("bannerclicks", bannerService.getBannerClicksByCategoryId(banner.getCategoryId()));
		request.setAttribute("bannerviews", bannerService.getBannerViewsByCategoryId(banner.getCategoryId()));
		return new ModelAndView("bm_bannerlist", "bannerlist", bannerService.findParametrizedBannerList(banner.getCategoryId(), params));
	}
	
	public ModelAndView duplicateBanner(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("+");
		
		Banner banner = new Banner();
		try{
			String parentId = request.getParameter("parentId");
			Long pId = null;
			try{
			if (parentId != null && parentId.trim().length() > 0){
				if (parentId.equals("null")){
					pId = null;
				}else{
					pId = Long.parseLong(parentId);
				}
				banner.setCategoryId(pId);
				request.setAttribute("parentId", parentId);
			}else{
				parentId = null;
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		
		String bannerId = request.getParameter("bannerId");
		Long bId = null;
		if (bannerId != null){
			try{
				bId = Long.parseLong(bannerId);
			}
			catch(Exception e){
				logger.error("- error convering parameter bannerId:" + e.getMessage());
			}
		}
		
		Banner b = bannerService.findById(bId);
		banner.setTitle(b.getTitle() + "(copy)");
		banner.setPriority(b.getPriority());
		banner.setCategoryId(b.getCategoryId());
		banner.setBannerTypeId(b.getBannerTypeId());
		banner.setMaxClicks(b.getMaxClicks());
		banner.setMaxViews(b.getMaxViews());
		banner.setExpiredDate(b.getExpiredDate());
		banner.setPublishDate(b.getPublishDate());
		banner.setImageUrl(b.getImageUrl());
		banner.setImageType(b.getImageType());
		banner.setUrl(b.getUrl());
		banner.setInNewWindow(b.getInNewWindow());
		
		request.setAttribute("bannertype", bannerTypeService.findByBannerId(bId));
		request.setAttribute("bannertypes", bannerTypeService.findAllTypes());
		request.setAttribute("bannergroups", bannerService.findStringBannerGroupIds(b.getId()));
		request.setAttribute("bannerpages", bannerService.findStringBannerPagesIds(b.getId()));
		request.setAttribute("bannerclicks", bannerService.getBannerClicks(banner.getId()));
		request.setAttribute("bannerviews", bannerService.getBannerViews(banner.getId()));
		
		RequestUtil.getHistoryStack(request).goBack();
		RequestUtil.getHistoryStack(request).push( new Link(banner.getTitle(), 
					"/admin/bm_bannerlist.html&action=duplicateBanner&bannerId=" + pId, false) );
		
		request.setAttribute("duplicate", Boolean.TRUE);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		logger.debug("-");
		return new ModelAndView("bm_editbanner", "banner", banner);
	}	
	
	public ModelAndView editBanner(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("+");
		
		String bannerId = request.getParameter("bannerId");
		Long bId = null;
		if (bannerId != null){
			bId = Long.parseLong(bannerId);
		}
		
		Banner banner = bannerService.findById(bId);
		try{
			request.setAttribute("bannertype", bannerTypeService.findByBannerId(bId));

			request.setAttribute("bannertypes", bannerTypeService.findAllTypes());
			
			request.setAttribute("bannergroups", bannerService.findStringBannerGroupIds(bId));
			
			request.setAttribute("bannerpages", bannerService.findStringBannerPagesIds(bId));
			
			request.setAttribute("bannerclicks", bannerService.getBannerClicks(banner.getId()));
			
			request.setAttribute("bannerviews", bannerService.getBannerViews(banner.getId()));
	
			RequestUtil.getHistoryStack(request).push( new Link(banner.getTitle(), 
					"/admin/bm_bannerlist.html&action=editBanner&bannerId=" + banner.getId(), false) );
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		logger.debug("-");
		return new ModelAndView("bm_editbanner", "banner", banner);
	}
	
	public ModelAndView deleteBanner(HttpServletRequest request, HttpServletResponse response){
		logger.debug("+");
		
		String bannerId = request.getParameter("bannerId");
		Long bId = null;
		if (bannerId != null){
			bId = Long.parseLong(bannerId);
		}
		Banner banner = bannerService.findById(bId);
		try{
			bannerService.deleteBanner(banner.getId());
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
        return prepareView(request, banner);
    }
	
	public ModelAndView showStatistics(HttpServletRequest request, HttpServletResponse response){
		logger.debug("+");

		long bId = ServletRequestUtils.getLongParameter(request, "bannerId", -1);
		if (bId == -1) {
			List<Banner> banners = new ArrayList<Banner>();
			banners = bannerService.findAllBanners();
			request.setAttribute("bannerclicks", bannerService.getAllBannerClicks());
			request.setAttribute("bannerviews", bannerService.getAllBannerViews());
			logger.debug("-");
			RequestUtil.getHistoryStack(request).push( new Link("Statistics", 
					"/admin/bm_bannerlist.html?action=showStatistics", false, -1) );
			return new ModelAndView("bm_bannerstat", "banners", banners);
		}else{
			Calendar calendar = Calendar.getInstance();
			int amount = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
			if (amount < 0)
				amount += 7;
			calendar.add(Calendar.DATE, -amount);
			String formattedStartDate = BannerService.DATE_FORMAT.format(calendar.getTime());
			calendar.add(Calendar.DATE, amount);
			String formattedEndDate = BannerService.DATE_FORMAT.format(calendar.getTime());
			
			Banner banner = bannerService.findById(bId);
			
			request.getSession().setAttribute("bm_stat_period", "week");
			request.getSession().setAttribute("stat_start", formattedStartDate);
			request.getSession().setAttribute("stat_finish", formattedEndDate);
			
			logger.debug("-");
			
			RequestUtil.getHistoryStack(request).push( new Link(banner.getTitle(), 
					"/admin/bm_bannerlist.html?action=showStatistics&bannerId=" + bId, false) );
			return new ModelAndView("bm_detailstat", "banner", banner);
		}
		
	}
	
	public ModelAndView showDetailStatistics(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.debug("+");
		
		List<String> errors = new ArrayList<String>();
		
		String sDate = request.getParameter("stat_start"); 
		if (sDate == null || sDate.trim().length() == 0){
			errors.add("'From' field is empty");
		}
		
		String fDate = request.getParameter("stat_finish"); 
		if (fDate == null || fDate.trim().length() == 0){
			errors.add("'To' field is empty");
		}
		
		Date startDate = null;
		Date finishDate = null;
		try{
			startDate = Env.parseRoundDate(sDate);
			finishDate = Env.parseRoundDate(fDate);
			if (startDate.after(finishDate)){
				errors.add("wrong dates, start date is after finish date");
			}
		}catch(Exception e){
			logger.error("- validation error: " + e.getMessage());
		}
		
		Long bId = null;
		try {
			bId = ServletRequestUtils.getLongParameter(request, "id");
		} catch (ServletRequestBindingException e) {
			logger.error("cannot get banner id for request: " + e.getMessage());
		}
		
		request.getSession().setAttribute("stat_start", sDate);
		request.getSession().setAttribute("stat_finish", fDate);
		
		String period = request.getParameter("period");
		int periodInt = 1;
		if (period == null){
			period = DEFAULT_STAT_PERIOD;
		}
		periodInt = (period.equals(DEFAULT_STAT_PERIOD)?Calendar.MONTH:Calendar.WEEK_OF_YEAR);
		request.getSession().setAttribute("bm_stat_period", period);


		List<CalendarPeriod> stats = null;
		if (errors.size() == 0){
			stats = new ArrayList<CalendarPeriod>();
			
			GregorianCalendar sCalendar = new GregorianCalendar();
			sCalendar.setFirstDayOfWeek(Calendar.MONDAY);
	        sCalendar.setTime(startDate);
	        sCalendar.setMinimalDaysInFirstWeek(7);
			
	        GregorianCalendar fCalendar = new GregorianCalendar();
	        fCalendar.setFirstDayOfWeek(Calendar.MONDAY);
			fCalendar.setTime(finishDate);
			fCalendar.setMinimalDaysInFirstWeek(7);
			
			int sYear = sCalendar.get(Calendar.YEAR);
			int fYear = fCalendar.get(Calendar.YEAR);
			
			if (periodInt == Calendar.WEEK_OF_YEAR){
				if (sYear == fYear){
					if (sCalendar.before(getFirstDateInYear(sYear)) && fCalendar.after(getFirstDateInYear(sYear))){
						addToList(stats, getStat(bId, sDate, getFirstDayInYear(sYear), periodInt));
						addToList(stats, getStat(bId, getNextDay(getFirstDateInYear(sYear)), fDate, periodInt));
					}else{
						addToList(stats, getStat(bId, sDate, fDate, periodInt));
					}
				}else
				if (sYear != fYear){
					for (int currYear = sYear; currYear <= fYear; currYear++){
						if (currYear == sYear){
							if (sCalendar.before(getFirstDateInYear(currYear))){
								addToList(stats, getStat(bId, sDate, getFirstDayInYear(currYear), periodInt));
								if (fYear - currYear == 1 && fCalendar.before(getFirstDateInYear(fYear))){
									addToList(stats, getStat(bId, getNextDay(getFirstDateInYear(currYear)), fDate, periodInt));
								}else{
									addToList(stats, getStat(bId, getNextDay(getFirstDateInYear(currYear)), getLastDayInYear(currYear), periodInt));
								}
							}else{
								if (fYear - currYear == 1 && fCalendar.before(getFirstDateInYear(fYear))){
									addToList(stats, getStat(bId, sDate, fDate, periodInt));
								}else{
									addToList(stats, getStat(bId, sDate, getLastDayInYear(currYear), periodInt));
								}
							}
						}else
						if (currYear != sYear && currYear != fYear){
							if (fYear - currYear == 1 && fCalendar.before(getFirstDateInYear(fYear))){
								addToList(stats, getStat(bId, getNextDay(getFirstDateInYear(currYear)), fDate, periodInt));
							}else{
								addToList(stats, getStat(bId, getNextDay(getFirstDateInYear(currYear)), getLastDayInYear(currYear), periodInt));
							}
						}else
						if (currYear == fYear){
							if (fCalendar.after(getFirstDateInYear(fYear))){
								addToList(stats, getStat(bId, getNextDay(getFirstDateInYear(fYear)), fDate, periodInt));
							}
						}
					}
				}
			}else
			if (periodInt == Calendar.MONTH){
				if (sCalendar.get(Calendar.YEAR) == fCalendar.get(Calendar.YEAR)){
					for (CalendarPeriod c : getStat(bId, sDate, fDate, periodInt)){
						stats.add(c);
					}
				}else{
					for (CalendarPeriod c : getStat(bId, sDate, sCalendar.get(Calendar.YEAR) + "-12-31", periodInt)){
						stats.add(c);
					}
					for (int year = sCalendar.get(Calendar.YEAR) + 1; year < fCalendar.get(Calendar.YEAR); year++){
						for (CalendarPeriod c : getStat(bId, year + "01-01", year + "-12-31", periodInt)){
							stats.add(c);
						}
					}
					for (CalendarPeriod c : getStat(bId, fCalendar.get(Calendar.YEAR) + "-01-01th", fDate, periodInt)){
						stats.add(c);
					}
				}
			}
			
			
			
		}else{
			request.setAttribute("errors", errors);
		}
		
		Banner banner = bannerService.findById(bId);
		request.setAttribute("banner", banner);
		RequestUtil.getHistoryStack(request).push( new Link(banner.getTitle(), 
				"/admin/bm_bannerlist.html?action=showStatistics&bannerId=" + banner.getId(), false) );
		logger.debug("-");
		return new ModelAndView("bm_detailstat", "bannerstat", stats);
	}
	
	private List<CalendarPeriod> addToList(List<CalendarPeriod> l1, List<CalendarPeriod> l2){
		for (CalendarPeriod o : l2){
			l1.add(o);
		}
		return l1;
	}
	
	private String getNextDay(GregorianCalendar c){
		c.add(Calendar.DAY_OF_YEAR, 1);
		return Env.formatRoundDate(c.getTime());
	}
	
	private GregorianCalendar getFirstDateInYear(int year){
		GregorianCalendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setMinimalDaysInFirstWeek(7);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.WEEK_OF_YEAR, 1);
		c.set(Calendar.DAY_OF_WEEK, 1);
		c.add(Calendar.DAY_OF_YEAR, -7);
		return c;
	}
	
	private String getFirstDayInYear(int year){
		GregorianCalendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setMinimalDaysInFirstWeek(7);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.WEEK_OF_YEAR, 1);
		c.set(Calendar.DAY_OF_WEEK, 1);
		c.add(Calendar.DAY_OF_YEAR, -7);
		return Env.formatRoundDate(c.getTime());
	}
	
	private String getLastDayInYear(int year){
		GregorianCalendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setMinimalDaysInFirstWeek(7);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.WEEK_OF_YEAR, c.getActualMaximum(Calendar.WEEK_OF_YEAR));
		c.set(Calendar.DAY_OF_WEEK, 7);
		c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
		return Env.formatRoundDate(c.getTime());
	}
	
	private List<CalendarPeriod> getStat(Long bId, String sDate, String fDate, int period) throws Exception{
		
		ArrayList<CalendarPeriod> stats = new ArrayList<CalendarPeriod>();
		
		GregorianCalendar sCalendar = new GregorianCalendar();
		sCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        sCalendar.setTime(Env.parseRoundDate(sDate));
        sCalendar.setMinimalDaysInFirstWeek(7);
		
        GregorianCalendar fCalendar = new GregorianCalendar();
        fCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		fCalendar.setTime(Env.parseRoundDate(fDate));
		fCalendar.setMinimalDaysInFirstWeek(7);
		if (
				
			(period == Calendar.MONTH && sCalendar.get(period) != fCalendar.get(period)) || 
			(period == Calendar.WEEK_OF_YEAR && sCalendar.get(period) != fCalendar.get(period))){

        	stats.add(new CalendarPeriod(
        			sCalendar.get(period) + (period == Calendar.MONTH?1:0), 
        			Env.parseRoundDate(sDate), 
        			Env.parseRoundDate(getLastDateOfMonth(sDate, period)), 
        			CalendarPeriod.WEEK_PERIOD, 
        			bannerService.getClicksByDate(bId, sDate, getLastDateOfMonth(sDate, period)), 
        			bannerService.getViewsByDate(bId, sDate, getLastDateOfMonth(sDate, period))));
        	for (int weekNumber = sCalendar.get(period) + 1; weekNumber < fCalendar.get(period); weekNumber++ ){
        		
        		GregorianCalendar c = new GregorianCalendar();
        		c.setFirstDayOfWeek(Calendar.MONDAY);
        		c.set(Calendar.YEAR, sCalendar.get(Calendar.YEAR));
        		c.set(period, weekNumber);
        		c.setMinimalDaysInFirstWeek(7);

        		stats.add(new CalendarPeriod(
        				weekNumber + (period == Calendar.MONTH?1:0), 
	        			Env.parseRoundDate(getStartDateOfMonth(Env.formatRoundDate(c.getTime()), period)), 
	        			Env.parseRoundDate(getLastDateOfMonth(Env.formatRoundDate(c.getTime()), period)), 
	        			CalendarPeriod.WEEK_PERIOD, 
	        			bannerService.getClicksByDate(bId, getStartDateOfMonth(Env.formatRoundDate(c.getTime()), period), 
	        					getLastDateOfMonth(Env.formatRoundDate(c.getTime()), period)), 
	        			bannerService.getViewsByDate(bId, getStartDateOfMonth(Env.formatRoundDate(c.getTime()), period), 
	        					getLastDateOfMonth(Env.formatRoundDate(c.getTime()), period)))); 
        	}
        	stats.add(new CalendarPeriod(
        			fCalendar.get(period) + (period == Calendar.MONTH?1:0), 
        			Env.parseRoundDate(getStartDateOfMonth(fDate, period)), 
        			Env.parseRoundDate(fDate), 
        			CalendarPeriod.WEEK_PERIOD, 
        			bannerService.getClicksByDate(bId, getStartDateOfMonth(fDate, period), fDate), 
        			bannerService.getViewsByDate(bId, getStartDateOfMonth(fDate, period), fDate)));
        }else{
        	stats.add(new CalendarPeriod(
        			sCalendar.get(period) + (period == Calendar.MONTH?1:0), 
        			Env.parseRoundDate(sDate), 
        			Env.parseRoundDate(fDate), 
        			CalendarPeriod.WEEK_PERIOD, 
        			bannerService.getClicksByDate(bId, sDate, fDate), 
        			bannerService.getViewsByDate(bId, sDate, fDate)));
        }
		return stats;
	}
	
	public ModelAndView pageChooser(HttpServletRequest request, HttpServletResponse response){
		logger.debug("+");
		
		if (request.getParameter("linkchooser") != null){
			logger.error("linkchooser");
			request.setAttribute("linkchooser", true);
		}
		
		Long bannerId = null;
		Banner banner = null;
		try {
			bannerId = ServletRequestUtils.getLongParameter(request, "bannerId");
			banner = bannerService.findById(bannerId);
		} catch (ServletRequestBindingException e) {
			logger.error("cannot get banner id: " + e.getMessage());
		}
		
		Language lang = (Language) request.getSession().getAttribute("language_object");
		Long langId = lang.getId();
		if (lang == null){
			logger.error("cannot find site language");
			return new ModelAndView("bm_pagechooser");
		}
		
		try{
			Map<String, PagesHandler> pagesHandlers = SiteMapBuilder.getInstance().getHandlers();
			for (String key : pagesHandlers.keySet()) {
				PageDescriptor p = pagesHandlers.get(key).getPages(langId);
				request.setAttribute(key, p);
				
				if(p.getContent() != null){
					processCategoryStatus(langId, bannerId, p);
				}
			}
		}catch(Exception e){
			logger.error("cannot build menus for pagechooser:" + e.getMessage());
		}
		if (langId != null){
			Language l;
			try {
				l = Language.findById(langId);
				request.setAttribute("langId", l.getCode().toUpperCase());
			} catch (Exception e) {
				logger.error("cannot find language:" + e.getMessage());
			}
		}
		String url = request.getRequestURL().toString();
		String uri = request.getRequestURI();
		request.setAttribute("url_address", url.replaceAll(uri, "/"));
		if (banner != null){
			request.setAttribute("activated", banner.getActivated());
		}else{
			request.setAttribute("activated", false);
		}
		
		logger.debug("-");
		return new ModelAndView("bm_pagechooser");
	}
	
	public void processCategoryStatus(Long langId, Long bannerId, PageDescriptor pmPageDescriptor) throws Exception{
		for (PageDescriptor pmCategoryPageDescriptor : pmPageDescriptor.getContent()) {
			pmCategoryPageDescriptor.setContentStatus(String.valueOf(BannerProductPageChooserController.getCategoryStatus(langId, bannerId, pmCategoryPageDescriptor)));
		}
	}
	
	public ModelAndView groupChooser(HttpServletRequest request, HttpServletResponse response){
		logger.debug("+");

		Collection<Group> bannerGroups = Group.getGroups(Env.getSiteId());
		
		Banner banner = null;
		Long bannerId = null;
		try {
			bannerId = ServletRequestUtils.getLongParameter(request, "bannerId");
			banner = bannerService.findById(bannerId);
		} catch (ServletRequestBindingException e) {
			logger.error("cannot get banner id: " + e.getMessage());
		}

		if (banner != null){
			request.setAttribute("activated", banner.getActivated());
		}else{
			request.setAttribute("activated", false);
		}
		logger.debug("-");
		return new ModelAndView("bm_groupchooser", "bannergroups", bannerGroups);
	}
	
	public void setBannerService(BannerService bannerService){
		this.bannerService = bannerService;
	}

	public void setBannerTypeService(BannerTypeService bannerTypeService) {
		this.bannerTypeService = bannerTypeService;
	}
	
	private static String getStartDateOfMonth(String d, int type) throws ParseException{
    	GregorianCalendar c = new GregorianCalendar();
    	c.setFirstDayOfWeek(Calendar.MONDAY);
    	c.setMinimalDaysInFirstWeek(7);
    	c.setTime(Env.parseRoundDate(d));
    	
    	if (type == Calendar.MONTH){
    		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
    	}else
    	if (type == Calendar.WEEK_OF_YEAR){
    		if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
        		if (c.get(Calendar.WEEK_OF_YEAR) != 1){
        			c.add(Calendar.WEEK_OF_YEAR, -1);
        		}else{
        			c.add(Calendar.WEEK_OF_YEAR, -1);
        		}
//        		int b = c.get(Calendar.DAY_OF_WEEK);
        		c.add(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_WEEK));
        	}else{
//        		int e = 2-c.get(Calendar.DAY_OF_WEEK);
        		c.add(Calendar.DAY_OF_YEAR, 2-c.get(Calendar.DAY_OF_WEEK));
        	}
    	}
    	
    	return Env.formatRoundDate(c.getTime());
    }
    
    private static String getLastDateOfMonth(String d, int type) throws ParseException{
    	GregorianCalendar c = new GregorianCalendar();
    	c.setFirstDayOfWeek(Calendar.MONDAY);
    	c.setMinimalDaysInFirstWeek(7);
    	c.setTime(Env.parseRoundDate(d));
    	
    	if (type == Calendar.MONTH){
    		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
    	}else
    	if (type == Calendar.WEEK_OF_YEAR){
    		if (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
//        		int a = 8 - c.get(Calendar.DAY_OF_WEEK);
        		c.add(Calendar.DAY_OF_YEAR, 8 - c.get(Calendar.DAY_OF_WEEK));
        	}
    	}
    	
    	return Env.formatRoundDate(c.getTime());
    }
}
