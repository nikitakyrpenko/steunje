/*
 * @(#)$Id: BrowseCategory.java,v 1.7, 2006-05-30 09:30:43Z, Dmitry Dzifuta$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.document.command;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.document.DocumentModule;
import com.negeso.module.document.domain.SortOrder;
import com.negeso.module.document.generators.CategoryXmlBuilder;


/**
 *
 * Browse category command
 * 
 * @version		$Revision: 8$
 * @author		Olexiy Strashko
 * 
 */
public class BrowseCategory extends AbstractCommand{
    private static Logger logger = Logger.getLogger( BrowseCategory.class );
    private static final String RESULT_BROWSE_CATEGORY = "success";
    private static final String INPUT_CATEGORY_ID = "category_id";


    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */
    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = this.getRequestContext();
        ResponseContext response = new ResponseContext();
        
        if ( !SecurityGuard.isContributor(request.getSession().getUser()) ){
            response.setResultName( AbstractCommand.RESULT_ACCESS_DENIED );
            return response;
        }
        
        Element page = null;
        Connection con = null;
        try{
            con = DBHelper.getConnection();
            page = XmlHelper.createPageElement( request );
            
            Long categoryId = request.getLong( INPUT_CATEGORY_ID );
            if ( categoryId == null ){
                categoryId = DocumentModule.get().getRootCategory(con).getId();
            }
            
            CategoryXmlBuilder.buildAdminCategoryXml(
                categoryId,
                con, 
                page, 
                request.getSession().getUser().getId(),
                request.getSession().getLanguage().getId(),
                SortOrder.getSortOrder(request)
            );

            response.setResultName( RESULT_BROWSE_CATEGORY );
        }
        catch( CriticalException e ){
            logger.error("-error", e);
            response.setResultName(RESULT_FAILURE);
        }
        catch( SQLException e ){
            logger.error("-error", e);
            response.setResultName(RESULT_FAILURE);
        }
        finally{
            DBHelper.close(con);
        }
        if ( page != null ){
            response.getResultMap().put( OUTPUT_XML, page.getOwnerDocument() );
        }
        return response;
    }
}
