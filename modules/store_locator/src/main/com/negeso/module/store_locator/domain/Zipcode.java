package com.negeso.module.store_locator.domain;

import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Collection;

import org.apache.log4j.Logger;


/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Jan 19, 2005
 */

public class Zipcode implements DbObject {
    private Long id = null;
    private Long city_id = null;
    private Long min = null;
    private Long max = null;

    private final static String tableId = "sl_zipcode";
    private final static String selectSql =
            " SELECT id, city_id, min, max " +
            " FROM " + tableId;
    private final static String findByIdSql = selectSql +
            " WHERE id=? ";

    private final static String findByMinMaxSql = selectSql +
    		" WHERE min=? AND max=?";

    private final static String findByMinSql = selectSql +
			" WHERE min=?";

    private static final String findRangeByZipcodeIdSql = selectSql +
		" WHERE (? BETWEEN min AND max) AND (NOT (city_id IS NULL))";
    
    private final static String insertSql =
            " INSERT INTO " + tableId +
            " (id, city_id, min, max) " +
            " VALUES ( ?, ?, ?, ? )";
    private final static String updateSql =
            " UPDATE "+ tableId + " SET city_id=?, min=?, max=? " +
            " WHERE id=? ";
    private final static String deleteSql =
            " DELETE FROM " + tableId + " WHERE id=? ";
    private final static int fieldCount = 4;
    private static Logger logger = Logger.getLogger( Zipcode.class );

    
    public static Long validate( String value, Collection errors ){
		try{
			value = value.trim();
			if ( !value.matches("\\d\\d\\d\\d") ){
				errors.add( "invalid zipcode: " + value);
				return null;
			}
			Long longValue = Long.valueOf( value );
			return longValue;
		}
		catch( NumberFormatException e ){
			errors.add( "invalid zipcode: " + value);
			return null;
		}
	}
    
    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        id = newId;
    }

    public Long getCityId() {
        return city_id;
    }

    public void setCityId(Long newCityId) {
        city_id = newCityId;
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long newMin) {
        min = newMin;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long newMax) {
        max = newMax;
    }


    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = new Long(rs.getLong("id"));
            if (rs.getObject("city_id") == null ){
                this.city_id = null;
            }
            else{
                this.city_id = new Long(rs.getLong("city_id"));
            }
            this.min = new Long(rs.getLong("min"));
            if (rs.getObject("max") == null ){
                this.max = null;
            }
            else{
                this.max = new Long(rs.getLong("max"));
            }

            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    public static Zipcode findCityRangeByZipcodeId(Connection connection, Long id)
    		throws CriticalException{
		try
		{
		    PreparedStatement statement = connection.prepareStatement(findRangeByZipcodeIdSql);
		    statement.setLong(1, id.longValue());
		    ResultSet result = statement.executeQuery();
		    if (result.next())
		    {
		        Zipcode zipcode = new Zipcode();
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

    public static Zipcode findById(Connection connection, Long id)
            throws CriticalException{
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                Zipcode zipcode = new Zipcode();
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

    public static Zipcode findByMinMax(Connection connection, Long min, Long max)
    	throws CriticalException{
		try
		{
			PreparedStatement statement = null;
			if ( max == null ){
				statement = connection.prepareStatement(findByMinSql);	
			    statement.setLong(1, min.longValue());
			}
			else{
				statement = connection.prepareStatement(findByMinMaxSql);	
			    statement.setLong(1, min.longValue());
			    statement.setLong(2, max.longValue());
			}
		    
		    ResultSet result = statement.executeQuery();
		    if (result.next())
		    {
		        Zipcode zipcode = new Zipcode();
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
        if ( this.city_id==null ){
            stmt.setObject(2, null);
        }
        else{
            stmt.setLong(2, this.city_id.longValue());
        }

        stmt.setLong(3, this.min.longValue());

        if ( this.max==null ){
            stmt.setObject(4, null);
        }
        else{
            stmt.setLong(4, this.max.longValue());
        }

        logger.debug( "-" );
        return stmt;
    }

    public Long insert(Connection con) throws CriticalException {
        try{
            PreparedStatement statement = con.prepareStatement(insertSql);
            this.id = DBHelper.getNextInsertId( con, "sl_zipcode_id_seq" );

            statement.setLong(1, this.id.longValue());
            if ( this.city_id==null ){
                statement.setObject(2, null);
            }
            else{
                statement.setLong(2, this.city_id.longValue());
            }

            statement.setLong(3, this.min.longValue());

            if ( this.max==null ){
                statement.setObject(4, null);
            }
            else{
                statement.setLong(4, this.max.longValue());
            }

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
            if ( this.city_id==null ){
                statement.setObject(1, null);
            }
            else{
                statement.setLong(1, this.city_id.longValue());
            }

            statement.setLong(2, this.min.longValue());

            if ( this.max==null ){
                statement.setObject(3, null);
            }
            else{
                statement.setLong(3, this.max.longValue());
            }

            statement.setLong(4, this.id.longValue());
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
