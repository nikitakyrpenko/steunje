/*
 * @(#)Id: ManageVisitorsCommandCactusTest.java, 31.07.2007 17:55:42, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.user.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.PreparedDatabaseLauncher;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;
import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class ManageVisitorsCommandCactusTest extends CommandTestCase{
	
	//user id - not administrator (access denied), must be in DB
	private static final int USER_ID_NOT_ADMIN = 3;

	//user id - administrator, must be in DB
	private static final int USER_ID_ADMIN = 1;

	private static final long TEST_USER_ID = 99999;

	private static final String TEST_USER_USERNAME = "Dimaz";
	
	private static final String TEST_USER_USERNAME_NEW = "Dimaz will be back! ;)";

	private static final String TEST_USER_LOGIN = "Dimaz";

	private static final String TEST_USER_PASS = "pass";
	
	private static final String TEST_USER_PASS_NEW = "new pass ;)";

	private static final String TEST_USER_TYPE = "visitor";

	private static final String TEST_GROUP_ROLE_ID = "visitor";

	private static final long TEST_GROUP_ID = 9999;

	private static final String TEST_GROUP_NAME = "testGroup";

	private static final Date TEST_USER_EXPIRED_DATE = new Date(2453454);

	private static final Date TEST_USER_PUBLISH_DATE = new Date(2341234);

	private ManageVisitorsCommand command;
	private SessionData session;
	
	public ManageVisitorsCommandCactusTest(){
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new ManageVisitorsCommand();
		command.setRequestContext(requestContext);
	}
	
	
	/*
	 * if session with user who is not administrator
	 */
	public void testAccessDenied() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_NOT_ADMIN);
		
		ResponseContext responseContext = command.execute();
		
		assertEquals(ManageVisitorsCommand.RESULT_ACCESS_DENIED, responseContext.getResultName());
	}
	
	
	
	/*
	 * if session with user who is administrator, but without action parameter(by default, it must list_users())
	 */
	public void beginNoActionParameter(WebRequest request) throws Exception {
//		request.addParameter(ManageVisitorsCommand.INPUT_ACTION, "wrong");
	}
	
	public void testNoActionParameter() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create testDB visitor user
		createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("list","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']", document);
		XMLAssert.assertXpathEvaluatesTo(String.valueOf(TEST_USER_ID),"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']/@id", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_LOGIN,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']/@login", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_USERNAME,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']/@name", document);
	
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		
		deleteDBUserById(TEST_USER_ID);
	}
	
	private void createDBVisitorUser() throws Exception{
		Connection conn = DBHelper.getConnection();
		
		String query = "INSERT INTO user_list " +
							"(id,username,login,password,type,publish_date,expired_date,site_id)"+
							"VALUES (?,?,?,?,?,?,?,1);";
		
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setLong(1, TEST_USER_ID);
		ps.setString(2, TEST_USER_USERNAME);
		ps.setString(3, TEST_USER_LOGIN);
		ps.setString(4, TEST_USER_PASS);
		ps.setString(5, TEST_USER_TYPE);
		ps.setDate(6, TEST_USER_PUBLISH_DATE);
		ps.setDate(7, TEST_USER_EXPIRED_DATE);
		
		
		ps.execute();
		ps.close();
		conn.close();
	}
	
	private void deleteDBUserById(Long id) throws Exception{
		Connection conn = DBHelper.getConnection();
		
		String query = "DELETE FROM user_list WHERE user_list.id = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setLong(1, id);
		
		ps.execute();
		ps.close();
		conn.close();
	}
	
	private void deleteDBUserByLogin(String login) throws Exception{
		Connection conn = DBHelper.getConnection();
		
		String query = "DELETE FROM user_list WHERE user_list.login = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setString(1, login);
		
		ps.execute();
		ps.close();
		conn.close();
	}
	
	
	
	/*
	 * if session with user who is administrator with action = 'add'
	 */
	public void beginAddActionParameter(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorsCommand.INPUT_ACTION, "add");
	}
	
	public void testAddActionParameter() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create testDB visitor user
		createDBVisitorGroup();
		
		ResponseContext responseContext = command.execute();
		
		final Document document = (Document) responseContext.getResultMap().get("xml");
		PreparedDatabaseLauncher pdl = new PreparedDatabaseLauncher() {
			@Override
			protected void assertTest() throws Exception {
				XMLAssert.assertXpathExists("/*[local-name()='page']", document);
				XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
				XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);
		
				XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
				XMLAssert.assertXpathEvaluatesTo("add","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
				
				XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']", document);
				XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']/*[local-name()='groups']", document);
				XMLAssert.assertXpathEvaluatesTo(
						String.valueOf(TEST_GROUP_ID),
						"/*[local-name()='page']" +
						"/*[local-name()='contents']" +
						"/*[local-name()='user']" +
						"/*[local-name()='groups']" +
						"/*[local-name()='group']" +
						"/@id", 
						document);
				XMLAssert.assertXpathEvaluatesTo(TEST_GROUP_NAME,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']/*[local-name()='groups']/*[local-name()='group']/@name", document);
				XMLAssert.assertXpathEvaluatesTo(TEST_GROUP_ROLE_ID,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']/*[local-name()='groups']/*[local-name()='group']/@role-id", document);
				
			}
		};
		pdl.runTestAndClean("DELETE FROM groups WHERE groups.id =" + TEST_GROUP_ID);
	
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
			
	}
	
	private void createDBVisitorGroup() throws Exception{
		Connection conn = DBHelper.getConnection();
		
		String query = "INSERT INTO groups " +
							"(id, name, role_id,site_id)"+
							"VALUES (?, ?, ?, 1);";
		
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setLong(1, TEST_GROUP_ID);
		ps.setString(2, TEST_GROUP_NAME);
		ps.setString(3, TEST_GROUP_ROLE_ID);
		
		ps.execute();
		ps.close();
		conn.close();
	}
	
	


	/*
	 * if session with action = 'edit'
	 */
	public void beginEditActionParameter(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorsCommand.INPUT_ACTION, "edit");
		request.addParameter(ManageVisitorsCommand.INPUT_ID, String.valueOf(TEST_USER_ID));
	}
	
	public void testEditActionParameter() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create testDB visitor user
		createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("edit","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']", document);
		XMLAssert.assertXpathEvaluatesTo(String.valueOf(TEST_USER_ID),"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']/@id", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_LOGIN,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']/@login", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_USERNAME,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']/@name", document);
	
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		
		deleteDBUserById(TEST_USER_ID);	
	}
	
	
	/*
	 * if session with action = 'edit_password'
	 */
	public void beginEditPasswordActionParameter(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorsCommand.INPUT_ACTION, "edit_password");
		request.addParameter(ManageVisitorsCommand.INPUT_ID, String.valueOf(TEST_USER_ID));
	}
	
	public void testEditPasswordActionParameter() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create testDB visitor user
		createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("edit_password","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']", document);
		XMLAssert.assertXpathEvaluatesTo(String.valueOf(TEST_USER_ID),"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']/@id", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_LOGIN,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']/@login", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_USERNAME,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']/@name", document);
	
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		
		deleteDBUserById(TEST_USER_ID);
	}
	
	
	
	/*
	 * if session with action = 'save', we specify password parameter, it must be updated
	 */
	public void beginSaveActionParameterCheckUpdatePassword(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorsCommand.INPUT_ACTION, "save");
		request.addParameter(ManageVisitorsCommand.INPUT_ID, String.valueOf(TEST_USER_ID));
		request.addParameter(ManageVisitorsCommand.INPUT_PASSWORD, TEST_USER_PASS_NEW);
	}
	
	public void testSaveActionParameterCheckUpdatePassword() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create testDB visitor user
		createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("save","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']", document);
		XMLAssert.assertXpathEvaluatesTo(String.valueOf(TEST_USER_ID),"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']/@id", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_LOGIN,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']/@login", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_USERNAME,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']/@name", document);
	
		validatePassword();
		
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		
		deleteDBUserById(TEST_USER_ID);
	}
	
	private void validatePassword() throws Exception {
		Connection conn = DBHelper.getConnection();
		
		String query = "select user_list.password from user_list where user_list.id = ?";
		
		PreparedStatement ps = conn.prepareStatement(query);
		
		ps.setLong(1, TEST_USER_ID);
		
		ResultSet rs = ps.executeQuery();
		
		String pass = "";
		
		while(rs.next()){
			pass = rs.getString(1);
		}
		
		assertEquals(TEST_USER_PASS_NEW, pass);
		
		rs.close();
		ps.close();
		conn.close();
	}
	
	/*
	 * if session with action = 'save', we don't specify password parameter, wait for updating for user_name
	 */
	public void beginSaveActionParameterCheckUpdateUserName(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorsCommand.INPUT_ACTION, "save");
		request.addParameter(ManageVisitorsCommand.INPUT_ID, String.valueOf(TEST_USER_ID));
		request.addParameter(ManageVisitorsCommand.INPUT_NAME, String.valueOf(TEST_USER_USERNAME_NEW));
		request.addParameter(ManageVisitorsCommand.INPUT_LOGIN, String.valueOf(TEST_USER_LOGIN));
