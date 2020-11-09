/*
 * @(#)$Id: CreatePollOptionCommand.java,v 1.9, 2005-12-05 10:30:51Z, Svetlana Bondar$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.webpoll.command;

import java.sql.Connection;

import org.apache.log4j.Logger;


import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.list.command.CreateListItemCommand;
import com.negeso.framework.list.command.GetListItemCommand;
import com.negeso.module.webpoll.domain.PollOption;

/**
 * @author Sergiy Oliynyk
 */
public class CreatePollOptionCommand extends CreateListItemCommand {

    private static Logger logger =
        Logger.getLogger(CreatePollOptionCommand.class);

    public ResponseContext execute() {
        logger.debug("+");
        RequestContext request = getRequestContext();
        ResponseContext response = super.execute();
        if (!RESULT_SUCCESS.equals(response.getResultName()))
            return response;
        PollOption option = new PollOption();
        option.setListItemId(request.getLong(
           GetListItemCommand.INPUT_LIST_ITEM_ID));
        option.setPollId(request.getLong(GetListItemCommand.INPUT_LIST_PATH));
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            option.insert(conn);
        }
        catch (Exception ex) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request ", ex);
            DBHelper.rollback(conn);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return response;
    }
}
