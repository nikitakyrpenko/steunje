/*
 * @(#)$Id: FtpCommand.java,v 1.0, 2007-02-18 11:20:25Z, Alexander Serbin$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.ftp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;

/**
 * 
 * @TODOO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 1$
 *
 */
public abstract class FtpCommand {

	FTPClient ftpClient;
	Map<String, Object> resultMap = new HashMap<String, Object>() ;
	
	abstract public void execute() throws IOException; 

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

	protected FTPClient getFtpClient() {
		return ftpClient;
	}
	
	protected void setResult(String parameter, Object result) {
		resultMap.put(parameter, result);
	}
	
	public Object getResult(String parameter){
		return resultMap.get(parameter);
	}
}

