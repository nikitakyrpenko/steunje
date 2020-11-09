package com.negeso.module.guestbook.web.component;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.SpringConstants;
import com.negeso.framework.Env;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.generators.PageNavigator;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.guestbook.bo.Guestbook;
import com.negeso.module.guestbook.bo.Message;
import com.negeso.module.guestbook.service.GuestbookService;
import com.negeso.module.guestbook.service.MessageService;
import com.negeso.module.guestbook.web.command.PostMessageCommand;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.page.AbstractPageComponent;
import com.octo.captcha.image.ImageCaptcha;
import com.negeso.module.job.State;

public class RecordsPageComponent extends AbstractPageComponent {

	private static Logger logger = Logger.getLogger(RecordsPageComponent.class);
	
	private static final String GB_POST = "gb_post";
	public static final String GB_ID = "gb_id";
	
	private static final String GB_MESSAGE = "gb_message";
    private static final String AUTHOR_NAME = "author_name";
    private static final String AUTHOR_EMAIL = "author_email";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.negeso.framework.command.Command#execute()
	 */
	public Element getElement(Document document, RequestContext request, Map parameters) {
		logger.debug("+");
	    
		int gbId = getGbId(request, parameters);
		boolean isPublishedOnly = Boolean.valueOf(Env.getProperty("guestbook.messages.publishedOnly", "false")); 
			
		try {
			String action = request.getNonblankParameter("action");
			logger.debug("action: " + action);

			if (GB_POST.equals(action)) {
				String captchaInput = request.getParameter("captcha_input");
			    ImageCaptcha captcha = (ImageCaptcha) request.getSession().getAttribute("captcha");
				if (!captcha.validateResponse(captchaInput)  ){
			        return generatePushGuestFormXml(request, document);
			    } else {
					Command postMessageCommand = new PostMessageCommand();
					request.setParameter(GB_ID, String.valueOf(gbId));
					postMessageCommand.setRequestContext(request);
					postMessageCommand.execute();
			    	return generatePostMessageXml(request, gbId, document);
			    }
			}
			
			GuestbookService guestbookService = ((GuestbookService) request.getService(ModuleConstants.GUESTBOOK,
					SpringConstants.GUESTBOOK_GUESTBOOK_SERVICE));
			List<Guestbook> gbs = guestbookService.listGuestbooks();

			if (gbId == 0) { // build guestbook list and return it
				return generateGuestbooksXml(document, gbs);
			}
			
			MessageService messageService = ((MessageService) request.getService(ModuleConstants.GUESTBOOK,
					SpringConstants.GUESTBOOK_MESSAGE_SERVICE));
			
			int countOfRecords = (isPublishedOnly && parameters != null) ?
					messageService.countPublishedRecords(gbId) :
					messageService.countRecords(gbId);

			int currentPageId = getCurrentPageId(request);
			int portion_size = Integer.parseInt(Env.getProperty("guestbook.paging.portionsize", "10"));
			int items_per_page = Integer.parseInt(Env.getProperty("guestbook.paging.itemsperpage", "20"));
			
			List<Message> messages = (isPublishedOnly && parameters != null) ?
					messageService.listPublishedMessages((currentPageId-1)*items_per_page, items_per_page, gbId) :
					messageService.listMessages((currentPageId-1)*items_per_page, items_per_page, gbId);
				
			String gbName = getGbName(gbs, gbId);
			return generateGuestbookXml(document, messages, isPublishedOnly,
					gbId, gbName, items_per_page, portion_size, countOfRecords, currentPageId);
			
		} catch (Exception e) {
			logger.error("error", e);
			Element elt = Xbuilder.createEl(document, "guestbook", null);
			Xbuilder.setAttr(elt, "error-clause", e.getMessage() );
			return elt;
		}
	}

	public int getGbId(RequestContext requestContext, Map parameters) {
		int gbId;
		// if gb_id is not defined or incorrect it is set to 0
		String tmpGbId = requestContext.getParameter(GB_ID);
		if (tmpGbId == null && parameters != null) {
			tmpGbId = this.getStringParameter(parameters, GB_ID);
		}
		
		try {
			gbId = Integer.parseInt(tmpGbId);
		} catch (NumberFormatException e) {
			logger.debug("wrong guestbook Id=" + tmpGbId);
			gbId = 0;
		}
		return gbId;
	}
	
