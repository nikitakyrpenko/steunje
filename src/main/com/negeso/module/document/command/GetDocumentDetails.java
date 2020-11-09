/*
 * @(#)$Id: GetDocumentDetails.java,v 1.5, 2006-04-04 15:30:35Z, Dmitry Dzifuta$
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
import com.negeso.module.document.generators.DocumentListXmlBuilder;

/**
 *
 * Get document details
 * 
 * @version		$Revision: 6$
 * @author		Olexiy Strashko
 * 
 */
public class GetDocumentDetails extends AbstractCommand {
    private static Logger logger = Logger.getLogger( GetDocumentDetails.class );
    private static final String RESULT_DOCUMENT_DETAILS = "success";
    private static final String INPUT_DOCUMENT_ID = "document_id";
    

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
            
            
            Long docId = request.getLong(INPUT_DOCUMENT_ID);
            DocumentListXmlBuilder.buildDocumentDetailsXml(
                docId,
                con, 
                page, 
                request.getSession().getLanguage().getId(),
                request.getSession().getUser().getId() 
            );
            
            response.setResultName( RESULT_DOCUMENT_DETAILS );
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
