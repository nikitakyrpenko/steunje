/*
 * @(#)$Id: ContactForm.java,v 1.4, 2005-06-06 13:04:15Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.contact_book.component;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.contact_book.generators.GroupXmlBuilder;

/**
 * Contact form
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 5$
 */
public class ContactForm {
    private static Logger logger = Logger.getLogger( ContactForm.class );

	String action = null;
	Long contactId = null;
	Long groupId = null;
	String searchString = null;
	String orderBy = null;
    String order = null;
	String firstName = null;
	String secondName = null;
	String email = null;
	String department = null;
	String job = null;
	
	//"show_all"
	//"search"
	//"advanced_search"
	//"show_contact"
	
	public static ContactForm buildFromRequest( RequestContext request ){
		ContactForm form = new ContactForm();
		
		String tmp = request.getNonblankParameter("action");
		form.setAction(tmp);
		
        if ( request.getLong( "contactId" ) != null ){
            form.setContactId(request.getLong( "contactId" ));
            if ( form.getAction() == null ){
                form.setAction("show_contact");
            }
        }
		
        if ( request.getLong( "groupId" ) != null ){
            form.setGroupId(request.getLong( "groupId" ));
            if ( form.getAction() == null ){
                form.setAction("show_group");
            }
        }

		tmp = request.getNonblankParameter("search_string");
		if ( tmp != null ){
			form.setSearchString( tmp.toLowerCase() );
		}
		
		tmp = request.getNonblankParameter("order_by");
		if ( tmp != null ){
			form.setOrderBy(tmp);
		}
		else{
			form.setOrderBy("first_name");
		}
        
        if (request.getParameter("order")!=null && !request.getParameter("order").equals("")){
            form.setOrder(request.getParameter("order"));
        }

		tmp = request.getNonblankParameter("fname");
		form.setFirstName(tmp);
		
		tmp = request.getNonblankParameter("sname");
		form.setSecondName(tmp);
		
		tmp = request.getNonblankParameter("dep");
		form.setDepartment(tmp);

		tmp = request.getNonblankParameter("job");
		form.setJob(tmp);

		tmp = request.getNonblankParameter("email");
		form.setEmail(tmp);

		return form;
	}
	
	/**
	 * Build element
	 * 
	 * @param parent
	 * @return
	 * @throws CriticalException
	 */
	Element buildElement(Element parent) throws CriticalException{
		Element formEl = Xbuilder.addEl(parent, "contact-form", null);
		Xbuilder.setAttr(formEl, "action", this.getAction());
		Xbuilder.setAttr(formEl, "contact-id", this.getContactId());
		Xbuilder.setAttr(formEl, "group-id", this.getGroupId());
		Xbuilder.setAttr(formEl, "search-string", this.getSearchString());
		Xbuilder.setAttr(formEl, "order-by", this.getOrderBy());
        Xbuilder.setAttr(formEl, "order", this.getOrder());
		Xbuilder.setAttr(formEl, "first-name", this.getFirstName());
		Xbuilder.setAttr(formEl, "second-name", this.getSecondName());
		Xbuilder.setAttr(formEl, "email", this.getEmail());
		Xbuilder.setAttr(formEl, "department", this.getDepartment());
		Xbuilder.setAttr(formEl, "job", this.getJob());
		
		Connection con = null;
		try{
			con = DBHelper.getConnection();
			GroupXmlBuilder.buildGroups(con, formEl);
		}
		catch(SQLException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
		finally {
			DBHelper.close(con);
		}
		return formEl;
	}
	
	/**
	 * 
	 */
	public ContactForm() {
		super();
	}

	/**
	 * @return Returns the action.
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action The action to set.
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return Returns the contactId.
	 */
	public Long getContactId() {
		return contactId;
	}
	/**
	 * @param contactId The contactId to set.
	 */
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}
	/**
	 * @return Returns the orderBy.
	 */
	public String getOrderBy() {
		return orderBy;
	}
	/**
	 * @param orderBy The orderBy to set.
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	/**
	 * @return Returns the searchString.
	 */
	public String getSearchString() {
		return searchString;
	}
	/**
	 * @param searchString The searchString to set.
	 */
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	/**
	 * @return Returns the department.
	 */
	public String getDepartment() {
		return department;
	}
	/**
	 * @param department The department to set.
	 */
	public void setDepartment(String department) {
		this.department = department;
	}
	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return Returns the firstName.
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName The firstName to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return Returns the job.
	 */
	public String getJob() {
		return job;
	}
	/**
	 * @param job The job to set.
	 */
	public void setJob(String job) {
		this.job = job;
	}
	/**
	 * @return Returns the secondName.
	 */
	public String getSecondName() {
		return secondName;
	}
	/**
	 * @param secondName The secondName to set.
	 */
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	/**
	 * @return Returns the groupId.
	 */
	public Long getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId The groupId to set.
	 */
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

    public String getOrder(){
        return order;
    }

    public void setOrder(String newOrder){
        order = newOrder;
    }
}
