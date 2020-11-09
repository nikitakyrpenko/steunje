/*
 * @(#)ServletFrontController.java  Created on 14.01.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.negeso.framework.util.NegesoRequestUtils;
import org.apache.catalina.util.RequestUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.hibernate3.support.OpenSessionInViewInterceptor;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.command.LoginCommand;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.domain.UserCookie;
import com.negeso.framework.friendly_url.FriendlyUrlDescriptor;
import com.negeso.framework.friendly_url.FriendlyUrlHandler;
import com.negeso.framework.friendly_url.UrlEntityType;
import com.negeso.framework.module.engine.Module;
import com.negeso.framework.module.engine.ModuleEngine;
import com.negeso.framework.module.engine.RuntimeModule;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.HTTPRequestUtil;
import com.negeso.framework.util.Timer;
import com.negeso.framework.util.logging.RequestLogger;
import com.negeso.framework.view.AccessDeniedView;
import com.negeso.framework.view.ResourceNotFoundView;
import com.negeso.framework.view.View;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.newsline.generators.NewsLineXmlBuilder;

/**
 * Application entry point. Normally, all requests from web browsers should go
 * here. This would allow for statistics gathering, authentication, correct
 * application initialization etc.
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class WebFrontController extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(WebFrontController.class);
    private static final Logger perfomanceLogger = Logger.getLogger(PerfomanceLogger.class);
    
    /** Counts processed requests, excluding redirected ones */
    private int requestId = 0;
    
    private static final String REQUEST_COUNTER = "requestCounter";
    
    /** HttpServletRequest in the IOParameters map; normally used by View */
    public static final String HTTP_SERVLET_REQUEST = "HTTP_SERVLET_REQUEST";

    /** HttpServletResponse in the IOParameters map; normally used by View */
    public static final String HTTP_SERVLET_RESPONSE = "HTTP_SERVLET_RESPONSE";

    /** ServletConfig in the IOParameters map; normally used by View */
    public static final String SERVLET_CONFIG = "SERVLET_CONFIG";
    
    private static final String MEDIA_PATH_INDENTIFIER = "media",
								SITE_MAP_PATTERN = "/sitemap_?[a-zA-Z\\-_]*\\.xml";

    private String securityFolder = "";

    private List<String> securityImageList = new ArrayList<String>();

    @Override
    public void init(ServletConfig cfg) throws ServletException {
    	
    	super.init(cfg);
    	Env.setSiteId(new Long(1));
    	CommandFactory.reloadCache();
    	securityFolder = Env.getProperty("security.folder","security");
        securityImageList = Env.getPropertyList("security.file.list", new String[]{"gif", "jpg", "bmp", "png", "js", "css"});
    	DBHelper.startDatabaseMonitor();
    	// instantiate the spring's dispatcher servlets
    	ModuleEngine moduleEngine = ModuleEngine.getInstance();
    	Module[] modules = moduleEngine.getModules();
    	DispatchersContainer dc = DispatchersContainer.getInstance();
    	for (int i = 0; i < modules.length; i++) {
    		String moduleId = modules[i].getId();
    		// obtain spring config locations
    		String[] springConfigFiles = moduleEngine.getSpringConfigs(moduleId);
    		if (springConfigFiles.length > 0) {
	    		logger.debug("+ - init dispatcher servlet for module: " + moduleId);
	    		ModuleEngineDispatcherServlet dispatcher = new ModuleEngineDispatcherServlet(this,StringUtils.join(springConfigFiles, ","));
	    		dispatcher.setModuleName(moduleId);
	    		dispatcher.init(cfg);
	    		dc.getModuleDispatchers().put(moduleId, dispatcher);
    		}
    	}
    	
    	String coreLevelProperty = Env.getProperty("core.cms.log.level", "ERROR");
		Level level = Level.toLevel(coreLevelProperty);
		Enumeration<Logger> e = Logger.getRoot().getLoggerRepository().getCurrentLoggers();
    	while (e.hasMoreElements()) {
    		Logger loger = e.nextElement();
			if (loger.getAppender("CmsAppender") != null) {
				loger.setLevel(level);
			}
			
		}
    	
    	//TODO put descriptor into database in future and redesign structure!!!
    	List<FriendlyUrlDescriptor> list = new ArrayList<FriendlyUrlDescriptor>();
    	list.add(new FriendlyUrlDescriptor("/*.htm*", "/*.xml", "id", "search_news", "list-item-component", UrlEntityType.LIST_ITEM, true));
    	list.add(new FriendlyUrlDescriptor("/*", null, "id", "search_news", "list-item-component", UrlEntityType.LIST_ITEM, true));
    	list.add(new FriendlyUrlDescriptor("/", "-xml/", "search_news", "list-item-component"));
    	FriendlyUrlHandler.put("NEWS_MODULE_ROOT_FRIENDLY_URL_PART", list);
    	list = new ArrayList<FriendlyUrlDescriptor>();
    	list.add(new FriendlyUrlDescriptor("/*.htm*", "/*.xml", "id", "newsline", "news-line-component", UrlEntityType.LIST_ITEM, true));
    	list.add(new FriendlyUrlDescriptor("/*", null, "id", "newsline", "news-line-component", UrlEntityType.LIST_ITEM, true));
    	list.add(new FriendlyUrlDescriptor("/", "-xml/", "newsline", "news-line-component"));
    	FriendlyUrlHandler.put(NewsLineXmlBuilder.NEWSLINE_MODULE_ROOT_FRIENDLY_URL_PART, list);
    	
    	list = new ArrayList<FriendlyUrlDescriptor>();
    	list.add(new FriendlyUrlDescriptor("/*.html", "/*.xml", "id", "faqsimple", "faq-component", UrlEntityType.LIST_ITEM, true));
    	list.add(new FriendlyUrlDescriptor("/", "-xml/", "faqsimple", "faq-component"));
    	FriendlyUrlHandler.put("FAQ_MODULE_ROOT_FRIENDLY_URL_PART", list);
    	CommandFactory.putMatchers(new FriendlyUrlHandler());
    	
    }

	/** Pre-processes requests and delegates them to default implementation */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        synchronized(this) {
            req.setAttribute(REQUEST_COUNTER, ++requestId + " " + req.getRequestURI());
            logger.info("+ " + req.getAttribute(REQUEST_COUNTER));
        }
        
        processSecurity(req, resp);
		if(!processByModuleServlet(req, resp) && checkPermissions(req, resp)) {
			try {
			    super.service(req, resp);
			} catch(Throwable e) {
			    logger.error("Throwable: ", e);
			    writeErrorMessage(e, resp);
			}
        }
		
        logger.info("- " + req.getAttribute(REQUEST_COUNTER));
    }

	@SuppressWarnings("unchecked")
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        Map map = new HashMap();
        NegesoRequestUtils.parseParameters(
                map,
                req.getQueryString(),
                "UTF-8");
        doPipe(req, resp, map);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map params = new HTTPRequestUtil().parseRequestParameters(req, "UTF-8");
        doPipe(req, resp, params);
    }
    
    /** Resolves and executes "Command - View" pipe */
    private void doPipe(HttpServletRequest req, HttpServletResponse resp, Map map) {
    	
    	RequestContext request = buildRequestContext(req, resp, map);
        logger.debug("+");
        Timer totalTimer = new Timer();
        totalTimer.start();
        OpenSessionInViewInterceptor interceptor = new OpenSessionInViewInterceptor();
        interceptor.setSessionFactory(DBHelper.getSessionFactory());
        interceptor.setFlushMode(HibernateAccessor.FLUSH_AUTO);
        WebRequest webRequest = new ServletWebRequest(
                (HttpServletRequest) request.getIOParameters().get(HTTP_SERVLET_REQUEST),
                (HttpServletResponse) request.getIOParameters().get(HTTP_SERVLET_RESPONSE)
        );
        interceptor.preHandle(webRequest);
        
       
        CommandMapping cmap = CommandFactory.resolveCommand(request);
        try {
        	
            Command command = (Command)
            	CommandFactory.getCommandForName(cmap.getCommandName(), request);
            command.setRequestContext(request);
            Timer timer = new Timer();
            timer.start();
            
            ResponseContext response = command.execute();
            PerfomanceLogger.logCommand(command, response.getResultName(), timer);
            
            String result = response.getResultName();
            
            RequestLogger.putRequestToLog(req, map, result);
            Validate.notNull(result, "No result for: "+ cmap.getCommandName());
            
            Map paramsOfBind =
                cmap.evaluateParameters(
                    cmap.getBindParameters(result),
                    request.getParameter(RequestContext.REQUEST_URI));
            
            response.getResultMap().putAll(paramsOfBind);
            String viewClassName = cmap.getBinds().get(result);
            Class viewClass = null;
			if (viewClassName != null) {
				
				
            	viewClass = CommandFactory.getViewClassForName(viewClassName);
            	
            } else {
            	/* "failure" and "access-denied" are globally known views
            	 * and may be omitted in bindings, so check this specially */
            	if (result.equals(AbstractCommand.RESULT_FAILURE)) {
            		viewClass = ResourceNotFoundView.class;
            	} else if (result.equals(AbstractCommand.RESULT_ACCESS_DENIED)){
            		viewClass = AccessDeniedView.class;
            	} else {
            		throw new RuntimeException("Unknown View");
            	}
            }
			
            timer.start();
            View view = (View) viewClass.newInstance();
            view.process(request, response);
            
            PerfomanceLogger.logView(view, timer);
            logger.debug("-");
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
            throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
		} finally {
			if (interceptor != null) {
				try {
					interceptor.postHandle(webRequest, null);
				} catch (Exception e) {logger.error("", e); }
				try {
					interceptor.afterCompletion(webRequest, null);
				} catch (Exception e) {logger.error("", e); }
			}
			perfomanceLogger.info("Total time: " + totalTimer.stop());
        }
		
		
    }

    /** If a request is itended for module, forward to it */
    private boolean processByModuleServlet(HttpServletRequest request,
    		HttpServletResponse response) throws IOException {
        logger.debug("+");
    	String moduleId = RuntimeModule.getModule().obtainModuleId(request.getRequestURI());
    	if (request.getRequestURI().contains("Chowis")){
    		moduleId = "webshop";
		}
    	if (moduleId == null) {
            logger.debug("-");
    		return false;
    	}
   		try {
   			ModuleEngineDispatcherServlet dispatcher = 
   				DispatchersContainer.getInstance().getModuleDispatchers().get(moduleId);
//   			response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//   			response.addHeader("Access-Control-Allow-Credentials", "true");
//   			response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
//   			response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
			dispatcher.service(request, response);
			logger.info("- forwarded to the dispatcher servlet '"
					+ moduleId + "'");
   		} catch (ServletException e) {
   			logger.error(e);
   			throw new IOException(e.getMessage());
   		}
        logger.debug("-");
    	return true;
    }

    /** Sends error page or exception stack trace to browser */
    private void writeErrorMessage(Throwable ex, HttpServletResponse response) {
        logger.debug("+");
        PrintWriter out = null;
        try {
            response.setDateHeader("Expires", 0);
            boolean productionSite = Env.getHostName().contains(".");
            if (productionSite) {
            	String errorHtml = Env.getRealPath("/error.html");
            	response.setContentType("text/html; charset=UTF-8");
            	out = response.getWriter();
            	out.print(FileUtils.readFileToString(new File(errorHtml), "utf-8"));
                System.out.println("[FIND EXEPTION] Exception : "  + ex.getMessage());
            } else {
            	response.setContentType("text/plain; charset=UTF-8");
            	out = response.getWriter();
            	if (ex.getCause() == null) ex.printStackTrace(out);
            	else ex.getCause().printStackTrace(out);
            }
            logger.debug("-");
        } catch (Exception e) {
            logger.error("- Exception", e);
        } finally {
    		IOUtils.closeQuietly(out);
    	}
    }
    
    /**
     * Creates RequestContext:
     * <li> request parameters are placed in a mutable Map (which preserves
     *      parameters insertion order)</li>
     * <li> request uri is cleaned from extra slashes and placed into
     *      request context under the name of RequestContext.REQUEST_URI.
     *      Cleaned uri </li>
     * <li> SessionData is read from http session</li>
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param params ordered map of request parameters
     * @return
     */
    private RequestContext buildRequestContext(
        HttpServletRequest request,
        HttpServletResponse response,
        Map<String, Object> params)
    {
        logger.debug("+");
        Map<String, Object> modifiableMap = new LinkedHashMap<String, Object>();
        for (String key : params.keySet()) {
            modifiableMap.put(key, params.get(key));
        }
        modifiableMap.put(
            RequestContext.REQUEST_URI,
            new String[] { request.getRequestURI().replaceAll("/+", "/") } );
        modifiableMap.put(
            RequestContext.REQUEST_LOCALES,
            new String[] { Env.getLocales(request) } );
        modifiableMap.put(
                RequestContext.REQUEST_URL,
                new String[] { request.getRequestURL().toString() } );
        Map<String, Object> currentIO = new HashMap<String, Object>();
        currentIO.put(HTTP_SERVLET_REQUEST, request);
        currentIO.put(HTTP_SERVLET_RESPONSE, response);
        currentIO.put(SERVLET_CONFIG, getServletConfig());
        RequestContext requestContext =
            new RequestContext(
                modifiableMap,
                SessionData.getSessionData(request),
                currentIO
            );
        logger.debug("-");
        return requestContext;
    }
    
    /** Get last modified date of resource */
    @Override
	public long getLastModified(HttpServletRequest request) {
    	logger.debug("+");
    	String uri = request.getRequestURI();
    	try {
			uri = new String(request.getRequestURI().getBytes("ISO-8859-1"), "UTF-8");
			uri = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	String moduleId = RuntimeModule.getModule().obtainModuleId(uri);
    	if (moduleId != null) {
            logger.debug("- processed by module '" + moduleId + "'");
			return DispatchersContainer.getInstance().
			getModuleDispatchers().get(moduleId).getLastModified(request);
    	}
        if (!uri.matches(".*/media/.*") ){
            logger.debug("- not in media folder");
            return super.getLastModified(request);
        }
		uri = uri.replaceAll(".*/media/", "/media/"); 
    	String filePath = Env.getSite().getRealPath(uri);  	//	this.getServletContext().getRealPath(uri);
        if( filePath == null ){
            logger.debug("- empty file name");
            return super.getLastModified(request);
        }
        File file = new File(filePath);
         if (!file.exists()){
            logger.error("- file does not exist: " + filePath);
            return super.getLastModified(request);
        }
        Folder folder = Repository.get().getFolder( file.getParentFile() );
        if (folder.getContainerId() != null){
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user_object");
            if (!folder.canView(user)) {
                logger.debug("- access denied");
                return System.currentTimeMillis();
            }
        }
        logger.debug("-");
        return file.lastModified();
    }
    
    @Override
    public void destroy() {
    	logger.debug("+");
    	super.destroy();
    	DBHelper.stopDatabaseMonitor();
    	DispatchersContainer.getInstance().destroyModuleDispatchers();
    	logger.debug("-");
    }
    
    private void processSecurity(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    	Cookie[] cookies = httpRequest.getCookies();
    	
    	Cookie saveCookie = getSaveCookie(cookies);
        //executes when user login and cookie with current user exists
        if (saveCookie != null/* && httpRequest.getParameter(LoginCommand.INPUT_LOGOUT) == null*/){
        	SessionData session =  SessionData.getSessionData(httpRequest);
        	if (!session.isSessionStarted()){
        		session.startSession();
        	}else{
        		return;
        	}
        	
        	String cookieLogin = getCookieLogin(saveCookie.getValue());
        	String loginUniqNumber = getUniqNumber(saveCookie.getValue());
        	String interfaceLanguage = null;
        	UserCookie userCookie = null;
    		try {
				userCookie = UserCookie.findById(Long.valueOf(loginUniqNumber));
			} catch (ObjectNotFoundException e) {
				logger.warn("- (cookie for user " + cookieLogin + "exists," +
						"but no user in system)");
				return;
			} catch (NumberFormatException e) {
				logger.error("- (user uniq number is not a number)");
				return;
			}
			try{
				if (userCookie != null && userCookie.getLogin().equals(cookieLogin)){
					User user = User.findByLogin(cookieLogin);	
					if (user != null){
						
						if (Env.isContentFreeze() & !SecurityGuard.isAdministrator(user)){
							logger.error("- ('remember me' does not works due to content freeze and user is not administrator)");
							return;
						}
						
						session.setAttribute(LoginCommand.SAVED_LOGIN, user.getLogin());
						
						for (int i = 0; i < cookies.length; i++) {
		                    if (cookies[i].getName().equals("interface_language"))
		                        interfaceLanguage = cookies[i].getValue();
		                }
						
						if (interfaceLanguage != null){
							session.setAttribute(LoginCommand.INTERFACE_LANGUAGE, interfaceLanguage);
						}
					}
				}
			}catch(ObjectNotFoundException e){
				logger.error("- (cannot found user with cookie's login " + cookieLogin + ")");
			}
        }
    }
    
    /**
	 * @param cookies
	 * @return
	 */
	private Cookie getSaveCookie(Cookie[] cookies) {
		Cookie cookie = null;
		if (cookies != null && cookies.length > 0)
		for (int i = 0 ; i < cookies.length; i++){
			if (cookies[i] != null && cookies[i].getName().equals(LoginCommand.USER_COOKIE) && cookies[i].getValue() != null)
				cookie = cookies[i];
		}
		return cookie;
	}
	/**
	 * @param value
	 * @return
	 */
	private String getUniqNumber(String value) {
		return value.substring(value.indexOf("_") + 1, value.length());
	}
	/**
	 * @param value
	 * @return
	 */
	private String getCookieLogin(String value) {
		return value.substring(0, value.indexOf("_"));
	}
	
    private Cookie generateNewUserCookieAndInsert(String login, String language) {
		UserCookie newUserCookie = new UserCookie(
				login, 
				0L, 
				language,
				new Timestamp(System.currentTimeMillis()),
				Env.getSiteId());
		newUserCookie.generateNewIdNumber();
		newUserCookie.insert();
		Cookie cookie = new Cookie(LoginCommand.USER_COOKIE, newUserCookie.getLogin() + "_" + String.valueOf(newUserCookie.getUniqId()));
		cookie.setMaxAge(Env.COOKIE_MAX_AGE);
        cookie.setPath("/");
        return cookie;
	}
	
	private boolean checkPermissions(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("+");
        boolean result = true;
        PrintWriter out = null;
        try {
            String reqURI = request.getRequestURI();
            for(String img : securityImageList){
                if(reqURI.contains("."+img.trim())){
                   if(reqURI.contains(securityFolder)){
                       Object user = request.getSession().getAttribute(SessionData.USER_ATTR_NAME);
                       result = user instanceof User && (
                                       SecurityGuard.isContributor((User)user) ||
                                       SecurityGuard.isAdministrator((User)user) ||
                                       SecurityGuard.canView((User)user, null)
                       );
                   }
                   break;
                }
            }
            if(!reqURI.matches(SITE_MAP_PATTERN) && reqURI.contains(".xml") && !reqURI.contains(MEDIA_PATH_INDENTIFIER)){
	            Object user = request.getSession().getAttribute(SessionData.USER_ATTR_NAME);
	            result = user instanceof User && SecurityGuard.isAdministrator((User)user);
            }
            if(!result){
                response.setDateHeader("Expires", 0);
                String errorHtml = Env.getRealPath("/error.html");
                response.setContentType("text/html; charset=UTF-8");
                out = response.getWriter();
                out.print(FileUtils.readFileToString(new File(errorHtml), "utf-8"));
                System.out.println("[FIND EXEPTION] Exception");
                logger.error("[FIND EXEPTION]");
                logger.debug("-");
            }
        } catch (Exception e) {
            logger.error("- Exception", e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return result;
    }
	


}
