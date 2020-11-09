/*
 * @(#)$Id: SystemLogService.java,v 1.0, 2006-11-27 07:48:34Z, Olexiy Strashko$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.negeso.framework.domain.DBHelper;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;

/**
 * 
 * Log service. To use batch logging - setup batch key before usage.
 * Batch logging can be used for import operations when requred to log and 
 * to view batch of events.  
 * 
 * 
 * @author		Olexiy V. Strashko
 * @version		$Revision: 1$
 *
 */
public class SystemLogService {
    private static final Logger logger = Logger.getLogger(SystemLogService.class);
	private Connection connection;
	private User user;
	private String batchKey;
	private List<LogEvent> events;
	private boolean errors = false;

	public static volatile int rows;
	public static AtomicInteger progress;
	public static AtomicInteger buffer;

	public SystemLogService(Connection connnection, User user) {
		this.connection = connnection;
		this.user = user;
	}
	
	public void addEvent(
		String event, String object, String description,
		String result, String moduleKey, String service) 
	{
		LogEvent logEvent = new LogEvent();
		logEvent.setBatchKey(this.getBatchKey());
		logEvent.setDescription(description);
		logEvent.setEvent(event);
		logEvent.setModuleKey(moduleKey);
		logEvent.setObject(object);
		logEvent.setResult(result);
		logEvent.setService(service);
		if (logEvent.isErrorResult()) {
			this.setErrors(true);
		}
		this.getEvents().add(logEvent);
	}

	/**
	 * Flushes all events into database. Optimized by batch updates.
	 */ 
	public void flush() {
		try {
			PreparedStatement stmt = this.getConnection().prepareStatement(
				"INSERT INTO core_systemlog " +
				"(event_date, author, event, object, description, result, module_key, service, batch_key, site_id) " +
				"VALUES (now(), ?, ?, ?, ?, ?, ?, ?, ?, ?)"
			);
			for (LogEvent event: this.getEvents()) {
				this.addEventToDb(event, stmt);
			}
			stmt.executeBatch();
		} catch (Exception e) {
			logger.error("-error", e);
		}
	}
	
	private void addEventToDb(LogEvent event, PreparedStatement stmt) throws SQLException{
		if (user != null) {
			stmt.setString(1, user.getName());
		} else {
			stmt.setString(1, null);
		}
		stmt.setString(2, event.getEvent());
		stmt.setString(3, event.getObject());
		stmt.setString(4, event.getDescription());
		stmt.setString(5, event.getResult());
		stmt.setString(6, event.getModuleKey());
		stmt.setString(7, event.getService());
		stmt.setString(8, event.getBatchKey());
		stmt.setLong(9, Env.getSiteId());
		stmt.addBatch();
	}

	/**
	 * Get log events for the latest (maximum) batch key
	 * 
	 * @param parent
	 * @param moduleKey
	 * @param service
	 * @return
	 */
	public Element buildLastEvents(Element parent, String moduleKey, String service) {
		Element log = Xbuilder.addEl(parent, "SystemLog", null);
		try {
			PreparedStatement stmt = this.getConnection().prepareStatement(
				"SELECT * FROM core_systemlog WHERE module_key=? AND service=? AND site_id=? AND batch_key = ? ORDER BY event_date");
			stmt.setString(1, moduleKey);
			stmt.setString(2, service);
			stmt.setLong(3, Env.getSiteId());
			stmt.setString(4, this.getMaxBatchKey(moduleKey, service));
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Element event = Xbuilder.addEl(log, "LogEvent", null);
				Xbuilder.setAttr(event, "date", Env.formatRoundDate(rs.getTimestamp("event_date")) );
				Xbuilder.setAttr(event, "author", rs.getString("author"));
				Xbuilder.setAttr(event, "event", rs.getString("event"));
				Xbuilder.setAttr(event, "object", rs.getString("object"));
				Xbuilder.setAttr(event, "description", rs.getString("description"));
				Xbuilder.setAttr(event, "result", rs.getString("result"));
				Xbuilder.setAttr(event, "service", rs.getString("service"));
			}
		} catch (Exception e) {
			logger.error("-error", e);
		}
		return log;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getMaxBatchKey(String moduleKey, String service) {
		try {
			PreparedStatement stmt = this.getConnection().prepareStatement(
				"SELECT MAX(batch_key) AS max_batch_key FROM core_systemlog WHERE module_key=? AND service=? AND site_id=?");
			stmt.setString(1, moduleKey);
			stmt.setString(2, service);
			stmt.setLong(3, Env.getSiteId());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString("max_batch_key");
			}
		} catch (Exception e) {
			logger.error("-error", e);
		}
		return null;
	}

	/**
	 * Setups the value for the batch key. It is calculated as long value from
	 * current timestamp.
	 * @param moduleKey the name of the module for which the log entries are
	 * gathered.
	 * @param service the name of the service inside of module.
	 */
	public void setupBatchKey(String moduleKey, String service) {
		String currentTime = String.valueOf(System.currentTimeMillis());
		this.setBatchKey(currentTime);
	}

	/**
	 * @return Returns the batchKey.
	 */
	public String getBatchKey() {
		return batchKey;
	}

	/**
	 * @param batchKey The batchKey to set.
	 */
	public void setBatchKey(String batchKey) {
		this.batchKey = batchKey;
	}

	public List<LogEvent> getEvents() {
		if (events == null) {
			events = new ArrayList<LogEvent>();
		}
		return events;
	}

	public boolean hasErrors() {
		return errors;
	}

	public void setErrors(boolean errors) {
		this.errors = errors;
	}

	public void addEvent(SystemLogConstants.Event event, String name, String message, SystemLogConstants.Result result, String moduleKey, String serviceType) {
		this.addEvent(event.name(), name, message, result.name(), moduleKey, serviceType);
	}

	public static List<LogEvent> getAll(String... logKeys){
		Connection conn = null;
		List<LogEvent> events = new ArrayList<LogEvent>();
		try {
			conn = DBHelper.getConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM core_systemlog where object IN (?, ?)");
			stmt.setString(1, logKeys[0]);
			stmt.setString(2, logKeys[1]);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				LogEvent event = new LogEvent();
				event.setEvent(rs.getString("event"));
				event.setDescription(rs.getString("description"));
				event.setResult(rs.getString("result"));
				event.setService(rs.getString("service"));
				event.setModuleKey(rs.getString("module_key"));
				events.add(event);
			}
		} catch (Exception e) {
			logger.error("-error", e);
		}finally {
			DBHelper.close(conn);
		}
		return events;
	}

	public static void resetCounter(){
		SystemLogService.rows = 0;
		SystemLogService.progress = new AtomicInteger(0);
		SystemLogService.buffer = new AtomicInteger(0);
	}
}
