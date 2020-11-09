/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.core.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.LookupService;
import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.core.service.LanguageService;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class GeoIpLanguagesController extends AbstractController{
	
	private LanguageService languageService = null;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Language> languages = languageService.list();
		if ("save".equals(request.getParameter("action"))) {
			
			for (Country country : LookupService.getCountries()) {
				String langCode = request.getParameter(country.getCode());
				if (StringUtils.isNotBlank(langCode)) {
					Language lang = getLangByCode(languages, langCode); 
					if (!lang.getCountries().containsKey(country.getCode())) {
						for (Language language : languages) {
							language.getCountries().keySet().remove(country.getCode());
							languageService.update(languages);
						}
						lang.getCountries().put(country.getCode(), country.getName());
					}
				}
			}
			languageService.update(languages);
			Language.resetCache();
		}
		RequestUtil.getHistoryStack(request).push( new Link("CM_GEO_IP_LANGUAGES", 
				"/admin/site_placeholder.html", true));
		return new PreparedModelAndView("geoip_languages")
						.addToModel("countries", LookupService.getCountries())
						.addToModel("languages", languages)
						.get();
	}
	
	private Language getLangByCode(List<Language> list, String langCode) {
		for (Language language : list) {
			if (langCode.equals(language.getCode())) {
				return language;
			}
		}
		return null;
	}

	public LanguageService getLanguageService() {
		return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

}

