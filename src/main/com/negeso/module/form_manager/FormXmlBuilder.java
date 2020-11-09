/*
 * Created on 27.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.form_manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.generators.Xbuilder;


/**
 * @author Shkabi
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FormXmlBuilder {
    public static void buildLanguages(
            Element parentElement, 
            Statement statement, 
            RequestContext request) 
        throws SQLException
    {
        String formLanguage = parentElement.getAttribute("lang");
        Element languagesElement = 
            Xbuilder.createEl(parentElement, "languages", null);
        parentElement.appendChild(languagesElement);
        
        ResultSet result = statement.executeQuery("SELECT code FROM language;");
        Element bufferElement = null;
        while (result.next())
        {
            bufferElement = Xbuilder.createEl(languagesElement, "language", null);
            bufferElement.setAttribute( "code", result.getString(1).trim());
            if ( result.getString(1).equals(formLanguage))
            {
                bufferElement.setAttribute( "current", "true" );
            }
            languagesElement.appendChild( bufferElement );
        }
        result.close();
    }
    
    public static void buildForms (
            Element parentElement, 
            Statement statement, 
            RequestContext request)
        throws SQLException
    {
        String formLanguage = parentElement.getAttribute("lang");
        Element formsElement = Xbuilder.createEl(parentElement, "forms", null);
        parentElement.appendChild(formsElement);
        ResultSet result = 
            statement.executeQuery("SELECT forms.id, forms.name " +
                    "FROM forms, language " +
                    "WHERE language.code = '" + formLanguage + "' " +
                    "AND forms.lang_id = language.id AND forms.site_id="+Env.getSiteId()+
		    " ORDER BY forms.name");
        Element bufferElement = null;
        while (result.next())
        {
            bufferElement = Xbuilder.createEl(formsElement, "form", null);
            bufferElement.setAttribute("id", result.getString(1));
            bufferElement.setAttribute("name", result.getString(2));
            formsElement.appendChild(bufferElement);
        }
        result.close();
    }
    
    public static void buildForm (
            Element parentElement, 
            Connection connection,
            Statement statement, 
            RequestContext request, 
            String ID)
        throws SQLException, FormManagerException, CriticalException, ObjectNotFoundException
    {
        ResultSet result = 
            statement.executeQuery("SELECT name, description, email, " +
                    "creation_date, last_modification_date, article_id, ex, mail_success_page_id " +
                    "FROM forms " +
                    "WHERE id='" + ID + "'");
        if (result.next())
        {
            Article article = Article.findById( connection, new Long( result.getLong("article_id")));
            if (article == null)
            {
                throw new FormManagerException();
            }
            Element bufferElement = 
                Xbuilder.createEl(parentElement, "form", article.getText());
            bufferElement.setAttribute("article_id", result.getString("article_id"));
            bufferElement.setAttribute("id", ID);
            bufferElement.setAttribute("name", result.getString("name"));
            bufferElement.setAttribute("description", 
                    result.getString("description"));
            bufferElement.setAttribute("email", result.getString("email"));
			bufferElement.setAttribute("ex", result.getString("ex"));
			Xbuilder.setAttr(bufferElement, "page_id", result.getLong("mail_success_page_id"));

            Calendar cl = Calendar.getInstance();
            cl.setTime(result.getTimestamp("creation_date"));                        
            bufferElement.setAttribute("creation_date",
                    cl.get(Calendar.YEAR)+"-"
                    + (cl.get(Calendar.MONTH)+1)+"-"
                    + cl.get(Calendar.DAY_OF_MONTH));

            cl.setTime(result.getTimestamp("last_modification_date"));
            bufferElement.setAttribute("last_modification_date",
                    cl.get(Calendar.YEAR)+"-"
                    + (cl.get(Calendar.MONTH)+1)+"-"
                    + cl.get(Calendar.DAY_OF_MONTH));

            parentElement.appendChild(bufferElement);
        }
        result.close();
        buildSuccessMailPageList(parentElement, request, connection);
    }
    
    public static void buildSuccessMailPageList(Element parentElement, RequestContext request, Connection connection) throws SQLException {
    	 String formLanguage = request.getParameter("form_language");
    	 if ( formLanguage == null ) {
             formLanguage = request.getSession().getLanguageCode();  
         }
         PreparedStatement stmt = connection.prepareStatement("SELECT * FROM page WHERE class = 'mail' AND lang_id = ?");
         stmt.setLong(1, Language.getByCode(formLanguage).getId());
         ResultSet rs = stmt.executeQuery();
         Element pagesEl = Xbuilder.createEl(parentElement, "pages", "");
         while (rs.next()) {
         	Element pageEl = Xbuilder.createEl(parentElement, "page", "");
         	Xbuilder.setAttr(pageEl, "id", rs.getInt("id"));
         	Xbuilder.setAttr(pageEl, "title", rs.getString("title"));
         	Xbuilder.setAttr(pageEl, "filename", rs.getString("filename"));
         	pagesEl.appendChild(pageEl);
         }
         parentElement.appendChild(pagesEl);
    } 
   
    
    public static void buildFullFormList(
                Connection connection,
                Statement statement,
                Element parentElement,
                String languageCode)
        throws SQLException
    {
        ResultSet result = statement.executeQuery(
                " SELECT forms.id, forms.form_id, forms.name, forms.email, " +
                " article.text, forms.ex " +
                " FROM forms, article " +
                " WHERE forms.site_id="+Env.getSiteId()+" AND article.id=forms.article_id AND " +
                " forms.lang_id IN " +
                " (SELECT id " +
                "   FROM language " +
                "   WHERE code='" + languageCode + "')");
        Element bufferElement = null;
        while(result.next())
        {
            bufferElement = Xbuilder.createEl(
                    parentElement, "form",  result.getString(5));
            bufferElement.setAttribute("id", result.getString(1));
            bufferElement.setAttribute("form_id", result.getString(2));
            bufferElement.setAttribute("name", result.getString(3));
            bufferElement.setAttribute("email", result.getString(4));
			bufferElement.setAttribute("ex", result.getString(6));
            parentElement.appendChild(bufferElement);
        }
        result.close();
    }
}
