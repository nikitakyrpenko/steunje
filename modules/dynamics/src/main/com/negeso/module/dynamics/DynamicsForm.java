/*
 * @(#)$Id: DynamicsForm.java,v 1.14, 2005-06-06 13:04:25Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.dynamics;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;


import com.negeso.framework.Env;


public class DynamicsForm implements Serializable {
    
    
    String startDate;
    
    
    String endDate;
    
    
    /** String constants "pages" | "modules" | "news" */
    String type;
    
    
    /** String constants "chart" | "table" */
    String style;
    
    
    /** How many results per page are displayed */
    int paging = 10;
    
    
    int pageNumber = 1;
    
    
    /** Show only items whose status changes in the next <b>filter</b> days */
    int filter = 14;
    
    
    String pagesOrder;
    
    
    String newsOrder;
    
    
    String modulesOrder;
    
    
    String productsOrder;
    
    
    private static Logger logger = Logger.getLogger(DynamicsForm.class);
    
    
    public String getOrder()
    {
        logger.debug("+ -");
        if("modules".equals(getType())) {
            return modulesOrder;
        } else if ("pages".equals(getType())) {
            return pagesOrder;
        } else if ("news".equals(getType())) {
            return newsOrder;
        } else if ("products".equals(getType())) {
            return productsOrder;
        } else if ("productCategories".equals(getType())) {
//            return productCategoriesOrder;
        }
        throw new RuntimeException("Wrong data type, cannot set sorting order");
    }

    
    public String getEndDate()
    {
        logger.debug("+");
        try {
            Date end = Env.parseDate(endDate == null ? "" : endDate);
            Date start = Env.parseDate(getStartDate());
            if(!end.after(start)) {
                throw new Exception("start date should preceed end date");
            }
        } catch (Exception e) {
            logger.info("wrong value: " + endDate);
            try {
                final long year = 1000 * 60 * 60 * 24 * 365L;
                Date date = Env.parseDate(getStartDate());
                date.setTime(date.getTime() + year);
                endDate = Env.formatDate(date);
            } catch (ParseException e1) {
                logger.error("cannot fix end date", e1);
            }
            logger.info("fixed to default: " + endDate);
        }
        logger.debug("-");
        return endDate;
    }
    
    
    public int getPaging()
    {
        logger.debug("+");
        if(paging < 10 || paging > 100) {
            logger.info("wrong value: " + paging);
            paging = 10;
            logger.info("fixed to default: " + paging);
        }
        logger.debug("-");
        return paging;
    }
    
    
    public int getPageNumber()
    {
        logger.debug("+");
        if(pageNumber < 1) {
            logger.info("wrong value: " + pageNumber);
            pageNumber = 1;
            logger.info("fixed to default: " + pageNumber);
        }
        logger.debug("-");
        return pageNumber;
    }
    
    
    public String getStartDate()
    {
        logger.debug("+");
        try {
            Env.parseDate(startDate == null ? "" : startDate);
        } catch (ParseException e) {
            logger.info("wrong value: " + startDate);
            startDate = Env.formatDate(new Date());
            logger.info("fixed to default: " + startDate);
        }
        logger.debug("-");
        return startDate;
    }
    
    
    public String getStyle()
    {
        logger.debug("+");
        if(!"chart".equals(style) && !"table".equals(style))
        {
            logger.info("wrong value: " + style);
            style = "table";
            logger.info("fixed to default: " + style);
        }
        logger.debug("-");
        return style;
    }
    
    
    public String getType()
    {
        logger.debug("+");
        if( !"pages".equals(type) &&
            !"modules".equals(type) &&
            !"news".equals(type) &&
            !"products".equals(type) &&
            !"productCategories".equals(type) )
        {
            logger.info("wrong value: " + type);
            type = "pages";
            logger.info("fixed to default: " + type);
        }
        logger.debug("-");
        return type;
    }
    
    
    public int getFilter()
    {
        logger.debug("+ -");
        if(filter < 0) {
            logger.info("wrong value: " + filter);
            filter = 14;
            logger.info("fixed to default: " + filter);
        }
        return filter;
    }
    
    
    public void setOrder(String order)
    {
        logger.debug("+ -");
        
        
        logger.debug("+ -");
        if("modules".equals(getType())) {
            modulesOrder = order;
        } else if ("pages".equals(getType())) {
            pagesOrder = order;
        } else if ("news".equals(getType())) {
            newsOrder = order;
        } else if ("products".equals(getType())) {
            productsOrder = order;
        } else if ("productCategories".equals(getType())) {
//            productCategoriesOrder = order;
        }
    }
    
    
    public void setEndDate(String endDate)
    {
        logger.debug("+ -");
        this.endDate = endDate;
    }
    
    
    public void setPaging(int itemsPerPage)
    {
        logger.debug("+ -");
        this.paging = itemsPerPage;
    }
    
    
    public void setPageNumber(int pageNumber)
    {
        logger.debug("+ -");
        this.pageNumber = pageNumber;
    }
    
    
    public void setStartDate(String startDate)
    {
        logger.debug("+ -");
        this.startDate = startDate;
    }
    
    
    public void setStyle(String style)
    {
        logger.debug("+ -");
        this.style = style;
    }
    
    
    public void setType(String type)
    {
        logger.debug("+ -");
        this.type = type;
    }
    
    
    public void setFilter(int filter)
    {
        logger.debug("+ -");
        this.filter = filter;
    }
    
    
}