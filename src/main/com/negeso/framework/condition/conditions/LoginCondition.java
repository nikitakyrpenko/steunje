/*
 * @(#)$Id: LoginCondition.java,v 1.1, 2007-03-19 17:04:59Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.condition.conditions;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.LdapLoginCommand;
import com.negeso.framework.command.LoginCommand;
import com.negeso.framework.condition.Conditionable;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.command.GetPageCommand;
import com.negeso.framework.security.SecurityGuard;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */
public class LoginCondition extends Condition {

	Logger logger = Logger.getLogger(LoginCondition.class);
	
	public LoginCondition(Conditionable context) {
		super(context);
	}

	@Override
	protected boolean doCheckCondition() {
		logger.debug("+-");
	    User user = getRequest().getSession().getUser();
		if (isConditionFalse(user)) {
			 LoginCommand loginCommand = new LoginCommand();
			 loginCommand.setRequestContext(getRequest());
			 ResponseContext loginResponse = loginCommand.execute();
			 String result = loginResponse.getResultName();
			 if (!result.equals(LoginCommand.RESULT_AUTHENTICATED)) {
				 setResultName(AbstractCommand.RESULT_ACCESS_DENIED);
				 GetPageCommand command = new GetPageCommand();
				 command.setRequestContext(getRequest());
				 ResponseContext responseContext = command.execute();
				 Document document = (Document)responseContext.get(AbstractCommand.OUTPUT_XML);	
				 Element parentElement = document.getDocumentElement();					 
				 Element elLangs = Xbuilder.addEl(parentElement, "loginForm", null);
				 String interfaceLanguage = getRequest().getSession().getInterfaceLanguageCode();
			     if (interfaceLanguage == null){
			          interfaceLanguage = Env.getDefaultInterfaceLanguageCode();
			     }
				 LdapLoginCommand.buildLangs(elLangs, interfaceLanguage);
			     
				 setOutputXML(document);
  	        	 return false;
			 }
		} 
		return true;
	}

	private boolean isConditionFalse(User user) {
		return !getRequest().getSession().isAuthorizedUser() ||
		 	!SecurityGuard.isContributor(user);
	}

	@Override
	protected boolean isConditionParametersPresent() {
		if (getRequest().getParameter(LoginCommand.INPUT_LOGIN) != null) {
			return true;
		}
		return false;
	}

}

