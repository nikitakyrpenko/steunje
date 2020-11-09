/*
* @(#)$Id: EventController.java,v 1.10, 2007-02-07 11:25:31Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.Env;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.Language;
import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.module.lite_event.domain.Event;
import com.negeso.wcms.field_configuration.Field;
import com.negeso.wcms.field_configuration.FieldConfiguration;
import com.negeso.wcms.field_configuration.FieldConfigurationCache;

/**
 *
 * TODO
 * 
 * @version                $Revision: 11$
 * @author                 Svetlana Bondar
 * 
 */
public class EventController extends AbstractController {

	private static Logger logger = Logger.getLogger(EventController.class);
    
    private EventService eventService;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("+");   
		request.setAttribute("languageFilter", Language.getItems());     
		request.setAttribute("fieldConfigurations", 
				FieldConfigurationCache.getInstance().getFieldsConfiguration(
						Long.valueOf(Env.getProperty("event.defaultFieldSetId","1")), eventService.getHibernateTemplate()));   
		
		String action = request.getParameter("act");
		Long id = (request.getParameter("id") == null || "".equals(request.getParameter("id"))
				? null : Long.valueOf( request.getParameter("id") ));
		Long langId = request.getParameter("langId") == null || "".equals(request.getParameter("langId"))
				? Language.getDefaultLanguage().getId() : Long.valueOf( request.getParameter("langId") );
		
		Event event = null;		
		if (id != null) { 	
			event = updateEvent(request, action, id, langId);
		} else {	
			event = addEvent(request, action, langId);
		}
		
		if ( "saveAndClose".equals(action) ) { //save and close
			logger.debug("-");   
			request.setAttribute("categoryId", event.getCategoryId());
			return EventListController.eventsList(request, eventService);
		} 		
		
		RequestUtil.getHistoryStack(request).push( new Link("eventDetails.link", 
				"/admin/event_details.html?id=" + event.getId() + "&langId=" + langId) );
		logger.debug("-");   
		return new ModelAndView("em_event_details", "event", event);
	}

	private Event addEvent(HttpServletRequest request, String action, Long langId) throws ParseException {
		logger.debug("+");
		Event event = new Event();
		event.setLangId(langId);
		event.setFieldSetId( Long.valueOf(Env.getProperty("event.defaultFieldSetId","1")) );
		if ( "saveAndClose".equals(action) || "save".equals(action) ) {
			setDates(request, event);
			createFields(request, event);				
			eventService.addEvent(event);
			request.setAttribute("articles", getArticles(event));			
		} else {
			Map emptyMap = new HashMap();
			emptyMap.put("langId", langId);
			event.setFieldsFromEventDetails(emptyMap, eventService.getHibernateTemplate());
			addArticles(event);			
		}
		logger.debug("-");
		return event;
	}

	private void addArticles(Event event) {
		logger.debug("+");
		for (FieldConfiguration fieldConf: 
			FieldConfigurationCache.getInstance().getFieldsConfiguration(
					Long.valueOf(Env.getProperty("event.defaultFieldSetId","1")), eventService.getHibernateTemplate())) {				
			if (fieldConf.getType().equals(FieldConfiguration.RTE_ARTICLE_TYPE)) {
				Article article = new Article();
				eventService.addArticle(article);
				event.getFields().get(fieldConf.getId()).setValue(article.getId().toString());					
			}
		}
		logger.debug("-");		
	}

	private Event updateEvent(HttpServletRequest request, String action, Long id, Long langId) throws ParseException {
		logger.debug("+");
		Event event = null;
		if ( "saveAndClose".equals(action) || "save".equals(action) ) {				
			event = eventService.getEvent(id);
			setDates(request, event);
			event.setLangId(langId);
			createFields(request, event);				
			eventService.updateEvent(event);
		} else {			
			event = eventService.getEvent(id, langId);
		}
		request.setAttribute("articles", getArticles(event));
		logger.debug("-"); 
		return event;
	}

	private Map<String, String> getArticles(Event event) {
		logger.debug("+");
		Map<String, String> articles = new HashMap<String, String> ();
		for (FieldConfiguration fieldConf: 
			FieldConfigurationCache.getInstance().getFieldsConfiguration(event.getFieldSetId(), eventService.getHibernateTemplate())) {
			logger.debug("fieldConf.getType = " + fieldConf.getType());
			if (fieldConf.getType().equals(FieldConfiguration.RTE_ARTICLE_TYPE)) {
				Article article = eventService.getArticle(Long.valueOf(event.getFields().get(fieldConf.getId()).getValue()));
				articles.put(article.getId().toString(), article.getText());
				logger.debug("articles.put = " +  article.getId().toString() + " value = " + article.getText());		
			}
		}
		logger.debug("-");		
		return articles;
	}

	private void createFields(HttpServletRequest request, Event event) {
		logger.debug("+");
		for (FieldConfiguration fieldConf: 
			FieldConfigurationCache.getInstance().getFieldsConfiguration(event.getFieldSetId(), eventService.getHibernateTemplate())) {
			event.getFields().put( fieldConf.getId(), new Field(
					fieldConf.getId(), 
					request.getParameter(fieldConf.getName()).toString(),
					fieldConf.getTitles().get(event.getLangId()).toString(),
					fieldConf.getName()
			) );
		}
//		logger.debug("categoryId = " + request.getParameter("categoryId"));
		event.setCategoryId(Long.valueOf( request.getParameter("categoryId") ));
		logger.debug("-");
	}

	private void setDates(HttpServletRequest request, Event event) throws ParseException {
		logger.debug("+");
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		if (request.getParameter("publishDate") != null  && !request.getParameter("publishDate").equals("")) {			
			event.setPublishDate( df.parse(request.getParameter("publishDate")) );
		} else {
			event.setPublishDate(null);
		}
		
		if (request.getParameter("expiredDate") != null  && !request.getParameter("expiredDate").equals("")) {			
			event.setExpiredDate( df.parse(request.getParameter("expiredDate")) );
		} else {
			event.setExpiredDate(null);
		}
		if (request.getParameter("date") != null  && !request.getParameter("date").equals("")) {			
			event.setDate( df.parse(request.getParameter("date")) );
		} else {
			event.setDate(null);
		}
		logger.debug("-"); 
	}

	public EventService getEventService() {
		return eventService;
	}
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

}
