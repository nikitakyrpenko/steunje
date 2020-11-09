package com.negeso.module.glossary.generators;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.module.glossary.domain.Alphabet;
import com.negeso.module.glossary.domain.Words;
import com.negeso.module.glossary.domain.Category;
import com.negeso.module.glossary.NavigationForm;

/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 2, 2005
 */

public class GlossaryXmlGenerator {
    private static Logger logger = Logger.getLogger( GlossaryXmlGenerator.class );

    public static void createHeaderXml(
    	Connection conn, Element parent, NavigationForm state)
    	throws CriticalException
    {
        Element glossary_header = Xbuilder.addEl(parent, "glossary_header", null);
        if (!state.getMode().equals(NavigationForm.FIRST_TIME_MODE)){
            glossary_header.setAttribute("show", state.getMode());
            Xbuilder.setAttr(glossary_header, "cat", state.getCategoryID());
            Xbuilder.setAttr(glossary_header, "find", state.getSearchWord());
            Xbuilder.setAttr(glossary_header, "wid", state.getWordID());
            Xbuilder.setAttr(glossary_header, "id", state.getLetterID());
            Xbuilder.setAttr(glossary_header, "action", state.getAction());
            if (state.getMode().equals(NavigationForm.LETTER_MODE)){
                glossary_header.setAttribute("field", ""+state.getLetterID().longValue());
            }
            else if (state.getMode().equals(NavigationForm.FIND_MODE)){
                glossary_header.setAttribute("field", state.getSearchWord());
            }
            else if (state.getMode().equals(NavigationForm.CATEGORY_MODE)){
                glossary_header.setAttribute("field", ""+state.getCategoryID().longValue());
            }
            if(state.getWordID()!=null){
                glossary_header.setAttribute("word_id", ""+state.getWordID().longValue());
            }
        }
        createAlphabetXml(conn, glossary_header, state);
        createCategoryXml(conn, glossary_header, state);
    }

    public static void createAlphabetXml(
    	Connection conn, Element parent, NavigationForm state) 
    	throws CriticalException
    {
    	
        Element glossary_alphabet = Xbuilder.addEl(parent, "glossary_alphabet", null);
        Element glossary_letter = null;
        try{
	        PreparedStatement statement = conn.prepareStatement(Alphabet.getAlphabetSql());
	        ResultSet res = statement.executeQuery();
	        while (res.next()){
	            glossary_letter = Xbuilder.addEl(glossary_alphabet, "glossary_letter", null);
	            if ( state.getMode().equals(NavigationForm.LETTER_MODE) &&
	                 state.getLetterID().longValue() == res.getLong("id")){
	                glossary_letter.setAttribute("selected", "true");
	            }
	
	            glossary_letter.setAttribute("id", res.getString("id"));
	            glossary_letter.setAttribute("letter", res.getString("letter"));
	            glossary_letter.setAttribute("amount", res.getString("amount"));
	        }
	        res.close();
        }
        catch(SQLException e){
        	logger.error("-error", e);
        	throw new CriticalException(e);
        }
 
    }

