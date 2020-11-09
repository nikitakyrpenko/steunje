/*
 * @(#)$Id: GetCategoryDetails.java,v 1.7, 2006-04-04 15:30:35Z, Dmitry Dzifuta$
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
import com.negeso.framework.domain.Language;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.document.domain.Category;
import com.negeso.module.document.generators.CategoryXmlBuilder;

/**
 *
 * Get category details
 * 
 * @version		$Revision: 8$
 * @author		Olexiy Strashko
 * 
 */
public class GetCategoryDetails extends AbstractCommand {
    private static Logger logger = Logger.getLogger( GetCategoryDetails.class );
    private static final String RESULT_CATEGORY_DETAILS = "success";
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
                logger.error("!!!Error: catId is null");
            }

            if ( "true".equals(request.getNonblankParameter("is_new")) ){
                CategoryXmlBuilder.buildNewCategoryDetailsXml(
                    categoryId,
                    con, 
                    page, 
                    request.getSession().getUser().getId(),
                    request.getSession().getLanguage().getId()
                );

                // new category
                page.appendChild( 
                    Language.getDomItems(page.getOwnerDocument()) 
                );
            }
            else{
                logger.info("get category: " + categoryId);
                Category cat = Category.findById(con, categoryId);  
                if ( cat == null ){
                    logger.error("ERROR: categoryId not found by id:" + categoryId);
                    response.setResultName(RESULT_FAILURE);
                }
                page.appendChild( 
                    Language.getDomItems(page.getOwnerDocument(), cat.getLangId())
                );

                CategoryXmlBuilder.buildCategoryDetailsXml(
                    categoryId,
                    con, 
                    page, 
                    request.getSession().getUser().getId(),
                    request.getSession().getLanguage().getId()
                );
            }

            response.setResultName( RESULT_CATEGORY_DETAILS );
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
