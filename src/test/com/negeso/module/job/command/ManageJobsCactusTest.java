/*
 * @(#)$Id: ManageJobsCactusTest.java,v 1.1, 2006-11-20 19:19:12Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.job.command;

import java.sql.Connection;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.XMLtoHardDiskWriter;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.module.job.domain.Department;
/*import com.negeso.module.newsletter.domain.Request;*/

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 2$
 *
 */
public class ManageJobsCactusTest extends CommandTestCase {
	
	private static final String ROOTPATH = "/*[local-name()='page']";
	
	private static final String ATTR_VIEW_PATH = "/@view";
	
	private static final String ACT_DEP_DETAILS = "department_details";
	private static final String ACT_DEP_ADD = "add_department";
	private static final String ACT_DEP_SAVE = "save_department";
	private static final String ACT_DEP_DELETE = "delete_department";
	private static final String ACT_DEP_VACANCIES = "department_vacancies";
	
	private static final String ACT_VAC_DELETE = "delete_vacancy";
	private static final String ACT_VAC_ADD = "add_vacancy";
	private static final String ACT_VAC_SAVE = "save_vacancy";
	private static final String ACT_VAC_DETAILS = "vacancy_details";

	private static final String ACT_APPF = "application_form";
	private static final String ACT_APPF_ADD = "application_form_add_field";
	private static final String ACT_APPF_MOVE = "application_form_move_field";
	private static final String ACT_APPF_SAVE = "save_application_form";
	private static final String ACT_APPF_DELETE = "application_form_delete_field";

	private static final String ACT_APP_DELETE = "delete_applicant";
	private static final String ACT_APP_DETAILS = "application_form_add_field";
	private static final String ACT_APP_STATUS = "application_form_move_field";
	
	
	private Command command;
	
	public ManageJobsCactusTest() {
		super();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new ManageJobs();
		command.setRequestContext(requestContext);
		
	}

	
	/*
	 * test access denied
	 */
	public void testAccessDenied() throws Exception{
		requestContext.getSession().setAttribute(SessionData.USER_ATTR_NAME, getGuestUser());
		
		ResponseContext responseContext = command.execute();
		assertEquals(AbstractCommand.RESULT_ACCESS_DENIED, responseContext.getResultName());
	}
	
	
	/*
	 * test no action parameter
	 */
	public void testNoActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("department-list", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals(ManageJobs.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	/*
	 * test 'departament details' parameter
	 */
	public void beginDepDetailsActionParameter(WebRequest request){
		request.addParameter("action", ACT_DEP_DETAILS);
	}
	
	public void testDepDetailsActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("department-details", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals("department", responseContext.getResultName());
	}
	
	
	/*
	 * test 'departament add' parameter
	 */
	public void beginDepAddActionParameter(WebRequest request){
		request.addParameter("action", ACT_DEP_ADD);
	}
	
	public void testDepAddActionParameter() throws Exception{
		
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("department-details", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals("department", responseContext.getResultName());

		Long departmentId = requestContext.getLong(ManageJobs.INPUT_DEPARTMENT_ID);
		assertNotNull(departmentId);

		// remove added test department = quickly solution
		// TODO improve this solution
		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
	        Department dept = Department.findById(conn, departmentId);
	        dept.delete(conn);
		} catch (Exception e) {
			fail("Unexpected exception happened: " + e);
		} finally {
			DBHelper.close(conn);
		}
	}
	
	/*
	 * test 'departament vacancies' parameter
	 */
	public void beginDepVacanciesActionParameter(WebRequest request){
		request.addParameter("action", ACT_DEP_VACANCIES);
	}
	
	public void testDepVacanciesActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("department-vacancies", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals(ManageJobs.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	
	
	/*
	 * test 'vacancies delete' parameter
	 */
	public void beginVacDeleteActionParameter(WebRequest request){
		request.addParameter("action", ACT_VAC_DELETE);
	}
	
	public void testVacDeleteActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("department-vacancies", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals(ManageJobs.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	
	
	/*
	 * test 'vacancies add' parameter
	 */
	public void beginVacAddActionParameter(WebRequest request){
		request.addParameter("action", ACT_VAC_ADD);
	}
	
	public void testVacAddActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("vacancy-details", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals("vacancy", responseContext.getResultName());
	}
	
	
	
	/*
	 * test 'vacancies save' parameter
	 */
	public void beginVacSaveActionParameter(WebRequest request){
		request.addParameter("action", ACT_VAC_SAVE);
	}
	
	public void testVacSaveActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("department-vacancies", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals(ManageJobs.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	
	/*
	 * test 'vacancies details' parameter
	 */
	public void beginVacDetailsActionParameter(WebRequest request){
		request.addParameter("action", ACT_VAC_SAVE);
	}
	
	public void testVacDetailsActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("department-vacancies", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals(ManageJobs.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	

	public void beginAppfActionParameter(WebRequest request){
		request.addParameter("action", ACT_APPF);
	}
	
	public void testAppfActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("application-form", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals("application_form", responseContext.getResultName());
	}
	
	
	
	public void beginAppfAddActionParameter(WebRequest request){
		request.addParameter("action", ACT_APPF_ADD);
	}
	
	public void testAppfAddActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("application-form", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals("application_form", responseContext.getResultName());
	}
	
	
	
	public void beginAppfMoveActionParameter(WebRequest request){
		request.addParameter("action", ACT_APPF_MOVE);
	}
	
	public void testAppfMoveActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("application-form", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals("application_form", responseContext.getResultName());
	}
	
	
	
	public void beginAppfSaveActionParameter(WebRequest request){
		request.addParameter("action", ACT_APPF_SAVE);
	}
	
	public void testAppfSaveActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("application-form", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals("application_form", responseContext.getResultName());
	}
	
	
	
	public void beginAppfDeleteActionParameter(WebRequest request){
		request.addParameter("action", ACT_APPF_DELETE);
	}
	
	public void testAppfDeleteActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("application-form", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals("application_form", responseContext.getResultName());
	}
	
	
	public void beginAppDeleteActionParameter(WebRequest request){
		request.addParameter("action", ACT_APP_DELETE);
	}
	
	public void testAppDeleteActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("department-vacancies", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals(ManageJobs.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	
	
	public void beginAppDetailsActionParameter(WebRequest request){
		request.addParameter("action", ACT_APP_DETAILS);
	}
	
	public void testAppDetailsActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("application-form", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals("application_form", responseContext.getResultName());
	}
	
	
	
	public void beginAppStatusActionParameterIsDelete(WebRequest request){
		request.addParameter("action", ACT_APP_DETAILS);
		request.addParameter("is_delete", "not null");
	}
	
	public void testAppStatusActionParameter() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("department-list", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals(ManageJobs.RESULT_SUCCESS, responseContext.getResultName());
	}
	
	
	
	public void beginAppStatusActionParameterWithoutIsDelete(WebRequest request){
		request.addParameter("action", ACT_APP_DETAILS);
	}
	
	public void testAppStatusActionParameterWithoutIsDelete() throws Exception{
		ResponseContext responseContext = command.execute();
		
		Document doc = (Document) responseContext.getResultMap().get(ManageJobs.OUTPUT_XML);
		
		XMLAssert.assertXpathExists(ROOTPATH, doc);
		XMLAssert.assertXpathExists(ROOTPATH + ATTR_VIEW_PATH, doc);
		XMLAssert.assertXpathEvaluatesTo("application-form", ROOTPATH + ATTR_VIEW_PATH, doc);
		
		assertEquals("application_form", responseContext.getResultName());
	}
	private User getGuestUser(){
		User user = new User();
		user.setType("guest");
		return user;
	}
}
