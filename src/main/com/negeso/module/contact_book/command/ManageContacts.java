/*
 * @(#)$Id: ManageContacts.java,v 1.10, 2007-01-09 18:40:37Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.contact_book.command;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Contact;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.Timer;
import com.negeso.module.contact_book.Configuration;
import com.negeso.module.contact_book.domain.ContGroup;
import com.negeso.module.contact_book.domain.Group;
import com.negeso.module.contact_book.generators.ContactXmlBuilder;

/**
 *
 * Contact manager
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 11$
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ManageContacts extends AbstractCommand {
	private static Logger logger = Logger.getLogger( ManageContacts.class );
	
	private static final String INPUT_GROUP_ID = "groupId";
	private static final String INPUT_BACK_LINK = "back_link";
	private static final String INPUT_CONTACT_ID = "contactId";

	private static final String RESULT_EDIT_CONTACT = "edit-contact";
	private static final String RESULT_BROWSE_CONTACTS = "contact-list";


	private static final String INPUT_ORDER_BY = "order_by";

	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	@ActiveModuleRequired
	public ResponseContext execute() {
		logger.debug("+");
		RequestContext request = this.getRequestContext();
		ResponseContext response = new ResponseContext();
		
		if ( !SecurityGuard.isContributor(request.getSession().getUser()) ){
			response.setResultName( AbstractCommand.RESULT_ACCESS_DENIED );
			return response;
		}
		
		Timer timer = new Timer();
		timer.start();
		
		int langId = request.getSession().getLanguage().getId().intValue();
		Connection con = null;
		String action = request.getNonblankParameter( "action" );
		logger.info("action: " + action);
		Element page = null;
		try{
			page = XmlHelper.createPageElement( request );
			Xbuilder.setAttr(
				page, "back-link", request.getNonblankParameter(INPUT_BACK_LINK)
			);
			con = DBHelper.getConnection();
			if ( "show_contact".equals(action) ){
				response.setResultName(ManageContacts.RESULT_EDIT_CONTACT);
				this.renderContact(con, request, page, langId);
			}
			else if( "add_contact".equals(action) ){
				String[] params = null;
				request.setParameter(INPUT_CONTACT_ID, params);
				response.setResultName(ManageContacts.RESULT_EDIT_CONTACT);
				this.renderContact(con, request, page, langId);
			}
			else if( "save_contact".equals(action) ){
				this.doSaveContact(con, request, page, langId);
				response.setResultName(ManageContacts.RESULT_EDIT_CONTACT);
				this.renderContact(con, request, page, langId);
			}
			else if( "search_contacts".equals(action) ){
				this.doSearchContacts(con, request, page, langId);
				response.setResultName(ManageContacts.RESULT_BROWSE_CONTACTS);
			}
			else if( "remove_contact".equals(action) ){
				this.doRemoveContact(con, request, page, langId);
				response.setResultName(ManageContacts.RESULT_BROWSE_CONTACTS);
				this.renderGroupContacts(con, request, page, langId);
			}
			else{
				response.setResultName(ManageContacts.RESULT_BROWSE_CONTACTS);
				this.renderGroupContacts(con, request, page, langId);
			}
		}
		catch( SQLException e ){
			logger.error("-error", e);
			response.setResultName(RESULT_FAILURE);
		}
		catch( CriticalException e ){
			logger.error("-error", e);
			response.setResultName(RESULT_FAILURE);
		}
		finally{
			DBHelper.close(con);
		}
		
		if ( page != null ){
			response.getResultMap().put( OUTPUT_XML, page.getOwnerDocument() );
		}
		logger.info("time:" + timer.stop());
		
		logger.debug("-");
		return response;
	}

	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException
	 */
	private void doSearchContacts(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		String searchString = request.getNonblankParameter("search_string");
		logger.info("search_string:" + searchString);
		/*
		if ( searchString != null ){
			searchString = "%" + searchString.trim() + "%";			
		}
		*/
		ContactXmlBuilder.searchContacts(con, page, searchString, "first_name");
		Element resEl = (Element) page.getFirstChild();
		if ( resEl != null ){
			Xbuilder.setAttr(resEl, "search-string", searchString);
		}
	}

	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException
	 */
	private void doRemoveContact(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long contactId = request.getLong(INPUT_CONTACT_ID);
		Long groupId = request.getLong(INPUT_GROUP_ID);
		
    	if (contactId == null){
    		logger.error("Error: ContactId is null!!!!");
    		return;
    	}
    	if (groupId == null){
    		logger.error("Error: GroupId is null!!!!");
    		return;
    	}
		
		ContGroup contGroup = ContGroup.findByGroupAndContact(
			con, groupId, contactId
		);
		if ( contGroup != null ){
			Contact contact = Contact.findById(con, contactId);
			if ( contact != null ){
				contact.delete(con);
				contGroup.delete(con);
			}
		}
	}

	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException
	 */
	private void renderContact(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long contactId = request.getLong(INPUT_CONTACT_ID);
		Long groupId = request.getLong(INPUT_GROUP_ID);
		
		if ( groupId == null ){
			ContGroup contGroup = ContGroup.findByContactId(con, contactId);
			if ( contGroup == null ){
				logger.error("Error: groupId is null!!!");
			}
			else{
				groupId = contGroup.getGroupId();
			}
		}
		
		Group group = Group.findById(con, groupId); 
		if ( group == null ){
			logger.error("Error: group not found!!!");
		}
		
		Element contact = null;
		if (contactId != null){
			ContactXmlBuilder.appendContactEl(con, page, contactId);
			contact = (Element) page.getLastChild();
		}
		else{
	        contact = Xbuilder.addEl(page, "contact", null);
		}
		
		if ( contact != null ){
	        Xbuilder.setAttr(contact, "group-id", groupId);
	        Xbuilder.setAttr(contact, "group-title", group.getTitle());
	        Xbuilder.setAttr(contact, "image-width", "" + Configuration.getImageWidth());
	        Xbuilder.setAttr(contact, "image-height", "" + Configuration.getImageHeight());
		}
	}

	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 * @throws CriticalException
	 */
	private void renderGroupContacts(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long groupId = request.getLong(INPUT_GROUP_ID);
		logger.info("render contacts:" + groupId);
		String orderBy = request.getString(INPUT_ORDER_BY, null);
		if ( groupId != null ){
			ContactXmlBuilder.buildGroupContacts(con, page, groupId, orderBy);
		}
		
	}

	@Deprecated
	private String getOrderByField(RequestContext request){
		String orderBy = request.getNonblankParameter(INPUT_ORDER_BY);
		if ( orderBy == null ){
			orderBy = (String) request.getSession().getAttribute("cb_order_by_field");
			if ( orderBy == null ){
				orderBy = " first_name, second_name ";
			}
		}
		return orderBy;
	}
	
	/**
	 * @param con
	 * @param request
	 * @param page
	 * @param langId
	 */
	private void doSaveContact(
		Connection con, RequestContext request, Element page, int langId) 
		throws CriticalException 
	{
		Long contactId = request.getLong( INPUT_CONTACT_ID );
		// remove
		Contact contact = null;
		if ( contactId != null ){
			contact = Contact.findById(con, contactId);
		}
		if ( contact == null ){
			logger.info("Creating a new contact");
			contact = new Contact();
		}
		
		String tmp = request.getNonblankParameter("firstName");
		contact.setFirstName(tmp);
		
		tmp = request.getNonblankParameter("secondName");
		contact.setSecondName(tmp);

		tmp = request.getNonblankParameter("email");
		contact.setEmail(tmp);

		tmp = request.getNonblankParameter("phone");
		contact.setPhone(tmp);
		
		tmp = request.getNonblankParameter("weblink");
		if ( tmp != null ){
			if ( !tmp.startsWith("http://") ){
				tmp = "http://" + tmp;;
			}
		}
		contact.setWebLink(tmp);

		tmp = request.getNonblankParameter("department");
        logger.debug("Department:" + tmp);
		contact.setDepartment(tmp);

		tmp = request.getNonblankParameter("jobTitle");
		contact.setJobTitle(tmp);

		/* check&set publish date */ 
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Timestamp date = null;
		tmp = request.getNonblankParameter("birthday");
		if (tmp != null) {
			try{
				date = new Timestamp(dateFormat.parse(tmp).getTime());
				contact.setBirthDate(date);
			}
			catch(ParseException e){
				logger.error("error");
			}
		}
		else{
			contact.setBirthDate(null);
		}

        tmp = request.getNonblankParameter("nickname");
        contact.setNickname(tmp);


		if ( request.getNonblankParameter("removeImage") != null ){
			logger.info("remove image");
			contact.setImageLink(null);
		}
		else{
			tmp = request.getNonblankParameter("imageLink");
			logger.info("image field:" + tmp);
			contact.setImageLink(tmp);
		}
		
		contact.setType( Configuration.getContactType() );
		if ( contact.getId() == null ){
			contact.insert(con);
			ContGroup contGroup = new ContGroup();
			contGroup.setContactId(contact.getId());
			Long groupId = request.getLong( INPUT_GROUP_ID );
			contGroup.setGroupId(groupId);
			contGroup.insert(con);
			request.setParameter(INPUT_CONTACT_ID, contact.getId().toString());
		}
		else{
			contact.update(con);
		}
	}

	@Override
	public String getModuleName() {
		return ModuleConstants.CONTACT_BOOK_MODULE;
	}
}
