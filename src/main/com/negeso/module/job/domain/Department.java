package com.negeso.module.job.domain;

import com.negeso.framework.domain.AbstractDbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DomainHelper;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

import org.apache.log4j.Logger;


/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 22, 2005
 */

public class Department extends AbstractDbObject{
    private Long id = null;
    private String title = null;
    private String description = null;
    private String email = null;

    private static Logger logger = Logger.getLogger( Department.class );
    private static String tableId = "job_departments";
    private static int fieldCount = 4;

    private static final String selectFromSql =
        " SELECT id, title, description, email " +
        " FROM " + tableId;

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, title, description, email) "+
        " VALUES (?,?,?,?) ";

    private static final String updateSql =
        " UPDATE " + tableId +
        " SET id=?, title=?, description=?, email=? " +
        " WHERE id=? ";

    public static final String selectAdminDepartmentsSql =
        " SELECT " +
        "   job_departments.id, job_departments.title, job_departments.email, " + 
        "   COUNT(job_vacancies.id) AS vacancy_count, " + 
        //  subquery for application counting 
        "   (SELECT COUNT(*) FROM job_dva " +
        "       WHERE (department_id = job_departments.id) " +
        "   ) AS application_count, " + 
        //  subquery for new_application counting 
        "   (SELECT COUNT(*) FROM job_dva " +
        "       WHERE (job_dva.department_id = job_departments.id) AND (job_dva.application_status='new') " +
        "   ) AS new_application_count " + 
        " FROM job_departments "  +
        " LEFT JOIN job_vacancies " +
        "     ON job_departments.id = job_vacancies.department_id " +
        //  join to count applications 
        " WHERE job_departments.id NOT IN (?) " + 
        " GROUP BY job_departments.id, job_departments.title, job_departments.email " +
        " ORDER BY job_departments.title "
    ;

    public static final String selectVisitorDepartmentsSql =
		  "SELECT " +
		  	"job_departments.id, " +
		  	"job_departments.title, " +
		  	"job_departments.description, " +
        " COUNT(job_vacancies.id) AS vacancy_count " + 
		  "FROM " +
		  	"job_departments " +
		  "LEFT JOIN job_vacancies ON " +
        "	(job_departments.id = job_vacancies.department_id) AND " +
        "   (job_vacancies.needed > 0) AND " +
		  	"(job_vacancies.publish_date <= now() AND " +
		  	"(job_vacancies.expire_date >= now() OR job_vacancies.expire_date IS NULL)) " +
		  "LEFT JOIN job_dep2lang_presence ON " +
		  	"(job_dep2lang_presence.j_dep_id = job_departments.id) " +
		  "LEFT JOIN job_vac2lang_presence ON " +
		  	"(job_vac2lang_presence.j_vac_id=job_vacancies.id) " +
		  "WHERE " +
		  	"job_dep2lang_presence.lang_id = ? AND "+ 
		  	"job_vac2lang_presence.lang_id = ? AND "+
		  	"job_vac2lang_presence.is_present = true AND "+
		  	"job_dep2lang_presence.is_present = true "+ 
		  "GROUP BY " +
		  	"job_departments.id, " +
		  	"job_departments.title, " +
		  	"job_departments.description " +
		  "ORDER BY job_departments.title;"
    ;
    
    public static final String selectGeneralVisitorVacanciesSql =
        " SELECT job_vacancies.id, job_vacancies.title, job_vacancies.position, " + 
        "   job_vacancies.salary, job_vacancies.needed, article.text,  " +
        "   article.id AS article_id" + 
        " FROM job_vacancies " +
        " LEFT JOIN article " +
        "   ON job_vacancies.article_id = article.id " +
		" LEFT JOIN job_vac2lang_presence " +
        " ON (job_vac2lang_presence.j_vac_id = job_vacancies.id)" +
        " WHERE job_vacancies.department_id = ? AND " +
        "   (job_vacancies.needed > 0) AND " +
		" 	(job_vacancies.publish_date <= now() AND (job_vacancies.expire_date >= now() " +
		"   OR job_vacancies.expire_date IS NULL)) " +         
		" AND (SELECT is_present FROM  job_dep2lang_presence WHERE  j_dep_id = job_departments.id AND lang_id = ?)	" +
        "    = TRUE " +
		" AND job_vac2lang_presence.lang_id = ? " +
        " AND job_vac2lang_presence.is_present = true" + 
        " ORDER BY job_vacancies.title "
    ;
    
    private static final String selectDepartmentsAndVacanciesSql =
        " SELECT job_departments.id, job_departments.title, " +
        " job_vacancies.id AS vac_id, job_vacancies.title AS vac_title, COUNT(job_vacancies.id) AS vac_count  " +
        " FROM job_departments " +
        " LEFT JOIN job_vacancies " +
        "    ON (job_departments.id = job_vacancies.department_id) AND " +
        "   (job_vacancies.needed > 0) AND " +
		" 	(job_vacancies.publish_date <= now() AND (job_vacancies.expire_date >= now() " +
		"   OR job_vacancies.expire_date IS NULL)) " +         
		" LEFT JOIN job_vac2lang_presence " +
        " ON (job_vac2lang_presence.j_vac_id = job_vacancies.id)" +
        " WHERE job_departments.id <>1 " +
        " AND (SELECT is_present FROM  job_dep2lang_presence WHERE  j_dep_id = job_departments.id AND lang_id = ?)	" +
        "    = TRUE " +
        " AND job_vac2lang_presence.lang_id = ? " +
        " AND job_vac2lang_presence.is_present = TRUE" + 
        " GROUP BY job_departments.id, job_departments.title, job_departments.description, job_vacancies.id, job_vacancies.title " +
        " ORDER BY job_departments.title, job_vacancies.title "
    ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = (Long) DomainHelper.fromObject(rs.getObject("id"));
            this.title = (String) DomainHelper.fromObject(rs.getObject("title"));
            this.description = (String) DomainHelper.fromObject(rs.getObject("description"));
            this.email = (String) DomainHelper.fromObject(rs.getObject("email"));

            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt) throws SQLException {
        logger.debug( "+" );
        try{
            DomainHelper.toStatement(id, stmt, 1);
            DomainHelper.toStatement(title, stmt, 2);
            DomainHelper.toStatement(description, stmt, 3);
            DomainHelper.toStatement(email, stmt, 4);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new SQLException("Error while saving into statement");
        }
        logger.debug( "-" );
        return stmt;
    }

        
    public static Department findById(Connection connection, Long id)
        throws CriticalException
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                Department department = new Department();
                department.load(result);
                result.close();
                statement.close();
                return department;
            }
            result.close();
            statement.close();
            return null;
        }
        catch(SQLException e)
        {
            throw new CriticalException(e);
        }
    }

    public String getTableId() {
        return tableId;
    }

    public String getFindByIdSql() {
        return findByIdSql;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public static String getSelectDepartmentsAndVacanciesSql(){
        return selectDepartmentsAndVacanciesSql;
    }
}
