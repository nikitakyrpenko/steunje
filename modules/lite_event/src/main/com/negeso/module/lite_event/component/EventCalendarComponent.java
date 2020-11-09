/*
 * @(#)$Id: EventCalendarComponent.java,v 1.1, 2007-05-16 17:35:37Z, Svetlana Bondar$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.lite_event.component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.PreparedPageComponent;
import com.negeso.module.lite_event.EventComponentService;
import com.negeso.module.lite_event.EventXmlBuilder;
import com.negeso.module.lite_event.domain.Event;

/**
 * 
 * @TODO
 * 
 * @author		Svetlana Bondar
 * @version		$Revision: 2$
 *
 */
public class EventCalendarComponent extends PreparedPageComponent {

	private static Logger logger = Logger.getLogger(EventCalendarComponent.class);
	
	@Override
	public void buildXml() throws Exception {
		logger.debug("+");
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime( Env.parseRoundDate( getRequestContext().getNonblankParameter("startDate") ) );
		
		Xbuilder.setAttr(this.getModel(), "startDate", getRequestContext().getNonblankParameter("startDate") );
		Xbuilder.setAttr(this.getModel(), "startDateYear", startDate.get(Calendar.YEAR) );		
		Xbuilder.setAttr(this.getModel(), "startDateMonth", startDate.get(Calendar.MONTH) + 1 );		
		Xbuilder.setAttr(this.getModel(), "startDateDay", startDate.get(Calendar.DAY_OF_MONTH) );		
		
		DateRange dateRange = this.getDateRange( 
					startDate, 
					getRequestContext().getNonblankParameter("period") 
		);		
		
		Calendar tmpDate = Calendar.getInstance();
		tmpDate.setTime(dateRange.getBeginDate());
		int month = tmpDate.get(Calendar.MONTH);		
		Element monthEl = this.buildMonthEl(startDate, month);
		
		int i = 0;
		List<Event> events = this.getEvents(dateRange.getBeginDate(), dateRange.getEndDate());
		
		while ( tmpDate.getTime().compareTo( dateRange.getEndDate() ) <= 0 ) {
			if (month != tmpDate.get(Calendar.MONTH))  {
				monthEl = this.buildMonthEl(startDate, ++month);
			}			
			Element dayEl = this.buildDayEl(tmpDate, monthEl);
			i = this.addEventEl(tmpDate, i, events, dayEl);
			tmpDate.set(Calendar.DAY_OF_MONTH, tmpDate.get(Calendar.DAY_OF_MONTH) + 1 );
		}
		
		logger.debug("-");
	}


	private int addEventEl(Calendar tmpDate, int i, List<Event> events, Element dayEl) {
		if ( i < events.size() ) {				
			Event event = events.get(i);
			if ( !event.getCategory().areParentCategoriesExpired() 
					&& tmpDate.getTime().compareTo( event.getDate() ) == 0 
			) {
				event.setLangId(getSession().getLanguage().getId());
				event.setFieldsFromEventDetails(
						EventComponentService.getEventDetailsMap(
								this.getHiberSession(), 
								getSession().getLanguage().getId(), 
								event.getId()), null
				);
				Element eventEl = EventXmlBuilder.buildEvent(
						dayEl, 
						event,
						getSession().getLanguageCode()
				);
				Xbuilder.setAttr(
						eventEl, 
						"categoryName", 
						event.getCategory().getTitles().get(
								getSession().getLanguage().getId()
						).toString()
				);
				
				i++;
			}
		}
		return i;
	}


