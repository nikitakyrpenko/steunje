/*
 * @(#)$Id: ApplicationXmlBuilder.java,v 1.15, 2005-06-06 13:04:07Z, Stanislav Demchenko$
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

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.job.JobModule;
import com.negeso.module.job.domain.Application;
import com.negeso.module.job.domain.Vacancy;
import com.negeso.module.job.extra_field.FieldRepository;
import com.negeso.module.job.extra_field.FieldType;

/**
 *
 * Application generation 
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 16$
 */
public class ApplicationXmlBuilder {
    private static Logger logger = Logger.getLogger( FieldType.class );

    public final static String getCommonTemplateSql = 
        " SELECT * FROM job_templates WHERE sys_name = ?"
    ;

    public final static String getApplicationFieldsSql = 
        " SELECT extra_field_value, extra_field_value_id, job_extra_fields.type_id " +
        " FROM job_application_field_values " +
        " LEFT JOIN job_extra_fields " +
        "   ON job_extra_fields.id = job_application_field_values.extra_field_id " +
        " WHERE job_application_field_values.application_id = ? " +
        " ORDER BY order_number "
    ;

    /**
     * Build vacancy application form
     * 
     * @param con
     * @param parent
     * @param vacancyId
     * @param langId
     * @return
     * @throws CriticalException
     */
    public static Element buildVacancyApplicationForm(
        Connection con, Element parent, Long vacancyId, Long langId) 
        throws CriticalException
    {
        
        if (vacancyId == null) {
            logger.error("Vacancy id is null, building general vacancy");
            vacancyId = JobModule.get().getGeneralVacancy(con).getId();
        }
        
        Element form = Xbuilder.addEl(parent, "application_form", null);
        ApplicationXmlBuilder.buildCommonApplicationFields(
            con, form, langId
        );
        
        Xbuilder.setAttr(
            form, 
            "vacancy_id", 
            vacancyId
        );
        
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement(Vacancy.getVacancyExtraFieldsSql);
            stmt.setLong(1, vacancyId.longValue());
            ResultSet rs = stmt.executeQuery();
            buildFieldsFromRS(rs, form, langId);
            rs.close();
            stmt.close();
        }
        catch(SQLException e){
            logger.error("-error", e);
        }
        return form;
    }

    /**
     * Build fields from ResultSet
     * 
     * @param rs
     * @param parent
     * @param langId
     * @throws CriticalException
     * @throws SQLException
     */
    private static void buildFieldsFromRS(
        ResultSet rs, Element parent, Long langId) 
        throws CriticalException, SQLException
    {
        Element fieldEl = null;
        FieldType fieldType = null;
        while( rs.next() ){
            fieldType = FieldRepository.get().getType(
                rs.getStatement().getConnection(), 
                DBHelper.makeLong(rs.getLong("type_id"))
            );
            
            fieldEl = fieldType.buildXml(
                rs.getStatement().getConnection(), parent, langId
            );
            Xbuilder.setAttr(fieldEl, "id", "" + rs.getLong("id"));
            Xbuilder.setAttr(fieldEl, "is_required", "" + rs.getBoolean("is_required"));
            Xbuilder.setAttr(fieldEl, "sys_name", rs.getString("sys_name"));
        }
    }
    
    
    /**
     * Build general department application form
     * 
     * @param con
     * @param parent
     * @param departmentId
     * @param langId
     * @return
     * @throws CriticalException
     */
    public static Element buildDepartmentApplicationForm(
        Connection con, Element parent, Long departmentId, Long langId) 
        throws CriticalException
    {
        return ApplicationXmlBuilder.buildGeneralApplicationForm(
            con, parent, langId
        ); 
    }

    /**
     * Build general application form
     * 
     * @param con
     * @param parent
     * @param langId
     * @return
     * @throws CriticalException
     */
	public static Element buildGeneralApplicationForm(
        Connection con, Element parent, Long langId) throws CriticalException
    {
        return ApplicationXmlBuilder.buildVacancyApplicationForm(
            con, parent, JobModule.get().getGeneralVacancy(con).getId(), langId
        );
	}
    
    
    /**
     * 
     * 
     * @param con
     * @param parent
     * @param langId
     * @return
     * @throws CriticalException
     */
    public static Element buildCommonApplicationFields(
        Connection con, Element parent, Long langId) throws CriticalException
    {
        PreparedStatement stmt = null;
        try{
            Long templateId = JobModule.get().getFieldTepmlateId(con);
            
            if ( templateId == null ){
                logger.error("common temlate not foundby :" + JobModule.get().getCommonTemplateName());
                return parent;
            }
            logger.info("Template id :" + templateId);

            stmt = con.prepareStatement(Vacancy.getTemplateFieldsSql);
            stmt.setLong(1, templateId.longValue());
            ResultSet rs = stmt.executeQuery();
            ApplicationXmlBuilder.buildFieldsFromRS(rs, parent, langId);
            rs.close();
            stmt.close();
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
        }
        return parent;
    }
    
    
    /**
     * 
     * @param con
     * @param parent
     * @param applicationId
     * @return
     * @throws CriticalException
     */
    public static Element buildApplication( 
        Connection con, Element parent, Long applicationId, Long langId )
        throws CriticalException 
    {
        
        Element applEl = Xbuilder.addEl(parent, "application", null);
        
        if ( applicationId == null ){
            logger.error("!!! applicationId is NULL");
            return applEl;
        }
        
        Application application = Application.findById(con, applicationId);
        
        if ( application == null ){
            logger.error("!!! application not found by Id:" + applicationId);
            return applEl;
        }
      
        Xbuilder.setAttr(applEl, "first_name", application.getName()); 
        Xbuilder.setAttr(applEl, "second_name", application.getSurname()); 
        Xbuilder.setAttr(applEl, "address_line", application.getAddress()); 
        Xbuilder.setAttr(
			applEl, 
			"birthday", 
			JobModule.formatDate(application.getBirthdate())
		); 
        Xbuilder.setAttr(applEl, "birthplace", application.getBirthplace()); 
        Xbuilder.setAttr(applEl, "citizenship", application.getCitizenship()); 
        Xbuilder.setAttr(applEl, "email", application.getEmail()); 
        Xbuilder.setAttr(applEl, "cv_flie", application.getCv()); 
        Xbuilder.setAttr(applEl, "phone", application.getTelephone()); 
        Xbuilder.setAttr(applEl, "mobile", application.getMobile()); 
        Xbuilder.setAttr(applEl, "post_date", JobModule.formatDate(application.getPostDate()) ); 

        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement( ApplicationXmlBuilder.getApplicationFieldsSql );
            stmt.setLong(1, applicationId.longValue());
            ResultSet rs = stmt.executeQuery();

            FieldType fieldType = null;
            while( rs.next() ){
                fieldType = FieldRepository.get().getType(
                    rs.getStatement().getConnection(), 
                    DBHelper.makeLong(rs.getLong("type_id"))
                );
                fieldType.buildValueXml(
                    rs.getStatement().getConnection(),
                    applEl, 
                    DBHelper.makeLong( rs.getLong("extra_field_value_id") ),
                    rs.getString("extra_field_value"),
                    langId
                );
            }
        }
        catch (SQLException e){
            logger.error("-error");
            throw new CriticalException(e);
        }
       
        return applEl;
    }

    /**
     * @param rs
     * @param vacancyEl
     * @param b
     * @throws SQLException 
     */
    public static Element buildBriefApplication(
        ResultSet rs, Element parent, boolean isVisitor) throws SQLException 
    {
        Element application = Xbuilder.addEl(parent, "application", null);
        Xbuilder.setAttr(application, "id", "" + rs.getLong("application_id"));
        Xbuilder.setAttr(application, "title", rs.getString("application_title"));
        Xbuilder.setAttr(
			application, 
			"post_date", 
			JobModule.formatDate(rs.getDate("application_date"))
		);
        Xbuilder.setAttr(application, "status", rs.getString("application_status"));
        Xbuilder.setAttr(application, "dva_id", rs.getString("application_dva_id"));
        return application;
    }
}
