/*
 * @(#)$Id: VacancyXmlBuilder.java,v 1.13, 2005-06-06 13:05:17Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.job.generator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Article;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.util.SQLUtil; 
import com.negeso.module.job.JobModule;
import com.negeso.module.job.LanguagePresence;
import com.negeso.module.job.domain.Vacancy;
import com.negeso.module.job.domain.Department;

/**
 *
 * Vacancy Xml builder
 * 
 * @version		$Revision: 14$
 * @author		Olexiy Strashko
 * 
 */
public class VacancyXmlBuilder {
    private static Logger logger = Logger.getLogger( VacancyXmlBuilder.class );

    public final static String getBreafDepVacanciesApplicationSql = 
        " SELECT " +
        "   job_vacancies.id, " +
        "   job_vacancies.title," +
        "   job_vacancies.needed, " +
        "   job_vacancies.position, " +
        "   job_vacancies.salary, " +
        "   job_vacancies.publish_date, " +
        "   job_vacancies.expire_date, " +
        "   job_applications.id AS application_id, " +
        "   name || ' ' || COALESCE(surname, '') AS application_title, " +
        "   job_applications.post_date AS application_date, " +
        "   job_dva.application_status AS application_status, " +
        "   job_dva.id AS application_dva_id " +
        " FROM job_vacancies " +
        " LEFT JOIN job_dva " +
        "   ON job_vacancies.id = job_dva.vacancy_id " +
        " LEFT JOIN job_applications " +
        "   ON job_dva.application_id = job_applications.id " +
        " WHERE job_vacancies.department_id = ? " +
        " ORDER BY " +
        "   job_vacancies.id DESC, job_vacancies.needed DESC, job_vacancies.title, job_dva.application_status, " +
        "   application_title "
    ;

    public final static String getBreifDepGeneralApplicationSql = 
        " SELECT " +
        "   job_applications.id AS application_id, " +
        "   name || ' ' || COALESCE(surname, '') AS application_title, " +
        "   job_applications.post_date AS application_date, " +
        "   job_dva.application_status AS application_status, " +
        "   job_dva.id AS application_dva_id " +
        " FROM job_applications " +
        " LEFT JOIN job_dva " +
        "   ON job_dva.application_id = job_applications.id " +
        " WHERE job_dva.department_id = ? AND job_dva.vacancy_id = ? " +
        " ORDER BY " +
        "   job_dva.application_status, application_title "
    ;
    
    private static final String getVacancyDetailsSql =
        " SELECT job_vacancies.id, title, \"position\", article_id, salary, " +
        "   needed, publish_date, expire_date, \"type\", " +
        "   article.id AS article_id, article.text, " +
        "   department_id, region_id " +
        " FROM job_vacancies " +
        " LEFT JOIN article " +
        "   ON job_vacancies.article_id = article.id " +
        " WHERE job_vacancies.id = ? "
    ;
    
    private static final String getExpireDateSql =
    	" AND (job_vacancies.expire_date >= now() " +" OR job_vacancies.expire_date IS NULL)";
        
    private static final String searchVacanciesSql =
        " SELECT job_vacancies.id, job_vacancies.title, " +
        " job_vacancies.position, job_vacancies.salary, article.text "+
        " FROM article, job_vacancies"+
		" LEFT JOIN job_vac2lang_presence " +
        " ON (job_vac2lang_presence.j_vac_id = job_vacancies.id)" +
        " WHERE article.id = job_vacancies.article_id " +
        " AND needed > 0 " +
        " AND department_id != 1 " +
        " AND job_vac2lang_presence.lang_id = ? " +
        " AND job_vac2lang_presence.is_present = ?" + 
        " AND " +
        " ((job_vacancies.publish_date IS Null AND job_vacancies.expire_date IS Null) OR " +
        "  (job_vacancies.publish_date<=now() AND job_vacancies.expire_date IS Null) OR " +
        "  (job_vacancies.publish_date IS Null AND job_vacancies.expire_date>=now()) OR " +
        "  (job_vacancies.publish_date<=now() AND job_vacancies.expire_date>=now()) ) ";

    private static String[] SEARCH_FIELDS = {"job_vacancies.title", "job_vacancies.position", "article.text"};

