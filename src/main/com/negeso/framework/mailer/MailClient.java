/*
 * @(#)$Id: MailClient.java,v 1.20, 2007-03-27 08:28:38Z, Alexander Serbin$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.mailer;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.sun.mail.smtp.SMTPMessage;



/**
 * <p>Title: Utility class to send the mails.</p>
 * <p>Description: sendXmessage method can be used to send simple mails and
 * mails with attachments (multipart) </p>
 * 
 * @version 	$Revision: 21$
 * @author 		Oleg Lyebyedyev
 * @author 		Olexiy Strashko
 * 
 */

public class MailClient {

	private static final String SPAM_EMAILS = "spam.emails";


	private static Logger logger = Logger.getLogger( MailClient.class );
    
    
    /**********************************************
     * Default smtp host
     **********************************************/
   // public static final String DEFAULT_SMTP_HOST = Env.getSmtpHost();

    /***********************************************
     * Default encoding value
     ***********************************************/
    public static final String DEFAULT_ENCODING = "UTF-8";


    /***********************************************
     * Default content type
     ***********************************************/
    public static final String DEFAULT_CONTENT_TYPE = "text/plain";

    private static final Properties javaMailProperties = new Properties();
    
	private JavaMailSenderImpl sender = new JavaMailSenderImpl();
	
	{
		String validSmtpHost = Env.getValidSmtpHost();
		javaMailProperties.put("mail.host", validSmtpHost);
		sender.setHost(validSmtpHost);
		sender.setDefaultEncoding(DEFAULT_ENCODING);
	}  
    /**
     * Build extended email address by adress owner name and email. 
     * String pattern:  {$name} <{$email}> 
     * 
     * @param name		The name of the adress owner
     * @param email		The email of the adress owner
     * @return 			The resulting extended adress 
     * @throws 			MessagingException
     * @throws CriticalException
     */
    public static String buildAddress(String name, String email) 
    	throws CriticalException
    {
        if (name == null){
            name = "";
        }
        if (email == null){
            logger.error("- Input parameter: email is null");
            throw new CriticalException("Input parameter: email is null");
        }
        return "\"" + name + "\"" + " <" + email + ">";
    }
    
    /**
     * Build extended email address by adress owner name and email. 
     * String pattern:  {$name} <{$email}> 
     * 
     * @param name		The name of the adress owner
     * @param email		The email of the adress owner
     * @return 			The resulting extended adress[name,email] 
     * @throws 			MessagingException
     * @throws CriticalException
     */
    public static String[] buildAddressUseingEncoding(String name, String email) 
    	throws CriticalException
    {
    	String[] address = {null,null};
    	address[0] = (name == null) ? "" : name ;
    	if (email == null){
            logger.error("- Input parameter: email is null");
            throw new CriticalException("Input parameter: email is null");
        }
    	address[1]=email;        
        return address;
    }

    /***************************************************************
     * Send simple text/plain message without attachments
     * and default encoding UTF-8.
     * No excetion if message sent ok.
     * @param to
     * @param from
     * @param subj
     * @param messageString
     * @throws MessagingException 
    ***************************************************************/
    public void sendTextPlainMessage( 
    	String to,
        String from,
        String subj,
        String messageString
    ) throws MessagingException 
    {
    	MessageVO vo = new MessageVO();
    	String[] recipients = new String[1];
    	recipients[0] = to;
    	vo.to = recipients;
    	vo.from = from;
    	vo.subj = subj;
    	vo.messageString = messageString;
    	vo.isHTML = false;
    	sendMessage(vo);
    }


    /***************************************************************
     * Send simple html message without attachments
     * and default encoding UTF-8
     * No exception if message sent ok.
     * @param to
     * @param from
     * @param subj
     * @param messageString
     * @param smtpHost
     * @throws MessagingException
     ***************************************************************/
    public void sendHTMLMessage( 
    	String to,
		String from,
		String subj,
		String messageString
	) 
    	throws MessagingException
	{
    	MessageVO vo = new MessageVO();
    	String[] recipients = new String[1];
    	recipients[0] = to;
    	vo.to = recipients;
    	vo.from = from;
    	vo.subj = subj;
    	vo.messageString = messageString;
    	vo.isHTML = true;
    	sendMessage(vo);
	}

