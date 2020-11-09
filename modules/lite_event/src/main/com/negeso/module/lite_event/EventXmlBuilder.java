/*
* @(#)$Id: EventXmlBuilder.java,v 1.6, 2007-02-05 18:37:42Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.w3c.dom.Element;

import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.lite_event.domain.Category;
import com.negeso.module.lite_event.domain.Event;
import com.negeso.wcms.field_configuration.Field;
import com.negeso.wcms.field_configuration.FieldConfiguration;
import com.negeso.wcms.field_configuration.FieldConfigurationCache;

/**
 *
 * TODO
 * 
 * @version                $Revision: 7$
 * @author                 Svetlana Bondar
 * 
 */
public class EventXmlBuilder {
	private static Logger logger = Logger.getLogger(EventXmlBuilder.class);
	
	private static Element buildEventBrief(Element parentEl, Event event, String langCode) {
		logger.debug("+");
		Element eventEl = Xbuilder.addEl(parentEl, "Event", null);
	    Xbuilder.setAttr(eventEl, "id", event.getId());
	    Xbuilder.addFormattedDateEl(eventEl, event.getDate(), "Date", langCode);
	    buildFieldSetEl(eventEl, event);
	    logger.debug("-");
		return eventEl;   
	}

	public static Element buildEvent(Element parentEl, Event event, String langCode) {
		logger.debug("+");
		Element eventEl = buildEventBrief(parentEl, event, langCode);
		Xbuilder.addFormattedDateEl(eventEl, event.getPublishDate(), "PublishDate", langCode);
		Xbuilder.addFormattedDateEl(eventEl, event.getExpiredDate(), "ExpiredDate", langCode);
	    logger.debug("-");
		return eventEl;   
	}
	
	private static Element buildFieldSetEl( Element parentEl, Event event ) {
		logger.debug("+");
		Element fieldListEl = Xbuilder.addEl(parentEl, "FieldList", null);

		for (FieldConfiguration fieldConf: FieldConfigurationCache.getInstance().getFieldsConfiguration(event.getFieldSetId(), null)) {
			Element fieldEl = Xbuilder.addEl(fieldListEl, "Field", null);
			Xbuilder.setAttr(fieldEl, "name", fieldConf.getName());
			Xbuilder.setAttr(fieldEl, "order", fieldConf.getOrderNumber());

			Field field = event.getFields().get(fieldConf.getId());
			Xbuilder.setAttr(fieldEl, "title", field.getTitle());
			
			if (fieldConf.getType().equals(FieldConfiguration.RTE_ARTICLE_TYPE)) {
				Article article = (Article) DBHelper.getHibernateTemplate().load(Article.class, Long.valueOf( field.getValue() ));
				fieldEl.appendChild( article.createDomElement(fieldEl.getOwnerDocument()) );
			} else {				
				Xbuilder.setAttr(fieldEl, "value", field.getValue());			
			}
		}
		logger.debug("-");
		return fieldListEl;
	}
	
	private static Element buildCategory(Category category, Element parent, Long langId) {
		logger.debug("+");
		Element categoryEl = Xbuilder.addEl(parent, "Category", null); 
		Xbuilder.setAttr(categoryEl, "id", category.getId()); 
		Xbuilder.setAttr(categoryEl, "title", category.getTitles().get(langId)); 
		Xbuilder.setAttr(categoryEl, "leaf", category.isLeaf());
		logger.debug("-");
	    return categoryEl;
	  }

	  private static Element buildEventList(Session hiberSession, Category category, Element parent, Language language) {
		  logger.debug("+");
		  Element eventListEl = Xbuilder.addEl(parent, "EventList", null);
		  for (Event event: category.getEvents()) {
			  Timestamp now = new Timestamp( System.currentTimeMillis() );                
			  if ( (event.getPublishDate() == null ||  event.getPublishDate().before(now) || event.getPublishDate().equals(now)) 
					  && (event.getExpiredDate() == null || event.getExpiredDate().after(now)) ) {
				  	event.setLangId(language.getId());
					event.setFieldsFromEventDetails(
							EventComponentService.getEventDetailsMap(
									hiberSession, 
									language.getId(), 
									event.getId()), null
					);
					EventXmlBuilder.buildEvent( eventListEl, event, language.getCode() );
			  }
		  }
		  logger.debug("-");
		  return eventListEl;
	  }
	  
	  public static Element buildCategoryTree(Session hiberSession, Category parentCategory, Element rootEl, Language language){
		  logger.debug("+");
		  LinkedHashMap<Long, Element> elements = new LinkedHashMap<Long, Element>();
		  List<List<Category>> subCatsList = new ArrayList<List<Category>>();

		  Element parentEl = processCategory(hiberSession, language, subCatsList, rootEl, parentCategory, elements); 			  
		  while( !subCatsList.isEmpty() ) {
			List<Category> subCategories = subCatsList.iterator().next();
			for (Category cat : subCategories) {
				parentEl = processCategory(hiberSession, language, subCatsList, parentEl, cat, elements);         
			
			    Long parentId = cat.getParentCategory().getId();
			    if (elements.containsKey( parentId )) {
			      elements.get( parentId ).appendChild(parentEl);  
			    }
			}       
			subCatsList.remove(subCategories);
		  }
		  logger.debug("-");
		  return rootEl;
	  }

	private static Element processCategory(Session hiberSession, Language language, 
			List<List<Category>> subCatsList, Element parentEl, Category cat, LinkedHashMap<Long, Element> elements) {
		
		logger.debug("+");
		Element categoryEl =  buildCategory(cat, parentEl, language.getId());
		if ( cat.isLeaf() ) {
			buildEventList(hiberSession, cat, categoryEl, language);
		} else if ( cat.getSubCategories() != null && !cat.getSubCategories().isEmpty() ) {
			subCatsList.add(cat.getSubCategories());
		}
		elements.put(cat.getId(), categoryEl);
		logger.debug("-");		 
		return categoryEl;
	}


}
