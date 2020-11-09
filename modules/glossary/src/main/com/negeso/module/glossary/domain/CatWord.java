package com.negeso.module.glossary.domain;

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
 * Mar 2, 2005
 */

public class CatWord implements DbObject{
    private Long id = null;
    private Long category_id = null;
    private Long word_id = null;

    private final static String tableId = "d_cat_word";
    private final static String selectSql =
            " SELECT id, category_id, word_id " +
            " FROM " + tableId;
    private final static String findByIdSql = selectSql +
            " WHERE id=? ";
    private final static String findByCategoryAndWordSql = selectSql +
			" WHERE category_id=? AND word_id=? ";

    private final static String insertSql =
            " INSERT INTO " + tableId +
            " (id, category_id, word_id) " +
            " VALUES ( ?, ?, ? )";
    private final static String updateSql =
            " UPDATE "+ tableId + " SET category_id=? , word_id=? " +
            " WHERE id=? ";
    private final static String deleteSql =
            " DELETE FROM " + tableId + " WHERE id=? ";
    private final static int fieldCount = 3;
    private static Logger logger = Logger.getLogger( CatWord.class );

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        id = newId;
    }

    public Long getCategoryId() {
        return category_id;
    }

    public void setCategoryId(Long newCategoryId) {
        category_id = newCategoryId;
    }

    public Long getWordId() {
        return word_id;
    }

    public void setWordId(Long newWordId) {
        word_id = newWordId;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = new Long(rs.getLong("id"));
            this.category_id = new Long(rs.getLong("category_id"));
            this.word_id = new Long(rs.getLong("word_id"));

            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    public static CatWord findById(Connection connection, Long id)
            throws CriticalException{
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                CatWord cat_word = new CatWord();
                cat_word.load(result);
                result.close();
                statement.close();
                return cat_word;
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

    public static CatWord findByCategoryAndWord(
    	Connection connection, Long catId, Long wordId)
	    throws CriticalException
	{
		try
		{
		    PreparedStatement statement = connection.prepareStatement(findByCategoryAndWordSql);
		    statement.setLong(1, catId.longValue());
		    statement.setLong(2, wordId.longValue());
		    ResultSet result = statement.executeQuery();
		    if (result.next())
		    {
		        CatWord cat_word = new CatWord();
		        cat_word.load(result);
		        result.close();
		        statement.close();
		        return cat_word;
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
        //todo
        return null;
    }

    public Long insert(Connection con) throws CriticalException {
        try{
            PreparedStatement statement = con.prepareStatement(insertSql);
            this.id = DBHelper.getNextInsertId( con, "d_cat_word_id_seq" );
            statement.setLong(1, this.id.longValue());
            statement.setLong(2, this.category_id.longValue());
            statement.setLong(3, this.word_id.longValue());
            statement.execute();
            statement.close();
        }
        catch(Exception e){
            logger.error(e.getMessage());
            throw new CriticalException("Error while insert");
        }
        return this.id;
    }

    public void update(Connection con) throws CriticalException {
        try{
            PreparedStatement statement = con.prepareStatement(updateSql);
            statement.setLong(1, this.category_id.longValue());
            statement.setLong(2, this.word_id.longValue());
            statement.setLong(3, this.id.longValue());
            statement.execute();
            statement.close();
        }
        catch(Exception e){
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
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
