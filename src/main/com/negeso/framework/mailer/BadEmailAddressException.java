/*
 * @(#)$Id: BadEmailAddressException.java,v 1.0, 2005-04-07 15:21:19Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.mailer;

/**
 *
 * Bad email sddress exception
 * 
 * @version		$Revision: 1$
 * @author		Olexiy Strashko
 * 
 */
public class BadEmailAddressException extends Exception {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public BadEmailAddressException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public BadEmailAddressException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public BadEmailAddressException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public BadEmailAddressException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
