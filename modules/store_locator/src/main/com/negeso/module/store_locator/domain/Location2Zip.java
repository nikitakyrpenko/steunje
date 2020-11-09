package com.negeso.module.store_locator.domain;

import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

import org.apache.log4j.Logger;


/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Jan 19, 2005
 */

public class Location2Zip implements DbObject{
    private Long id = null;
    private Long contact_id = null;
    private Long zipcode_id = null;

    private final static String tableId = "sl_location2zip";
    private final static String selectSql =
            " SELECT id, contact_id, zipcode_id " +
            " FROM " + tableId;
    private final static String findByIdSql = selectSql +
            " WHERE id=? ";
    private final static String findByLocationAndZipSql = selectSql +
    		" WHERE contact_id=? AND zipcode_id=?";

    private final static String insertSql =
            " INSERT INTO " + tableId +
            " (id, contact_id, zipcode_id) " +
            " VALUES ( ?, ?, ? )";
    private final static String updateSql =
            " UPDATE "+ tableId + " SET contact_id=?, zipcode_id=? " +
            " WHERE id=? ";
    private final static String deleteSql =
            " DELETE FROM " + tableId + " WHERE id=? ";
    private final static int fieldCount = 3;
    private static Logger logger = Logger.getLogger( Location2Zip.class );

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        id = newId;
    }

    public Long getContactId() {
        return contact_id;
    }

    public void setContactId(Long newContactId) {
        contact_id = newContactId;
    }

    public void setZipcodeId(Long newZipcodeId) {
        zipcode_id = newZipcodeId;
    }

    public Long getZipcodeId() {
        return zipcode_id;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = new Long(rs.getLong("id"));
            this.contact_id = new Long(rs.getLong("contact_id"));
            this.zipcode_id = new Long(rs.getLong("zipcode_id"));

            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    public static Location2Zip findById(Connection connection, Long id)
            throws CriticalException{
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                Location2Zip zipcode = new Location2Zip();
                zipcode.load(result);
                result.close();
                statement.close();
                return zipcode;
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

    public static Location2Zip findByLocationAndZip(
    	Connection connection, Long locationId, Long zipcodeId)
	    throws CriticalException
	{
    	if ( (locationId == null) || (zipcodeId == null) ){
    		logger.warn("locationId or zipcodeId is NULL!!!");
    		return null;
    	}
		try
		{
		    PreparedStatement statement = connection.prepareStatement(findByLocationAndZipSql);
		    statement.setLong(1, locationId.longValue());
		    statement.setLong(2, zipcodeId.longValue());
		    ResultSet result = statement.executeQuery();
		    if (result.next())
		    {
		        Location2Zip zipcode = new Location2Zip();
		        zipcode.load(result);
		        result.close();
		        statement.close();
		        return zipcode;
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

    public PreparedStatement saveIntoStatement(PreparedStatement stmt) throws SQLException {
        logger.debug( "+" );

        stmt.setLong(1, this.id.longValue());
        stmt.setLong(2, this.contact_id.longValue());
        stmt.setLong(3, this.zipcode_id.longValue());

        logger.debug( "-" );
        return stmt;
    }

    public Long insert(Connection con) throws CriticalException {
        try{
            PreparedStatement statement = con.prepareStatement(insertSql);
            this.id = DBHelper.getNextInsertId( con, "sl_location2zip_id_seq" );

            statement.setLong(1, this.id.longValue());
            statement.setLong(2, this.contact_id.longValue());
            statement.setLong(3, this.zipcode_id.longValue());

            statement.execute();
            statement.close();
        }
        catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new CriticalException("Error while insert");
        }
        return this.id;
    }

    public void update(Connection con) throws CriticalException {
        try{
            PreparedStatement statement = con.prepareStatement(updateSql);
            statement.setLong(1, this.contact_id.longValue());
            statement.setLong(2, this.zipcode_id.longValue());
            statement.setLong(3, this.id.longValue());
            statement.execute();
            statement.close();
        }
        catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new CriticalException("Error while update");
        }
    }

    public void delete(Connection con) throws CriticalException {
        try{
            PreparedStatement statement = con.prepareStatement(deleteSql);
            statement.setLong(1, this.id.longValue());
            statement.execute();
            statement.close();
        }
        catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new CriticalException("Error while delete");
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
