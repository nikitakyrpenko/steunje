/*
 * @(#)$Id: Mtaf.java,v 1.0.1.0, 2007-01-09 18:47:23Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.mail_to_a_friend;

import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.generators.Xbuilder;

/**
 *
 * Mail to a friend domain object
 * 
 * @version		$Revision: 2$
 * @author		Olexiy Strashko
 * 
 */
public class Mtaf {
    private String senderEmail = null;
    private String senderName = null;
    private String friendEmail = null;
    private String friendName = null;
    
    private String mailBody = null;
    private boolean isHtml = true;
    
    private String subject = null;
    private String pageLink = null;
    private String pageTitle = null;

    
    public Element addMtafAttrs(Element mtaf){
        Xbuilder.setAttr(mtaf, "friend_email", this.getFriendEmail()); 
        Xbuilder.setAttr(mtaf, "friend_name", this.getFriendName()); 
        Xbuilder.setAttr(mtaf, "sender_email", this.getSenderEmail()); 
        Xbuilder.setAttr(mtaf, "sender_name", this.getSenderName()); 
        Xbuilder.setAttr(mtaf, "page_link", this.getPageLink()); 
        Xbuilder.setAttr(mtaf, "page_title", this.getPageTitle()); 
        Xbuilder.setAttr(mtaf, "site_link", Env.getHostName()); 
        Xbuilder.setAttr(mtaf, "mail_body", this.getMailBody()); 
        Xbuilder.setAttr(mtaf, "subject", this.getSubject()); 
        return mtaf;
    }
    
    /**
     * @return Returns the friendEmail.
     */
    public String getFriendEmail() {
        return friendEmail;
    }
    /**
     * @param friendEmail The friendEmail to set.
     */
    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }
    /**
     * @return Returns the friendName.
     */
    public String getFriendName() {
        return friendName;
    }
    /**
     * @param friendName The friendName to set.
     */
    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
    /**
     * @return Returns the isHtml.
     */
    public boolean isHtml() {
        return isHtml;
    }
    /**
     * @param isHtml The isHtml to set.
     */
    public void setHtml(boolean isHtml) {
        this.isHtml = isHtml;
    }
    /**
     * @return Returns the mailBody.
     */
    public String getMailBody() {
        return mailBody;
    }
    /**
     * @param mailBody The mailBody to set.
     */
    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }
    /**
     * @return Returns the pageLink.
     */
    public String getPageLink() {
        return pageLink;
    }
    /**
     * @param pageLink The pageLink to set.
     */
    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }
    /**
     * @return Returns the pageTitle.
     */
    public String getPageTitle() {
        return pageTitle;
    }
    /**
     * @param pageTitle The pageTitle to set.
     */
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }
    /**
     * @return Returns the senderEmail.
     */
    public String getSenderEmail() {
        return senderEmail;
    }
    /**
     * @param senderEmail The senderEmail to set.
     */
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
    /**
     * @return Returns the senderName.
     */
    public String getSenderName() {
        return senderName;
    }
    /**
     * @param senderName The senderName to set.
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    /**
     * @return Returns the subject.
     */
    public String getSubject() {
        return subject;
    }
    /**
     * @param subject The subject to set.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * 
     */
    public Mtaf() {
        super();
    }

}
