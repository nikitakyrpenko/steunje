/*
 * @(#)$Id: LocalizedException.java,v 1.3, 2005-01-20 09:52:39Z, Sergey Oleynik$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework;

/**
 *
 * TODO Type description here
 * 
 * @version		$Revision: 4$
 * @author		Olexiy Strashko
 * 
 */
public class LocalizedException extends Exception {
    LocalizedMessage localizedMessage = null;

    /**
     * 
     */
    public LocalizedException() {
        super();
    }

    /**
     * @param message
     */
    public LocalizedException(String message) {
        super(message);
        this.localizedMessage = new LocalizedMessage(message);
    }

    public String getMessage(String languageCode){
        return this.localizedMessage.getMessage(languageCode);
    }

    public String getMessage(int languageId){
        return this.localizedMessage.getMessage(languageId);
    }
}
