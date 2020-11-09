/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.friendly_url;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.negeso.framework.Env;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.CommandMapping;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.module.AbstractFriendlyUrlHandler;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.page.command.GetPageCommand;
import com.negeso.framework.view.AbstractHttpView;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class FriendlyUrlHandler extends AbstractFriendlyUrlHandler {
	
	private static Logger logger = Logger.getLogger(FriendlyUrlHandler.class);
	
	public static final String INPUT_FIRST_PARAM = "first_param";
	public static final String INPUT_SECOND_PARAM = "second_param";
	public static final String ROOT_URL_PART = "root";
	public static final String PAGE_CLASS = "pageClass";
	public static final String PAGE_COMPONENT = "pageComponent";
	public static final String ENTITY_TYPE = "entityType";
	public static final String PARAM_NAME = "paramName";
	public static final String INPUT_FILENAME = GetPageCommand.INPUT_FILENAME;
	
	
	
	private static Map<String, List<FriendlyUrlDescriptor>> keyToDescriptor = new HashMap<String, List<FriendlyUrlDescriptor>>();
	public static Map<String, String> constValueToPageName = new HashMap<String, String>();
	private static Map<String, String> pageNameToConstValue = new HashMap<String, String>();
	
	public Map<String, CommandMapping> getUrlMatchers() {
		synchronized (constValueToPageName) {
			constValueToPageName.clear();
			pageNameToConstValue.clear();
			Map<String, CommandMapping> urlMatchers = new LinkedHashMap<String, CommandMapping>();
			for (Entry<String, List<FriendlyUrlDescriptor>> entry : keyToDescriptor.entrySet()) {
				List<String> urls = getModuleUrls(entry.getKey(), entry.getValue().get(0));
				for (String url : urls) {
					for (String prefix: new String[] {"/", ADMIN}) {
						for (FriendlyUrlDescriptor descriptor : entry.getValue()) {
							buildMatcher(prefix + url + descriptor.getHtmlMatcher(), url, urlMatchers, false, descriptor, prefix);
							if (prefix.equals(ADMIN) && descriptor.getXmlMatcher() != null) {
								buildMatcher(prefix + url + descriptor.getXmlMatcher(), url, urlMatchers, true, descriptor, prefix);
							}
						}
					}
				}
			}
			return urlMatchers;
		}
	}
	
	private void buildMatcher(String url, String title,
			Map<String, CommandMapping> urlMatchers, boolean isXml,
			FriendlyUrlDescriptor descriptor, String prefix) {
		CommandMapping cmap = new CommandMapping();
		if (isXml) {
			cmap.getBinds().put(Command.RESULT_SUCCESS, XML_VIEW);					
		} else {
			cmap.getBinds().put(Command.RESULT_SUCCESS, HTML_VIEW);
			Map<String, String> bindParams = new HashMap<String, String>(3);
			bindParams.put(AbstractHttpView.KEY_XSL, SITE_XSL);
			cmap.addBindingParameters(Command.RESULT_SUCCESS, bindParams);
			if (prefix.equals(ADMIN)) {
				cmap.getBinds().put(Command.RESULT_ACCESS_DENIED, HTML_VIEW);
				bindParams = new HashMap<String, String>(3);
				bindParams.put(AbstractHttpView.KEY_XSL, LOGIN_XSL);
				cmap.addBindingParameters(Command.RESULT_ACCESS_DENIED, bindParams);
			}
		}
		cmap.setCommandName(ProcessFriendlyUrlCommand.NAME);
		cmap.setPattern(url);
		if (descriptor.isFirstParamPresent()) {
			cmap.getCommandParameters().put(INPUT_FIRST_PARAM, "{1}" + (descriptor.isSleshNeeded() ? "/" : ""));			
			cmap.getCommandParameters().put(ENTITY_TYPE, descriptor.getEntityType().toString());
		}
		if (descriptor.isSecondParamPresent()) {
			cmap.getCommandParameters().put(INPUT_SECOND_PARAM, "{2}");
		}
		//cmap.getCommandParameters().put(ROOT_URL_PART, title + "/");
		cmap.getCommandParameters().put(PAGE_CLASS, descriptor.getPageClass());
		cmap.getCommandParameters().put(PAGE_COMPONENT, descriptor.getPageComponent());
		cmap.getCommandParameters().put(PARAM_NAME, descriptor.getUrlParamName());
		cmap.getCommandParameters().put(INPUT_FILENAME, constValueToPageName.get(title));
		urlMatchers.put(url, cmap);
	}
	
	
	
	private List<String> getModuleUrls(String customConstKey, FriendlyUrlDescriptor descriptor){
		List<String> moduleUrls = new ArrayList<String>();
		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			PreparedStatement stmt = conn.prepareStatement(
					" SELECT translation, language.id as lang_id, language.code FROM dic_custom_translation " +
					" LEFT JOIN dic_custom_const ON dic_custom_const.id = dic_custom_translation.const_id " +
					" LEFT JOIN core_reference ON core_reference.id = dic_custom_translation.lang_id " +
					" INNER JOIN language ON language.code = core_reference.code " +
					" WHERE dic_custom_const.key = ? ");
			stmt.setString(1, customConstKey);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (StringUtils.hasText(rs.getString("translation"))) {
					String translation = rs.getString("translation");
					if (Env.isUseLangCodeAsUrlStart()) {
						translation = rs.getString("code") + "/" + UrlAnalyzer.parse(translation);
					}
					PageH page = getPageName(descriptor, rs.getLong("lang_id"), translation);
					if (page != null) {
						moduleUrls.add(translation);
						constValueToPageName.put(translation, page.getFilename());
						pageNameToConstValue.put(page.getFilename(), translation);
					}
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Unable to get translation for customConstKey: " + customConstKey, e);
		} finally {
			DBHelper.close(conn);
		}
		return moduleUrls;
	}
	
	private PageH getPageName(FriendlyUrlDescriptor descriptor, Long langId, String translation) {
		PageH page = null;
		
			page = PageService.getInstance().findByClassAndObligatoryComponentT(descriptor.getPageClass(), langId, 
					descriptor.getPageComponent());					
		 if(page == null){
			logger.error("Page not found by class: " + descriptor.getPageClass() + " and component name: " +
					descriptor.getPageComponent() + ", therefore root URL: " + translation + " will not be available! ");
		}
		return page;
	}
	
	public static void put(String key, List<FriendlyUrlDescriptor> descriptorList) {
		synchronized (keyToDescriptor) {
			keyToDescriptor.put(key, descriptorList);
		}
	}
	
	public static List<FriendlyUrlDescriptor> get(String key) {
		return keyToDescriptor.get(key);
	}

}

