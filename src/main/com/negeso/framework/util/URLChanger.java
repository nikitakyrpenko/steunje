package com.negeso.framework.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.CriticalException;

public class URLChanger {
	private static Logger logger = Logger.getLogger(URLChanger.class);
	public static int changeLinks(Connection conn, String prevLink, String newLink, long siteId)
			throws CriticalException{
		try{
			PreparedStatement stat = conn.prepareStatement(
					" UPDATE article " +
					" SET text = replace(text, ?, ?) " +
					" WHERE site_id=? "); 
			stat.setString(1, prevLink);
			stat.setString(2, newLink);
			stat.setLong(3, new Long(siteId));
			return stat.executeUpdate();
		}
		catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new CriticalException("Can't change links in site with id="+siteId);
		}
	}
}
