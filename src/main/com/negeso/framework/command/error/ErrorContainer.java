/*
 * @(#)$Id: $
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.command.error;

import java.util.Collection;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: $
 *
 */
public interface ErrorContainer {

	public Collection<String> getErrors();
	public boolean hasErrors();

}

