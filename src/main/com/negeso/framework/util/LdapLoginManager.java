/*
 * @(#)$Id: LdapLoginManager.java,v 1.3, 2007-01-10 11:27:36Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.util;

import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.ldap.LdapLogEntry;

/**
 * 
 * @TODO
 * 
 * @author		Andrey V. Morskoy
 * @version		$Revision: 4$
 *
 */
public class LdapLoginManager {
	 private static Logger logger = Logger.getLogger(LdapLoginManager.class);
	 
	 public static String RESULT_ACCEPTED = "accepted";
	 public static String RESULT_DENIED = "denied";
	 public static String RESULT_NOT_SUPERUSER = "notSuperuser";
	 public static String RESULT_NOT_ENOUGH_RIGHTS = "notEnoughRights";
	 public static String RESULT_LDAP_NOT_ACCESSIBLE = "ldapNotAccessible";
	 public static String DEMO_CN = "demousers";
	 public static String PRODUCTION_CN = "produsers";
	 
	 
	private static final String SUPERUSER_LOG_FILE = Env.getRealPath("WEB-INF/generated/logs/superuser_log.csv");
	
	String ldapDN;
	String domain;
	String ldapServer;
	
	
	/**
	 * Constracts new manager.
	 * @param ldapDomainName - main name writen in '.' separated form
	 * @param domain - DNS domain with LDAP SRV records
	 */
	public LdapLoginManager( String ldapDN, String domain ){
		logger.debug("+");
		this.ldapDN = ldapDN;
		this.domain = domain;
		logger.debug("-");
	}
	
	/**
	 * Constracts new manager with configuration from application properties 
	 * or JNDI default context (java:comp/env). 
	 */
	public LdapLoginManager(){
		logger.debug("+");
		this.ldapDN = Env.getJndiProperty("uaLdapDN", "o=Negeso");
		this.domain = Env.getJndiProperty("uaDomain", "negeso.com");
		logger.debug("-");
	}

	/**
	 * gets LdapServer "host:port" string from DNS domain with LDAP SRV records.
	 * SRV record have format: prio weight port host (e.g., 30 30 636 ldap-3.negeso.com.)   
	 * Looks up server with max(2*prio+weight).
	 * @return LdapServer "host:port" string
	 * @throws Exception if DNS domain fails
	 */
	private String getLdapServer() throws Exception {
		logger.debug("+");
		if (this.ldapServer == null) {			
			TreeMap servers = new TreeMap <Integer,String> ();
			DirContext context = new InitialDirContext();
			Attributes attributes = context.getAttributes(
					"dns:/_ldap._tcp." + this.domain,
					new String[] { "SRV" });
			Enumeration values = attributes.get("SRV").getAll();
			while (values.hasMoreElements()) {
			    StringTokenizer tokenizer = new StringTokenizer((String) values.nextElement(), " ");
			    int i = Integer.parseInt( tokenizer.nextToken() ) * 2 + Integer.parseInt( tokenizer.nextToken() );
			    int port = Integer.parseInt( tokenizer.nextToken() );
			    String host = tokenizer.nextToken();
				if (host.lastIndexOf('.') == host.length()-1) {
					host = host.substring(0,host.length()-1); 
				}
			    servers.put(new Integer(i), host + ":" + port);			    
			}
			if (!servers.isEmpty()) {
				this.ldapServer = servers.get(servers.lastKey()).toString();
				logger.debug("ldapServer = " + ldapServer);
			} else {
				logger.error("ldapServer not found for domain " + this.domain);
				throw new CommunicationException("ldapServer not found for domain " + this.domain);
			}
		}
		
		logger.debug("-");
		return this.ldapServer;
	}
	
	
	/**
	 * gets SuperUserCn (demo or prod) specified by memberUid (login) for ldapServerName<br>
	 * @param login
	 * @return userCn, or null, if failed to find
	 * @throws Exception if server fails, or if arg are bad
	 */
	private String getSuperUserCn(String memberUid) throws Exception {
		logger.debug("+");
		Hashtable<String,String> env = new Hashtable<String,String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		
		env.put(Context.PROVIDER_URL, 
				"ldap://" + getLdapServer() + "/ou=CMSadmins," + this.ldapDN);

		InitialLdapContext ctx = new InitialLdapContext(env,null);
		try {
			if ( searchUserInCn(memberUid, PRODUCTION_CN, ctx) != -1 ) 
				return PRODUCTION_CN;
			if ( searchUserInCn(memberUid, DEMO_CN, ctx) != -1 )
				return DEMO_CN;			
		} finally {
			ctx.close();
		}
		
		logger.debug("-");
		return null;
	}

