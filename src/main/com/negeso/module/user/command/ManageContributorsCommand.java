/*
 * @(#)ManageContributorsCommand.java  Created on 30.05.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.user.command;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.security.SecurityCache;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.user.domain.Group;


/**
 * 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class ManageContributorsCommand extends AbstractCommand {


    /** Id of an existing user (null to create user) */
    public static final String INPUT_ID = "id";
    
    
    public static final String INPUT_ACTION = "action";
    
    
    public static final String INPUT_NAME = "name";
    
    
    public static final String INPUT_LOGIN = "login";
    
    
    public static final String INPUT_PASSWORD = "password";
    
    
    public static final String INPUT_GROUPS = "groups";
    
    
    private String name;
    
    
    private String login;
    
    
    private String action;
    
    
    private String password;
    
    
    private Long[] groups;
    
    
    /** 
     * INPUT_ACTION should be equal to one of the following constants.
     * ACTION_LIST is the default value.
     */
    private static final String ACTION_ADD = "add";
    private static final String ACTION_EDIT = "edit";
    private static final String ACTION_EDIT_PASSWORD = "edit_password";
    private static final String ACTION_SAVE = "save";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_LIST = "list";
    
    
    /** User identifier */
    private Long id = null;
    
    
    private Collection errors = new LinkedList();
    
    
    Element elPage = Xbuilder.createTopEl("page");
    Element elContext = Xbuilder.addEl(elPage, "context", null);
    Element elContents = Xbuilder.addEl(elPage, "contents", null);
    
    
    private static Logger logger = Logger.getLogger(ManageContributorsCommand.class);


    private User admin;
    
    
    public ResponseContext execute()
    {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        Map responseMap = response.getResultMap();
        RequestContext request = getRequestContext();
        SessionData session = getRequestContext().getSession();
        admin = session.getUser();
        if(!SecurityGuard.isAdministrator(admin)){
            response.setResultName(RESULT_ACCESS_DENIED);
            return response;
        }
        readParameters(request);
        if(ACTION_ADD.equals(action)) {
            elContents.appendChild(showUser());
        } else if(ACTION_EDIT.equals(action)) {
            elContents.appendChild(showUser());
        } else if(ACTION_EDIT_PASSWORD.equals(action)) {
            elContents.appendChild(editPassword());
        } else if(ACTION_SAVE.equals(action)) {
            if(saveUser()){
                elContents.appendChild(listUsers());
            }else{
                elContents.appendChild(showUser());
            }
        } else if(ACTION_DELETE.equals(action)) {
            deleteUser();
            elContents.appendChild(listUsers());
        }else{
            elContents.appendChild(listUsers());
        }
        Xbuilder.addEl(elContext, "action", action);
        Xbuilder.setAttr(elContext,
                "containers_management_enabled",
                ""+ ManageContainersCommand.MANAGEMENT_ENABLED);
        if(errors.size() > 0){
            Iterator iter = errors.iterator();
            while (iter.hasNext()) {
                String error = (String) iter.next();
                Xbuilder.addEl(elContext, "error", error);
            }
        }
        response.setResultName(RESULT_SUCCESS);
        responseMap.put("xml", elPage.getOwnerDocument());
        logger.debug("-");
        return response;
    }
    
    
    private Element showUser() {
        logger.debug("+");
        Element elUser = Xbuilder.createEl(elPage, "user", null);
        
        if(ACTION_SAVE.equals(action)){
            
//          TODO If we have user input, show it
        }else if(ACTION_EDIT.equals(action)){
            try{
                User user = User.findById(id);
                Xbuilder.setAttr(elUser, "id", id.toString());
                Xbuilder.setAttr(elUser, "login", user.getLogin());
                Xbuilder.setAttr(elUser, "name", user.getName());
                elUser.appendChild(buildGroups(id));
            } catch (Exception e) {
                logger.error("CriticalException", e);
                errors.add("Failed to get list of users");
            }
        }else{ // ACTION_ADD
            elUser.appendChild(buildGroups(null));
            logger.debug("-");
        }
        return elUser;
    }
    
    private Element buildGroups(Long uid){
        logger.debug("+");
        Element elGroups = Xbuilder.createEl(elPage, "groups", null);
        try {
            Iterator iter = User.getMembership(uid, false).iterator();
            while (iter.hasNext()) {
                String[] info = (String[]) iter.next();
                Element elGroup = Xbuilder.addEl(elGroups, "group", null);
                elGroup.setAttribute("id", info[0]);
                elGroup.setAttribute("name", info[1]);
                elGroup.setAttribute("linked", info[2]);
                elGroup.setAttribute("role-id", info[3]);
            }
        } catch (CriticalException e) {
            logger.error("CriticalException", e);
            errors.add("Failed to fetch list of groups");
        }
        logger.debug("-");
        return elGroups;
    }
    
    
    private Element editPassword() {
        logger.debug("+");
        Element elUser = Xbuilder.createEl(elPage, "user", null);
        try{
            User user = User.findById(id);
            Xbuilder.setAttr(elUser, "id", id.toString());
            Xbuilder.setAttr(elUser, "login", user.getLogin());
            Xbuilder.setAttr(elUser, "name", user.getName());
        } catch (Exception e) {
            logger.error("- Exception", e);
            errors.add("Cannot fetch user profile");
        }
        return elUser;
    }


    private void deleteUser() {
        logger.debug("+");
        if(admin.getId().equals(id)){
            logger.debug("cannot delete self");
            errors.add("You cannot delete yourself");
            return;
        }
        try {
            User user = User.findById(id);
            logger.info(
                "User " + user.getLogin() + " id# " + user.getId() +
                " is about to be deleted by " + admin.getLogin());
            user.delete();
            SecurityCache.invalidate();
        } catch (Exception e) {
            logger.error("Exception", e);
            errors.add("Cannot delete the user with id # " + id);
        }
    }


    private Element listUsers() {
        logger.debug("+");
        Collection users;
        Element elUsers = Xbuilder.createEl(elPage, "users", null);
        try {
            users = User.getContributors();
            Iterator iter = users.iterator();
            while (iter.hasNext()) {
                User user = (User) iter.next();
                Element elUser = Xbuilder.addEl(elUsers, "user", null);
                Xbuilder.setAttr(elUser, "id", user.getId().toString());
                Xbuilder.setAttr(elUser, "login", user.getLogin());
                Xbuilder.setAttr(elUser, "name", user.getName());
            }
        } catch (CriticalException e) {
            logger.error("CriticalException", e);
            errors.add("Failed to get list of users");
        }
        logger.debug("-");
        return elUsers;
    }


    private boolean saveUser() {
        logger.debug("+");
        try {
            User user = null;
            if(id != null){
                user = User.findById(id);
                if(password != null){
                    user.setPassword(password);
                    user.update();
                }else{
                    // if user is going to update himself, do not let him
                    // exclude himself from the administrative group
                    if(admin.getId().equals(user.getId())){
                        boolean stillWillBeAdmin = false;
                        for (int i = 0; i < groups.length; i++) {
                            Long gid = groups[i];
                            Group group = new Group();
                            group.load(gid);
                            if("administrator".equals(group.getRoleId())){
                                stillWillBeAdmin = true;
                                break;
                            }
                        }
                        if(!stillWillBeAdmin){
                            errors.add("You cannot exclude yourself " +
                                    " from the administrative group");
                            logger.debug("- must keep administrative rightts");
                            return false;
                        }
                    }
                    logger.info(
                        "User " + user.getLogin() + " id# " + user.getId() +
                        " is about to be updated by " + admin.getLogin());
                    user.setLogin(login);
                    user.setName(name);
                    user.update();
                    user.updateMembership(groups);
                    SecurityCache.invalidate();
                }
            }else{
                user = new User();
                user.setLogin(login);
                user.setName(name);
                user.setType("administrator");
                user.setPassword(password);
                try {
                    user = User.findByLogin(login);
                    errors.add("User with this login already exists");
                    logger.debug("- duplicate login");
                    return false;
                } catch (Exception e) {
                    // User not found, it is OK
                    logger.info(
                        "User " + user.getLogin() + " id# " + user.getId() +
                        " is about to be inserted by " + admin.getLogin());
                    user.insert();
                }
                user.updateMembership(groups);
                SecurityCache.invalidate();
            }
            logger.debug("-");
            return true;
        } catch (Exception e) {
            logger.error("Exception", e);
            errors.add("Failed to update the account");
            SecurityCache.invalidate();
            logger.debug("- security cache is invalidated");
            return false;
        }
    }
    
    
    /** Checks validity of parameters in request context. */
    private void readParameters(RequestContext request) {
        logger.debug("+");
        id = request.getLong(INPUT_ID);
        action = request.getString(INPUT_ACTION, ACTION_LIST);
        if( ACTION_SAVE.equals(action)){
            name = request.getString(INPUT_NAME, "");
            login = request.getString(INPUT_LOGIN, null);
            password = request.getString(INPUT_PASSWORD, null);
            groups = request.getLongs(INPUT_GROUPS);
        }
        logger.debug("-");
    }
    
    
}

