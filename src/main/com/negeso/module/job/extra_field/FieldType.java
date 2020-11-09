/*
 * @(#)$Id: FieldType.java,v 1.6, 2005-06-06 13:04:27Z, Stanislav Demchenko$
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
import com.negeso.framework.domain.Cacheble;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DomainHelper;
import com.negeso.framework.domain.Language;
import com.negeso.module.job.domain.JobString;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 *
 * Field type domain 
 * 
 * @author        Alexander G. Shkabarnya
 * @author        Olexiy Strashko
 * @version       $Revision: 7$
 */
public class FieldType extends AbstractDbObject implements Cacheble {
    private static Logger logger = Logger.getLogger( FieldType.class );
    private static String tableId = "job_field_types";
    
    private static final String selectFromSql =
        " SELECT id, title_id, type " +
        " FROM " + tableId;

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, title_id, type) "+
        " VALUES (?,?,?) ";

    private static final String updateSql =
        " UPDATE " + tableId +
        " SET id=?, title_id=?, type=? " +
        " WHERE id=? ";

    private static int fieldCount = 3;

    private static final String getOptionsSql =
        " SELECT * FROM job_field_options " +
        " WHERE field_type_id = ?"
    ;

    public static final String getAllFieldTypesSql = 
    	" SELECT job_field_types.id, job_field_types.type, job_strings.value AS title " +
    	" FROM job_field_types " +
    	" LEFT JOIN job_strings ON" +
    	" job_field_types.title_id = job_strings.id " +
    	" WHERE job_strings.lang_id = ?" +
    	" ORDER BY title, type"
    ;

    private Long id = null;
    private Long titleId = null;
    private String type = null;
    
    private boolean expired = true;
    private Map<Long, String> nameCache = null;
    private List<FieldOption> options = null;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTitleId() {
        return titleId;
    }

    public void setTitleId(Long titleId) {
        this.titleId = titleId;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = (Long)DomainHelper.fromObject(rs.getObject("id"));
            this.titleId = (Long)DomainHelper.fromObject(rs.getObject("title_id"));
            this.type = (String)DomainHelper.fromObject(rs.getObject("type"));

            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt) 
        throws SQLException 
    {
        logger.debug( "+" );
        try{
            DomainHelper.toStatement(id, stmt, 1);
            DomainHelper.toStatement(titleId, stmt, 2);
            DomainHelper.toStatement(type, stmt, 3);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new SQLException("Error while saving into statement");
        }
        logger.debug( "-" );
        return stmt;
    }

    public static FieldType findById(Connection connection, Long id)
        throws CriticalException
    {
        
        if ( id == null ){
            logger.error("!!!error: id is null");
            return null;
        }
        
        PreparedStatement statement = null;
        try
        {
            statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                FieldType extraField = new FieldType();
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

    /* (non-Javadoc)
     * @see com.negeso.module.job.domain.Cacheble#isExpired()
     */
    public boolean isExpired() {
        return this.expired;
    }

    /* (non-Javadoc)
     * @see com.negeso.module.job.domain.Cacheble#setExpired()
     */
    public void setExpired(boolean newExpired) {
        this.expired = newExpired;
    }

    /**
     * @return
     */
    public List<FieldOption> getOptions(Connection con) throws CriticalException{
        if ( this.options != null){
            return this.options;
        }
        PreparedStatement stmt = null;
        try{
            if ( logger.isInfoEnabled() ){
                logger.info("Reading options for type:" + this.getType());
            }
            stmt = con.prepareStatement(FieldType.getOptionsSql);
            stmt.setLong( 1, this.getId().longValue() );
            ResultSet rs = stmt.executeQuery();
            this.options = new ArrayList<FieldOption>();
            FieldOption option = null;
            while ( rs.next() ){
                option = new FieldOption();
                option.load(rs);
                this.options.add(option);
            }
            rs.close();
            stmt.close();
        }
        catch(SQLException e){
            logger.error("-error", e);
            throw new CriticalException(e);
        }
        return this.options;
    }

    /**
     * Build xml Element for this Type
     *  
     * @param con
     * @param parent
     * @param langId
     * @return
     * @throws CriticalException
     */
    public Element buildXml(Connection con, Element parent, Long langId) 
        throws CriticalException
    {
        return TypeXmlBuilder.buildTypeXml(con, parent, this, langId);
    }

    /**
     * Build xml Element for this Type
     *  
     * @param con
     * @param parent
     * @param langId
     * @return
     * @throws CriticalException
     */
    public Element buildValueXml(
        Connection con, Element parent, Long optionId, String value, Long langId) 
        throws CriticalException
    {
        return TypeXmlBuilder.buildValueXml(
            con, parent, this, optionId, value, langId
        );
    }

    /**
     * Test if this field has options
     * 
     * @return
     */
    public boolean hasOptions(){
        return FieldRepository.FIELD_TYPE_RADIO_BOX.equals(this.getType()) ||
               FieldRepository.FIELD_TYPE_CHECK_BOX.equals(this.getType()) ||   
               FieldRepository.FIELD_TYPE_SELECT_BOX.equals(this.getType())
        ;
    }
    
    /**
     * Check if this field type has this option, by id
     * 
     * @param con
     * @param optionId
     * @return
     * @throws CriticalException
     */
    public boolean hasOption(Connection con, Long optionId) throws CriticalException{
        for (FieldOption option: this.getOptions(con)){
            if ( option.getId().equals(optionId) ){
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return
     */
    public boolean isMultipleOption() {
        if ( FieldRepository.FIELD_TYPE_CHECK_BOX.equals( this.getType() ) ){
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    public boolean isSingleOption() {
        if ( FieldRepository.FIELD_TYPE_RADIO_BOX.equals( this.getType() ) ){
            return true;
        }
        if ( FieldRepository.FIELD_TYPE_SELECT_BOX.equals( this.getType() ) ){
            return true;
        }
        return false;
    }
    
    /**
     * Build selected option set for option id and strValue for multiple option
     * FieldType, depending on choise multiplicity.
     * 
     * @param con
     * @param optionId
     * @param value
     * @param langId
     * @return
     * @throws CriticalException
     */
    public Set<Long> buildSelectedOptionSet(
        Connection con, Long optionId, String value, Long langId) 
        throws CriticalException
    {
        Set<Long> selectedOptions = null;
        if ( this.isSingleOption() ){
            selectedOptions = new HashSet<Long>();
            if ( this.hasOption(con, optionId)){
                selectedOptions.add(optionId);
            }
            else{
                logger.error(
                    " Incorrect option Id:" + optionId + 
                    " for: " + value + 
                    " in : " + this.getTitle(con, langId) 
                );
            }
        }
        else if ( this.isMultipleOption() ){
            selectedOptions = new HashSet<Long>();
            String[] values = value.split(",");
            Long curOptId = null;
            for ( String curValue: values ){
                try{
                    curOptId = new Long( curValue );
                    logger.info("hasOption:" + this.hasOption( con, curOptId ));
                    logger.info("CurOptId:" + curOptId );
                    if ( this.hasOption( con, curOptId )){
                        selectedOptions.add( curOptId );
                    }
                    else{
                        logger.error(
                            "Incorrect option Id:" + curValue + 
                            " for: " + value + 
                            " in : " + this.getTitle(con, langId) 
                        );
                    }
                }
                catch(NumberFormatException e){
                    logger.error(
                        "Incorrect option Id:" + curValue + 
                        " for: " + value + 
                        " in : " + this.getTitle(con, langId) 
                    );
                }
            }
        }
        return selectedOptions;
    }
}