    public static void createSearchXml(
    	Connection conn, Element parent, NavigationForm state) throws CriticalException
    {
        Element glossary_search_results = Xbuilder.addEl(parent, "glossary_search_results", null);
        Element glossary_word;
        PreparedStatement stat = null;
        try{
	        if (state.getMode().equals(NavigationForm.ALL_MODE)){
	            stat = conn.prepareStatement(Words.getFindAllSql());
	        }
	        else if (state.getMode().equals(NavigationForm.CATEGORY_MODE)){
	            stat = conn.prepareStatement(Words.getFindByCategory());
	            Category category = Category.findById(conn, state.getCategoryID());
	            if ( category == null ){
	            	return;
	            }
	            stat.setLong(1, category.getId().longValue());
	            Xbuilder.setAttr(glossary_search_results, "category", category.getName());
	        }
	        else if (state.getMode().equals(NavigationForm.LETTER_MODE)){
	            stat = conn.prepareStatement(Words.getFindByLetterSql());
	            Alphabet letter = Alphabet.findById(conn, state.getLetterID());
	            stat.setString(1, letter.getLetter());
	            Xbuilder.setAttr(glossary_search_results, "letter", letter.getLetter());
	        }
	        else if (state.getMode().equals(NavigationForm.FIND_MODE)){
	            stat = conn.prepareStatement(Words.getFindBySearchWord());
	            stat.setString(1, state.getSearchWord().toLowerCase() + "%");
	            Xbuilder.setAttr(glossary_search_results, "search_word", state.getSearchWord());
	        }
	        ResultSet res = stat.executeQuery();
            String word = null;
            String currLetter = null;
            String tmp = null;
	        while(res.next()){
	            glossary_word = Xbuilder.addEl(glossary_search_results, "glossary_word", null);
	            glossary_word.setAttribute("id", res.getString("id"));
	            word = res.getString("word");
	            if ( word != null ){
		            glossary_word.setAttribute("word", word);
		            tmp = word.substring(0, 1).toUpperCase();
		            if ( !tmp.equals(currLetter) ){
		            	currLetter = tmp;
		            	glossary_word.setAttribute("letter", currLetter);
		            }
	            }
	            //glossary_word.setAttribute("description", res.getString("description"));
	        }
	        res.close();
	
	        if ( stat != null ) {
	        	stat.close();
	        }
        }
	    catch(SQLException e){
	    	logger.error("-error", e);
	    	throw new CriticalException(e);
	    }
    }

    /**
     * Create categories XML
     * 
     * @param conn
     * @param parent
     * @param state
     * @throws CriticalException
     */
    public static void createCategoryXml(
    	Connection conn, Element parent, NavigationForm state)
        throws CriticalException 
	{
        Element glossary_category_list = Xbuilder.addEl(parent, "glossary_category_list", null);
        Element glossary_category;
        try{
	        PreparedStatement stat = conn.prepareStatement(" SELECT id, name FROM d_category ");
	        ResultSet res = stat.executeQuery();
	        while (res.next()){
	            glossary_category = Xbuilder.addEl(glossary_category_list, "glossary_category", null );
	            if ( state != null ){
		            if ( state.getMode().equals(NavigationForm.CATEGORY_MODE) &&
		                 state.getCategoryID().longValue() == res.getLong("id")){
		                glossary_category.setAttribute("selected", "true");
		            }
	            }
	            glossary_category.setAttribute("id", res.getString("id"));
	            glossary_category.setAttribute("name", res.getString("name"));
	        }
	        res.close();
        }
        catch(SQLException e){
        	logger.error("-error", e);
        	throw new CriticalException(e);
        }
    }

     public static Element createWordXml(
     	Connection conn, Element parent, NavigationForm state) 
     	throws CriticalException
     {
         Words word = Words.findById(conn, state.getWordID());
         Article article = null;
         Element glossary_word_details = null;
         try{
             article = Article.findById(conn, word.getArticleID());
	         glossary_word_details = Xbuilder.addEl(parent, "glossary_word_details", article.getText());
	         glossary_word_details.setAttribute("id", ""+word.getId().longValue());
	         glossary_word_details.setAttribute("article_id", ""+word.getArticleID().longValue());
	         glossary_word_details.setAttribute("word", word.getWord());
         }
         catch(ObjectNotFoundException e){
         	logger.error("-error", e);
         	throw new CriticalException(e);
         }
         return glossary_word_details;
     }

	/**
	 * @param con
	 * @param wordEl
	 * @param wordID
	 * @throws CriticalException
	 */
	public static void createWordCategoriesXml(
		Connection con, Element parent, Long wordId) 
		throws CriticalException 
	{
        Element glossary_category_list = Xbuilder.addEl(parent, "glossary_category_list", null);
        Element glossary_category = null;
        try{
	        PreparedStatement stmt = con.prepareStatement( Words.getWordCategories );
	        stmt.setLong(1, wordId.longValue());
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()){
	            glossary_category = Xbuilder.addEl(glossary_category_list, "glossary_category", null );
            	if ( rs.getLong("word_id") > 0 ){
	                glossary_category.setAttribute("selected", "true");
	            }
	            glossary_category.setAttribute("id", rs.getString("id"));
	            glossary_category.setAttribute("name", rs.getString("name"));
	        }
	        rs.close();
        }
        catch(SQLException e){
        	logger.error("-error", e);
        	throw new CriticalException(e);
        }
	}
}
