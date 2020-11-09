/*
 * @(#)$Id: ImageTransformException.java,v 1.0, 2004-07-07 07:33:42Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.image;

/**
 *
 * Basic image transform exception
 * 
 * @version		$Revision: 1$
 * @author		Olexiy Strashko
 * 
 */
public class ImageTransformException extends Exception {

    /**
     * 
     */
    public ImageTransformException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public ImageTransformException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public ImageTransformException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public ImageTransformException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
    }
}
