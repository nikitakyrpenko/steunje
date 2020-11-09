package com.negeso.framework.menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DomainObject;
import com.negeso.framework.domain.ObjectNotFoundException;


/**
 * @author Oleg Lyebyedyev
 * @version 1.0
 */

public class MenuItem extends DomainObject implements Entity {

	private static final long serialVersionUID = -382056497889598992L;

	private static Logger logger = Logger.getLogger(MenuItem.class);
	
    private Long menu_id = null;

    private String title = null;

    private String link = null;

    private String link_type = null;

    private Integer order = null;

    private String clazz = null;
    
    private Timestamp publishDate;
    
    private Timestamp expiredDate;

    /*******************************************************
     * Update menu item sql statement
     ******************************************************/
    private static final String UPDATE_MENU_ITEM_SQL =
        " UPDATE menu_item SET " +
        " id = ?," +        " menu_id = ?," +        " title = ?," +        " link = ?," +        " link_type = ?, " +        " order_ = ?," +        " class = ?" +        " WHERE id = ?";

    /*******************************************************
     * Insert menu item statement
     *******************************************************/
    private static final String INSERT_MENU_ITEM_SQL =
        " INSERT INTO menu_item (" +        " id," +        " menu_id," +        " title," +        " link," +        " link_type," +        " order_," +        " class," +
        " publish_date," +
        " expired_date" +        " ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    
    private static final String FIND_MENUITEMS_BYPARENTID_NOT_NULL_QUERY = 
    	"select menu_item.id, menu_item.menu_id,menu_item.title,menu_item.link,menu_item.link_type, " +
    	"menu_item.order_,menu_item.class from menu_item " +
        "left join menu on menu.parent_menu_item_id = ? " + 
        "and menu.level > 0 and menu.site_id = ? and menu.lang_id=? " + 
        "where menu_item.menu_id = menu.id  ORDER BY menu_item.order_";
    
    private static final String FIND_MENUITEMS_BYPARENTID_NULL_QUERY = 
    	"select menu_item.id, menu_item.menu_id,menu_item.title,menu_item.link,menu_item.link_type, " +
    	"menu_item.order_,menu_item.class from menu_item " +
        "left join menu on menu.parent_menu_item_id is null " + 
        "and menu.level > 0 and menu.site_id = ? and menu.lang_id=? " + 
        "where menu_item.menu_id = menu.id ORDER BY menu_item.order_";
    
    private static final String FIND_UNLINKEDMENUITEMS_QUERY = 
    	"select menu_item.id, menu_item.menu_id,menu_item.title,menu_item.link,menu_item.link_type, " +
    	"menu_item.order_,menu_item.class from menu_item " +
        "left join menu on menu.parent_menu_item_id is null " + 
        "and menu.level = 0 and menu.site_id = ? and menu.lang_id=? " + 
        "where menu_item.menu_id = menu.id and menu_item.class NOTNULL  ORDER BY menu_item.title";
    
    private static final String FIND_MENU_ITEM_BY_ID_AND_LANG_ID =
    	" SELECT menu_item.* FROM menu_item " +
    	" LEFT JOIN menu ON " +
    	" menu.id = menu_item.menu_id" +
    	" WHERE menu_item.id = ? AND menu.lang_id = ?";
    
    public void setMenuId(long menuId) { menu_id = new Long(menuId); }
    public Long getMenuId() { return menu_id; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setLink(String link) { this.link = link; }
    public String getLink() { return link; }
    
    public String getLinkWithoutLangCode() {
    	return link.substring(0, link.lastIndexOf("_"));
    }
    
    public void setLinkType(String type) { link_type = type; }
    public String getLinkType() { return link_type; }

    public void setOrder(int order) { this.order = new Integer(order); }
    public Integer getOrder() { return order; }

    public void setMenuItemClass(String val) { clazz = val; }
    public String getMenuItemClass() { return clazz; }
    
    public MenuItem() { super(null); }

    @Override
    public String getTableId() { return "menu_item"; }

    /** @inheritDoc */
    @Override
    public void load(long id) throws ObjectNotFoundException {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            load(id, conn);
        } catch (SQLException ex) {
            logger.error("-", ex);
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(conn);
        }
    }

