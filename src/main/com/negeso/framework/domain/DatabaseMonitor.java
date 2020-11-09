/*
 * @(#)$Id: DatabaseMonitor.java,v 1.0.1.0, 2007-02-12 15:57:50Z, Vyacheslav Zapadnyuk$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 2$
 *
 */
public class DatabaseMonitor extends Thread {

    private static final Logger logger = Logger.getLogger(DatabaseMonitor.class);
	
    private static final DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SS");
    
	private static int LOG_INTERVAL = 60000;
	
	private static final String LOG_PATH = "/WEB-INF/generated/logs";
	private static final String LOG_FILENAME = "connections.log";
	
	private File logFile;
	
	private List<Entry> entries = new LinkedList<Entry>();
	
	private static DatabaseMonitor instance;
	
	private boolean shouldRun = false;
	
	public static synchronized DatabaseMonitor getInstance() {
		if (instance == null) {
			instance = new DatabaseMonitor();
		}
		return instance;
	}
	
	private class Event {

		public static final int ADD_CONNECTION 		= 0;
		public static final int REMOVE_CONNECTION 	= 1;
		
		private long time;
		private int type;

		public Event(int type) {
			this.type = type;
		}
		
		public void setTime(long time) {
			this.time = time;
		}
		
		public long getTime() {
			return time;
		}
		
		public void setType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}
	
	private static class Entry {

		private int connectionId;
		private int userId;
		private String caller;
		private Event event;

		public Entry(int connectionId, int userId) {
			this.connectionId = connectionId;
			this.userId = userId;
		}
		
		public int getConnectionId() {
			return connectionId;
		}
		
		public int getUserId() {
			return userId;
		}
		
		public void setCaller(String caller) {
			this.caller = caller;
		}
		
		public String getCaller() {
			return caller;
		}

		public void setEvent(Event event) {
			this.event = event;
		}
		
		public Event getEvent() {
			return event;
		}
	}

	public DatabaseMonitor() {
		super();
		File destFolder = new File(Env.getRealPath(LOG_PATH));
		logFile = new File(destFolder, LOG_FILENAME);
	}
	
	public static String format(StackTraceElement[] ste) {
		// parse a strack trace
		StackTraceElement element = ste[5];
		String name = String.format("%s.%s",
				element.getClassName(),
				element.getMethodName());
		return name;
	}
	
	private void addEntry(int connectionId, Thread t, int eventType) {
//		//
//        StackTraceElement[] trace = t.getStackTrace();
//        for (int i=0; i < trace.length; i++)
//            System.out.println("\tat " + trace[i]);
//		//
		int threadId = getThreadId(t);
		Entry entry = new Entry(connectionId, threadId);
		String caller = format(t.getStackTrace());
		entry.setCaller(caller);
		Event event = new Event(eventType);
		event.setTime(System.currentTimeMillis());
		entry.setEvent(event);
		entries.add(entry);
	}
	
	public synchronized void obtainConnectionEvent(Connection conn, Thread t) {
		if (conn != null)
			addEntry(getConnectionId(conn), t, Event.ADD_CONNECTION);
	}
	
	public synchronized void closeConnectionEvent(Connection conn, Thread t) {
		if (conn != null)
			addEntry(getConnectionId(conn), t, Event.REMOVE_CONNECTION);
	}

	private int getConnectionId(Connection conn) {
		return conn.hashCode();
	}

	private int getThreadId(Thread t) {
		return t.hashCode();
	}
	
	public void run() {
		logger.debug("+");
		while (!interrupted() && shouldRun) {
			try {
				Thread.sleep(LOG_INTERVAL);
				synchronized(entries) {
					Writer writer = null;
					try {
						writer = new FileWriter(logFile, true);
						String result = createReport();
						writer.write(result);
					} catch (Throwable t) {
						logger.error(t);
						shouldRun = false;
					} finally {
						try {
							if (writer != null) {
								writer.close();
							}
						} catch (IOException e) {
							// do nothing
						}
					}
					entries.clear();
				}
				
			} catch (InterruptedException e) {
				logger.debug("Thread interrupted");
				// do nothing
			}
		}
		logger.debug("-");
	}
	
