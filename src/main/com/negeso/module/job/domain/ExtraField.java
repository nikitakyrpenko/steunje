/*
 * @(#)$Id: ExtraField.java,v 1.8, 2005-06-06 13:04:26Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.job.domain;

import com.negeso.framework.domain.AbstractDbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DomainHelper;
import com.negeso.framework.domain.OrderControlHelper;
import com.negeso.framework.domain.Orderable;


import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

import org.apache.log4j.Logger;


/**
 *
 * ExtraField domain
 * 
 * @version      $Revision: 9$
 * @author       Alexander G. Shkabarnya
 * @author       Olexiy Strashko
 * 
 */
public class ExtraField extends AbstractDbObject implements Orderable{

    private static Logger logger = Logger.getLogger( ExtraField.class );
    private static String tableId = "job_extra_fields";
    private static int fieldCount = 8;

    private static final String selectFromSql =
        " SELECT id, type_id, is_required, vacancy_id, template_id, is_removed, sys_name, order_number " +
        " FROM " + tableId;

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, type_id, is_required, vacancy_id, template_id, is_removed, sys_name, order_number) "+
        " VALUES (?,?,?,?,?,?,?,?) ";

    private static final String updateSql =
        " UPDATE " + tableId +
        " SET id=?, type_id=?, is_required=?, vacancy_id=?, template_id=?, " +
        " is_removed=?, sys_name = ?, order_number = ? " +
        " WHERE id=? ";

    private static final String hasDependentValuesSql = 
        " SELECT COUNT(*) AS cnt " +
        " FROM job_application_field_values " +
        " WHERE extra_field_id=? ";

	private static OrderControlHelper orderController = new OrderControlHelper(
		tableId, "vacancy_id"
	); 

    
    private Long id = null;
    private Long typeId = null;
    private boolean isRequired = false;
    private Long vacancyId = null;
    private Long templateId = null;
    private boolean isRemoved = false;
    private String sysName = null;
    private Long orderNumber = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isIsRequired() {
        return isRequired;
    }

    public void setRequired(boolean is_required) {
        this.isRequired = is_required;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long type_id) {
        this.typeId = type_id;
    }

    public Long getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(Long vacancy_id) {
        this.vacancyId = vacancy_id;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = (Long)DomainHelper.fromObject(rs.getObject("id"));
            this.typeId = (Long)DomainHelper.fromObject(rs.getObject("type_id"));
            this.isRequired = rs.getBoolean("is_required");
            this.vacancyId = (Long)DomainHelper.fromObject(rs.getObject("vacancy_id"));
            this.templateId = (Long)DomainHelper.fromObject(rs.getObject("template_id"));
            this.isRemoved = rs.getBoolean("is_removed");
            this.sysName = (String)DomainHelper.fromObject(rs.getObject("sys_name"));
            this.orderNumber = (Long)DomainHelper.fromObject(rs.getObject("order_number"));

            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt) throws SQLException {
        logger.debug( "+" );
        try{
            DomainHelper.toStatement(id, stmt, 1);
            DomainHelper.toStatement(typeId, stmt, 2);
            stmt.setBoolean(3, isRequired);
            DomainHelper.toStatement(vacancyId, stmt, 4);
            DomainHelper.toStatement(templateId, stmt, 5);
            stmt.setBoolean(6, isRemoved);
            stmt.setString(7, sysName);
            DomainHelper.toStatement(orderNumber, stmt, 8);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new SQLException("Error while saving into statement");
        }
        logger.debug( "-" );
        return stmt;
    }

    
    
    public static ExtraField findById(Connection connection, Long id)
        throws CriticalException
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                ExtraField extraField = new ExtraField();
                extraField.load(result);
                result.close();
                statement.close();
                return extraField;
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

    public boolean hasDependentValues(Connection con)
        throws CriticalException
    {
        try
        {
            PreparedStatement statement = con.prepareStatement(hasDependentValuesSql);
            statement.setLong(1, this.getId());
            ResultSet rs = statement.executeQuery();
            boolean res = true;
            if (rs.next())
            {
                if ( rs.getLong("cnt") == 0 ) {
                    res = false;
                }
            }
            rs.close();
            statement.close();
            return res;
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
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

    /**
     * @return Returns the isRemoved.
     */
    public boolean isRemoved() {
        return isRemoved;
    }
    /**
     * @param isRemoved The isRemoved to set.
     */
    public void setRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }
    /**
     * @return Returns the templateId.
     */
    public Long getTemplateId() {
        return templateId;
    }
    /**
     * @param templateId The templateId to set.
     */
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
    /**
     * @return Returns the sysName.
     */
    public String getSysName() {
        return sysName;
    }
    /**
     * @param sysName The sysName to set.
     */
    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

	/**
	 * Move up
	 *
	 * @return
	 * @throws SQLException
	 */
	public Long moveUp(Connection con) throws CriticalException {
		logger.debug("+-");
		return orderController.moveUp(
			con, this.getId(), this.getOrderNumber(), this.getVacancyId()
		);
	} 

	/**
	 * Move down
	 *
	 * @return
	 * @throws SQLException
	 */
	public Long moveDown(Connection con) throws CriticalException {
		logger.debug("+-");
		return orderController.moveDown(
			con, this.getId(), this.getOrderNumber(), this.getVacancyId()
		);
	}
	/**
	 * @return Returns the orderNumber.
	 */
	public Long getOrderNumber() {
		return orderNumber;
	}
	/**
	 * @param orderNumber The orderNumber to set.
	 */
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	
    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#insert(java.sql.Connection)
     */
    public Long insert(Connection con) throws CriticalException {
		this.setOrderNumber(
			orderController.getNextInsertOrder(
				con, this.getVacancyId()  
			)
		);					
        return super.insert(con);
    }

    /* (non-Javadoc)
     * @see com.negeso.framework.domain.DbObject#delete(java.sql.Connection)
     */
    public void delete(Connection con) throws CriticalException {
		orderController.decOrder(con, this.getVacancyId(), this.getOrderNumber());
        super.delete(con);
    }
}
