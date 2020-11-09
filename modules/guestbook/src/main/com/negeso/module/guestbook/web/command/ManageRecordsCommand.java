package com.negeso.module.guestbook.web.command;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.SpringConstants;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;

import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.SecurityGuard;

import com.negeso.module.guestbook.service.MessageService;
import com.negeso.module.guestbook.web.component.RecordsPageComponent;

public class ManageRecordsCommand extends AbstractCommand {

	private static Logger logger = Logger.getLogger(ManageRecordsCommand.class);
	
	private static final String PUBLISH_MESSAGE_ACTION = "gb_publish";
	private static final String DELETE_MESSAGE_ACTION = "gb_delete";
		
	private static final String MESSAGE_ID = "id";

	public ResponseContext execute() {
		logger.debug("+");
		RequestContext request = this.getRequestContext();
		ResponseContext response = new ResponseContext();
		
		// TODO move to security filter
		if ( !SecurityGuard.isContributor(request.getSession().getUser()) ){
			response.setResultName( AbstractCommand.RESULT_ACCESS_DENIED );
			return response;
		}
		
		String action = request.getNonblankParameter( "action" );
		logger.info("action: " + action);
		
		try {
			Long id = request.getLong(MESSAGE_ID);
	    	if (id == null) {
	    		//logger.warn("Error: Message Id is null");
				id = 0L; 
	    	}
			if (PUBLISH_MESSAGE_ACTION.equals(action)) {
				((MessageService) request.getService(ModuleConstants.GUESTBOOK,
						SpringConstants.GUESTBOOK_MESSAGE_SERVICE)).publishMessage(id);
			} else if (DELETE_MESSAGE_ACTION.equals(action)) {
				((MessageService) request.getService(ModuleConstants.GUESTBOOK,
						SpringConstants.GUESTBOOK_MESSAGE_SERVICE)).deleteMessage(id);
			}
			response.setResultName(RESULT_SUCCESS);
			Element page = createPageElement(request);
			response.getResultMap().put( OUTPUT_XML, page.getOwnerDocument() );
		}
		catch( CriticalException e ){
			logger.error("-error", e);
			response.setResultName(RESULT_FAILURE);
		}
		logger.debug("-");
		return response;
	}

	private Element createPageElement(RequestContext request) {
		Element page = XmlHelper.createPageElement( request );
		RecordsPageComponent rec = new RecordsPageComponent();
		Element guestbook = rec.getElement(page.getOwnerDocument(), request, null);
		page.appendChild(guestbook);
		return page;
	}
	
}
