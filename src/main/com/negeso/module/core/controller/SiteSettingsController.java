package com.negeso.module.core.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.dao.CountryDao;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.OrderControlHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.site.PageAlias;
import com.negeso.framework.site.PageRedirect;
import com.negeso.framework.site.SiteUrl;
import com.negeso.framework.site.SiteUrlCache;
import com.negeso.framework.site.service.PageAliasService;
import com.negeso.framework.site.service.PageRedirectService;
import com.negeso.framework.site.service.SiteUrlService;
import com.negeso.framework.site_map.PageDescriptor;
import com.negeso.framework.site_map.PagesHandler;
import com.negeso.framework.site_map.SiteMapBuilder;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.core.domain.ConfigurationParameter;
import com.negeso.module.core.service.ModuleService;
import com.negeso.module.core.service.ParameterService;

public class SiteSettingsController extends MultiActionController {
	private static Logger logger = Logger.getLogger(SiteSettingsController.class);
	
	private static final String  FAVICON_PARAM_NAME = "SS.FAVICON_PATH";
	public static final String  FRONTPAGE_PARAM_NAME = "SS.FRONT_PAGES";
	private static final String  BROWSER_PREFIX_PARAM_NAME = "SS.BROWSER_PREFIX";
	private static final String  BROWSER_SUFFIX_PARAM_NAME = "SS.BROWSER_SUFFIX";
	private static final String  IS_PARTICIPATE_OF_RANK_BOOSTER = "ss.isParticipleOfRankBooster";
	private static final String  IS_MULTI_DOMAINS_LINKS = "SS.IS_MULTI_DOMAINS_LINKS";
	private static final String  TITLE_IN_RANK_BOOSTER = "ss.titleInRankBooster";
	private static final String  LANG_DETERMINATION_STRATEGY = "SS.LANG_DETERMINATION_STRATEGY";
	private static final String  DEFAULT_LANGUAGE = "SS.DEFAULT_LANGUAGE";
	private static final String  COPYRIGHT = "SS.COPYRIGHT";
	private static final String  AUTHOR = "SS.AUTHOR";
	private static final String  NOODP = "SS.NOODP";
	public static final String  CANONICAL_META_TAGS = "ss.canonicalMetaTags";
	private static final String  REDIRECT_EXPLAIN_ARTICLE_CLASS = "redirectExplain";
	private ParameterService parameterService;
	private ModuleService moduleService;
	private SiteUrlService siteUrlService;
	private PageAliasService pageAliasService;
	private PageRedirectService pageRedirectService;
	private CountryDao countryDao;
	
