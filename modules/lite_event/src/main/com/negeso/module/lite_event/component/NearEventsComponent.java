/*
* @(#)$Id: NearEventsComponent.java,v 1.0, 2007-05-16 16:18:15Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event.component;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.PreparedPageComponent;
import com.negeso.module.lite_event.EventComponentService;
import com.negeso.module.lite_event.EventXmlBuilder;
import com.negeso.module.lite_event.domain.Event;

/**
 *
 * TODO
 * 
 * @version                $Revision: 1$
 * @author                 sbondar
 * 
 */
public class NearEventsComponent extends PreparedPageComponent {
	
	private static Logger logger = Logger.getLogger(NearEventsComponent.class);
	
	@Override
	public void buildXml() throws Exception {
		logger.debug("+");
		int eventsCount = getIntParameter(getParameters(),"eventsCount", Integer.MAX_VALUE);

		List<Event> events = (List<Event>) this.getHiberSession().createQuery(
				" FROM Event e" +
				" where " +
				" e.siteId = " + Env.getSiteId() + 
				" and coalesce(e.category.publishDate,current_date) <= current_date " +
				" and coalesce(e.category.expiredDate,current_date+1) > current_date  " +  
				" and coalesce(e.publishDate,current_date) <= current_date " +
				" and coalesce(e.expiredDate,current_date+1) > current_date  " +
				" and coalesce(e.date,current_date) >= current_date  " +
				" order by e.date, e.id"
				).list();
			
		Calendar now = Calendar.getInstance();
		boolean first = true;
		int i = 0;
		int counter = 0;
		while (i < events.size() && counter < eventsCount ) { 
			Event event = events.get(i); 
			
			if ( !event.getCategory().areParentCategoriesExpired() ) {
				event.setLangId(getSession().getLanguage().getId());
				event.setFieldsFromEventDetails(
						EventComponentService.getEventDetailsMap(
								this.getHiberSession(), 
								getSession().getLanguage().getId(), 
								event.getId()), null
				);
				Element eventEl = EventXmlBuilder.buildEvent(
						this.getModel(), 
						event,
						getSession().getLanguageCode()
				);
				
				//set current event			 			
				Calendar eventDate = Calendar.getInstance();
				eventDate.setTime(event.getDate());
				if (first && now.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR) 
						&& now.get(Calendar.DAY_OF_YEAR ) == eventDate.get(Calendar.DAY_OF_YEAR )) {
					Xbuilder.setAttr(eventEl, "current", "true");
				}
				first = false;
				counter++;
			}
			logger.debug("current event = " + event);
			i++;
		}
		logger.debug("-");
	}

	@Override
	public String getName() {
		return "NearEventsComponent";
	}

}
