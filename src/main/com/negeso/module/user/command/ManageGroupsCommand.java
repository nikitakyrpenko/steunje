/*
 * @(#)ManageGroupsCommand.java  Created on 16.06.2004
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
import com.negeso.module.user.domain.Container;
import com.negeso.module.user.domain.Group;
import com.negeso.module.user.domain.Role;

/**
 * 
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class ManageGroupsCommand extends AbstractCommand {


    /** Id of an existing user (null to create user) */
    public static final String INPUT_ID = "id";
    
    
    public static final String INPUT_ACTION = "action";
    
    
    public static final String INPUT_NAME = "name";
    
    
    public static final String INPUT_CONTAINERS = "containers";
    
    
    public static final String INPUT_USERS = "users";
    
    
    public static final String INPUT_ROLE = "role";
    
    
    private String name;
    
    
    private String action;
    
    
    private Long[] containers;
    
    
    private Long[] users;
    
    
    private String role;
    
    
    /** 
     * INPUT_ACTION should be equal to one of the following constants.
     * ACTION_LIST is the default value.
     */
    private static final String ACTION_ADD = "add";
    private static final String ACTION_EDIT = "edit";
    private static final String ACTION_SAVE = "save";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_LIST = "list";
    
    
    /** User identifier */
    private Long id = null;
    
    
    private Collection errors = new LinkedList();
    
    
    Element elPage = Xbuilder.createTopEl("page");
    Element elContext = Xbuilder.addEl(elPage, "context", null);
    Element elContents = Xbuilder.addEl(elPage, "contents", null);
    
    
    private static Logger logger = Logger.getLogger(ManageGroupsCommand.class);


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
            logger.warn("- Access denied");
            return response;
        }
        id = request.getLong(INPUT_ID);
        action = request.getString(INPUT_ACTION, ACTION_LIST);
        if(ACTION_ADD.equals(action)) {
            elContents.appendChild(buildItem());
        } else if(ACTION_EDIT.equals(action)) {
            elContents.appendChild(buildItem());
        } else if(ACTION_SAVE.equals(action)) {
            name = request.getString(INPUT_NAME, "");
            containers = request.getLongs(INPUT_CONTAINERS);
            users = request.getLongs(INPUT_USERS);
            role = request.getString(INPUT_ROLE, "visitor");
            if(save()){
                elContents.appendChild(buildList());
            }else{
                elContents.appendChild(buildItem());
            }
        } else if(ACTION_DELETE.equals(action)) {
            delete();
            elContents.appendChild(buildList());
        }else{
            elContents.appendChild(buildList());
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
    
    
    private Element buildItem() {
        logger.debug("+");
        Element elGroup = Xbuilder.createEl(elPage, "group", null);
        if(ACTION_SAVE.equals(action)){
//          TODO If we have user input, show it
        }else if(ACTION_EDIT.equals(action)){
            if(id == null){
                errors.add("Group id is not specified");
                logger.error("- group id is null");
                return elGroup;
            }
            try {
                Group group = new Group();
                group.load(id);
                Xbuilder.setAttr(elGroup, "id", id.toString());
                Xbuilder.setAttr(elGroup, "name", group.getName());
                Xbuilder.setAttr(elGroup, "role-id", group.getRoleId());
                elGroup.appendChild(buildContainers(group));
                elGroup.appendChild(buildUsers(group));
                elGroup.appendChild(buildRoles());
            } catch (Exception e) {
                logger.error("CriticalException", e);
                errors.add("Failed to get group data");
            }
        }else{ // ACTION_ADD
            elGroup.appendChild(buildContainers(null));
            elGroup.appendChild(buildUsers(null));
            elGroup.appendChild(buildRoles());
            logger.debug("-");
        }
        return elGroup;
    }
    
    
    private Element buildContainers(Group group){
        logger.debug("+");
        Element elContainers = Xbuilder.createEl(elPage, "containers", null);
        try {
            if(group != null){
                Iterator iter = group.getContainers().iterator();
                while (iter.hasNext()) {
                    String[] info = (String[]) iter.next();
                    Element elContainer = Xbuilder.addEl(elContainers, "container", null);
                    elContainer.setAttribute("id", info[0]);
                    elContainer.setAttribute("name", info[1]);
                    elContainer.setAttribute("linked", info[2]);
                }
            }else{
                Iterator iter = Container.getContainers(Env.getSiteId()).iterator();
                while (iter.hasNext()) {
                    Container container = (Container) iter.next();
                    Element elGroup = Xbuilder.addEl(elContainers, "container", null);
                    elGroup.setAttribute("id", container.getId().toString());
                    elGroup.setAttribute("name", container.getName());
                    elGroup.setAttribute("linked", "false");
                }
            }
        } catch (CriticalException e) {
            logger.error("CriticalException", e);
            errors.add("Failed to fetch list of containers");
        }
        logger.debug("-");
        return elContainers;
    }
    
    
    private Element buildUsers(Group group){
        logger.debug("+");
        Element elUsers = Xbuilder.createEl(elPage, "users", null);
        try {
            if(group != null){
                Iterator iter = group.getContributors().iterator();
                while (iter.hasNext()) {
                    String[] info = (String[]) iter.next();
                    Element elUser = Xbuilder.addEl(elUsers, "user", null);
                    elUser.setAttribute("id", info[0]);
                    elUser.setAttribute("name", info[1]);
                    elUser.setAttribute("linked", info[2]);
                }
            }else{
                Iterator iter = User.getContributors().iterator();
                while (iter.hasNext()) {
                    User user = (User) iter.next();
                    Element elGroup = Xbuilder.addEl(elUsers, "user", null);
                    elGroup.setAttribute("id", user.getId().toString());
                    elGroup.setAttribute("name", user.getName());
                    elGroup.setAttribute("linked", "false");
                }
            }
        } catch (CriticalException e) {
            logger.error("CriticalException", e);
            errors.add("Failed to fetch list of users");
        }
        logger.debug("-");
        return elUsers;
    }
    
    
    private Element buildRoles(){
        logger.debug("+");
        Element elRoles = Xbuilder.createEl(elPage, "roles", null);
        try {
            Iterator iter = Role.getRoles().iterator();
            while (iter.hasNext()) {
                Role role = (Role) iter.next();
                Element elRole = Xbuilder.addEl(elRoles, "role", null);
                elRole.setAttribute("id", role.getId());
                elRole.setAttribute("name", role.getName());
            }
        } catch (CriticalException e) {
            logger.error("CriticalException", e);
            errors.add("Failed to fetch list of roles");
        }
        logger.debug("-");
        return elRoles;
    }
    
    
    private void delete() {
        logger.debug("+");
        if(id == null){
            errors.add("Group id is not specified");
            logger.error("- Group id is null");
            return;
        }
        try {
            Group group = new Group();
            group.load(id);
            logger.info(
                "Group " + group.getName() + " id# " + group.getId() +
                " is about to be deleted by " + admin.getLogin());
            group.delete();
            SecurityCache.invalidate();
        } catch (Exception e) {
            logger.error("Exception", e);
            errors.add("Cannot delete group # " + id);
        }
        logger.debug("-");
    }


    private Element buildList() {
        logger.debug("+");
        Element elGroups = Xbuilder.createEl(elPage, "groups", null);
        try {
            Iterator iter = Group.getGroups(Env.getSiteId()).iterator();
            while (iter.hasNext()) {
                Group group = (Group) iter.next();
                Element elGroup =
                    Xbuilder.addEl(elGroups, "group", null);
                Xbuilder.setAttr(elGroup, "id", group.getId().toString());
                Xbuilder.setAttr(elGroup, "name", group.getName());
                Xbuilder.setAttr(elGroup, "role-id", group.getRoleId());
            }
        } catch (CriticalException e) {
            logger.error("CriticalException", e);
            errors.add("Failed to get list of groups");
        }
        logger.debug("-");
        return elGroups;
    }
    
    
    private boolean save() {
        logger.debug("+");
        if("".equals(name) || name == null){
            errors.add("Group name cannot be empty");
            logger.error("- group name is empty");
            return false;
        }
        String err = null;
        try {
            Group group = null;
            if(id != null){
                err = "Failed to update the group";
                group = new Group();
                group.load(id);
                boolean stillWillBeAdmin = false;
                if( "administrator".equals(group.getRoleId()) ) {
                    for (int i = 0; i < users.length; i++) {
                        if(admin.getId().equals(users[i])){
                            stillWillBeAdmin = true;
                            break;
                        }
                    }
                    if(!stillWillBeAdmin){
                        errors.add("You cannot exclude yourself " +
                                " from the administrative group");
                        logger.debug("- must keep administrative rights");
                        return false;
                    }
                }
                logger.info(
                    "Group " + group.getName() + " id# " + group.getId() +
                    " is about to be updated by " + admin.getLogin());
                group.setName(name);
                group.update();
            }else{
                err = "Failed to create group";
                group = new Group();
                group.setName(name);
                group.setRoleId(role);
                group.setSiteId(Env.getSiteId());
                logger.info(
                    "Group " + group.getName() + " id# " + group.getId() +
                    " is about to be updated by " + admin.getLogin());
                group.insert();
            }
            group.setContainers(containers);
            group.setContributors(users);
            SecurityCache.invalidate();
            logger.debug("-");
            return true;
        } catch (Exception e) {
            logger.error("Exception", e);
            errors.add(err);
            SecurityCache.invalidate();
            logger.error("- failed to update group");
            return false;
        }
    }
    
    
}
