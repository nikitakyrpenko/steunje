package com.negeso.module.contact_book.domain;

import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.form_manager.domain.Forms;

import java.sql.*;

import org.apache.log4j.Logger;


/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Feb 23, 2005
 */

public class Group implements DbObject {
    private static Logger logger = Logger.getLogger( Group.class );
    private static String tableId = "cb_group";
    private static int fieldCount = 2;
    private static final String selectFromSql =
        " SELECT id, title " +
        " FROM " + tableId;

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, title) " +
        " VALUES ( ?, ? )";

    private final static String updateSql =
        " UPDATE " +
        tableId +
        " SET title=? " +
        " WHERE id = ?";

    private final static String groupListSql =
            " SELECT id, title, " +
            " ( SELECT count(*) FROM cb_cont_group " +
            "   WHERE group_id=cb_group.id ) AS amount "+
            " FROM " + tableId;

    Long id = null;
    String title = "";

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

            return this.id;
        }
        catch(SQLException e){
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
            this.id = DBHelper.getNextInsertId( con, "cb_group_id_seq" );
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
    	Statement updateStatement = null;
        try
        {
            updateStatement = con.createStatement();
            updateStatement.executeUpdate("DELETE FROM " + tableId +
                    " WHERE id='" + this.id + "' ");
        }
        catch (SQLException e)
        {
            throw new CriticalException(e);
        }finally{
        	DBHelper.close(updateStatement);
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

    public static Group findById(Connection connection, Long id)
        throws CriticalException
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                Group group = new Group();
                group.load(result);
                result.close();
                statement.close();
                return group;
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

    public static String getGroupListSql(){
        return groupListSql;
    }
}
