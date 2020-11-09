/*
 * @(#)$Id: SiteUrl.java,v 1.4, 2005-06-06 13:05:09Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.site;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.AbstractDbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

/**
 *
 * Site URL domain object
 * 
 * @version		$Revision: 5$
 * @author		Olexiy Strashko
 * 
 */
public class SiteUrl extends AbstractDbObject {

	private static final long serialVersionUID = 4500168169355068466L;
	
	private static Logger logger = Logger.getLogger( SiteUrl.class );
    private static String tableId = "site_url";

    private static final String selectFromSql =
        " SELECT * " +
        " FROM " + tableId;

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";
    
    private static final String findBySiteIdSql =
        selectFromSql +
        " WHERE site_id = ? ";   

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, site_id, url, is_main, lang_id, is_single_lang, show_lang_selector) "+
        " VALUES (?,?,?,?,?,?,?) ";

    private static final String updateSql =
        " UPDATE " + tableId +
        " SET id=?, site_id=?, url=?, is_main=?, lang_id=?, is_single_lang=?, show_lang_selector=? " +
        " WHERE id=? ";

    private static final String findByUrl =
    	selectFromSql +
        " WHERE lower(url)=?";

    private static int fieldCount = 7;
    
    private Long id = null;
    private Long siteId = null;
    private String url = null;
    private Boolean main = false;
    private Long langId = null;
    private Boolean isSingleLanguage = false;
    private Boolean showLangSelector = true;
    private List<Long> langIds = new ArrayList<Long>();
    private String bing = null;
    private String analytic = null;
    private String verification = null;
    private String tagManager = null;

    /**
     * Resolve url to site id 
     * 
     * @param request
     * @return
     * @throws CriticalException 
     */
    public static Long resolveUrlToSiteId(String url) {
        Connection con = null;
        try{
            con = DBHelper.getConnection();
            PreparedStatement stmt = con.prepareStatement(findByUrl);
            stmt.setString(1, url);
            ResultSet rs = stmt.executeQuery();
            if ( rs.next() ){
                return DBHelper.makeLong(rs.getLong("site_id"));
            }
            return null;
        }
        catch(SQLException e){
            logger.error("error", e);
            throw new CriticalException(e);
        }
        finally{
            DBHelper.close(con);
        }
    }

    /**
     * Find Site url by id
     * 
     * @param siteId
     * @return
     * @throws CriticalException 
     */
    public static SiteUrl findById(Connection con, Long id) 
        throws CriticalException 
    {
        return (SiteUrl) DBHelper.findDbObjectById(con, new SiteUrl(), id);
    }
    
    public static SiteUrl findByUrl(Connection con, String url) 
	    throws CriticalException 
	{
    	logger.debug( "+" );
		try {			
			PreparedStatement stmt = null;
			stmt = con.prepareStatement( findByUrl );
			stmt.setString(1, url);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new SiteUrl(
						rs.getLong("id"),
						rs.getString("url"),
						rs.getLong("site_id"),
						rs.getBoolean("is_main"),
						DBHelper.makeLong(rs.getLong("lang_id")),
						rs.getBoolean("is_single_lang"),
						rs.getBoolean("show_lang_selector")
						);
			}
			logger.debug("-");
		} catch ( SQLException ex ) {
			logger.error( "-", ex );
			throw new CriticalException( ex );
		}
		return null;
	}

    
    /**
     * 
     */
    public SiteUrl() {
        super();
    }
    
    public SiteUrl(Long id, String url, Long siteId) {
        super();
        this.id = id;
        this.url = url;
        this.siteId = siteId;
    }
    
    public SiteUrl(Long id, String url, Long siteId, Boolean isMain, Long langId, Boolean isSingleLanguage, Boolean showLangSelector) {
        super();
        this.id = id;
        this.url = url;
        this.siteId = siteId;
        this.main = isMain;
        this.langId = langId;
        this.isSingleLanguage = isSingleLanguage;
        this.showLangSelector = showLangSelector;
    }
    
    public SiteUrl(Builder builder) {
    	this.id = builder.id;
        this.url = builder.url;
        this.siteId = builder.siteId;
        this.main = builder.main;
        this.langId = builder.langId;
        this.isSingleLanguage = builder.isSingleLanguage;
        this.showLangSelector = builder.showLangSelector;
        this.bing = builder.bing;
        this.analytic = builder.analytic;
        this.verification = builder.verification;
		this.tagManager = builder.tagManager;
    }

	public Long save(Connection con) { 
    	logger.debug( "+" );
		try {
			// get the next insert id	
			this.setId( DBHelper.getNextInsertId( 
				con, 
				this.getTableId() + "_id_seq" ) 
			);
			PreparedStatement stmt = null;
			stmt = con.prepareStatement( insertSql );
			stmt = this.saveIntoStatement( stmt );
			stmt.execute();
			// increment order of all siblings
			logger.debug("-");
			return this.getId();
		} catch ( SQLException ex ) {
			logger.error( "-", ex );
			throw new CriticalException( ex );
		}
    }
    
    public void update(Connection con) { 
    	logger.debug( "+" );
		try {			
			PreparedStatement stmt = null;
			stmt = con.prepareStatement( updateSql );
			stmt = this.saveIntoStatement( stmt );
			stmt.setObject(getFieldCount() + 1, this.getId());
			stmt.execute();
			logger.debug("-");
		} catch ( SQLException ex ) {
			logger.error( "-", ex );
			throw new CriticalException( ex );
		}
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#load(java.sql.ResultSet)
     */
    public Long load(ResultSet rs) throws CriticalException{
        logger.debug( "+" );
        try{
            this.id = DBHelper.makeLong( rs.getLong("id") );
            this.siteId = DBHelper.makeLong( rs.getLong("site_id") );
            this.url = rs.getString("url");
            this.main = rs.getBoolean("is_main");
            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#saveIntoStatement(java.sql.PreparedStatement)
     */
    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
        throws SQLException
    {
        logger.debug( "+" );
        try{
            stmt.setLong(1, this.id);
            stmt.setLong(2, this.siteId);
            stmt.setString(3, this.url);
            stmt.setBoolean(4, this.main);
            stmt.setObject(5, this.langId);
            stmt.setBoolean(6, this.isSingleLanguage);
            stmt.setBoolean(7, this.showLangSelector);
        }
        catch (SQLException e)
        {
            logger.error(e.getMessage(), e);
            throw new SQLException("Error while saving into statement");
        }
        logger.debug( "-" );
        return stmt;
    }
    
    
    public static List<SiteUrl> list(Connection con, Long siteId) 
	    throws CriticalException 
	{
		logger.debug( "+" );
		List<SiteUrl> list = new ArrayList<SiteUrl>();
		try {			
			PreparedStatement stmt = null;
			stmt = con.prepareStatement( findBySiteIdSql );
			stmt.setLong(1, siteId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
				list.add(new SiteUrl.Builder().id(rs.getLong("id"))
							.url(rs.getString("url"))
							.siteId(rs.getLong("site_id"))
							.main(rs.getBoolean("is_main"))
							.langId(DBHelper.makeLong(rs.getLong("lang_id")))
							.isSingleLanguage(rs.getBoolean("is_single_lang"))
							.showLangSelector(rs.getBoolean("show_lang_selector"))
							.bing(rs.getString("bing_code"))
							.analytic(rs.getString("google_analytic_code"))
							.verification(rs.getString("google_verification_code"))
							.tagManager(rs.getString("google_tag_manager_code"))
						.build());
			}
			logger.debug("-");
		} catch ( SQLException ex ) {
			logger.error( "-", ex );
			throw new CriticalException( ex );
		}
		return list;
	}

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getTableId()
     */
    public String getTableId() {
        return SiteUrl.tableId;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFindByIdSql()
     */
    public String getFindByIdSql() {
        return SiteUrl.findByIdSql;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getUpdateSql()
     */
    public String getUpdateSql() {
        return SiteUrl.updateSql;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getInsertSql()
     */
    public String getInsertSql() {
        return SiteUrl.insertSql;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFieldCount()
     */
    public int getFieldCount() {
        return SiteUrl.fieldCount;
    }
    
    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }
  
    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    
    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * @return Returns the siteId.
     */
    public Long getSiteId() {
        return siteId;
    }
    
    /**
     * @param siteId The siteId to set.
     */
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

	public Boolean getMain() {
		return main;
	}

	public void setMain(Boolean main) {
		this.main = main;
	}

	public Long getLangId() {
		return langId;
	}

	public void setLangId(Long langId) {
		this.langId = langId;
	}

	public Boolean getSingleLanguage() {
		return isSingleLanguage;
	}

	public void setSingleLanguage(Boolean isSingleLanguage) {
		this.isSingleLanguage = isSingleLanguage;
	}

	public Boolean getShowLangSelector() {
		return showLangSelector;
	}

	public void setShowLangSelector(Boolean showLangSelector) {
		this.showLangSelector = showLangSelector;
	}

	public List<Long> getLangIds() {
		return langIds;
	}

	public void setLangIds(List<Long> langIds) {
		this.langIds = langIds;
	}

	public String getBing() {
		return bing;
	}

	public void setBing(String bing) {
		this.bing = bing;
	}

	public String getAnalytic() {
		return analytic;
	}

	public void setAnalytic(String analytic) {
		this.analytic = analytic;
	}

	public String getVerification() {
		return verification;
	}

	public void setVerification(String verification) {
		this.verification = verification;
	}

	public String getTagManager() {
		return tagManager;
	}

	public void setTagManager(String tagManager) {
		this.tagManager = tagManager;
	}

	public static class Builder {
		private Long id = null;
	    private Long siteId = null;
	    private String url = null;
	    private Boolean main = false;
	    private Long langId = null;
	    private Boolean isSingleLanguage = false;
	    private Boolean showLangSelector = true;
	    private String bing = null;
	    private String analytic = null;
	    private String verification = null;
		private String tagManager = null;
	    
	    public SiteUrl build() {
	    	return new SiteUrl(this); 
	    }
	    
		public Builder siteId(Long siteId) {
			this.siteId = siteId;
			return this;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder main(Boolean main) {
			this.main = main;
			return this;
		}

		public Builder langId(Long langId) {
			this.langId = langId;
			return this;
		}

		public Builder isSingleLanguage(Boolean isSingleLanguage) {
			this.isSingleLanguage = isSingleLanguage;
			return this;
		}

		public Builder showLangSelector(Boolean showLangSelector) {
			this.showLangSelector = showLangSelector;
			return this;
		}

		public Builder bing(String bing) {
			this.bing = bing;
			return this;
		}

		public Builder analytic(String analytic) {
			this.analytic = analytic;
			return this;
		}

		public Builder verification(String verification) {
			this.verification = verification;
			return this;
		}

		public Builder id(Long id) {
			this.id = id;
			return this;
		}

		public Builder tagManager(String tagManager){
	    	this.tagManager = tagManager;
	    	return this;
		}
	}
}
