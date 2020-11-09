/*
 * @(#)$Id: SetContainerCommand.java,v 1.5, 2005-06-06 13:05:06Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.user.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;

/**
 *
 * Set the container id of object. Type(table) of object is customized 
 * by tableId parameter.
 * 
 * WARNING: If you cache objects with container id - cache will be invalid!!! 
 * 
 * @version		$Revision: 6$
 * @author		Olexiy Strashko
 * 
 */
public class SetContainerCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger(SetContainerCommand.class);
	
	/**  */
	public static final String INPUT_CONTAINER_ID = "containerId";

	/**  */
	public static final String INPUT_OBJECT_ID = "objectId";

	/**  */
	public static final String INPUT_ROLE_ID = "roleId";

	/**  */
	public static final String INPUT_TABLE_ID = "tableId";

	/**  */
	public static final String RESULT_SUCCESS = "success";
	
	/**  */ 
	public static final String RESULT_FAILURE = "failure";
	
	/** Object of class File */
	public static final String OUTPUT_XML = "xml";

    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */
    public ResponseContext execute() {

		logger.debug("+");
	    RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		
	    if ( !SecurityGuard.isContributor(request.getSession().getUser())){
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.debug("-");
			return response;
	    }

	    // READ CoNTAINER ID 
	    Long containerId = request.getLong(INPUT_CONTAINER_ID);
	    // NULL is valid value for containerId 
        /*if ( containerId == null ){
			response.setResultName(RESULT_FAILURE);
			logger.error("- error: containerId is null");
			return response;
	    }*/

	    // READ TABLE ID 
	    String tableId = request.getString(INPUT_TABLE_ID, null);
	    if ( tableId == null ){
			response.setResultName(RESULT_FAILURE);
			logger.error("- error: tableId is null");
			return response;
	    }

	    // READ OBJECT ID 
	    Long objectId = request.getLong(INPUT_OBJECT_ID);
	    if ( objectId == null ){
			response.setResultName(RESULT_FAILURE);
			logger.error("- error: objectId is null");
			return response;
	    }

	    String role = request.getString(
	    	INPUT_ROLE_ID, SecurityGuard.ADMINISTRATOR
	    );

	    if( logger.isInfoEnabled() ){
		    logger.info(
		        " containerId: " + containerId +
		        " role: " + role +
		        " tableId: " + tableId +
		        " objectId: " + objectId
		    );
	    }
		
	    Connection con = null;
	    try{
	        logger.debug("+ -");
            con = DBHelper.getConnection();
	        PreparedStatement stmt = con.prepareStatement(
	            "UPDATE " + tableId + " SET container_id=? " +
	            "WHERE id=?"
	        );
            if ( containerId == null ){
                stmt.setObject(1, null); 
            }
            else{
                stmt.setLong(1, containerId.longValue());
            }
	        stmt.setLong(2, objectId.longValue());
	        
	        int resCode = stmt.executeUpdate();
	        logger.info(" update result:" + resCode);

	        stmt.close();

	        Element el = XmlHelper.createPageElement(request);
	        response.getResultMap().put(OUTPUT_XML, el.getOwnerDocument());
	        response.setResultName( RESULT_SUCCESS );
	    }
	    catch( SQLException e ){
	        logger.error("-error", e);
	        response.setResultName(RESULT_FAILURE);
	        return response;
	    } 
	    catch (CriticalException e) {
	        logger.error("-error", e);
	        response.setResultName(RESULT_FAILURE);
	        return response;
        }
	    finally{
	        DBHelper.close(con);
	    }
		logger.debug("-");
		return response;		
    }
}
