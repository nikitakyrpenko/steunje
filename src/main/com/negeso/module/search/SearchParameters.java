/*
 * @(#)$Id: SearchParameters.java,v 1.16, 2007-01-09 18:59:08Z, Anatoliy Pererva$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.search;

import java.io.Serializable;
import java.util.List;


import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;


public class SearchParameters implements Serializable{
	
	private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(SearchParameters.class);
    
    private String allWords = "";
    private String exactPhrase = "";
    private String atLeastOne = "";
    private String without = "";
    private String languageCode = Env.getDefaultLanguageCode();
    private int paging = 10;
    private int lastMonths = 0;
    private int curPage = 1;
    private String sortOrder = "score";
    private Long uid = null;
    private long lowerTime = 0;
    private long upperTime = 0;
    private List<String> categoryProductExclude;
   

    public SearchParameters() {}

    
    public SearchParameters(RequestContext request) {

    	this.languageCode = null;
    			
		if ( request.getParameter("sortOrder") != null ) {			
			this.sortOrder = request.getParameter("sortOrder");
		
		}

		if ( request.getParameter("keyword") != null ) {			
			this.atLeastOne = request.getParameter("keyword");
		}			

	}


	/** Product category and its parent groups for exclude it from search */
    public List<String> getCategoryProductExclude() {
        return categoryProductExclude;
    }

    /** user who makes the search  */
    public Long getUid()
    {
        return uid;
    }
    
    
    /** user who makes the search  */
    public void setUid(Long uid)
    {
        this.uid = uid;
    }
    
    
    /** Currently displayed page of results (defaults to 1) */
    public int getCurPage()
    {
        return curPage;
    }
    
    
    /** Currently displayed page of results (defaults to 1) */
    public void setCurPage(int curPage)
    {
        this.curPage = curPage > 0 ? curPage : 1;
    }
    
    
    /** If true, results are sorted by relevance (default), otherwise by date */
    public String getSortOrder()
    {
        return sortOrder;
    }
    
    
    /** If true, results are sorted by relevance (default), otherwise by date */
    public void setSortOrder(String sortByScore)
    {
        this.sortOrder = sortByScore;
    }
    
    
    /** All of these words must be in search result */
    public String getAllWords()
    {
        return allWords;
    }
    
    
    /** All of these words must be in search result */
    public void setAllWords(String allWords)
    {
        this.allWords = allWords;
    }
    
    
    /** At least one of these words must be in search result */
    public String getAtLeastOne()
    {
        return atLeastOne;
    }
    
    
    /** At least one of these words must be in search result */
    public void setAtLeastOne(String atLeastOne)
    {
        this.atLeastOne = atLeastOne;
    }
    
    
    /** Exactly this phrase must be in search result */
    public String getExactPhrase()
    {
        return exactPhrase;
    }
    
    
    /** Exactly this phrase must be in search result */
    public void setExactPhrase(String exactPhrase)
    {
        this.exactPhrase = exactPhrase;
    }
    
    
    /** How old are can be entries to be listed in search result */
    public int getLastMonths()
    {
        return lastMonths;
    }
    
    
    /** How old are can be entries to be listed in search result */
    public void setLastMonths(int lastMonths)
    {
        this.lastMonths = (lastMonths < 0 || lastMonths > 12) ? 0 : lastMonths;
    }
    
    
    /** How many entries are to be displayed in search result */
    public int getPaging()
    {
        return paging;
    }
    
    
    /** How many entries are to be displayed in search result */
    public void setPaging(int paging)
    {
        this.paging = paging;
    }
    
    
    /** None of these words must be in search result */
    public String getWithout()
    {
        return without;
    }
    
    
    /** None of these words must be in search result */
    public void setWithout(String without)
    {
        this.without = without;
    }
    
    
    public String getLanguageCode() {
        return languageCode;
    }
    
    
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }


	public long getLowerTime() {
		return lowerTime;
	}


	public void setLowerTime(long lowerTime) {
		this.lowerTime = lowerTime;
	}


	public long getUpperTime() {
		return upperTime;
	}


	public void setUpperTime(long upperTime) {
		this.upperTime = upperTime;
	}


    public void setCategoryProductExclude(List<String> categoryProductExclude) {
        this.categoryProductExclude = categoryProductExclude;
    }

}

