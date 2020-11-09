/*
* @(#)$Id: ManageCountersCommand.java,v 1.0, 2006-01-25 15:33:58Z, Andrey Morskoy$
*
* Copyright (c) 2005 Negeso Ukraine
*
* This software is the confidential and proprietary information of Negeso
* ("Confidential Information").  You shall not disclose such Confidential 
* Information and shall use it only in accordance with the terms of the 
* license agreement you entered into with Negeso.
*/

package com.negeso.module.statistics.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.statistics.domain.Counter;

/**
 *
 * Manage Counters Command
 * 
 * @version                $Revision: 1$
 * @author                 Svetlana Bondar
 * 
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ManageCountersCommand extends AbstractCommand {
    private static Logger logger = Logger.getLogger(ManageCountersCommand.class);

    
    private Element model = Xbuilder.createTopEl("model");
    
    private Collection<String> errors = new LinkedList<String>();
    @ActiveModuleRequired
	public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        
        if (!SecurityGuard.isContributor(request.getSession().getUser())) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- not a contributor");            
            return response;
        }
        
        
        //TODO SB:        
        model = Xbuilder.createTopEl("model");
        String action = request.getParameter("action");

        if ("delete".equals(action))	{
        	deleteCounter(request.getLong("id"));        	
        	logger.debug("delete id=" + request.getLong("id"));        	
        }	
        
        if (request.getParameter("save") != null)	{      
        	updateCounters();
        }	else	{        	
        	list();
        }

        if ("insert".equals(action))	{ //add in list end         	
        	insertCounters();  
        }        	

        
        for (String error : errors) {
            if(error != null && !error.equals("")) {
                Xbuilder.addEl(model, "error", error);
            }
        }        
        
        Map resultMap = response.getResultMap();
        resultMap.put(OUTPUT_XML, model.getOwnerDocument());        
        response.setResultName(RESULT_SUCCESS);
        logger.debug("-");
        return response;
	}

    private void list() {
        logger.debug("+");        
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = DBHelper.getConnection();
            st = connection.prepareStatement(Counter.selectFrom);
            
            ResultSet rs = st.executeQuery();

            while (rs.next()){

            	Element elCounter = Xbuilder.addEl(model, "counter_page", null);                
                Xbuilder.setAttr(elCounter, "id", rs.getLong("id"));
                Xbuilder.setAttr(elCounter, "page_id", rs.getLong("page_id"));
                Xbuilder.setAttr(elCounter, "counter", rs.getBoolean("is_counter"));
                Xbuilder.setAttr(elCounter, "page_name", rs.getString("page_name"));
                logger.debug("id=" + rs.getLong("id"));    
                
			}            
            logger.debug("-");            
        } catch (Exception e) {
            logger.error("Exception", e);
            addException(e);        	
        } finally {
            DBHelper.close(st);
            DBHelper.close(connection);
        }
    }

    private void updateCounters() {    	
        logger.debug("+");
        RequestContext request = getRequestContext();        
        Connection connection = null;
        PreparedStatement st = null;
        try {
            connection = DBHelper.getConnection();
            st = connection.prepareStatement(Counter.selectFrom);
            
            ResultSet rs = st.executeQuery();
            while (rs.next()){
            	Counter counter = new Counter();
            	Long id = counter.load(rs);
            	            	
        		if ( "on".equalsIgnoreCase(request.getParameter(id.toString())) ) {
        			counter.setActive(true); 
        		} 	else {
        			counter.setActive(false);
        		}
        		counter.update(connection);
            	
            	Element elCounter = Xbuilder.addEl(model, "counter_page", null);                
                Xbuilder.setAttr(elCounter, "id", id);
                Xbuilder.setAttr(elCounter, "page_id", rs.getLong("page_id"));
                Xbuilder.setAttr(elCounter, "counter", counter.isActive());
                Xbuilder.setAttr(elCounter, "page_name", rs.getString("page_name"));
                logger.debug("id=" + rs.getLong("id"));    
                
			}            
            logger.debug("-");            
        } catch (Exception e) {
            logger.error("Exception", e);
            addException(e);        	
//            throw new RuntimeException(e);
        } finally {
            DBHelper.close(st);
            DBHelper.close(connection);
        }
    }
 
    private void deleteCounter(Long id) {    	
        logger.debug("+");        
        Connection connection = null;       
        try {
            connection = DBHelper.getConnection();
            Counter.deleteById(connection, id);
            logger.debug("-");            
        } catch (Exception e) {
            logger.error("Exception", e);
            addException(e);        	
        } finally {
            DBHelper.close(connection);
        }
    }

	private void insertCounter(Long id, String name, Connection connection) 
		throws CriticalException 
	{    	
	    logger.debug("+");
    	Counter counter = new Counter();
    	counter.setPageId(id);
    	counter.setPageName(name);
    	counter.setActive(false);
    	        
        counter.insert(connection);
                            
    	Element elCounter = Xbuilder.addEl(model, "counter_page", null);                
        Xbuilder.setAttr(elCounter, "id", counter.getId());
        Xbuilder.setAttr(elCounter, "page_id", counter.getPageId());
        Xbuilder.setAttr(elCounter, "counter", counter.isActive());
        Xbuilder.setAttr(elCounter, "page_name", counter.getPageName());
        logger.debug("insert id=" + counter.getId());    
	        
	}
    
    private void insertCounters() {    	
        logger.debug("+");
        RequestContext request = getRequestContext();
        Long id = request.getLong("id");	//menuItemId or pageId
        String link = request.getParameter("link");
        
        Connection connection = null;        
        try {
            connection = DBHelper.getConnection();
            
            if( link != null && !link.equals("")) { //insert page        	
                insertCounter(
                		id, 
                		request.getParameter("title") + " ( " + link + " )", 
                		connection
                );
            } else {								//insert branch
            	insertBranch(id, connection);        	
            }

        } catch (Exception e) {
            logger.error("Exception", e);
            addException(e);        	
        } finally {
            DBHelper.close(connection);
        }
                                
    }
    
    private void insertBranch(Long menuItemId, Connection connection) 
    	throws Exception
    {    	
        logger.debug("+");
        ArrayList<Long> parents = new ArrayList<Long>();

        PreparedStatement menuTreeStmt = connection.prepareStatement(
            "select m2.level, mi2.id, mi1.id as parent_id" +
            " ,p.title, p.filename, p.id as page_id" +            
            " from menu_item mi1 " +
            " right join menu m2 on (m2.parent_menu_item_id = mi1.id)" +
            " join menu_item mi2 on (m2.id = mi2.menu_id) " +
            " left join page p on (mi2.link = p.filename and " +
            "		p.id not in (select page_id from stat_counter))" +
            " where m2.level > 0" +
            " order by m2.level, mi1.id"
        );
                
        ResultSet rs = menuTreeStmt.executeQuery();        
        while (rs.next()) {
        	if ( rs.getLong("id") == menuItemId || parents.contains(rs.getLong("parent_id"))) {
//        		logger.debug("id=" + rs.getLong("id") + " parentId=" + rs.getLong("parent_id") +
//        				"page_id" + rs.getLong("page_id") );        		

        		parents.add(rs.getLong("id"));
        		if ( rs.getLong("page_id") > 0 ) {        			
        			insertCounter(                		
        					rs.getLong("page_id"), 
        					rs.getString("title") + " ( " + rs.getString("filename") + " )", 
        					connection
        			);
        		}
        	}        	
        }
        rs.close();
       	DBHelper.close(menuTreeStmt);
        logger.debug("-");
    }    

    private void addException(Exception e) {
        errors.add(e.getMessage());
        if(e.getCause() != null) errors.add(e.getCause().getMessage());
    }

	@Override
	public String getModuleName() {
		return ModuleConstants.SITE_STATISTICS_MODULE;
	}
    
}