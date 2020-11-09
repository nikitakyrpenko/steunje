/**
 * @(#)$Id: UserPageStatisticsCommand.java,v 1.1, 2007-01-09 18:59:47Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.statistics.command;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.form_manager.command.ManageFormsCommand;
import com.negeso.module.statistics.Month;
import com.negeso.module.statistics.StatisticsParameters;
import com.negeso.module.statistics.StatisticsXmlBuilder;
import com.negeso.module.statistics.cache.StatisticsCache;
import com.negeso.module.statistics.forms.PageStatisticsForm;
import com.negeso.module.statistics.forms.UserPageStatisticsForm;

@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class UserPageStatisticsCommand extends AbstractCommand{
    private static Logger logger = Logger.getLogger(UserPageStatisticsCommand.class);
    public static final String OUTPUT_DOCUMENT = "xml";
    
    @ActiveModuleRequired
    public ResponseContext execute() {
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        
        try {
            if (!SecurityGuard.canEdit(request.getSession().getUser(), null)) {
                response.setResultName(RESULT_ACCESS_DENIED);
                logger.debug("- Access denied");
                return response;
            }
          
            Element parentElement =   Xbuilder.createTopEl(
                                                        "page"
                                                        );
            String formLanguage = request.getSession().getLanguageCode(); 
           
            Connection conn = null;
            
            
            //Form attributes and requests parameters
            StatisticsParameters params = new StatisticsParameters();
            HashMap<String,String> map = new HashMap<String,String>();
            UserPageStatisticsForm form = new UserPageStatisticsForm(request);
            ArrayList<Month> months = new ArrayList<Month>();
            ArrayList<Integer> years = new ArrayList<Integer>();
            HashMap<Long,String> users = new HashMap<Long,String>();
            Integer curM = form.getMonth();
            Integer curY = form.getYear();
            Long curId = null;
            String curName = "";
            Boolean regen = false;
            if( form!=null ){
                regen = form.getDoRegen();
                curId = form.getUserId();
                
                if( curM!=null ){
                    map.put("CurrentMonth",curM.toString());
                }
                else{
                    map.put(
                            "CurrentMonth",
                            Integer.toString( Month.getLastMonth().getMonth() ));
                    curM =  Month.getLastMonth().getMonth() ;
                }
                
                if( curY!=null ){
                    map.put("CurrentYear",curY.toString());
                    months = Month.getMonths(curY);
                        
                }
                else{
                    map.put(
                            "CurrentYear",
                            Integer.toString( Month.getLastMonth().getYear() ));
                    months = Month.getMonths( Month.getLastMonth().getYear() );
                    curY = Month.getLastMonth().getYear();
               
                }
                
                if( curId!=null){
                    map.put("CurrentUserId",curId.toString());  
                    User usr = User.findById(curId);
                    if( usr!=null ){
                        curName = usr.getName();
                    }
                }
                else{
                    SessionData session = request.getSessionData();         
                    Long sessionUserId = (long)session.getUserId();
                    
                    map.put("CurrentUserId",sessionUserId.toString()); 
                    curId = sessionUserId;
                    User usr = User.findById(curId);
                    if( usr!=null ){
                        curName = usr.getName();
                    }
                }
                
            }
            params.setMap(map);
            params.setMonths(months);
            
            for( Integer i=Month.getFirstMonth().getYear(); 
                 i<=Month.getLastMonth().getYear();
                 i++
               )
            {
                years.add(i);
            }
            params.setYears(years);
            
            //START FAKE USERS
            for( Long i=1L; i<10L;i++ ){
                users.put(i,"User "+i.toString());
            }
            //END FAKE USERS
            params.setUsers(users);
            
            List<String> cachePars = new ArrayList<String>();
            cachePars.add(curM.toString());
            cachePars.add(curY.toString());
            cachePars.add(curName);
                
            String filename = StatisticsCache.getInstance().createHtmlFilename(
                              "user_page_statistics",
                              cachePars
            );
            String xmlFilename = StatisticsCache.getInstance().createXmlFilename(
                               "user_page_statistics",
                               cachePars
            );
            filename = StatisticsCache.CACHE_FOLDER + "/" + filename;
            xmlFilename = StatisticsCache.CACHE_FOLDER + "/" + xmlFilename;
          
            String path = Env.getRealPath(filename);
            String xmlPath = Env.getRealPath(xmlFilename);
           
            File cacheFile = new File(path);
            File xmlCacheFile = new File(xmlPath);
            
           Document xmlDoc = null;
             
            
           if(    !xmlCacheFile.exists() 
               || !cacheFile.exists() 
               ||  regen.toString().equals("true") ){ 
               try {
                   
                    
                    StatisticsXmlBuilder.buildUserPageStatistics(
                            parentElement,
                            null,
                            request,
                            params
                    );
                    parentElement.setAttribute("buildCache","true");
                    xmlDoc = parentElement.getOwnerDocument();                       
                    
                    
                } catch (SQLException e) {
                    logger.error("SQLException in Statistics " + e.getMessage() );
                } 
           }
           else{
               DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
               factory.setNamespaceAware(true);
               factory.setIgnoringElementContentWhitespace(true);
               File xt = new File(xmlPath);
                xmlDoc = factory.newDocumentBuilder().parse( xt );
              
                            
               if( xmlDoc!=null ){
                       parentElement = xmlDoc.getDocumentElement();
                      
               }
               
             
               parentElement.setAttribute("buildCache","false");
           }
            
           
               
            parentElement.setAttribute("cahedFilename", path);   
            parentElement.setAttribute("cahedXmlFilename", xmlPath);   
            request.setParameter("csv_title","Statistics per user per page");
            String head =   "User; "
                        +    curId
                        +   " "
                        +    curName
                        +   "\n"
                        +   "Page;Hits"
                        ;
            request.setParameter("csv_head",head);
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, xmlDoc);
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
            
        }
        catch (Exception e) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request ", e);
        }
        return response;
    }

	@Override
	public String getModuleName() {
		return ModuleConstants.SITE_STATISTICS_MODULE;
	}
    
}
