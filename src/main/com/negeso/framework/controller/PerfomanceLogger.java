/*
 * @(#)$Id: PerfomanceLogger.java,v 1.1, 2005-11-22 10:05:47Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.controller;

import org.apache.log4j.Logger;

import com.negeso.framework.command.Command;
import com.negeso.framework.command.GetFileCommand;
import com.negeso.framework.util.Timer;
import com.negeso.framework.view.GetFileView;
import com.negeso.framework.view.View;

/**
 *
 * Weak slass for perfomance logging in WebFrontController
 * 
 * @version		$Revision: 2$
 * @author		Olexiy V. Strashko
 * 
 */
public class PerfomanceLogger {
    private static Logger perfomanceLogger = Logger.getLogger(PerfomanceLogger.class);

    public static void logCommand(Command command, String resultName, Timer timer){
       	if (command instanceof GetFileCommand) {
        	perfomanceLogger.debug(
        		command.getClass().getName() + " execution time: " +
                timer + "result: " + resultName 
            );
       	} else {
        	perfomanceLogger.info(
        		command.getClass().getName() + " execution time: " +
                timer + "result: " + resultName 
            );
       	}
    }
    
    public static void logView(View view, Timer timer){
       	if (view instanceof GetFileView) {
        	perfomanceLogger.debug(
        		view.getClass().getName() + " execution time: " + timer  
            );
       	} else {
        	perfomanceLogger.info(
        		view.getClass().getName() + " execution time: " + timer 
            );
       	}
    }
}
