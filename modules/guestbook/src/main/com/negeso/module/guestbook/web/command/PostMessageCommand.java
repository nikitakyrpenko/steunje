/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.guestbook.web.command;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.negeso.SpringConstants;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.module.guestbook.bo.Message;
import com.negeso.module.guestbook.service.MessageService;
import com.negeso.module.guestbook.web.component.RecordsPageComponent;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class PostMessageCommand extends AbstractCommand {

	private static final Logger logger = Logger.getLogger(PostMessageCommand.class);
	
	private static final String GB_MESSAGE = "gb_message";

	private static final String AUTHOR_NAME = "author_name";

	private static final String AUTHOR_EMAIL = "author_email";

	private static final String POSTED_MESSAGES = "posted_messages";

	public static final String GUESTBOOK_LIMIT = "gb_messages_limit";
	
	public int getGbId(RequestContext requestContext) {
		int gb_id = 0;
		String tmpGbId = requestContext.getParameter(RecordsPageComponent.GB_ID);
		
		try {
			gb_id = Integer.parseInt(tmpGbId);
		} catch (NumberFormatException e) {
			logger.error("Incorrect guestbookId=" + tmpGbId);
			gb_id = 0;
		}
		return gb_id;
	}
	
	public ResponseContext execute() {
		logger.debug("+");
		RequestContext request = this.getRequestContext();
		ResponseContext response = new ResponseContext();
		
		if (!request.getSessionData().isSessionStarted()){
			response.setResultName(RESULT_FAILURE);
			logger.warn("- session was not started");
			return response;
		}
		
		Integer posted_messages = (Integer)(request.getSessionData().getAttribute(POSTED_MESSAGES));
		
		if (posted_messages == null) {
			request.getSession().setAttribute(POSTED_MESSAGES, new Integer(1));
		} else {
			if (posted_messages.intValue() > 3) {
				response.setResultName(RESULT_FAILURE);
				logger.info("- tried to post more than 3 messages");
				request.setParameter(GUESTBOOK_LIMIT, "true");
				return response;
			}
			request.getSession().setAttribute(POSTED_MESSAGES, posted_messages.intValue() + 1);
		}

		String action = request.getNonblankParameter("action");
		logger.info("action: " + action);

		try {
			String newMessage = request.getParameter(GB_MESSAGE);

			if (newMessage == null) {
				logger.error("Error: Null message was posted to guestbook.");
				response.setResultName(RESULT_FAILURE);
				logger.debug("- Null message");
				return response;
			}
			if (newMessage.trim().equals("")) {
				logger.warn("Empty message was postedto guestbook");
				response.setResultName(RESULT_SUCCESS);
				logger.debug("- nothing was added");
				return response;
			}

			Message message = new Message();
			message.setAuthorName(request.getParameter(AUTHOR_NAME));
			message.setAuthorEmail(request.getParameter(AUTHOR_EMAIL));
			message.setMessage(newMessage);
			message.setPosted(new Timestamp(System.currentTimeMillis()));
			int gbId = getGbId(request);
			message.setGbId(gbId);

			MessageService messageService = ((MessageService) request.getService(ModuleConstants.GUESTBOOK,
					SpringConstants.GUESTBOOK_MESSAGE_SERVICE));
			messageService.insertMessage(message);
		} catch (Exception ex) {
			logger.error(ex);
		}

		response.setResultName(RESULT_SUCCESS);
		logger.debug("-");
		return response;
	}

}
