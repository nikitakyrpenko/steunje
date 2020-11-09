/*
 * @(#)GetEditablePageCommand.java  Created on 04.02.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.page.command;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.condition.ConditionFailedException;
import com.negeso.framework.condition.commands.ConditionalCommand;
import com.negeso.framework.condition.conditions.LoginCondition;
import com.negeso.framework.domain.User;

/**
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class GetEditablePageCommand extends ConditionalCommand {
	
    private static Logger logger =
        Logger.getLogger(GetEditablePageCommand.class);
    
    @Override
	protected  void conditionalExecute() throws ConditionFailedException {
		logger.debug("+-");
		
		if (!Env.getSite().isLive()) {
            getResponseContext().getResultMap()
            .put(OUTPUT_XML, GetPageCommand.biuldSiteStatusWarning());
            getResponseContext().setResultName(RESULT_SUCCESS);
            logger.debug("site is not live");
            return;
		}
		
		getCondition().checkCondition();
        User user = getRequestContext().getSession().getUser();
        GetPageCommand.buildPage(user, getRequestContext(), getResponseContext(), false);        
	}
    
    @Override
	public void setCondition() {
		setCondition(new LoginCondition(this));
	}

}
