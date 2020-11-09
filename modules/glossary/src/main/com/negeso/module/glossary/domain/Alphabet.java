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

public class Alphabet implements DbObject{
    private Long id = null;
    private String letter = null;

    private final static String tableId = "d_alphabet";
    private final static String selectSql =
            " SELECT id, letter " +
            " FROM " + tableId;
    private final static String findByIdSql = selectSql +
            " WHERE id=? ";
    private final static String insertSql =
            " INSERT INTO " + tableId +
            " (id, letter) " +
            " VALUES ( ?, ? )";
    private final static String updateSql =
            " UPDATE "+ tableId + " SET letter=? " +
            " WHERE id=? ";
    private final static String deleteSql =
            " DELETE FROM " + tableId + " WHERE id=? ";
    private final static String alphabetSql =
            " SELECT DISTINCT " + tableId + ".id, " + tableId + ".letter, COUNT(d_words.id) AS amount " +
            " FROM " + tableId +
            " LEFT JOIN d_words ON " + tableId + ".letter=upper(substr(d_words.word, 1, 1))" +
            " GROUP BY " + tableId + ".id, " + tableId + ".letter " +
            " ORDER BY " + tableId + ".id ";
    
    private final static int fieldCount = 2;
    private static Logger logger = Logger.getLogger( Alphabet.class );


    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        id = newId;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String newLetter) {
        letter = newLetter;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = new Long(rs.getLong("id"));
            this.letter = rs.getString("letter");

            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    public static Alphabet findById(Connection connection, Long id)
            throws CriticalException{
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                Alphabet letter = new Alphabet();
                letter.load(result);
                result.close();
                statement.close();
                return letter;
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
            this.id = DBHelper.getNextInsertId( con, "d_alphabet_id_seq" );
            statement.setLong(1, this.id.longValue());
            statement.setString(2, this.letter);
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
            statement.setString(1, this.letter);
            statement.setLong(2, this.id.longValue());
            statement.execute();
            statement.close();
        }
        catch(Exception e){
            logger.error(e.getMessage());
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

    public static String getAlphabetSql(){
        return alphabetSql;
    }
}