	public ModelAndView getEntryPage(HttpServletRequest request,HttpServletResponse response){
		RequestUtil.getHistoryStack(request).push( new Link("CM_SITE_SETTINGS", 
				"/admin/site_settings", true, -1));
		return new PreparedModelAndView("site_settings").get();
	}
	
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response){
		logger.debug("+");
		ModelAndView mav = new ModelAndView("general_settings");
		
		for (String paramName : new String[] {FAVICON_PARAM_NAME, BROWSER_PREFIX_PARAM_NAME, 
				BROWSER_SUFFIX_PARAM_NAME, IS_PARTICIPATE_OF_RANK_BOOSTER, TITLE_IN_RANK_BOOSTER,
				IS_MULTI_DOMAINS_LINKS,LANG_DETERMINATION_STRATEGY,COPYRIGHT,AUTHOR,DEFAULT_LANGUAGE,NOODP,CANONICAL_META_TAGS}) {
			ConfigurationParameter param = parameterService.findParameterByName(paramName);
			if (param == null) {
				param = createSeiteSettingsParam(paramName);
			}
			mav.addObject(paramName.substring(3), param.getValue());
		}
		
		ConfigurationParameter frontPageParam = parameterService.findParameterByName(FRONTPAGE_PARAM_NAME);
		if(frontPageParam  == null)
			frontPageParam  = createSeiteSettingsParam(FRONTPAGE_PARAM_NAME);
		Map<String,String> curLangFrontPages = parseLangParam(frontPageParam);
		
		
		List<Language> languages = Language.getItems();
		Map<Language,String> langFrontPages = new HashMap<Language, String>();
		for(Language lang:languages){
			langFrontPages.put(lang, curLangFrontPages.get(lang.getCode()));
		}
		List<PageH> availablePage = PageService.getInstance().listAll();
		
		mav.addObject("languages", Language.getItems());
		mav.addObject("langFrontPages", langFrontPages);
		mav.addObject("avialeblePages", availablePage);
		mav.addObject("siteUrls", siteUrlService.listBySiteId(Env.getSiteId()));
		mav.addObject("edition", Env.getProperty("EDITION"));
		
		RequestUtil.getHistoryStack(request).push( new Link("CM_GENERAL_SETTINGS", 
				"/admin/site_settings?action=edit", true));
		logger.debug("-");
		return mav;
	}
	
	public ModelAndView getHostNames(HttpServletRequest request,HttpServletResponse response){
		String todo = request.getParameter("todo");
		String url = request.getParameter("url");
		if ("save".equals(todo) && StringUtils.hasText(url)) {
			SiteUrl siteUrl = new SiteUrl();
			NegesoRequestUtils.bindToRequest(siteUrl, request);
			siteUrl.setSiteId(Env.getSiteId());
			siteUrlService.createOrUpdate(siteUrl);
		}else if ("delete".equals(todo)) {
			SiteUrl siteUrl = new SiteUrl();
			NegesoRequestUtils.bindToRequest(siteUrl, request);
			siteUrlService.delete(siteUrl);
		}
		List<SiteUrl> urls = siteUrlService.listBySiteId(Env.getSiteId());
		RequestUtil.getHistoryStack(request).push( new Link("CM_HOST_NAMES", 
				"/admin/site_settings?action=getHostNames", true));
		return new PreparedModelAndView("site_urls").addToModel("urls", urls)
		.addToModel("current", request.getServerName())
		.addToModel("languages", Language.getItems())
		.get();
	}
	
	public ModelAndView saveDomainGoogleCodes(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long id = ServletRequestUtils.getLongParameter(request, NegesoRequestUtils.INPUT_ID);
		SiteUrl siteUrl = siteUrlService.findById(id);
		if (siteUrl != null) {
			NegesoRequestUtils.bind(siteUrl, request);
			siteUrlService.createOrUpdate(siteUrl);
		}
		return null;
	}
	
	public ModelAndView clearCache(HttpServletRequest request,HttpServletResponse response) {
		File cacheDir = new File(request.getSession().getServletContext().getRealPath("/WEB-INF/generated/html_cache/"));
	    try {
	        if (cacheDir.exists()) {
	        	FileUtils.cleanDirectory(cacheDir);
	        }
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return new PreparedModelAndView("site_settings").get();
	}
	
	public ModelAndView getUrlAliases(HttpServletRequest request, HttpServletResponse response) throws ObjectNotFoundException {
		String todo = request.getParameter("todo");
		PageAlias pageAlias = new PageAlias();
		if ("save".equals(todo)) {
			NegesoRequestUtils.bindToRequest(pageAlias, request);
			String params = ServletRequestUtils.getStringParameter(request, "params", org.apache.commons.lang.StringUtils.EMPTY);
			params = org.apache.commons.lang.StringUtils.replace(params, "amp;", "&");
			pageAlias.setSiteId(Env.getSiteId());
			PageH page = PageService.getInstance().findById(pageAlias.getPageId());
			pageAlias.setLangId(page.getLangId());
			pageAlias.setLink(page.getFilename() + params);
			pageAliasService.createOrUpdate(pageAlias);
		} else if ("delete".equals(todo)) {
			NegesoRequestUtils.bindToRequest(pageAlias, request);
			pageAliasService.delete(pageAlias);
		} else if ("checkOnUniqueness".equals(todo)) {
			String fileName = ServletRequestUtils.getStringParameter(request, "fileName", "");
			boolean isUnique = true;
			pageAlias = pageAliasService.findByFilename(fileName);
			if (pageAlias != null && !pageAlias.getId().equals(getIdParam(request))){
				isUnique = false;
			}
			
				PageH page = PageService.getInstance().findByFileName(fileName);				
				if (page != null) {
					isUnique = false;
				}
			
			return new PreparedModelAndView("isUnique").addToModel("isUnique", isUnique).get();
		}
		Long pageId = ServletRequestUtils.getLongParameter(request, "page_id", 0L);
		List<PageAlias> aliases = pageId > 0 
				? pageAliasService.readAll(Env.getSiteId(), pageId) 
				: pageAliasService.readAll(Env.getSiteId());
		Link link = new Link("CM_URL_ALIASES", "/admin/site_settings?action=getUrlAliases", true);
		if (pageId > 0) {
			link.setLevel(-1);
		}
		RequestUtil.getHistoryStack(request).push(link);
		return new PreparedModelAndView("page_aliases").
			addToModel("aliases", aliases).
			addToModel("languages", Language.getItems()).
			addToModel("pageId", pageId)
			.get();
	}
	
	
	
	public ModelAndView redirects(HttpServletRequest request,HttpServletResponse response) throws ObjectNotFoundException {
		String todo = request.getParameter("todo");
		PageRedirect pageRedirect = new PageRedirect();
		
		if ("save".equals(todo)) {
			NegesoRequestUtils.bindToRequest(pageRedirect, request);
			if (pageRedirect.getId() == null ||pageRedirect.getId() < 1L ){
				pageRedirect.setOrder(getNextRedirectOrder());
			}
			pageRedirect.setSiteId(Env.getSiteId());
			pageRedirectService.createOrUpdate(pageRedirect);
		
		}else if ("delete".equals(todo)) {
			Long id = NegesoRequestUtils.getId(request, null);
			if(id != null){
				pageRedirect = pageRedirectService.find(id);
				pageRedirectService.delete(pageRedirect);
			
			}
		}else if ("up".equals(todo)) {
			Long id = NegesoRequestUtils.getId(request, null);
			if(id != null){
				pageRedirect = pageRedirectService.find(id);
				moveUp(pageRedirect);
			}
			
		}else if ("down".equals(todo)) {
			Long id = NegesoRequestUtils.getId(request, null);
			if(id != null){
				pageRedirect = pageRedirectService.find(id);
				moveDown(pageRedirect);
			}
		}

		List<PageRedirect> redirects = pageRedirectService.listAll();
		Link link = new Link("CM_REDIRECTS", "/admin/site_settings?action=redirects", true);
		
		
		Article redirectExplainArticle = null;
		try{
			redirectExplainArticle = Article.findByClass(REDIRECT_EXPLAIN_ARTICLE_CLASS, Env.getInterfaceLanguageCode(request));
		}catch (ObjectNotFoundException e) {
			 redirectExplainArticle = Article.findByClass(REDIRECT_EXPLAIN_ARTICLE_CLASS, "nl");	
		}catch (Exception e) {
			logger.error("Can't found redirect explain article");
		}
		RequestUtil.getHistoryStack(request).push(link);
		return new PreparedModelAndView("redirects").
			addToModel("redirectExplainArticle", redirectExplainArticle).
			addToModel("isSU", isSuperUser(request)).
			addToModel("redirects", redirects).
			addToModel("languages", Language.getItems()).
			addToModel("countries", countryDao.readAll())
			.get();
	}
	
	private Long getNextRedirectOrder(){
		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			return  new OrderControlHelper("page_redirect").getNextInsertOrder(conn, null);
			
		} catch (SQLException e) {
			logger.error(e);
		}finally{
			DBHelper.close(conn);
		}
		return null;
	}
	
	
	
	
	private void moveDown(PageRedirect pageRedirect) {
		if(pageRedirect == null){
			return;
		}

		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			OrderControlHelper orderHelper = new OrderControlHelper("page_redirect");
			orderHelper.moveDown(conn, pageRedirect.getId(), pageRedirect.getOrder());
			
		} catch (SQLException e) {
			logger.error(e);
		}finally{
			DBHelper.close(conn);
		}
		
	}

	private void moveUp(PageRedirect pageRedirect) {
			if(pageRedirect == null){
				return;
			}

			Connection conn = null;
			try {
				conn = DBHelper.getConnection();
				OrderControlHelper orderHelper = new OrderControlHelper("page_redirect");
				orderHelper.moveUp(conn, pageRedirect.getId(), pageRedirect.getOrder());
				
			} catch (SQLException e) {
				logger.error(e);
			}finally{
				DBHelper.close(conn);
			}
			
		
	}

	public ModelAndView getEditAliasDialog(HttpServletRequest request,HttpServletResponse response){
		SiteMapBuilder.getInstance().getHandlers();
		Long langId = NegesoRequestUtils.getInterfaceLanguage(request).getId();
		Map<String, PageDescriptor> descriptors = new HashMap<String, PageDescriptor>();
		Long pageId = ServletRequestUtils.getLongParameter(request, "page_id", 0L);
		PageAlias pageAlias = new PageAlias();
		if (pageId == 0) {
			String id = request.getParameter("id");
			if (StringUtils.hasText(id)) {
				pageAlias = pageAliasService.find(new Long(request.getParameter("id")));			
			}
			for (Entry<String, PagesHandler> entry : SiteMapBuilder.getInstance().getHandlers().entrySet()) {
				descriptors.put(entry.getKey(), entry.getValue().getPages(langId, 1L, Boolean.TRUE));
			}
		}
		return new PreparedModelAndView("page_chooser").
			addToModel("descriptors", descriptors).
			addToModel("pageAlias", pageAlias).
			addToModel("pageId", pageId)
			.get();
	}
	
	
	
	
	
	public ModelAndView save(HttpServletRequest request,HttpServletResponse response){		
		logger.debug("+");
		
		for (String paramName : new String[] {FAVICON_PARAM_NAME, BROWSER_PREFIX_PARAM_NAME, 
				BROWSER_SUFFIX_PARAM_NAME, IS_PARTICIPATE_OF_RANK_BOOSTER, TITLE_IN_RANK_BOOSTER, 
				IS_MULTI_DOMAINS_LINKS, LANG_DETERMINATION_STRATEGY,COPYRIGHT,AUTHOR,DEFAULT_LANGUAGE,NOODP,CANONICAL_META_TAGS}) {
			ConfigurationParameter param = parameterService.findParameterByName(paramName);
			param.setValue(request.getParameter(paramName.substring(3)));
			parameterService.save(param, false);
		}
		
		List<Language> languages = Language.getItems();
		String langFrontPages = "";
		List<SiteUrl> list = siteUrlService.listBySiteId(Env.getSiteId());
		Map<Long, SiteUrl> map = new HashMap<Long, SiteUrl>();
		for (SiteUrl siteUrl : list) {
			siteUrl.getLangIds().clear();
			map.put(siteUrl.getId(), siteUrl);
		}
		for(Language lang:languages){
			String frontPage = request.getParameter("lang_"+lang.getCode());
			langFrontPages += lang.getCode()+"="+frontPage+";";
			try {
				Long siteUrlId = ServletRequestUtils.getLongParameter(request, "site_url_id_for_lang_code_" + lang.getCode());
				SiteUrl siteUrl = map.get(siteUrlId);
				if (siteUrl != null) {
					siteUrl.getLangIds().add(lang.getId());			
				}
			} catch (ServletRequestBindingException e) {
			}
		}	
		ConfigurationParameter frontPageParam = parameterService.findParameterByName(FRONTPAGE_PARAM_NAME);
		frontPageParam.setValue(langFrontPages);
		parameterService.save(frontPageParam, false);
		siteUrlService.update(list);
		SiteUrlCache.resetCache();
			
		logger.debug("-");	
		return edit(request,response);
	}
	
	
	private Map<String, String> parseLangParam(ConfigurationParameter frontPageParam) {
		Map<String, String> langFrontPages = new HashMap<String, String>();
		if (frontPageParam == null || frontPageParam.getValue() == null)
			return langFrontPages;
		String[] langValues = frontPageParam.getValue().split(";");
		for(String langValue: langValues){
			if (langValue == null || "".equals(langValue.trim()))
				continue;
			String[]keyValue = langValue.split("=");
			if (keyValue.length == 2){
				langFrontPages.put(keyValue[0], keyValue[1]);
			}
			
		}
		return langFrontPages;
	}


	
	
	private ConfigurationParameter createSeiteSettingsParam(String paramName) {
		Long module = moduleService.getModuleByName("core");
		
		ConfigurationParameter parameter = new ConfigurationParameter();
		parameter.setModuleId(module);
		parameter.setName(paramName);
		parameter.setDescription(paramName);
		parameter.setReadonly(false);
		parameter.setResetCache(true);
		parameter.setRequired(true);
		parameter.setVisible(false);
		parameter.setSiteId(1L);
		parameterService.save(parameter);
		return parameter;
	}
	
	
	private Boolean isSuperUser(HttpServletRequest request){
		Object user = request.getSession().getAttribute(SessionData.USER_ATTR_NAME);
        if ( user == null || ! (user instanceof User) ){
            return false;
        }
        return ((User) user).isSuperUser();
	}
	
	
	private static Long getIdParam (HttpServletRequest request) {
		return ServletRequestUtils.getLongParameter(request, "id", -1L);
	}


	


	public ParameterService getParameterService() {
		return parameterService;
	}


	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}


	public ModuleService getModuleService() {
		return moduleService;
	}


	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}

	public SiteUrlService getSiteUrlService() {
		return siteUrlService;
	}

	public void setSiteUrlService(SiteUrlService siteUrlService) {
		this.siteUrlService = siteUrlService;
	}

	public PageAliasService getPageAliasService() {
		return pageAliasService;
	}

	public void setPageAliasService(PageAliasService pageAliasService) {
		this.pageAliasService = pageAliasService;
	}

	public void setPageRedirectService(PageRedirectService pageRedirectService) {
		this.pageRedirectService = pageRedirectService;
	}

	public void setCountryDao(CountryDao countryDao) {
		this.countryDao = countryDao;
	}
	
	
	
	
}
