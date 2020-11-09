/*
 * @(#)$Id: ApplicationActions.java,v 1.8, 2005-06-06 13:04:07Z, Stanislav Demchenko$
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
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.job.JobModule;
import com.negeso.module.job.domain.Application;
import com.negeso.module.job.domain.DVA;
import com.negeso.module.job.domain.ExtraField;
import com.negeso.module.job.domain.Vacancy;
import com.negeso.module.job.generator.ApplicationXmlBuilder;

/**
 *
 * Application actions and XML renderers
 * 
 * @version		$Revision: 9$
 * @author		Olexiy Strashko
 * 
 */
public class ApplicationActions {
    private static Logger logger = Logger.getLogger( ApplicationActions.class );

    /**
     * Set application status
     * 
     * @param con
     * @param request
     * @throws CriticalException 
     */
    static void doSetApplicationStatus(Connection con, RequestContext request) 
        throws CriticalException 
    {
        Long dvaId = request.getLong( ManageJobs.INPUT_DVA_ID );
        if ( dvaId == null ){
            logger.error("!!! ERROR,  dva_id is nULL");
            return;
        }
        
        String newStatus = request.getNonblankParameter( "new_status" );
        if ( newStatus == null ){
            logger.error("!!! ERROR,  status is nULL");
            return;
        }
        logger.info("Status: " + newStatus);

        DVA dva = DVA.findById( con, dvaId );
        logger.info("DVAId: " + dvaId + " deptId:" + dva.getDepartmentId());
        if ("delete".equals(newStatus)){
            ApplicationActions.doRemoveApplication(con, request);
            request.setParameter("is_delete", "true");
            return;
        }
        
        String oldStatus = dva.getApplicationStatus();
        if ( JobModule.APPLICATION_STATUS_HIRED.equals(newStatus) ){
            if ( !newStatus.equals(oldStatus) ){
                Vacancy vacancy = Vacancy.findById(con, dva.getVacancyId() );
                if( vacancy.getNeeded() != null ){
                    if ( vacancy.getNeeded() > 0 ){
                        logger.info("hired dec vacancy: " + vacancy.getTitle());
                        vacancy.setNeeded( vacancy.getNeeded() - 1 );
                        vacancy.update(con);
                    }
                }
            }
        }

        if ( JobModule.APPLICATION_STATUS_HIRED.equals(oldStatus) ){
            if ( JobModule.APPLICATION_STATUS_REJECTED.equals(newStatus) ){
                Vacancy vacancy = Vacancy.findById(con, dva.getVacancyId() );
                if( vacancy.getNeeded() != null ){
                    logger.info("rejected inc vacancy: " + vacancy.getTitle());
                    vacancy.setNeeded( vacancy.getNeeded() + 1 );
                    vacancy.update(con);
                }
            }
        }
        
        if ( !oldStatus.equals(newStatus) ) {
            dva.setApplicationStatus(newStatus);
            dva.update(con);
            logger.info("Afted status: " + dva.getApplicationStatus());
        }
    }

    /**
     * Remove application
     * 
     * @param con
     * @param request
     * @throws CriticalException 
     */
    static void doRemoveApplication(Connection con, RequestContext request) 
        throws CriticalException 
    {
        Long dvaId = request.getLong( ManageJobs.INPUT_DVA_ID );
        if ( dvaId == null ){
            logger.error("!!! ERROR,  dva_id is nULL");
            return;
        }
        
        DVA dva = DVA.findById( con, dvaId );
        request.setParameter(
            ManageJobs.INPUT_DEPARTMENT_ID, dva.getDepartmentId().toString()
        );
        if ( !dva.hasMoreApplicationLinks(con) ) {
            Application appl = dva.getApplication(con);
            if ( appl != null ){
                appl.delete(con);
            }
        }
        dva.delete( con );
        request.setParameter( 
            ManageJobs.INPUT_DEPARTMENT_ID, dva.getDepartmentId().toString() 
        );
    }

    /**
     * 
     * 
     * @param con
     * @param request
     * @param page
     * @throws CriticalException 
     */
    static void renderApplicationDetails(
        Connection con, RequestContext request, Element page) 
        throws CriticalException 
    {
        Long dvaId = request.getLong("dva_id");
        if ( dvaId == null ){
            logger.error("ERROR!!! dvaId is null");
            return;
        }

        DVA dva = DVA.findById(con, request.getLong("dva_id") );
        if ( dva == null ){
            logger.error("ERROR!!! dvaId not found by id:" + dvaId);
            return;
        }

        Long applicationId = dva.getApplicationId();
        if ( applicationId == null ){
            logger.error("!!! ERROR, application_id in DVA is nULL");
            return;
        }

        Element applEl = ApplicationXmlBuilder.buildApplication(
            con, 
            page, 
            dva.getApplicationId(), 
            request.getSession().getLanguage().getId() 
        );

        Xbuilder.setAttr( applEl, "department_id", dva.getDepartmentId() );
        Xbuilder.setAttr( page, "department_id", dva.getDepartmentId() );
        Xbuilder.setAttr( applEl, "vacancy_id", dva.getVacancyId() );
        Xbuilder.setAttr( applEl, "dva_id", dva.getId() );
        Xbuilder.setAttr( applEl, "status", dva.getApplicationStatus() );
        
        if ( JobModule.APPLICATION_STATUS_NEW.equals(dva.getApplicationStatus()) ){
            dva.setApplicationStatus(JobModule.APPLICATION_STATUS_WAITING);
            dva.update(con);
        }
    }

