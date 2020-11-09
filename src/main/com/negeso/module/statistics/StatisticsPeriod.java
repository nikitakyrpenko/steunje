/*
 * @(#)Id: StatisticsPeriod.java, 03.12.2007 12:56:06, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.statistics.domain.Statistics;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class StatisticsPeriod{
	
	public static final int LIMIT_STATISCTICS_NUMBER = 7000;

	private static Logger logger = Logger.getLogger(StatisticsPeriod.class);

	private static final String tableId = "stat_archive";
	
	private static final int fieldCount = 7;
	
	private static String insertQuery = "INSERT INTO " + tableId + "(page,hits,user_id,month,year,site_id) VALUES (?,?,?,?,?,?)";
	
	private static String insertQueryNoUser = "INSERT INTO " + tableId + "(page,hits,month,year,site_id) VALUES (?,?,?,?,?)";
	
	public final static String selectFrom =
		"SELECT id, user_id, user_login, user_ip, hit_date, hit_time, page_name, referer, event, site_id" +
        " from stat_statistics";
	
	private final static String findByYearMonth =
        selectFrom + " where EXTRACT(YEAR FROM stat_statistics.hit_date) = ? AND" +
        " EXTRACT(MONTH FROM stat_statistics.hit_date) = ? limit " + LIMIT_STATISCTICS_NUMBER;
    
    private final static String getYears = 
    	"select distinct EXTRACT(YEAR FROM stat_statistics.hit_date) from stat_statistics";
    
    private final static String getMonthes = 
    	"select distinct EXTRACT(MONTH FROM stat_statistics.hit_date) from stat_statistics " +
    			" where EXTRACT(YEAR FROM stat_statistics.hit_date) = ?";
	
	private Long id;
    private Long userId;
    private Long hits = 0L;
    private Long year;
    private Long month;
    private String pageName;
    private Long siteId;    

	public int getFieldCount() {
		return fieldCount;
	}

	public String getFindByIdSql() {
		return null;
	}

	public Long getId() {
		return id;
	}

	public String getInsertSql() {
		return insertQuery;
	}

	public String getTableId() {
		return tableId;
	}

	public String getUpdateSql() {
		return null;
	}

	public void insert(Connection con) throws CriticalException {
		logger.debug("+");
		PreparedStatement st = null;
		try {
			if (userId == null){
				st = con.prepareStatement( insertQueryNoUser );
			}else{
				st = con.prepareStatement( insertQuery);
			}
			st = saveIntoStatement(st);
			st.execute();
		} catch (SQLException e) {
			logger.error("error while inserting statistics period:" + e);
		} finally{
			DBHelper.close(st);
		}
		logger.debug("-");
	}

	public PreparedStatement saveIntoStatement(PreparedStatement stmt)throws SQLException {
		stmt.setString(1, getPageName());
		stmt.setLong(2, getHits());
		if (getUserId() != null){
			stmt.setLong(3, getUserId());
			stmt.setLong(4, getMonth());
			stmt.setLong(5, getYear());
		}else{
			stmt.setLong(3, getMonth());
			stmt.setLong(4, getYear());
			stmt.setLong(5, getSiteId());
		}
		return stmt;
	}

	public void setId(Long newId) {
		this.id = newId;
	}

	public String toString() {
        logger.debug("+");
        StringBuffer sb = new StringBuffer("Statistics [");
        sb.append("id=");
        sb.append(id);
        sb.append(" user_id=");
        sb.append(userId);
        sb.append(" hits=");
        sb.append(hits);
        sb.append(" pageName=");
        sb.append(pageName);
        sb.append(" month=");
        sb.append(month);
        sb.append(" year=");
        sb.append(year);
        sb.append(" site_id=");
        sb.append(siteId);        
        sb.append("]");
        logger.debug("-");
        return sb.toString();
    }
	
	public void setUserId(Long userId){
		this.userId = userId;
	}
	
	public void setPageName(String pageName){
		this.pageName = pageName;
	}
	
	public void setHits(Long hits){
		this.hits = hits;
	}
	
	public void setMonth(Long month){
		this.month = month;
	}
	
	public void setYear(Long year){
		this.year = year;
	}
	
	public String getPageName(){
		return pageName;
	}
	
	public Long getUserId(){
		return userId;
	}
	
	public Long getHits(){
		return hits;
	}
	
	public Long getMonth(){
		return month;
	}
	
	public Long getYear(){
		return year;
	}
	
	public Long getSiteId(){
		return siteId;
	}

	public void addHit(){
		hits++;
	}
	
	public void setSiteId(Long site_id){
		siteId = site_id;
	}
	
	public static void insertByMonth(Connection conn, ArrayList<StatisticsPeriod> period){
		logger.debug("+");
		if (period == null || period.isEmpty()){
			logger.error("- error statistics period is emtpy.return");
			return;
		}
		try{
			Iterator<StatisticsPeriod> i = period.iterator();
			while(i.hasNext()){
				StatisticsPeriod sPeriod = i.next();
				sPeriod.insert(conn);
			}
		}catch(Exception e){
			logger.error("- error during archive old statistics.return " + e);
		}
		logger.debug("-");
	}
	
	public static void deletePeriod(Connection conn, ArrayList<Statistics> statistics){
    	logger.debug("+");
		try{
			Iterator<Statistics> i = statistics.iterator();
			while(i.hasNext()){
				Statistics s = i.next();
				s.delete(conn);
			}
		}catch(Exception e){
			logger.debug("- error during deleting statistics period");
		}
		logger.debug("-");
    }
	
	/**
	 * @return
     * @throws SQLException 
	 */
	public static List<String> findYears(Connection conn){
		logger.debug("+");
		List<String> years = new ArrayList<String>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
	        stmt = conn.prepareStatement(getYears);
	        rs = stmt.executeQuery();
	        while (rs.next()) {
	        	String curr_year = rs.getString(1);
	        	years.add(curr_year);
	        }
	    }
	    catch (SQLException ex) {
	    	logger.error("- no statistics found:" + ex.getMessage());
	    }
	    finally {
	    	DBHelper.close(rs);
	    	DBHelper.close(stmt);
	    }
	    logger.debug("-");
	    return years;
	}
	
	public static List<String> findMonthesByYear(Connection conn, String year){
		logger.debug("+");
		List<String> monthes = new ArrayList<String>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBHelper.getConnection();
	        stmt = conn.prepareStatement(getMonthes);
	        stmt.setString(1, year);
	        rs = stmt.executeQuery();
	        while (rs.next()) {
	        	String curr_month = rs.getString(1);
	        	monthes.add(curr_month);
	        }
	    }
	    catch (SQLException ex) {
	        logger.error("- no statistics for " + year + ":" + ex.getMessage());
	    }
	    finally {
	    	DBHelper.close(rs);
	    	DBHelper.close(stmt);
	    }
	    logger.debug("-");
	    return monthes;
	}
	
	public static ArrayList<Statistics> findByYearAndMonth(Connection conn, String year, String month){
		logger.debug("+");
		ArrayList<Statistics> statistics = new ArrayList<Statistics>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBHelper.getConnection();
			stmt = conn.prepareStatement(findByYearMonth);
	        stmt.setString(1, year);
	        stmt.setString(2, month);
	        rs = stmt.executeQuery();
	        while (rs.next()) {
	        	Statistics stat = new Statistics();
	        	stat.load(rs);
	        	statistics.add(stat);
	        }
	    }
	    catch (SQLException ex) {
	        logger.error("- no statistics found for " + year + ":" + month + ":" + ex.getMessage());
	    }
	    finally {
	    	DBHelper.close(rs);
	    	DBHelper.close(stmt);
	    }
	    logger.debug("-");
	    return statistics;
	}	
}
