/*
 * @(#)$Id: TypeXmlBuilder.java,v 1.5, 2005-06-06 13:05:12Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.job.extra_field;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;


import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;

/**
 *
 * Type 
 * 
 * @version		$Revision: 6$
 * @author		Olexiy Strashko
 * 
 */
public class TypeXmlBuilder {
    private static Logger logger = Logger.getLogger( TypeXmlBuilder.class );

    /**
     * Build FieldType Xml, including options
     * 
     * @param con
     * @param parent
     * @param fieldType
     * @param langId
     * @return
     * @throws CriticalException
     */
    public static Element buildTypesXml(
        Connection con, Element parent, Long langId) 
        throws CriticalException
    {
        Element typeList = Xbuilder.addEl( parent, "field_type_list", null );
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            stmt = con.prepareStatement(FieldType.getAllFieldTypesSql);
            stmt.setLong(1, langId);
            rs = stmt.executeQuery();
            Element el = null;
            while ( rs.next() ){
            	el = Xbuilder.addEl(typeList, "field_type", null);
            	Xbuilder.setAttr(el, "id", rs.getLong("id"));
            	Xbuilder.setAttr(el, "title", rs.getString("title"));
            	Xbuilder.setAttr(el, "type_name", rs.getString("type"));
            }
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
        } finally {
        	DBHelper.close(rs);
        	DBHelper.close(stmt);
        }
        return typeList;
    }
    
    
    /**
     * Build FieldType Xml, including options
     * 
     * @param con
     * @param parent
     * @param fieldType
     * @param langId
     * @return
     * @throws CriticalException
     */
    public static Element buildTypeXml(
        Connection con, Element parent, FieldType fieldType, Long langId) 
        throws CriticalException
    {
        return TypeXmlBuilder.buildGeneralXml(false, con, parent, fieldType, null, null, langId);
    }

    /**
     * Build FieldType with values
     * 
     * @param con
     * @param parent
     * @param fieldType
     * @param langId
     * @return
     * @throws CriticalException
     */
    public static Element buildValueXml(
        Connection con, 
        Element parent, 
        FieldType fieldType, 
        Long optionId, 
        String value, 
        Long langId
        ) 
        throws CriticalException
    {
        return TypeXmlBuilder.buildGeneralXml(
            true, con, parent, fieldType, optionId, value, langId
        );
    }


    /**
     * Build FieldType and value Xml, including selected options, 
     * if: isValueProcessing = true - returns also checked options and value
     * else: build type only
     *    
     * 
     * @param con
     * @param parent
     * @param fieldType
     * @param langId
     * @return
     * @throws CriticalException
     */
    public static Element buildGeneralXml(
        boolean isValueProcessing, 
        Connection con, 
        Element parent, 
        FieldType fieldType,
        Long optionId,
        String value,
        Long langId 
        ) 
        throws CriticalException
    {
        logger.debug("+");
        Element field = Xbuilder.addEl(parent, "field", null);
        Xbuilder.setAttr(field, "type_id", fieldType.getId());
        Xbuilder.setAttr(field, "type_name", fieldType.getType());
        Xbuilder.setAttr(field, "title", fieldType.getTitle(con, langId));
        
        
        if ( fieldType.hasOptions() ) {
            Set<Long> selectedOptions = null;
            if ( isValueProcessing ){
                selectedOptions = fieldType.buildSelectedOptionSet(con, optionId, value, langId);
            }
            
            FieldOption option = null;
            Element optionEl = null;
            Iterator i = fieldType.getOptions(con).iterator();
            for ( ; i.hasNext(); ){
                option = (FieldOption) i.next();
                optionEl = Xbuilder.addEl(field, "option", null);
                Xbuilder.setAttr(optionEl, "id", option.getId());
                Xbuilder.setAttr(optionEl, "title", option.getTitle(con, langId));
                if ( isValueProcessing ){
                    if ( selectedOptions != null ){
                        if ( selectedOptions.contains( option.getId() ) ){
                            Xbuilder.setAttr(optionEl, "selected", "true");
                        }
                    }
                }
                else{
                    Xbuilder.setAttr(optionEl, "selected", "" + option.isDefault());
                }
            }
        }
        else{
            if ( isValueProcessing ){
                Xbuilder.setAttr(field, "value", value);
            }
        }
        logger.debug("-");
        return field;
    }



}
