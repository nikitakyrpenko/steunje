/*
 * @(#)$Id: FieldOption.java,v 1.5, 2005-06-06 13:04:27Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.job.extra_field;

import com.negeso.framework.domain.AbstractDbObject;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DomainHelper;
import com.negeso.framework.domain.Language;
import com.negeso.module.job.domain.JobString;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;




/**
 *
 * Field type option domain 
 * 
 * @author        Alexander G. Shkabarnya
 * @author        Olexiy Strashko
 * @version       $Revision: 6$
 */
public class FieldOption extends AbstractDbObject {
    private static Logger logger = Logger.getLogger( FieldOption.class );

    private static String tableId = "job_field_options";

    private static int fieldCount = 4;

    private static final String selectFromSql =
        " SELECT id, field_type_id, title_id, is_default " +
        " FROM " + tableId;

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, field_type_id, title_id, is_default) "+
        " VALUES (?, ?, ?, ?) ";

    private static final String updateSql =
        " UPDATE " + tableId +
        " SET id=?, field_type_id=?, title_id=?, is_default=? " +
        " WHERE id=? ";

    private Long id = null;
    private Long fieldTypeId = null;
    private Long titleId = null;
    private boolean isDefault = false;
    private Map<Long, String> nameCache = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFieldTypeId() {
        return fieldTypeId;
    }

    public void setFieldTypeId(Long extra_field_id) {
        this.fieldTypeId = extra_field_id;
    }

    public Long getTitleId() {
        return titleId;
    }

    public void setTitleId(Long titleId) {
        this.titleId = titleId;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = (Long)DomainHelper.fromObject(rs.getObject("id"));
            this.fieldTypeId = (Long)DomainHelper.fromObject(rs.getObject("field_type_id"));
            this.titleId = (Long)DomainHelper.fromObject(rs.getObject("title_id"));
            this.isDefault = rs.getBoolean("is_default");

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
            DomainHelper.toStatement(this.getId(), stmt, 1);
            DomainHelper.toStatement(this.getFieldTypeId(), stmt, 2);
            DomainHelper.toStatement(this.getTitleId(), stmt, 3);
            stmt.setBoolean(4, this.isDefault());
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new SQLException("Error while saving into statement");
        }
        logger.debug( "-" );
        return stmt;
    }

    public static FieldOption findById(Connection connection, Long id)
        throws CriticalException
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                FieldOption extraField = new FieldOption();
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
     * @param con
     * @param langId
     * @return
     * @throws CriticalException 
     */
    public String getTitle(Connection con, Long langId) throws CriticalException {
        if ( this.nameCache == null ){
            this.nameCache = JobString.getStringMapById( con, this.getTitleId() );
        }
        String name = this.nameCache.get(langId);
        if ( name == null ){
            name = this.nameCache.get(
                Language.getDefaultLanguage().getId()
            );
        }
        return name;
    }

    /**
     * @return
     */
    public boolean isDefault() {
        return this.isDefault;
    }
    
    /**
     * @param isDefault The isDefault to set.
     */
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

	/**
	 * 
	 * @param con
	 * @param optionTile
	 * @param langId
	 * @throws CriticalException 
	 */
	public void setTitle(Connection con, String optionTitle, Long langId) 
		throws CriticalException 
    {
		JobString jString = JobString.findById(con, this.getTitleId(), langId);
		if ( jString == null ){
			jString = new JobString();
			jString.setValue(optionTitle);
			jString.setId(langId);
			jString.setLangId(langId);
			jString.insert(con);
		}
		else{
			jString.setValue(optionTitle);
			jString.update(con);
		}
	}
}
