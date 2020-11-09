/*
 * @(#)$Id: DepartmentActions.java,v 1.3, 2005-06-06 13:04:22Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.job.command;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.module.job.JobModule;
import com.negeso.module.job.domain.Department;
import com.negeso.module.job.generator.DepartmentXmlBuilder;
import com.negeso.module.job.generator.VacancyXmlBuilder;
import com.negeso.module.job.LanguagePresence;

/**
 *
 * Department actions and XML renderers
 * 
 * @version		$Revision: 4$
 * @author		Olexiy Strashko
 * 
 */
public class DepartmentActions {
    private static Logger logger = Logger.getLogger( DepartmentActions.class );

    /**
     * Render add department 
     * 
     * @param con
     * @param request
     * @param page
     * @param langId
     * @throws CriticalException
     */
    static void doAddDepartment(
        Connection con, RequestContext request, Element page, int langId ) 
        throws CriticalException
    {
        Department dept = JobModule.get().newDepartment(con);
        request.setParameter(ManageJobs.INPUT_DEPARTMENT_ID, dept.getId().toString());
    }

    /**
     * Save department
     * 
     * @param con
     * @param request
     * @throws CriticalException 
     */
    static void doSaveDepartment(Connection con, RequestContext request) 
        throws CriticalException 
    {
        Long departmentId = request.getLong( ManageJobs.INPUT_DEPARTMENT_ID );
        logger.info( departmentId );
        Department dept = null;
        if ( departmentId == null ){
            dept = new Department(); 
        }
        else{
            dept = Department.findById( con, departmentId );
        }
        dept.setDescription( request.getNonblankParameter("description"));
        dept.setEmail( request.getNonblankParameter("email"));
        dept.setTitle( request.getNonblankParameter("title"));
        if ( dept.getId() == null ){
            dept.insert( con );
        }
        else{
            dept.update(con);
        }
        doDepartmentLanguagePresenceUpdate(con, request, dept.getId());
    }

    /**
     * Remove department
     * 
     * @param con
     * @param request
     * @param page
     * @param langId
     * @throws CriticalException 
     */
    static void doRemoveDepartment(
        Connection con, RequestContext request) 
        throws CriticalException 
    {
        Long departmentId = request.getLong( ManageJobs.INPUT_DEPARTMENT_ID );
        if ( departmentId == null ){
            logger.error("!!! ERROR, department_id is nULL");
        }
        Department dept = Department.findById( con, departmentId );
        dept.delete( con );
    }

    /**
     * Render department details
     * 
     * @param con
     * @param request
     * @param page
     * @throws CriticalException
     */
    static void renderDepartmentDetails(
        Connection con, RequestContext request, Element page, int langId ) 
        throws CriticalException
    {
        Long deptId = request.getLong( ManageJobs.INPUT_DEPARTMENT_ID );
        DepartmentXmlBuilder.buildDepartmentDetails(con, page, deptId, langId);
        //this.addLanguage(request, page);
    }

    /**
     * Render departments
     * 
     * @param con
     * @param request
     * @param page
     * @param langId
     * @throws CriticalException
     */
    static void renderDepartments( 
        Connection con, RequestContext request, Element page, int langId )
        throws CriticalException
    {
        DepartmentXmlBuilder.buildBriefAdminDepartments(con, page, langId); 
        VacancyXmlBuilder.buildAdminGeneralDepartmentApplications(
            con, 
            page, 
            JobModule.get().getGeneralDepartmentId(), 
            new Long(langId) 
        );
    }

    
    private static void doDepartmentLanguagePresenceUpdate(Connection con, RequestContext request, Long targetId) {
    	logger.debug("+");			
    	LanguagePresence languagePresence = new LanguagePresence(
        		"job_dep2lang_presence", "j_dep_id"
        	);
    	LanguagePresence.setDoLanguageAdd( false );
    	languagePresence.updateOrAddLanguagePresence(con, request, targetId, "");
    	logger.debug("-");
    }


    public static void doDepartmentLanguagePresenceAdd(Connection con, Long id) {
    	logger.debug("+");			
    	LanguagePresence languagePresence = new LanguagePresence(
        		"job_dep2lang_presence", "j_dep_id"
        	);
    	LanguagePresence.setDoLanguageAdd(true);
    	languagePresence.updateOrAddLanguagePresence(con, null, id, "");
    	logger.debug("-");
    }
    
}
