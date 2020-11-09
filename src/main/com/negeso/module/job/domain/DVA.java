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

public class DVA extends AbstractDbObject{
    private Long id = null;
    private Long departmentId = null;
    private Long vacancyId = null;
    private Long applicationId = null;
    private String applicationStatus = null;

    private static Logger logger = Logger.getLogger( DVA.class );
    private static String tableId = "job_dva";
    private static int fieldCount = 5;

    private static final String selectFromSql =
        " SELECT id, department_id, vacancy_id, application_id, application_status " +
        " FROM " + tableId;

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, department_id, vacancy_id, application_id, application_status) "+
        " VALUES (?,?,?,?,?) ";

    private static final String updateSql =
        " UPDATE " + tableId +
        " SET id=?, department_id=?, vacancy_id=?, application_id=?, application_status=? " +
        " WHERE id=? ";

    private static final String countApplicationLinksSql =
        " SELECT COUNT(*) AS count_links " +
        " FROM " + tableId + 
        " WHERE application_id = ?" 
    ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getVacancyId() {
        return vacancyId;
    }

    public void setVacancy_id(Long vacancyId) {
        this.vacancyId = vacancyId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplication_id(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String application_status) {
        this.applicationStatus = application_status;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = (Long)DomainHelper.fromObject(rs.getObject("id"));
            this.departmentId = (Long)DomainHelper.fromObject(rs.getObject("department_id"));
            this.vacancyId = (Long)DomainHelper.fromObject(rs.getObject("vacancy_id"));
            this.applicationId = (Long)DomainHelper.fromObject(rs.getObject("application_id"));
            this.applicationStatus = (String)DomainHelper.fromObject(rs.getObject("application_status"));

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
            DomainHelper.toStatement(departmentId, stmt, 2);
            DomainHelper.toStatement(vacancyId, stmt, 3);
            DomainHelper.toStatement(applicationId, stmt, 4);
            DomainHelper.toStatement(applicationStatus, stmt, 5);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new SQLException("Error while saving into statement");
        }
        logger.debug( "-" );
        return stmt;
    }

    public static DVA findById(Connection connection, Long id)
        throws CriticalException
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                DVA dva = new DVA();
                dva.load(result);
                result.close();
                statement.close();
                return dva;
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

    /**
     * @param con
     * @return
     * @throws CriticalException 
     */
    public Application getApplication(Connection con) 
        throws CriticalException 
    {
        if ( this.getApplicationId() == null ){
            return null;
        }
        Application appl = Application.findById(con, this.getApplicationId());
        return appl;
    }

    /**
     * @return
     * @throws CriticalException 
     */
    public boolean hasMoreApplicationLinks(Connection con) 
        throws CriticalException 
    {
        try
        {
            PreparedStatement statement = con.prepareStatement(
                countApplicationLinksSql
            );
            statement.setLong(1, this.getId().longValue());
            ResultSet result = statement.executeQuery();
            boolean res = false;
            if (result.next())
            {
                if ( result.getInt("count_links") > 0){
                    res = true;
                }
            }
            result.close();
            statement.close();
            return res;
        }
        catch(SQLException e)
        {
            logger.error("-error", e);
            throw new CriticalException(e);
        }
    }
}
