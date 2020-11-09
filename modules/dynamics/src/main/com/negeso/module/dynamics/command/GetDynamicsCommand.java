/*
 * @(#)$Id: GetDynamicsCommand.java,v 1.13, 2005-06-06 13:04:32Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.dynamics.command;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.dynamics.DynamicsForm;
import com.negeso.module.dynamics.generators.DynamicsXmlBuilder;
import com.negeso.module.dynamics.generators.ModulesXmlBuilder;
import com.negeso.module.dynamics.generators.NewsXmlBuilder;
import com.negeso.module.dynamics.generators.PagesXmlBuilder;
import com.negeso.module.dynamics.generators.ProductsXmlBuilder;


/**
* 
* @version      $Revision: 14$
* @author       Stanislav Demchenko
* 
*/
public class GetDynamicsCommand extends AbstractCommand {
    
    
    private Logger logger = Logger.getLogger(getClass());
    
    
    Map generators = new HashMap(); 
    
    
    private DynamicsForm form;


    private Element model;


    private RequestContext req;
    
    
    public ResponseContext execute()
    {
        logger.debug("+");
        generators.put("pages", new PagesXmlBuilder());
        generators.put("news", new NewsXmlBuilder());
        generators.put("modules", new ModulesXmlBuilder());
        generators.put("products", new ProductsXmlBuilder());
        req = getRequestContext();
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_ACCESS_DENIED);
        SessionData session = req.getSession();
        if(!SecurityGuard.isContributor(session.getUser())) {
            logger.debug("- Access denied");
            return response;
        }
        form = (DynamicsForm) session.getAttribute("dynamics-form");
        if(form == null) {
            form = new DynamicsForm();
            session.setAttribute("dynamics-form", form);
        }
        updateFormBean();
        DynamicsXmlBuilder builder =
            (DynamicsXmlBuilder) generators.get(form.getType());
        if(builder != null ) {
            Document doc = XmlHelper.newDocument();
            model = buildResults(session, builder, doc);
            doc.appendChild(model);
        } else {
            model = Xbuilder.createTopEl("dynamics");
        }
        addFormToModel();
        response.setResultName(RESULT_SUCCESS);
        response.getResultMap().put(OUTPUT_XML, model.getOwnerDocument());
        logger.debug("-");
        return response;
    }
    
    
    private void updateFormBean()
    {
        logger.debug("+");
        if(req.getString("type", null) != null)
            form.setType(req.getString("type", null));
        if(req.getString("order", null) != null)
            form.setOrder(req.getString("order", null));
        if(req.getString("style", null) != null)
            form.setStyle(req.getString("style", null));
        if(req.getString("startDate", null) != null)
            form.setStartDate(req.getString("startDate", null));
        if(req.getString("endDate", null) != null)
            form.setEndDate(req.getString("endDate", null));
        if(req.getString("pageNumber", null) != null)
            form.setPageNumber((int) req.getLongValue("pageNumber"));
        if(req.getString("paging", null) != null)
            form.setPaging((int) req.getLongValue("paging"));
        if(req.getString("filter", null) != null)
            form.setFilter((int) req.getLongValue("filter"));
        logger.debug("-");
    }
    
    
    private void addFormToModel()
    {
        logger.debug("+");
        model.setAttribute("startDate", form.getStartDate());
        model.setAttribute("endDate", form.getEndDate());
        model.setAttribute("style", form.getStyle());
        model.setAttribute("type", form.getType());
        model.setAttribute("order", form.getOrder());
        model.setAttribute("pageNumber", "" + form.getPageNumber());
        model.setAttribute("paging", "" + form.getPaging());
        model.setAttribute("filter", "" + form.getFilter());
        logger.debug("-");
    }
    
    
    private Element buildResults(
        SessionData session,
        DynamicsXmlBuilder builder,
        Document doc)
    {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            logger.debug("-");
            return builder.getElement(conn, doc, form, session.getLanguage());
        } catch (SQLException e) {
            logger.error("- SQLException", e);
            return null;
        } finally {
            DBHelper.close(conn);
        }
    }
    
    
}
