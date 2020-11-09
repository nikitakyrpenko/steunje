/*
 * @(#)$Revision: 6$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.security;

import com.negeso.framework.LocalizedException;

/**
 * Access denied exception.  
 *
 * @version 	$Revision: 6$
 * @author 		Olexiy.Strashko
 */

public class AccessDeniedException extends LocalizedException {

    private static final String STANDARD_MESSAGE = "OPERATION_ACCESS_DENIED"; 
    
    /**
     * 
     * @return
     * @deprecated
     */
    public static AccessDeniedException newInstance(){
        return new AccessDeniedException(STANDARD_MESSAGE);
    }
    
	/**
	 * @param string
	 */
	public AccessDeniedException() {
		super(STANDARD_MESSAGE);
	}
	
    public AccessDeniedException( String message) {
        super(message );
    }
}
