/*
 * @(#)$Id: DepartmentXmlBuilder.java,v 1.9, 2005-06-06 13:04:23Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.job.generator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.job.JobModule;
import com.negeso.module.job.domain.Department;
import com.negeso.module.job.LanguagePresence;

/**
 *
 * Department Xml builder
 * 
 * @version		$Revision: 10$
 * @author		Olexiy Strashko
 * 
 */
public class DepartmentXmlBuilder {
    private static Logger logger = Logger.getLogger( DepartmentXmlBuilder.class );

    
    /**
     * Build departent details, if department id is null - render new 
     * department
     * 
     * @param con
     * @param parent
     * @param departmentId
     * @return
     * @throws CriticalException
     */
    public static Element buildDepartmentDetails(
        Connection con, Element parent, Long departmentId, int langId ) 
        throws CriticalException
    {
        logger.debug("+");
        Element departmentEl = Xbuilder.addEl( parent, "department", null );
        
        Department dept = null;
        if ( departmentId != null ){
            dept = Department.findById(con, departmentId);
        }
        else{
            dept = new Department();
        }
        
        if ( dept.getId() == null ){
            Xbuilder.setAttr(departmentEl, "new", "true");
        }
        else{
            Xbuilder.setAttr(departmentEl, "id", dept.getId());
        }
        Xbuilder.setAttr(departmentEl, "title", dept.getTitle());
        Xbuilder.setAttr(departmentEl, "email", dept.getEmail());
        Xbuilder.setAttr(departmentEl, "description", dept.getDescription());
        
        if (dept.getId() != null){
	        LanguagePresence languagePresence = new LanguagePresence(
	        		"job_dep2lang_presence", "j_dep_id"
	        	);
	        departmentEl.appendChild(
	        		languagePresence.getPresenceElement(
	        				con, 
	        				departmentEl,
	        				dept.getId())
	        	);
        }
        logger.debug("-");
        return departmentEl;
    }
    
    
    public static Element buildBriefAdminDepartments(
        Connection con, Element parent, int langId ) 
        throws CriticalException
    {
        logger.debug("+");
        Element departments = Xbuilder.addEl(parent, "departments", null);

        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement(Department.selectAdminDepartmentsSql);
            stmt.setLong(1, JobModule.get().getGeneralDepartmentId().longValue());
            ResultSet rs = stmt.executeQuery();
            Element deptEl = null;
            int tmp = 0;
            while (rs.next()){
                deptEl = buildBriefDepartment(rs, departments);
                Xbuilder.setAttr(deptEl, "email", rs.getString("email")); 
                Xbuilder.setAttr(deptEl, "vacancies", rs.getString("vacancy_count")); 
                Xbuilder.setAttr(deptEl, "applications", rs.getString("application_count"));
                tmp = rs.getInt("new_application_count");
                if ( tmp != 0 ){
                    Xbuilder.setAttr(deptEl, "new_applications", tmp);
                }
            }
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
        }
        logger.debug("-");
        return departments;
    }
    
    /**
     * Build brief department element
     * 
     * @param rs
     * @param parent
     * @return
     * @throws CriticalException
     */
    private static Element buildBriefDepartment(ResultSet rs, Element parent) 
        throws SQLException
    {
        Element department = Xbuilder.addEl(parent, "department", null);
        Xbuilder.setAttr(department, "id", "" + rs.getLong("id"));
        Xbuilder.setAttr(department, "title", rs.getString("title"));
        return department;
    }
    
    
    /**
     * Build visitor departments
     * 
     * @param con
     * @param parent
     * @return
     * @throws CriticalException
     */
    public static Element buildVisitorDepartments(
        Connection con, Element parent, int langId ) 
        throws CriticalException
    {
        logger.debug("+");
        
        Element departments = Xbuilder.addEl(parent, "departments", null);

        PreparedStatement stmt = null;
        try{
        	logger.debug("DB querry! selectVisitorDepartmentsSql = " + Department.selectVisitorDepartmentsSql);
            stmt = con.prepareStatement(Department.selectVisitorDepartmentsSql);
            stmt.setLong(1, langId);
            stmt.setLong(2, langId);
            ResultSet rs = stmt.executeQuery();
            Element deptEl = null;
            while (rs.next()){
                deptEl = buildBriefDepartment(rs, departments);
                Xbuilder.setAttr(deptEl, "description", rs.getString("description"));                 
                Xbuilder.setAttr(deptEl, "vacancies", rs.getString("vacancy_count")); 
            }
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
        }
        logger.debug("-");
        return departments;
    }
    
    /**
     * Build visitor departments
     * 
     * @param con
     * @param parent
     * @return
     * @throws CriticalException
     */
    public static Element buildGeneralVisitorVacancies(
        Connection con, Element parent, int langId ) 
        throws CriticalException
    {
        logger.debug("+");
        Element vacancies = Xbuilder.addEl(parent, "general-vacancies", null);
        
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement( Department.selectGeneralVisitorVacanciesSql );
            stmt.setLong(1, JobModule.get().getGeneralDepartmentId().longValue());
            stmt.setLong(2, langId);
            stmt.setLong(3, langId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                VacancyXmlBuilder.buildVacancy(rs, vacancies, true);
            }
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
        }
        
        logger.debug("-");
        return vacancies;
    }

    public static Element buildDepartmentsAndVacancies(Connection conn, Element parent, int langId )
            throws Exception{
        Element department_list = Xbuilder.addEl(parent, "department_list", null);
        PreparedStatement stat = conn.prepareStatement(Department.getSelectDepartmentsAndVacanciesSql());
        stat.setLong(1, langId);
        stat.setLong(2, langId);
        ResultSet res = stat.executeQuery();
        long prevDepId = -1;
        long curDepId = -1;
        long prevVacId = -1;
        long curVacId = -1;
        Element department = null;
        Element vacancy = null;
        while (res.next()){
            curDepId = res.getLong("id");
            if(curDepId!=prevDepId){//Adding new department
                department = Xbuilder.addEl(department_list, "department", null);
                department.setAttribute("id", ""+curDepId);
                department.setAttribute("title", res.getString("title"));
                department.setAttribute("vac_count", ""+res.getLong("vac_count"));
                prevDepId = curDepId;
            }
            curVacId = res.getLong("vac_id");
            if (curVacId!=prevVacId){
                vacancy = Xbuilder.addEl(department, "vacancy", null);
                vacancy.setAttribute("id", ""+curVacId);
                vacancy.setAttribute("title", res.getString("vac_title"));
                prevVacId = curVacId;
            }
        }
        return department_list;
    }

}
  
