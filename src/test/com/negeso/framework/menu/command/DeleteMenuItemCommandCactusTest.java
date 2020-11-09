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

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.internal.CactusTestCase;

import com.meterware.httpunit.WebRequest;
import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.RequestContext;
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
public class DeleteMenuItemCommandCactusTest extends CommandTestCase {
	
	Connection conn= null;
	Statement st = null;
	public DeleteMenuItemCommandCactusTest (){
		super();
	}
	
	@Override
	public void setUp() {
		try {
			super.setUp();
            deleteFakeRecord();
        } catch (Exception e) {
			fail("Exception occured");
		}
		command = new DeleteMenuItemCommand();
	}
	
	public void testSuccess(){
        validateDB();
        prepareRequestContext(0);
        addFakeRecordToDB(false);
        ResponseContext responseContext = null;
		responseContext = command.execute();
        validateDB();
        assertEquals(Command.RESULT_SUCCESS, responseContext.getResultName());
    }
	
	public void testFailurewhenUserCannnotManage(){
		validateDB();
        prepareRequestContext(1);
		addFakeRecordToDB(false);
		ResponseContext responseContext = null;		
		responseContext = command.execute();
		deleteFakeRecord();
		assertEquals("Cannot delete the menu item. "+"You are not authorized for this operation",
				responseContext.getResultMap().get(DeleteMenuItemCommand.OUTPUT_ERROR));
		assertEquals(Command.RESULT_FAILURE, responseContext.getResultName());
	}
	public void testFailurewhenPageIsNODELETE(){
		prepareRequestContext(3);
        addFakeRecordToDB(true);
		ResponseContext responseContext = null;		
		responseContext = command.execute();
		deleteFakeRecord();
		assertEquals("Cannot delete the menu item. "+"Page is protected from deletion",
				responseContext.getResultMap().get(DeleteMenuItemCommand.OUTPUT_ERROR));
		assertEquals(Command.RESULT_FAILURE, responseContext.getResultName());
	}

	
	private void prepareRequestContext(int testId){
		setUp();
		requestContext.setParameter("id","333");
		
		if (testId == 1){
		//TODO add some test user to DB
			requestContext.getSession().setUserId(3);
		}
		command.setRequestContext(requestContext);
	}
	private void validateDB(){
		String queryMenu = " SELECT count(id) FROM menu where id = 333";
		String queryMenu_item = " SELECT count(id) FROM menu_item where id = 333";
		String queryPage = " SELECT count(id) FROM page where id = 333";
		

		try {
			conn = DBHelper.getConnection();
			conn.setAutoCommit(false);
			st = conn.createStatement();
			ResultSet rst1 = st.executeQuery(queryMenu);
			if(rst1.next()){
                assertEquals(0,rst1.getInt(1));
			}else{
				fail("Query failed");
			}
			ResultSet rst2 = st.executeQuery(queryMenu_item);
			if(rst2.next()){
                assertEquals(0,rst2.getInt(1));
			}else{
				fail("Query failed");
			}
			ResultSet rst3 = st.executeQuery(queryPage);
			if(rst3.next()){
                assertEquals(0,rst3.getInt(1));
			}else{
				fail("Query failed");
			}
		} catch (SQLException e) {
			DBHelper.rollback(conn);
			fail("SQLException occured");
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			fail("SQLException occured");
		}		

	}
	
	private void deleteFakeRecord(){
		String delete = "DELETE from menu_item where id = 333;" +
						"DELETE FROM page WHERE id = 333;" +
						"DELETE FROM menu WHERE id = 333;";
		try {
			conn = DBHelper.getConnection();
			conn.setAutoCommit(false);
			st = conn.createStatement();
			st.executeUpdate(delete);
			conn.commit();
		} catch (SQLException e) {
			DBHelper.rollback(conn);
			/*fail("SQLException occured");*/
		}

		try {
			conn.close();
		} catch (SQLException e) {
			fail("SQLException occured");
		}		
	}

    //@TODO: wrong test. duplicate key violates unique constraint "menu_pkey"
    private void addFakeRecordToDB(boolean pageNoDelete){
		String insertToMenu = "INSERT into menu(id,level,site_id) " +
							  "VALUES (333,2,1);";

        String noDelete = pageNoDelete?"'nodelete'":null;
        String insertToPage = "INSERT INTO page(id,lang_id,filename,visible,container_id,site_id,protected)" +
							  "VALUES (333,1,'filename.html',TRUE,1,1,"+ noDelete +");";
		
		String insertToMenu_item = "INSERT INTO menu_item(id,menu_id,title,link,link_type) " +
									"VALUES (333,333,'title','filename.html','page');";
		try {
			conn = DBHelper.getConnection();
			conn.setAutoCommit(false);
			st = conn.createStatement();
			st.executeUpdate(insertToMenu);
			st.executeUpdate(insertToPage);
			st.executeUpdate(insertToMenu_item);
			conn.commit();
		} catch (SQLException e) {
			DBHelper.rollback(conn);
			fail("SQLException occured" + e.getMessage());
        }
		try {
			conn.close();
		} catch (SQLException e) {
			fail("SQLException occured" + e.getMessage());
		}
	}

}
