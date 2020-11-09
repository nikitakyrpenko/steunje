package com.negeso.framework.ldap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.negeso.framework.domain.CriticalException;

public class LdapLogEntry{
	
	public static final String LDAP_REFERENCE = "logEntry";

	Long id;
	Timestamp logDate;
	String login;
	Long adminId;
	String remoteAddr;
	String result;
	String siteName;
	
	
	
	public LdapLogEntry(Timestamp logDate,String login,Long adminId,  String remoteAddr, String result,String siteName) 
	{
		this.adminId = adminId;
		this.logDate = logDate;
		this.login = login;
		this.remoteAddr = remoteAddr;
		this.result = result;
		this.siteName = siteName;
	}

	
	public LdapLogEntry(){};
	
		
	public void load(ResultSet rs)throws CriticalException{
		try{
			logDate = rs.getTimestamp("date");
			login = rs.getString("login");
			adminId = rs.getLong("admin_id");
			remoteAddr = rs.getString("remote_addr");
			siteName = rs.getString("host_name");
			result = rs.getString("result");
		}
		catch (SQLException sqlExc) {
			throw new CriticalException();
		}
	}
	
	
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}
	

	

	



	public String getSiteName() {
		return siteName;
	}


	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}


	public Timestamp getLogDate() {
		return logDate;
	}



	public void setLogDate(Timestamp logDate) {
		this.logDate = logDate;
	}

	
	public Long getAdminId() {
		return adminId;
	}
	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getRemoteAddr() {
		return remoteAddr;
	}
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	public String isResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}


	public String getResult() {
		return result;
	}
	
	
	
}
