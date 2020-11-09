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
package com.negeso.module.translator.service;

import com.negeso.framework.Env;
import com.negeso.framework.domain.Language;
import com.negeso.framework.mailer.MailClient;
import org.apache.log4j.Logger;

/**
 * 
 * @TODO
 * 
 * @author		Mykola Lyhozhon
 * @version		$Revision: $
 *
 */
public class LanguageGrammarCheckRequestNotifier {
	
	private static Logger logger = Logger.getLogger(LanguageGrammarCheckRequestNotifier.class);
	
	private static final String DEFAULT_TEXT = "Admin of site %s asked to check and update grammar and spelling errors for next languages: ";
	private static final String DEFAULT_SUBJECT = "Grammar and spelling check request";
	
	public void notify(String siteUrl, long[] langIds) {
		try {
			if (langIds == null || langIds.length == 0) {
				return;
			}
			MailClient mailer = new MailClient();
			
			StringBuffer body = new StringBuffer(String.format(DEFAULT_TEXT, siteUrl));
			for (long langId : langIds) {
				Language language = Language.findById(langId);
				body.append("<br/>").append(language.getLanguage());
			}
			
			mailer.sendHTMLMessage(
					"support@negeso.com",
					Env.getDefaultEmail(),
					DEFAULT_SUBJECT,
					body.toString()
			);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}

