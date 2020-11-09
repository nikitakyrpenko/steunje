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
package com.negeso.module.translator.web.controller;

import com.negeso.framework.Env;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.i18n.DatabaseResourceBundle;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.menu.IMenuService;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.core.domain.I18nCoreProperty;
import com.negeso.module.core.service.I18nCorePropertyService;
import com.negeso.module.core.service.PlaceHolderService;
import com.negeso.module.translator.Configuration;
import com.negeso.module.translator.domain.Log;
import com.negeso.module.translator.page.NewAddedPageTranslationStrategy;
import com.negeso.module.translator.page.PageTranslator;
import com.negeso.module.translator.service.*;
import com.negeso.module.translator.service.AbstractTranslator.TranslationStrategyType;
import com.negeso.module.translator.service.replace.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class TranslationManagementController extends MultiActionController {
	
	private static Logger logger = Logger.getLogger(TranslationManagementController.class);
	
	private IMenuService menuService;
	private I18nCorePropertyService i18nCorePropertyService;
	private PlaceHolderService placeHolderService;
	private LogService logService = null;
	
	private static final String NON_TRANSLATABLE_WORDS_PARAM_NAME = "nonTranslatableWords";
	private static final String REQUEST_FOR_GRAMMAR_CHECK_PARAM_NAME = "requestForGrammarCheck";
	private static final String LOKED_LANGUAGE_PARAM_NAME = "lockedLanguage";
	private static final String REMARK_FOR_LANGUAGE = "remarkForLanguage";
	
	private boolean busy;
	
	private Long defaultLicenseLangsLimit = 2L;
	private Long defaultStmCharsQuota = 1000000L;
	private Object mutex = new Object();
	
	private AbstractTranslator translator;
	
	public ModelAndView handleCommonRequests(HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
		
		String action = ServletRequestUtils.getStringParameter(request, "act");
		
		if ("isBusy".equals(action)) {
			PrintWriter out = response.getWriter();
	        out.print(busy ? translator.getProgressIndication() : busy);
	        out.close();
	        return null;
		}
		if ("checkTranslatedCharsCount".equals(action)) {
			PrintWriter out = response.getWriter();
	        out.print(translator.getTranslateService().getCharsCounter());
	        out.close();
	        return null;
		}
		Long stmCharsQuota = Long.valueOf(Env.getNotCachedSiteProperty("stmCharsQuota", defaultStmCharsQuota.toString()));
		if (!isNegesoAccountUsed()) {
			stmCharsQuota = 9999999L;
		} 
    	PreparedModelAndView modelAndView = new PreparedModelAndView("stm_settings");
    	
    	ITranslateService translateService = Configuration.get().getTranslateService();
    	translateService.setPageNameTranslatable(ServletRequestUtils.getBooleanParameter(request, "isPageNameTranslatable", false));
    	translateService.setWithoutHtmlTags(ServletRequestUtils.getBooleanParameter(request, "isWithoutHtmlTags", false));
    	
    	Long fromLangId = ServletRequestUtils.getLongParameter(request, "from", 0);
    	Long toLangId = ServletRequestUtils.getLongParameter(request, "to", 0);
    	
    		if ("translate".equals(action) && fromLangId > 0 && toLangId > 0 && !fromLangId.equals(toLangId) && !busy && stmCharsQuota > 0) {
    			synchronized (mutex) {
    				translate(request, fromLangId, toLangId, translateService, modelAndView.get(), TranslationStrategyType.values()[ServletRequestUtils.getIntParameter(request, "strategy")],
    						ServletRequestUtils.getBooleanParameter(request, "isTranslateLinks", true));
    			}
    		} else if("translateSimplePage".equals(action) && !busy && stmCharsQuota > 0) {
    			synchronized (mutex) {
    				modelAndView.addToModel("translatedPages", translateSimplePage(request, translateService));
    			}
    		} else if("translateNews".equals(action) && !busy && stmCharsQuota > 0) {
    			synchronized (mutex) {
    				translateNews(request, translateService);
    			}
    		} else if("translateCustomConst".equals(action) && !busy && stmCharsQuota > 0) {
    			synchronized (mutex) {
    				translateConst(request, translateService);
    				return null;
    			}
    		}
    	
    	if("addLang".equals(action)) {
    		addLanguage(request);
    	} else if("getNewsTranslationDialog".equals(action)) {
    		getNewsTranslationDialog(request, modelAndView);
    	} 
    	if (busy) {
    		modelAndView.addToModel("status", "busy");
		}
    	RequestUtil.getHistoryStack(request).push( new Link("STM_SETTINGS",
				"/admin/stm_settings.html", true, -1));
    	
    	modelAndView.addToModel("supportedLangs", translateService.getSupportedLaguages());
    	modelAndView.addToModel("currentLangs", Language.getItems());
    	modelAndView.addToModel("licenseLangsLimit", Env.getNotCachedSiteProperty("licenseLangsLimit", defaultLicenseLangsLimit.toString()));
    	modelAndView.addToModel("stmCharsQuota", stmCharsQuota);
    	modelAndView.addToModel(REQUEST_FOR_GRAMMAR_CHECK_PARAM_NAME, i18nCorePropertyService.findByName(REQUEST_FOR_GRAMMAR_CHECK_PARAM_NAME, Env.getSiteId()));
    	modelAndView.addToModel(LOKED_LANGUAGE_PARAM_NAME, i18nCorePropertyService.findByName(LOKED_LANGUAGE_PARAM_NAME, Env.getSiteId()));
    	modelAndView.addToModel(REMARK_FOR_LANGUAGE, i18nCorePropertyService.findByName(REMARK_FOR_LANGUAGE, Env.getSiteId()));
		return modelAndView.get();
	}

	private void translateConst(HttpServletRequest request,
                                ITranslateService translateService) {
		String fromLangCode = request.getParameter("fromLangCode");
		String[] toLangCodes = parseIds(request, "toLangCode");
		Long constId = NegesoRequestUtils.getId(request, null);
		if (StringUtils.isNotBlank(fromLangCode) && toLangCodes != null && toLangCodes.length > 0 && constId != null) {
			addReplacers(translateService, Language.getByCode(fromLangCode).getId());
			CustomConstTranslator tarTranslator = new CustomConstTranslator(translateService);
			tarTranslator.translateSimpleConst(fromLangCode, toLangCodes, constId);
		}
	}

	private boolean isNegesoAccountUsed() {
		return Configuration.NEGESO_GOOGLE_API_KEY_VALUE.equals(Env.getNotCachedSiteProperty(Configuration.NEGESO_GOOGLE_API_KEY, Configuration.NEGESO_GOOGLE_API_KEY_VALUE));
	}
	
	public ModelAndView listNonTranslatableWords(HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
		Long langId = ServletRequestUtils.getLongParameter(request, "langId");
		I18nCoreProperty property = i18nCorePropertyService.findByName(NON_TRANSLATABLE_WORDS_PARAM_NAME, Env.getSiteId());
		property.setCurrentLangId(langId);
		if ("save".equals(ServletRequestUtils.getStringParameter(request, "act"))) {
			property.setValue(ServletRequestUtils.getStringParameter(request, NON_TRANSLATABLE_WORDS_PARAM_NAME, StringUtils.EMPTY));
			i18nCorePropertyService.createOrUpdate(property);
		}
		IOUtils.write(property.getValue(), response.getOutputStream());
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		return null;
	}
	
	public ModelAndView saveParams(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
		long[] langIds = ServletRequestUtils.getLongParameters(request, LOKED_LANGUAGE_PARAM_NAME);
		i18nCorePropertyService.setBooleanValues(LOKED_LANGUAGE_PARAM_NAME, Env.getSiteId(), langIds);
		
		langIds = ServletRequestUtils.getLongParameters(request, REQUEST_FOR_GRAMMAR_CHECK_PARAM_NAME);
		I18nCoreProperty property = i18nCorePropertyService.findByName(REQUEST_FOR_GRAMMAR_CHECK_PARAM_NAME, Env.getSiteId());
		for (long langId : langIds) {
			property.getField(langId).setValue(Boolean.TRUE.toString());
		}
		new LanguageGrammarCheckRequestNotifier().notify(Env.getHostName(), langIds);
		i18nCorePropertyService.createOrUpdate(property);
		
		property = i18nCorePropertyService.findByName(REMARK_FOR_LANGUAGE, Env.getSiteId());
		Long cutLang = ServletRequestUtils.getLongParameter(request, REMARK_FOR_LANGUAGE + "Id");
		property.setCurrentLangId(cutLang);
		property.setValue(request.getParameter(REMARK_FOR_LANGUAGE));
		for (Language language : Language.getItems()) {
			if (!language.getId().equals(cutLang)) {
				property.setCurrentLangId(language.getId());
				property.setValue(request.getParameter("remarkForLanguage_" + language.getId()));
			}
		}
		i18nCorePropertyService.createOrUpdate(property);
		return handleCommonRequests(request, response);
	}
	
	public ModelAndView deleteLang(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
		Long langId = ServletRequestUtils.getLongParameter(request, "langId", 0);
		if (langId > 0) {
			Connection con = null;
			try{
				con = DBHelper.getConnection();
				Language language = Language.findById(langId);
				busy = true;
				ListTranslator listTranslator = new ListTranslator(null);
				translator = listTranslator;
				listTranslator.clean(con, language);
				
				translator = new PageTranslator(null, TranslationStrategyType.all);
				translator.clean(con, language);
				
				translator = new MenuTranslator(null);
				translator.clean(con, language);
				
				translator = new DictionaryTranslator(null);
				translator.clean(con, language);
				
				translator = new CustomConstTranslator(null);
				translator.clean(con, language);
				
				translator = new ProductModuleTranslator(null);
				translator.clean(con, language);
				resetCustomConstantsCache(con);
				menuService.flush();
				
				Language.delete(con, language);
				con.commit();
				Language.resetCache();
			} catch (Throwable e) {
				DBHelper.rollback(con);
				logger.error(e);
			} finally {
				busy = false;
				DBHelper.close(con);
			}
		}
		return handleCommonRequests(request, response);
	}
	

	private void addLanguage(HttpServletRequest request) {
		Language newLanguage = new Language();
		NegesoRequestUtils.bindToRequest(newLanguage, request);
		if (StringUtils.isNotBlank(newLanguage.getCode())) {
			Language lang = null;
			try {
				lang = Language.findByCode(newLanguage.getCode());
			} catch (ObjectNotFoundException e) {
				// Ignore
			}
			if (lang == null) {
				Connection con = null;
				PreparedStatement stmt = null;
				try {
					con = DBHelper.getConnection();
					Long langId = DBHelper.getNextInsertId(con, "language_id_seq");
					stmt = con.prepareStatement("INSERT INTO language (id, language, code) VALUES (?, ?, ?)");
					stmt.setLong(1, langId);
					stmt.setString(2, newLanguage.getLanguage());
					stmt.setString(3, newLanguage.getCode());
					stmt.execute();
//					stmt = con.prepareStatement("INSERT INTO interface_language (language, code) VALUES (?, ?)");
//					stmt.setString(1, language.getLanguage());
//					stmt.setString(2, language.getCode());
//					stmt.execute();
				} catch (SQLException e) {
					logger.error(e);
				} finally {
					DBHelper.close(null, stmt, con);
				}
				Language.resetCache();
			}
		}
	} 

	private void translateNews(HttpServletRequest request,
                               ITranslateService translateService) throws ServletRequestBindingException {
		
		String[] itemIds = parseIds(request, "itemIds");
		String[] translateToLang = parseIds(request, "translateToLang");
		String[] listIds = parseIds(request, "listIds");
		Long fromLangId = ServletRequestUtils.getLongParameter(request, "langId", -1);
		
		if (itemIds != null && translateToLang != null && fromLangId > 0) {
			addReplacers(translateService, fromLangId);
			ListTranslator listTranslator = new ListTranslator(translateService);
			Connection con = null;
			try {
				con = DBHelper.getConnection();
				for (String itemId : itemIds) {
					for (int i = 0; i < translateToLang.length; i++) {
						Long toLangId = Long.valueOf(translateToLang[i]); 
						if (StringUtils.isNotBlank(listIds[i])){
							ListItem listItem = ListItem.findById(con, Long.valueOf(itemId));
							ListItem translatedListItem = null;
							if (listItem.getPerLangId() != null) {
								translatedListItem = ListItem.findByPerLangId(con, listItem.getPerLangId(), toLangId);
							}
							if (translatedListItem != null) {
								listTranslator.copyAndTranslateListItem(con, Language.findById(fromLangId), Language.findById(toLangId), listItem, translatedListItem);
							} else {
								listItem.setListId(Long.valueOf(listIds[i]));
								listTranslator.copyAndTranslateListItem(con, Language.findById(fromLangId), Language.findById(toLangId), listItem);
							}
						}
					}
				}			
			} catch (Exception e) {
				logger.error(e);
			} finally {
				DBHelper.close(con);
			}
		}
	}
	
	private String[] parseIds(HttpServletRequest request, String str) {
		String[] ids = null;
		String strIds = ServletRequestUtils.getStringParameter(request, str, StringUtils.EMPTY);
		if(StringUtils.isNotBlank(strIds)) {
			ids = strIds.split(",");
		}
		return ids;
	}

	private void getNewsTranslationDialog(HttpServletRequest request, PreparedModelAndView modelAndView) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		modelAndView.get().setViewName("stm_newsTranslationDialog");
		Long moduleId = ServletRequestUtils.getLongParameter(request, "moduleId", 0L);
		Long fromLangId = ServletRequestUtils.getLongParameter(request, "langId", -1);
		if (fromLangId > 0) {
			try {
				java.util.List<List> newsLists = new ArrayList<List>();
				con = DBHelper.getConnection();
				stmt = con.prepareStatement("SELECT * FROM list WHERE module_id = ? AND  lang_id <> ?");
				stmt.setLong(1, moduleId > 0 ? moduleId : Env.getSite().getModuleId(ModuleConstants.NEWS));
				stmt.setLong(2, fromLangId);
				rs = stmt.executeQuery();
				while (rs.next()) {
					List list = new List();
					list.load(rs);
					newsLists.add(list);
				}
				modelAndView.addToModel("newsLists", newsLists);
				modelAndView.addToModel("fromLang", Language.findById(fromLangId));
			} catch (Exception e) {
				logger.error(e);
			} finally {
				DBHelper.close(rs, stmt, con);
			}
		}
	}

	private java.util.List<PageH> translateSimplePage(HttpServletRequest request,
                                                      ITranslateService translateService) {
		translateService.setPageNameTranslatable(ServletRequestUtils.getBooleanParameter(request, "isPageNameTranslatable", false));
		NewAddedPageTranslationStrategy pageTranslationStrategy = new NewAddedPageTranslationStrategy(translateService, new PageTranslator(translateService));
		pageTranslationStrategy.setListIdsMap(new HashMap<Long, Long>());
		Long pageId = ServletRequestUtils.getLongParameter(request, "id", 0);
		PageH page = PageService.getInstance().findById(pageId);
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		java.util.List<PageH> translatedPages =  new ArrayList<PageH>();
		if (page != null) {
			try {
				con = DBHelper.getConnection();
				Language to = null;
				Language from = Language.findById( page.getLangId());
				addReplacers(translateService, from.getId());
				for (String langId : ServletRequestUtils.getStringParameter(request, "translateToLang", StringUtils.EMPTY).split(",")) {
					
					to = Language.findById(Long.valueOf(langId));
					
//					PageTranslationStrategy pageTranslationStrategy2 = new PageTranslationStrategy(translateService, translator);
//					translateService.setPagesMap(pageTranslationStrategy2.getPageTranslationMap(con, from, to));
					
//					stmt = con.prepareStatement("SELECT * FROM tr_page_statistics WHERE from_page_id = ? AND  from_lang_id = ? AND to_lang_id =? ");
//					stmt.setLong(1, page.getId());
//					stmt.setLong(2, page.getLangId());
//					stmt.setLong(3, to.getId());
//					rs = stmt.executeQuery();
//					if(!rs.next()) {
						 PageH translatedPage = pageTranslationStrategy.copyAndTranslateToSpecialPage(page, con, from, to);
						 translatedPages.add(translatedPage);
//					}
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {
				DBHelper.close(rs, stmt, con);
			}
		}
		return translatedPages;
	}
	
	private void addReplacers(ITranslateService translateService, Long langId) {
		PlaceHolderReplacer phReplacer = new PlaceHolderReplacer();
		phReplacer.setPlaceHolders(getPlaceHolderService().listPlaceHolders(Env.getSiteId()));
		translateService.getReplacers().add(phReplacer);
		I18nCoreProperty i18nCoreProperty = i18nCorePropertyService.findByName(NON_TRANSLATABLE_WORDS_PARAM_NAME, Env.getSiteId());
		if (i18nCoreProperty != null ) {
			i18nCoreProperty.setCurrentLangId(langId);
			NonTranslatableWordsReplacer ntwReplacer = new NonTranslatableWordsReplacer();
			ntwReplacer.setNonTranslatableWords(i18nCoreProperty.getValue());
			translateService.getReplacers().add(ntwReplacer);
		}
		translateService.getReplacers().add(new WhiteSpaceReplacer());
		translateService.getReplacers().add(new AmpReplacer());
	}
	
	private void translate(HttpServletRequest request, Long fromLangId, Long toLangId, ITranslateService translateService, ModelAndView mv, TranslationStrategyType translationStrategyType, boolean isTranslateLinks ){
		Connection con = null;
		try{
			busy = true;
			con = DBHelper.getConnection();
			con.setAutoCommit(false);
			Language fromLanguage = Language.findById(fromLangId);
			Language toLanguage = Language.findById(toLangId);
			
			addReplacers(translateService, fromLangId);
			PageTranslator pageTranslator = new PageTranslator(translateService, translationStrategyType);
			PagesReplacer pagesReplacer = new PagesReplacer();
			if (isTranslateLinks) {
				pagesReplacer.setPages(pageTranslator.getTranslationStrategy().getPageTranslationMap(con, fromLanguage, toLanguage));
				translateService.getReplacers().add(pagesReplacer);
			}
			
			
			if (TranslationStrategyType.all.equals(translationStrategyType) || TranslationStrategyType.allExceptSpecial.equals(translationStrategyType)) {
				ListTranslator listTranslator = new ListTranslator(translateService);
				translator = listTranslator;
				translator.translate(con, fromLanguage, toLanguage);
				
				FormTranslator formTranslator = new FormTranslator(translateService);
				translator = formTranslator;
				translator.translate(con, fromLanguage, toLanguage);
				
				translator = pageTranslator;
				pageTranslator.setOldFormIdToNewFormObjectMap(formTranslator.getOldFormIdToNewFormObjectMap());
				pageTranslator.setListIdsMap(listTranslator.getListIdsMap());
				pageTranslator.translate(con, fromLanguage, toLanguage);
				
				MenuTranslator menuTranslator = new MenuTranslator(translateService);
				menuTranslator.setPageIdsMap(pageTranslator.getPageIdsMap());
				menuTranslator.setPagesReplacer(pagesReplacer);
				translator = menuTranslator;
				translator.translate(con, fromLanguage, toLanguage);
				
				translator = new DictionaryTranslator(translateService);
				translator.translate(con, fromLanguage, toLanguage);
				
				translator = new CustomConstTranslator(translateService);
				translator.translate(con, fromLanguage, toLanguage);
				
				translator = new ProductModuleTranslator(translateService);
				translator.translate(con, fromLanguage, toLanguage);
				resetCustomConstantsCache(con);
			} else if(TranslationStrategyType.newAdded.equals(translationStrategyType)) {
				translator = pageTranslator;
				pageTranslator.setListIdsMap(new HashMap<Long, Long>());
				pageTranslator.translate(con, fromLanguage, toLanguage);
			}
			
			
			menuService.flush();
			makeLog(request, fromLanguage, toLanguage, translateService);
			con.commit();
			mv.addObject("status", "Done!");
		} catch (Throwable e) {
			DBHelper.rollback(con);
			logger.error(e);
			mv.addObject("status", e.getMessage());
		} finally {
			busy = false;
			DBHelper.close(con);
		}
	}

	private void makeLog(HttpServletRequest request, Language fromLanguage,
                         Language toLanguage, ITranslateService translateService) {
		Log log = new Log();
		log.setCharsNumber(Long.valueOf(translateService.getCharsCounter()));
		log.setFromLangCode(fromLanguage.getCode());
		log.setToLangCode(toLanguage.getCode());
		log.setUrl(request.getServerName());
		if (isNegesoAccountUsed()) {
			Env.updateNotCachedSiteProperty(Configuration.STM_CHARS_QUOTA, (Long.valueOf(Env.getNotCachedSiteProperty(Configuration.STM_CHARS_QUOTA, defaultStmCharsQuota.toString())) - translateService.getCharsCounter()) + StringUtils.EMPTY);
		}
		logService.save(log);
	}

	public void setMenuService(IMenuService menuService) {
		this.menuService = menuService;
	}
	
	private void resetCustomConstantsCache(Connection con) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement("SELECT name FROM module");			
			rs = stmt.executeQuery();
			while (rs.next()) {
				DatabaseResourceBundle.clearModuleConsts(rs.getString("name"));
			}
			DatabaseResourceBundle.clearModuleConsts(DatabaseResourceBundle.DICT_COMMON_XSL);
		} catch (SQLException e) {
			throw e;
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}

	public I18nCorePropertyService getI18nCorePropertyService() {
		return i18nCorePropertyService;
	}

	public void setI18nCorePropertyService(
			I18nCorePropertyService i18nCorePropertyService) {
		this.i18nCorePropertyService = i18nCorePropertyService;
	}

	public PlaceHolderService getPlaceHolderService() {
		if (placeHolderService == null) {
			placeHolderService = (PlaceHolderService) DispatchersContainer.getInstance().getBean("core", "placeHolderService");
		}
		return placeHolderService;
	}

	public void setPlaceHolderService(PlaceHolderService placeHolderService) {
		this.placeHolderService = placeHolderService;
	}

	public LogService getLogService() {
		return logService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

}

