/*
* @(#)$Id: EventViewComponent.java,v 1.0, 2007-05-16 16:18:15Z, Svetlana Bondar$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.lite_event.component;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.condition.ConditionFailedException;
import com.negeso.framework.condition.components.ConditionalPreparedPageComponent;
import com.negeso.framework.condition.conditions.SecuredCondition;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.module.lite_event.EventService;
import com.negeso.module.lite_event.EventXmlBuilder;
import com.negeso.module.lite_event.SubscriptionHelper;
import com.negeso.module.lite_event.domain.Category;
import com.negeso.module.lite_event.domain.Event;



/**
 *
 * TODO
 * 
 * @version                $Revision: 1$
 * @author                 Svetlana Bondar
 * 
 */
public class EventViewComponent extends ConditionalPreparedPageComponent {

	private static Logger logger = Logger.getLogger(EventViewComponent.class);
	  
	private static final String VIEW_TYPE_EVENT_LIST = "eventList";
	private static final String VIEW_TYPE_EVENT_DETAILS = "eventDetails";

	private static final String ERROR_STATUS = "error";
	private static final String SUBSCRIBED_STATUS = "subscribed";
	
	private static final String INPUT_EVENT_ID = "id";
	private static final String INPUT_AMOUNT = "amount";
	
	@Override
	public String getName() {
		return "EventViewComponent";
	}
	
	@Override
	public void buildXml() {
		logger.debug("+");
		if (this.getView() == null || "".equals(this.getView())) {
			this.setView(VIEW_TYPE_EVENT_LIST);
		} 
		
		if (VIEW_TYPE_EVENT_LIST.equals( getView() )) {
			for (Category category: (List<Category>) this.getHiberSession().createQuery(
					" FROM Category " +
					" where " +
					" siteId = " + Env.getSiteId() + 
					" and parentCategory is null " +
					" and coalesce(publishDate,current_date) <= current_date " +
					" and coalesce(expiredDate,current_date+1) > current_date  " +
					" order by orderNumber").list() ) {
				EventXmlBuilder.buildCategoryTree(
						this.getHiberSession(), 
						category, 
						this.getModel(), 
						getSession().getLanguage()
				);
			}
		
		} else if (VIEW_TYPE_EVENT_DETAILS.equals( getView() )) {
			buildEventDetails();
		}
		logger.debug("-");
	}

	private Long getEventId() {
		logger.debug("event_id = " + getRequestContext().getLong(INPUT_EVENT_ID));
		return getRequestContext().getLong(INPUT_EVENT_ID);
	}
	
	private Long getAmount() {
		return getRequestContext().getLong(INPUT_AMOUNT);
	}
	
	private void buildEventDetails() {
		logger.debug("+");
		try {
			EventService service = new EventService();
			service.setHibernateTemplate(DBHelper.getHibernateTemplate());
			Event event = service.getEvent( getEventId(), getSession().getLanguage().getId());	
			
			Element eventElement = EventXmlBuilder.buildEvent(
					this.getModel(),
					event,
					getSession().getLanguageCode()
			);
			if (getAmount() != null) {
					doSubscribe(eventElement, event);
			}					
		 
		} catch (AccessDeniedException e) {
			setLoginForPageClass();
		} 
		logger.debug("-");		
	}

	private boolean userInfoIsAbsent() {
		Validate.notNull(getSession().getUser());
		return (User.getUserStatus(getSession().getUser().getLogin())== User.SYSTEM_USER);
	}
	
	private void doSubscribe(Element eventElement, Event event) throws AccessDeniedException {
		logger.debug("+-");
		try {
			getCondition().checkCondition();
			
			if (!userInfoIsAbsent()) {
				SubscriptionHelper.getInstance().doSubscribe(this.getHiberSession(), getSession(), getAmount(), event );
				setStatus(SUBSCRIBED_STATUS);
				eventElement.setAttribute("amount", getAmount().toString());
			} else setStatus("systemUser");
			
		} catch (ConditionFailedException e) {
			throw new AccessDeniedException();
		} 
		catch (Exception e) {
			logger.error("error while subscribing");
			eventElement.setAttribute("errorDetails", e.getMessage());
			setStatus(ERROR_STATUS);
		}
	}
	
	//--------------------------- implements Conditionable -------------------------------------------------
	
	private void setLoginForPageClass() {
		Xbuilder.setAttr((Element) getModel().getOwnerDocument().getChildNodes().item(0), "class", "login");
	}

	@Override
	protected void setCondition() {
		setCondition(new SecuredCondition(this));
	}
	
	@Override
	public boolean whenToSave(RequestContext request) {
		if (getAmount() != null &&
		    getEventId() != null) {
			return SAVE_IN_THIS_CASE;
		}
		return  DONT_SAVE;
	}

	
	
}
