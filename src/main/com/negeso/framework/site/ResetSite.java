/*
 * @(#)$Id: ResetSite.java,v 1.2, 2005-06-06 13:05:03Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.site;


import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.page.command.GetEditablePageCommand;
import com.negeso.framework.security.SecurityGuard;

/**
 *
 * ResetSite command. Allows administrator to:
 * - reset current site cache
 * - reset all sites
 * 
 * @version		$Revision: 3$
 * @author		Olexiy Strashko
 * 
 */
public class ResetSite extends AbstractCommand {
    private static Logger logger = Logger.getLogger( ResetSite.class );


    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */
    public ResponseContext execute() {
        RequestContext request = this.getRequestContext();
        ResponseContext response = new ResponseContext();
        if ( !SecurityGuard.isAdministrator(request.getSession().getUser())){
            response.setResultName(RESULT_ACCESS_DENIED);
            return response;
        }
        
        String action = request.getNonblankParameter("action");
        if ("all".equals(action)){
            SiteEngine.get().reset();
            logger.info("SiteEngine is reset");
        }
        else{
            String siteName = Env.getSite().getName();
            Env.getSite().setExpired(true);
            logger.info("Site " + siteName + " is reset");
        }
        Command command = new GetEditablePageCommand();
        command.setRequestContext(request);
        return command.execute();
    }
}