	private String getGbName(List<Guestbook> guestbooks, int gbId) {
		String gbName = "";
		for (Guestbook g : guestbooks) {
			if (g.getId().intValue() == gbId) {
				gbName = g.getName();
				break;
			}
		}
		return gbName;
	}
	
	private int getCurrentPageId(RequestContext requestContext) {
		int currentPageId = 1;
		Integer tmpint = requestContext.getIntValue("page_id");
		if (tmpint != null)
			currentPageId = tmpint.intValue();
		if (currentPageId <= 0)
			currentPageId = 1;
		return currentPageId;
	}
	
	private static Element generatePushGuestFormXml(
            RequestContext request, Document document){
        logger.debug("+");
		Element guestbookEl = Xbuilder.createEl(document, "guestbook", null);
        guestbookEl.setAttribute("mode", State.ERROR_MODE);
        Element gb_response = Xbuilder.addEl(guestbookEl, "guestbook_response", null);
        Xbuilder.setAttr(gb_response, "text", "You entered incorrect text");
        logger.debug("- Thank you message");
      
        String newMessage = request.getParameter(RecordsPageComponent.GB_MESSAGE);
        String name = request.getParameter(RecordsPageComponent.AUTHOR_NAME);
        String email = request.getParameter(RecordsPageComponent.AUTHOR_EMAIL);
        
        Element formParameters = Xbuilder.addEl(
                guestbookEl,
                "form-params",
                null
        );
        formParameters.setAttribute("message",  newMessage!=null
                                              ? newMessage
                                              : " "
        );
        formParameters.setAttribute("name",  name!=null
                                            ? name
                                            : " "
        );
        formParameters.setAttribute("email",  email!=null
                                            ? email
                                            : " "
        );            
        logger.debug("-");
        return guestbookEl;
        
    }
	
	private Element generatePostMessageXml(RequestContext request, int gb_id, Document document) {
		Element elt = Xbuilder.createEl(document, "guestbook", null);
		
		if (request.getParameter(PostMessageCommand.GUESTBOOK_LIMIT) != null 
			&& request.getParameter(PostMessageCommand.GUESTBOOK_LIMIT).equals("true"))
		{
			elt.setAttribute("mode", "not posted");
			elt.setAttribute("description", "message_per_session_limit_exceeded");
			logger.debug("- not posted");
		}
		else {
			elt.setAttribute("mode", "posted");
			Element gb_response = Xbuilder.addEl(elt, "guestbook_response", null);
			Xbuilder.setAttr(gb_response, "text", "Thank you for message");
			logger.debug("- Thank you message");
		}
		return elt;
	}

	private Element generateGuestbooksXml(Document document, List<Guestbook> guestbooks) {
		Element guestbooksEl = Xbuilder.createEl(document, "guestbooks", null);
		for (Guestbook g : guestbooks) {
			Element gbook = Xbuilder.addEl(guestbooksEl, "guestbook", null);
			Xbuilder.setAttr(gbook, "id", g.getId());
			Xbuilder.setAttr(gbook, "name", g.getName());
		}
		return guestbooksEl;
	}
	
	private Element generateGuestbookXml(Document document,
			List<Message> messages, boolean isPublishedOnly, int gbId, String gbName,
			int items_per_page, int portion_size, int countOfRecords, int currentPageId) {
		Element elt = Xbuilder.createEl(document, "guestbook", null);
		Xbuilder.setAttr(elt, "publishedOnly", isPublishedOnly);
		logger.error("isPublishedOnly=" + isPublishedOnly);
		Xbuilder.setAttr(elt, "id", gbId);
		Xbuilder.setAttr(elt, "name", gbName);
			
		for (Message m : messages) {
			 Element record = Xbuilder.addEl(elt, "record", null);
			 Xbuilder.setAttr(record, "id",m.getId());
			 Xbuilder.setAttr(record, "posted", (m.getPosted()).toString().substring(0, 16));
					 
			 if (m.getPublished() != null) {
				 Xbuilder.setAttr(record, "published", (m.getPublished()).toString().substring(0, 16));
			 }

			 Xbuilder.setAttr(record, "name", m.getAuthorName());
			 Xbuilder.setAttr(record, "email", m.getAuthorEmail());
			 Xbuilder.setAttr(record, "message", m.getMessage());
		}
	
		PageNavigator navigator = new PageNavigator();
		navigator.setItemsPerPage(items_per_page);
		navigator.setPortionSize(portion_size);
		navigator.setResultSize(countOfRecords);
		navigator.setCurrentPageId(currentPageId);
		navigator.build(elt);
		return elt;
	}
}
