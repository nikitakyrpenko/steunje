/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.parametrized_link;

import java.util.List;

import com.negeso.framework.Env;
import com.negeso.module.parametrized_link.domain.ParametrizedLink;

/**
 * 
 * @TODO
 * 
 * @author		Pochapskiy Olexandr
 * @version		$Revision: $
 *
 */
public interface IParametrizedLinkService {
	
	public List<ParametrizedLink> getParametrizedLinks(long langId); 
	public void deleteLink(String linkId); 
	public void addParametrizedLink(ParametrizedLink parametrizedLink);
	public void updateParametrizedLink(ParametrizedLink parametrizedLink); 
	public ParametrizedLink getParametrizedLink(long linkId);
	
}

