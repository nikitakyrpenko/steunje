/*
 * @(#)Article.java       @version  10.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

import com.negeso.framework.Env;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.controller.SessionData;
import com.negeso.module.core.domain.ArticleRevision;
import com.negeso.module.core.service.ArticleRevisionService;
import com.negeso.module.form_manager.FormManagerException;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Article entity encapsulation. Implements ActiveRecord pattern.
 *
 *
 * @version     1.1
 * @author      Olexiy.Strashko
 */
public class Article extends DomainObject {

    private static Logger logger = Logger.getLogger( Article.class );

    protected static String tableId = "article";

    private final static String findByIdStatementString =
        " SELECT id, lang_id, head, text, class, container_id, last_modified " +
        "   FROM " + Article.tableId +
        "   WHERE id = ? ";

    private final static String findByClassStatementString =
        " SELECT article.* " +
        "   FROM article, language " +
        "   WHERE article.lang_id = language.id " +
        "   AND article.class = ? AND language.code = ? ";

    private final static String updateStatementString =
        " UPDATE " + Article.tableId +
        "   SET id = ?, lang_id = ?, head = ?, text = ?, class = ?, " +
        "   container_id = ?, last_modified=? " +
        "   WHERE id = ?";

    private final static String insertStatementString =
        " INSERT INTO " + Article.tableId +
        " (id, lang_id, head, text, class, container_id, last_modified)" +
        " VALUES (?, ?, ?, ?, ?, ?, ?) ";

    private Long lang_id = null;

    private String head = "Enter your text here";

    private String text = "Untitled article";

    private String class_ = "cls";
    
    private Long containerId;    
    
    private Timestamp last_modified = new Timestamp(System.currentTimeMillis());
    
    /* If tmporaryDate!=null - this article is temporary, created on this date */
    private Date temporaryDate;
    
    /**
     * Default Construction
     */
    public Article()
    {
        super( null );
    }


    /**
     * Protected Construction
     */
    protected Article( Long id, Long lang_id, String head, String text,
                       String class_, Long containerId, Timestamp last_modified)
    {
        super( id );
        this.lang_id = lang_id;
        this.head = head;
        this.text = text;
        this.class_ = class_;
        this.containerId = containerId;
        this.last_modified = last_modified;
    }


    /**
     * @return tableId - table name in DB
     */
    public String getTableId()
    {
        return Article.tableId;
    }


    /********************************************************************************************
     *  Static ActiveRecord pattern members
     */

