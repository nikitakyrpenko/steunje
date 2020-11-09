/*
 * @(#)DomainObject.java       @version	12.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.domain;

import java.sql.SQLException;

/**
 * Base class for all domain objects
 *
 * @version 	15.12.2003
 * @author 	 	Stanislav Demchenko
 * @author 	 	Olexiy Strashko
 */
public abstract class DomainObject {

//    protected static String tableId = null;

    protected Long id = null;
    protected boolean wasSynchronized = false;
    
    public DomainObject(Long id)
    {
        this.id = id;
    }

    abstract public String getTableId();

    abstract public Long insert() throws CriticalException;

    abstract public void update() throws CriticalException;

    abstract public void delete() throws CriticalException;

    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Test if object is loaded from DB. Simply - check if
     * id not null - object is loaded. Validation for existance
     * by id in DB is not performed.
     *
     * @return true if object is loaded from DB
     */
    public boolean isLoaded()
    {
        if (getId() == null) {
            return false;
        }
        return true;
    }

    /**************************************************************
     * Check is the object was synchronized with DB
     * @return
     **************************************************************/
    public boolean isSynchronized()
    {
        return wasSynchronized;
    }


    abstract public void load(long id)
        throws ObjectNotFoundException, CriticalException;


    /**
     * Helper method to read numeric foreign or primary keys from db as Long
     * (value object).
     * 
     * @return positive Long value or null
     * @throws SQLException
     */
    public static Long makeLong(long value) {
        return value > 0 ? new Long(value) : null;
    }
    
    
    /**
     * Helper method to read numeric values from db as Integers.
     * 
     * @return positive Integer value or null
     * @throws SQLException
     */
    public static Integer makeInteger(int value) {
        return value > 0 ? new Integer(value) : null;
    }
    
    
}