	private Element buildDayEl(Calendar tmpDate, Element monthEl) throws ParseException {
		Element dayEl = Xbuilder.addEl(monthEl, "Day", null);
		Xbuilder.setAttr(dayEl, "number", tmpDate.get(Calendar.DAY_OF_MONTH));
		
		SimpleDateFormat dateFormater = new SimpleDateFormat(
				"EEEE", 
				new Locale(getSession().getLanguageCode() == null ? Env.getDefaultLanguageCode() : getSession().getLanguageCode()));		
		Xbuilder.setAttr(dayEl, "weekDay", dateFormater.format(tmpDate.getTime()));
		int  weekDayNumber = tmpDate.get(Calendar.DAY_OF_WEEK);
		weekDayNumber = weekDayNumber == 1 ? 7 : weekDayNumber - 1;
		Xbuilder.setAttr(dayEl, "weekDayNumber", weekDayNumber);
		
		Calendar now = Calendar.getInstance();			
		Date nowDate = Env.parseRoundDate( 
				now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH)+1) + "-" + now.get(Calendar.DAY_OF_MONTH)   
		);
		if ( tmpDate.getTime().compareTo( nowDate ) == 0 ) {
			Xbuilder.setAttr(dayEl, "current", "true");
		}
		return dayEl;
	}


	private Element buildMonthEl(Calendar startDate, int month) {
		Element monthEl;
		monthEl = Xbuilder.addEl(this.getModel(), "Month", null);
		Xbuilder.setAttr(monthEl, "number", month + 1);
		Xbuilder.setAttr(monthEl, "days", getDaysQuantity(month, startDate.get(Calendar.YEAR)));
		
		Calendar tmpDate = Calendar.getInstance();
		tmpDate.set(Calendar.MONTH, month);
		SimpleDateFormat dateFormater = new SimpleDateFormat(
				"MMMMM", 
				new Locale(getSession().getLanguageCode() == null ? Env.getDefaultLanguageCode() : getSession().getLanguageCode()));		
		Xbuilder.setAttr(monthEl, "name", dateFormater.format(tmpDate.getTime()));

		return monthEl;
	}


	private int getDaysQuantity(int month, int year) {
		Calendar tmpDate = Calendar.getInstance();
		tmpDate.set(Calendar.DAY_OF_MONTH, 1);
		tmpDate.set(Calendar.MONTH, month + 1);
		tmpDate.set(Calendar.YEAR, year);
		tmpDate.set(Calendar.DAY_OF_YEAR, tmpDate.get(Calendar.DAY_OF_YEAR) -  1);
		return tmpDate.get(Calendar.DAY_OF_MONTH);
	}


	private List<Event> getEvents( Date beginDate, Date endDate) {
		logger.debug("+");
		logger.debug("beginDate = " + beginDate);
		logger.debug("endDate = " + endDate);
		Query query =  this.getHiberSession().createQuery(
				" FROM Event e" +
				" where " +
				" e.siteId = " + Env.getSiteId() + 
				" and coalesce(e.category.publishDate,current_date) <= current_date " +
				" and coalesce(e.category.expiredDate,current_date+1) > current_date  " +  
				" and coalesce(e.publishDate,current_date) <= current_date " +
				" and coalesce(e.expiredDate,current_date+1) > current_date  " +
				" and coalesce(e.date,current_date) >= :beginDate  " +
				" and coalesce(e.date,current_date) <= :endDate  " +		
				" order by e.date, e.category.id")
//				);
		.setDate("beginDate", beginDate)
		.setDate("endDate", endDate);
				
		logger.debug("-");
		return (List<Event>) query.list();
	}

	private DateRange getDateRange( Calendar startDate, String period) throws ParseException {
		logger.debug("+");
		Date beginDate = null;
		Date endDate = null;		
		Calendar tmpDate = Calendar.getInstance();
		if ("day".equals(period)) {
			beginDate = startDate.getTime();
			endDate = startDate.getTime();
			
		} else if ("week".equals(period)) {
			tmpDate.setTime( startDate.getTime() );
			tmpDate.setFirstDayOfWeek(Calendar.MONDAY);
			tmpDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			beginDate = tmpDate.getTime();
			tmpDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			endDate = tmpDate.getTime();;
			
		} else if ("month".equals(period)) {
			tmpDate.setTime( startDate.getTime() );
			tmpDate.set( Calendar.DAY_OF_MONTH, 1 );
			beginDate = tmpDate.getTime();
			tmpDate.set( Calendar.MONTH, tmpDate.get(Calendar.MONTH)+1 );
			tmpDate.set( Calendar.DAY_OF_MONTH, tmpDate.get(Calendar.DAY_OF_MONTH)-1 );
			endDate = tmpDate.getTime();;

		} else if ("year".equals(period)) {
			tmpDate.setTime( Env.parseRoundDate( startDate.get(Calendar.YEAR ) + "-01-01" ) );
			beginDate = tmpDate.getTime();
			tmpDate.setTime( Env.parseRoundDate( startDate.get(Calendar.YEAR ) + "-12-31" ) );
			endDate = tmpDate.getTime();
		}		

		logger.debug("beginDate = " + beginDate);
		logger.debug("endDate = " + endDate);
		
		logger.debug("-");
		return new DateRange(beginDate, endDate);
	}
	

	@Override
	public String getName() {
		return "EventCalendarsComponent";
	}

}

class DateRange {
	
	Date beginDate;
	Date endDate;
	
	public DateRange(Date beginDate, Date endDate) {
		this.beginDate = beginDate;
		this.endDate = endDate;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}