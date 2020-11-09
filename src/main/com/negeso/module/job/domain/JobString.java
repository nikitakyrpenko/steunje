package com.negeso.module.job.domain;

import com.negeso.framework.domain.AbstractDbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DomainHelper;
import com.negeso.framework.domain.Language;
import com.negeso.module.job.JobModule;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 22, 2005
 */

public class JobString extends AbstractDbObject{
    private Long id = null;
    private Long langId = null;
    private String value = null;

    private static Logger logger = Logger.getLogger( JobString.class );
    private static String tableId = "job_strings";
    private static int fieldCount = 3;

    private static final String selectFromSql =
        " SELECT id, lang_id, value " +
        " FROM " + tableId;

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, lang_id, value) "+
        " VALUES (?,?,?) ";

    private static final String updateSql =
        " UPDATE " + tableId +
        " SET id=?, lang_id=?, value=? " +
        " WHERE id=? ";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLangId() {
        return langId;
    }

    public void setLangId(Long lang_id) {
        this.langId = lang_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = (Long)DomainHelper.fromObject(rs.getObject("id"));
            this.langId = (Long)DomainHelper.fromObject(rs.getObject("lang_id"));
            this.value = (String)DomainHelper.fromObject(rs.getObject("value"));

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
            DomainHelper.toStatement(langId, stmt, 2);
            DomainHelper.toStatement(value, stmt, 3);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new SQLException("Error while saving into statement");
        }
        logger.debug( "-" );
        return stmt;
    }

    public static JobString findById(Connection connection, Long titleId, Long langId)
        throws CriticalException
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, titleId);
            statement.setLong(2, langId);
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                JobString extraField = new JobString();
                extraField.load(result);
                result.close();
                statement.close();
                return extraField;
            }
            result.close();
            statement.close();
            return null;
        }
        catch(SQLException e)
        {
        	logger.error("-error", e);
            throw new CriticalException(e);
        }
    }

    /**
     * Get all strings into map: <langId, value>  
     * 
     * @param connection
     * @param id
     * @return
     * @throws CriticalException
     */
    public static Map<Long, String> getStringMapById(Connection con, Long id)
        throws CriticalException
    {
        logger.debug("+");
        try
        {
            PreparedStatement statement = con.prepareStatement(
                findByIdSql
            );
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();

            Map<Long, String> map = null;
            String value = null;
            Long langId = null;
            while ( result.next() ) {
                value = result.getString("value"); 
                langId = DBHelper.makeLong( result.getLong("lang_id") );
                if ( ( value == null ) || ( langId == null ) ){
                    continue;
                }
                    
                if ( map == null ){
                    map = new HashMap<Long, String>();
                }
                map.put(langId, value);
            }
            result.close();
            statement.close();
            
            if ( map == null ){
                logger.info("Building new jString for id:" + id);
                JobString jString = new JobString();
                jString.setId(id);
                jString.setValue(JobModule.get().getDefaultStringValue());
                jString.setLangId( Language.getDefaultLanguage().getId() );
                jString.insert(con);
                map = new HashMap<Long, String>();
                map.put( jString.getLangId(), jString.getValue());
            }

            logger.debug("-");
            return map;
        }
        catch(SQLException e)
        {
            logger.error("- error", e);
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
    
    
    public static Long getNextId(Connection con) throws CriticalException{
    	return DBHelper.getNextInsertId(con, "job_strings_id_seq");
    }

    public static void createValues(Connection con, Long id, String value) throws CriticalException{
        JobString jString = null;
        for ( Language lang: (ArrayList <Language>) Language.getItems()){
        	jString = new JobString();
        	jString.setId( id );
        	jString.setLangId( lang.getId() );
        	jString.setValue( value );
        	jString.insert(con);
        }
    }
}
