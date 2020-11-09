/*
 * @(#)$Id: GroupXmlBuilder.java,v 1.4, 2005-06-06 13:04:39Z, Stanislav Demchenko$
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

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.contact_book.domain.Group;

/**
 *
 * Group xml builder
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 5$
 */
public class GroupXmlBuilder {
    private static Logger logger = Logger.getLogger( GroupXmlBuilder.class );
	/**
	 * Build list of all groups. Order by title
	 * 
	 * @param con
	 * @param contact
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static void 	buildGroups(
		Connection con, Element parent) 
		throws CriticalException
	{
		try{
            Element groups = Xbuilder.addEl(parent, "cb-groups", null);
            Element group;
            PreparedStatement statement = con.prepareStatement(
                    Group.getGroupListSql());
            ResultSet result =  statement.executeQuery();
            while (result.next()){
                group = Xbuilder.addEl(groups, "cb-group", null);

                Xbuilder.setAttr(group, "id", result.getString("id"));
                Xbuilder.setAttr(group, "title", result.getString("title"));
                Xbuilder.setAttr(group, "contact-amount", result.getString("amount"));
            }
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new CriticalException("Error while creating cbGroups XML");
        }
	}
	
	/**
	 * Build group xml by id
	 * 
	 * @param con
	 * @param contact
	 * @param parent
	 * @param langId
	 * @throws CriticalException
	 */
	public static void 	buildGroup(
		Connection con, Element parent, Long groupId) 
		throws CriticalException
	{
		Element group= Xbuilder.addEl(parent, "cb-group", null);
		if ( groupId != null ){
			Group cbGroup = Group.findById(con, groupId);
			if ( cbGroup != null ){
		        Xbuilder.setAttr(group, "id", "" + cbGroup.getId().longValue());
				Xbuilder.setAttr(group, "title", cbGroup.getTitle());
			}
		}
	}
}
