/*
 * @(#)$Id: TransferFileListCommand.java,v 1.3, 2007-03-13 12:05:07Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.ftp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.log4j.Logger;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 4$
 *
 */
public class TransferFileListCommand extends FtpCommand {

	Logger logger = Logger.getLogger(TransferFileListCommand.class);
	
	private List<File> fileList; 
    private String remoteFolder;
    private int fileType = FTP.ASCII_FILE_TYPE;
    
    public void setFileType(int fileType) {
    	this.fileType = fileType;
    }
    
	@Override
	public void execute() throws IOException {
		TransferFileCommand command = new TransferFileCommand();
		command.setRemoteFolder(remoteFolder);
		command.setFtpClient(getFtpClient());
		for (File file: fileList) {
			if (!file.exists()) {
				logger.error("download file: " + file);
			command.setRemoteFileName(file.getName());
			command.setLocalFile(file);
			command.setFileType(fileType);
			command.execute();
			}
		}
	}

	public void setRemoteFolder(String remoteFolder) {
		this.remoteFolder = remoteFolder;
	}

	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}

	public List<File> getFileList() {
		return fileList;
	}

	public String getRemoteFolder() {
		return remoteFolder;
	} 

}

