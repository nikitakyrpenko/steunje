/*
 * @(#)$Id: CreatePollCommand.java,v 1.3, 2005-12-05 10:30:51Z, Svetlana Bondar$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.webpoll.command.tln;

import java.sql.Connection;

import org.apache.log4j.Logger;


import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.list.command.CreateListItemCommand;
import com.negeso.framework.list.command.GetListItemCommand;
import com.negeso.framework.list.domain.List;
import com.negeso.framework.list.domain.ListItem;
import com.negeso.module.webpoll.domain.PollOption;

/**
 * @author Sergiy Oliynyk
 */
public class CreatePollCommand extends CreateListItemCommand {

    private static Logger logger =
        Logger.getLogger(CreatePollCommand.class);

    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = super.execute();
        if (!RESULT_SUCCESS.equals(response.getResultName())) {
            logger.error("- Cannot create list item");
            return response;
        }
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            Long pollListItemId = request.getLong(
                GetListItemCommand.INPUT_LIST_ITEM_ID);
            List pollList = List.getByParentListItemId(conn, pollListItemId);
            ListItem optionListItem = new ListItem();
            optionListItem.setListId(pollList.getId());
            PollOption option = new PollOption();
            option.setPollId(pollListItemId);
            optionListItem.setTitle("Neutraal");
            optionListItem.insert(conn);
            option.setListItemId(optionListItem.getId());
            option.insert(conn);
            optionListItem.setTitle("Nee");
            optionListItem.insert(conn);
            option.setListItemId(optionListItem.getId());
            option.insert(conn);
            optionListItem.setTitle("Ja");
            optionListItem.insert(conn);
            option.setListItemId(optionListItem.getId());
            option.insert(conn);
            conn.commit();
            logger.debug("-");
        }
        catch (Exception ex) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        return response;
    }
}
