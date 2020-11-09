/*
 * @(#)$Id: ContactXmlBuilder.java,v 1.14, 2007-01-09 18:40:52Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.contact_book.generators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Contact;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.contact_book.domain.Group;
import com.negeso.module.contact_book.Configuration;

/**
 *
 * Contact xml builder
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 15$
 */
public class ContactXmlBuilder {
    private static Logger logger = Logger.getLogger( ContactXmlBuilder.class );

    private final static String selectFromWhereContactWithGroupSql = 
        " SELECT contact.id, first_name, second_name, company_name, address_line, zip_code, " +
        " state, city, country, phone, fax, type, web_link, email, image_link, " +
        " department, job_title, birth_date, nickname, " +
        " cb_group.title AS group_title, cb_group.id AS group_id " +
        " FROM contact " +
        " LEFT JOIN cb_cont_group ON contact.id=cb_cont_group.contact_id " +
        " LEFT JOIN cb_group ON cb_cont_group.group_id=cb_group.id " + 
        " WHERE type='" + Configuration.getContactType() + "' "
    ;
	/**
	 * Build contacts for group. Contacts are ordered by orderBy field.
	 * If order by = null 
	 * 
	 * @param con
	 * @param contact
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static Element buildGroupContacts(
		Connection con, Element parent, Long groupId, String orderBy) 
		throws CriticalException
	{
		logger.debug("+");
        try{
            // build group element
            Group gr = Group.findById(con, groupId);
            Element groupEl = Xbuilder.addEl(parent, "cb-group", null);
            Xbuilder.setAttr(groupEl, "id", "" + gr.getId().longValue());
            Xbuilder.setAttr(groupEl, "title", gr.getTitle());
            String order = " first_name, second_name ";
            if (orderBy!=null){
                order=orderBy;
            }
            PreparedStatement statement = con.prepareStatement(
            		selectFromWhereContactWithGroupSql + 
                    " AND (cb_cont_group.group_id=? AND contact.id=cb_cont_group.contact_id) " +
                    " ORDER BY " + order);
            statement.setLong(1, groupId.longValue());
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
            	Contact contactObj = new Contact();
            	contactObj.load(rs);
            	Element contactEl = contactObj.getElement(parent.getOwnerDocument());
                contactEl.setAttribute("group-title", getGroupNameByUserId(rs.getLong("id")));
                groupEl.appendChild(contactEl);
            }
    		logger.debug("-");
            return groupEl;
        }
        catch(Exception e){
            logger.error("- " + e.getMessage(),e);
            throw new CriticalException("Error while creating XML");
        }
	}
	
	/**
	 * Build contact xml 
	 * 
	 * @param con
	 * @param contact
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static void 	appendContactEl( 
		Connection con, Element parent, Long contactId) 
		throws CriticalException
	{
		logger.debug("+");
		Contact contactObj = Contact.findById(con, contactId);
		parent.appendChild(contactObj.getElement(parent.getOwnerDocument()));
		logger.debug("-");
	}
	
	/**
	 * Search contacts. Indexed fields:
	 * - first name;
	 * - second name;
	 * - email;
	 * - department;
	 * - job-title;
	 * 
	 * i.e. first_name LIKE '%searchString%' AND second_name .....
	 * 
	 * @param con
	 * @param contact
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static void 	buildAllContacts( 
		Connection con, Element parent, String orderBy) 
		throws CriticalException
	{
		logger.debug("+");
		try{
            // build group element
            Element resultsEl = Xbuilder.addEl(parent, "cb-search-results", null);

            PreparedStatement stat = con.prepareStatement(
            		selectFromWhereContactWithGroupSql +
					" ORDER BY " + orderBy
            );
            ResultSet rs = stat.executeQuery();

            while(rs.next()){
            	resultsEl.appendChild(buildContactWithGroupEl(resultsEl, rs));
            }
        }
        catch (SQLException e){
            logger.error(e.getMessage(), e);
            throw new CriticalException("Error while search");
        }
		logger.debug("-");
	}

	
	/**
	 * Search contacts. Indexed fields:
	 * - first name;
	 * - second name;
	 * - email;
	 * - department;
	 * - job-title;
	 * 
	 * i.e. first_name LIKE '%searchString%' AND second_name .....
	 * 
	 * @param con
	 * @param contact
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static void 	searchContacts( 
		Connection con, Element parent, String searchString, String orderBy) 
		throws CriticalException
	{
		logger.debug("+");
		try{
            // build group element
            Element resultsEl = Xbuilder.addEl(parent, "cb-search-results", null);

            PreparedStatement stat = con.prepareStatement(
            		selectFromWhereContactWithGroupSql +
                    " AND (first_name iLike ? OR second_name iLike ? " +
                    " OR email iLike ? OR department iLike ? OR job_title iLike ?) " +
					" ORDER BY " + orderBy
            );
            
            List foundItems = new ArrayList();
            String[] words = searchString.split(" ");
            for(int i=0; i<words.length; i++) {
            	logger.debug("# ["+i+"] = "+ words[i]);
            	String searchWorld = "%" + words[i].replace('*', '%') + "%";	
                stat.setString(1, searchWorld);
                stat.setString(2, searchWorld);
                stat.setString(3, searchWorld);
                stat.setString(4, searchWorld);
                stat.setString(5, searchWorld);
                ResultSet rs = stat.executeQuery();
                while(rs.next()){
                	if(!foundItems.contains(rs.getLong("id"))) {
                		resultsEl.appendChild(buildContactWithGroupEl(resultsEl, rs));
	                    foundItems.add(rs.getLong("id"));
                	}
                }
            }
        }
        catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new CriticalException(e);
        }
		logger.debug("-");
	}

	/**
	 * Search contacts. Indexed fields are described by input search array.
	 * Array looks like:
	 *  'first_name', 'jo',
	 *  'department', 'prog',
	 *  'email', 'negeso'
	 * 	
	 * i.e. first_name LIKE '%jo%' AND departmen LIKE .....
	 * 
	 * @param con
	 * @param contact
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static void 	searchContacts( 
			Connection con, Element parent, String[][] searchArray, String orderBy) 
			throws CriticalException
	{
		logger.debug("+");
		try{
            // build group element
            Element resultsEl = Xbuilder.addEl(parent, "cb-search-results", null);
            String sql = 
            	selectFromWhereContactWithGroupSql;
            
            if (searchArray.length>0){
            	sql+= " AND ( " + searchArray[0][0] + " iLike ? ";
                for ( int i=1; i<searchArray.length; i++ ){
                	sql+= " OR " + searchArray[i][0] + " iLike ? ";
                }
                sql+= " ) ";
            }
            sql += " ORDER BY " + orderBy;

            PreparedStatement stat = con.prepareStatement(sql);
            for (int i=0; i<searchArray.length; i++){
                stat.setString(i+1, searchArray[i][1].replace('*', '%'));
            }

            ResultSet rs = stat.executeQuery();
            // build contacts
            while(rs.next()){
            	resultsEl.appendChild(buildContactWithGroupEl(resultsEl, rs));
            }
        }
        catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new CriticalException("Error while search");
        }
		logger.debug("-");
	}
	
	/**
	 * Search contacts by date delay. 
	 * 
	 * WHERE ( now() < birth_day ) AND ( birth_day < remindDelay ) 
	 * 
	 * @param con
	 * @param parent
	 * @param remindDelay
	 * @throws CriticalException
	 */
	public static void searchContactsByDate( 
			Connection con, Element parent, int startDate, int endDate)
			throws CriticalException
	{
		logger.debug("+");
        try{
            // build group element
            Element resultsEl = Xbuilder.addEl(parent, "cb-search-results", null);

            PreparedStatement stat =
                    con.prepareStatement(Contact.getFindByDateSql(startDate, endDate));
            stat.setInt(1, startDate);
            stat.setInt(2, endDate);
            ResultSet rs = stat.executeQuery();
            // build contacts
            while(rs.next()){
            	Contact contactObj = new Contact();
            	contactObj.load(rs);
            	resultsEl.appendChild(contactObj.getElement(resultsEl.getOwnerDocument()));
            }
        }
        catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new CriticalException("Error while search");
        }
		logger.debug("-");
	}
   
	/**
	 * Build contact by ResultSet
	 * @param parent
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private static Element buildContactWithGroupEl(Element parent, ResultSet rs) 
		throws SQLException
	{
		logger.debug("+");
		Contact contactObj = new Contact();
		contactObj.load(rs);
		Element contactEl = contactObj.getElement(parent.getOwnerDocument());
		contactEl.setAttribute("group-id", "" + rs.getLong("group_id"));
		contactEl.setAttribute("group-title", rs.getString("group_title"));
		logger.debug("-");
        return contactEl;
	}

	public static String getGroupNameByUserId(Long userId) throws SQLException {
		logger.debug("+");
		String group = "";
		Connection con = null;
		try{
			con = DBHelper.getConnection();
			PreparedStatement stat = con.prepareStatement(
		               " SELECT cb_group.title " +
		               " FROM cb_group, cb_cont_group" +
		               " WHERE cb_cont_group.contact_id=? " +
		               " AND cb_cont_group.group_id=cb_group.id "	           
		            );
			stat.setLong(1, userId);
			ResultSet res = stat.executeQuery();
			if (res.next()){
				group = res.getString("title");
			}
		}
		catch (Exception e){
			throw new SQLException("Can't open connection");
		}
		finally{
			DBHelper.close(con);
		}
		logger.debug("-");
		return group;
	}

}
