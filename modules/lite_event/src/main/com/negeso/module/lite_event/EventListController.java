/*
* @(#)$Id: EventListController.java,v 1.3, 2007-02-06 11:02:20Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.i18n.DatabaseResourceBundle;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.lite_event.domain.Category;
import com.negeso.module.lite_event.domain.Event;
import com.negeso.module.statistics.CSVBuilder;

/**
 *
 * TODO
 * 
 * @version                $Revision: 4$
 * @author                 Svetlana Bondar
 * 
 */
public class EventListController extends AbstractController {

	private static Logger logger = Logger.getLogger(EventListController.class);
    
    private EventService eventService;
    
	private DatabaseResourceBundle messageSource;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("+");   
		
		//subscribers list
		if ( request.getParameter("id") != null ) {			
			RequestUtil.getHistoryStack(request).push( new Link("subscribers.link", "/admin/subscribers.html?categoryId=" + request.getParameter("categoryId")) );
			logger.debug("-");
			return new ModelAndView("em_subscribers", "subscribers", eventService.getSubscribers( request.getParameter("id") ));
		}
		
		//events list
		if ( request.getParameter("deleteId") != null ) {			
			eventService.deleteEvent( request.getParameter("deleteId") );
			
		} else if ( request.getParameter("downloadId") != null ) {			
			downloadEventSubscribers(response, request);
		}					
		logger.debug("-");
		return eventsList(request, eventService);			
			
	}

	public static ModelAndView eventsList(HttpServletRequest request, EventService eventService) {
		logger.debug("+-");       
		Category category = eventService.getCategory(request.getParameter("categoryId"));
		RequestUtil.getHistoryStack(request).push( 
				new Link( 
						category.getDefaultTitle(),
						"/admin/eventslist.html?categoryId=" + request.getParameter("categoryId"),
						false,
						category.getLevel() * 2 + 1));
		return new ModelAndView("em_events", "events", 
				eventService.getEvents( request.getParameter("categoryId") ));
	}


	public EventService getEventService() {
		return eventService;
	}

	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}
	
	private String getEventId(HttpServletRequest request) {
		return request.getParameter("downloadId");
	}
	
	private void downloadEventSubscribers(HttpServletResponse httpResponse, HttpServletRequest request) {
		logger.debug("+");		
		Event event = eventService.getEvent(new Long(getEventId(request)), Env.getSite().getLangId());
		String begin = event.getDefaultTitle() + ";" + event.getDate() + ";";
		StringBuffer buf = new StringBuffer();

		String head = getHead(request); 
        CSVBuilder.writeHead(buf, head);
        
        StringBuffer data = new StringBuffer();
        for (Object[] subscriber: eventService.getSubscribers(getEventId(request))) {        	
        	data.append(begin + subscriber[0] + ";" + subscriber[1] + ";" + subscriber[2] + "\n");
        }
        CSVBuilder.writeData(buf, data);        
        CSVBuilder.buildFile(httpResponse, buf);        
		logger.debug("-");
	}

	//TODO refactoring create AbstractController with i18n functionality als
	private DatabaseResourceBundle getMessageSource(HttpServletRequest request) {
		if (messageSource == null) 
				return DatabaseResourceBundle.getInstance("lite_event.jsp", SessionData.getLanguageCode(request));
		return messageSource;
	}
	
	private String getString(HttpServletRequest request, String key) {
		return getMessageSource(request).getString(key);
	}
	
	private String getHead(HttpServletRequest request) {
		return getString(request, "EVENTLISTCONTROLLER.EVENTTITLE") + ";" +
			   getString(request, "EVENTLIST.LISTDATE") + ";" +
			   getString(request, "EVENTLISTCONTROLLER.SUBSCRIBER_NAME") + ";" +
			   getString(request, "EVENTLISTCONTROLLER.SUBSCRIBER_LOGIN") + ";" +
			   getString(request, "EVENTLISTCONTROLLER.AMOUNT_OF_PEOPLE"); 
	}

	
}
