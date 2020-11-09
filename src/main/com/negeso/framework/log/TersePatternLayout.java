package com.negeso.framework.log;

import org.apache.log4j.PatternLayout;


/**
 * This layout is to log the exception message but not the stack trace.
 * 
 * @author Stanislav Demchenko
 */
public class TersePatternLayout extends PatternLayout {
	
	@Override
	public boolean ignoresThrowable() { return false; }

}
