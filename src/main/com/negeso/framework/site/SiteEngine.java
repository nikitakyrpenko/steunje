/*
 * @(#)$Id: SiteEngine.java,v 1.14, 2006-01-25 11:02:41Z, Svetlana Bondar$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.site;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

/**
 *
 * SiteEngine singleton. Site subsystem facade.
 * 
 * @version		$Revision: 15$
 * @author		Olexiy Strashko
 * 
 */
public class SiteEngine {
    
    private static SiteEngine instance = new SiteEngine();
    
    private static SiteCache cache = new SiteCache();
    
    private static MentrixConfigCache cacheMentrixConfigs = new MentrixConfigCache();

	private static final Long DEFAULT_SITE_ID = 1L;
    
    private static Logger logger = Logger.getLogger(SiteEngine.class);
    
    /** This Factory Method returns an instance of SiteEngine */
    public static SiteEngine get(){ return SiteEngine.instance; }
    
    /**
     * Returns a site by its id.
     * 
     * @return an instanse of Site (never null).
     * @throws RuntimeException if the site not found.
     */
    public Site getSite(Long siteId){
        logger.debug("+");
        
        Site site = cache.getSite(siteId);
        if (site == null) {
        	logger.info("site is null: " + site);
        } else {}
        
        //if (true){
        if ( site == null ){
            logger.info("load site into cache");        
            Connection con = null;
            try {
                con = DBHelper.getConnection();
                site = Site.findById(con, siteId);                                
                Validate.notNull(site, "Site not found by id: " + siteId);
                site.setCacheTime(System.currentTimeMillis());
                cache.addSite(site.getId(), site);
                logger.debug("- added to cache");
                return site;
            } catch(SQLException e) {
                logger.error("- SQLException");
                throw new RuntimeException(e);
            } finally {
                DBHelper.close(con);
            }
        }
        else{
        	logger.debug("- returned from cache");
        	return site;
        }
    }
    
    public MentrixConfig getMentrixConfig(Long siteId) {
        logger.debug("+");
        
        MentrixConfig mentrixConfig = cacheMentrixConfigs.getMentrixConfig(siteId);
        if (mentrixConfig == null) {
        	logger.info("mentrixconfig is null: " + mentrixConfig);
        }
        
        if ( mentrixConfig == null ){
            logger.info("load mentrixconfig into cache");        
            Connection con = null;
            try {
                con = DBHelper.getConnection();
                mentrixConfig = MentrixConfig.findById(con, siteId);
                if (mentrixConfig != null) {
                	mentrixConfig.setCacheTime(System.currentTimeMillis());
                	cacheMentrixConfigs.addMentrixConfig(mentrixConfig.getId(), mentrixConfig);
                	logger.debug("- added to cache");
                }
            } catch(SQLException e) {
                logger.error("- SQLException");
            } catch(CriticalException e) {
                logger.error("- CriticalException");
            } finally {
                DBHelper.close(con);
            }
        }
        else{
        	logger.debug("- returned from cache");
        }    	
    	return mentrixConfig;
    }
    
    /**
     * Reloads site from database if site design expired.
     * 
     * @param siteId  reloaded site id 
     */
    public void checkExpiredSite(Long siteId){
//    	if (Env.isMultisite()                 	
        logger.debug("+");            
        Connection con = null;
        PreparedStatement stmt = null;
        try{
            con = DBHelper.getConnection();
			stmt = con.prepareStatement("SELECT expired FROM site WHERE id = " + siteId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            //if site design expired
            if ( rs.getBoolean("expired") ) {
                Site site = Site.findById(con, siteId);
                Validate.notNull(site, "Site not found by id: " + siteId);
                site.setCacheTime(System.currentTimeMillis());
                cache.addSite(site.getId(), site);
                logger.debug("- REadded to cache");	
                try {
	                MentrixConfig mentrixConfig = MentrixConfig.findById(con, siteId);
	                Validate.notNull(mentrixConfig, "MentrixConfig not found by id: " + siteId);
	                mentrixConfig.setCacheTime(System.currentTimeMillis());
	                cacheMentrixConfigs.addMentrixConfig(mentrixConfig.getId(), mentrixConfig);
	                logger.debug("- REadded to cache");
                } catch (CriticalException e) {
                	logger.error("- error", e);
                }
                DBHelper.close(stmt);
                
				stmt = con.prepareStatement("UPDATE site SET expired = false WHERE id = " + siteId);	                
                stmt.executeUpdate();                	            	
            }
        }
        catch(SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
        finally{
        	DBHelper.close(stmt);
            DBHelper.close(con);
        }        	
        logger.debug("-");
    }


    /**
     * @param serverName
     * @return
     */
    public Long resolveUrlToSiteId(String serverName) {
    	if (!Env.isMultisite()) {
    		return this.getDefaultSiteId();
    	}
        Long siteId = SiteUrl.resolveUrlToSiteId(serverName);
        if ( siteId == null ){
            logger.error("Unable to resolve: " + serverName + ", no such site");
        }
        return siteId;
    }
    
    public Long getDefaultSiteId() {
    	return DEFAULT_SITE_ID;
    }

    /** Cleans inner caches */
    public void reset(){ cache.reset(); }
    
    /** Prevent instantiation by class clients */
    private SiteEngine() { /* singleton */ }
    
    
    
    private static class MentrixConfigCache {
        
        private MentrixConfigCache() { /* singleton */ }

        private HashMap<Long, MentrixConfig> siteConfigs =
                new HashMap<Long, MentrixConfig>();
        
        
        synchronized void reset(){
            this.siteConfigs.clear();
        }
        
        synchronized MentrixConfig getMentrixConfig(Long id) {
            return siteConfigs.get(id);
        }
        
        synchronized void addMentrixConfig(Long id, MentrixConfig siteConfig){
            siteConfigs.put(id, siteConfig);
        }
    }

    
    /**
     * The cache is made as a separate class to make synchronization of
     * access/modification easier.
     */
    private static class SiteCache {
        
        private SiteCache() { /* singleton */ }

        private HashMap<Long, Site> sites =
                new HashMap<Long, Site>();
        
        
        synchronized void reset(){
            this.sites.clear();
        }
        
        synchronized Site getSite(Long id) {
            return sites.get(id);
        }
        
        synchronized void addSite(Long id, Site site){
            sites.put(id, site);
        }
    }
}
