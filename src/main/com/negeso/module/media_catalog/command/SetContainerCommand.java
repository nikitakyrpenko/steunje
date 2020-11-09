/*
 * @(#)$Id: SetContainerCommand.java,v 1.2, 2005-06-06 13:05:07Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.command;


import org.apache.log4j.Logger;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.module.media_catalog.Repository;

/**
 *
 * SetContainerCommand for setting container 
 * 
 * @version		$Revision: 3$
 * @author		Olexiy Strashko
 * 
 */
public class SetContainerCommand extends AbstractCommand {
    private static Logger logger = Logger.getLogger(SetContainerCommand.class);
    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */
    public ResponseContext execute() {
        logger.debug("+");
        Repository.get().getCache().clearFolderDomainCache();
        AbstractCommand delegate = new com.negeso.module.user.command.SetContainerCommand();
        delegate.setRequestContext(this.getRequestContext());
        logger.debug("-");
        return delegate.execute();
    }

}
