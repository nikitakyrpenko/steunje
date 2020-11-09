/*
 * @(#)Page.java       @version 12.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DomainObject;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.module.user.domain.DefaultContainer;


/**
 * Page entity encapsulation. ActiveRecord pattern.
 *
 * @version     $Revision$ $Date: 06.04.2007 14:55:54$
 * @author      Stanislav Demchenko
 * @author      Olexiy Strashko
 */
@Deprecated
public class Page extends DomainObject {
    
    private static Logger logger = Logger.getLogger( Page.class );
    
    protected static String tableId = "page";
    
    protected static String sequenceId = "page_id_seq";
    
    private final static String SQL_FIND_BY_ID = 
        " SELECT * FROM page WHERE id = ? ";    
    
    private final static String SQL_FIND_BY_NAME = 
        " SELECT * FROM page WHERE site_id = ? AND lower(filename) = ? ";
    
    private final static String SQL_FIND_BY_LANGUAGE = 
        " SELECT * FROM page WHERE lang_id = ? AND site_id = ? ";
    
    private final static String SQL_FIND_ALL = 
        " SELECT * FROM page WHERE  site_id = ? order by filename";
    
    private final static String SQL_FIND_BY_CATEGORY =
        "SELECT * FROM page WHERE category = ? AND lang_id = ? AND site_id = ?";

    private final static String SQL_FIND_BY_CLASS =
        " SELECT page.* FROM page, language " +
        " WHERE page.class = ? AND language.code = ? " +
        " AND language.id = page.lang_id";
    
    private final static String SQL_FIND_BY_CLASS_AND_COMPONENT_NAME =
        " SELECT page.* FROM page " +
        " LEFT JOIN page_component ON page_component.page_id = page.id " +
        " WHERE page.class = ? AND page.lang_id = ? AND page_component.class_name = ? ";
    
    private final static String SQL_UPDATE =
        " UPDATE page SET" +
        " id = ?," +
        " lang_id = ?," +
        " filename = ?," +
        " title = ?," +
        " contents = ?," +
        " class = ?," +
        " category = ?," +
        " last_modified = ?," +
        " protected = ?," +
        " publish_date = ?," +
        " expired_date = ?," +
        " visible = ?," +
        " container_id = ?," +
        " attribute_set_id = ?, " +
        " site_id = ?," +
        " meta_description = ?," +
        " meta_keywords = ?," +
        " property_value = ?," +
        " google_script = ?," +
        " is_search = ?," +
        " edit_user = ?," +
        " edit_date = now()," +
        " meta_title = ? " +
        " WHERE id = ?";
    
    private final static String SQL_UPDATE_LAST_EDIT = " UPDATE page SET" +
    " edit_user = ?," +
    " edit_date = now() " +
    " WHERE id = ?";
    
    private final static String SQL_UPDATE_LAST_EDIT_BY_ARTICLE = " UPDATE page SET" +
    " edit_user = ?," +
    " edit_date = now() " +
    " WHERE id in (SELECT page_id FROM page_component as pc,page_component_params as pcp " +
    "					WHERE pc.id = pcp.element_id AND pc.class_name = 'article-component' AND pcp.value =  trim(to_char(?,'99999')))";
    
    private final static String SQL_UPDATE_LAST_EDIT_PHOTO = " UPDATE page SET" +
    " edit_user = ?," +
    " edit_date = now() " +
    " WHERE id in (SELECT page_id FROM page_component as pc WHERE  pc.class_name = 'photo-album-component')";
    
    
    
    
    private final static String SQL_INSERT =
        " INSERT INTO page(" +
        " id," +
        " lang_id," +
        " filename," +
        " title," +
        " contents," +
        " class," +
        " category," +
        " last_modified," +
        " protected," +
        " publish_date," +
        " expired_date," +
        " visible," +
        " container_id," +
        " attribute_set_id," +
        " site_id, " +
        " meta_description," +
        " meta_keywords," +
        " property_value," +
        " google_script," +
        " is_search," +
        " edit_user," +
        " edit_date," +
        " meta_title " +
        " ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,now(),?)";
    
