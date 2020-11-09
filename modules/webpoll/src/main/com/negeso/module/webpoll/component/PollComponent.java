/*
 * @(#)$Id: PollComponent.java,v 1.13, 2005-12-05 10:30:59Z, Svetlana Bondar$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.webpoll.component;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.list.component.ListComponent;
import com.negeso.framework.list.generators.ListXmlBuilder;
import com.negeso.module.webpoll.domain.Voter;
import com.negeso.module.webpoll.generators.PollXmlBuilder;

/**
 * @author Sergiy Oliynyk
 */
public class PollComponent extends ListComponent {

    private static Logger logger = Logger.getLogger(PollComponent.class);

    public static final String INPUT_POLL_ID = "pollId";
    
    public Element getElement(Document document, 
        RequestContext request, Map parameters)
    {
        logger.debug("+");
        Connection conn = null;
        try {
            Long pollId = request.getLong("pollId");
            String[] listPath = new String[1];
            listPath[0] = pollId != null ? pollId.toString() : "0"; 
            parameters.put("listPath", listPath);
            Element element = super.getElement(document, request, parameters);
            if (pollId == null)
                pollId = Long.valueOf(element.getAttribute("listPath"));
            conn = DBHelper.getConnection();
            String address = request.getRemoteAddr();
            Voter voter = Voter.findByListItemIdAndAddress(conn, pollId, 
                address);
            if (voter != null && !voter.canVote())
                element.setAttribute("disabled", "true");
            return element;
        }
        catch (Exception ex) {
            logger.error("Cannot process request", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return getStubElement(document);
    }

    protected ListXmlBuilder getListXmlBuilder() {
        return PollXmlBuilder.getInstance();
    }
}
