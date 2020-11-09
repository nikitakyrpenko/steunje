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

package com.negeso.module.webpoll.command;


import org.apache.log4j.Logger;

import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.list.command.CreateListItemCommand;

/**
 * @author Sergiy Oliynyk
 */
public class CreatePollCommand extends CreateListItemCommand {

    private static Logger logger =
        Logger.getLogger(CreatePollCommand.class);

    public ResponseContext execute() {
        logger.debug("+ -");
        return super.execute();
    }
}
