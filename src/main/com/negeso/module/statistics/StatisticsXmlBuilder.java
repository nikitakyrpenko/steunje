/**
 * @(#)$Id: StatisticsXmlBuilder.java,v 1.2, 2006-09-13 14:08:13Z, Svetlana Bondar$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.statistics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;

import com.negeso.module.statistics.forms.UserPageStatisticsForm;

public class StatisticsXmlBuilder{

	private static Logger logger = Logger.getLogger(StatisticsXmlBuilder.class);
	
	private static final String tableId = "stat_statistics";
    
	private final static String pagesSumArchiveAllWhere = 
		"where stat_archive.month = ? AND stat_archive.year = ? AND stat_archive.site_id = ? " +
		"GROUP BY stat_archive.page,page.title ORDER BY counter desc";
	
    private final static String pageSumArchiveAll = 
    	"select sum(stat_archive.hits) AS counter, stat_archive.page AS filename, page.title " +
    	"from stat_archive left join page on page.filename like stat_archive.page ";
    
    private final static String pageSumArchiveAllOnlyWithCounter = 
    	"select sum(stat_archive.hits) AS counter, stat_archive.page AS filename, " +
    	"page.title from stat_archive left join page on page.filename like stat_archive.page " +
    	"JOIN stat_counter ON stat_counter.page_id = page.id "; 
    
    private final static String usersPageSumArchiveAllWhere = 
    	"where stat_archive.month = ? AND stat_archive.year = ? AND stat_archive.user_id = ? " +
    	"AND stat_archive.site_id = ? GROUP BY stat_archive.page,page.title ORDER BY counter desc";
    
    private final static String usersPageSumArchiveAll = 
    	"select sum(stat_archive.hits) AS counter, stat_archive.page AS filename, " +
    	"page.title from stat_archive left join page on page.filename like stat_archive.page ";
    
    private final static String usersPageSumArchiveAllOnlyWithCounter = 
    	"select sum(stat_archive.hits) AS counter, stat_archive.page AS filename, " +
    	"page.title from stat_archive left join page on page.filename like stat_archive.page " +
    	"JOIN stat_counter ON stat_counter.page_id = page.id ";
    
    private final static String pagesSumOfHitsSql =
    	"SELECT COUNT(" + tableId + ".id) AS counter, page.title, " + tableId + ".page_name as filename " +
    	"FROM " + tableId + 
    	" LEFT JOIN page ON page.filename = " + tableId + ".page_name AND page.site_id = ? " +
    	" WHERE ((hit_date >= ?) AND (hit_date <= ?) AND (stat_statistics.site_id=?) )" +
    	"GROUP BY " + tableId + ".page_name, page.title, filename " +
    	"ORDER BY counter desc ";

    private final static String pagesSumOfHitsOnlyWithCounterSql =
    	"SELECT COUNT(" + tableId + ".id) AS counter, page.title, " + tableId + ".page_name as filename " +
    	"FROM " + tableId + 
    	" LEFT JOIN page ON page.filename = " + tableId + ".page_name AND page.site_id = ? " + 
    	" JOIN stat_counter ON stat_counter.page_id = page.id " +
    	" WHERE ((hit_date >= ?) AND (hit_date <= ?) AND stat_statistics.site_id=? AND stat_counter.is_counter=true) " +
    	"GROUP BY " + tableId + ".page_name, page.title, filename " +
    	"ORDER BY counter desc ";
    
    private final static String userSumOfHitsSql =
    	"SELECT COUNT(" + tableId + ".id) AS counter, page.title, " + tableId + ".page_name AS filename " +
    	"FROM " + tableId + 
    	" LEFT JOIN page ON filename = " + tableId + ".page_name " +
    	" LEFT JOIN user_list ON user_list.id = " + tableId + ".user_id " +
    	" WHERE ((hit_date >= ?) AND (hit_date <= ?) AND " +
    	" (" + tableId + ".user_id = ?) AND stat_statistics.site_id=?) " +
    	"GROUP BY " + tableId + ".page_name, page.title, filename " +
    	"ORDER BY counter desc ";

    private final static String userSumOfHitsOnlyWithCounterSql =
    	"SELECT COUNT(" + tableId + ".id) AS counter, page.title, " + tableId + ".page_name as filename " +
    	"FROM " + tableId + 
    	" LEFT JOIN page ON page.filename = " + tableId + ".page_name " +
    	" LEFT JOIN user_list ON user_list.id = " + tableId + ".user_id " +
    	" LEFT JOIN stat_counter ON stat_counter.page_id = page.id" +
    	" WHERE ((hit_date >= ?) AND (hit_date <= ?) AND " +
    	" (" + tableId + ".user_id = ?) AND stat_statistics.site_id=? AND (stat_counter.is_counter = true) AND stat_counter.is_counter=true) " +
    	"GROUP BY " + tableId + ".page_name, page.title, filename " +
    	"ORDER BY counter desc ";

    private final static String usersListSql =
    	"SELECT user_list.id, user_list.login, user_list.username " +
    	"FROM user_list " +
    	"LEFT JOIN stat_statistics ON stat_statistics.user_id = user_list.id " +
        "where user_list.site_id=? " +
    	"GROUP BY user_list.username, user_list.login, user_list.id " +
    	"ORDER BY user_list.username ASC;";
    

    
	private static final int INTERFACE_WIDTH = 757;
    private static final int GRAPHICAL_WIDTH = 400;
    private static final String [] colors = new String [] 
                           {"Green", "Blue", "Cyan", "Yellow", "Orange", "Red"};
    
    
    public static void buildStatisticsList(
    Element parentElement, 
    Statement statement, 
    RequestContext request,
    HashMap<String,String> attributes)
    throws SQLException{}
    public static void buildUserPageStatistics(
            Element parentElement, 
            Statement statement, 
            RequestContext request,
            StatisticsParameters params
            )
    throws SQLException, CriticalException
    {
        String formLanguage = parentElement.getAttribute("lang");
        HashMap<String,String> attributes = new HashMap<String,String>();
        ArrayList<Month> months = new ArrayList<Month>();
        ArrayList<Integer> years = new ArrayList<Integer>();
        HashMap<Long,String> users = new HashMap<Long,String>();
        
        if( params!=null && params.getMap()!=null ){
            attributes = params.getMap();
        }
        if( params!=null && params.getMonths()!=null ){
            months = params.getMonths();
        }
        
        if( params!=null && params.getYears()!=null ){
            years = params.getYears();
        }
        if( params!=null && params.getUsers()!=null ){
            users = params.getUsers();
        }
        
        int curColor = 0;
        StatisticsItem item;
        StatisticsList statL = new StatisticsList();
        
        Element listElement = null;
        Element bufferElement = null;
        Element tmpElement = null;
        Element tempEl = null;
        Element usrEl = null;
        listElement = Xbuilder.createEl(
                parentElement, 
                "StatisticsList", 
                null);
        Set<String> keys1 = attributes.keySet();
        String month = "";
        for (Iterator it = keys1.iterator(); it.hasNext();){
        	String currentKey = (String)it.next();
        	if( currentKey.equals("CurrentMonth") ){
        		month = attributes.get(currentKey);
        	}
        }
        
        Set<String> keys2 = attributes.keySet();
        String year = "";
        for (Iterator it = keys2.iterator(); it.hasNext();){
        	String currentKey =  (String)it.next();
        	if( currentKey.equals("CurrentYear") ){
        		year = attributes.get(currentKey);
        	}
        }
        
        String currentYear = new SimpleDateFormat("yyyy").format(new java.util.Date());
        String currentMonth = new SimpleDateFormat("MM").format(new java.util.Date());
        
        if (currentMonth.length() == 2 && currentMonth.charAt(0) == '0'){
        	currentMonth = currentMonth.substring(1, 2);
        }
        
        Date startDate = null;
        Date finishDate = null;
        Long userId = null;
        UserPageStatisticsForm form = new UserPageStatisticsForm(request);

        if(form != null) {
        	userId = form.getUserId();
        }
        
        if ( form.getUserId() == null ) {
        	SessionData session = request.getSessionData();      	
        	userId = (long)session.getUserId();
        }        
        
        logger.info("------- Current: year="+year+"; month="+month+"; userId = "+ userId);
		
        try {
			startDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+month+"-01").getTime());
			finishDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+month+"-31").getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
        logger.debug("+");
	    PreparedStatement stmt = null;
	    Connection conn = null;
        try {    	
        	conn = DBHelper.getConnection();
	    	
        	if (currentMonth.equals(month) && currentYear.equals(year)){
	    		if ("true".equals(Env.getProperty("statistics.logOnlyCounterPages", "true")) ) {
	    			stmt = conn.prepareStatement(userSumOfHitsOnlyWithCounterSql);
	    		}
	    		else {
	            	stmt = conn.prepareStatement(userSumOfHitsSql);
	    		}
	    		stmt.setDate(1, startDate);
		    	stmt.setDate(2, finishDate);
		    	stmt.setLong(3, userId);   	
		    	stmt.setLong(4, Env.getSiteId());
        	}else{
        		if ("true".equals(Env.getProperty("statistics.logOnlyCounterPages", "true")) ) {
	    			stmt = conn.prepareStatement(usersPageSumArchiveAllOnlyWithCounter + usersPageSumArchiveAllWhere);
	    		}
	    		else {
	            	stmt = conn.prepareStatement(usersPageSumArchiveAll + usersPageSumArchiveAllWhere);
	    		}
        		stmt.setLong(1, Long.parseLong(month));
    	    	stmt.setLong(2, Long.parseLong(year));
    	    	stmt.setLong(3, userId);   	
    	    	stmt.setLong(4, Env.getSiteId());
        	}
	        ResultSet rs = stmt.executeQuery();
	        
	        while (rs.next()) {
	    		item = new StatisticsItem();
	    		String itemName = "";
	    		String title = "";
	    		if (rs.getString("title") == null){
	    			itemName = rs.getString("filename")+ " (removed)";
	    			title = rs.getString("filename");
	    		}else{
	    			itemName = rs.getString("title") + " ("+rs.getString("filename")+")";
	    			title = rs.getString("title");
	    		}
	    		item.setName(itemName);
	            item.setValue(rs.getLong("counter"));
	            item.setPageName(rs.getString("filename"));
	            item.setTitle(title);
	            statL.add(item);   
	        }
	        rs.close();
	    }
	    catch (SQLException ex) {
	        logger.error("- Throwing new CriticalException");
	        throw new CriticalException(ex);
	    }
	    finally {
	    	DBHelper.close(stmt);
	    	DBHelper.close(conn);
	    }
	    logger.debug("-");
	    
        long max = 1;
        try{
           max = statL.getMaxValue();        
        }
        catch(Exception maxException){
            max = 1;
        }
        if(max==0){
            max=1;
        }
        for( Iterator it= statL.getList().iterator(); it.hasNext(); ){
               
            item = (StatisticsItem)it.next();  
            
            bufferElement = Xbuilder.createEl(
                    listElement, 
                    "StatisticsItem", 
                    null);
            tmpElement = Xbuilder.createEl(
                    bufferElement, 
                    "StatisticsItemName", 
                    item.getName());
            bufferElement.appendChild(tmpElement);
            tmpElement = Xbuilder.createEl(
                    bufferElement, 
                    "StatisticsItemNameQty", 
                    Long.toString(item.getValue()));
            tmpElement.setAttribute("style","td"+colors[curColor]);
            Long curLength = 
                              StatisticsXmlBuilder.GRAPHICAL_WIDTH 
                           *  item.getValue() 
                           /  max;
            tmpElement.setAttribute("length",curLength.toString());  
            Xbuilder.addEl(bufferElement, "PageName", item.getPageName());
            Xbuilder.addEl(bufferElement, "PageTitle", item.getTitle());
           
            bufferElement.appendChild(tmpElement);
            listElement.appendChild(bufferElement);
            
            if (curColor==(colors.length-1)){
                curColor=0;
            }
            else{
                curColor++;
            }
        } 
        bufferElement = Xbuilder.createEl(
                parentElement, 
                "NavigationAttributes", 
                null);
        
       
        tmpElement = Xbuilder.createEl(
                bufferElement, 
                "Users", 
                null 
                );
        Set usersK = users.keySet();
        for( Iterator it=usersK.iterator(); it.hasNext(); ){
                Long curId = (Long)it.next();
                tempEl = Xbuilder.createEl(
                        tmpElement, 
                        "User", 
                        null 
                );
                usrEl = Xbuilder.createEl(
                        tempEl, 
                        "UserId",
                        curId.toString() 
                );
                tempEl.appendChild(usrEl);
                
                usrEl = Xbuilder.createEl(
                        tempEl, 
                        "UserLogin", 
                        users.get(curId) 
                );
                tempEl.appendChild(usrEl);
        }
        
        SessionData session = request.getSessionData();      	
        Long sessionUserId = (long)session.getUserId();
        
        Long selectedUserId = new Long(userId);
         
        logger.debug("+");
        try {    	
        	conn = DBHelper.getConnection();
	    	stmt = conn.prepareStatement(usersListSql);
            stmt.setLong(1,Env.getSiteId());
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	        	tempEl = Xbuilder.createEl(
                        tmpElement, 
                        "User", 
                        null 
                );
	        	tempEl.setAttribute("UserLogin",rs.getString("username")+" / "+rs.getString("login"));
                tmpElement.appendChild(tempEl);
                
                tempEl.setAttribute("UserId",rs.getString("id"));
                tmpElement.appendChild(tempEl);
                
                if(sessionUserId.equals(rs.getLong("id")) ) {
                	tempEl.setAttribute("sessionUser","true");
                	tmpElement.appendChild(tempEl);
                };
                if(selectedUserId.equals(rs.getLong("id")) ) {
                	tempEl.setAttribute("selectedUser","true");
                	tmpElement.appendChild(tempEl);
                };
	        }
	        rs.close();
	    }
	    catch (SQLException ex) {
	        logger.error("- Throwing new CriticalException");
	        throw new CriticalException(ex);
	    }
	    finally {
	    	DBHelper.close(stmt);
	    	DBHelper.close(conn);
	    }
	    logger.debug("-");

        bufferElement.appendChild(tmpElement);
        tmpElement = Xbuilder.createEl(
                bufferElement, 
                "Months", 
                null 
                );
        int counter = 1;
        for( Iterator it = months.iterator(); it.hasNext() ; counter++){
            Month curMonth = (Month)it.next();
            
            tempEl = Xbuilder.createEl(
                    tmpElement, 
                    "Month", 
                    Integer.toString(curMonth.getMonth()) 
            );
            
            tempEl.setAttribute("MonthName",curMonth.getName() );
            tempEl.setAttribute("Year",Integer.toString( curMonth.getYear() ));
            
            tempEl.setAttribute("counter",Integer.toString(counter));
            tmpElement.appendChild(tempEl);
            
        }
        bufferElement.appendChild(tmpElement);
        tmpElement = Xbuilder.createEl(
                bufferElement, 
                "Years", 
                null 
        );   
        for( Iterator it = years.iterator(); it.hasNext() ; ){
            Integer curYear = (Integer)it.next();
            tempEl = Xbuilder.createEl(
                    tmpElement, 
                    "YearItem", 
                    curYear.toString()
            );
            tmpElement.appendChild(tempEl);
            
        }

        bufferElement.appendChild(tmpElement);

       if( attributes!=null && !attributes.isEmpty() ){ 
           Set<String> keys = attributes.keySet(); 
           
           for( Iterator<String> it = (Iterator<String>)keys.iterator();
                it.hasNext(); 
                ){
               String cur = it.next();
                tmpElement = Xbuilder.createEl(
                        bufferElement, 
                        cur, 
                        attributes.get(cur)
                        );
                bufferElement.appendChild(tmpElement);  
               
           }
       }
       parentElement.appendChild(listElement);
       parentElement.appendChild(bufferElement);
  }
    public static void buildPageStatistics(
            Element parentElement, 
            Statement statement, 
            RequestContext request,
            StatisticsParameters params
            )
    throws SQLException, CriticalException
    {
        HashMap<String,String> attributes = new HashMap<String,String>();
        ArrayList<Month> months = new ArrayList<Month>();
        ArrayList<Integer> years = new ArrayList<Integer>();
        
        if( params!=null && params.getMap()!=null ){
            attributes = params.getMap();
        }
        if( params!=null && params.getMonths()!=null ){
            months = params.getMonths();
        }
        
        if( params!=null && params.getYears()!=null ){
            years = params.getYears();
        }
        
        int curColor = 0;
        StatisticsItem item;
        StatisticsList statL = new StatisticsList();
        
        Element listElement = null;
        Element bufferElement = null;
        Element tmpElement = null;
        Element tempEl = null;
        listElement = Xbuilder.createEl(
                parentElement, 
                "StatisticsList", 
                null);

        
        Set<String> keys1 = attributes.keySet();
        String month = "";
        for (Iterator it = keys1.iterator(); it.hasNext();){
        	String currentKey = (String)it.next();
        	if( currentKey.equals("CurrentMonth") ){
        		month = attributes.get(currentKey);
        	}
        }
        
        Set<String> keys2 = attributes.keySet();
        String year = "";
        for (Iterator it = keys2.iterator(); it.hasNext();){
        	String currentKey =  (String)it.next();
        	if( currentKey.equals("CurrentYear") ){
        		year = attributes.get(currentKey);
        	}
        }
        
        logger.info("---------------- Year="+year+"; month="+month);

        String currentYear = new SimpleDateFormat("yyyy").format(new java.util.Date());
        String currentMonth = new SimpleDateFormat("MM").format(new java.util.Date());
    	
        if (currentMonth.length() == 2 && currentMonth.charAt(0) == '0'){
        	currentMonth = currentMonth.substring(1, 2);
        }
        
        Date startDate = null;
        Date finishDate = null;
		try {
			startDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+month+"-01").getTime());
			finishDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(year+"-"+month+"-31").getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
        logger.debug("+");
	    PreparedStatement stmt = null;
	    Connection conn = null;
        try {
        	conn = DBHelper.getConnection();
        	
        	if (currentMonth.equals(month) && currentYear.equals(year)){
        		//statistics by current month
        		if ("true".equals(Env.getProperty("statistics.logOnlyCounterPages", "true")) ) {
        			stmt = conn.prepareStatement(pagesSumOfHitsOnlyWithCounterSql);
        			logger.info("=================== Stat. for pages only with active counter");
        		}
        		else {
        			stmt = conn.prepareStatement(pagesSumOfHitsSql);
        			logger.info("===================  Stat. for all pages");
        		}

        		stmt.setLong(1, Env.getSiteId());
    	    	stmt.setDate(2, startDate);
    	        stmt.setDate(3, finishDate);
                stmt.setLong(4,Env.getSiteId());
        	}else{
        		if ("true".equals(Env.getProperty("statistics.logOnlyCounterPages", "true")) ) {
        			stmt = conn.prepareStatement(pageSumArchiveAllOnlyWithCounter + pagesSumArchiveAllWhere);
        			logger.info("=================== Stat. for pages only with active counter");
        		}
        		else {
        			stmt = conn.prepareStatement(pageSumArchiveAll + pagesSumArchiveAllWhere);
        			logger.info("===================  Stat. for all pages");
        		}

        		stmt.setLong(1, Long.parseLong(month));
    	    	stmt.setLong(2, Long.parseLong(year));
                stmt.setLong(3,Env.getSiteId());
        	}
        	
    		ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	    		item = new StatisticsItem();
	    		String itemName = "";
	    		String title = "";
	    		if (rs.getString("title") == null){
	    			itemName = rs.getString("filename") + " (removed)";
	    			title = rs.getString("filename");
	    		}else{
	    			itemName = rs.getString("title") + " ("+rs.getString("filename")+")";
	    			title = rs.getString("title");
	    		}
	    		item.setName(itemName);
	            item.setValue(rs.getLong("counter"));
	            item.setPageName(rs.getString("filename"));
	            item.setTitle(title);
	            statL.add(item);
	        }
	        rs.close();
	    }
	    catch (SQLException ex) {
	        logger.error("- Throwing new CriticalException");
	        throw new CriticalException(ex);
	    }
	    finally {
	    	DBHelper.close(stmt);
	    	DBHelper.close(conn);
	    }
	    logger.debug("-");
       
        long max = 1;
        try{
           max = statL.getMaxValue();        
        }
        catch(Exception maxException){
            max = 1;
        }
        if(max==0){
            max=1;
        }
        for( Iterator it= statL.getList().iterator(); it.hasNext(); ){
               
            item = (StatisticsItem)it.next();  
            
            bufferElement = Xbuilder.createEl(
                    listElement, 
                    "StatisticsItem", 
                    null);
            tmpElement = Xbuilder.createEl(
                    bufferElement, 
                    "StatisticsItemName", 
                    item.getName());
            bufferElement.appendChild(tmpElement);
            tmpElement = Xbuilder.createEl(
                    bufferElement, 
                    "StatisticsItemNameQty", 
                    Long.toString(item.getValue()));
            tmpElement.setAttribute("style","td"+colors[curColor]);
            Long curLength = 
                              StatisticsXmlBuilder.GRAPHICAL_WIDTH 
                           *  item.getValue() 
                           /  max;
            tmpElement.setAttribute("length",curLength.toString());            
           
            bufferElement.appendChild(tmpElement);
            Xbuilder.addEl(bufferElement, "PageName", item.getPageName());
            Xbuilder.addEl(bufferElement, "PageTitle", item.getTitle());
            listElement.appendChild(bufferElement);
            
            if (curColor==(colors.length-1)){
                curColor=0;
            }
            else{
                curColor++;
            }       
        } 
        
        bufferElement = Xbuilder.createEl(
                parentElement, 
                "NavigationAttributes", 
                null);
        tmpElement = Xbuilder.createEl(
                bufferElement, 
                "Months", 
                null 
                );
        int currentId = 11;
        int counter = 1;
        for( Iterator it = months.iterator(); it.hasNext() ; counter++){
            Month curMonth = (Month)it.next();
            
            tempEl = Xbuilder.createEl(
                    tmpElement, 
                    "Month", 
                    Integer.toString(curMonth.getMonth()) 
            );
            
            tempEl.setAttribute("MonthName",curMonth.getName() );
            tempEl.setAttribute("Year",Integer.toString( curMonth.getYear() ));
            
            tempEl.setAttribute("counter",Integer.toString(counter));
            tmpElement.appendChild(tempEl);
            
        }
        bufferElement.appendChild(tmpElement);
        
        
        tmpElement = Xbuilder.createEl(
                bufferElement, 
                "Years", 
                null 
        );   
        for( Iterator<Integer> it = years.iterator(); it.hasNext() ; ){
            Integer curYear = (Integer)it.next();
            tempEl = Xbuilder.createEl(
                    tmpElement, 
                    "YearItem", 
                    curYear.toString()
            );
            tmpElement.appendChild(tempEl);
            
        } 
        bufferElement.appendChild(tmpElement);
        
       if( attributes!=null && !attributes.isEmpty() ){ 
           Set<String> keys = attributes.keySet(); 
           
           for( Iterator<String> it = (Iterator<String>)keys.iterator();
                it.hasNext(); 
                ){
               String cur = it.next();
                tmpElement = Xbuilder.createEl(
                        bufferElement, 
                        cur, 
                        attributes.get(cur)
                        );
                bufferElement.appendChild(tmpElement);  
               
           }
       }
        parentElement.appendChild(listElement);
        parentElement.appendChild(bufferElement);
    }
    
    public static void buildUsersList(
            Element parentElement, 
            Statement statement, 
            RequestContext request
            )
    throws SQLException, CriticalException
    {
        Element listElement = null;
        Element bufferElement = null;
        Element tmpElement = null;
        Element tempEl = null;
        Element usrEl = null;
        bufferElement = Xbuilder.createEl(
                parentElement, 
                "Users", 
                null 
        );
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{   
            conn = DBHelper.getConnection();
            stmt = conn.prepareStatement(usersListSql);
            stmt.setLong(1,Env.getSiteId());
            rs = stmt.executeQuery();
            SessionData session = request.getSessionData();         
            Long sessionUserId = (long)session.getUserId();
            
            UserPageStatisticsForm form = new UserPageStatisticsForm(request);
            Long userId = null;
            if( form != null) {
                    userId = form.getUserId();
            }
            if( userId==null ){
                userId = sessionUserId;
            }
            
            Long selectedUserId = new Long(userId);

            while (rs.next()) {
                listElement = Xbuilder.createEl(
                        bufferElement, 
                        "User", 
                        null 
                );
               
                listElement.setAttribute( 
                          "UserLogin",rs.getString("username")
                        + " / "
                        + rs.getString("login")
                );
                
                listElement.setAttribute("UserId",rs.getString("id"));
                
                if(sessionUserId.equals(rs.getLong("id")) ) {
                    listElement.setAttribute("sessionUser","true");
                    
                };
                if(selectedUserId.equals(rs.getLong("id")) ) {
                    listElement.setAttribute("selectedUser","true");
                   
                };
                
                bufferElement.appendChild(listElement);
            }
        }
        catch( Exception e ){
                logger.error("generating User List error");
        }finally{
        	DBHelper.close(rs, stmt, conn);
        }
        parentElement.appendChild(bufferElement);    
    }
}