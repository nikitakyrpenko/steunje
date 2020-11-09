/*
* @(#)$Id: EventService.java,v 1.13, 2007-02-07 11:25:31Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.negeso.framework.Env;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.Language;
import com.negeso.module.lite_event.domain.Category;
import com.negeso.module.lite_event.domain.Event;
import com.negeso.wcms.field_configuration.Field;
import com.negeso.wcms.field_configuration.FieldConfiguration;
import com.negeso.wcms.field_configuration.FieldConfigurationCache;



/**
 *
 * TODO
 * 
 * @version                $Revision: 14$
 * @author                 Svetlana Bondar
 * 
 */
public class EventService extends HibernateDaoSupport {
	
    private static Logger logger = Logger.getLogger(EventService.class);

	public List<Event> getEvents(String categoryId) {
    	logger.debug("+");
    	List<Object[]> tmp =  (List<Object[]>) this.getHibernateTemplate().find( 
    		"select e, ed " +
    		" from Event e, EventDetails ed" +
    		" where ed.eventId = e.id " + 
    		" and ed.langId = " + Env.getSite().getLangId() +  
			" and e.siteId = " + Env.getSiteId() +
    		" and e.categoryId = " + categoryId +
    		" order by e.date" 
    	); 
    	List<Event> events = new ArrayList<Event>();
    	for (Object[] array: tmp) {
    		Event event = (Event)array[0];
    		event.setLangId( Env.getSite().getLangId());
    		event.setFieldsFromEventDetails((Map) array[1], this.getHibernateTemplate());
    		setDefaultTitle(event);
    		events.add(event);
    	}
    	logger.debug("tmp = " + tmp.size());
    	logger.debug("events = " + events.size());
    	logger.debug("-");
    	return events;
	}

	private void setDefaultTitle(Event event) {
		logger.debug("+");
		FieldConfiguration conf = FieldConfigurationCache.getInstance().getFieldsConfiguration(event.getFieldSetId(), this.getHibernateTemplate()).get(0);
		if (conf.getType().equals(FieldConfiguration.RTE_ARTICLE_TYPE)) {
			int defTitleLength = Integer.parseInt(Env.getProperty("event.defaultTitleLength", "50"));
			String title = this.getArticle(Long.valueOf(event.getFields().get(conf.getId()).getValue())).getText();
			event.setDefaultTitle(
					title.length() > defTitleLength ? title.substring(0, defTitleLength) : title
			);
			
		} else {    			
			event.setDefaultTitle(
					event.getFields().get(conf.getId()).getValue()
			);
		}
		logger.debug("-");
	}

	public List<Object[]> getSubscribers(String eventId) {
		logger.debug("+-");
		return this.getHibernateTemplate().find(           
        		"select us.name, us.login," +
        		" s.peopleAmount, s.date" +
        		" from Subscription as s, User as us " +
        		" where s.userId = us.id and us.siteId = " + Env.getSiteId() +
        		" and s.eventId = " + eventId
        	);
	}

	public void deleteEvent(String eventId) {
    	logger.debug("+-");
    	this.getHibernateTemplate().delete(
    			this.getHibernateTemplate().load(Event.class, new Long(eventId)));
    	this.getHibernateTemplate().flush();
	}

	public Event getEvent(Long eventId) {
		logger.debug("+-");
		return (Event) this.getHibernateTemplate().load(Event.class, eventId);
	}

	public Article getArticle(Long articleId) {
		logger.debug("+-");
		return (Article) this.getHibernateTemplate().load(Article.class, articleId);
	}
	
	public void addArticle(Article article) {
		logger.debug("+-");
		this.getHibernateTemplate().save(article);
		this.getHibernateTemplate().flush();
	}

	public void updateEvent(Event event) {
		logger.debug("+");
		event.setSiteId(Env.getSiteId());
		Map eventDetailsMap = getEventDetails(event.getId(), event.getLangId());
		for (Field field: event.getFields().values()) {
			eventDetailsMap.put(field.getName(), field.getValue());
		}		
		this.getHibernateTemplate().update("EventDetails", eventDetailsMap);
		this.getHibernateTemplate().update(event);
		this.getHibernateTemplate().flush();
		logger.debug("-");		
	}
	
	public void addEvent(Event event) {
		logger.debug("+");
		event.setSiteId(Env.getSiteId());
		this.getHibernateTemplate().save(event);
				
		Map eventDetailsMap = event.getEventDetails();		
		this.getHibernateTemplate().save("EventDetails", eventDetailsMap);		
		event.setEventDetailsId((Long)eventDetailsMap.get("id"));		
				
		long currentLangId = event.getLangId().longValue();
		for (Language language: Language.getItems()) {
			if (language.getId().longValue() != currentLangId) {	
				logger.debug("eventDetailsMap.get(langId) =  " + language.getId());
				Map langEventDetailsMap = new HashMap();
				langEventDetailsMap.putAll(eventDetailsMap);
				langEventDetailsMap.put("langId", language.getId());
				langEventDetailsMap.put("id", null);

				for (FieldConfiguration fieldConf: 
					FieldConfigurationCache.getInstance().getFieldsConfiguration(
							Long.valueOf(Env.getProperty("event.defaultFieldSetId", "1")), this.getHibernateTemplate())) {				
					if (fieldConf.getType().equals(FieldConfiguration.RTE_ARTICLE_TYPE)) {
						Article article = new Article();
						article.setText(
								getArticle( Long.valueOf( event.getFields().get( fieldConf.getId() ).getValue() ) ).getText()
						);						
						this.addArticle(article);						
						langEventDetailsMap.put(fieldConf.getName(), article.getId().toString());						
					}
				}

				this.getHibernateTemplate().save("EventDetails", langEventDetailsMap);
				
			}
		}		
		this.getHibernateTemplate().flush();
		logger.debug("-");		
	}
	
	public Event getEvent(Long eventId, Long langId) {
		logger.debug("+");
		Event event = (Event) this.getHibernateTemplate().load(Event.class, eventId);
		event.setLangId(langId);
		event.setFieldsFromEventDetails(getEventDetails(eventId, langId), this.getHibernateTemplate());
		setDefaultTitle(event);
		logger.debug("-");
		return event;
	}

	private HashMap getEventDetails(Long eventId, Long langId) {
		HashMap eventDetailsMap = (HashMap) this.getHibernateTemplate().find( 
				"from EventDetails ed where ed.langId = " + langId +  
				" and ed.eventId = " + eventId
		).get(0);
		return eventDetailsMap;
	}

	public Category getCategory(String categoryId) {
		logger.debug("+-");
		return (Category) this.getHibernateTemplate().load(Category.class, Long.valueOf(categoryId));
	}
	
}