    private static final String IS_SPECIAL_QUERY = 
    	" SELECT mi.id FROM menu_item as mi where menu_id " +
    	" IN ( SELECT menu.id FROM menu WHERE menu.level = 0 AND menu.lang_id = ? AND menu.site_id=? ) " +
    	" AND link = ?";  
    
    private final static int FIELD_COUNT = 22;
    
    
    /** FK to language */
    private Long langId = new Long( 0 );

    /** Filename of the Page, stored encoded */
    private String filename = null;

    /** Title of the Page, stored encoded */
    private String title = null;
    
    /** Title for browsers */
    private String metaTitle = null;
    
    /** meta description of the Page, stored encoded */
    private String meta_description = null;
    
    /** meta keywords of the Page, stored encoded */
    private String meta_keywords = null;
    
    /** value of the additional property of the Page, stored encoded */
    private String property_value = null;
    
    /** google_script of the Page, stored encoded */
    private String google_script = null;

    /** Contents of the Page, stored encoded */
    private String contents = null;

    /** Class_ of the Page, stored encoded */
    private String class_ = "";

    /** Category of the Page, stored as varchar */
    private String category = null;

    /** Last modified attribute */
    private Timestamp last_modified = new Timestamp(System.currentTimeMillis());

    /** Protected */
    private String protected_ = null;

    /** Publication date */
    private Timestamp publish_date = null;

    /** Expiration date */
    private Timestamp expired_date = null;
    
    /** Last edit date */
    private Timestamp edit_date = null;
    
    /** Last edit user */
    private String edit_user = null;

    /** Force page visibility in menu */
    private boolean visible = false;

    /** Container which the page belongs to */
    private Long containerId = DefaultContainer.getInstance().getId();

    /** Attribute set id */
    private Long attributeSetId = null;
    
    /** is page included in search **/
    private boolean is_search = true;

    /** Default construction */
    public Page()
    {
        super( null );
        logger.debug("+ -");
    }

    /** Protected construction (for DB-loading proposal) */
    protected Page(
            Long id,
            Long langId,
            String filename,
            String title,
            String contents,
            String class_,
            String category,
            Timestamp last_modified,
            String protected_,
            Timestamp publish_date,
            Timestamp expired_date,
            boolean visible,
            Long containerId,
            Long attributeSetId,
            String meta_description,
            String meta_keywords,
    		String property_value,
    		String google_script,
    		boolean is_search)
    {
        super( id );
        logger.debug("+");
        this.langId = langId;
        this.filename = filename;
        this.title = title;
        this.contents = contents;

        this.class_ = class_;
        this.category = category;
        this.last_modified = last_modified;
        this.protected_ = protected_;
        this.publish_date = publish_date;
        this.expired_date = expired_date;
        this.visible = visible;
        this.containerId = containerId;
        this.attributeSetId = attributeSetId;

        this.meta_description = meta_description;
        this.meta_keywords = meta_keywords;
        this.property_value = property_value;
        this.google_script = google_script;
        this.is_search = is_search;
        logger.debug("-");
    }

    protected Page(
            Long id,
            Long langId,
            String filename,
            String title,
            String metaTitle,
            String contents,
            String class_,
            String category,
            Timestamp last_modified,
            String protected_,
            Timestamp publish_date,
            Timestamp expired_date,
            Timestamp edit_date,
            String edit_user,
            boolean visible,
            Long containerId,
            Long attributeSetId,
            String meta_description,
            String meta_keywords,
    		String property_value,
    		String google_script,
    		boolean is_search)
    {
        super( id );
        logger.debug("+");
        this.langId = langId;
        this.filename = filename;
        this.title = title;
        this.metaTitle = metaTitle;
        this.contents = contents;

        this.class_ = class_;
        this.category = category;
        this.last_modified = last_modified;
        this.protected_ = protected_;
        this.publish_date = publish_date;
        this.expired_date = expired_date;
        this.edit_date = edit_date;
        this.edit_user = edit_user;
        this.visible = visible;
        this.containerId = containerId;
        this.attributeSetId = attributeSetId;

        this.meta_description = meta_description;
        this.meta_keywords = meta_keywords;
        this.property_value = property_value;
        this.google_script = google_script;
        this.is_search = is_search;
        logger.debug("-");
    }

    
    
