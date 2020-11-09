/*
 * @(#)$Id: JobModule.java,v 1.12, 2006-04-25 20:06:19Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.job;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.job.command.DepartmentActions;
import com.negeso.module.job.command.VacancyActions;
import com.negeso.module.DefaultNameNumerator;
import com.negeso.module.job.domain.Department;
import com.negeso.module.job.domain.Vacancy;

/**
 *
 * Job module
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 13$
 */
public class JobModule {
    private static Logger logger = Logger.getLogger( JobModule.class );
    
    public static final String DEFAULT_LIST_ITEM_NAME_VACANCY = "New vacancy";
    public static final String DEFAULT_LIST_ITEM_NAME_DEPARTMENT = "New department";
    
    public static final String APPLICATION_MAIL_XSL = "J_APPLICATION_MAIL_TEMPL_XSL";

    public static final String APPLICATION_STATUS_NEW = "new";
    public static final String APPLICATION_STATUS_WAITING = "waiting";
    public static final String APPLICATION_STATUS_HIRED = "hired";
    public static final String APPLICATION_STATUS_REJECTED = "rejected";

    private static final Long GENERAL_DEPT_ID = new Long(1);
    private static final Long GENERAL_VAC_ID = new Long(1);
    private static final String DEFAULT_STRING_VALUE = "Noname";
    private static final String COMMON_FIELDS_TEMPLATE_NAME = "job_common_fields";
    
    private final static String getCommonTemplateSql = 
        " SELECT * FROM job_templates WHERE sys_name = ?"
    ;

    private Configuration config = null; 
    
    private static JobModule instance = null;

	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy"); 
	
    // cached
    private Department generalDepartment = null;
    private Vacancy generalVacancy = null;

    
	/**
	 * 
	 */
	public JobModule() {
		super();
	}

    public static JobModule get(){
        if ( JobModule.instance == null ){
            JobModule.instance = new JobModule();
        }
        return JobModule.instance;
    }
	
	public Long getGeneralDepartmentId(){
        return JobModule.GENERAL_DEPT_ID;
    }

    public Department getGeneralDepartment(Connection con) throws CriticalException{
        if ( this.generalDepartment == null ){
            this.generalDepartment = Department.findById(con, this.getGeneralDepartmentId());
        }
        return this.generalDepartment;
    }

    public Vacancy getGeneralVacancy(Connection con) throws CriticalException{
        if ( this.generalVacancy == null ){
            this.generalVacancy = Vacancy.findById(con, GENERAL_VAC_ID);
        }
        return this.generalVacancy;
    }

    public String getDefaultStringValue() {
        return JobModule.DEFAULT_STRING_VALUE;
    }


    public String getCommonTemplateName() {
        return JobModule.COMMON_FIELDS_TEMPLATE_NAME;
    }
    
    /**
     * Create new department
     * 
     * @param con
     * @param departmentId
     * @return
     * @throws CriticalException
     */
    public Department newDepartment(Connection con) 
        throws CriticalException
    {
    	logger.debug("+");
        Department gDept = this.getGeneralDepartment(con);
        Department dept = new Department();
        dept.setTitle( DefaultNameNumerator.getDefaultName(con,
						  getNumbersSqlDepartment(), 
		        		  DEFAULT_LIST_ITEM_NAME_DEPARTMENT
		        		  ) 
		);
        dept.setEmail( gDept.getEmail() );
        dept.insert(con);
        DepartmentActions.doDepartmentLanguagePresenceAdd(con, dept.getId());
        logger.debug("-");
        return dept;
    }

    /**
     * Create new vacancy
     * 
     * @param con
     * @param departmentId
     * @return
     * @throws CriticalException
     */
    public Vacancy newVacancy(Connection con, Long departmentId) 
        throws CriticalException
    {
    	logger.debug("+");
        Vacancy gVacancy = this.getGeneralVacancy(con);
        Vacancy vacancy = gVacancy.clone(con);
        vacancy.createArticle(con);
        vacancy.setDepartmentId(departmentId);
        vacancy.setNeeded( new Long(1) );
        vacancy.setTitle(
        		DefaultNameNumerator.getDefaultName(con,
        				  getNumbersSqlVacancy(departmentId), 
		        		  DEFAULT_LIST_ITEM_NAME_VACANCY
		        		  )		
        );
        vacancy.setPublishDate( new Timestamp(System.currentTimeMillis()));
        vacancy.update(con);
        VacancyActions.doVacancyLanguagePresenceAdd(con, vacancy.getId(), vacancy.getDepartmentId());
        logger.debug("-");
        return vacancy;
    }
    
    private String getNumbersSqlVacancy(Long department_id){
    	
    	StringBuffer query = new StringBuffer(700);
    	return query.append("SELECT DISTINCT CASE WHEN substr(title, 13) = '' THEN '0'").
    		append(" ELSE substr(title, 13) ").
    		append(" END ").
    		append(" as number FROM job_vacancies").
    		append(" WHERE department_id = " + department_id).
    		append(" AND (title ~ '" + DEFAULT_LIST_ITEM_NAME_VACANCY + " [0-9]+' ").
    		append(" OR title  =  '"+ DEFAULT_LIST_ITEM_NAME_VACANCY + "') ").
    		append(" ORDER by number ").toString();
    }
    
    private String getNumbersSqlDepartment(){
    	
    	StringBuffer query = new StringBuffer(700);
    	return query.append("SELECT DISTINCT CASE WHEN substr(title, 16) = '' THEN '0'").
    		append(" ELSE substr(title, 16) ").
    		append(" END ").
    		append(" as number FROM job_departments").
    		append(" WHERE (title ~ '" + DEFAULT_LIST_ITEM_NAME_DEPARTMENT + " [0-9]+' ").
    		append(" OR title  =  '"+ DEFAULT_LIST_ITEM_NAME_DEPARTMENT + "') ").
    		append(" ORDER by number ").toString();
    }

    /**
     * @return
     * @throws CriticalException 
     */
    public Long getFieldTepmlateId(Connection con) throws CriticalException {
        PreparedStatement stmt = null;
        Long templateId = null;
        try{
            stmt = con.prepareStatement(getCommonTemplateSql);
            stmt.setString(1, JobModule.get().getCommonTemplateName());
            ResultSet rs = stmt.executeQuery();
            if ( rs.next() ){
                templateId = DBHelper.makeLong( rs.getLong( "id" ) );
            }
            else{
                logger.error("common temlate not foundby :" + JobModule.get().getCommonTemplateName());
                return templateId;
            }
            
            if ( templateId == null ){
                logger.error("common temlate not foundby :" + JobModule.get().getCommonTemplateName());
                return templateId;
            }
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
        }
        return templateId;
    }

	/**
	 * @return
	 */
	public Configuration getConfig() {
		if ( this.config == null ){
			this.config = new Configuration();
		}
		return this.config;
	}

	public static String formatDate(Timestamp date){
		if (date == null){
			return null;
		}
		return JobModule.dateFormatter.format(date);
	}
	
	public static Timestamp parseDate(String date){
		if (date == null){
			return null;
		}
		Timestamp retVal = null;
		try{
			retVal = new Timestamp(JobModule.dateFormatter.parse(date).getTime());
		}
		catch(ParseException e){
			logger.error("Bad date string:" + date);
		}
		return retVal; 
	}

	public static String formatDate(Date date){
		if (date == null){
			return null;
		}
		return JobModule.dateFormatter.format(date);
	}

	public void clearCache() {
		this.generalDepartment = null;
		this.generalVacancy = null;
	}

}
