package com.negeso.module.glossary.domain;

import com.negeso.framework.domain.DbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;


/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 2, 2005
 */

public class Words implements DbObject{
    private Long id = null;
    private String word = "";
    private Long article_id = null;

    public static final String DEFAULT_WORD_VALUE = "New word";
    public static final String DEFAULT_WORD_DESCRIPTION = "Word description";
    
    private final static String tableId = "d_words";
    private final static String selectSql =
            " SELECT id, word, article_id " +
            " FROM " + tableId;
    private final static String findByIdSql = selectSql +
            " WHERE id=? ";
    private final static String findAllSql = selectSql +
            " ORDER BY word ";
    private final static String insertSql =
            " INSERT INTO " + tableId +
            " (id, word, article_id) " +
            " VALUES ( ?, ?, ? )";
    private final static String updateSql =
            " UPDATE "+ tableId + " SET word=?, article_id=? " +
            " WHERE id=? ";
    private final static String deleteSql =
            " DELETE FROM " + tableId + " WHERE id=? ";
    private final static String findByLetterSql =
            " SELECT id, word, article_id " +
            " FROM " + tableId +
            " WHERE upper(substr(d_words.word, 1, 1))=? ORDER BY word ";
    private final static String findByCategory =
            " SELECT d_words.id, d_words.word, d_words.article_id " +
            " FROM " + tableId +
        	" LEFT JOIN d_cat_word ON " +
            " d_cat_word.category_id=? WHERE "+ tableId +".id=d_cat_word.word_id ORDER BY word  "; 
    private final static String findBySearchWord = 
            " SELECT id, word, article_id " +
            " FROM "+ tableId +
            " WHERE lower(word) LIKE ? ORDER BY word ";
    
    private final static String findBySearchWordNotLower = 
        " SELECT id, word, article_id " +
        " FROM "+ tableId +
        " WHERE word LIKE ? ORDER BY word ";

    public final static String getWordCategories = 
    		" SELECT d_category.id, d_category.name, COUNT(d_words.id) AS word_id " +
			" FROM d_category " +
			" LEFT JOIN d_cat_word ON d_category.id = d_cat_word.category_id " + 
			" LEFT JOIN d_words ON (d_words.id = d_cat_word.word_id) AND " +
			" (d_words.id = ?) " +
			" GROUP BY d_category.id, d_category.name "
	;
        

    private final static int fieldCount = 3;
    private static Logger logger = Logger.getLogger( Words.class );


    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        id = newId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String newWord) {
        word = newWord;
    }

    public Long getArticleID() {
        return article_id;
    }

    public void setArticleID(Long newArticleID) {
        article_id = newArticleID;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = new Long(rs.getLong("id"));
            this.word = rs.getString("word");
            this.article_id = new Long(rs.getLong("article_id"));

            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    public static Words findById(Connection connection, Long id)
            throws CriticalException{
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                Words words = new Words();
                words.load(result);
                result.close();
                statement.close();
                return words;
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
    
    public static ArrayList<Words> findByWord(Connection connection, String title)
    		throws CriticalException{
    	PreparedStatement statement = null;
    	ResultSet result = null;
    	ArrayList<Words> res = new ArrayList<Words>();
    	try {
            statement = connection.prepareStatement(findBySearchWordNotLower);
            statement.setString(1, title);
            result = statement.executeQuery();
            
            while (result.next()){
                Words words = new Words();
                words.load(result);
                res.add(words);
            }
            result.close();
            statement.close();
            return res;
        }
        catch(SQLException e){
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
            this.id = DBHelper.getNextInsertId( con, "d_words_id_seq" );
            statement.setLong(1, this.id.longValue());
            statement.setString(2, this.word);
            statement.setLong(3, this.article_id.longValue());
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
            statement.setString(1, this.word);
            statement.setLong(2, this.article_id.longValue());
            statement.setLong(3, this.id.longValue());
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

    public static String getFindByLetterSql(){
        return findByLetterSql;
    }

    public static String getFindAllSql(){
        return findAllSql;
    }

    public static String getFindByCategory(){
        return findByCategory;
    }

    public static String getFindBySearchWord(){
        return findBySearchWord;
    }
}
