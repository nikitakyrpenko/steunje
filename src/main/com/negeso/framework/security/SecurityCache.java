/*
 * @(#)$Id: SecurityCache.java,v 1.20, 2007-04-11 06:40:06Z, Alexander Serbin$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.security;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.module.user.domain.DefaultContainer;


/**
 * Security permissions cache. Designed to make security checks consistent, and
 * to resolve roles as specified in
 * {@link com.negeso.framework.security.SecurityGuard} which uses this class
 * internally.
 * <br>
 * Singleton.
 * 
 * @version     $Revision: 21$
 * @author Stanislav Demchenko
 */

abstract public class SecurityCache {
    
    /** Holds caches of mentrix's sites (site id is the key of the map) */
    private static HashMap<Long, SecurityCache> CACHES = new HashMap(1);
    
    
    /**
     * Stores permissions of all users (including "guest" user),
     * except administrators. 
     * <pre>
     * Key is user id.
     * Value is another Map, where
     *      Key is container id.
     *      Value is the highest role of the user for the container.
     * </pre>
     * The inner map for each user also contains NULL key as container id, and
     * the value is the biggest user's role in the system
     */
    private final Map<Long, Map> UIDS_CIDS_ROLES = new HashMap();
    
    
    /** Collection of ids of administrators */
    private final Collection<Long> ADMINISTRATIVE_UIDS = new HashSet();
    
    
    /** Roles of guests. Keys are containerids, values are role names */
    private Collection<Long> FREE_CIDS = new HashSet();
    
    
    /**
     * "true" indicates that
     * <ul>
     * <li> the cache has been initialized </li>
     * <li> the latest update was completed successfully (not interrupteded by
     *      ecxeptions) and the cached data can be considered consistent.
     * </ul>
     */
    private boolean cacheLoaded = false;

    private final Long siteId;
    
    private static Logger logger = Logger.getLogger(SecurityCache.class);
    
    /** Marks the cache as invalid and clears it */
    private synchronized void clearCache() {
        cacheLoaded = false;
    	CACHES.clear();
        UIDS_CIDS_ROLES.clear();
        FREE_CIDS.clear();
        ADMINISTRATIVE_UIDS.clear();
    }
    
    protected boolean isAnonymousUser(Long uid) {
    	return uid == null;
    }
    
    protected boolean isDefaultContainer(Long containerId) {
    	return DefaultContainer.isMyId(containerId); 	
    }
    
    protected abstract String doResolveRole(Long uid, Long cid); 
    
