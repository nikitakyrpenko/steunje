/*
 * @(#)ManageContainersCommand.java  Created on 14.06.2004
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
import com.negeso.module.user.domain.DefaultContainer;
import com.negeso.module.user.domain.Group;



/**
 * 
 * @version 1.0
 * @author Stanislav Demchenko
 */

public class ManageContainersCommand extends AbstractCommand {


    /** Id of an existing container (null to create container) */
    public static final String INPUT_ID = "id";
    
    
    public static final String INPUT_ACTION = "action";
    
    
    public static final String INPUT_NAME = "name";
    
    
    public static final String INPUT_GROUPS = "groups";
    
    
    private String name;
    
    
    private String action;
    
    
    private Long[] groups;
    
    
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
    
    
    private Collection<String> errors = new LinkedList<String>();
    
    
    Element elPage = Xbuilder.createTopEl("page");
    Element elContext = Xbuilder.addEl(elPage, "context", null);
    Element elContents = Xbuilder.addEl(elPage, "contents", null);
    
    
    private static Logger logger = Logger.getLogger(ManageContainersCommand.class);


    private User admin;
    
    
    public static final boolean MANAGEMENT_ENABLED =
        Boolean.valueOf(
            Env.getProperty("security.containers.management.enabled", "false"))
                .booleanValue();
    
    
    public ResponseContext execute()
    {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        Map<String, Object> responseMap = response.getResultMap();
        RequestContext request = getRequestContext();
        SessionData session = getRequestContext().getSession();
        admin = session.getUser();
        if(!SecurityGuard.isAdministrator(admin)) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- not an administrator");
            return response;
        }
        if(!MANAGEMENT_ENABLED) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.warn("- container management is disabled");
            return response;
        }
        readParameters(request);
        if(ACTION_ADD.equals(action)) {
            elContents.appendChild(showContainer());
        } else if(ACTION_EDIT.equals(action)) {
            elContents.appendChild(showContainer());
        } else if(ACTION_SAVE.equals(action)) {
            if(save()){
                elContents.appendChild(list());
            }else{
                elContents.appendChild(showContainer());
            }
        } else if(ACTION_DELETE.equals(action)) {
            delete();
            elContents.appendChild(list());
        }else{
            elContents.appendChild(list());
        }
        Xbuilder.addEl(elContext, "action", action);
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
    
    
    private Element showContainer() {
        logger.debug("+");
        Element elContainer = Xbuilder.createEl(elPage, "container", null);
        
        if(ACTION_SAVE.equals(action)){
            
//          TODO If we have user input, show it
        }else if(ACTION_EDIT.equals(action)){
            if(id == null){
                errors.add("Container id is not specified");
                logger.error("- container id is null");
                return elContainer;
            }
            try{
                Container container = new Container();
                container.load(id);
                Xbuilder.setAttr(elContainer, "id", id.toString());
                Xbuilder.setAttr(elContainer, "name", container.getName());
                elContainer.appendChild(buildGroups(container));
            } catch (Exception e) {
                logger.error("CriticalException", e);
                errors.add("Failed to get container data");
            }
        }else{ // ACTION_ADD
            elContainer.appendChild(buildGroups(null));
            logger.debug("-");
        }
        return elContainer;
    }
    
    
    private Element buildGroups(Container container){
        logger.debug("+");
        Element elGroups = Xbuilder.createEl(elPage, "groups", null);
        try {
            if(container != null){
                Iterator iter = container.getGroups().iterator();
                while (iter.hasNext()) {
                    String[] info = (String[]) iter.next();
                    Element elGroup = Xbuilder.addEl(elGroups, "group", null);
                    elGroup.setAttribute("id", info[0]);
                    elGroup.setAttribute("name", info[1]);
                    elGroup.setAttribute("linked", info[2]);
                    elGroup.setAttribute("role-id", info[3]);
                }
            }else{
                Iterator iter = Group.getGroups(Env.getSiteId()).iterator();
                while (iter.hasNext()) {
                    Group group = (Group) iter.next();
                    Element elGroup = Xbuilder.addEl(elGroups, "group", null);
                    elGroup.setAttribute("id", group.getId().toString());
                    elGroup.setAttribute("name", group.getName());
                    elGroup.setAttribute("linked", "false");
                    elGroup.setAttribute("role-id",  group.getRoleId());
                }
            }
        } catch (CriticalException e) {
            logger.error("CriticalException", e);
            errors.add("Failed to fetch list of groups");
        }
        logger.debug("-");
        return elGroups;
    }
    
    
    private void delete() {
        logger.debug("+");
        if(id == null){
            errors.add("Container id is not specified");
            logger.error("- container id is null");
            return;
        }
    	if (id.equals(DefaultContainer.getInstance().getId()))  {
    		errors.add("You can not delete default container");
    		return;
    	}
        try {
            Container container = new Container();
            container.load(id);
            logger.info(
                "Container " + container.getName() +
                " id# " + container.getId() +
                " is about to be deleted by " + admin.getLogin());
            container.delete();
            SecurityCache.invalidate();
        } catch (Exception e) {
            logger.error("Exception", e);
            errors.add("Cannot delete container # " + id + ". Probably some resources refers to it");
        }
        logger.debug("-");
    }


    private Element list() {
        logger.debug("+");
        Element elContainers = Xbuilder.createEl(elPage, "containers", null);
        try {
            Iterator iter = Container.getContainers(Env.getSiteId()).iterator();
            while (iter.hasNext()) {
                Container container = (Container) iter.next();
                Element elContainer =
                    Xbuilder.addEl(elContainers, "container", null);
                Xbuilder.setAttr(elContainer, "id", container.getId().toString());
                Xbuilder.setAttr(elContainer, "name", container.getName());
            }
        } catch (CriticalException e) {
            logger.error("CriticalException", e);
            errors.add("Failed to get list of containers");
        }
        logger.debug("-");
        return elContainers;
    }
    
    
    private boolean save()
    {
        logger.debug("+");
        if("".equals(name) || name == null){
            errors.add("Container name cannot be empty");
            logger.error("- container name is empty");
            return false;
        }
        String err = null;
        try {
            Container container = null;
            if(id != null){
                err = "Failed to update the container";
                container = new Container();
                container.load(id);
                logger.info(
                    "Container " + container.getName() +
                    " id# " + container.getId() +
                    " is about to be updated by " + admin.getLogin());
                container.setName(name);
                container.update();
            }else{
                err = "Failed to create container";
                container = new Container();
                container.setName(name);
                container.setSiteId(Env.getSiteId());
                logger.info(
                    "Container " + container.getName() +
                    " id# " + container.getId() +
                    " is about to be inserted by " + admin.getLogin());
                container.insert();
            }
            container.setGroups(groups);
            SecurityCache.invalidate();
            logger.debug("-");
            return true;
        } catch (Exception e) {
            logger.error("Exception", e);
            errors.add(err);
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
            groups = request.getLongs(INPUT_GROUPS);
        }
        logger.debug("-");
    }
    
    
}
