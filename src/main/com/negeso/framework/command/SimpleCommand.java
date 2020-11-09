/*
 * @(#)SimpleCommand.java  Created on 04.02.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.command;


import org.apache.log4j.Logger;

import com.negeso.framework.controller.ResponseContext;


/**
 * Always returns result AbstractCommand#RESULT_SUCCESS.
 * Can be used with a View which does the real job. 
 * 
 * @version     $Revision: 6$
 * @author      Stanislav Demchenko
 */

public class SimpleCommand extends AbstractCommand {
    
    private static Logger logger = Logger.getLogger(SimpleCommand.class);

    public ResponseContext execute() {
        logger.debug("+ -");
        ResponseContext response = new ResponseContext();
        response.setResultName(RESULT_SUCCESS);
        return response;
    }

}
