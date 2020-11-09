

/* 
 * @Project DBTest
 * @Package common
 * 
 * @Created 06.11.2003
 * 
 * (p)2003 Negeso Ukraine
 */
 
package com.negeso.framework.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;


/**
 * @author 	Olexiy.Strashko
 * @class 	Timer
 * @file 	Timer.java
 * @package common
 * 
 * 
 */

public class Timer {
	/**
	 * Timer utility class for time tuning.
	 * works in milliseconds. 
	 *  
	 */

	private DecimalFormat formatter = null;
	
	private long totalTime = 0;
	private long startTime = 0;

	public Timer() {
		/*
		 * Counstruction, timer automatically starts, use stop() to get time
		 */
		super();
		this.startTime = Timer.getCurrentTimeInMillis();
	}

	static public long getCurrentTimeInMillis(){
		return System.currentTimeMillis();
	}

	public long start(){
		/*
		 * Start timer
		 */
		this.startTime = Timer.getCurrentTimeInMillis();
		return this.startTime;
	}

	public long stop(){
		/*
		 * Stop timer, and then start it again
		 */
		long retTime = Timer.getCurrentTimeInMillis() - this.startTime;
	    this.totalTime += retTime; 
		this.start();
		return retTime;
	}
	
	public long total(){
	    return this.totalTime;
	}
	
	public String formatTotal(){
	    return "" + this.totalTime + " ms"; 
	}
	/**
	 * To String. Stops the timer and formats time
	 */
	public String toString(){
	    return this.getFormatter().format(this.stop()); 
	}
	
	private NumberFormat getFormatter(){
		if ( this.formatter == null ){
			this.formatter = new DecimalFormat("###,###,###,###.### ms ");
			this.formatter.setGroupingUsed(true);
		}
		return this.formatter;
	}
}
