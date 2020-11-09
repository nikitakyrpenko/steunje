/*
 * @(#)$Id: LdapLoginCommand.java,v 1.4, 2007-01-10 10:52:55Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.controller.WebFrontController;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.command.GetPageCommand;
import com.negeso.framework.security.EncriptedAuthenticator;
import com.negeso.framework.util.LdapLoginManager;
import com.negeso.framework.view.RedirectView;

/**
 * 
 * This commands processes authentication only of Negeso Superuser's Group
 * 
 * 
 * @author		Andrey V. Morskoy
 * @version		$Revision: 5$
 * @see Negeso's Wiki on Mainline 2.5 
 */
public class LdapLoginCommand extends AbstractCommand {
	 private static Logger logger = Logger.getLogger(LdapLoginCommand.class);
	 
	 public static final String USER_ROLE_KEY = "user_role";
	 
	 
	public ResponseContext execute() {
		 logger.debug("+");
		 //Element parentElement = Xbuilder.createTopEl("page");
		 GetPageCommand command = new GetPageCommand();
		 command.setRequestContext(getRequestContext());
		 ResponseContext responseContext = command.execute();
		 Document document = (Document)responseContext.get(AbstractCommand.OUTPUT_XML);	
		 
		 Element parentElement = document.getDocumentElement();
		 
		 Element loginForm = Xbuilder.addEl(parentElement, "LdapLoginForm", "LdapLogin");
		 ResponseContext response = new ResponseContext();
		 RequestContext request = getRequestContext();
		 SessionData session = request.getSessionData();

		 response.setResultName(LoginCommand.RESULT_DENIED);
		 session.setSuperuser(null);
		 
	     String login = request.getParameter(LoginCommand.INPUT_LOGIN);

	     String interfaceLanguage = request.getParameter(LoginCommand.INPUT_LANGUAGE);
	     if (interfaceLanguage == null){
	          interfaceLanguage = Env.getDefaultInterfaceLanguageCode();
	     }
	     
	     request.getSessionData().setAttribute(LoginCommand.INTERFACE_LANGUAGE, interfaceLanguage);
	     Cookie cookie = new Cookie("interface_language", interfaceLanguage);
	        cookie.setMaxAge(Env.COOKIE_MAX_AGE);
	        cookie.setPath("/");
	        HttpServletResponse httpResponse = 
	            (HttpServletResponse)request.getIOParameters()
	                .get(WebFrontController.HTTP_SERVLET_RESPONSE);
	        httpResponse.addCookie(cookie);
	        
	     buildLangs(loginForm,interfaceLanguage);

	     String decPwd = null;
	     try{
	    	 decPwd = EncriptedAuthenticator.processPassword(request, response, loginForm);
	     }
	     catch (Exception e) {
	    	 logger.error("Failed to decript password - sending undecripted to ldap",e);
	    	 decPwd = request.getParameter("password");
	     }
	     
		 String result = LdapLoginManager.RESULT_DENIED;
		 
    	LdapLoginManager manager = new LdapLoginManager();
    	
    	if(login!=null){
			try{
				result = manager.authUser(login, decPwd, false);
				logger.error("RESULT: "+result);
			}
			catch (AuthenticationException e) {
				logger.error("Not authorized");
				result = LdapLoginManager.RESULT_DENIED;
			}
			catch (CommunicationException e) {
				logger.error("Ldap communication error: ",e);
				result = LdapLoginManager.RESULT_LDAP_NOT_ACCESSIBLE;
			}
			catch (Exception e) {
				logger.error("Ldap authorization error: ",e);
				result = LdapLoginManager.RESULT_DENIED;
			}
		}

		if( LdapLoginManager.RESULT_ACCEPTED.equals(result) ){
			 try{				 
				 User superuser = User.findFirstAdmin();
				 logger.error("USER ID: "+superuser.getId());
				 superuser.setSuperuserLogin(login);
				 session.setSuperuser(superuser);
				 response.setResultName(RESULT_SUCCESS);
				 EncriptedAuthenticator.removeKey(request);
				 
				 LdapLoginManager.storeAuthLogToDb(request, login, superuser.getId(), result);
			 }
			 catch (Exception e) {
				 logger.error("Admin dirty switching error: ",e);
			 }
		 }
		else if( LdapLoginManager.RESULT_NOT_SUPERUSER.equals(result) ||
				LdapLoginManager.RESULT_NOT_ENOUGH_RIGHTS.equals(result) ||
				LdapLoginManager.RESULT_LDAP_NOT_ACCESSIBLE.equals(result)){
			Xbuilder.setAttr(loginForm, "ldapStatus", result);
			LdapLoginManager.storeAuthLogToDb(request, login, null, result);
		}
		else if(login!=null){
			Xbuilder.setAttr(loginForm, "ldapStatus", LdapLoginManager.RESULT_DENIED);
			LdapLoginManager.storeAuthLogToDb(request, login, null, result);
		}
		
		 Map resultMap = response.getResultMap();
         resultMap.put("xml", parentElement.getOwnerDocument());
        
         int finSlash = Env.getHostName().lastIndexOf("/");
         String tmp = Env.getHostName().substring(0,finSlash);
         int shemeSlash = tmp.lastIndexOf("/");
         tmp = tmp.substring(shemeSlash+1,finSlash);
         
         resultMap.put(RedirectView.PARAMETER_DESTINATION, "http://"+tmp+"/admin/");
	     logger.debug("-");
	     return response;
	}

	public static void buildLangs(Element parentEl, String currentCode){
	        logger.debug("+");
	        Connection conn = null;
	        String langsQuery =
	        	"select id, language, code from interface_language order by id;";
	        Statement stmt = null;
	        ResultSet rs = null;
	        try {
	            conn = DBHelper.getConnection();
	            stmt = conn.createStatement();
	            rs = stmt.executeQuery(langsQuery);
	            Element langsEl = Xbuilder.addEl(parentEl,"Languages",null);
            	Xbuilder.setAttr(langsEl,"current",currentCode);
	            while (rs.next()) {
	            	Element lang = Xbuilder.addEl(langsEl,"Language",null);
	            	Xbuilder.setAttr(lang,"language",rs.getString("language"));
	            	Xbuilder.setAttr(lang,"code",rs.getString("code"));
	            }
	        } catch (Exception e) {
	            logger.error("Cannot get list of interface languages", e);
	        }
	        finally {
	        	DBHelper.close(rs, stmt, conn);
	        }
	        logger.debug("-");
	    }
		
	
}
