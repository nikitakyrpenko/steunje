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
package com.negeso.module.social.web.controller;

import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.social.bo.SocialNetwork;
import com.negeso.module.social.bo.SocialNetworkParam;
import com.negeso.module.social.service.SocialNetworkService;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class SocialNetworksController extends AbstractController{
	
	private SocialNetworkService socialNetworkService = null;
	
	private static final String ACTION = "action";
	private static final String ACTION_EDIT = "edit";
	

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Language language = NegesoRequestUtils.getInterfaceLanguage(request);
		List<SocialNetwork> list = socialNetworkService.list();
		for (SocialNetwork socialNetwork : list) {
			socialNetwork.setCurrentLangId(language.getId());
		}
		
		if (ACTION_EDIT.equals(request.getParameter(ACTION))) {
			for (SocialNetwork socialNetwork : list) {
				for (Entry<String, SocialNetworkParam> entry : socialNetwork.getParams().entrySet()) {
					entry.getValue().setValue(request.getParameter(entry.getValue().getCode() + "_" + entry.getValue().getId()));
				}
				socialNetworkService.update(socialNetwork);
			}
		}
		
		RequestUtil.getHistoryStack(request).push( new Link("CORE.SOCIAL_NETWORK", 
				"/admin/site_settings", true));
		return new PreparedModelAndView("edit_social_network")
				.addToModel("socialNetworks", list)
				.addToModel("languages", Language.getItems())
				.addToModel("curLang", language.getId()).get();
	}

	public SocialNetworkService getSocialNetworkService() {
		return socialNetworkService;
	}

	public void setSocialNetworkService(SocialNetworkService socialNetworkService) {
		this.socialNetworkService = socialNetworkService;
	}

}