    /***************************************************************
     * Send simple html message with attachments
     * and default encoding UTF-8
     * No exception if message sent ok.
     */ 
    
    
    
    public void sendHTMLMessage( 
        String to,
        String from,
        String subj,
        String messageString,
        String smtpHost,
        DataSource[] attachments
    ) throws MessagingException 
    {
    	MessageVO vo = new MessageVO();
    	String[] recipients = new String[1];
    	recipients[0] = to;
    	vo.to = recipients;
    	vo.from = from;
    	vo.subj = subj;
    	vo.messageString = messageString;
    	vo.isHTML = true;
    	vo.attachments = attachments;
    	sendMessage(vo);
    }

    public void sendMessage( String[] to,
            String from,
            String subj,
            String messageString,
            DataSource[] attachments,
            String encoding,
            String contentType,
            Map headers 
    )
    throws MessagingException
    {
    	MessageVO vo = new MessageVO();
    	vo.to = to;
    	vo.from = from;
    	vo.subj = subj;
    	vo.messageString = messageString;
    	vo.isHTML = true;
    	vo.attachments = attachments;
    	vo.setContentType(contentType);
    	vo.headers = headers;
    	sendMessage(vo);
    }

    public void sendBouncedMessage(String[] to,
            String[] from,
            String returnPath,
            String subj,
            String messageString,
            DataSource[] attachments,
            String encoding,
            String contentType,
            Map headers 
    )
    throws MessagingException, UnsupportedEncodingException
    {
    	Session session = Session.getInstance(javaMailProperties, null);
		SMTPMessage message = new SMTPMessage(session);
		message.setSentDate(new Date());
		message.setRecipient(RecipientType.TO, new InternetAddress(to[1], to[0], encoding));
		String email = avoidAntiSpamProtection(from[1]);
		message.setFrom(new InternetAddress(email, from[0], encoding));
		message.setSubject(subj, encoding);
		contentType += "; charset=\""+encoding+"\"";
		message.setContent(messageString, contentType);
		message.setEnvelopeFrom(returnPath);
		
		MimeMultipart mp = new MimeMultipart();
		 
		MimeBodyPart mimeBodyPart1 = new MimeBodyPart();
		mimeBodyPart1.setDataHandler(new DataHandler(messageString, contentType));
		mp.addBodyPart(mimeBodyPart1);
		 
		for (DataSource ds : attachments) {
			MimeBodyPart mimeBodyPart2 = new MimeBodyPart();
	        mimeBodyPart2.setDataHandler(new DataHandler(ds));
	        mimeBodyPart2.setFileName(ds.getName());
	        mp.addBodyPart(mimeBodyPart2);
	    }
		 
	    message.setContent(mp);		
		
		addHeaders(message, headers);
    	message.setHeader("Content-Transfer-Encoding","7bit");       	

    	Transport.send(message);				
    }
    
    /****************************************************************
     * Main sendmessage method. No excpetion if message sent ok.
     *
     * @param to - Recipient address(es). An array of addresses can be used.
     * @param from - Sender address
     * @param subj - message subject
     * @param messageString - message text
     * @param attachments - Array of attachments ( DataSource[] ).
     * If null or length == 0 then message will not multipart
     * @param encoding - encoding for subject and message.
     * If null then default UTF-8 is used.
     * @param contentType - Usually "text/plain" or "text/html".
     * If null then "text/plain as default.
     * @param smtpHost - smtp host address.
     * If null the 127.0.0.1 (localhost) is used.
     * @param headers - additional RFC822 headers to put into message.
     * If null - no additional headers generated.
     * @throws MessagingException
     ***************************************************************/
    
