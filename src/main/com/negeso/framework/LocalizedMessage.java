/*
 * @(#)$Id: LocalizedMessage.java,v 1.7, 2005-06-06 13:04:44Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework;

import java.sql.Connection;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.module.dictionary.DictionaryUtil;

/**
 *
 * Localized message
 * 
 * @version		$Revision: 8$
 * @author		Olexiy Strashko
 * 
 */
public class LocalizedMessage {
	private static Logger logger = Logger.getLogger( LocalizedMessage.class );

	private String message = null;

    public LocalizedMessage(String messageId) {
        super();
        this.message = messageId;
    }

    public LocalizedMessage() {
    	super();
	}

	public String getMessage(String languageCode){
        logger.debug("+");
        Connection conn = null;
        String localized = null;
        try {
            conn = DBHelper.getConnection();
            localized = DictionaryUtil.findEntry(conn, this.message, 
                languageCode);
            if (localized == null)
                logger.error( "I18N Constant: " + this.message + " not found");
        }
        catch (Exception ex) {
            logger.error("Dictionary error: ", ex);
        }
        finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
        return localized;
    }

    public String getMessage(int languageId){
        String languageCode = Env.getDefaultLanguageCode();
        try {
            languageCode = Language.findById(new Long(languageId)).getCode();
        }
        catch (Exception ex) {
            logger.error("Language not found ", ex);
        }
        return this.getMessage(languageCode);
    }
    
    /**
     * @param string
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
