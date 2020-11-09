/*
 * @(#)$Id: Configuration.java,v 1.13, 2007-03-22 17:58:56Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.newsletter;
import com.negeso.framework.Env;

/**
 *
 * Newsletter configuration 
 * 
 * @version		$Revision: 14$
 * @author		Olexiy Strashko
 * 
 */
public class Configuration {
	
	public enum EmailState {

		SENT("sent"),
		NOT_SENT("not_sent"),
		BOUNCED("bounced"),
		READ("read");
		
		private String name;
		
		EmailState(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
    private static final String TEMPLATE_NEWSLETTER_CONFIRMATION_XSL = "/template/newsletter_confirmation.xsl";
    
	public static final String SIMPLE_VIEW = "nl_simple_view";
	public static final String RESULT_VIEW = "nl_result_view";
	
    public static final String PUBLICATION_STATUS_CREATED = "created";
	public static final String PUBLICATION_STATUS_SCHEDULED = "scheduled";
	public static final String PUBLICATION_STATUS_SENT = "sent";
    public static final String PUBLICATION_STATUS_SUSPENDED = "suspended";

    public static final String IMPORT_SUBSCRIBERS_KEY = "subscribers";
    public static final String IMPORT_TYPE = "import_type";
    public static final String ATTRIBUTE_EMAIL = "EMAIL";
    public static final String DELIMETER = "delimeter";
    public static final String IMPORT_TYPE_UPDATE = "add";
    public static final String IMPORT_TYPE_DELETE_ADD = "delete_add";
    public static final String IMPORT_TYPE_SINGLE = "single";
    public static final String SUBSCRIPTION_LANG = "subscription_language";
    public static final String IMPORT_GROUPS = "group_ids";
    public static final String ERROR_MESSAGE = "error_message";
    public static final String MAIL_CONTENT = "mail_content";
    public static final String HTML_CONTENT_TYPE = "text/html";
    public static final String PLAIN_CONTENT_TYPE = "text/plain";
    public static final String IMAP_STORE = "imaps";
    
    public static final String READ_NOTIFICATION_PARAM = "read_notification";
    
    private static final String NEWSLETTERS_BCC_EMAIL = "newsletter.bcc.mail";
    private static final String NEWSLETTERS_NOTIFY_onSUBSCRIBE = "newsletter.notifyBcc.subscribe"; 
    private static final String NEWSLETTERS_NOTIFY_onUNSUBSCRIBE = "newsletter.notifyBcc.unsubscribe"; 

    
    private static final String NEWSLETTERS_ADMIN_NOTIFICATION = "newsletter.adminNotification";
    private static final String NEWSLETTERS_MAX_SUBSCRIBERS_COUNT = "newsletter.max.subscriber.number";
    private static final String NEWSLETTERS_MAX_LETTERS_COUNT = "newsletter.maxLettersCount";
    private static final String NEWSLETTERS_ERROR_LETTER_SUBJECT = "newsletter.errorLetterSubject";
    private static final String NEWSLETTERS_ERROR_LETTER_BODY = "newsletter.errorLetterBody";
    private static final String NEWSLETTERS_IMPORT_PER_MONTH_LIMIT = "newsletter.importPerMonthLimit";
    public static final String NEWSLETTERS_IMPORT_MONTH_COUNTER = "newsletter.importMonthCounter";
    private static final String NEWSLETTERS_MAX_LETTERS_ERROR_SUBJECT = "newsletter.errorMaxLettersSubject";
    private static final String NEWSLETTERS_MAX_LETTERS_ERROR_BODY = "newsletter.errorMaxLettersBody";
    
    private static final String NEWSLETTERS_SPECIAL_PAGE_NAME_PATTERN = "newsletter.getSpecialPageNamePattern";
    private static final String NEWSLETTERS_IS_SPECIAL_PAGES_ENABLED = "newsletter.isSpecialPagesEnabled";
    private static final String NEWSLETTERS_IMAP_HOST = "newsletter.imap_host";
    private static final String NEWSLETTERS_IMAP_USER = "newsletter.imap_user";
    private static final String NEWSLETTERS_IMAP_PASS = "newsletter.imap_pass";
    private static final String NEWSLETTERS_IMAP_PORT = "newsletter.imap_port";
    private static final String NEWSLETTERS_IMAP_FOLDER = "newsletter.imap_folder";
    private static final String NEWSLETTERS_BOUNCE_CHECK_PATTERN = "newsletter.bounce_check_pattern";
    private static final String NEWSLETTERS_MAIL_DAEMON = "newsletter.mail_daimon";
    private static final String NEWSLETTERS_MAIL_DELIVERY_FAILED_SUBJ = "newsletter.mail_delivery_failed_subj";
    private static final String NEWSLETTERS_ADMIN_MAIL = "newsletter.admin.mail";
    private static final String NEWSLETTERS_PAGING_ITEMS_PER_PAGE = "newsletter.paging.itemsperpage";
    private static final String NEWSLETTERS_PAGING_PORTION_SIZE = "newsletter.paging.portionsize";
    private static final String NEWSLETTERS_EMAIL_RETURN_PATH = "newsletter.emailReturnPath";
    private static final String NEWSLETTERS_BOUNCE_MAIL_DELETE = "newsletter.bounce.mail.delete";
    private static final String NEWSLETTERS_MAX_GROUP_NUMBER = "newsletter.max.group.number";
    private static final String NEWSLETTERS_IS_SENDING_ON = "newsletter.isSendingOn";

    public static final String IMAP_PORT_PROPERTY = "mail.imaps.port";
    public static final String IMAP_HOST_PROPERTY = "mail.imaps.host";
    
    public static final String BOUNCE_CHECK_PATTERN = "0 0/1 * * * ?";
    public static final Long DEFAULT_PROOFING_GROUP_ID = 1L;
	
	public static final String PROOFING_MESSAGE_KEY = "NL.PROOFING"; // $NON-NLS-1$
	public static final String INVALID_EMAIL_STATE_MESSAGE = "Invalid email address"; // $NON-NLS-1$

    /**
	 * The max value for thread pool specified for schedule of the publications
	 */
	public static final int SCHEDULED_THREAD_POOL_SIZE = 10;
    
    private String defaultFeedbackEmail = null;

    public String getDefaultFeedbackEmail(){
    	if ( this.defaultFeedbackEmail == null ){
    		this.defaultFeedbackEmail = Env.getProperty(
    			"newsletter.default.feedbackEmail",
				"support@negeso.com"
			);    	
    	}
    	return this.defaultFeedbackEmail;
    }
    
    
    public boolean needsAdminNotification() {
    	String notification = Env.getProperty(NEWSLETTERS_ADMIN_NOTIFICATION, "true");
    	return Boolean.parseBoolean(notification);
    }
    
    public static Long getMaxSubscribersCount() {
    	String count = Env.getProperty(NEWSLETTERS_MAX_SUBSCRIBERS_COUNT, "2500");
    	return Long.parseLong(count);
    }
    
    public static int getMaxLettersCount() {
    	return Env.getIntProperty(NEWSLETTERS_MAX_LETTERS_COUNT, 2500);
    }    

    public static String getErrorLetterSubj() {
    	return Env.getProperty(NEWSLETTERS_ERROR_LETTER_SUBJECT);
    }
    
    public static String getErrorLetterBody() {
    	return Env.getProperty(NEWSLETTERS_ERROR_LETTER_BODY);
    }
    
    
    public static String getNewslettersBccEmail() {
    	return Env.getProperty(NEWSLETTERS_BCC_EMAIL);
    }
    
    /**
     * @return true if notification on subscribe enabled
    */
    public static boolean isNewslettersNotifyOnSubscribeEnable() {
		return Boolean.parseBoolean(
				Env.getProperty(NEWSLETTERS_NOTIFY_onSUBSCRIBE,"false")
			);
	}

	/**
	 * @return true if notification on unsubscribe enabled
	*/
	
	public static boolean isNewslettersNotifyOnUnsubscribeEnable() {
		return Boolean.parseBoolean(
				Env.getProperty(NEWSLETTERS_NOTIFY_onUNSUBSCRIBE,"false")
			);
	}
    
    public String getMaxLettersErrorSubj() {
    	return Env.getProperty(NEWSLETTERS_MAX_LETTERS_ERROR_SUBJECT, "Max letters per month sending counter exceeded");
    }
    
    public String getMaxLettersErrorBody() {
    	return Env.getProperty(NEWSLETTERS_MAX_LETTERS_ERROR_BODY, "Newsletters per month sending attempts exceeded");
    }

	public static String getConfirmationXslTemplate() {
		return TEMPLATE_NEWSLETTER_CONFIRMATION_XSL;
	}
	
	public static Long getImportPerMonthLimit() {
    	String count = Env.getProperty(NEWSLETTERS_IMPORT_PER_MONTH_LIMIT, "0");
    	return Long.parseLong(count);
    }

	public static Long getImportPerMonthCounter() {
    	String count = Env.getProperty(NEWSLETTERS_IMPORT_MONTH_COUNTER, "0");
    	return Long.parseLong(count);
    }

	public static String getSpecialPageNamePattern() {
    	return Env.getProperty(NEWSLETTERS_SPECIAL_PAGE_NAME_PATTERN);
	}    

	public static boolean isSpecialPagesEnabled() {
    	return "true".equals(Env.getProperty(NEWSLETTERS_IS_SPECIAL_PAGES_ENABLED));
	}       
    
	public static String getIMAPHost(){
		return Env.getProperty(NEWSLETTERS_IMAP_HOST);
	}
	
	public static String getIMAPUser(){
		return Env.getProperty(NEWSLETTERS_IMAP_USER);
	}
	
	public static String getIMAPPass(){
		return Env.getProperty(NEWSLETTERS_IMAP_PASS);
	}
	
	public static String getIMAPPort(){
		return Env.getProperty(NEWSLETTERS_IMAP_PORT);
	}
	
	public static String getImapsFolder(){
		return Env.getProperty(NEWSLETTERS_IMAP_FOLDER);
	}
	
	public static String getBounceTimePattern(){
		return Env.getProperty(NEWSLETTERS_BOUNCE_CHECK_PATTERN);
	}
	
	public static String getMailDaemonAddress(){
		return Env.getProperty(NEWSLETTERS_MAIL_DAEMON);
	}
	
	public static String getBouncedSubject(){
		return Env.getProperty(NEWSLETTERS_MAIL_DELIVERY_FAILED_SUBJ);
	}
	
	public static String getAdminEMail(){
		return Env.getProperty(NEWSLETTERS_ADMIN_MAIL);
	}
	
	public static int getPagingItemsPerPage(){
		String prop = Env.getProperty(NEWSLETTERS_PAGING_ITEMS_PER_PAGE, "0");
		return Integer.valueOf(prop).intValue();
	}
	
	public static int getPagingPortionSize(){
		String prop = Env.getProperty(NEWSLETTERS_PAGING_PORTION_SIZE, "0");
		return Integer.valueOf(prop).intValue();
	}
	
	public static String getEmailReturnPath() {
    	return Env.getProperty(NEWSLETTERS_EMAIL_RETURN_PATH);
	}
	
	public static boolean isDeleteBouncedEmail() {
		return Boolean.valueOf(Env.getProperty(NEWSLETTERS_BOUNCE_MAIL_DELETE, "false"));
	}
	
	public static int getMaxGroupNumber() {
		String strMaxNumber = Env.getProperty(NEWSLETTERS_MAX_GROUP_NUMBER, "0");
		try {
			return Integer.valueOf(strMaxNumber).intValue();
		} catch (Exception e) {
			return 0;
		}
	}
	
    public static boolean isSendingOn() {
        String res = Env.getGlobalProperty(NEWSLETTERS_IS_SENDING_ON, "true");
        return "true".equalsIgnoreCase(res);
    }
	
	public static String getXMailerAttrKey(){
		return "x-mailer";
	}
	
	public static String getXMailingIdAttrKey(){
		return "x-mailing-id";
	}

    /**
     * Host name for notification of sent newsletter 
     * @return
     */
	public static String getNotificationHostName() {
		return Env.getCommonHostName();
	}
	
}