    public void sendMessage(MessageVO vo) throws MessagingException  {
    		MimeMessage message = sender.createMimeMessage();
    		message.setSentDate(new Date());
        	if (vo.isEncodingSpecified()) {
        		sender.setDefaultEncoding(vo.encoding);
        	}
        	// use the true flag to indicate you need a multipart message
        	MimeMessageHelper helper = new MimeMessageHelper(message, vo.isAttachments());
        	helper.setTo(vo.to);
        	helper.setFrom(avoidAntiSpamProtection(vo.from));
        	helper.setSubject(vo.subj);
        	helper.setText(vo.messageString, vo.isHTML);
        	addAttachments(vo, helper);
        	addHeaders(message, vo);
        	message.setHeader("Content-Transfer-Encoding","7bit");
        	
        	if (logger.isDebugEnabled()) {
        		logger.debug(new StringBuffer(500)
					        		.append("\n------Send mail-----")
					        		.append("\nto: " + Arrays.toString(vo.to))
					        		.append("\nfrom: " + vo.from)
					        		.append("\nsubject: " + vo.subj)
					        		.append("\n!-----Send mail----!"));        		
        	}
        	
        	try {
        		sender.send(message);				
			} catch (MailException e) {
				logger.error("Error sending message to: " + Arrays.toString(vo.to) + ",  error message: " + e.getMessage());
				throw e;
			}
        }

    private boolean isSpamEmail(String from) {
    	String property = Env.getGlobalProperty(SPAM_EMAILS);
    	if (property == null) {
    		return false;
    	}
    	String[] spamEmails = property.split(",");
    	for (int i = 0; i < spamEmails.length; i++) {
    		if (from.contains(spamEmails[i])) {
    			return true;
    		}
    	}
    	return false;
    }

	private String avoidAntiSpamProtection(String from) {
		if	(isSpamEmail(from)) {
			return Env.getDefaultEmail();
		}	
		return from;
	}

	private void addAttachments(MessageVO vo, MimeMessageHelper helper) throws MessagingException {
		if (vo.isAttachments()) {
		    for ( int i = 0; i < vo.attachments.length; ++i ) {
		    	helper.addAttachment(vo.attachments[i].getName(), vo.attachments[i]);
		    }
		}
	}
	
	private void addAttachments(MimeMessage message, DataSource[] attachments) {
		for (DataSource ds : attachments) {
			 Multipart multipart = new MimeMultipart();
			 
		}
	}
    
    private void addHeaders(MimeMessage message, MessageVO vo) throws MessagingException {
    	if (vo.isHeadersSpecified()) {
    		String key = null;
    		String value = null;
            for (Iterator i = vo.headers.keySet().iterator(); i.hasNext();  ){
                key = (String) i.next();
                value = (String) vo.headers.get(key);
                if ( value != null ) {
	                message.setHeader(key, value);
                }
            }
    	}
    }
    
    private void addHeaders(MimeMessage message, Map<String, String> headers) throws MessagingException {
   		for (Map.Entry<String, String> entry : headers.entrySet()) {
			message.setHeader(entry.getKey(), entry.getValue());
   		}
    }
    
    public class MessageVO {
    	private String[] to;
    	private String from;
    	private String subj;
    	private String messageString;
    	private DataSource[] attachments;
    	private String encoding;
    	private boolean isHTML = false;
    	private Map headers;
        
        public boolean isAttachments() {
        	return attachments != null && attachments.length > 0;
        }
        
        public boolean isEncodingSpecified() {
        	return StringUtils.isNotEmpty(encoding);
      	}
        
        public boolean isHeadersSpecified() {
        	return headers != null && headers.size() > 0 ;
        }
        
        public void setContentType(String contentType) {
            if (contentType.compareTo("text/html") == 0) {
            	isHTML = true;
            } else {
            	isHTML = false;
            }
        }

		public void setAttachments(DataSource[] attachments) {
			this.attachments = attachments;
		}

		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public void setHeaders(Map headers) {
			this.headers = headers;
		}

		public void setHTML(boolean isHTML) {
			this.isHTML = isHTML;
		}

		public void setSubj(String subj) {
			this.subj = subj;
		}

		public void setTo(String[] to) {
			this.to = to;
		}
		
    }	
    	
}