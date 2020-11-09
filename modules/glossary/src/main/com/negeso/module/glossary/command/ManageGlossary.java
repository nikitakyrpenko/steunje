/*
 * @(#)$Id: ManageGlossary.java,v 1.6, 2005-06-06 13:04:48Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.glossary.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.ActiveModuleRequired;
import com.negeso.framework.command.ProtectedCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.Timer;
import com.negeso.module.DefaultNameNumerator;
import com.negeso.module.form_manager.UniqueNameViolationException;
import com.negeso.module.glossary.NavigationForm;
import com.negeso.module.glossary.domain.CatWord;
import com.negeso.module.glossary.domain.Category;
import com.negeso.module.glossary.domain.Words;
import com.negeso.module.glossary.generators.GlossaryXmlGenerator;

/**
 *
 *  Glossary management command
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 7$
 */
@ProtectedCommand
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ManageGlossary extends AbstractCommand {
	private static Logger logger = Logger.getLogger( ManageGlossary.class );

	private static final String ERROR_CLAUSE_NOT_UNIQ_WORD = "word_is_not_uniq";
	
	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	@ActiveModuleRequired
	public ResponseContext execute() {
		logger.debug("+");
		RequestContext request = this.getRequestContext();
		ResponseContext response = new ResponseContext();
		
		if ( !SecurityGuard.isContributor(request.getSession().getUser()) ){
			response.setResultName( AbstractCommand.RESULT_ACCESS_DENIED );
			return response;
		}
		
		Timer timer = new Timer();
		timer.start();
		
		Connection con = null;
		Element page = null;
		
		try{
			NavigationForm state = new NavigationForm(request);
			page = XmlHelper.createPageElement(request);
			
			con = DBHelper.getConnection();
            con.setAutoCommit(true);
			
			try{
				if ( "save_word".equals( state.getAction() )  ) {
					this.doSaveWord( con, request );
				}
				if ( "add_word".equals( state.getAction() )  ) {
					state.setWordID( this.doAddWord( con, request ) ); 
				}
				if ( "delete_word".equals( state.getAction() )  ) {
					this.doDeleteWord( con, request ); 
				}
	            con.commit();
			}catch(UniqueNameViolationException e){
				logger.warn("-word is already exists", e);
				Element error = Xbuilder.addEl(page, "error-clause", null);
				error.setAttribute("caused-by", ERROR_CLAUSE_NOT_UNIQ_WORD);
				state.setWordID(request.getLong("wordId"));
			}
			
            GlossaryXmlGenerator.createHeaderXml(con, page, state);
            
            if( state.getWordID() != null ){
                Element wordEl = GlossaryXmlGenerator.createWordXml(con, page, state);
                GlossaryXmlGenerator.createWordCategoriesXml(con, wordEl, state.getWordID());
            }
            else{
                if (!state.getMode().equals(NavigationForm.FIRST_TIME_MODE)){
                    GlossaryXmlGenerator.createSearchXml(con, page, state);
                }
            }
			response.setResultName(RESULT_SUCCESS);
		}
		catch(CriticalException e){
			logger.error("-error", e);
			response.setResultName(RESULT_FAILURE);
		} 
		catch(SQLException e) {
			logger.error("-error", e);
			response.setResultName(RESULT_FAILURE);
		}

		if ( page != null ){
			response.getResultMap().put( OUTPUT_XML, page.getOwnerDocument() );
		}
		logger.info("time:" + timer.stop());
		
		logger.debug("-");
		return response;
	}

	/**
	 * @param con
	 * @param request
	 * @throws CriticalException
	 */
	private void doDeleteWord(Connection con, RequestContext request) 
		throws CriticalException 
	{
		Long wordId = request.getLong("removingWordId");
		if ( wordId == null ){
			logger.error("-error!!! wordId is not set!!!");
			return;
		}
		
		Words word = Words.findById(con, wordId);
		if ( word == null ){
			logger.error("-error!!! wordId not found by id: " + wordId);
			return;
		}
		
		word.delete( con );
	}

	/**
	 * @param con
	 * @param request
	 * @throws CriticalException
	 */
	private Long doAddWord(Connection con, RequestContext request) 
		throws CriticalException 
	{
		Words word = new Words();
		word.setWord(DefaultNameNumerator.getDefaultName(con,getNumbersSqlWords(),Words.DEFAULT_WORD_VALUE));  
		Article article = new Article();
		article.setText(Words.DEFAULT_WORD_DESCRIPTION); 
		article.insert();
		word.setArticleID( article.getId() );
		word.insert( con );
		return word.getId();
	}
	
	private String getNumbersSqlWords(){
    	StringBuffer query = new StringBuffer(700);
    	return query.append("SELECT DISTINCT CASE WHEN substr(word, 10) = '' THEN '0'").
    		append(" ELSE substr(word, 10) ").
    		append(" END ").
    		append(" as number FROM d_words ").
    		append(" WHERE (word ~ '" + Words.DEFAULT_WORD_VALUE + " [0-9]+' ").
    		append(" OR word  =  '"+ Words.DEFAULT_WORD_VALUE + "') ").
    		append(" ORDER by number ").toString();
    }

	/**
	 * @param con
	 * @param request
	 * @throws CriticalException
	 */
	private void doSaveWord(Connection con, RequestContext request) 
		throws CriticalException, UniqueNameViolationException
	{
		Long wordId = request.getLong("wordId");
		if ( wordId == null ){
			logger.error("-error!!! wordId is not set!!!");
			return;
		}
		
		Words word = Words.findById(con, wordId);
		if ( word == null ){
			logger.error("-error!!! wordId not found by id: " + wordId);
			return;
		}
		
		String name = request.getNonblankParameter("title");
		
		ArrayList<Words> words = Words.findByWord(con, name);
		
		//!!! need refacoring 
		//due to strange procedure of add new words to dictionary.
		//first it create record in database then, initialize it, but don't remove record if press 
		//Back after creating
		if (words.size() != 0){
			if (words.size() != 1 || !isDefault(wordId, words)){
				logger.warn("-word is alredy in dictionary");
				throw new UniqueNameViolationException();
			}
		}
		
//		if (words != null && words.size() == 2 && name == Words.DEFAULT_WORD_VALUE){
//			logger.debug("def. value in dictionary");
//		}else
//		if (words.size() > 0){
//			logger.warn("-word is alredy in dictionary");
//			throw new UniqueNameViolationException();
//		}

		if ( name != null ){
			word.setWord(name);
			word.update(con);
		}
		
		/*
		Long[] categoryIds = request.getLongs("categories");
		logger.error("cat len:" + categoryIds);
		
		CatWord catWord = null;
		for ( int i = 0; i < categoryIds.length; i++ ){
			logger.error("asdasd: " + categoryIds[i]);
			catWord = CatWord.findByCategoryAndWord(
				con, categoryIds[i], wordId
			);
			
			if (catWord == null ){
				catWord = new CatWord();
				catWord.setWordId(wordId);
				catWord.setCategoryId(categoryIds[i]);
				catWord.insert(con);
			}

			}
			else{
				if ( catWord != null ){
					catWord.delete(con);
				}
			}
		}
		*/
		
		Long[] categoryIds = request.getLongs("categories");
		// process categories
		PreparedStatement stmt = null;
		try{
			stmt = con.prepareStatement(Category.selectSql);
			ResultSet rs = stmt.executeQuery();
			Long catId = null;
			CatWord catWord = null;
			while ( rs.next() ){
				catId = DBHelper.makeLong(rs.getLong("id"));
				catWord = CatWord.findByCategoryAndWord(con, catId, wordId);
					
				boolean found = false; 
				for ( int i = 0; i < categoryIds.length; i++ ){
					if ( catId.equals( categoryIds[i] ) ){
						if ( catWord == null ){
							catWord = new CatWord();
							catWord.setWordId(wordId);
							catWord.setCategoryId(catId);
							catWord.insert(con);
						}
						found = true;
						break;
					}
				}

				if ( !found ){
					if ( catWord != null ){
						catWord.delete(con);
					}
				}
			}
		}
		catch(SQLException e){
			logger.error("-error", e);
		}
	}
	
	private boolean isDefault(Long id, ArrayList<Words> words){
		Iterator<Words> i = words.iterator();
		while (i.hasNext()){
			if (i.next().getId() == id.intValue()){
				return true;
			}
		}
		return false;
	}

	@Override
	public String getModuleName() {
		return ModuleConstants.GLOSSARY_MODULE;
	}
}