    /**
     * Build brief vacancy from ResultSet
     * 
     * @param rs
     * @param parent
     * @param isVisitor
     * @return
     * @throws SQLException
     */
    public static Element buildBriefVacancy(ResultSet rs, Element parent, boolean isVisitor)
        throws SQLException
    {
        Element vacancy = Xbuilder.addEl(parent, "vacancy", null);
        Xbuilder.setAttr(vacancy, "id", "" + rs.getLong("id"));
        Xbuilder.setAttr(vacancy, "title", rs.getString("title"));
        Xbuilder.setAttr(vacancy, "position", rs.getString("position"));
        Xbuilder.setAttr(vacancy, "salary", rs.getString("salary"));
        Xbuilder.setAttr(vacancy, "person_needed", rs.getString("needed"));

        if ( !isVisitor ){
            Xbuilder.setAttr(
                vacancy, "publish_date", JobModule.formatDate(rs.getDate("publish_date"))
            );
            Xbuilder.setAttr(
                vacancy, "expire_date", JobModule.formatDate(rs.getDate("expire_date"))
            );
        }
        return vacancy;
    }
    
    
    public static Element buildVacancy(ResultSet rs, Element parent, boolean isVisitor)
        throws SQLException
    {
        Element vacancy = buildBriefVacancy(rs, parent, isVisitor);

        Element art = Xbuilder.addEl(vacancy, "article", null);
        Xbuilder.setAttr(art, "id", rs.getString("article_id"));
        Xbuilder.addEl(art, "text", rs.getString("text"));
        Xbuilder.setAttr(vacancy, "department_id", rs.getLong("department_id"));
        Xbuilder.setAttr(vacancy, "region_id", rs.getLong("region_id"));
        
        return vacancy;
    }

    
    public static Element buildVacancyDetails(
        Connection con, Element parent, Long vacancyId, Long langId, Boolean isAdmin ) 
        throws CriticalException
    {
        logger.debug("+");
        
        Element vacancyEl = Xbuilder.createEl(parent, "vacancy", null);
        if ( vacancyId == null ){
            logger.error("-ERROR!!! VacancyId is NULL");
            return vacancyEl;
        }

        PreparedStatement stmt = null;
        try{
        	String vacancyDetailsSql = (isAdmin)?getVacancyDetailsSql:getVacancyDetailsSql+getExpireDateSql;
            stmt = con.prepareStatement( vacancyDetailsSql );            
            stmt.setLong( 1, vacancyId.longValue() );            
            LanguagePresence languagePresence = new LanguagePresence(
            		"job_vac2lang_presence", "j_vac_id"
            	);
     
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                vacancyEl = buildVacancy(rs, parent, false);
                vacancyEl.appendChild(
                		languagePresence.getPresenceElement(
                				con, 
                				vacancyEl,
                				rs.getLong("id"))
                	);
           
            } 
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
        }
        
