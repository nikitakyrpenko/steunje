/*
 * Created on 12.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.domain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DbObject;


/**
 * @author OLyebyedyev
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WcmsTemplate implements DbObject {
    private static Logger logger = Logger.getLogger(WcmsTemplate.class);
    
    private Long id = null;
    
    private String name = null;
    
    private Boolean isMandatory = null;
    
    private String comment = null;
    
    private ArrayList elements = new ArrayList(3);
    
    
    private static final String getTemplateSQL = 
        " SELECT " +
        " wat.wcms_templ_id, " +
        " wat.name AS template_name, " +
        " wat.is_mandatory, " +
        " wat.comment, " +
        " tel.wcms_attr_templ_el_id, " +
        " tel.attr_name, " +
        " tel.ordernum, " +
        " tel.help_text, " +
        " attype.name AS type_name, " +
        " attype.wcms_attr_type_id " +
        " FROM " +
        " wcms_attr_template wat, " +
        " wcms_attr_templ_element tel, " +
        " wcms_attr_type attype " +
        " WHERE " +
        " wat.wcms_templ_id = ? " +
        " AND wat.wcms_templ_id = tel.wcms_templ_id " +
        " AND attype.wcms_attr_type_id = tel.wcms_attr_type_id " +
        " ORDER BY tel.ordernum ";
    
    /**
     * 
     */
    public WcmsTemplate() {
        super();
        
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getId()
     */
    public Long getId() {
        logger.debug("+-");
        return this.id;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#setId(java.lang.Long)
     */
    public void setId(Long newId) {
        logger.debug("+");
        if (newId == null || newId.longValue()<=0){
            logger.error("Incorrect id:" + newId );
            throw new RuntimeException("Incorrect Id: " + newId);
        }
        this.id = newId;     
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#load(java.sql.ResultSet)
     */
    public Long load(ResultSet rs) throws CriticalException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#insert(java.sql.Connection)
     */
    public Long insert(Connection con) throws CriticalException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#update(java.sql.Connection)
     */
    public void update(Connection con) throws CriticalException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#delete(java.sql.Connection)
     */
    public void delete(Connection con) throws CriticalException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getTableId()
     */
    public String getTableId() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFindByIdSql()
     */
    public String getFindByIdSql() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getUpdateSql()
     */
    public String getUpdateSql() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getInsertSql()
     */
    public String getInsertSql() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#getFieldCount()
     */
    public int getFieldCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#saveIntoStatement(java.sql.PreparedStatement)
     */
    public PreparedStatement saveIntoStatement(PreparedStatement stmt)
            throws SQLException {
        throw new UnsupportedOperationException("This method is not implemented");
    }
    
    public static WcmsTemplate getTemplate(Connection con, Long id) 
        throws CriticalException
    {
        logger.debug("+");
        if (id == null || id.longValue()<=0){
            logger.error("Incorrect id: " + id);
            throw new CriticalException("Incorrect id: " + id);
        }
        try {
            
            PreparedStatement pstmt = con.prepareStatement(WcmsTemplate.getTemplateSQL);
            pstmt.setLong(1, id.longValue());
            ResultSet rs = pstmt.executeQuery();
            /*
        " wat.wcms_templ_id, " +
        " wat.name AS template_name, " +
        " wat.is_mandatory, " +
        " wat.comment, " +
        " tel.wcms_attr_templ_el_id " +
        " tel.attr_name, " +
        " tel.ordernum, " +
        " tel.help_text, " +
        " attype.name AS type_name,
        " attype.wcms_attr_type_id " +
             */
            WcmsTemplate wt = new WcmsTemplate();
            while (rs.next()) {
                wt.id = new Long(rs.getLong("wcms_templ_id"));
                wt.name = rs.getString("template_name");
                wt.isMandatory = new Boolean(Boolean.valueOf("is_mandatory"));
                wt.comment = rs.getString("comment");
                WcmsTemplateElement elem = new WcmsTemplateElement();
                elem.templId = wt.id;  
                elem.id = new Long(rs.getLong("wcms_attr_templ_el_id"));
                elem.attributeName = rs.getString("attr_name");
                elem.orderNum = new Integer(rs.getInt("ordernum"));
                elem.helpText = rs.getString("help_text");
                String typeName = rs.getString("type_name");
                elem.typeId = new Integer(rs.getInt("wcms_attr_type_id"));
                elem.elementType = WcmsAttributeType.getType(typeName);
                if (elem.elementType == null) {
                    logger.error("- Unknown element type " + typeName );
                    throw new CriticalException("Unknown element type " + typeName);
                }
                wt.elements.add(elem);
            }
            rs.close();
            pstmt.close();
            
            logger.debug("-");
            return wt;    
            
            
        } catch (SQLException ex) {
            logger.error("Exception during reading template", ex);
            throw new CriticalException(ex);
        }
        
        
        
        
    }
    
    /**
     * 
     * @return
     */
    public ArrayList getElements()
    {
        return elements;
    }
}