    /**
     * @return tableId - table name in DB
     */
    public String getTableId()
    {
        logger.debug("+ -");
        return Page.tableId;
    }

    public boolean isDateOk() {
        logger.debug( "+" );
        long curTime = System.currentTimeMillis();
        Timestamp publish = getPublishDate();
        Timestamp expire = getExpiredDate();
        boolean valid = true;
        if ( publish != null && curTime < publish.getTime() ) {
            valid = false;
        }
        if ( expire != null && curTime > expire.getTime() ) {
            valid = false;
        }
        logger.debug( "-" );
        return valid;
    }

    /*************************************************************************
     *                  ActiveRecord pattern members
     *************************************************************************
     */

    /**
     * Finds Page by id. If object is not found - throws ObjectNotFoundException
     *
     * @param id
     * @return Page if object is found, else - throws ObjectNotFoundException
     * @throws SQLException
     */
    public static Page findById( Long id ) throws ObjectNotFoundException {
        logger.debug( "+" );
        Page result = null;
        PreparedStatement findStatement = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            findStatement = conn.prepareStatement( SQL_FIND_BY_ID );
            findStatement.setObject( 1, id, Types.BIGINT );
            rs = findStatement.executeQuery();
            if ( rs.next() == false ) {
                logger.error("- throwing ObjectNotFoundException");
                throw new ObjectNotFoundException( "Object: " + Page.tableId +
                    " not found by id: " + id );
            }
            result = load( rs );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.error( ex, ex );
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( conn );
        }
    }

    /**
     * Finds Page by id. If object is not found - throws ObjectNotFoundException
     *
     * @param id
     * @return Page if object is found, else - throws ObjectNotFoundException
     * @throws SQLException
     */
    public static Page findById( Connection conn, Long id ) throws ObjectNotFoundException {
        logger.debug( "+" );
        Page result = null;
        PreparedStatement findStatement = null;
        ResultSet rs = null;
        try {
            findStatement = conn.prepareStatement( SQL_FIND_BY_ID );
            findStatement.setObject( 1, id, Types.BIGINT );
            rs = findStatement.executeQuery();
            if ( rs.next() == false ) {
                logger.error("- throwing ObjectNotFoundException");
                throw new ObjectNotFoundException( "Object: " + Page.tableId +
                    " not found by id: " + id );
            }
            result = load( rs );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.error( ex, ex );
            throw new CriticalException( ex );
        } finally {
        	DBHelper.close(findStatement);
        }
    }

    /**
     * Factory method to find and create Page by filename.
     *
     * @param fileName
     * @return Page object, never null
     * @throws CriticalException in case of database error
     * @throws ObjectNotFoundException if object is not found
     */
    public static Page findByFileName( String fileName )
    throws ObjectNotFoundException {
        logger.debug( "+" );
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            return findByFileName( fileName, conn);
        }  catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        }  finally {
            DBHelper.close( conn );
        }
    }

    public static Page findByFileName( String fileName, Connection connection) throws ObjectNotFoundException {
    	PreparedStatement findStatement = null;
    	ResultSet rs = null;
    	try {
    		findStatement = connection.prepareStatement( 
	                SQL_FIND_BY_NAME 
	            );
            findStatement.setLong( 1, Env.getSiteId() );
            findStatement.setString( 2, fileName.toLowerCase() );
            rs = findStatement.executeQuery();
            if ( rs.next() == false ) {
                logger.warn("- throwing page not found: " + fileName);
                throw new ObjectNotFoundException( "Object: " + Page.tableId +
                    " not found by fileName: " + fileName );
            }
            Page result = load( rs );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } finally {
        	DBHelper.close(rs);
        	DBHelper.close(findStatement);        	
        }
    }
    
    public static Page findByFileName( String fileName, String langCode ) throws ObjectNotFoundException {
    	PreparedStatement findStatement = null;
    	ResultSet rs = null;
    	Connection conn = null;
        try {
            conn = DBHelper.getConnection();
    		findStatement = conn.prepareStatement( 
	                SQL_FIND_BY_NAME + " OR lower(filename) = ?"
	            );
            findStatement.setLong( 1, Env.getSiteId() );
            findStatement.setString( 2, fileName.toLowerCase() );
            findStatement.setString(3, fileName.replace(".", "_" + langCode + ".").toLowerCase());
            rs = findStatement.executeQuery();
            if ( rs.next() == false ) {
                logger.warn("- throwing page not found: " + fileName);
                throw new ObjectNotFoundException( "Object: " + Page.tableId +
                    " not found by fileName: " + fileName );
            }
            Page result = load( rs );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } finally {
        	DBHelper.close(rs, findStatement, conn);       	
        }
    }
    
    
    public static List<Page> findByLanguage(String langCode ) {
        logger.debug( "+" );
        Connection conn = null;
        List<Page> pages = new ArrayList<Page>();
        try {
            conn = DBHelper.getConnection();
          
            PreparedStatement findStatement = conn.prepareStatement( 
                SQL_FIND_BY_LANGUAGE
            );
            
            Language lang = Language.findByCode(langCode);
            if ( lang == null ){
                logger.error("-language not foung by code:" + langCode);
            } 
            
            findStatement.setLong( 1, lang.getId() );
            findStatement.setLong( 2, Env.getSiteId());
            
            ResultSet rs = findStatement.executeQuery();
           
            while ( rs.next()  ) {
               pages.add(load(rs));
            }
            logger.debug( "-" );
           
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } catch (CriticalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            DBHelper.close( conn );
        }
		 return pages;
    }
    
    
    public static List<Page> findAll() {
        logger.debug( "+" );
        Connection conn = null;
        List<Page> pages = new ArrayList<Page>();
        try {
            conn = DBHelper.getConnection();
          
            PreparedStatement findStatement = conn.prepareStatement( 
                SQL_FIND_ALL
            );
            
            findStatement.setLong( 1, Env.getSiteId());
            ResultSet rs = findStatement.executeQuery();
           
            while ( rs.next() ) {
               pages.add(load(rs));
            }
            logger.debug( "-" );
           
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } catch (CriticalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} finally {
            DBHelper.close( conn );
        }
		 return pages;
    }
    
    
    /**
     * Factory method to find and create Page by category and language code.
     *
     * @param category
     * @param langCode
     * @return Page object, never null
     * @throws CriticalException in case of database error
     * @throws ObjectNotFoundException if object is not found
     */
    public static Page findByCategory( String category, String langCode )
    throws ObjectNotFoundException {
        logger.debug( "+" );
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
          
            PreparedStatement findStatement = conn.prepareStatement( 
                SQL_FIND_BY_CATEGORY 
            );
            
            Language lang = Language.findByCode(langCode);
            if ( lang == null ){
                logger.error("-language not foung by code:" + langCode);
            } 
            findStatement.setString( 1, category );
            findStatement.setLong( 2, lang.getId() );
            findStatement.setLong( 3, Env.getSiteId() );
            
            ResultSet rs = findStatement.executeQuery();
            if ( rs.next() == false ) {
                logger.error("- throwing 'ObjectNotFoundException'");
                throw new ObjectNotFoundException( "Object: " + Page.tableId
                    + " not found by category: " + category );
            }
            Page result = load( rs );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( conn );
        }
    }

    /**
     * Factory method to find and create Page by class and language code.
     *
     * @param klass
     * @param langCode
     * @return Page object, never null
     * @throws CriticalException in case of database error
     * @throws ObjectNotFoundException if object is not found
     */
    public static Page findByClass( String klass, String langCode )
        throws CriticalException, ObjectNotFoundException
    {
        logger.debug( "+" );
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            return findByClass(klass, langCode, conn);
	    } catch ( SQLException ex ) {
	        logger.error( "- SQLException", ex );
	        throw new CriticalException( ex );
	    } finally {
            DBHelper.close( conn );
        }
    }
    
    public static Page findByClass( String klass, String langCode, Connection connection ) throws ObjectNotFoundException {
    	PreparedStatement findStatement = null;
    	ResultSet rs = null;
    	try {
    		findStatement = connection.prepareStatement( SQL_FIND_BY_CLASS );
	        findStatement.setString( 1, klass );
	        findStatement.setString( 2, langCode );
	        rs = findStatement.executeQuery();
	        if ( rs.next() == false ) {
	            logger.error("- throwing 'ObjectNotFoundException'");
	            throw new ObjectNotFoundException(
	                "Page not found by class: " + klass );
	        }
	        Page result = load( rs );
	        logger.debug( "-" );
	        return result;
	    } catch ( SQLException ex ) {
	        logger.error( "- SQLException", ex );
	        throw new CriticalException( ex );
	    } finally {
	    	DBHelper.close(findStatement);
	    	DBHelper.close(rs);
	    }
    }
    
    public static Page findByClassAndObligatoryComponent( String klass, Long langId, String componentName ) throws ObjectNotFoundException {
    	Connection con = null;
    	PreparedStatement stmt = null;
    	ResultSet rs = null;
    	Page page = null;
    	try {
			con = DBHelper.getConnection();
			stmt = con.prepareStatement(SQL_FIND_BY_CLASS_AND_COMPONENT_NAME);
			stmt.setString(1, klass);
			stmt.setLong(2, langId);
			stmt.setString(3, componentName);
			rs = stmt.executeQuery();
			if ( rs.next() == false ) {
	            logger.error("- throwing 'ObjectNotFoundException'");
	            throw new ObjectNotFoundException(
	                "Page not found by class: " + klass + " AND component name: " + componentName + " langId; " + langId);
	        }
			page = load( rs );
		} catch (SQLException ex) {
			logger.error( "- SQLException", ex );
	        throw new CriticalException( ex );
		} finally {
			DBHelper.close(rs, stmt, con);
		}
		return page;
    }
    
    public long isSpecial(Long langId, String filename) {
        logger.debug("+");
        Connection conn = null;
        long page2menuId = 0;
        try {
            conn = DBHelper.getConnection();
            PreparedStatement stmt = conn.prepareStatement(IS_SPECIAL_QUERY);
            stmt.setLong(1, langId);
            stmt.setLong(2, Env.getSiteId());
            stmt.setString(3, filename);
            ResultSet rst = stmt.executeQuery();
            while(rst.next()) {
            	page2menuId = rst.getLong("id");
            }
            logger.debug("-");
            return page2menuId;
        } catch (SQLException ex) {
            logger.error("SQLException", ex);
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(conn);
        }
    }

    
    
    /**
     * Loads Page by RecordSet. All blob fields from table are
     * passed throught Env.fixEncoding() method.
     *
     * @see Env
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Page load( ResultSet rs ) {
        logger.debug( "+" );
        try {
            Page result = new Page(
                new Long( rs.getLong( "id" ) ),
                rs.getLong( "lang_id" ) != 0 ? 
                    new Long( rs.getLong( "lang_id" )) : null,
                rs.getString( "filename" ),
                rs.getString( "title" ),
                rs.getString( "meta_title" ),
                rs.getString( "contents" ),
                rs.getString( "class" ),
                rs.getString( "category" ),
                rs.getTimestamp( "last_modified" ),
                rs.getString( "protected" ),
                rs.getTimestamp( "publish_date" ),
                rs.getTimestamp( "expired_date" ),
                rs.getTimestamp( "edit_date" ),
                rs.getString( "edit_user" ),
                rs.getBoolean( "visible" ),
                makeLong(rs.getLong( "container_id" )),
                makeLong(rs.getLong( "attribute_set_id" )),
        		rs.getString( "meta_description" ),
        		rs.getString( "meta_keywords" ),
        		rs.getString( "property_value" ),
        		rs.getString( "google_script" ),
        		rs.getBoolean( "is_search" )
            );
            logger.debug("-");
            return result;
        } catch ( SQLException ex ) {
            logger.error("- SQLException", ex);
            throw new CriticalException( ex );
        }
    }

    /**
     * Save into PreparedStatement
     *
     * @throws SQLException
     */
    public PreparedStatement saveIntoStatement( PreparedStatement stmt )
    throws SQLException {
        logger.debug( "+" );
        stmt.setObject( 1, id );
        stmt.setObject( 2, langId, Types.BIGINT );
        stmt.setString( 3, filename );
        stmt.setString( 4, title );
        stmt.setString( 5, contents );
        stmt.setString( 6, class_ );
        stmt.setString( 7, category );
        stmt.setTimestamp( 8, last_modified);
        stmt.setString( 9, protected_ );
        stmt.setTimestamp( 10, publish_date );
        stmt.setTimestamp( 11, expired_date );
        stmt.setBoolean( 12, visible );
        stmt.setObject( 13, containerId );
        stmt.setObject( 14, attributeSetId );
        stmt.setObject( 15, Env.getSiteId() );
        stmt.setString( 16, meta_description );
        stmt.setString( 17, meta_keywords );
        stmt.setString( 18, property_value );
        stmt.setString( 19, google_script );
        stmt.setBoolean( 20, is_search );
        stmt.setString( 21, edit_user );
        stmt.setString( 22, metaTitle );
        logger.debug("-");
        return stmt;
    }

    public void load( long id ) throws ObjectNotFoundException {
        logger.debug("+ -");
        throw new UnknownError( "Unsupported" );
    }

    /**
     * Updates Page into DB.
     *
     * @throws SQLException
     */
    public void update() {
        logger.debug("+");
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            pstmt = conn.prepareStatement( SQL_UPDATE );
            pstmt = this.saveIntoStatement( pstmt );
            pstmt.setObject(FIELD_COUNT + 1, this.getId());
            pstmt.execute();
            logger.debug("-");
            return;
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( conn );
        }
    }

    
    public  static void updateLastEdit(Long pageId, String editUser) {
        logger.debug("+");
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            pstmt = conn.prepareStatement( SQL_UPDATE_LAST_EDIT );
            pstmt.setObject(1, editUser);
            pstmt.setObject(2, pageId);
            pstmt.execute();
            logger.debug("-");
            return;
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( conn );
        }
    }
    
    public  static void updateLastEditByArticle(Long articleId, String editUser) {
        logger.debug("+");
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            pstmt = conn.prepareStatement( SQL_UPDATE_LAST_EDIT_BY_ARTICLE );
            pstmt.setObject(1, editUser);
            pstmt.setObject(2, articleId);
            pstmt.execute();
            logger.debug("-");
            return;
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( conn );
        }
    }
    
    
    public  static void updateLastEditPhotoPages( String editUser) {
        logger.debug("+");
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            pstmt = conn.prepareStatement( SQL_UPDATE_LAST_EDIT_PHOTO );
            pstmt.setObject(1, editUser);
           
            pstmt.execute();
            logger.debug("-");
            return;
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( conn );
        }
    }
    
    /**
     * Inserts Page into db, if insertion sucseeded Page id
     * is updated from aut-generated id by DB.
     *
     * @throws SQLException
     * @return new Page id
     */
    public Long insert() {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            return insert(conn);
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( conn );
        }
    }

    public Long insert(Connection connection) {
        PreparedStatement insertStatement = null;    	
    	try {
	        this.setId( DBHelper.getNextInsertId( connection, sequenceId ) );
	        insertStatement = connection.prepareStatement( SQL_INSERT );
	        insertStatement = this.saveIntoStatement( insertStatement );
	        insertStatement.execute();
	        logger.debug("-");
	        return this.getId();
	    } catch ( SQLException ex ) {
	        logger.error( "- SQLException", ex );
	        throw new CriticalException( ex );
	    }
    }
    
    /**
     * Inserts Page into db, if insertion sucseeded Page id
     * is updated from aut-generated id by DB.
     *
     * @throws SQLException
     * @return new Page id
     */
    /*public Long insert(Connection conn) {
        logger.debug("+");
        PreparedStatement insertStatement = null;
        try {
            this.setId( DBHelper.getNextInsertId( conn, sequenceId ) );
            insertStatement = conn.prepareStatement( SQL_INSERT );
            insertStatement = this.saveIntoStatement( insertStatement );
            insertStatement.execute();
            logger.debug("-");
            return this.getId();
        } catch ( SQLException ex ) {
            logger.error( "- SQLException", ex );
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( insertStatement );
        }
    }*/

    /**
     * Deletes Page from DB
     *
     * @throws SQLException
     * @return new Page id
     */
    public void delete() {
        Page.logger.debug( "+" );
        DBHelper.deleteObject( this.getTableId(), this.getId(), Page.logger );
        Page.logger.debug( "-" );
    }

    public String toString() {
        logger.debug("+ -");
        return new ToStringBuilder(this).toString();
    }

    /*************************************************************
     *                  Setters'n'Getters
     */

    public String getCategory()
    {
        return category;
    }

    public String getClass_()
    {
        return class_;
    }

    public String getContents()
    {
        return contents;
    }
    
    public String getFilename()
    {
        return filename;
    }
    
    public Long getLangId()
    {
        return langId;
    }
    
    public Timestamp getLastModified()
    {
        return last_modified;
    }
    
    public String getProtected_()
    {
        return protected_;
    }

    public String getTitle()
    {
        return title;
    }

    public boolean getVisible()
    {
        return visible;
    }

    public Timestamp getExpiredDate()
    {
        return expired_date;
    }

    public Timestamp getPublishDate()
    {
        return publish_date;
    }


    public Long getContainerId() {
        return containerId;
    }

    public void setCategory( String string )
    {
        category = string;
    }

    public void setClass_( String string )
    {
        class_ = string;
    }

    public void setContents( String string )
    {
        contents = string;
    }

    public void setFilename( String string )
    {
        filename = string;
    }

    public void setLangId( Long long1 )
    {
        langId = long1;
    }
    
    public void setLastModified( Timestamp last_modified )
    {
        this.last_modified = last_modified;
    }

    public void setProtected_( String string )
    {
        protected_ = string;
    }

    public void setTitle( String string )
    {
        title = string;
    }
    
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public void setExpiredDate( Timestamp expired_date )
    {
        this.expired_date = expired_date;
    }

    public void setPublishDate( Timestamp publish_date )
    {
        this.publish_date = publish_date;
    }

    public void setContainerId(Long containerId) {
    	//Validate.notNull(containerId, "Null container id");
    	if (containerId != null) {
    		this.containerId = containerId;
    	} else {
    		this.containerId = DefaultContainer.getInstance().getId();
    	}
    }

    /** @return Returns the attributeSetId. */
    public Long getAttributeSetId() {
        return this.attributeSetId;
    }
    /** @param attributeSetId The attributeSetId to set. */
    public void setAttributeSetId(Long attributeSetId) {
        this.attributeSetId = attributeSetId;
    }

	public String getMetaDescription() {
		return meta_description;
	}

	public void setMetaDescription(String meta_description) {
		this.meta_description = meta_description;
	}

	public String getMetaKeywords() {
		return meta_keywords;
	}

	public void setMetaKeywords(String meta_keywords) {
		this.meta_keywords = meta_keywords;
	}

	public String getPropertyValue() {
		return property_value;
	}

	public void setPropertyValue(String property_value) {
		this.property_value = property_value;
	}
	
	public void setSearch(boolean isSearch) {
		this.is_search = isSearch;
	}

	public boolean isSearch() {
		return this.is_search;
	}

	public String getGoogleScript() {
		return google_script;
	}

	public void setGoogleScript(String google_script) {
		this.google_script = google_script;
	}

	public Timestamp getEdit_date() {
		return edit_date;
	}

	public void setEdit_date(Timestamp editDate) {
		edit_date = editDate;
	}

	public String getEdit_user() {
		return edit_user;
	}

	public void setEdit_user(String editUser) {
		edit_user = editUser;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}
	
}
