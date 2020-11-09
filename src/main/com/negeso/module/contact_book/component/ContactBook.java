/*
 * @(#)$Id: ContactBook.java,v 1.9, 2007-01-09 18:40:43Z, Anatoliy Pererva$
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
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.module.contact_book.generators.ContactXmlBuilder;

/**
 *
 * Contact book component
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 10$
 */
public class ContactBook extends AbstractPageComponent {
	private static Logger logger = Logger.getLogger( ContactBook.class );

	/* (non-Javadoc)
	 * @see com.negeso.module.page.PageComponent#getElement(org.w3c.dom.Document, com.negeso.framework.controller.RequestContext, java.util.Map)
	 */
	public Element getElement(
		Document document, RequestContext request, Map parameters) 
	{
        logger.debug("+");
        Element elt = Xbuilder.createEl(document, "contact-book", null);
        

        Connection con = null;
		try{
	        ContactForm form = ContactForm.buildFromRequest(request);
	        form.buildElement(elt);

	        con = DBHelper.getConnection();
			
	        String order = form.getOrderBy();
            if(form.getOrderBy()!= null && form.getOrderBy().trim().equals("first_name, second_name")
                && form.getOrder()!= null && form.getOrder().equals("down")){
                order=" first_name DESC, second_name DESC ";
            }else
            if(form.getOrder()!= null && form.getOrder().equals("down")){
                order+=" DESC ";
            }

			if ( "show_contact".equals( form.getAction() ) ){
				ContactXmlBuilder.appendContactEl( con, elt, form.getContactId() );
			}
			else if ( "advanced_search".equals( form.getAction() ) ){
	            createSearchResults(elt, request, con, form, order);
			}
			else if ( "show_group".equals( form.getAction() ) ){
		        logger.debug(form.getGroupId());
	            ContactXmlBuilder.buildGroupContacts(
	            	con, elt, form.getGroupId(), order
				);
			}
			else if ( "search".equals( form.getAction() ) ){
				logger.debug(form.getOrderBy());
	            ContactXmlBuilder.searchContacts(
	            	con, elt, form.getSearchString(), order
				);
			}
            else if ( "show_all".equals( form.getAction() ) ) {
                // show all contacts
                ContactXmlBuilder.buildAllContacts( con, elt, order );
            }
            else{
                //do not show any contacts - main page
            }
    	}
    	catch(SQLException e){
			logger.error("-error", e);
			DBHelper.rollback(con);
			elt.setAttribute("error", e.getMessage());
		}
    	catch(CriticalException e){
			logger.error("-error", e);    	
			DBHelper.rollback(con);
			elt.setAttribute("error", e.getMessage());
		}
    	finally{
    		DBHelper.close(con);
    	}

    	return elt;
	}
	
	/**
	 * 
	 * @param parent
	 * @param request
	 * @param conn
	 * @throws CriticalException
	 */
	public void createSearchResults(
        Element parent, RequestContext request, Connection conn, ContactForm form, String order) throws CriticalException
    {
        ArrayList fieldList = new ArrayList();
        ArrayList valueList = new ArrayList();
        Element search = parent; //Xbuilder.addEl(parent, "cb-search", null );
        if ( request.getParameter("fname")!= null &&
            !request.getParameter("fname").equals("")){
            fieldList.add("first_name");
            valueList.add(request.getParameter("fname"));
            search.setAttribute("fname", request.getParameter("fname").toLowerCase());
        }
        if ( request.getParameter("sname")!= null &&
            !request.getParameter("sname").equals("")){
            fieldList.add("second_name");
            valueList.add(request.getParameter("sname"));
            search.setAttribute("sname", request.getParameter("sname").toLowerCase());
        }
        if ( request.getParameter("email")!= null &&
            !request.getParameter("email").equals("")){
            fieldList.add("email");
            valueList.add(request.getParameter("email"));
            search.setAttribute("email", request.getParameter("email").toLowerCase());
        }
        if ( request.getParameter("dep")!= null &&
            !request.getParameter("dep").equals("")){
            fieldList.add("department");
            valueList.add(request.getParameter("dep"));
            search.setAttribute("dep", request.getParameter("dep").toLowerCase());
        }
        if ( request.getParameter("job")!= null &&
            !request.getParameter("job").equals("")){
            fieldList.add("job_title");
            valueList.add(request.getParameter("job"));
            search.setAttribute("job", request.getParameter("job").toLowerCase());
        }
        if (fieldList.size()>0){
            String [][] args = new String[fieldList.size()][2];
            for (int i=0; i<fieldList.size(); i++){
                args[i][0]=(String)fieldList.get(i);
                args[i][1]=(String)valueList.get(i);
            }
            ContactXmlBuilder.searchContacts(conn, search, args, order);
        }
    }
}