    /**
     * Finds article by id. If object is not found - id is seted to null
     *
     * @param id
     * @return Article if object is found, null - if object not found
     * @throws SQLException
     */
    public static Article findById( Long id )
        throws CriticalException, ObjectNotFoundException
    {
        logger.debug( "+" );
        Article result = null;
        PreparedStatement findStatement = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            findStatement = conn.prepareStatement( findByIdStatementString );
            findStatement.setObject( 1, id, Types.BIGINT );
            rs = findStatement.executeQuery();
            if ( rs.next() == false ) {
                throw new ObjectNotFoundException( "Object: " + Article.tableId +
                    " not found by id: " + id );
            }
            result = load( rs );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.debug( ex, ex );
            throw new CriticalException( ex );
        }
        finally {
            DBHelper.close( conn );
        }
    }


    public static Article findById( Connection con, Long id )
	    throws CriticalException, ObjectNotFoundException
	{
	    logger.debug( "+" );
	    Article result = null;
	    PreparedStatement findStatement = null;
	    try {
	        findStatement = con.prepareStatement( findByIdStatementString );
	        findStatement.setObject( 1, id, Types.BIGINT );
	        ResultSet rs = findStatement.executeQuery();
	        if ( rs.next() == false ) {
	            throw new ObjectNotFoundException( "Object: " + Article.tableId +
	                " not found by id: " + id );
	        }
	        result = load( rs );
	        rs.close();
	        logger.debug( "-" );
	        return result;
	    } catch ( SQLException ex ) {
	        logger.debug( ex, ex );
	        throw new CriticalException( ex );
	    }
	    finally {
	        DBHelper.close( findStatement );
	    }
	}

    /**
     * Finds article by id. If object is not found - id is seted to null
     *
     * @param id
     * @return Article if object is found, null - if object not found
     * @throws SQLException
     */
    public static Article findByClass(String klass, String langCode)
        throws CriticalException, ObjectNotFoundException
    {
        logger.debug( "+" );
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            PreparedStatement findStatement =
                conn.prepareStatement(findByClassStatementString );
            findStatement.setString( 1, klass );
            findStatement.setString( 2, langCode );
            ResultSet rs = findStatement.executeQuery();
            if ( rs.next() == false ) {
                throw new ObjectNotFoundException(
                    " Object: " + Article.tableId +
                    " not found by class: " + klass );
            }
            Article result = load( rs );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.debug( ex, ex );
            throw new CriticalException( ex );
        }
        finally {
            DBHelper.close( conn );
        }
    }


    /**
     * Creates Article from current row of the RecordSet.
     *
     * @see Env
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Article load( ResultSet rs )
        throws SQLException
    {
        logger.debug("+");
        Article result = new Article(
            new Long(rs.getLong("id")),
            makeLong(rs.getLong("lang_id")),
            rs.getString("head"),
            rs.getString("text"),
            rs.getString("class"),
            makeLong(rs.getLong("container_id")),
            rs.getTimestamp( "last_modified" )
            );
        logger.debug("-");
        return result;
    }


    /********************************************************************************************
     *  Synamic ActiveRecord pattern members
     */

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DomainObject#load(long)
     */
    public void load( long id )
        throws ObjectNotFoundException, CriticalException
    {
        Article article = findById(new Long(id));
        this.copyFrom(article);
    }


    /**
     * Save into PreparedStatement
     *
     * @throws SQLException
     *
     */
    public PreparedStatement saveIntoStatement(
        PreparedStatement stmt) throws SQLException
    {
        Article.logger.debug( "+" );
        stmt.setObject(1, this.getId());
        stmt.setObject(2, this.getLangId(), Types.BIGINT );
        stmt.setString(3, this.getHead() );
        stmt.setString(4, this.getText() );
        stmt.setString(5, this.getClass_() );
        stmt.setObject(6, this.containerId);
        stmt.setObject(7, last_modified);
        Article.logger.debug( "-" );
        return stmt;
    }

    /**
     * Inserts article into db, if insertion sucseeded article id
     * is updated from aut-generated id by DB.
     *
     * @throws SQLException
     * @return new article id
     */
    public Long insert()
        throws CriticalException
    {
        logger.debug( "+" );
        PreparedStatement insertStatement = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            this.setId( DBHelper.getNextInsertId(conn, "article_id_seq"));
            insertStatement = conn.prepareStatement( insertStatementString );
            insertStatement = this.saveIntoStatement(insertStatement);
            insertStatement.execute();
            return this.getId();
        } catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        }
        finally {
            DBHelper.close( conn );
        }
    }

    public Long insert(Connection con)
	    throws CriticalException
	{
	    logger.debug( "+" );
	    PreparedStatement insertStatement = null;
	    try {
	        this.setId( DBHelper.getNextInsertId(con, "article_id_seq"));
	        insertStatement = con.prepareStatement( insertStatementString );
	        insertStatement = this.saveIntoStatement(insertStatement);
	        insertStatement.execute();
	        return this.getId();
	    } catch ( SQLException ex ) {
	        logger.debug( "-", ex );
	        throw new CriticalException( ex );
	    }
	    finally {
	        DBHelper.close( insertStatement );
	    }
	}
    
    

    /**
     * Updates article into DB.
     *
     * @throws SQLException
     */
    public void update()
        throws CriticalException
    {
        logger.debug( "+" );
        PreparedStatement updateStatement = null;
        Connection conn = null;
        try {
        	createRevision(this);
            conn = DBHelper.getConnection();
            updateStatement = conn.prepareStatement( updateStatementString );
            updateStatement = this.saveIntoStatement(updateStatement);
            updateStatement.setObject(8, this.getId(), Types.BIGINT);
            updateStatement.execute();
        } catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        } catch (ObjectNotFoundException e) {
        	logger.debug( "-", e );
            throw new CriticalException( e );
		}
        finally {
            DBHelper.close( conn );
        }
    }

    public void update(Connection conn)
        throws CriticalException
    {
        logger.debug( "+" );
        PreparedStatement updateStatement = null;
        try {
        	createRevision(this);
            updateStatement = conn.prepareStatement( updateStatementString );
            updateStatement = this.saveIntoStatement(updateStatement);
            updateStatement.setObject(8, this.getId(), Types.BIGINT);
            updateStatement.execute();
        }
        catch (SQLException ex) {
            logger.debug("- Throwing new CriticalException", ex);
            throw new CriticalException( ex );
        } catch (ObjectNotFoundException e) {
        	logger.debug("- Throwing new CriticalException", e);
            throw new CriticalException( e );
		}
        finally {
            DBHelper.close(updateStatement);
        }
        logger.debug("-");
    }

    /**
     * Deletes Article from DB
     *
     * @throws SQLException
     *
     */
    public void delete()
        throws CriticalException
    {
        Article.logger.debug( "+" );
        DBHelper.deleteObject( Article.tableId, this.getId(), Article.logger );
        this.setId( null );
        Article.logger.debug( "-" );
    }
    
    public void delete(Connection con)
    	throws CriticalException
	{
	    Article.logger.debug( "+" );
	    DBHelper.deleteObject( con, Article.tableId, this.getId(), Article.logger );
	    this.setId( null );
	    Article.logger.debug( "-" );
	}

    
    public String getLanguage() throws CriticalException
    {
        Article.logger.debug( "+" );
        try {
            Language lang = Language.findById( this.getLangId() );
            return lang.getCode();
        } catch ( ObjectNotFoundException ex ) {
            Article.logger.debug( "-" );
            return "en";
        }
    }

    public Element getDomElement()
        throws CriticalException
    {
        return this.getDom().getDocumentElement();
    }
    
    public Document getDom() throws CriticalException {
        logger.debug("+");
        Document doc = Env.createDom();
        doc.appendChild(createDomElement(doc));
        logger.debug("-");
        return doc;
    }
    
    public Element createDomElement(Document doc)
        throws CriticalException
    {
        logger.debug("+");
        Element articleElement = Env.createDomElement(doc, "article");
        articleElement.setAttribute("xmlns:negeso", Env.NEGESO_NAMESPACE);
        articleElement.setAttribute("id", this.getId().toString());
        articleElement.setAttribute("lang", this.getLanguage());
        articleElement.setAttribute("class", this.getClass_());
        if (this.last_modified != null)
            articleElement.setAttribute("last_modified",
            	this.getLastModified().toString());

        if (this.containerId != null)
            articleElement.setAttribute("containerId",
                this.containerId.toString());
        
        Element headElement = Env.createDomElement(doc, "head");
        if (this.head != null) { 
            headElement.appendChild(doc.createTextNode(this.head));
        }
        articleElement.appendChild(headElement);

        Element textElement = Env.createDomElement(doc, "text");
        if (this.text != null)
            textElement.appendChild(doc.createTextNode(this.text));
        articleElement.appendChild(textElement);

        logger.debug("-");
        return articleElement;
    }

    public String toString()
    {
        return "Article: id:" + this.getId()
            + " lang_id:" + this.getLangId()
            + " head:" + this.getHead()
            + " text:" + this.getText()
            + " class_:" + this.getClass_()
            + " last_modified:" + this.getLastModified();
    }


    /********************************************************************************************
     *  Setters/Getters
     */
    /**
     * @return Returns the class_.
     */
    public String getClass_()
    {
        return class_;
    }


    /**
     * @param class_ The class_ to set.
     */
    public void setClass_( String class_ )
    {
        this.class_ = class_;
    }


    /**
     * @return Returns the head.
     */
    public String getHead()
    {
        return head;
    }


    /**
     * @param head The head to set.
     */
    public void setHead( String head )
    {
        this.head = head;
    }


    /**
     * @return Returns the lang_id.
     */
    public Long getLangId()
    {
        return lang_id;
    }


    /**
     * @param lang_id The lang_id to set.
     */
    public void setLangId( Long lang_id )
    {
        this.lang_id = lang_id;
    }


    /**
     * @return Returns the text.
     */
    public String getText()
    {
        return text;
    }


    /**
     * @param text The text to set.
     */
    public void setText( String text )
    {
        this.text = text;
    }

    public Long getContainerId() {
        return this.containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }


	public Timestamp getLastModified() {
		return last_modified;
	}

	public void setLastModified(Timestamp last_modified) {
		this.last_modified = last_modified;
	}

    /**************************************************************
      * Copy data from arg to this
      * @param arg
      * @throws CriticalException
      **************************************************************/
     private void copyFrom( Article arg )
         throws CriticalException {

         this.setId(arg.getId());
         this.setLangId(arg.getLangId());
         this.setClass_(arg.getClass_());
         this.setHead(arg.getHead());
         this.setText(arg.getText());

         this.wasSynchronized = arg.isSynchronized();
     }

     
     public static void UpdateArticlesWithForm(
             Connection connection, 
             Long formArticleID)
         throws SQLException, FormManagerException, 
                CriticalException, ObjectNotFoundException
     {
         if (formArticleID== null)
         {
             throw new FormManagerException();
         }
         Article article = Article.findById(connection, formArticleID);
         if (article == null)
         {    
             throw new FormManagerException();
         }
         Statement statement = connection.createStatement();
         ResultSet result = statement.executeQuery(
                 " SELECT form_id, name, ex FROM forms " +
                 " WHERE article_id='" + formArticleID.toString()  + "'");
         if (!result.next())
         {
             throw new FormManagerException();
         }
         String newForm = 
             "<FORM class=contact " +
             "onsubmit=\"return validate(this);\" " +
             "method=post encType=multipart/form-data form_id=\""  + 
             result.getString(1) + "\">" +
             " <INPUT form_name='" + result.getString(2) + 
             "'  form_ex='" + result.getString(3)  +
             "' type=hidden value=" + result.getString(1) + 
             " name=mailToFlag>" + 
             "<input type='hidden' name='first_obligatory_email_field' value=''>" + 
             article.getText() + "</FORM>";
         PreparedStatement updateStatement = connection.prepareStatement(
                 " UPDATE article " +
                 " SET text= replace( text, " +
                 " substring( text from " +
                 "'<FORM class=contact " +
                 "onsubmit=\"return validate\\\\(this\\\\);\" " +
                 "method=post encType=multipart/form-data " +
                 "form_id=\""+
                 result.getString(1) +".*?</FORM>' ), ? )" +
                 " WHERE " +
                 " substring( text from " +
                 "'<FORM class=contact " +
                 "onsubmit=\"return validate\\\\(this\\\\);\" " +
                 "method=post encType=multipart/form-data " +
                 "form_id=\""+ 
                 result.getString(1) +"\".*?</FORM>' ) is not null ");
         updateStatement.setString( 1, newForm );
         updateStatement.execute();
         DBHelper.close(result);
         DBHelper.close(statement);
         DBHelper.close(updateStatement);
     }
     
     public static void deleteFormInArticles(Connection connection, Long formId) 
         throws SQLException
     {
         Statement statement = connection.createStatement();
         statement.executeUpdate(
                 " UPDATE article " +
                 " SET text= replace( text, " +
                 " substring( text from " +
                 "'<FORM class=contact " +
                 "onsubmit=\"return validate\\\\(this\\\\);\" " +
                 "method=post encType=multipart/form-data " +
                 "form_id=\""+
                 formId.toString() +".*?</FORM>' ), '' )" +
                 " WHERE " +
                 " substring( text from " +
                 "'<FORM class=contact " +
                 "onsubmit=\"return validate\\\\(this\\\\);\" " +
                 "method=post encType=multipart/form-data " +
                 "form_id=\""+ 
                 formId.toString() +".*?</FORM>' ) is not null ");
         statement.close();
     }


	public Long getLang_id() {
		return lang_id;
	}


	public void setLang_id(Long lang_id) {
		this.lang_id = lang_id;
	}


	public Date getTemporaryDate() {
		return temporaryDate;
	}


	public void setTemporaryDate(Date temporaryDate) {
		this.temporaryDate = temporaryDate;
	}

	public static Element buildArticle(String articleClassName, Element parent, Language lang){
		logger.debug("+");
		Article article = null;
		try {
			article = Article.findByClass(
				articleClassName, lang.getCode()
			);
		} catch(ObjectNotFoundException e){
			logger.info("Creating new article");
			article = new Article();
			article.setClass_(articleClassName);
			article.setLangId( lang.getId() );
			article.insert();
		}
		Element el = article.createDomElement(parent.getOwnerDocument());
		parent.appendChild(el);
		logger.debug("-");		
		return el;
	}
	
	private void createRevision(Article article) throws CriticalException, ObjectNotFoundException {
		ArticleRevision articleRevision = new ArticleRevision(article.getId(), article.getText());
		User user = (User) ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession().getAttribute(SessionData.USER_ATTR_NAME);
		if ( user != null ) {
			articleRevision.setAuthor(user.isSuperUser() ? user.getSuperuserLogin() : user.getLogin());
		}
		ArticleRevisionService articleRevisionService = 
			(ArticleRevisionService)DispatchersContainer.getInstance().getBean("core", "articleRevisionService");
		articleRevisionService.create(articleRevision);
	}

}