        logger.debug("-");
        return vacancyEl;
    }
    
    public static Element buildVisitorVacanciesByDepartment(Long departmentId,
        Element parent, Connection conn, Long langId ) 
        throws CriticalException 
    {
        Element vacancy_list = Xbuilder.addEl(parent, "vacancy_list", null);
        try{
            PreparedStatement stat = conn.prepareStatement(Vacancy.getSelectByDepartmentIdSql());
            stat.setLong(1, departmentId.longValue());
            stat.setLong(2, langId);
        	stat.setBoolean(3, true);
            ResultSet res = stat.executeQuery();
            while(res.next()){
                buildVacancy(res, vacancy_list, true);
            }
            res.close();
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);        
        }
        return vacancy_list;
    }
    
    public static Element buildVisitorVacanciesByRegion(Long regionId,
            Element parent, Connection conn, Long langId ) 
            throws CriticalException
        {
            Element vacancy_list = Xbuilder.addEl(parent, "vacancy_list", null);
            try{
                PreparedStatement stat = conn.prepareStatement(Vacancy.selectByRegionIdSql);
                stat.setLong(1, regionId);
                stat.setLong(2, langId);
            	stat.setBoolean(3, true);
                ResultSet res = stat.executeQuery();
                while(res.next()){
                    buildVacancy(res, vacancy_list, true);
                }
                res.close();
            }
            catch(SQLException e){
                logger.error("-error", e);
                throw new CriticalException(e);        
            }
            return vacancy_list;
        }

    /**
     * Build general applicaions for departament 
     * 
     * @param con
     * @param parent
     * @param departmentId
     * @param langId
     * @return
     * @throws CriticalException
     */
    public static Element buildAdminGeneralDepartmentApplications(
        Connection con, Element parent, Long departmentId, Long langId ) 
        throws CriticalException
    {
        logger.debug("+");
        Element applications = Xbuilder.addEl(parent, "general_applications", null);
        
        if ( departmentId == null ){
            logger.error("- ERROR!!! null departmentId");
            return applications;
        }
        
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement( getBreifDepGeneralApplicationSql );
            stmt.setLong( 1, departmentId );
            stmt.setLong( 2, JobModule.get().getGeneralVacancy(con).getId() );
            
            ResultSet rs = stmt.executeQuery();

            Element appEl = null;
            long newApplCounter = 0;
            long applCounter = 0;
            while (rs.next()){
                appEl = ApplicationXmlBuilder.buildBriefApplication(rs, applications, false);
                Xbuilder.setAttr(appEl, "department_id", departmentId);
                applCounter++;
                if ( JobModule.APPLICATION_STATUS_NEW.equals( rs.getString("application_status")) ){
                    newApplCounter++;
                }
            }
            if ( newApplCounter != 0 ){
                Xbuilder.setAttr(
                    applications, "new_applications", "" + newApplCounter
                );
            }
            if ( applCounter != 0 ){
                Xbuilder.setAttr(
                    applications, "applications", "" + applCounter
                );
            }
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
        }
        
        logger.debug("-");
        return  applications;
    }
    
    
    /**
     * Build department vacancies
     * 
     * @param con
     * @param parent
     * @param langId
     * @return
     * @throws CriticalException
     */
    public static Element buildAdminDepartmentVacancyApplications(
        Connection con, Element parent, Long departmentId, Long langId ) 
        throws CriticalException
    {
        logger.debug("+");
        Element vacancies = Xbuilder.addEl(parent, "vacancies", null);
        
        if ( departmentId == null ){
            logger.error("- ERROR!!! null departmentId");
            return vacancies;
        }
        
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement( getBreafDepVacanciesApplicationSql );
            stmt.setLong( 1, departmentId.longValue() );
            
            ResultSet rs = stmt.executeQuery();
            
            Element vacancyEl = null;
            Element applEl = null;
            long curVacancyId = 0;
            long tmpId = 0;
            long newApplCounter = 0;
            long applCounter = 0;
            while (rs.next()){
                tmpId = rs.getLong("id");
                if ( curVacancyId != tmpId ){
                    if ( vacancyEl != null ){
                        Xbuilder.setAttr(
                            vacancyEl, "applications", "" + applCounter
                        );
                        Xbuilder.setAttr(
                            vacancyEl, "new_applications", "" + newApplCounter
                        );
                    }
                    
                    vacancyEl = VacancyXmlBuilder.buildBriefVacancy(rs, vacancies, false);
                    Xbuilder.setAttr(vacancyEl, "department_id", departmentId);
                    curVacancyId = tmpId;
                    newApplCounter = 0;
                    applCounter = 0;
                }
                
                if ( rs.getLong("application_id") != 0 ){ 
                    applEl = ApplicationXmlBuilder.buildBriefApplication(rs, vacancyEl, false);
                    Xbuilder.setAttr(applEl, "department_id", departmentId);
                    applCounter++;
                }
                if ( "new".equals( rs.getString("application_status")) ){
                    newApplCounter++;
                }
            }
            if ( vacancyEl != null ){
                Xbuilder.setAttr(
                    vacancyEl, "applications", "" + applCounter
                );
                if ( newApplCounter != 0 ){
                    Xbuilder.setAttr(
                        vacancyEl, "new_applications", "" + newApplCounter
                    );
                }
            }
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
        }
        
        logger.debug("-");
        return vacancies;
    }

    public static Element buildVacancySearchXml(RequestContext request, Connection conn, Element parent, Long langId)
                throws Exception{
        Element vacancySearchResults = Xbuilder.addEl(parent, "vacancy_search_results", null);
        SearchState searchState = new SearchState(request);
        String sql = searchVacanciesSql;
        if (searchState.getSearchSalary()!=null){
            sql+=" AND salary>="+searchState.getSearchSalary().toString()+" ";
        }
        if (searchState.getSearchWord()!=null){
            String buff = SQLUtil.createLike(SEARCH_FIELDS, SQLUtil.generateSearchWordList(searchState.getSearchWord()));
            if(buff!=""){
                sql+=" AND " + buff;
            }
        }
        logger.debug("DB querry! searchVacanciesSql = " + sql);
        PreparedStatement stat = conn.prepareStatement(sql);
        stat.setLong(1, langId);
        stat.setBoolean(2, true);
        ResultSet res = stat.executeQuery();
        int count = 0;
        Element vacancySearchResult = null;
        while(res.next()){
            vacancySearchResult = Xbuilder.addEl(vacancySearchResults, "vacancy_search_result",
                    res.getString("text"));
            vacancySearchResult.setAttribute("id", res.getString("id"));
            vacancySearchResult.setAttribute("title", res.getString("title"));
            vacancySearchResult.setAttribute("position", res.getString("position"));
            vacancySearchResult.setAttribute("salary", res.getString("salary"));
            count++;
        }
        parent.setAttribute("count", ""+count);
        return vacancySearchResults;
    }

    public static Element buildFoundVacancyXml(RequestContext request, Connection conn, Element parent, Long langId)
                throws Exception{
        Vacancy vac = Vacancy.findById(conn, new Long(request.getParameter("vac_id")));
        Article art = Article.findById(conn, vac.getArticleId());
        Department dep = Department.findById(conn, vac.getDepartmentId());
        Element vacancy = Xbuilder.addEl(parent, "vacancy", art.getText());
        
        Xbuilder.setAttr(vacancy, "id", vac.getId().longValue());
        Xbuilder.setAttr(vacancy, "department", dep.getTitle());
        Xbuilder.setAttr(vacancy, "position", vac.getPosition());
        Xbuilder.setAttr(vacancy, "title", vac.getTitle());
        Xbuilder.setAttr(vacancy, "salary", vac.getSalary());        
        return vacancy;
    }
}
