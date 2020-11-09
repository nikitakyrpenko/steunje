/**
 * @(#)$Id: PageStatisticsForm.java,v 1.0, 2006-01-25 15:33:58Z, Andrey Morskoy$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.statistics.forms;

import org.apache.log4j.Logger;

import com.negeso.framework.controller.RequestContext;
import com.negeso.module.statistics.Month;
import com.negeso.module.statistics.command.PageStatisticsCommand;

public class PageStatisticsForm {
    private static Logger logger = Logger.getLogger(PageStatisticsForm.class);
    private Integer month;
    private Integer year;
    private RequestContext request;
    private Boolean doRegen = false;
    
    public PageStatisticsForm(RequestContext request){
        if( request==null ){
           logger.error("Null request");
           return;
        }
        else{
            this.request = request;
            String par = request.getParameter("statusId");
            if( par!=null ){
                this.setYear(Integer.parseInt(par));
            }
            
            par = request.getParameter("regen");
            if( par!=null ){
                this.setDoRegen( Boolean.parseBoolean(par) );
            }
            
            par = request.getParameter("month");
            if( par!=null ){
               Month tmpM = Month.getMonthById(par);
               if( tmpM!=null ){
                   this.setMonth(tmpM.getMonth());
                   this.setYear(tmpM.getYear());
               }
            }
        }
    }
    
    public Integer getMonth() {
        return month;
    }
    public void setMonth(Integer month) {
        this.month = month;
    }


    public Integer getYear() {
        return year;
    }


    public void setYear(Integer year) {
        this.year = year;
    }






    public RequestContext getRequest() {
        return request;
    }






    public void setRequest(RequestContext request) {
        this.request = request;
    }






    public Boolean getDoRegen() {
        return doRegen;
    }






    public void setDoRegen(Boolean doRegen) {
        this.doRegen = doRegen;
    }

}
