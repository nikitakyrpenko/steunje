/*
 * @(#)$Id: VacancyActions.java,v 1.5, 2005-06-06 13:05:17Z, Stanislav Demchenko$
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
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.module.job.JobModule;
import com.negeso.module.job.LanguagePresence;
import com.negeso.module.job.domain.Vacancy;
import com.negeso.module.job.generator.DepartmentXmlBuilder;
import com.negeso.module.job.generator.VacancyXmlBuilder;

/**
 *
 * Vacancy actions and XML renderers
 * 
 * @version     $Revision: 6$
 * @author      Olexiy Strashko
 * 
 */
public class VacancyActions {
    private static Logger logger = Logger.getLogger( VacancyActions.class );

    /**
     * Render vacancies for department
     * 
     * @param con
     * @param request
     * @param page
     * @param langId
     * @throws CriticalException
     */
    static void renderVacancies( 
        Connection con, RequestContext request, Element page, int langId ) 
        throws CriticalException
    {
        Long departmentId = request.getLong( ManageJobs.INPUT_DEPARTMENT_ID );
        if ( departmentId == null ){
            logger.error("!!! ERROR, department_id is nULL");
        }
        
        Element department = DepartmentXmlBuilder.buildDepartmentDetails(
            con, page, departmentId, langId
        );

        VacancyXmlBuilder.buildAdminDepartmentVacancyApplications(
            con, department, departmentId, new Long(langId) 
        );
        
        VacancyXmlBuilder.buildAdminGeneralDepartmentApplications(
            con, department, departmentId, new Long(langId) 
        );
    }

    
    /**
     * Remove vacancy
     * 
     * @param con
     * @param request
     * @throws CriticalException 
     */
    static void doRemoveVacancy(Connection con, RequestContext request) 
        throws CriticalException 
    {
        Long vacancyId = request.getLong( ManageJobs.INPUT_VACANCY_ID );
        if ( vacancyId == null ){
            logger.error("!!! ERROR, vacancy_id is nULL");
            return;
        }
        Vacancy vacancy = Vacancy.findById( con, vacancyId );
        request.setParameter(
            ManageJobs.INPUT_DEPARTMENT_ID, vacancy.getDepartmentId().toString()
        );
        if ( vacancy.getArticleId() != null ){
            try{
                Article art = Article.findById(vacancy.getArticleId());
                art.delete();
            }
            catch(ObjectNotFoundException e){
                logger.error("vacancy article not foung!", e);
            }
        }
        vacancy.delete( con );
    }

    
    /**
     * @param con
     * @param request
     * @param page
     * @param langId
     */
    static void doAddVacancy(
        Connection con, RequestContext request)
        throws CriticalException 
    {
        Long departmentId = request.getLong( ManageJobs.INPUT_DEPARTMENT_ID );
        if ( departmentId == null ){
            logger.error("!!! ERROR, department_id is nULL");
            return;
        }

        Vacancy vacancy = JobModule.get().newVacancy(con, departmentId);
        request.setParameter(
            ManageJobs.INPUT_VACANCY_ID, vacancy.getId().toString()
        );
    }

    /**
     * Save department
     * 
     * @param con
     * @param request
     * @throws CriticalException 
     */
    static void doSaveVacancy(Connection con, RequestContext request) 
        throws CriticalException 
    {
        Long vacancyId = request.getLong( ManageJobs.INPUT_VACANCY_ID );
        if ( vacancyId == null ){
            logger.error("!!! ERROR, vacancy_id is nULL");
            return;
        }
        Vacancy vacancy = Vacancy.findById(con, vacancyId);
        if ( vacancy == null ){
            logger.error("!!! ERROR, vacancy_id not found by Id:" + vacancyId);
            return;
        }
        
        vacancy.setTitle( request.getNonblankParameter("title") );
        vacancy.setPosition( request.getNonblankParameter("position") );
        vacancy.setNeeded( request.getLong("person_needed") );
        vacancy.setSalary( request.getNonblankParameter("salary") );
        vacancy.setPublishDate( JobModule.parseDate(request.getNonblankParameter("publish_date")) );
        vacancy.setExpireDate(  JobModule.parseDate(request.getNonblankParameter("expired_date")) );
        vacancy.setRegionId(request.getLong("region_id") );
        vacancy.update(con);
        doVacancyLanguagePresenceUpdate(con, request);
    }

    private static void doVacancyLanguagePresenceUpdate(Connection con, RequestContext request) {
    	logger.debug("+");		
    	LanguagePresence languagePresence = new LanguagePresence(
        		"job_vac2lang_presence", "j_vac_id"
        	);
    	LanguagePresence.setDoLanguageAdd( false );
    	languagePresence.updateOrAddLanguagePresence(con, request, request.getLong( ManageJobs.INPUT_VACANCY_ID ), "");
    	logger.debug("-");
    }

    /**
     * @param con
     * @param request
     * @param page
     * @throws CriticalException 
     */
    static void renderVacancyDetails(Connection con, RequestContext request, Element page) 
        throws CriticalException 
    {
        Long vacancyId = request.getLong( ManageJobs.INPUT_VACANCY_ID );
        if ( vacancyId == null ){
            logger.error("!!! ERROR, vacancy_id is nULL");
        }
        
        VacancyXmlBuilder.buildVacancyDetails(
            con, page, vacancyId, request.getSession().getLanguage().getId(), true
        );
        //this.addLanguage(request, page);
    }
    
	public static void doVacancyLanguagePresenceAdd(Connection con, Long id , Long depId) {
		logger.debug("+");		
		LanguagePresence languagePresence = new LanguagePresence(
        		"job_vac2lang_presence", "j_vac_id"
        	);
		LanguagePresence languagePresenceForDep = new LanguagePresence(
        		"job_dep2lang_presence", "j_dep_id"
        	);
		Map<Long, Boolean> map =languagePresenceForDep.getPresenceMap(con, depId);
		Iterator i = map.keySet().iterator();
		try{
			for (; i.hasNext();){
				Long curLangId = (Long) i.next();
				if (map.get(curLangId)){
					languagePresence.doPresenceInsert(con, true, id, curLangId.intValue());
				}
			}		
		}catch (SQLException e) {
			logger.error("Error: " + e);
		}		
		logger.debug("-");
	}
    
}
