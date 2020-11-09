/**
 * @(#)$Id: UserPageStatisticsForm.java,v 1.0, 2006-01-25 15:34:00Z, Andrey Morskoy$
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

public class UserPageStatisticsForm {
    private static Logger logger = Logger.getLogger(UserPageStatisticsForm.class);
    private Integer month;
    private Integer year;
    private Long userId;
    private Boolean doRegen = false;    
    private RequestContext request;
    
    public UserPageStatisticsForm(RequestContext request){
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
            
            par = request.getParameter("month");
            if( par!=null ){
               Month tmpM = Month.getMonthById(par);
               if( tmpM!=null ){
                   this.setMonth(tmpM.getMonth());
                   this.setYear(tmpM.getYear());
               }
            }
            
            par = request.getParameter("userId");
            if( par!=null ){
                this.setUserId( Long.parseLong(par) );
            }
            
            par = request.getParameter("regen");
            if( par!=null ){
                this.setDoRegen( Boolean.parseBoolean(par) );
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






    public Long getUserId() {
        return userId;
    }






    public void setUserId(Long userId) {
        this.userId = userId;
    }






    public Boolean getDoRegen() {
        return doRegen;
    }






    public void setDoRegen(Boolean doRegen) {
        this.doRegen = doRegen;
    }






    






   






   
}
