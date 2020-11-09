/*
 * Created on 08.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.form_manager.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;
import com.negeso.module.DefaultNameNumerator;

/**
 * @author Shkabi
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Forms implements DbObject {
    private static final Logger logger = Logger.getLogger( Forms.class );
    private static String tableId = "forms";
    private static int fieldCount = 11;
    private final static String DEFAULT_FORM_NAME = "New Form";
    private static final String selectFromSql = 
        " SELECT id, lang_id, form_id, name, description, email, " +
        " creation_date, last_modification_date, article_id, site_id, ex " +
        " FROM " + tableId;

    private static final String findByIdSql = 
        selectFromSql + 
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + 
        tableId + 
        " (id, lang_id, form_id, name, description, email, creation_date," +
        "  last_modification_date, article_id, site_id, ex ) " +
        " VALUES (?, ?, ?, ?, ?, ?, now(), now(), ?, ?, ?)";

    private static final String updateSql =
        " UPDATE " +
        tableId +
        " SET id=?, lang_id=?, form_id=?, name=?, description=?," +
        " email=?, last_modification_date=now(), article_id=?," +
        " site_id=?, ex=? " +
        " WHERE id = ?";
   
    private Long id = null;
    private Long langId = new Long(1);
    private String formId = null;
    private String name = null;
    private String description = null;
    private String email = null;
	private String ex = null;
    private Timestamp creationDate = null;
    private Timestamp lastModificationDate = null;
    private Long articleId = null;
	private Long siteId = null;
	private Long pageId = null;
	
	private List<FormField> fields = new ArrayList<FormField>();
	private Article article = new Article();
	
    public Long getId() {
        return this.id;
    }

    public void setId(Long newId) {
        this.id = newId;
    }
    
    public Long getLangId() {
        return this.langId;
    }

    public void setLangId(Long newLangId) {
        this.langId = newLangId;
    }
    
    public String getFormId() {
        return this.formId;
    }

    public void setFormId(String newFormId) {
        this.formId = newFormId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }
 
    public void setDefaultName(Connection connection) {
    	
        name = DefaultNameNumerator.getDefaultName(connection,
                tableId,
                "name",
                DEFAULT_FORM_NAME
                );
    		
    }
    
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }
    
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }
	
	public String getEx() {
        return this.ex;
    }

    public void setEx(String newEx) {
        this.ex = newEx;
    }
    
    public Timestamp getCreationDate() {
        return this.creationDate;
    }
    
    public Timestamp getLastModificationDate() {
        return this.lastModificationDate;
    }
    
    public String getCreationDateAsString() {
        
        if (this.creationDate == null)
            return null;
        Calendar cl = Calendar.getInstance();
        cl.setTime(creationDate);

        return cl.get(Calendar.YEAR) +
               "-" + (cl.get(Calendar.MONTH)+1) +
               "-" + cl.get(Calendar.DAY_OF_MONTH);
    }
    
    public String getLastModificationDateAsString() {
        
        if (this.lastModificationDate == null)
            return null;
        Calendar cl = Calendar.getInstance();
        cl.setTime(lastModificationDate);

        return cl.get(Calendar.YEAR) + 
               "-" + (cl.get(Calendar.MONTH)+1) +
               "-" + cl.get(Calendar.DAY_OF_MONTH);
    }
    
    public Long getArticleId() {
        return this.articleId;
    }

    public void setArticleId(Long newArticleId) {
        this.articleId = newArticleId;
    }
	
	public Long getSiteId() {
        return this.siteId;
    }

    public void setSiteId(Long newSiteId) {
        this.siteId = newSiteId;
    }
      
    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = new Long(rs.getLong("id"));
            this.langId = new Long(rs.getLong("lang_id"));
            this.formId = rs.getString("form_id"); 
            this.name = rs.getString("name"); 
            this.description = rs.getString("description"); 
            this.email = rs.getString("email");
			this.ex = rs.getString("ex");
            this.creationDate = rs.getTimestamp("creation_date");
            this.lastModificationDate = rs.getTimestamp("last_modification_date");
            this.articleId = new Long(rs.getLong("article_id"));
			this.siteId = new Long(rs.getLong("site_id"));
        
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

  
    public Long insert(Connection con) throws CriticalException {
        try
        {
            PreparedStatement statement = con.prepareStatement(insertSql);
            saveIntoStatement(statement);
            statement.executeUpdate();
            statement.close();
            return this.id;
        }
        catch(SQLException e)
        {
        	throw new CriticalException();
        }
    }

   
    public void update(Connection con) throws CriticalException {
       try {
           PreparedStatement statement = con.prepareStatement(updateSql);
           saveIntoStatement(statement);
           statement.setLong(10, this.id.longValue());
           statement.execute();
           statement.close();
       }
       catch(SQLException e)
       {
           throw new CriticalException(e);
       }
    }

    public void delete(Connection con) throws CriticalException {
    	Statement updateStatement = null;
        try
        {
            updateStatement = con.createStatement();
            updateStatement.executeUpdate("DELETE FROM " + tableId +
                    " WHERE id=" + this.id);
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

   
    public PreparedStatement saveIntoStatement(PreparedStatement stmt) throws SQLException {
        
        logger.debug( "+" );
        // id
        stmt.setLong( 1, this.id.longValue());
        // lang_id
        stmt.setLong( 2, this.langId.longValue());
        // form_id
        stmt.setString( 3, this.formId);
        // name
        stmt.setString( 4, this.name);
        // description
        stmt.setString( 5, this.description);
        // email
        stmt.setString( 6, this.email);   
        // article_id
        stmt.setLong( 7, this.articleId.longValue());
		//site_id
        stmt.setLong( 8, this.siteId.longValue());
		//ex
        stmt.setString( 9, this.ex); 
        
        logger.debug( "-" );
        return stmt;
    }

    /*public static Forms findById(Connection connection, Long id) throws CriticalException {
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                Forms form = new Forms();
                form.load(result);
                result.close();
                statement.close();
                return form;
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
*/
	public List<FormField> getFields() {
		return fields;
	}

	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public void setLastModificationDate(Timestamp lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

}
