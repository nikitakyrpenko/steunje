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

public class City implements DbObject{
    private Long id = null;
    private String title = "";

    private final static String tableId = "sl_city";
    private final static String selectSql =
            " SELECT id, title " +
            " FROM " + tableId;
    private final static String findByIdSql = selectSql +
            " WHERE id=? ";
    private final static String insertSql =
            " INSERT INTO " + tableId +
            " (id, title) " +
            " VALUES ( ?, ? )";
    private final static String updateSql =
            " UPDATE "+ tableId + " SET title=? " +
            " WHERE id=? ";
    private final static String deleteSql =
            " DELETE FROM " + tableId + " WHERE id=? ";

    
    public static final String getCitiesSql = 
		"SELECT sl_city.id, sl_city.title, sl_zipcode.min, sl_zipcode.max " +
		"FROM sl_city " +
		"LEFT JOIN sl_zipcode " +
		"ON sl_city.id=sl_zipcode.city_id " +
		"ORDER BY title "
	;

    
	public static final String getCitiesWithLocaionsSql =
		"SELECT sl_city.id AS city_id, sl_city.title, city_zip.min, city_zip.max, " +
		"	contact.company_name AS location_title, contact.id AS location_id "+
		"FROM sl_city " +
		"LEFT JOIN sl_zipcode AS city_zip " +
		"	ON sl_city.id = city_zip.city_id " +
		"LEFT JOIN contact ON contact.id IN " +
		"( " +
		"	SELECT contact.id FROM contact " +
		"	LEFT JOIN sl_location2zip " +
		"	ON contact.id = sl_location2zip.contact_id " +
		"	LEFT JOIN sl_zipcode AS location_zip " +
		"	ON location_zip.id = sl_location2zip.zipcode_id " +
		"	WHERE " +
		"	location_zip.min <= city_zip.min AND location_zip.max >= city_zip.max " +
		") " +
		"	ORDER BY sl_city.title, contact.company_name "
	;

    
    private final static int fieldCount = 2;
    private static Logger logger = Logger.getLogger( City.class );

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        id = newId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = new Long(rs.getLong("id"));
            this.title = rs.getString("title");

            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    public static City findById(Connection connection, Long id)
            throws CriticalException{
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                City city = new City();
                city.load(result);
                result.close();
                statement.close();
                return city;
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
        stmt.setLong( 1, this.id.longValue());
        stmt.setString( 2, this.title);

        logger.debug( "-" );
        return stmt;
    }

    public Long insert(Connection con) throws CriticalException {
        try{
            PreparedStatement statement = con.prepareStatement(insertSql);
            this.id = DBHelper.getNextInsertId( con, "sl_city_id_seq" );

            statement.setLong(1, this.id.longValue());
            statement.setString(2, this.title);

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
            statement.setString(1, this.title);
            statement.setLong(2, this.id.longValue());
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