    /**
     * @param con
     * @param request
     * @param page
     * @throws CriticalException 
     */
    static void renderApplicationForm(
        Connection con, RequestContext request, Element page) 
        throws CriticalException 
    {
        Long vacancyId = request.getLong("vacancy_id");
        if ( vacancyId == null ){
            logger.error("ERROR!!! vacancyId is null");
            return;
        }
        
        ApplicationXmlBuilder.buildVacancyApplicationForm(
            con, page, vacancyId, request.getSession().getLanguage().getId()
        );

        if ( JobModule.get().getGeneralVacancy(con).getId().equals( vacancyId ) ){
            Xbuilder.setAttr(page, "is_general_form", "true");
        }
        else{
            Xbuilder.setAttr(page, "is_general_form", "false");
        }
        
        if ( request.getNonblankParameter("department_id") == null){
            Vacancy vacancy = Vacancy.findById(con, vacancyId);
            if ( vacancy == null ){
                logger.error("ERROR!!! vacancy not found by Id:" + vacancyId);
                return;
            }
            Xbuilder.setAttr( page, "department_id", vacancy.getDepartmentId() );
        }
        else{
            Xbuilder.setAttr(
                page, "department_id", request.getNonblankParameter("department_id")
            );
        }
    }

    /**
     * @param con
     * @param request
     * @throws CriticalException 
     */
    public static void doRemoveField(Connection con, RequestContext request)
        throws CriticalException 
    {
        Long fieldId = request.getLong("field_id");
        if ( fieldId == null ){
            logger.error("ERROR!!! fieldId is null");
            return;
        }
        ExtraField field = ExtraField.findById(con, fieldId);
        if ( field.hasDependentValues(con) ){
            field.setRemoved(true);
            field.update(con);
            logger.info("marked as removed field:" + field.getId());
        }
        else{
            field.delete(con);
            logger.info("deleted field:" + field.getId());
        }
    }
    
    /**
     * @param con
     * @param request
     * @throws CriticalException 
     */
    public static void doAddField(Connection con, RequestContext request) 
        throws CriticalException 
    {
        Long typeId = request.getLong("field_type_id");
        if ( typeId == null ){
            logger.error("ERROR!!! typeId is null");
            return;
        }
        Long vacancyId = request.getLong("vacancy_id");
        if ( vacancyId == null ){
            logger.error("ERROR!!! vacancyId is null");
            return;
        }
        
        ExtraField field = new ExtraField();
        field.setTypeId(typeId);
        field.setVacancyId(vacancyId);
        field.insert(con);
    }

    /**
     * @param con
     * @param request
     * @throws CriticalException 
     */
    public static void doSaveApplicationForm(Connection con, RequestContext request) 
        throws CriticalException 
    {
        Long[] isRequired = request.getLongs("is_required");
        if ( isRequired == null ){
            logger.error("ERROR!!! isRequired is null");
            return;
        }

        Long vacancyId = request.getLong("vacancy_id");
        logger.info("vacancyId:" + vacancyId);
        if ( vacancyId == null ){
            logger.error("ERROR!!! vacancyId is null");
            return;
        }
        Vacancy vac = Vacancy.findById(con, vacancyId);
        if (vac == null){
            logger.error("Vacancy not found by id:" + vacancyId);
            return;
        }
        boolean found = false;
        for (ExtraField field: vac.getFields(con)){
            found = false;
            for (Long id: isRequired){
                logger.info("req:" + id);
                if ( id.equals(field.getId()) ) {
                    found = true;
                    break;
                }
            }
            if ( found ){
                field.setRequired(true);
            }
            else{
                field.setRequired(false);
            }
            field.update(con);
        }
        
        if ( isRequired == null ){
            logger.error("ERROR!!! isRequired is null");
            return;
        }
    }

	/**
	 * 
	 * @param con
	 * @param request
	 * @throws CriticalException 
	 */
	public static void doMoveField(Connection con, RequestContext request) 
		throws CriticalException 
	{
        Long fieldId = request.getLong("field_id");
        if ( fieldId == null ){
            logger.error("ERROR!!! fieldId is null");
            return;
        }
        ExtraField field = ExtraField.findById(con, fieldId);
        if ( "down".equals( request.getString("direction", null) ) ){
            field.moveDown(con);
        }
        else{
            field.moveUp(con);
        }
	}
}
