/*
 * @(#)$NewsLineParameters.java$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.newsline.generators;

/**
 * @author Sergiy Oliynyk
 *
 */
public class NewsLineParameters {
    private String lists;
    private int itemsPerPage;
    private int pageNumber;
    private String parameters;
    private boolean includeNull;
    private boolean includeNotNull;

    public NewsLineParameters(String lists, int itemsPerPage, int pageNumber,
        String parameters, boolean includeNull, boolean includeNotNull)
    {
        this.lists = lists;
        this.itemsPerPage = itemsPerPage;
        this.pageNumber = pageNumber;
        this.parameters = parameters;
        this.includeNull = includeNull;
        this.includeNotNull = includeNotNull;
    }

    public String getLists() {
        return lists;
    }

    public void setLists(String lists) {
        this.lists = lists;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getPageNumber() {
        return pageNumber;
    }
    
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public String getParameters() {
        return parameters;
    }
    
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    public boolean isIncludeNull() {
        return includeNull;
    }
    
    public void setIncludeNull(boolean includeNull) {
        this.includeNull = includeNull;
    }
    
    public boolean isIncludeNotNull() {
        return includeNotNull;
    }
    
    public void setIncludeNotNull(boolean includeNotNull) {
        this.includeNotNull = includeNotNull;
    }
}
