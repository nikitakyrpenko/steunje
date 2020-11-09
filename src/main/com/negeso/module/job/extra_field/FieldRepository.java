/*
 * @(#)$Id: FieldRepository.java,v 1.4, 2005-06-06 13:04:27Z, Stanislav Demchenko$
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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.CriticalException;


/**
 *
 * ExtraField
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 5$
 */
public class FieldRepository {
    private static Logger logger = Logger.getLogger( FieldRepository.class );

    public static final String FIELD_TYPE_STRING = "string";
    public static final String FIELD_TYPE_NUMBER = "number";
    public static final String FIELD_TYPE_TEXT = "text";
    public static final String FIELD_TYPE_SELECT_BOX = "select_box";
    public static final String FIELD_TYPE_CHECK_BOX = "check_box";
    public static final String FIELD_TYPE_RADIO_BOX = "radio_box";
    public static final String FIELD_TYPE_FILE = "file";
    public static final String FIELD_TYPE_EMAIL = "email";
    public static final String FIELD_TYPE_DATE = "date";
    
    private static FieldRepository instance = null;

    private Map<Long, FieldType> typeCache = null; 
	
	/**
     * Singleton getter
     * 
     * @return
	 */
    public static FieldRepository get(){
        if ( FieldRepository.instance == null ){
            FieldRepository.instance = new FieldRepository();
        }
        return FieldRepository.instance;
    }
    
    /**
	 * 
	 */
	private FieldRepository() {
		super();
	}

    /**
     *  
     * @param con
     * @param typeId
     * @return
     * @throws CriticalException
     */
	public FieldType getType( Connection con, Long typeId ) 
        throws CriticalException
    {
		FieldType type = this.getTypeCache().get(typeId); 
		if ( type == null ){
            type = this.loadType(con, typeId);
            this.getTypeCache().put(typeId, type);
            logger.info("Type loaded:" + typeId );
		}
        if ( type.isExpired() ){
            type = this.loadType(con, typeId);
            this.getTypeCache().remove(typeId);
            this.getTypeCache().put(typeId, type);
            logger.info("Expired type reloaded:" + typeId );
        }
        return type;
	}
	
	/**
     * 
     * @param con
     * @param typeId
     * @return
     * @throws CriticalException
	 */
    private FieldType loadType(Connection con, Long typeId)
        throws CriticalException 
    {
        FieldType type = FieldType.findById( con, typeId );
        if ( type == null ){
            throw new CriticalException("Unknown typeId:" + typeId);
        }
        type.setExpired(false);
        return type;
    }
    
    /**
     * 
     *
     */
    public void clearCache(){
        this.getTypeCache().clear();
	}
	
    /**
     * 
     * @return
     */
    private Map<Long, FieldType> getTypeCache(){
        if ( this.typeCache == null ){
            this.typeCache = new HashMap<Long, FieldType>();
        }
        return this.typeCache;
    }
}
