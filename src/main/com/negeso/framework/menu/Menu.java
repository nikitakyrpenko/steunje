package com.negeso.framework.menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DomainObject;
import com.negeso.framework.domain.ObjectNotFoundException;

public class Menu extends DomainObject {

    private static final String UPDATE_MENU_SQL =
        " UPDATE menu SET " +
        " id=?, lang_id=?, parent_menu_item_id=?, level=?, site_id=? " +
        " WHERE id = ? ";

    private static final String INSERT_MENU_SQL =
        " INSERT INTO menu " +
        " (id, lang_id, parent_menu_item_id, level, site_id) " +
        " VALUES (?,?,?,?,?) ";

    protected Long id;

    private Long langId;

    private Long parentMenuItemId;

    private Integer level;

    private Integer siteId;
    
    private static Logger logger = Logger.getLogger(Menu.class);
    
    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setLangId(long langId) { this.langId = new Long(langId); }
    public Long getLangId() { return this.langId; }

    public void setLevel(int level) { this.level = new Integer(level); }
    public Integer getLevel() { return this.level; }
    
    public Integer getSiteId() { return siteId; }
    public void setSiteId(Integer siteId) { this.siteId = siteId; }

    public void setParentMenuItem(Long id) { this.parentMenuItemId = id; }
    public Long getParentMenuItem() { return parentMenuItemId; }

    public Menu() { super(null); }

    public String getTableId() { return "menu"; }
    
    /** @inheritDoc */
    @Override
    public void delete() { throw new UnsupportedOperationException(); }

    /** @inheritDoc */
    @Override
    public Long insert() {
        logger.debug("+");
        Connection conn = null;
       try {
            conn = DBHelper.getConnection();
            return insert(conn);
       }  catch (SQLException ex) {
	        logger.error("-", ex);
	        throw new CriticalException(ex);
	   } finally {
            DBHelper.close(conn);
        }
    }
    
    public Long insert(Connection connection) {
        PreparedStatement st = null;
        try {
	        id = DBHelper.getNextInsertId(connection, "menu_id_seq");
	        st = connection.prepareStatement(INSERT_MENU_SQL);
	        saveIntoStatement(st);
	        st.execute();
	        logger.debug("-");
	        return id;
	    } catch (SQLException ex) {
	        logger.error("-", ex);
	        throw new CriticalException(ex);
	    } finally {
	    	DBHelper.close(st);
	    }
    }
    
