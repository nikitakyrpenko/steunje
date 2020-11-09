/*
 * @(#)Module.java
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;
import com.negeso.framework.domain.Cacheble;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.DbObject;
import com.negeso.framework.module.domain.ModuleItem;
import com.negeso.module.core.domain.ConfigurationParameter;

/**
 * @author Sergiy Oliynyk
 */
public class Module implements DbObject, Cacheble, Entity {

	private static final long serialVersionUID = 9092373360254464516L;

	private static Logger logger = Logger.getLogger(Module.class);

    private Set<ConfigurationParameter> configurationParameters = new HashSet<ConfigurationParameter>();
    private Set<ModuleItem> items = new HashSet<ModuleItem>();
    
    public static final String tableId = "module";
    private static final int fieldCount = 6;


    private final static String findByIdSql =
        "SELECT id, name, golive, expired, url, title FROM module WHERE id=?";

    private final static String findByNameSql =
        "SELECT id, name, golive, expired, url, title FROM module WHERE name=? and site_id = ? ";

    private Long id;
    private String name;
    private String title;
    private Timestamp golive;
    private Long orderNumber;
    private Timestamp expiredDate;
    private String url;
    private boolean isExpired = true;
    private String image;
    private String dictionaryKey;
    private Integer parametersCount;
    private Integer constsCount;
    private Integer visibleParametersCount;
    private String helpUrl;
    
    /*
     * Constructors
     */
    public Module() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug("+");
        try {
            id = DBHelper.makeLong(rs.getLong("id"));
            name = rs.getString("name");
            golive = rs.getTimestamp("golive");
            expiredDate = rs.getTimestamp("expired");
            url = rs.getString("url");
            title = rs.getString("title");
            logger.debug("-");
            return id;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
    }

    public Long insert(Connection con) throws CriticalException { return null; }

    public void update(Connection con) throws CriticalException {}

    public void delete(Connection con) throws CriticalException {}

    public String getTableId() {
        return tableId;
    }

    public String getFindByIdSql() {
        return findByIdSql;
    }

    public String getUpdateSql() { return null; }

    public String getInsertSql() { return null; }

    public int getFieldCount() {
        return fieldCount;
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
       throws SQLException
    {
        return stmt;
    }

    
    public static Module findByName(Connection conn, String moduleName, String siteId)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(findByNameSql);
            stmt.setString(1, moduleName);
            stmt.setLong(2, new Long(siteId));
            ResultSet rs = stmt.executeQuery();
            Module module = null;
            if (rs.next()) {
                module = new Module();
                module.load(rs);
            }
            logger.debug("-");
            return module;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }

    
    public static Module findById(Connection conn, Long id)
        throws CriticalException
    {
        logger.debug("+");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(findByIdSql);
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            Module module = null;
            if (rs.next()) {
                module = new Module();
                module.load(rs);
            }
            logger.debug("-");
            return module;
        }
        catch (SQLException ex) {
            logger.error("- Throwing new CriticalException");
            throw new CriticalException(ex);
        }
        finally {
            DBHelper.close(stmt);
        }
    }
    
    public static Module getById(Connection conn, Long id)
        throws CriticalException
    {
        Module module = findById(conn, id);
        if (module == null)
            throw new CriticalException("Module not found by id=" + id);
        return module;
    }

    public boolean isActive() {
        return isActive(golive, expiredDate);
    }

    public static boolean isActive(Timestamp golive, Timestamp expired) {
        if (golive == null)
            return false;

        if (expired == null)
            return true;

        Timestamp currDate =
            new Timestamp(java.util.Calendar.getInstance().getTimeInMillis());

        return (currDate.compareTo(golive) >= 0 &&
            currDate.compareTo(expired) < 0);
    }

    public Element getElement(Document doc) {
        logger.debug("+");
        Element moduleElement = Env.createDomElement(doc, "module");
        moduleElement.setAttribute("id", String.valueOf("id"));
        moduleElement.setAttribute("name", name);
        if (golive != null)
            moduleElement.setAttribute("golive", golive.toString());
        if (expiredDate != null)
            moduleElement.setAttribute("expired", expiredDate.toString());
        moduleElement.setAttribute("url", url);
        moduleElement.setAttribute("active", String.valueOf(
            isActive(golive, expiredDate)));
        logger.debug("-");
        return moduleElement;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Timestamp getGolive() {
        return golive;
    }
    
    public void setGolive(Timestamp golive) {
        this.golive = golive;
    }
    
    public Timestamp getExpiredDate() {
        return expiredDate;
    }
    
    public void setExpiredDate(Timestamp expiredDate) {
        this.expiredDate = expiredDate;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.Cacheble#isExpired()
     */
    public boolean isExpired() {
        return this.isExpired;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.Cacheble#setExpired(boolean)
     */
    public void setExpired(boolean newExpired) {
        this.isExpired = newExpired;
    }

	public Set getConfigurationParameters() {
		return configurationParameters;
	}

	public void setConfigurationParameters(Set configurationParameters) {
		this.configurationParameters = configurationParameters;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Set<ModuleItem> getItems() {
		return items;
	}

	public void setItems(Set<ModuleItem> items) {
		this.items = items;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDictionaryKey() {
		return dictionaryKey;
	}

	public void setDictionaryKey(String dictionaryKey) {
		this.dictionaryKey = dictionaryKey;
	}

	public Integer getParametersCount() {
		return parametersCount;
	}

	public void setParametersCount(Integer parametersCount) {
		this.parametersCount = parametersCount;
	}

	public Integer getConstsCount() {
		return constsCount;
	}

	public void setConstsCount(Integer constsCount) {
		this.constsCount = constsCount;
	}

	public Integer getVisibleParametersCount() {
		return visibleParametersCount;
	}

	public void setVisibleParametersCount(Integer visibleParametersCount) {
		this.visibleParametersCount = visibleParametersCount;
	}

	public String getHelpUrl() {
		return helpUrl;
	}

	public void setHelpUrl(String helpUrl) {
		this.helpUrl = helpUrl;
	}
}
