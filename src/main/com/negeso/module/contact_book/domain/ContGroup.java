package com.negeso.module.contact_book.domain;

import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

import java.sql.*;

import org.apache.log4j.Logger;


/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Feb 23, 2005
 */

public class ContGroup implements DbObject {
    private static Logger logger = Logger.getLogger( ContGroup.class );
    private static String tableId = "cb_cont_group";
    private static int fieldCount = 3;
    private static final String selectFromSql =
        " SELECT id, contact_id, group_id " +
        " FROM " + tableId;

    private static final String selectByGroupAndContactSql =
        selectFromSql +
		" WHERE group_id=? AND contact_id=?";

    private static final String selectByContactIdSql =
        selectFromSql +
		" WHERE contact_id=?";

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " +
        tableId +
        " (id, contact_id, group_id) " +
        " VALUES (?, ?, ?)";

    private final static String updateSql =
        " UPDATE " +
        tableId +
        " SET contact_id=?, group_id=? " +
        " WHERE id = ?";
    
    private final static String contactListByGroupId =  
        " SELECT id, first_name, second_name, image_link, email, " +
        " department, job_title, birth_date, phone, web_link, nickname " +
        " FROM contact " +
        " WHERE cb_cont_group.group_id=? AND contact.id=cb_cont_group.contact_id ";

    Long id = null;
    Long contact_id = null;
    Long group_id = null;

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

    public Long getGroupId() {
        return group_id;
    }

    public void setGroupId(Long newGroupId) {
        group_id = newGroupId;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = new Long(rs.getLong("id"));
            this.contact_id = new Long(rs.getLong("contact_id"));
            this.group_id = new Long(rs.getLong("group_id"));

            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt) throws SQLException {
        logger.debug( "+" );
        stmt.setLong( 1, this.id.longValue());
        stmt.setLong( 2, this.contact_id.longValue());
        stmt.setLong( 3, this.group_id.longValue());

        logger.debug( "-" );
        return stmt;
    }

    public Long insert(Connection con) throws CriticalException {
        try{
            PreparedStatement statement = con.prepareStatement(insertSql);
            this.id = DBHelper.getNextInsertId( con, "cb_cont_group_id_seq" );

            statement.setLong(1, this.id.longValue());
            statement.setLong(2, this.contact_id.longValue());
            statement.setLong(3, this.group_id.longValue());

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
            statement.setLong(2, this.group_id.longValue());
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

    public static ContGroup findById(Connection connection, Long id)
        throws CriticalException
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                ContGroup contGroup = new ContGroup();
                contGroup.load(result);
                result.close();
                statement.close();
                return contGroup;
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

    public static ContGroup findByGroupAndContact(
    	Connection connection, Long groupId, Long contactId)
	    throws CriticalException
	{
	    try
	    {
	    	if (contactId == null){
	    		logger.error("Error: ContactId is null!!!!");
	    		return null;
	    	}
	    	if (groupId == null){
	    		logger.error("Error: GroupId is null!!!!");
	    		return null;
	    	}
	        PreparedStatement statement = connection.prepareStatement(selectByGroupAndContactSql);
	        statement.setLong(1, groupId.longValue());
	        statement.setLong(2, contactId.longValue());
	        ResultSet result = statement.executeQuery();
	        if (result.next())
	        {
	            ContGroup contGroup = new ContGroup();
	            contGroup.load(result);
	            result.close();
	            statement.close();
	            return contGroup;
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

    /**
     * Find contact by id
     * 
     * @param connection
     * @param contactId
     * @return
     * @throws CriticalException
     */
    public static ContGroup findByContactId(
    	Connection connection, Long contactId)
	    throws CriticalException
	{
	    try
	    {
	    	if (contactId == null){
	    		logger.error("Error: ContactId is null!!!!");
	    		return null;
	    	}
	        PreparedStatement statement = connection.prepareStatement(selectByContactIdSql);
	        statement.setLong(1, contactId.longValue());
	        ResultSet result = statement.executeQuery();
	        if (result.next())
	        {
	            ContGroup contGroup = new ContGroup();
	            contGroup.load(result);
	            result.close();
	            statement.close();
	            return contGroup;
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

    public static String getContactListByGroupId(){
        return contactListByGroupId;
    }
}
