/*
 * @(#)$Id: NullCondition.java,v 1.0, 2007-03-19 09:32:26Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.condition.conditions;

import com.negeso.framework.condition.Conditionable;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public class NullCondition extends Condition {

	public NullCondition(Conditionable context) {
		super(context);
	}

	@Override
	protected boolean doCheckCondition() {
		return true;
	}

}

