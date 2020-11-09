/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public interface SpringConstants {

	/**
	 * Guestbook
	 */
	public static final String GUESTBOOK_GUESTBOOK_SERVICE = "guestbookService";
	public static final String GUESTBOOK_MESSAGE_SERVICE = "messageService";
	
	/**
	 * BannerModule
	 */
	public static final String BANNER_MODULE_BANNER_SERVICE = "bannerService";
	public static final String BANNER_MODULE_TYPE_SERVICE = "bannerTypeService";

	/**
	 * Newsletter
	 */
	public static final String NEWSLETTER_MODULE_SUBSCRIBER_SERVICE = "subscriberService";
	public static final String NEWSLETTER_MODULE_LIMITATION_SERVICE = "limitationService";
	public static final String NEWSLETTER_MODULE_MAILING_SERVICE = "mailingService";
	public static final String NEWSLETTER_MODULE_PUBLICATION_SERVICE = "publicationService";

}
