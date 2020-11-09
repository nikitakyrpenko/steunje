/*
 * @(#)Orderable.java       @version	09.06.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

import java.sql.Connection;

/**
 * <code>Ordarable</code> interface implemetation mean that object can be reordered 
 * and supports operations:
 * 	- 	moveDown
 * 	-	moveUp
 * Working goes through database, so <code>Connection</code> can be
 * passed in all methods.
 * 
 * @see 		OrderControlHelper
 *
 * @version 	09.06.2004
 * @author 		Olexiy V. Strashko
 */
public interface Orderable {
	
	/**
	 * Move <code>Orderable<code> object up in order.
	 * 
	 * @param con	The <code>Connection</codew>
	 * @return		The new order number of the object 
	 * @throws CriticalException
	 */
	public Long moveUp(Connection con) throws CriticalException;
	
	/**
	 * Move <code>Orderable<code> object down in order.
	 * 
	 * @param con	The <code>Connection</codew>
	 * @return		The new order number of the object 
	 * @throws CriticalException
	 */
	public Long moveDown(Connection con) throws CriticalException;
}