	private String createReport() {
		StringBuilder sb = new StringBuilder();
		
		sb.append('\n').
		append(format.format(new Date()));
		sb.append("\n--- Summary ---");
		Set<Integer> userIds = getUserIds();
		Set<String> callers = getCallers();
		sb.append("\nNumber of users: ").
		append(userIds.size()).
		append("\nNumber of callers: ").
		append(callers.size());
		
		if (entries.size() > 0) {
			sb.append(String.format("\n\n| %15s | %10s | %80s | %10s |",
					"CONNECTION ID", "USER ID", "CALLER", "EVENT"));
			sb.append("\n-------------------------------------------------------").
			append("-----------------------------------------------------------").
			append("--------------");
			for (Entry entry : entries) {
				sb.append(String.format("\n| %15s | %10s | %80s | %10s |",
						entry.getConnectionId(),
						entry.getUserId(),
						entry.getCaller(),
						(entry.getEvent() == null ? "-" : ""+entry.getEvent().getType())));
			}
		}
		
		for (Integer id : userIds) {
			sb.append("\n\nUser #").
			append(id).
			append("\n--------------------").
			append("\nNumber of obtained connections: ").
			append(getNumberEvents(id, Event.ADD_CONNECTION)).
			append("\nNumber of closed connections: ").
			append(getNumberEvents(id, Event.REMOVE_CONNECTION));

			sb.append("\n\nAverage Time").
			append("\n--------------------");
			for (String caller : callers) {
				sb.append('\n').
				append(caller).
				append(": ");
				appendAverageTime(sb, id, caller);
			}
		}
		sb.append("\n\n");
		return sb.toString();
	}

	private int getNumberEvents(int userId, int eventType) {
		int count = 0;
		for (Entry e : entries) {
			if (e.getUserId() == userId 
					&& e.getEvent() != null
					&& e.getEvent().getType() == eventType) {
				count++;
			}
		}
		return count;
	}

	private Set<Integer> getUserIds() {
		Set<Integer> set = new LinkedHashSet<Integer>();
		for (Entry e : entries) {
			int id = e.getUserId();
			if (!set.contains(id)) {
				set.add(id);
			}
		}
		return set;
	}
	
	private Set<String> getCallers() {
		Set<String> set = new LinkedHashSet<String>();
		for (Entry e : entries) {
			String caller = e.getCaller();
			if (!set.contains(caller)) {
				set.add(caller);
			}
		}
		return set;
	}

	private long appendAverageTime(StringBuilder sb, int userId, String caller) {
		List<Event> addConnectionEvents = new LinkedList<Event>(); 
		List<Event> removeConnectionEvents = new LinkedList<Event>(); 
		for (Entry entry : entries) {
			if (entry.getUserId() == userId
					&& entry.getCaller().equals(caller)
					&& entry.getEvent() != null) {
				Event event = entry.getEvent();
				if (event.getType() == Event.ADD_CONNECTION)
					addConnectionEvents.add(event);
				else if (event.getType() == Event.REMOVE_CONNECTION)
					removeConnectionEvents.add(event);
			}
		}
		
		if (addConnectionEvents.size() != removeConnectionEvents.size()) {
			return -1;
		}

		long summaryTime = 0L;
		int size = addConnectionEvents.size();
		for (int i = 0; i < size; i++) {
			long beginTime = addConnectionEvents.get(i).getTime();
			long endTime = removeConnectionEvents.get(i).getTime();
			summaryTime += (endTime - beginTime);
		}
		long averageTime = (size == 0) ? 0 : summaryTime / size;
		sb.append(averageTime).
		append(" ms, ").
		append(size).
		append(" time(s) was called");
		return averageTime; 
	}

	public synchronized void requestStart() {
		logger.debug("+");
		shouldRun = true;
		start();
		logger.debug("-");
	}	
	
	public synchronized void requestStop() {
		logger.debug("+");
		shouldRun = false;
		interrupt();
		instance = null;
		logger.debug("-");
	}
	
	public void setInterval(int interval) {
		LOG_INTERVAL = interval;
	}
}
