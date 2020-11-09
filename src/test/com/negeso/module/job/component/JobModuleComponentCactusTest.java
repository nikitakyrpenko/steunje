/*
 * @(#)$Id: JobModuleComponentCactusTest.java,v 1.3, 2006-12-11 09:16:43Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.job.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.PageComponentTestCase;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.page.PageComponent;
import com.negeso.module.job.domain.Department;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 4$
 *
 */
public class JobModuleComponentCactusTest extends PageComponentTestCase {

	private PageComponent pageComponent;
	
	private List<Map<String, String>> departments;

	public JobModuleComponentCactusTest() {
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		pageComponent = new JobModuleComponent();
		fillDepartments();
		
	}
	
	private void fillDepartments(){
		PreparedStatement stmt = null;
		Connection con;
		departments = new ArrayList<Map<String, String>>();
        try{
        	con = DBHelper.getConnection();
            stmt = con.prepareStatement(Department.selectVisitorDepartmentsSql);
            int langId = requestContext.getSession().getLanguage().getId().intValue();
            stmt.setLong(1, langId);
            stmt.setLong(2, langId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
            	Map<String, String> dep = new HashMap<String, String>();
            	dep.put("id", String.valueOf(rs.getLong("id")));
            	dep.put("title", rs.getString("title"));
            	dep.put("description", rs.getString("description"));
            	dep.put("vacancies", String.valueOf(rs.getLong("vacancy_count")));
            	departments.add(dep);
            }
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
	}
	
	private Document getDocument(Element element) {
		   Document document = element.getOwnerDocument();
		   document.appendChild(element);
		   return document;
		}

	private Document getOutputDocument(RequestContext request, Map parameters) throws TransformerException, XpathException {
		Document document = getDocument(getElement(pageComponent));
		// place common validations here
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='job_module_component'])", document);
		return document;
	}
	
	private Document getParameterizedByModeStartDocument(
			String mode, 
			String start) 
			throws TransformerException, 
				   XpathException {
		requestContext.setParameter("mode", mode);		
		getParameters().clear();
		if (start == null) {
			// nothing
		} else {
			getParameters().put("start", start);
		}
		return getOutputDocument(requestContext,getParameters()); 
	}
	
	public void testModeNullstartNull() throws TransformerException, XpathException {
		Document document = getParameterizedByModeStartDocument(null, null);
		XMLAssert.assertXpathEvaluatesTo("", "/*[local-name()='job_module_component']/@start_page", document);		
	}

	public void testModeNullstartDepartmentsView() throws TransformerException, XpathException {
		Document document = getParameterizedByModeStartDocument(null, "DepartmentsView");
		XMLAssert.assertXpathEvaluatesTo("", "/*[local-name()='job_module_component']/@start_page", document);
	}
	
	public void testModeNullstartAllVacanciesView() throws TransformerException, XpathException {
		Document document = getParameterizedByModeStartDocument(null, "AllVacanciesView"); 
		XMLAssert.assertXpathEvaluatesTo("AllVacanciesView", "/*[local-name()='job_module_component']/@start_page", document);		
	}
	
	public void testModeNullstartGeneralApplicationForm() throws TransformerException, XpathException {
		Document document = getParameterizedByModeStartDocument(null, "GeneralApplicationForm");		
		XMLAssert.assertXpathEvaluatesTo("GeneralApplicationForm", "/*[local-name()='job_module_component']/@start_page", document);		
	}
	
	public void testModeEmptystartNull() throws TransformerException, XpathException {
		Document document = getParameterizedByModeStartDocument("", null); 
		XMLAssert.assertXpathEvaluatesTo("", "/*[local-name()='job_module_component']/@start_page", document);		
	}
	 
	public void testModeEmptystartDepartmentsView() throws TransformerException, XpathException {
		Document document = getParameterizedByModeStartDocument("", "DepartmentsView");
		XMLAssert.assertXpathEvaluatesTo("", "/*[local-name()='job_module_component']/@start_page", document);
	}
	
	public void testModeEmptystartAllVacanciesView() throws TransformerException, XpathException {
		Document document = getParameterizedByModeStartDocument("", "AllVacanciesView");
		XMLAssert.assertXpathEvaluatesTo("AllVacanciesView", "/*[local-name()='job_module_component']/@start_page", document);
	}

	public void testModeEmptystartGeneralApplicationForm() throws TransformerException, XpathException {
		Document document = getParameterizedByModeStartDocument("", "AllVacanciesView");
		XMLAssert.assertXpathEvaluatesTo(
				"AllVacanciesView", 
				"/*[local-name()='job_module_component']" +
				"/@start_page", 
				document);
	}

	public void validateGeneralModeXML(Document document) throws TransformerException, XpathException {
		XMLAssert.assertXpathEvaluatesTo(
				"general", 
				"/*[local-name()='job_module_component']" +
				"/@mode", 
				document);
		
		XMLAssert.assertXpathEvaluatesTo(
				"1", 
				"count(/*[local-name()='job_module_component']" +
				"/*[local-name()='departments'])", 
				document);
		
		XMLAssert.assertXpathEvaluatesTo(
				String.valueOf(departments.size()), 
				"count(/*[local-name()='job_module_component']" +
				"/*[local-name()='departments']" +
				"/*[local-name()='department'])", 
				document);
		for (int i = 0;i< departments.size();i++){
			XMLAssert.assertXpathEvaluatesTo(
					String.valueOf(departments.get(i).get("id")), 
					"/*[local-name()='job_module_component']" +
					"/*[local-name()='departments']" +
					"/*[local-name()='department'][" + (i + 1) + "]/@id", 
					document);
			
			XMLAssert.assertXpathEvaluatesTo(
					departments.get(i).get("title"), 
					"/*[local-name()='job_module_component']" +
					"/*[local-name()='departments']" +
					"/*[local-name()='department'][" + (i + 1) + "]/@title", 
					document);
			
			XMLAssert.assertXpathExists(
					"/*[local-name()='job_module_component']" +
					"/*[local-name()='departments']" +
					"/*[local-name()='department'][" + (i + 1) + "]/@description", 
					document);
			
			XMLAssert.assertXpathEvaluatesTo(
					departments.get(i).get("vacancies"), 
					"/*[local-name()='job_module_component']" +
					"/*[local-name()='departments']" +
					"/*[local-name()='department'][" + (i + 1) + "]/@vacancies", 
					document);
		}
	}
	
	public void testGeneralModeWhenModeIsNull() throws TransformerException, XpathException {
		validateGeneralModeXML(
				getParameterizedByModeStartDocument(null, null)
				);
	}

	public void testGeneralModeWhenModeIsGeneral() throws TransformerException, XpathException {
		validateGeneralModeXML(
				getParameterizedByModeStartDocument("general", null)
				);
	}
	
	public void testGeneralModeWhenModeGeneral() throws TransformerException, XpathException {
		validateGeneralModeXML(
				getParameterizedByModeStartDocument("", null)
				);
	}

	public void testErrorWhenDepartmentNotFoundById() throws TransformerException, XpathException {
		getParameters().clear();
		requestContext.setParameter("mode", "department_vacancies");
		Document document =	getOutputDocument(requestContext,getParameters());
		XMLAssert.assertXpathEvaluatesTo("error", "/*[local-name()='job_module_component']/@mode", document);	
	}
	
 	public void testDepartmentVacanciesMode() throws TransformerException, XpathException {
		getParameters().clear();
		requestContext.setParameter("dep_id", "1");
		requestContext.setParameter("mode", "department_vacancies");
		validateDepartmentVacanciesXML(getOutputDocument(requestContext,getParameters()));
	}
	
	private void validateDepartmentVacanciesXML(Document document) throws TransformerException, XpathException {
		XMLAssert.assertXpathEvaluatesTo("department_vacancies", "/*[local-name()='job_module_component']/@mode", document);

		XMLAssert.assertXpathEvaluatesTo("Company", "/*[local-name()='job_module_component']/@department", document);
		
		XMLAssert.assertXpathEvaluatesTo("1", "/*[local-name()='job_module_component']/@dep_id", document);
		
		XMLAssert.assertXpathEvaluatesTo("1", "count(/*[local-name()='job_module_component']/*[local-name()='vacancy_list'])", document);
	}

}