    /**
     * Caches publicly available containers in a separate list
     * in Collection FREE_CIDS.
     */
    protected void cacheFreeContainers() throws SQLException {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                " SELECT DISTINCT containers.id " +
                " FROM containers, permissions, groups " +
                " WHERE groups.site_id = " + siteId +
                " AND groups.role_id = 'guest' " +
                " AND groups.id = permissions.group_id " +
                " AND permissions.container_id = containers.id ");
            while(rs.next()) {
                FREE_CIDS.add(new Long(rs.getLong("id")));
            }
            logger.debug("-");
        } finally {
            DBHelper.close(rs, stmt, conn);
        }
    }
    
    
    /**
     * Caches administrators into a separate list. These users will not be
     * duplicated in other maps and collections.
     */
    protected void cacheAdministrators() throws SQLException {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            Map administratorPermissions = new HashMap();
            administratorPermissions.put(null, SecurityGuard.ADMINISTRATOR);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                    " SELECT DISTINCT user_list.id " +
                    " FROM user_list, members, groups " +
                    " WHERE groups.site_id = " + siteId +
                    " AND groups.role_id = 'administrator' " +
                    " AND members.group_id = groups.id " +
                    " AND user_list.id = members.user_id " );
            while(rs.next()) {
                ADMINISTRATIVE_UIDS.add( new Long(rs.getLong("id")) );
            }
            logger.debug("-");
        } finally {
            DBHelper.close(rs, stmt, conn);
        }
    }
    
    
    /**
     * Caches explicit permissions of users to cotainers; then caches
     * rights to the to the null container; and finally caches implicit rights
     * to all other containers (implicit rights to containers).
     * <br>
     * <br>
     * This methoddoes not cache administrators.
     * 
     * @throws SQLException if any database-level error occurs
     */
    protected Map cacheUser(Long uid) throws SQLException {
        logger.debug("+");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                    " SELECT id FROM user_list WHERE id =" + uid + " LIMIT 1");
            if(!rs.next()) {
                logger.warn("- user is not in [user_list]");
                return null;
            }
            Map<Long, String> cids2roles = getExplicitRolesMap(conn, uid);
            cids2roles.put(null, getRoleForNullContainer(conn, uid));
            for(Iterator iter = FREE_CIDS.iterator(); iter.hasNext();) {
                Object cid = iter.next();
                if(!cids2roles.containsKey(cid)) {
                    cids2roles.put((Long) cid, SecurityGuard.VISITOR);
                }
            }
            UIDS_CIDS_ROLES.put(uid, cids2roles);
            logger.debug("-");
            return cids2roles;
        } finally {
            DBHelper.close(rs, stmt, conn);
        }
    }
    
    
    /** Finds the highest role of the user in the system */
    private String getRoleForNullContainer(Connection conn, Long uid)
    throws SQLException {
        logger.debug("+");
        Collection roles = new HashSet();
        roles.add(SecurityGuard.GUEST);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            " SELECT DISTINCT " +
            " groups.role_id " +
            " FROM members, groups " +
            " WHERE members.user_id = " + uid +
            " AND members.group_id = groups.id ");
        while(rs.next()) {
            roles.add(rs.getString("role_id"));
        }
        DBHelper.close(rs);
        DBHelper.close(stmt);
        logger.debug("-");
        return SecurityGuard.getHighestRole(roles);
    }
    
    
    /** Builds map of explicitly assigned user's rights to containers */
    private Map getExplicitRolesMap(Connection conn, Long uid)
    throws SQLException {
        logger.debug("+");
        Map cids2roles = new HashMap();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            " SELECT DISTINCT " +
            " permissions.container_id AS cid, " +
            " groups.role_id " +
            " FROM members, groups, permissions " +
            " WHERE members.user_id = " + uid +
            " AND members.group_id = groups.id " +
            " AND groups.id = permissions.group_id " +
            " ORDER BY cid " );
        boolean first = true;
        Long curCid = null;
        Collection roles = new HashSet();
        while(rs.next()) {
            Long cid = new Long(rs.getLong("cid"));
            String role = rs.getString("role_id");
            if(first) {
                curCid = cid;
                first = false;
            }
            if(!cid.equals(curCid)) {
                cids2roles.put(curCid, SecurityGuard.getHighestRole(roles));
                roles.clear();
                curCid = cid;
            }
            roles.add(role);
        }
        if(!first) {
            cids2roles.put(curCid, SecurityGuard.getHighestRole(roles));
        }
        rs.close();
        stmt.close();
        logger.debug("-");
        return cids2roles;
    }
    
    /** Returns thread-local security cache */
    private synchronized static SecurityCache getInstance() {
        logger.debug("+");
        Long siteId = Env.getSiteId();
        SecurityCache securityCache = CACHES.get(siteId);
        if (securityCache == null) {
            securityCache = resolveDefaultContainerStrategy(siteId);
            CACHES.put(siteId, securityCache);
        }
        logger.debug("-");
        return securityCache;
    }
    
    private static SecurityCache resolveDefaultContainerStrategy(Long siteId) {
    	switch (getCurrentSecurityMode()) {
		case 0:
			return new DefaultContainerStrategy0(siteId);
		case 1:
			return new DefaultContainerStrategy1(siteId);
		}
		throw new CriticalException("Security mode is not defined");
	}

	/** Clears security cache */
    public static void invalidate() {
        logger.debug("+ -");
        getInstance().clearCache();
    }
    
    /** Cache is valid during 1 second (unless it was cleared manually). */
    static String resolveRole(Long uid, Long cid) {
        logger.debug("+ -");
        return getInstance().doResolveRole(uid, cid);
    }
    
    /** Prevent instantiation from outside. */
    protected SecurityCache(Long siteId) { this.siteId = siteId; }

	protected static int getCurrentSecurityMode() {
		return Env.getIntProperty("security.mode", 0);
	}

	protected boolean isCacheLoaded() {
		return cacheLoaded;
	}

	protected void setCacheLoaded(boolean b) {
		cacheLoaded = true;
	}

	protected Collection<Long> getFREE_CIDS() {
		return FREE_CIDS;
	}

	protected Collection<Long> getADMINISTRATIVE_UIDS() {
		return ADMINISTRATIVE_UIDS;
	}

	protected Map<Long, Map> getUIDS_CIDS_ROLES() {
		return UIDS_CIDS_ROLES;
	}

	 protected  String templateResolveRole(Long uid, Long containerId) {
		    try {
		        if (  isAnonymousUser(uid) &&
		          (containerId == null || isDefaultContainer(containerId))
		          ) {
		            logger.debug("- null uid & null cid, hence role is GUEST");
		            return SecurityGuard.GUEST;
		        }
		        if(!isCacheLoaded()) {
		            cacheFreeContainers();
		            cacheAdministrators();
		            setCacheLoaded(true);
		        }
		        if(isAnonymousUser(uid)) {
		            logger.debug("- guest");
		            return getFREE_CIDS().contains(containerId) ? SecurityGuard.GUEST : null;
		        }
		        Object p = getFREE_CIDS().contains(User.findById(uid));
		        if(getADMINISTRATIVE_UIDS().contains(uid)){
		            logger.debug("- user is administrator");
		            return SecurityGuard.ADMINISTRATOR;
		        }
		        Map<Long, String> cids2roles = getUIDS_CIDS_ROLES().get(uid);
		        if(cids2roles == null) {
		            cids2roles = cacheUser(uid);
		            if(cids2roles == null) {
		                logger.warn("- invalid user uid: " + uid);
		                return getFREE_CIDS().contains(containerId) ? SecurityGuard.GUEST : null;
		            }
		        }
		        return cids2roles.get(containerId);
		    } catch (Exception ex) {
		        logger.error("- Exception", ex);
		        return null;
		    }
	 }

}
