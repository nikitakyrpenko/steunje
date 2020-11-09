/*
 * @(#)$Id: TransferFileCommand.java,v 1.2, 2007-02-21 12:48:54Z, Anatoliy Pererva$
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 3$
 *
 */
public class TransferFileCommand extends FtpCommand {

    private String remoteFileName;
    private String remoteFolder;
    private File localFile;
    private int fileType = FTP.BINARY_FILE_TYPE;
	
	@Override
	public void execute() throws IOException {
		OutputStream outStream = null;
		try {
			getFtpClient().changeWorkingDirectory(remoteFolder);
			getFtpClient().enterLocalPassiveMode();
			getFtpClient().setFileType(fileType);
		    outStream = new FileOutputStream(localFile);
		    getFtpClient().retrieveFile( remoteFileName, outStream );
		} finally {
		    IOUtils.closeQuietly( outStream );
		}
	}

	public void setLocalFile(File localFile) {
		this.localFile = localFile;
	}

	public void setRemoteFileName(String remoteFileName) {
		this.remoteFileName = remoteFileName;
	}

	public void setRemoteFolder(String remoteFolder) {
		this.remoteFolder = remoteFolder;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

}