	private int searchUserInCn(String memberUid, String cn, InitialLdapContext ctx) throws NamingException {
		logger.debug("+");
		Attributes matchAttrs = new BasicAttributes(true);
		
		matchAttrs.put("cn",cn); 
		matchAttrs.put("memberUid",memberUid); 
	
		String[] retAttrs = {"gidNumber"};
		NamingEnumeration answer = ctx.search("", matchAttrs,retAttrs);
		int result =-1;
		while( answer.hasMore() ){
			SearchResult sr = (SearchResult)answer.next();
			result = Integer.parseInt( sr.getAttributes().get("gidNumber").get(0).toString() );
		}
		logger.debug("-");
		return result;
	}
	
	
	/**
	 * binds neccesary user - NL or UA
	 * @param login
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private InitialLdapContext bindUser(String login, String password)throws Exception{
		logger.debug("+");
		Hashtable<String,String> env = new Hashtable<String,String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, 
				"ldap://" + getLdapServer() + "/c=UA,ou=People,"+this.ldapDN);
		
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_CREDENTIALS,password);
		
		env.put(Context.SECURITY_PRINCIPAL,"uid="+login+", c=UA, ou=People, "+this.ldapDN);
		
		InitialLdapContext ctx = null;
		boolean failed = false;
		
		try{
			ctx = new InitialLdapContext(env,null);
		}
		catch (AuthenticationException e) {
			failed = true;
		}

		if(failed){
			env.put(Context.PROVIDER_URL, 
					"ldap://" + getLdapServer() + "/c=NL,ou=People,"+this.ldapDN);		
			env.put(Context.SECURITY_PRINCIPAL,"uid="+login+", c=NL, ou=People, "+this.ldapDN);
			ctx = new InitialLdapContext(env,null);
		}
		logger.debug("-");
		return ctx;
		
	}
	
	
	/**
	 * 
	 * @param login
	 * @param password
	 * @param isPasswordHashed
	 * @return
	 * @throws Exception
	 */
	public String authUser(String login, String password, boolean isPasswordHashed) throws Exception{
		logger.debug("+");
		String result = RESULT_DENIED;
		String userCn = getSuperUserCn(login);//demo prod or null
		
		InitialLdapContext ctx = bindUser(login, password);
			
		if (userCn == null) {
			result =  RESULT_NOT_SUPERUSER;
		} else if ( Env.getJndiProperty("cmsProfile", "demo").equals(userCn.substring(0,4)) || userCn.equals(PRODUCTION_CN)) {
			result =  RESULT_ACCEPTED;
		} else {
			result = RESULT_NOT_ENOUGH_RIGHTS;
		} 

	    ctx.close();
		logger.debug("-");
		return result;
		
	}
	
