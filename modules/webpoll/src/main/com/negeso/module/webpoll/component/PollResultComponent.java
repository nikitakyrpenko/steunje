/*
 * @(#)$Id: PollResultComponent.java,v 1.13, 2005-12-05 10:30:59Z, Svetlana Bondar$
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.list.component.ListComponent;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.framework.list.generators.ListXmlBuilder;
import com.negeso.module.webpoll.domain.PollOption;
import com.negeso.module.webpoll.domain.Voter;
import com.negeso.module.webpoll.generators.PollResultXmlBuilder;

/**
 * @author Sergiy Oliynyk
 */
public class PollResultComponent extends ListComponent {
    private static Logger logger = Logger.getLogger(PollComponent.class);

    public static final String INPUT_LIST_ITEM_ID = "optionId";
    public static final String INPUT_POLL_RESULT_ID = "pollResultId";
    
    public Element getElement(Document document, 
        RequestContext request, Map parameters)
    {
        logger.debug("+");
        boolean accepted = true;
        try {
            Long pollId = request.getLong(INPUT_LIST_ITEM_ID); 
            if (pollId != null)
                accepted = makeVote(pollId, request.getRemoteAddr());
            Long pollResultId = request.getLong(INPUT_POLL_RESULT_ID);
            String[] listPath = new String[1];
            listPath[0] = pollResultId != null ? pollResultId.toString() : "0"; 
            parameters.put("listPath", listPath);
            Element element = super.getElement(document, request, parameters);
            element.setAttribute("show", String.valueOf(pollResultId != null));
            if (pollId != null)
                element.setAttribute("accepted", String.valueOf(accepted));
            logger.debug("-");
            return element;
        }
        catch (Exception ex) {
            logger.error("- Cannot process request", ex);
        }
        return getStubElement(document);
    }

    private boolean makeVote(Long listItemId, String address)
        throws CriticalException
    {
        logger.debug("+");
        Connection conn = null;
        boolean voted = false;
        try {
            conn = DBHelper.getConnection();
            ListItem listItem = ListItem.getById(conn, listItemId);
            List pollList = List.getById(conn, listItem.getListId());
            Long pollListItemId = pollList.getParentListItemId();
            Voter voter = Voter.findByListItemIdAndAddress(conn, pollListItemId,
                address);
            if (voter == null) {
                voter = new Voter(null, pollListItemId, address, null);
                voter.insert(conn);
            }
            if (voter.canVote()) {
                voter.setVoteTime(new Timestamp(System.currentTimeMillis()));
                voter.update(conn);
                PollOption option = PollOption.findByListItemId(conn, 
                    listItemId);
                if (option != null) {
                    option.setCounter(option.getCounter()+1);
                    option.update(conn);    
                }
                voted = true;
            }
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return voted;
    }

    protected ListXmlBuilder getListXmlBuilder() {
        return PollResultXmlBuilder.getInstance();
    }
}
