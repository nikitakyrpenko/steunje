/*
 * @(#)$Id: MailToAFriendComponent.java,v 1.0.1.6, 2007-03-26 17:23:50Z, Alexander Serbin$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.mail_to_a_friend;

import java.util.Map;

import javax.mail.MessagingException;
import javax.xml.transform.Templates;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.mailer.MailClient;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.framework.util.StringUtil;


/**
 *
 * Mail To A Friedn component
 * 
 * @version		$Revision: 8$
 * @author		Olexiy Strashko
 * 
 */
public class MailToAFriendComponent extends AbstractPageComponent {
    private static Logger logger = Logger.getLogger(MailToAFriendComponent.class);


    private Mtaf mtaf = null;

    private static final String MAIL_TO_A_FRIEND_HTML_XSL = "MAIL_TO_A_FRIEND_HTML_XSL";
    private static final String MAIL_TO_A_FRIEND_PLAIN_XSL = "MAIL_TO_A_FRIEND_PLAIN_XSL";
    
    /* (non-Javadoc)
     * @see com.negeso.module.page.PageComponent#getElement(org.w3c.dom.Document, com.negeso.framework.controller.RequestContext, java.util.Map)
     */
    public Element getElement(
        Document document, RequestContext request, Map parameters) 
    {
        logger.debug("+");
        Element elt = Xbuilder.createEl(document, "mail_to_a_friend", null);

        String action = request.getNonblankParameter("action");
        logger.info("action:" + action);
        if ("sendmail".equals(action)){
            this.doSendMail(request);
            if ( this.hasErrors() ){
                this.renderStartPage(elt, request);
                XmlHelper.buildErrorsElement(elt, this.getErrors());
            }
            else{
                this.renderSuccessPage(elt);
            }
        }
        else{
            this.renderStartPage(elt, request);
        }
        logger.debug("-");
        return elt;
    }

    /**
     * @param elt
     */
    private void renderSuccessPage(Element elt) {
        Xbuilder.setAttr(elt, "view", "success_result");
        this.getMtaf().addMtafAttrs(elt);
    }

    /**
     * @param elt 
     * @param request 
     * 
     */
    private void renderStartPage(Element elt, RequestContext request) {
        Xbuilder.setAttr(elt, "view", "mail_form");
        this.getMtaf().setPageLink(request.getNonblankParameter("page_link"));
        this.getMtaf().setPageTitle(request.getNonblankParameter("page_title"));
        this.getMtaf().addMtafAttrs(elt);
    }
    
    /**
     * send mail
     * 
     * @param request
     */
    private void doSendMail(RequestContext request){
        this.getMtaf().setSenderEmail(request.getNonblankParameter("sender_email"));
        if ( this.getMtaf().getSenderEmail() == null ){
            this.getErrors().add("SENDER_MAIL_IS_REQUIRED");
        }
        else if ( !StringUtil.validateEmail(this.getMtaf().getSenderEmail())){
            this.getErrors().add("FRIEND_MAIL_IS_INVALID");
        }

        this.getMtaf().setSenderName(request.getNonblankParameter("sender_name"));
        
        this.getMtaf().setFriendEmail(request.getNonblankParameter("friend_email"));
        if ( this.getMtaf().getFriendEmail() == null ){
            this.getErrors().add("FRIEND_MAIL_IS_REQUIRED");
        }
        else if ( !StringUtil.validateEmail(this.getMtaf().getFriendEmail())){
            this.getErrors().add("FRIEND_MAIL_IS_INVALID");
        }

        this.getMtaf().setFriendName(request.getNonblankParameter("friend_name"));
        
        if ( "plain".equals(request.getNonblankParameter("text_type")) ){
            this.getMtaf().setHtml(false);
        }

        this.getMtaf().setMailBody(request.getNonblankParameter("mail_body"));
        if ( this.getMtaf().getFriendEmail() == null ){
            this.getErrors().add("MAIL_BODY_IS_REQUIRED");
        }
        this.getMtaf().setSubject(request.getString("subject", ""));
        
        this.getMtaf().setPageLink(request.getNonblankParameter("page_link"));
        this.getMtaf().setPageTitle(request.getNonblankParameter("page_title"));
        
        if ( this.hasErrors() ){
            return;
        }
        
        MailClient mailClient = new MailClient();
        try{
            String toAddress = mailClient.buildAddress(
                   this.getMtaf().getFriendName(), this.getMtaf().getFriendEmail()
            );
            String fromAddress = mailClient.buildAddress(
                   this.getMtaf().getSenderName(), this.getMtaf().getSenderEmail()
            );
            
            
            Element page = XmlHelper.createPageElement(request);
            Element mtaf = Xbuilder.addEl(
                page, 
                "mail_to_a_friend", 
                this.getMtaf().getMailBody()
            );
            this.getMtaf().addMtafAttrs(mtaf);

            if ( this.getMtaf().isHtml() ){
                
//              get template from Site
                Templates confirmationTmpl = Env.getSite().getXslTemplates("/template/mail_to_a_friend_html.xsl"); 

//                transform document using template to string - this is email's body
                String body = XmlHelper.transformToString(
                	page.getOwnerDocument(), 
                    confirmationTmpl, 
                    "html"
                );
                
                
                
                mailClient.sendHTMLMessage(
                    toAddress, 
                    fromAddress, 
                    this.getMtaf().getSubject(), 
                    body 
                );
            }
            else{
//              get template from Site
                Templates confirmationTmpl = Env.getSite().getXslTemplates("/template/mail_to_a_friend_plain.xsl"); 

//                transform document using template to string - this is email's body
                String body = XmlHelper.transformToString(
                	page.getOwnerDocument(), 
                    confirmationTmpl, 
                    "html"
                );
            	
                body = StringUtil.stripTagsEx(body);
                
                mailClient.sendTextPlainMessage(
                    toAddress, 
                    fromAddress, 
                    this.getMtaf().getSubject(), 
                    body 
                );
            }
        }
        catch (MessagingException e){
            this.getErrors().add("CRITCIAL_ERROR");
            logger.error("-error", e);
        } 
        catch (CriticalException e) {
            this.getErrors().add("CRITCIAL_ERROR");
            logger.error("-error", e);
        } 
        catch (Throwable e) {
        	this.getErrors().add("CRITCIAL_ERROR");
            logger.error("-error", e);
        }
    }
    
    /**
     * @return Returns the mtaf.
     */
    public Mtaf getMtaf() {
        if (this.mtaf == null){
            this.mtaf = new Mtaf();
        }
        return mtaf;
    }
}