	public static void logAuth(RequestContext request, String login, Long adminId, String result){
		logger.debug("+");
		try{
			 StringBuffer logLine = new StringBuffer();
		     logLine.append(new Date(System.currentTimeMillis())+";");
		     logLine.append(login+";");
		     logLine.append(adminId+";");
		     logLine.append(request.getRemoteAddr()+";");
		     logLine.append(Env.getHostName()+";");
		     logLine.append(result+"\n");
		     
		     RandomAccessFile rf= new RandomAccessFile(SUPERUSER_LOG_FILE,"rw");
		     rf.seek(rf.length());
		     rf.writeBytes(logLine.toString());
		     rf.close();
		 }
		 catch (Exception e) {
			 logger.error("Failed to make superuser's log: ",e);
		 }
		 logger.debug("-");
	}
	
	
	public static void storeAuthLogToDb(RequestContext request, String login, Long adminId, String result){
		logger.debug("+");
		Connection con = null;
		PreparedStatement stmp = null;
		try{
			
			con = getUaDbConnection(); 		
			
			String insertSql = " insert into superusers_log  " +
					" (date,login,admin_id,remote_addr,host_name,result) " +
					" VALUES ( ?,?,?,?,?,? ); ";
			
			stmp = con.prepareStatement(insertSql);
			stmp.setTimestamp(1, new Timestamp(new Date().getTime()));
			stmp.setString(2, login);
			if(adminId==null){
				stmp.setNull(3, Types.BIGINT);
			}
			else{
				stmp.setLong(3, adminId); 
			}
			stmp.setString(4, request.getRemoteAddr());
			stmp.setString(5, Env.getHostName());
			stmp.setString(6, result);
			
			stmp.execute();						

			stmp.close();
			con.close();
		}
		catch (Exception driverExc) {
			logger.error("Failed to store log in DB:",driverExc);
		}
		logger.debug("-");
	}

	private static Connection getUaDbConnection() throws ClassNotFoundException, SQLException {
		logger.debug("+");
		Connection con;
		Class.forName("org.postgresql.Driver");
		con = DriverManager.getConnection(
				Env.getJndiProperty("uaDbUrl", null),
				Env.getJndiProperty("uaDbUser", null),
				Env.getJndiProperty("uaDbPassword", null)
		);
		logger.debug("-");
		return con;
	}
	
	
	public static List<String> getDbLogHosts(){
		logger.debug("+");
		List<String> result = new ArrayList<String>();
		
		Connection con = null;
		PreparedStatement stmp = null;
		ResultSet rs = null;
		try{
			con = getUaDbConnection();		
			String logSql = "  SELECT host_name from superusers_log; ";
			
			stmp = con.prepareStatement(logSql);
			rs = stmp.executeQuery();						

			while(rs.next()){
				result.add(rs.getString("host_name"));
			}
			
			rs.close();
			stmp.close();
			con.close();
		}
		catch (Exception driverExc) {
			logger.error("Failed to get host_names from DB:",driverExc);
		}
		
		logger.debug("-");
		return result;
	}
	
	public static List<LdapLogEntry> getDbLogCSV(String hostName, String loginFilter){
		logger.debug("+");
		List<LdapLogEntry> result = new ArrayList<LdapLogEntry>();
	
		Connection con = null;
		PreparedStatement stmp = null;
		ResultSet rs = null;
		try{
			con = getUaDbConnection();
			
			String logSql;
			if("".equals(loginFilter)){
				loginFilter = null;
			}
			if(hostName==null || hostName.equals("All")){
				logSql = "  SELECT * from superusers_log " +
					 (loginFilter==null? " " : " where login= '"+loginFilter+"'") +
					 " order by date desc; ";
			}
			else{
				logSql = "  SELECT * from superusers_log where host_name = '" + hostName +  "' " +
						(loginFilter==null? " " : " and login= '"+loginFilter+"'") +
						" order by date desc; ";
			}
			logger.error("SQL: "+logSql);
			
			stmp = con.prepareStatement(logSql);
			rs = stmp.executeQuery();						

			while(rs.next()){
				LdapLogEntry entry = new LdapLogEntry();
				entry.load(rs);
				result.add(entry);
			}
			
			rs.close();
			stmp.close();
			con.close();
		}
		catch (Exception driverExc) {
			logger.error("Failed to get log in DB:",driverExc);
		}
		
		logger.debug("-");
		return result;
	}
		
}
