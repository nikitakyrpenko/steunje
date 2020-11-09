/*
 * @(#)$Id: PageNavigator.java,v 1.4, 2007-03-13 11:55:05Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.generators;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.CriticalException;


/**
 *
 * Page navigation builder. Generate XML for page navigation panel used for 
 * big data sets.  
 * 
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 5$
 */
public class PageNavigator {
	private static Logger logger = Logger.getLogger( PageNavigator.class );

	private long itemsPerPage;
	private long currentPageId;
	private long portionSize;
	private long resultSize;
	
	private String paramName;
	private String paramValue;

	/**
	 * Appends page navigation xml to parent Element. Supports:
	 * -	any size resultSizes
	 * -	next/prev page navigation
	 * - 	current page indicaion 
	 * 
	 * @param parent				The parent element
	 * @param resultSize			
	 * @param currentPageId
	 * @param portionSize
	 * @param itemsPerPage
	 * @throws CriticalException
	 */
	public void build(Element parent)
		throws CriticalException 
	{
		
		Element navigator = Xbuilder.addEl(parent, "PageNavigator", null);
		Xbuilder.setAttr(navigator, "resultSize", this.getResultSize());
		Xbuilder.setAttr(navigator, "itemFrom", this.getItemFrom());
		Xbuilder.setAttr(navigator, "itemTo", this.getItemTo());
//		Xbuilder.setAttr(navigator, this.getParamName(), this.getParamValue());

		long curPortion = currentPageId / (portionSize) ;
		if ( (currentPageId % portionSize) == 0 ){
			curPortion = curPortion - 1;
		}
		
		long firstPageId = (curPortion * portionSize) + 1;
		//long lastPageId = firstPageId + portionSize;
		long lastPageId = firstPageId + portionSize - 1;
		
		if ( logger.isInfoEnabled() ){
			//log
			logger.info("curPortion:" + curPortion);
			logger.info("curPageId:" + currentPageId);
			logger.info("fpage:" + firstPageId);
			logger.info("lpage:" + lastPageId);
		}
		
		if ( (lastPageId * itemsPerPage) > resultSize ) {
			lastPageId = ((int) (resultSize / itemsPerPage)) + 1;
			if ((resultSize % itemsPerPage) == 0)				//
				--lastPageId;									//

			if (logger.isInfoEnabled()){
				//log
				logger.info("lpage resized:" + currentPageId);
			}
		}
		else if ((lastPageId * itemsPerPage) != resultSize ) {
//		else{
			Element next = Xbuilder.addEl(navigator, "next", null);
			Xbuilder.setAttr(next, "pageId", "" + (lastPageId + 1) );
		}

		
		Element page = null;
		for (long i = firstPageId; i <= lastPageId; i++){
//		for (long i = firstPageId; i < lastPageId; i++){
			page = Xbuilder.addEl(navigator, "page", null);
			Xbuilder.setAttr(page, "pageId", "" + i);
			if ( i == currentPageId ){
				Xbuilder.setAttr(page, "current", "true");
			}
		}

		if ( curPortion != 0) {
			Element prev = Xbuilder.addEl(navigator, "previous", null);
			Xbuilder.setAttr(prev, "pageId", "" + (firstPageId - 1));
		}
	}
	
	
	public long getLimit() {
		return this.getItemsPerPage();
	}

	public long getOffcet() {
		return (this.getCurrentPageId() -1) * this.getItemsPerPage();
	}

	public long getItemFrom() {
		return ((this.getCurrentPageId() - 1)  * this.getItemsPerPage()) + 1;
	}
	
	public long getItemTo() {
		long retVal = this.getItemFrom() + this.getItemsPerPage() - 1;
		if (retVal > this.getResultSize()) {
			return this.getResultSize();
		}
		return retVal;
	}
	
	public long getItemsPerPage() {
		return itemsPerPage;
	}


	public void setItemsPerPage(long itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}


	public long getCurrentPageId() {
		return currentPageId;
	}


	public void setCurrentPageId(long currentPageId) {
		this.currentPageId = currentPageId;
	}


	public long getPortionSize() {
		return portionSize;
	}


	public void setPortionSize(long portionSize) {
		this.portionSize = portionSize;
	}


	public long getResultSize() {
		return resultSize;
	}


	public void setResultSize(long resultSize) {
		this.resultSize = resultSize;
	}


	public String getParamName() {
		return paramName;
	}


	public void setParamName(String paramName) {
		this.paramName = paramName;
	}


	public String getParamValue() {
		return paramValue;
	}


	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
}
