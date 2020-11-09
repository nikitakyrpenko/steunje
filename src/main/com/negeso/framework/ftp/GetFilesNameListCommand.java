/*
 * @(#)$Id: GetFilesNameListCommand.java,v 1.1, 2007-03-13 11:53:10Z, Anatoliy Pererva$
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

import org.apache.log4j.Logger;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */

public class GetFilesNameListCommand extends FtpCommand {

	private static Logger logger = Logger.getLogger(GetFilesNameListCommand.class);

	private String remoteFolder;
	
	public final static String FILES_LIST = "filesList";
	
	@Override
	public void execute() throws IOException {
		logger.debug("+");
//		FTPListParseEngine engine = getFtpClient().initiateListParsing(
//				"org.apache.commons.net.ftp.parser.NTFTPEntryParser",
//				//"org.apache.commons.net.ftp.parser.UnixFTPEntryParser",
//				remoteFolder);
//		FTPFile[] files = null;
//		if (engine.hasNext()) {
//			files = engine.getNext(25);
//		}
//		setResult(String.valueOf(FILES_LIST), files);
		getFtpClient().changeWorkingDirectory(remoteFolder);
		setResult(String.valueOf(FILES_LIST) , getFtpClient().listFiles());
		logger.debug("-");
	}

	public void setRemoteFolder(String remoteFolder) {
		this.remoteFolder = remoteFolder;
	}

}