//		request.addParameter(ManageVisitorsCommand.INPUT_PASSWORD, TEST_USER_PASS_NEW);
	}
	
	public void testSaveActionParameterCheckUpdateUserName() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//create testDB visitor user
		createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("save","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']", document);
		XMLAssert.assertXpathEvaluatesTo(String.valueOf(TEST_USER_ID),"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']/@id", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_LOGIN,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']/@login", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_USERNAME_NEW,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']/@name", document);
	
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		
		deleteDBUserById(TEST_USER_ID);
	}
	
	
	
	/*
	 * if session with action = 'save', we don't specify password parameter and id, if user in DB, we will get
	 * User with this login already exists, otherwise - new user will be created
	 */
	public void beginSaveActionParameterNewUserInsert(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorsCommand.INPUT_ACTION, "save");
		request.addParameter(ManageVisitorsCommand.INPUT_NAME, String.valueOf(TEST_USER_USERNAME));
		request.addParameter(ManageVisitorsCommand.INPUT_LOGIN, String.valueOf(TEST_USER_LOGIN));
		request.addParameter(ManageVisitorsCommand.INPUT_PASSWORD, TEST_USER_PASS);
	}
	
	public void testSaveActionParameterNewUserInsert() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//we don't create testDB visitor user
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
				
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("save","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_LOGIN,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']/@login", document);
		XMLAssert.assertXpathEvaluatesTo(TEST_USER_USERNAME,"/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']/*[local-name()='user']/@name", document);
	
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		
		deleteDBUserByLogin(TEST_USER_LOGIN);
	}
	
	
	
	/*
	 * if session with action = 'save', we don't specify password parameter and id, if user in DB, we will get
	 * User with this login already exists, otherwise - new user will be created
	 */
	public void beginSaveActionParameterUserExists(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorsCommand.INPUT_ACTION, "save");
		request.addParameter(ManageVisitorsCommand.INPUT_NAME, String.valueOf(TEST_USER_USERNAME));
		request.addParameter(ManageVisitorsCommand.INPUT_LOGIN, String.valueOf(TEST_USER_LOGIN));
		request.addParameter(ManageVisitorsCommand.INPUT_PASSWORD, TEST_USER_PASS);
	}
	
	public void testSaveActionParameterUserExists() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//we specially create testDB visitor user
		createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		XMLAssert.assertXpathEvaluatesTo("save","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("User with this login already exists","/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='user']", document);
	
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
		
		deleteDBUserById(TEST_USER_ID);
	}
	
	
	
	/*
	 * if session with action = 'delete', but don't specify user_id, so we wait for warning Cannot delete the user with id # 
	 */
	public void beginDeleteActionWithNoId(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorsCommand.INPUT_ACTION, "delete");
	}
	
	public void testDeleteActionWithNoId() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//we don't create testDB visitor user
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		XMLAssert.assertXpathEvaluatesTo("delete","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("Cannot delete the user with id # null","/*[local-name()='page']/*[local-name()='context']/*[local-name()='error']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']", document);
	
		assertEquals(ManageVisitorsCommand.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	
	
	/*
	 * if session with action = 'delete', and we specify user_id, so user must be deleted 
	 */
	public void beginDeleteActionWithId(WebRequest request) throws Exception {
		request.addParameter(ManageVisitorsCommand.INPUT_ACTION, "delete");
		request.addParameter(ManageVisitorsCommand.INPUT_ID, String.valueOf(TEST_USER_ID));
	}
	
	public void testDeleteActionWithId() throws Exception {
		session = SessionData.getSessionData(request);
		session.setUserId(USER_ID_ADMIN);
		
		//we create testDB visitor user
		createDBVisitorUser();
		
		ResponseContext responseContext = command.execute();
		
		Document document = (Document) responseContext.getResultMap().get("xml");
		
		XMLAssert.assertXpathExists("/*[local-name()='page']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']", document);
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']", document);

		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		XMLAssert.assertXpathEvaluatesTo("delete","/*[local-name()='page']/*[local-name()='context']/*[local-name()='action']", document);
		
		XMLAssert.assertXpathExists("/*[local-name()='page']/*[local-name()='contents']/*[local-name()='users']", document);
	
		assertEquals(
				ManageVisitorsCommand.RESULT_SUCCESS, 
				responseContext.getResultName());
		
		assertTrue(userDeleted());
	}
	
	private boolean userDeleted() throws Exception{
		Connection conn = DBHelper.getConnection();
		
		String query = "select user_list.password from user_list " +
				"where user_list.id = ?";
		
		PreparedStatement ps = conn.prepareStatement(
				query, 
				ResultSet.TYPE_SCROLL_INSENSITIVE, 
				ResultSet.CONCUR_UPDATABLE);
		
		ps.setLong(1, TEST_USER_ID);
		
		ResultSet rs = ps.executeQuery();
		
		boolean returnValue = rs.first();
		
		rs.close();
		ps.close();
		conn.close();
		
		return !returnValue;
	}
	
}
