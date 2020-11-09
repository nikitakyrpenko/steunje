/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.form_manager.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import org.springframework.transaction.annotation.Transactional;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.module.form_manager.FormAnalyzer;
import com.negeso.module.form_manager.FormManagerException;
import com.negeso.module.form_manager.UniqueNameViolationException;
import com.negeso.module.form_manager.dao.FormsDao;
import com.negeso.module.form_manager.domain.Forms;

/**
 * 
 * @TODO
 * 
 * @author		Pochapskiy Olexandr
 * @version		$Revision: $
 *
 */
@Transactional
public class FormService {
	
	private FormsDao formsDao;
	
	public Forms findById(Long formId){
		return formsDao.read(formId);
	}
	
	public void updateForm(Forms form)
	    throws SQLException, NumberFormatException, 
	           ObjectNotFoundException, 
	           FormManagerException, UniqueNameViolationException
	{
	    FormAnalyzer formAnalyzer = new FormAnalyzer();
	    formAnalyzer.setForm(form);
	    formAnalyzer.parseFormFields(form.getArticle().getText());
	    try {
	    	formsDao.createOrUpdate(form);
	    } catch (CriticalException e) {
	    	throw new UniqueNameViolationException();
	    }
	}    
	
	public void deleteForm(Long formId)
	    throws FormManagerException, CriticalException, 
	           ObjectNotFoundException, SQLException
	{
		Connection connection = DBHelper.getConnection();
	    Forms form = findById(formId);
	    
	    if(form != null){
		    Article article = Article.findById(form.getArticle().getId());
		    if(article == null){
		        throw new FormManagerException();
		    }
		   
		    form.delete(connection);
		    article.delete();  
		    Article.deleteFormInArticles(connection, formId);
	    }
	    
	    /*
	     *  ResultSet result = statement.executeQuery(
            " SELECT article_id, form_id " +
            " FROM forms " +
            " WHERE id ='" + getRequestContext().getParameter("form_id") + "'");
        if( result.next())
        {
            Article article = 
                Article.findById(new Long(result.getLong("article_id")));
            if(article == null)
            {
                result.close();
                throw new FormManagerException();
            }
            result.close();
            
            Forms form = 
                Forms.findById(connection, 
                        new Long(getRequestContext().getParameter("form_id")));            
            
            form.delete(connection);
            article.delete();
            
            Article.deleteFormInArticles(
                    connection, 
                    new Long(getRequestContext().getParameter("form_id")));
        }
        else
        {
            result.close();
            throw new FormManagerException();
        }*/
	}
	
	public String addForm(String formLanguage) throws FormManagerException, SQLException,  ObjectNotFoundException{
		
		Connection connection  = DBHelper.getConnection();
		Language lang = Language.findByCode(formLanguage);
        
        if (lang == null)
        {
            lang = Language.getDefaultLanguage();
        }
        String defaultEmail = Env.getDefaultEmail();
        
        Article article = new Article();
        article.setLangId(lang.getId());
        article.setText("This is a new form<br>" +
                "<table border='0' cellspacing='0' cellpadding='0'>" +
                "<tr><td>Field1:</td><td><input name='Field1' value='' required=\"false\" ></td></tr>" +
                "<tr><td>Field2:</td><td><input name='Field2' value='' required=\"false\" ></td></tr>" +
                "<tr><td>Email:</td><td><input name='Field2' value='' required=\"false\" ></td></tr>" +
                "<tr><td><INPUT class='form_submit' type='submit' value='Submit'></td><td></td></tr>" +
                "</table>");
        
        article.setHead("");
        article.setClass_("form");
        Long articleID = article.insert(connection);
        if ( articleID == null )
        {
            throw new FormManagerException();
        }
        
        Forms form = new Forms();
        form.setId(DBHelper.getNextInsertId(connection, "forms_id_seq"));
        form.setLangId(lang.getId());
        form.setFormId(getUniqueFormID(connection));
        form.setDefaultName(connection);
        form.setDescription("new form");
        form.setEmail(defaultEmail);
		form.setEx(" ");
        form.setArticleId(articleID);
		form.setSiteId(Env.getSiteId());
        return form.insert(connection).toString();
	}
	
	private String getUniqueFormID(Connection conn)
	    throws SQLException
	{
		Statement st = conn.createStatement();
	    boolean fl = false;
	    String uniqueID = null;
	    while (!fl)
	    {
	        uniqueID = "" + Calendar.getInstance().getTimeInMillis();
	        ResultSet result = st.executeQuery(
	                " SELECT form_id " +
	                " FROM forms " +
	                " WHERE form_id='" + uniqueID + "'");
	        if (!result.next())
	        {
	            fl=true;
	        }
	        result.close();
	    }
	    return uniqueID;
}

	public FormsDao getFormsDao() {
		return formsDao;
	}

	public void setFormsDao(FormsDao formsDao) {
		this.formsDao = formsDao;
	}
}

