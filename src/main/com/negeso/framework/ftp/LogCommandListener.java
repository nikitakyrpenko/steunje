/*
 * @(#)$Id: LogCommandListener.java,v 1.0, 2007-03-13 12:05:12Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.ftp;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.log4j.Logger;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 1$
 *
 */
public class LogCommandListener implements ProtocolCommandListener {

    static Logger logger = Logger.getLogger(LogCommandListener.class); 
	
    public void protocolCommandSent(ProtocolCommandEvent event)
    {
    	logger.info(event.getMessage());
    }

    public void protocolReplyReceived(ProtocolCommandEvent event)
    {
    	logger.info(event.getMessage());
    }

}