    public void load(long id, Connection connection) throws ObjectNotFoundException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM menu_item  WHERE id = " + id);
            if (!rs.next()) {
                logger.info("- menu item not found by id: " + id);
                throw new ObjectNotFoundException("No menu item # " + id);
            }
            mapRow(rs, this);
            logger.debug("-");
        } catch (SQLException ex) {
            logger.error("-", ex);
            throw new CriticalException(ex);
        } finally {
        	DBHelper.close(rs);
        	DBHelper.close(stmt);
        }
    }
    
    /** @inheritDoc */
    @Override
    public Long insert() {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            return insert(conn);
        } catch (SQLException ex) {
	        logger.error("-", ex);
	        throw new CriticalException(ex);
	    } finally {
            DBHelper.close(conn);
        }
    }
    
    public Long insert(Connection connection) {
    	PreparedStatement pstmt = null;
    	try {
	        id = DBHelper.getNextInsertId(connection, "menu_item_id_seq");
	        pstmt = connection.prepareStatement(INSERT_MENU_ITEM_SQL);
	        saveIntoStatement(pstmt);
	        pstmt.setTimestamp(8, this.publishDate);
	        pstmt.setTimestamp(9, this.expiredDate);
	        pstmt.execute();
	        logger.debug("-");
	        return id;
	    } catch (SQLException ex) {
	        logger.error("-", ex);
	        throw new CriticalException(ex);
	    } finally {
	    	DBHelper.close(pstmt);
	    }
    }
    
    /** @inheritDoc */
    @Override
    public void update() {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            PreparedStatement st = conn.prepareStatement(UPDATE_MENU_ITEM_SQL);
            st.setObject(8, id);
            saveIntoStatement(st);
            st.execute();
        } catch (SQLException ex) {
            logger.error("-", ex);
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
    }
    
    /** @inheritDoc */
    @Override
    public void delete() throws CriticalException {
    	logger.debug( "+" );
        DBHelper.deleteObject( this.getTableId(), this.getId(), logger );
        logger.debug( "-" );
    }

    private void saveIntoStatement(PreparedStatement st) throws SQLException {
        logger.debug("+");
        st.setObject(1, id);
        st.setLong(2, menu_id.longValue());
        st.setString(3, title);
        st.setString(4, link);
        st.setString(5, link_type);
        st.setObject(6, order);
        st.setString(7, clazz);
        logger.debug("-");
    }
    
    /**
     * Finds menu item by property "link".
     * 
     * @param langId    standard language code
     * @return          menu object; null if no item found
     */
    public static MenuItem findByLink(String link) {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            return findByLink(link, conn);
        } catch (SQLException ex) {
	        logger.error("SQLException", ex);
	        throw new CriticalException(ex);
	    } finally {
            DBHelper.close(conn);
        }
    }
    
    /**
     * Finds menu items by property "link".
     * 
     * @param link    link to page
     * @return          list menu objects; 
     */
    public static List<MenuItem> findItemsByLink(String link) {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            return findItemsByLink(link, conn);
        } catch (SQLException ex) {
	        logger.error("SQLException", ex);
	        throw new CriticalException(ex);
	    } finally {
            DBHelper.close(conn);
        }
    }
    
    
    
    
    public static MenuItem findByLink(String link, Connection connection) {
    	Statement stmt = null;
    	ResultSet rst = null;
    	try {
	        Long siteId = Env.getSiteId();
	        stmt = connection.createStatement();
	        rst = stmt.executeQuery(" SELECT menu_item.* FROM menu_item, menu " +
		            " WHERE menu_item.link like '"+ link + "%'" + 
		            " AND menu_item.menu_id = menu.id AND menu.site_id =" + siteId);
	        if(!rst.next()) {
	            logger.warn("- not found by link: " + link);
	            return null;
	        }
	        logger.debug("-");
	        return mapRow(rst, new MenuItem());
	    } catch (SQLException ex) {
	        logger.error("SQLException", ex);
	        throw new CriticalException(ex);
	    } finally {
	    	DBHelper.close(rst);
	    	DBHelper.close(stmt);
	    }
    }
    
    public static List<MenuItem> findItemsByLink(String link, Connection connection) {
    	Statement stmt = null;
    	ResultSet rst = null;
    	List<MenuItem> items =  new ArrayList<MenuItem>();
    	try {
	        Long siteId = Env.getSiteId();
	        stmt = connection.createStatement();
	        rst = stmt.executeQuery(" SELECT menu_item.* FROM menu_item, menu " +
		            " WHERE menu_item.link like '"+ link + "%'" + 
		            " AND menu_item.menu_id = menu.id AND menu.site_id =" + siteId);
	        while(rst.next()) {
	        	items.add(mapRow(rst, new MenuItem()));
	        }
	        if (items.size() == 0)
	        	logger.warn("- not found by link: " + link);
	        
	        logger.debug("-");
	        return items;
	    } catch (SQLException ex) {
	        logger.error("SQLException", ex);
	        throw new CriticalException(ex);
	    } finally {
	    	DBHelper.close(rst);
	    	DBHelper.close(stmt);
	    }
    }
    
    
    
    
    public static List<MenuItem> findMenuItemsByParentId(Connection conn, Long menuId, Long langId){
    	List<MenuItem> list = new ArrayList<MenuItem>();
    	PreparedStatement stmt = null;
    	ResultSet rst = null;
    	try {
	        if (menuId == null){
	        	stmt = conn.prepareStatement(FIND_MENUITEMS_BYPARENTID_NULL_QUERY);
	        	stmt.setLong(1, Env.getSiteId());
	        	stmt.setLong(2, langId);
	        }else{
	        	stmt = conn.prepareStatement(FIND_MENUITEMS_BYPARENTID_NOT_NULL_QUERY);
	        	stmt.setLong(1, menuId);
	        	stmt.setLong(2, Env.getSiteId());
	        	stmt.setLong(3, langId);
	        }
	        rst = stmt.executeQuery();
	        if (rst == null){
	        	logger.error("menu has no submenu items");
	        	return list;
	        }
	        
	        while(rst.next()){
	        	list.add(mapRow(rst, new MenuItem()));
	        }
	        
	        logger.debug("-");
	        return list;
	    } catch (SQLException ex) {
	        logger.error("SQLException", ex);
	        throw new CriticalException(ex);
	    } finally {
	    	DBHelper.close(rst);
	    	DBHelper.close(stmt);
	    }
    }
    
    public static List<MenuItem> findUnlinkedPages(Connection conn, Long langId){
    	List<MenuItem> list = new ArrayList<MenuItem>();
    	PreparedStatement stmt = null;
    	ResultSet rst = null;
    	try {
        	stmt = conn.prepareStatement(FIND_UNLINKEDMENUITEMS_QUERY);
        	stmt.setLong(1, Env.getSiteId());
        	stmt.setLong(2, langId);
	        rst = stmt.executeQuery();
	        if (rst == null){
	        	logger.error("menu has no submenu items");
	        	return list;
	        }
	        
	        while(rst.next()){
	        	list.add(mapRow(rst, new MenuItem()));
	        }
	        
	        logger.debug("-");
	        return list;
	    } catch (SQLException ex) {
	        logger.error("SQLException", ex);
	        throw new CriticalException(ex);
	    } finally {
	    	DBHelper.close(rst);
	    	DBHelper.close(stmt);
	    }
    }
    
    private static MenuItem mapRow(ResultSet rs, MenuItem item) {
        logger.debug("+");
        try {
            item.id = makeLong(rs.getLong("id"));
            item.menu_id = makeLong(rs.getLong("menu_id"));
            item.title = rs.getString("title");
            item.link = rs.getString("link");
            item.link_type = rs.getString("link_type");
            item.order = makeInteger(rs.getInt("order_"));
            item.clazz = rs.getString("class");
        } catch (SQLException ex) {
            logger.debug("SQLException", ex);
            throw new CriticalException(ex.getMessage());
        }
        logger.debug("-");
        return item;
    }
    
    public static MenuItem findMenuItemByIdAndLangId(long id, long langId) {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            stmt = conn.prepareStatement(FIND_MENU_ITEM_BY_ID_AND_LANG_ID);
            stmt.setLong(1, id);
            stmt.setLong(2, langId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs, new MenuItem());
            }
            logger.debug("-");
            return null;
        } catch (SQLException ex) {
            logger.error("-", ex);
            throw new CriticalException(ex);
        } finally {
        	DBHelper.close(rs);
        	DBHelper.close(stmt);
            DBHelper.close(conn);
        }
    }
    
	/**
	 * @return
	 * @see java.lang.String#toString()
	 */
	public String toString() {
		return "id=" + id + "|title=" + getTitle();
	}
	public Timestamp getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Timestamp publishDate) {
		this.publishDate = publishDate;
	}
	public Timestamp getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Timestamp expiredDate) {
		this.expiredDate = expiredDate;
	}

}