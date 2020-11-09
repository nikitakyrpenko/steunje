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

public class ApplicationFieldValue extends AbstractDbObject {
    private Long id = null;
    private Long application_id = null;
    private Long extra_field_value_id = null;
    private String extra_field_value = null;
    private Long extra_field_id = null;

    private static Logger logger = Logger.getLogger( ApplicationFieldValue.class );
    private static String tableId = "job_application_field_values";
    private static int fieldCount = 5;

    private static final String selectFromSql =
        " SELECT id, application_id, extra_field_value_id, extra_field_value, extra_field_id " +
        " FROM " + tableId;

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, application_id, extra_field_value_id, extra_field_value, extra_field_id) "+
        " VALUES (?,?,?,?,?) ";

    private static final String updateSql =
        " UPDATE " + tableId +
        " SET id=?, application_id=?, extra_field_value_id=?, extra_field_value=?, extra_field_id=? " +
        " WHERE id=? ";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplication_id() {
        return application_id;
    }

    public void setApplication_id(Long application_id) {
        this.application_id = application_id;
    }

    public Long getExtra_field_value_id() {
        return extra_field_value_id;
    }

    public void setExtra_field_value_id(Long extra_field_value_id) {
        this.extra_field_value_id = extra_field_value_id;
    }

    public String getExtra_field_value() {
        return extra_field_value;
    }

    public void setExtra_field_value(String extra_field_value) {
        this.extra_field_value = extra_field_value;
    }

    public Long getExtra_field_id() {
        return extra_field_id;
    }

    public void setExtra_field_id(Long extra_field_id) {
        this.extra_field_id = extra_field_id;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = (Long)DomainHelper.fromObject(rs.getObject("id"));
            this.application_id = (Long)DomainHelper.fromObject(rs.getObject("application_id"));
            this.extra_field_value_id = (Long)DomainHelper.fromObject(rs.getObject("extra_field_value_id"));
            this.extra_field_value = (String)DomainHelper.fromObject(rs.getObject("extra_field_value"));
            this.extra_field_id = (Long)DomainHelper.fromObject(rs.getObject("extra_field_id"));

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
            DomainHelper.toStatement(application_id, stmt, 2);
            DomainHelper.toStatement(extra_field_value_id, stmt, 3);
            DomainHelper.toStatement(extra_field_value, stmt, 4);
            DomainHelper.toStatement(extra_field_id, stmt, 5);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new SQLException("Error while saving into statement");
        }
        logger.debug( "-" );
        return stmt;
    }

    public static ApplicationFieldValue findById(Connection connection, Long id)
        throws CriticalException
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                ApplicationFieldValue applicationFieldValue = new ApplicationFieldValue();
                applicationFieldValue.load(result);
                result.close();
                statement.close();
                return applicationFieldValue;
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
}
