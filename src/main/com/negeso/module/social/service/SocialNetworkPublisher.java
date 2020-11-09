/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.social.service;

import com.negeso.module.social.SocialNetworkException;
import com.negeso.module.social.bo.SocialNetwork;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public interface SocialNetworkPublisher {
	
	public void publish(String title, String text, String url, String imageUrl, SocialNetwork socialNetwork) throws SocialNetworkException;
	
}

