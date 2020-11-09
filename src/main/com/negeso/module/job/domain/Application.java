/*
 * @(#)$Id: Application.java,v 1.4, 2005-06-06 13:04:07Z, Stanislav Demchenko$
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

import java.sql.*;

import org.apache.log4j.Logger;


/**
 *
 * Department Xml builder
 * 
 * @version      $Revision: 5$
 * @author       Alexander G. Shkabarnya
 * @author       Olexiy Strashko
 * 
 */
public class Application extends AbstractDbObject{
    private Long id = null;
    private String name = null;
    private String surname = null;
    private String address = null;
    private String telephone = null;
    private String mobile = null;
    private String email = null;
    private Timestamp birthdate = null;
    private String birthplace = null;
    private String citizenship = null;
    private String cv = null;
    private Date postDate = new Date( System.currentTimeMillis() );

    private static Logger logger = Logger.getLogger( Application.class );
    private static String tableId = "job_applications";
    private static int fieldCount = 12;

    private static final String selectFromSql =
        " SELECT id, name, surname, address, telephone, mobile, email, birthdate, " +
        " birthplace, citizenship, cv, post_date " +
        " FROM " + tableId;

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, name, surname, address, telephone, mobile, email, birthdate, " +
        " birthplace, citizenship, cv, post_date) "+
        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ";

    private static final String updateSql =
        " UPDATE " + tableId +
        " SET id=?, name=?, surname=?, address=?, telephone=?, " +
        " mobile=?, email=?, birthdate=?, birthplace=?, citizenship=?, " +
        " cv=?, post_date = ? " +
        " WHERE id=? ";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Timestamp birthdate) {
        this.birthdate = birthdate;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = (Long)DomainHelper.fromObject(rs.getObject("id"));
            this.name = (String)DomainHelper.fromObject(rs.getObject("name"));
            this.surname = (String)DomainHelper.fromObject(rs.getObject("surname"));
            this.address = (String)DomainHelper.fromObject(rs.getObject("address"));
            this.telephone = (String)DomainHelper.fromObject(rs.getObject("telephone"));
            this.mobile = (String)DomainHelper.fromObject(rs.getObject("mobile"));
            this.email = (String)DomainHelper.fromObject(rs.getObject("email"));
            this.birthdate = (Timestamp)DomainHelper.fromObject(rs.getObject("birthdate"));
            this.birthplace = (String)DomainHelper.fromObject(rs.getObject("birthplace"));
            this.citizenship = (String)DomainHelper.fromObject(rs.getObject("citizenship"));
            this.cv = (String)DomainHelper.fromObject(rs.getObject("cv"));
            this.postDate = (Date)DomainHelper.fromObject(rs.getObject("post_date"));

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
            DomainHelper.toStatement(name, stmt, 2);
            DomainHelper.toStatement(surname, stmt, 3);
            DomainHelper.toStatement(address, stmt, 4);
            DomainHelper.toStatement(telephone, stmt, 5);
            DomainHelper.toStatement(mobile, stmt, 6);
            DomainHelper.toStatement(email, stmt, 7);
            DomainHelper.toStatement(birthdate, stmt, 8);
            DomainHelper.toStatement(birthplace, stmt, 9);
            DomainHelper.toStatement(citizenship, stmt, 10);
            DomainHelper.toStatement(cv, stmt, 11);
            DomainHelper.toStatement(postDate, stmt, 12);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new SQLException("Error while saving into statement");
        }
        logger.debug( "-" );
        return stmt;
    }

    public static Application findById(Connection connection, Long id)
        throws CriticalException
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                Application application = new Application();
                application.load(result);
                result.close();
                statement.close();
                return application;
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
     * @return Returns the postDate.
     */
    public Date getPostDate() {
        return postDate;
    }
    
    /**
     * @param postDate The postDate to set.
     */
    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }
}
