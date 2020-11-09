/*
 * @(#)$Id: SelectContainerCommand.java,v 1.6, 2005-06-20 16:50:15Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.user.command;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.user.domain.Container;

/**
 *
 * Backend command for select container dialog. Parameters: selectedId and 
 * roleId. 
 * Return list of containers where current user has no less security role then 
 * parameter <code>roleId</code>. Default roleId is <code>Administrator</code>. 
 * One container is marked as selected then parameter <code>selectedId</code> 
 * contain valid container id.   
 * 
 * @version		$Revision: 7$
 * @author		Olexiy Strashko
 * 
 */
public class SelectContainerCommand extends AbstractCommand {
	private static Logger logger = Logger.getLogger(SelectContainerCommand.class);
	
	/**  */
	public static final String INPUT_SELECTED_CONTAINER_ID = "selectedId";

	/**  */
	public static final String INPUT_ROLE_ID = "roleId";

	/**
	 * 
	 */
	public SelectContainerCommand() {
		super();
	}

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
	    
	    try{
		    Element page = XmlHelper.createPageElement(request); 

		    this.getContainersElement(page, request);
		    	
		    response.getResultMap().put( OUTPUT_XML, page.getOwnerDocument() );
		    response.setResultName( RESULT_SUCCESS );
	    }
	    catch( CriticalException e ){
	        logger.error("-error", e);
	        response.setResultName(RESULT_FAILURE);
	        return response;
	    }
		logger.debug("-");
		return response;		
	}
	
	/**
	 * Biuld containers element 
	 * 
	 * @param parent
	 * @param request
	 * @return
	 * @throws CriticalException
	 */
	private Element getContainersElement(Element parent, RequestContext request) 
		throws CriticalException
	{
	    logger.debug("+");
	    // READ INPUT 
	    Long selectedId = request.getLong(INPUT_SELECTED_CONTAINER_ID);
	    // READ INPUT 
	    String role = request.getString(
	    	INPUT_ROLE_ID, SecurityGuard.ADMINISTRATOR
	    );
	    if( logger.isInfoEnabled() ){
		    logger.info(
		        " selectedId: " + selectedId +
		        " role: " + role
		    );
	    }
	    User user = request.getSession().getUser();
	    

	    Element containersEl = Xbuilder.addEl(
	        parent, "containers", null
	    );
	    Xbuilder.setAttr(containersEl, "selected-id", selectedId);
	    Xbuilder.setAttr(containersEl, "role", role);
	    
	    
		Element containerEl = null;
	    Container container = null;
    	for ( Iterator i = Container.getContainers(Env.getSiteId()).iterator(); i.hasNext(); ) {
	        container = (Container) i.next();
	        if ( this.canAccessContainer(container, user, role) ){
		        containerEl = Xbuilder.addEl( containersEl, "container", null );
		        Xbuilder.setAttr( containerEl, "title", container.getName() );
                Xbuilder.setAttr( containerEl, "id", container.getId() );
        		if ( container.getId().equals(selectedId) ){
                    Xbuilder.setAttr( 
			        	containerEl, "selected", "true" 
			        );
	    	    }
	    	}
	    }
	    logger.debug("-");
	    return containersEl;
	}
	
	/**
	 * Solve if user can access to container by role
	 * 
	 * @param container		The Container
	 * @param user			The User
	 * @param role			The role
	 * @return				true if user can access container
	 */
	private boolean canAccessContainer( Container container, User user, String role ){
		if ( SecurityGuard.ADMINISTRATOR.equalsIgnoreCase(role) ){
			return SecurityGuard.isAdministrator(user);
		}
		else if ( SecurityGuard.GUEST.equalsIgnoreCase(role) ){
			return SecurityGuard.canView(user, container.getId());
		}		        
		else if ( SecurityGuard.AUTHOR.equalsIgnoreCase(role) ){
			return SecurityGuard.canEdit(user, container.getId());
		}		        
		else if ( SecurityGuard.EDITOR.equalsIgnoreCase(role) ){
			return SecurityGuard.canEdit(user, container.getId());
		}		        
		else if ( SecurityGuard.MANAGER.equalsIgnoreCase(role) ){
			return SecurityGuard.canManage(user, container.getId());
		}		        
		return false;
	}	
}
