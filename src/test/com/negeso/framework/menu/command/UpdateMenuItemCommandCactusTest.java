/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.menu.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Document;
import com.negeso.CommandTestCase;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.DBHelper;

/**
 * 
 * @TODO
 * 
 * @author		Vadim Mishchenko
 * @version		$Revision: $
 *
 */
public class UpdateMenuItemCommandCactusTest extends CommandTestCase{
	@Override
	public void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		command = new UpdateMenuItemCommand();
	}
	
	public void testForSuccessResult(){		
		prepareUpdate();
		ResponseContext responseContext  = command.execute();
		Document document = (Document)responseContext.getResultMap().get(UpdateMenuItemCommand.OUTPUT_XML);
		try {
			validateXML(document, "update-menu-item-command.xml");
		} catch (Exception e) {
			fail("Unexpected exception while validation xml");
		}
		assertEquals(UpdateMenuItemCommand.RESULT_SUCCESS, responseContext.getResultName());
		/*validateDB(); @TODO:	Stupid test*/
	}
	
	public void testForError(){
		//if move == null
		command.setRequestContext(requestContext);
		ResponseContext responseContext = command.execute();
		assertEquals(UpdateMenuItemCommand.RESULT_FAILURE, responseContext.getResultName());
		assertEquals("Ordinal number is undefined",responseContext.getResultMap().get(UpdateMenuItemCommand.OUTPUT_ERROR)); 
	}
	private void prepareUpdate(){		
		requestContext.setParameter("move", "0");
		requestContext.setParameter("id", "42");
		requestContext.setParameter("title", "test");
		requestContext.setParameter("link", "contact_book_en.html");			
		command.setRequestContext(requestContext);
	}

    private void validateDB(){
		Connection conn = null;
		Statement  st = null;
		String queryTest = "SELECT menu_item.title FROM menu_item where menu_item.id = 42;";
		String queryRB = "UPDATE menu_item SET title = 'Over Negeso' WHERE id = 42;";
		try {
			conn = DBHelper.getConnection();
			conn.setAutoCommit(false);
			st = conn.createStatement();
			ResultSet rst1 = st.executeQuery(queryTest);
			if(rst1.next()){
                assertEquals("test",rst1.getString(1));
			}else{
				fail("Query failed");
			}
			st.executeUpdate(queryRB);
			conn.commit();
		} catch (SQLException e) {
			DBHelper.rollback(conn);
			fail("SQLException occured while query: "+e.getMessage());
		}
		try {
			conn.close();
		} catch (SQLException e) {
			fail("SQLException occured while closing connection"+e.getMessage());
		}		

	}

}