    /** @inheritDoc */
    @Override
    public void update() {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBHelper.getConnection();
            st = conn.prepareStatement(UPDATE_MENU_SQL);
            st.setObject(6, id);
            saveIntoStatement(st);
            st.execute();
        } catch (SQLException ex) {
            logger.debug("- SQLException", ex);
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(st);
            DBHelper.close(conn);
        }
        logger.debug("-");
    }
    
    private void saveIntoStatement(PreparedStatement st) throws SQLException {
        logger.debug("+");
        st.setObject(1, id);
        st.setObject(2, langId);
        st.setObject(3, parentMenuItemId);
        st.setObject(4, level);
        st.setObject(5, siteId);
        logger.debug("-");
    }

    public int countItems() {
        logger.debug("+");
        Connection conn = null;
        try {
            logger.debug("+ -");
            conn = DBHelper.getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) as cnt FROM menu_item WHERE menu_id = " + id);
            rs.next();
            logger.debug("-");
            return rs.getInt("cnt");
        } catch (SQLException ex) {
            logger.debug("-", ex);
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(conn);
        }
    }
    
    /** @inheritDoc */
    @Override
    public void load(long id) throws ObjectNotFoundException {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            load(id, conn);
        } catch (SQLException ex) {
            logger.debug("- SQLException", ex);
            throw new CriticalException(ex);
        }    
        finally {
            DBHelper.close(conn);
        }
    }

    public void load(long id, Connection connection) throws ObjectNotFoundException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM menu" + " WHERE id = " + id);
            if (rs.next()) {
                Menu.load(rs, this);
            } else {
                logger.debug("- Menu not found by id: " + id);
                throw new ObjectNotFoundException("Menu not found by id: " + id);
            }
            logger.debug("-");
        } catch (SQLException ex) {
            logger.debug("- SQLException", ex);
            throw new CriticalException(ex);
        } finally {
        	DBHelper.close(rs);
        	DBHelper.close(stmt);
        }
    }
    
    private static Menu load(ResultSet rs, Menu menu) {
        logger.debug("+");
        Validate.notNull(menu, "An instance of class Menu expected");
        try {
            menu.id = makeLong(rs.getLong("id"));
            menu.langId = makeLong(rs.getLong("lang_id"));
            menu.parentMenuItemId = makeLong(rs.getLong("parent_menu_item_id"));
            menu.level = new Integer(rs.getInt("level"));
            menu.siteId = new Integer(rs.getInt("site_id"));
        } catch (SQLException ex) {
            logger.debug("SQLException", ex);
            throw new CriticalException(ex.getMessage());
        }
        logger.debug("-");
        return menu;
    }
    
    /**
     * Finds menu by class and language
     * 
     * @param langId    standard language code
     * @return          menu object; never null
     */
    public static Menu getSpecial(Long langId) {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                " SELECT * FROM menu " +
                " WHERE level = 0 " +
                " AND lang_id = " + langId +
                " AND site_id = " + Env.getSiteId());
            ResultSet rst = stmt.executeQuery();
            rst.next();
            logger.debug("-");
            return load(rst, new Menu());
        } catch (SQLException ex) {
            logger.error("SQLException", ex);
            throw new CriticalException(ex);
        } finally {
            DBHelper.close(conn);
        }
    }
    
    /**
     * Finds menu by class and language
     * 
     * @param langId    standard language code
     * @return          menu object; never null
     */
    public static Menu getSpecial(Connection conn, Long langId) {
        logger.debug("+");
        try {
            PreparedStatement stmt = conn.prepareStatement(
                " SELECT * FROM menu " +
                " WHERE level = 0 " +
                " AND lang_id = " + langId +
                " AND site_id = " + Env.getSiteId());
            ResultSet rst = stmt.executeQuery();
            rst.next();
            logger.debug("-");
            return load(rst, new Menu());
        } catch (SQLException ex) {
            logger.error("SQLException", ex);
            throw new CriticalException(ex);
        }
    }

    /**
     * Finds menu by class and language
     * 
     * @param langId    standard language code
     * @return          menu object; never null
     */
    public static Menu getMain(Long langId) {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            return getMain(langId, conn);
        } catch (SQLException ex) {
	        logger.error("SQLException", ex);
	        throw new CriticalException(ex);
	    } finally {
            DBHelper.close(conn);
        }
    }

    public static Menu getMain(Long langId, Connection connection) {
    	Menu result = null;
    	PreparedStatement stmt = null;
    	ResultSet rst = null;
    	try {
    		stmt = connection.prepareStatement(
	                " SELECT * FROM menu " +
	                " WHERE level = 1 " +
	                " AND lang_id = " + langId +
	                " AND site_id = " + Env.getSiteId());
    		rst = stmt.executeQuery();
	        if (rst.next()) {
	        	result = load(rst, new Menu());
	        	logger.debug("-");
	        } else {
	            logger.debug("- Menu not found by langId: " + langId 
	            		+ " site_id:" + Env.getSiteId());
	        }    
	        return result;
	        
	    } catch (SQLException ex) {
	        logger.error("SQLException", ex);
	        throw new CriticalException(ex);
	    } finally {
	    	DBHelper.close(rst);
	    	DBHelper.close(stmt);
	    }
    }
    
    public 	boolean isMaximumReached() {
		return getMaxTopItems() <= countItems();
	}
    
	private static int getMaxTopItems() {
		return Integer.parseInt(Env.getProperty("menu.top.items.limit", "0"));
	}

    public boolean isTopMenu() {
    	return getLevel().intValue() == 1;
    }
    
	public static boolean isUnlimitedTopMenu() {
		return getMaxTopItems() == 0;
	}

}
