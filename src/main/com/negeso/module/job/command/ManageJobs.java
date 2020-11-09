/*
 * @(#)$Id: ManageJobs.java,v 1.6, 2005-06-06 13:04:49Z, Stanislav Demchenko$
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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.Timer;
import com.negeso.module.job.service.RegionService;

/**
 *
 * ManageDepartments command
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 7$
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ManageJobs extends AbstractCommand {
	private static Logger logger = Logger.getLogger( ManageJobs.class );

    private static final String RESULT_DEPARTMENT = "department";
    private static final String RESULT_VACANCY = "vacancy";
    private static final String RESULT_APPLICANT = "applicant";
    private static final String RESULT_APPLICATION_FORM = "application_form";
    

    static final String INPUT_DEPARTMENT_ID = "department_id";
    static final String INPUT_VACANCY_ID = "vacancy_id";
    static final String INPUT_APPLICATION_ID = "application_id";
    static final String INPUT_DVA_ID = "dva_id";
    
    
	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
    @ActiveModuleRequired
	public ResponseContext execute() {
		logger.debug("+");
		RequestContext request = this.getRequestContext();
		ResponseContext response = new ResponseContext();
		
		if ( !SecurityGuard.isContributor(request.getSession().getUser()) ){
			response.setResultName( AbstractCommand.RESULT_ACCESS_DENIED );
			return response;
		}
		
		Timer timer = new Timer();
		timer.start();
		
		int langId = request.getSession().getLanguage().getId().intValue();
		Connection con = null;
		String action = request.getNonblankParameter( "action" );
		logger.info("action: " + action);
		Element page = null;
		try{
			page = XmlHelper.createPageElement( request );
			con = DBHelper.getConnection();
            // DEPARTMENTS
			if ( "department_details".equals(action) ){
				DepartmentActions.renderDepartmentDetails(con, request, page, langId);
				Xbuilder.setAttr(page, "view", "department-details");
                response.setResultName(RESULT_DEPARTMENT);
			}
			else if( "add_department".equals(action) ){
                DepartmentActions.doAddDepartment(con, request, page, langId);
                DepartmentActions.renderDepartmentDetails(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "department-details");
                response.setResultName(RESULT_DEPARTMENT);
			}
			else if( "save_department".equals(action) ){
                DepartmentActions.doSaveDepartment(con, request);
                DepartmentActions.renderDepartments(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "department-list");
                response.setResultName(RESULT_SUCCESS);
			}
			else if( "delete_department".equals(action) ){
                DepartmentActions.doRemoveDepartment(con, request);
                DepartmentActions.renderDepartments(con, request, page, langId);
				Xbuilder.setAttr(page, "view", "department-list");
                response.setResultName(RESULT_SUCCESS);
			}
            else if( "department_vacancies".equals(action) ){
                VacancyActions.renderVacancies(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "department-vacancies");
                response.setResultName(RESULT_SUCCESS);
            }
            
            // VACANCIES
            else if( "delete_vacancy".equals(action) ){
                VacancyActions.doRemoveVacancy(con, request);
                VacancyActions.renderVacancies(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "department-vacancies");
                response.setResultName(RESULT_SUCCESS);
            }
            else if( "add_vacancy".equals(action) ){
                VacancyActions.doAddVacancy(con, request);
                VacancyActions.renderVacancyDetails(con, request, page);
                Xbuilder.setAttr(page, "view", "vacancy-details");
                response.setResultName(RESULT_VACANCY);
            }
            else if( "save_vacancy".equals(action) ){
                VacancyActions.doSaveVacancy(con, request); 
                VacancyActions.renderVacancies(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "department-vacancies");
                response.setResultName(RESULT_SUCCESS);
            }
            else if( "vacancy_details".equals(action) ){
                VacancyActions.renderVacancyDetails(con, request, page);
                Xbuilder.setAttr(page, "view", "vacancy-details");
                RegionService.getInstance().buildRegionsListXml(page);
                response.setResultName(RESULT_VACANCY);
            }
            
            // APPLICATION FORM
            else if( "application_form".equals(action) ){
                ApplicationActions.renderApplicationForm(con, request, page);
                Xbuilder.setAttr(page, "view", "application-form");
                response.setResultName(RESULT_APPLICATION_FORM);
            }
            else if( "application_form_add_field".equals(action) ){
                ApplicationActions.doAddField(con, request);
                ApplicationActions.renderApplicationForm(con, request, page);
                Xbuilder.setAttr(page, "view", "application-form");
                response.setResultName(RESULT_APPLICATION_FORM);
            }
            else if( "application_form_delete_field".equals(action) ){
                ApplicationActions.doRemoveField(con, request);
                ApplicationActions.renderApplicationForm(con, request, page);
                Xbuilder.setAttr(page, "view", "application-form");
                response.setResultName(RESULT_APPLICATION_FORM);
            }
            else if( "application_form_move_field".equals(action) ){
                ApplicationActions.doMoveField(con, request);
                ApplicationActions.renderApplicationForm(con, request, page);
                Xbuilder.setAttr(page, "view", "application-form");
                response.setResultName(RESULT_APPLICATION_FORM);
            }
            else if( "save_application_form".equals(action) ){
                ApplicationActions.doSaveApplicationForm(con, request);
                ApplicationActions.renderApplicationForm(con, request, page);
                Xbuilder.setAttr(page, "view", "application-form");
                response.setResultName(RESULT_APPLICATION_FORM);
            }

            // APPLICATIONS
            else if( "delete_applicant".equals(action) ){
                ApplicationActions.doRemoveApplication(con, request);
                VacancyActions.renderVacancies(con, request, page, langId);
                Xbuilder.setAttr(page, "view", "department-vacancies");
                response.setResultName(RESULT_SUCCESS);
            }
            else if( "applicant_details".equals(action) ){
                ApplicationActions.renderApplicationDetails(con, request, page);
                Xbuilder.setAttr(page, "view", "applicant-details");
                response.setResultName(RESULT_APPLICANT);
            }
            else if( "set_applicant_status".equals(action) ){
                ApplicationActions.doSetApplicationStatus(con, request);
                if ( request.getParameter("is_delete") != null ){
                    VacancyActions.renderVacancies(con, request, page, langId);
                    Xbuilder.setAttr(page, "view", "department-vacancies");
                    response.setResultName(RESULT_SUCCESS);
                }
                else{
                    ApplicationActions.renderApplicationDetails(con, request, page);
                    Xbuilder.setAttr(page, "view", "applicant-details");
                    response.setResultName(RESULT_APPLICANT);
                }
            }
			else{
				DepartmentActions.renderDepartments(con, request, page, langId);
				Xbuilder.setAttr(page, "view", "department-list");
                response.setResultName(RESULT_SUCCESS);
			}
		}
		catch( SQLException e ){
			logger.error("-error", e);
			response.setResultName(RESULT_FAILURE);
		}
		catch( CriticalException e ){
			logger.error("-error", e);
			response.setResultName(RESULT_FAILURE);
		}
		finally{
			DBHelper.close(con);
		}
		
		if ( page != null ){
			response.getResultMap().put( OUTPUT_XML, page.getOwnerDocument() );
		}
		logger.info("time:" + timer.stop());
		
		logger.debug("-");
		return response;
	}


	@Override
	public String getModuleName() {
		return ModuleConstants.JOB_MODULE;
	}
	
}
