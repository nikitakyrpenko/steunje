/*
 * @(#)$Id: SecurityGuard.java,v 1.25, 2007-01-09 18:35:44Z, Anatoliy Pererva$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.security;


import java.util.Collection;
import java.util.Objects;

import org.apache.log4j.Logger;


import com.negeso.framework.controller.SessionData;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.User;

/**
 * Utility class to resolve and check security permissions.
 *
 * @version     $Revision: 26$
 * @author      Olexiy Strashko
 * @author      Stanislav Demchenko
 */
public class SecurityGuard {
    
    
    /**
     * Anonymous web-surfers can view certain resources if
     * <ul>
     * <li> The group of anonymous users can access the container to which
     *      the resource belongs, or
     * <li> the resource does not belong to any particular container which
     *      means that it belongs to the null ("default") container
     * </ul>
     */
    public static final String GUEST = "guest";
    
    
    /**
     * Authenticated visitor can access restricted areas of the site.<br>
     * This role is applicable both to contributors and to end-users.
     * it does not reflect the fact whether the account was created through
     * Security Module or Login Module.
     * Instead, this role states the fact, that
     * <ul>
     * <li>user has an account in the system (user is authenticated), and</li>
     * <li>user is allowed, explicitly or implicitly, to view resources
     *     of the container</li>
     * </ul>
     */
    public static final String VISITOR = "visitor";
    
    
    /**
     * Author can create versions (requests for approval) of site content.<br>
     * This role will have sense when versioning is implemented. Until then,
     * it is not used.
     */
    public static final String AUTHOR = "author";
    
    
    /**
     * Editor can edit (and appove, when versioning is implemented)
     * resources of a particular container, when allowed explicitly.
     * Also, editor can edit resourcesd of "null container" (that is, those
     * outside any container).
     */
    public static final String EDITOR = "editor";
    
    
    /**
     * Manager can manage (edit/add/delete) resources belonging to
     * a particular container. All managers can manage resources
     * in the null container.
     */
    public static final String MANAGER = "manager";
    
    
    /**
     * Administrator can manage user accounts and all resources
     * belonging to any containers.
     */
    public static final String ADMINISTRATOR = "administrator";
    
    
    /** Roles arranged by supremacy (the first is the highest) */
    private static final String[] RANKED_ROLES = {
            ADMINISTRATOR,
            MANAGER,
            EDITOR,
            AUTHOR,
            VISITOR,
            GUEST
    };
    
    
    private static final Logger logger = Logger.getLogger(SecurityGuard.class);

    
    /**
     * Returns "true" if session user is a member of at least one group based
     * on role "administrator", "manager", "editor" or "author".
     * The method existed since earlier releases and should be replaced.
     * Now it is functionally identical to {@link SecurityGuard#canContribute(User user, Long cid)}
     * 
     * @deprecated
     */
    public static boolean isUserAutorized(RequestContext request) throws CriticalException {
        logger.debug("+");
        SessionData sessionData = request.getSessionData();
        if (!sessionData.isSessionStarted()){
            logger.debug("- session is not started");
            return false;
        }
        logger.debug("-");
        return canContribute(sessionData.getUser(), null);
    }
    
    
    /**
     * Finds the role of the user for resources of the container.
     * If the user can access the container through several groups,
     * the role which gives most permissions is returned.  
     * 
     * @param userId
     *              user whose permissions will be resolved; null for
     *              anonymous users
     * 
     * @param containerId
     *              container, for which we want to resolve the role;
     *              null is the least protected container
     * @return
     *              name of the role (SecurityGuard.GUEST ...
     *              SecurityGuard.ADMINISTRATOR); null if the user has no right
     *              for the container at all (not even can view).
     */
    public static String resolveRole(Long userId, Long containerId) {
        return SecurityCache.resolveRole(userId, containerId);
    }
    
    
    /** For description see {@link SecurityGuard#resolveRole(Long,Long)} */
    public static String resolveRole(User user, Long containerId) {
        if (Objects.nonNull(user)
                && Objects.nonNull(user.getType())
                && user.getType().equals("hairdresser"))
            return "hairdresser";

        return resolveRole(getUid(user), containerId);
    }
    
    
    /**
     * Checks if the user has role {@link SecurityGuard#ADMINISTRATOR}.
     * 
     * @param userId
     *              domain object User.
     *              If <tt>null</tt>, user is considered guest.
     * 
     * @return
     *              true if the user is an administrator;
     *              false otherwise 
     */
    public static boolean isAdministrator(Long userId) {
        logger.debug("+");
        String role = resolveRole(userId, null);
        if(ADMINISTRATOR.equals(role))
        {
            logger.debug("- administrator");
            return true;
        } else {
            logger.debug("- not an administrator");
            return false;
        }
    }
    
    
    /** For description see {@link SecurityGuard#isAdministrator(Long)} */
    public static boolean isAdministrator(User user) {
        logger.debug("+ -");
        return isAdministrator(getUid(user));
    }
    
    
    /**
     * Checks if the user can contribute to resources in null container.
     * <p>
     * This method does not really check if the user is a
     * contributor, it is merely a shorthand for
     * <code>SecurityGuard.canContribute(userId, null)</code>.
     * </p>
     * <p>
     * Strictly speaking, contributor is a person who has an account
     * created through Security Module (unlike Login Module).
     * To distinguish between contributors and non-contributors,
     * use a different approach.
     * </p>
     * 
     * @param userId
     *              id of the domain object ({@link User#getId()}).
     *              If <tt>null</tt>, user is considered a guest.
     * 
     * @return
     *              true if the user can contribute to the null container;
     *              false otherwise 
     */
    public static boolean isContributor(Long userId) {
        return canContribute(userId, null);
    }
    
    
    /**
     * Check if user is a Superuser. Superuser is Negeso developer. This method is proided
     * to enable special CMS security rights for Negeso Superusers! :) 
     * 
     * @param userId
     * @return
     */
    /*temporary commented by amorskoy
     * reason: Superuser can't be specified by userId. Only SessionData contains neccesary 
     * information
     * */
  /*  public static boolean isSuperUser(Long userId)
    {
        logger.debug("+ -");
        return isAdministrator(userId);
    }*/

    
    /**
     * Check if user is a Super User. Super User is Negeso developer. This method is proided
     * to enable special CMS security rights for Negeso Super Users! :) 
     * 
     * @param user
     * @return
     */
    public static boolean isSuperUser(User user) {
        return user==null? false : user.isSuperUser();
    }

    
    /** For description see {@link SecurityGuard#isContributor(Long)} */
    public static boolean isContributor(User user) {
        return isContributor(getUid(user));
    }
    
    
    /**
     * Checks if the user can manage resources of the container (has role
     * {@link SecurityGuard#MANAGER} or higher).
     * If container is null, returns true if user is a member of at least
     * one group based on role {@link SecurityGuard#MANAGER}
     * 
     * @param userId
     *              id of the domain object ({@link User#getId()}).
     *              If <tt>null</tt>, user is considered a guest.
     * 
     * @param containerId
     *              id of {@link com.negeso.module.user.domain.Container},
     *              may be null
     * 
     * @return
     *              true if the user can manage resources of the container;
     *              false otherwise
     */
    public static boolean canManage(Long userId, Long containerId) {
        logger.debug("+");
        String role = resolveRole(userId, containerId);
        if(ADMINISTRATOR.equals(role) || MANAGER.equals(role))
        {
            logger.debug("- can manage");
            return true;
        } else {
            logger.debug("- cannot manage");
            return false;
        }
    }
    
    
    /** For description see {@link SecurityGuard#canManage(Long,Long)} */
    public static boolean canManage(User user, Long containerId) {
        return canManage(getUid(user), containerId);
    }
    
    
    /**
     * Checks if the user can edit (and approve/publish) resources of
     * the container (has role {@link SecurityGuard#EDITOR} or higher).
     * If container is null, returns true if user is a member of at least
     * one group based on role {@link SecurityGuard#EDITOR}
     * 
     * @param userId
     *              id of the domain object ({@link User#getId()}).
     *              If <tt>null</tt>, user is considered a guest.
     * 
     * @param containerId
     *              id of {@link com.negeso.module.user.domain.Container},
     *              may be null
     * 
     * @return
     *              true if the user can manage resources of the container;
     *              false otherwise
     */
    public static boolean canEdit(Long userId, Long containerId) {
        logger.debug("+");
        String role = resolveRole(userId, containerId);
        if(ADMINISTRATOR.equals(role)
            || MANAGER.equals(role)
            || EDITOR.equals(role))
        {
            logger.debug("- can edit");
            return true;
        } else {
            logger.debug("- cannot edit");
            return false;
        }
    }
    
    
    /** For description see {@link SecurityGuard#canEdit(Long,Long)} */
    public static boolean canEdit(User user, Long containerId) {
        logger.debug("+ -");
        return canEdit(getUid(user), containerId);
    }
    
    
    /**
     * Checks if the user can edit resources of the container (has role
     * {@link SecurityGuard#AUTHOR} or higher).
     * If container is null, returns true if user is a member of at least
     * one group based on role {@link SecurityGuard#AUTHOR}
     * 
     * @param userId
     *              id of the domain object ({@link User#getId()}).
     *              If <tt>null</tt>, user is considered a guest.
     * 
     * @param containerId
     *              id of {@link com.negeso.module.user.domain.Container},
     *              may be null
     * 
     * @return
     *              true if the user can manage resources of the container;
     *              false otherwise
     */
    public static boolean canContribute(Long userId, Long containerId) {
        logger.debug("+");
        String role = resolveRole(userId, containerId);
        if(ADMINISTRATOR.equals(role)
            || MANAGER.equals(role)
            || EDITOR.equals(role)
            || AUTHOR.equals(role))
        {
            logger.debug("- can edit");
            return true;
        } else {
            logger.debug("- cannot edit");
            return false;
        }
    }
    
    
    /** For description see {@link SecurityGuard#canContribute(Long,Long)} */
    public static boolean canContribute(User user, Long containerId) {
        return canContribute(getUid(user), containerId);
    }
    
    
    /**
     * Checks if the user can view resources of the container (has role
     * {@link SecurityGuard#GUEST} or higher).
     * If container is null, returns true if user is a member of at least
     * one group based on role {@link SecurityGuard#GUEST}
     * 
     * @param userId
     *              id of the domain object ({@link User#getId()}).
     *              If <tt>null</tt>, user is considered a guest.
     * 
     * @param containerId
     *              id of {@link com.negeso.module.user.domain.Container},
     *              may be null
     * 
     * @return
     *              true if the user can manage resources of the container;
     *              false otherwise
     */
    public static boolean canView(Long userId, Long containerId) {
        return resolveRole(userId, containerId) != null;
    }
    
    
    /** For description see {@link SecurityGuard#canView(Long,Long)} */
    public static boolean canView(User user, Long containerId) {
        return canView(getUid(user), containerId);
    }
    
    
    /**
     * Finds the highest role in collection. Order of roles is defined by
     * {@link SecurityGuard#RANKED_ROLES}.
     * 
     * @param roles     collection of role names (strings)
     * 
     * @return      the highest role (one giving most permissions). null value
     *              means absence of any rights. 
     */
    static String getHighestRole(Collection roles){
        logger.debug("+");
        for(int i = 0; i < RANKED_ROLES.length; i++) {
            if(roles.contains(RANKED_ROLES[i])) {
                logger.debug("-");
                return RANKED_ROLES[i];
            }
        }
        logger.debug("- no role");
        return null;
    }
    
    
    /** Returns user id (or null if <tt>user</tt> is null). */
    private static Long getUid(User user) {
        return user == null ? null : user.getId();
    }
    
    
    private SecurityGuard() { /* The class is not for instantiation */ }
    
}
