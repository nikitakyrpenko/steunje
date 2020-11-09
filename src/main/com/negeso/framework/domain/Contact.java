/*
 * @(#)$Id: Contact.java,v 1.13, 2007-01-09 18:23:06Z, Anatoliy Pererva$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

import java.io.Serializable;
import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import com.negeso.framework.Env;
import com.negeso.framework.io.xls.XlsColumnNumber;
import com.negeso.framework.jaxb.TimestampAdapter;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.generators.Xbuilder;
import com.negeso.module.contact_book.Configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *
 * User/Company contact domain  
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 14$
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "contact", namespace = Env.NEGESO_NAMESPACE)
public class Contact implements DbObject, Serializable, Cloneable {
	private static Logger logger = Logger.getLogger( Contact.class );

	private static String tableId = "contact";
    
	private static final String findByIdSql = 
	    " SELECT id, first_name, second_name, company_name, address_line," +
	    " zip_code, state, city, country, phone, fax, type, web_link, email," +
	    " image_link, department, job_title, birth_date, nickname " +
	    " FROM " +
		tableId + 
		" WHERE id = ? "
	;

    private static final String findByDateSql =
	    " SELECT id, first_name, second_name, company_name, address_line," +
	    " zip_code, state, city, country, phone, fax, type, web_link, email," +
	    " image_link, department, job_title, birth_date, nickname " +
	    " FROM " +
		tableId
	;

	private static final String findByTypeSql =
	    " SELECT id, first_name, second_name, company_name, address_line," +
	    " zip_code, state, city, country, phone, fax, type, web_link, email," +
	    " image_link, department, job_title, birth_date, nickname " +
	    " FROM " +
		tableId + 
		" WHERE type = ? "
	;

	private static final String insertSql =
		" INSERT INTO "	+ 
		tableId + 
		" (" +
		"id, first_name, second_name, company_name, address_line," +
	    "zip_code, state, city, country, phone, fax, type, web_link, email," +
	    "image_link, department, job_title, birth_date, nickname " +
	    ") " +
		" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
	;

	private final static String updateSql =
		" UPDATE " +
		tableId +
		" SET " +
		" id=?, first_name=?, second_name=?, company_name=?, " +
		" address_line=?, zip_code=?, state=?, city=?, country=?, " +
		" phone=?, fax=?, type=?, web_link=?, email=?, image_link=?, " +
        " department=?, job_title=?, birth_date=?, nickname=? " +
		" WHERE id = ?"
	;
	
	private static int fieldCount = 19;
	
	@XmlAttribute private Long id = null;
	@XmlAttribute @XlsColumnNumber(4) private String firstName = null;
	@XmlAttribute @XlsColumnNumber(4) private String secondName = null;
	@XmlAttribute private String companyName = null;
	@XmlAttribute @XlsColumnNumber(5) private String addressLine = null;
	@XmlAttribute @XlsColumnNumber(7) private String zipCode = null;
	@XmlAttribute private String state = null;
	@XmlAttribute @XlsColumnNumber(6) private String city = null;
	@XmlAttribute @XlsColumnNumber(8) private String country = null;
	@XmlAttribute @XlsColumnNumber(9) private String phone = null;
	@XmlAttribute @XlsColumnNumber(10) private String fax = null;
	@XmlAttribute private String type = null;
	@XmlAttribute private String webLink = null;
	@XmlAttribute private String email = null;
	@XmlAttribute private String imageLink = null;
    @XmlAttribute private String department = "";
    @XmlAttribute private String jobTitle = "";
    @XmlAttribute @XmlJavaTypeAdapter(TimestampAdapter.class)
    private Timestamp birthDate = null;
    @XmlAttribute private String nickname = null;


	/**
	 * Finds Contact by id. Return null if contact not exists.
	 * 
	 * @param con
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws CriticalException
	 */
	public static Contact findById(
		Connection con, Long id
	) 
		throws CriticalException
	{
		logger.debug("+");
		Contact obj = null;
		try{
			PreparedStatement stmt = con.prepareStatement(
				Contact.findByIdSql
			);
			stmt.setLong(1, id.longValue());  
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				obj = new Contact();
				obj.load(rs);
			}
			else{
				return null;
			}
			rs.close();
			stmt.close();
			logger.debug("-");
			return obj;
		}
		catch(SQLException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
	}

	/**
	 * Finds Contact by type. Return null if contact not exists.
	 * 
	 * @param con
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws CriticalException
	 */
	public static Contact findByType(
		Connection con, String type
	) 
		throws CriticalException
	{
		logger.debug("+");
		Contact obj = null;
		try{
			PreparedStatement stmt = con.prepareStatement(
				Contact.findByTypeSql
			);
			stmt.setString(1, type);  
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				obj = new Contact();
				obj.load(rs);
			}
			else{
				return null;
			}
			rs.close();
			stmt.close();
			logger.debug("-");
			return obj;
		}
		catch(SQLException e){
			logger.error("-error", e);
			throw new CriticalException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#getId()
	 */
	public Long getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#setId(java.lang.Long)
	 */
	public void setId(Long newId) {
		this.id = newId;
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#load(java.sql.ResultSet)
	 */
	public Long load(ResultSet rs) throws CriticalException {
		logger.debug("+");
		try{
			this.id = DBHelper.makeLong(rs.getLong("id")); 
			this.firstName = rs.getString("first_name"); 
			this.secondName = rs.getString("second_name"); 
			this.companyName = rs.getString("company_name"); 
			this.addressLine = rs.getString("address_line"); 
			this.zipCode = rs.getString("zip_code"); 
			this.state = rs.getString("state"); 
			this.city = rs.getString("city"); 
			this.country = rs.getString("country"); 
			this.phone = rs.getString("phone"); 
			this.fax = rs.getString("fax");
			this.type = rs.getString("type");
			this.webLink = rs.getString("web_link");
			this.email = rs.getString("email");
			this.imageLink = rs.getString("image_link");
            this.department = rs.getString("department");
            this.jobTitle = rs.getString("job_title");
            if (rs.getObject("birth_date") == null)
                this.birthDate = null;
            else
                this.birthDate = rs.getTimestamp("birth_date");
            this.nickname = rs.getString("nickname");
		}
		catch( SQLException e ){
			throw new CriticalException(e);
		}
		logger.debug("-");
		return this.getId();
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#saveIntoStatement(java.sql.PreparedStatement)
	 */
	public PreparedStatement saveIntoStatement(PreparedStatement stmt)
		throws SQLException 
	{
		logger.debug( "+" );
		stmt.setLong(   1, this.getId().longValue() );
		stmt.setString( 2, this.getFirstName() );
		stmt.setString( 3, this.getSecondName() );
		stmt.setString( 4, this.getCompanyName() );
		stmt.setString( 5, this.getAddressLine() );
		stmt.setString( 6, this.getZipCode() );
		stmt.setString( 7, this.getState() );
		stmt.setString( 8, this.getCity() );
		stmt.setString( 9, this.getCountry() );
		stmt.setString( 10, this.getPhone() );
		stmt.setString( 11, this.getFax() );
		stmt.setString( 12, this.getType() );
		stmt.setString( 13, this.getWebLink() );
		stmt.setString( 14, this.getEmail() );
		stmt.setString( 15, this.getImageLink() );
        stmt.setString( 16, this.getDepartment() );
        stmt.setString( 17, this.getJobTitle() );
        if (this.getBirthDate()==null)
            stmt.setObject(18, null);
        else
            stmt.setTimestamp( 18, this.getBirthDate() );
        stmt.setString( 19, this.getNickname() );
		logger.debug( "-" );
		return stmt;
	}

	/**
	 * Get Element
	 * 
	 * @param doc
	 * @return
	 */
	public Element getElement(Document doc){
		Element contactEl = Xbuilder.createEl(doc, "contact", null);
		Xbuilder.setAttr(contactEl, "id", this.getId());
		Xbuilder.setAttr(contactEl, "first-name", this.getFirstName());
		Xbuilder.setAttr(contactEl, "second-name", this.getSecondName());
		// title is first-name + second name
		String titleStr = this.getSecondName();
	    if ( titleStr == null ){
	        titleStr = "";
	    }
	    Xbuilder.setAttr(contactEl, "title", MessageFormat.format(
	            "{0} {1}", this.getFirstName(), titleStr 
	        )
	    ); 
		Xbuilder.setAttr(contactEl, "company-name", this.getCompanyName());
		Xbuilder.setAttr(contactEl, "address-line", this.getAddressLine());
		Xbuilder.setAttr(contactEl, "zip-code", this.getZipCode());
		Xbuilder.setAttr(contactEl, "state", this.getState());
		Xbuilder.setAttr(contactEl, "city", this.getCity());
		Xbuilder.setAttr(contactEl, "country", this.getCountry());
		Xbuilder.setAttr(contactEl, "phone", this.getPhone());
		Xbuilder.setAttr(contactEl, "fax", this.getFax());
		Xbuilder.setAttr(contactEl, "type", this.getType());
		Xbuilder.setAttr(contactEl, "web-link", this.getWebLink());
		Xbuilder.setAttr(contactEl, "email", this.getEmail());
		Xbuilder.setAttr(contactEl, "image-link", this.getImageLink());
        Xbuilder.setAttr(contactEl, "department", this.getDepartment());
        Xbuilder.setAttr(contactEl, "job-title", this.getJobTitle());
        Xbuilder.setAttr(contactEl, "birthday", getDate(this.birthDate));
        Xbuilder.setAttr(contactEl, "nickname", this.getNickname());
		return contactEl;
	}
	
	
	public Object clone(){
		Contact clone = new Contact();
		clone.setFirstName(this.getFirstName());
		clone.setSecondName(this.getSecondName());
		clone.setCompanyName(this.getCompanyName());
		clone.setAddressLine(this.getAddressLine());
		clone.setZipCode(this.getZipCode());
		clone.setState(this.getState());
		clone.setCity(this.getCity());
		clone.setCountry(this.getCountry());
		clone.setPhone(this.getPhone());
		clone.setFax(this.getFax());
		clone.setType(this.getType());
		clone.setWebLink(this.getWebLink());
		clone.setEmail(this.getEmail());
        clone.setDepartment(this.getDepartment());
        clone.setJobTitle(this.getJobTitle());
        clone.setBirthDate(this.getBirthDate());
        clone.setNickname(this.getNickname());
		return clone;
	}
	
	

    /* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#insert(java.sql.Connection)
	 */
	public Long insert(Connection con) throws CriticalException {
		return DBHelper.insertDbObject(con, this);
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#update(java.sql.Connection)
	 */
	public void update(Connection con) throws CriticalException {
		DBHelper.updateDbObject(con, this);
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#delete(java.sql.Connection)
	 */
	public void delete(Connection con) throws CriticalException {
		DBHelper.deleteObject(
			con, this.getTableId(), this.getId(), Contact.logger
		);
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#getTableId()
	 */
	public String getTableId() {
		return Contact.tableId;
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#getFindByIdSql()
	 */
	public String getFindByIdSql() {
		return Contact.findByIdSql;
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#getUpdateSql()
	 */
	public String getUpdateSql() {
		return Contact.updateSql;
	}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#getInsertSql()
	 */
	public String getInsertSql() {
		return Contact.insertSql;
}

	/* (non-Javadoc)
	 * @see com.negeso.framework.domain.DbObject#getFieldCount()
	 */
	public int getFieldCount() {
		return Contact.fieldCount;
	}
	/**
	 * @return Returns the addressLine.
	 */
	public String getAddressLine() {
		return addressLine;
	}
	/**
	 * @param addressLine The addressLine to set.
	 */
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
	/**
	 * @return Returns the city.
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city The city to set.
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return Returns the companyName.
	 */
	public String getCompanyName() {
		return companyName;
	}
	/**
	 * @param companyName The companyName to set.
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	/**
	 * @return Returns the country.
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country The country to set.
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return Returns the fax.
	 */
	public String getFax() {
		return fax;
	}
	/**
	 * @param fax The fax to set.
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}
	/**
	 * @return Returns the firstName.
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName The firstName to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return Returns the phone.
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone The phone to set.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return Returns the secondName.
	 */
	public String getSecondName() {
		return secondName;
	}
	/**
	 * @param secondName The secondName to set.
	 */
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return Returns the zipCode.
	 */
	public String getZipCode() {
		return zipCode;
	}
	/**
	 * @param zipCode The zipCode to set.
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return Returns the state.
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state The state to set.
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return Returns the webLink.
	 */
	public String getWebLink() {
		return webLink;
	}
	/**
	 * @param webLink The webLink to set.
	 */
	public void setWebLink(String webLink) {
		this.webLink = webLink;
	}
	/**
	 * @return Returns the imageLink.
	 */
	public String getImageLink() {
		return imageLink;
	}
	/**
	 * @param imageLink The imageLink to set.
	 */
	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

    public String getDepartment() {
		return department;
	}

	public void setDepartment(String newDepartment) {
		department = newDepartment;
	}

    public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String newJobTitle) {
		jobTitle = newJobTitle;
	}

    public Timestamp getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Timestamp newBirthDate) {
		birthDate = newBirthDate;
	}

    public static String getFindByDateSql(int start, int end){
        //" WHERE ( ? <= birth_day ) AND ( birth_day <= ? ) "
        String where;
        if ( start < end )
            where = " WHERE type='" + Configuration.getContactType() + "' AND ( ? <= EXTRACT(doy FROM birth_date) ) AND ( EXTRACT(doy FROM birth_date) <= ? ) ORDER BY EXTRACT(doy FROM birth_date) ";
        else
            where = " WHERE type='" + Configuration.getContactType() + "' AND  ( ? >= EXTRACT(doy FROM birth_date) ) AND ( EXTRACT(doy FROM birth_date) >= ? ) ORDER BY EXTRACT(doy FROM birth_date) ";
        return findByDateSql+where;
    }

    /**
     * @return
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    

    private static String getDate(Timestamp date){
        if (date==null){
            return "";
        }
   		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
   		return dateFormat.format(date);
    }

}
