/*
 * @(#)$Id: FTPService.java,v 1.1, 2007-03-13 11:52:51Z, Anatoliy Pererva$
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
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;

/**
 * 
 * @TODO
 * 
 * @author		Alex Serbin
 * @version		$Revision: 2$
 *
 */
public class FTPService {
	
	private static Logger logger = Logger.getLogger(FTPService.class);
	
	private FTPClient ftpClient;

	private String serverIP;

	private String login;

	private String password;
	
	public FTPService() {
		ftpClient = new FTPClient();
		// ftpClient.addProtocolCommandListener(new LogCommandListener());
		serverIP = Env.getProperty("ftp.server");
		login = Env.getProperty("ftp.login");
		password = Env.getProperty("ftp.password");
		if (logger.isInfoEnabled()) {
			logger.info("ftp connection settings: ip = " + serverIP
					+ "; login =  " + login + "; password = " + password);
		}
	}
	
	public void executeCommand(FtpCommand ftpCommand) throws InterruptedException {
		int attemptCount = 0;
		while (true) {
			try {
				executeCommandInternal(ftpCommand);
				return;
			}
			catch (Throwable t) {
				if (attemptCount++ > Env.getIntProperty("ftp.max_attempts.count", 5)) {
					throw new RuntimeException("FTP is unavailable. Attempts count is " + attemptCount); 
				}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						throw e1;
					}
			}
		}
				
	}
	
	private void executeCommandInternal(FtpCommand ftpCommand) throws SocketException, IOException {
		try {
			logger.debug("connecting to " + serverIP + " server...");
			ftpClient.connect(serverIP);
			checkConnectionSuccessful();
			logger.debug("logging...");
			if (!ftpClient.login(login, password)) {
				ftpClient.disconnect();
				throw new IOException("Wrong login/password. Reply string from server: "
						+ ftpClient.getReplyString()
						+ ", FTP reply code="
						+ ftpClient.getReplyCode());
			}
			logger.debug("execute command....");
			ftpClient.enterLocalPassiveMode();
			ftpCommand.setFtpClient(ftpClient);
			ftpCommand.execute();
			logger.debug("logout....");
			if (!ftpClient.logout()) {
				logger
						.error("Failed to successfully logout from ftp server. Reply code: "
								+ ftpClient.getReplyCode());
			}
		} finally {
			if (ftpClient.isConnected())
				try {
					logger.debug("disconnect in finally block");
					ftpClient.disconnect();
				} catch (IOException e) {
					logger.error("Problem disconnecting from FTP server ");
				}
		}		
	}

	private void checkConnectionSuccessful() throws IOException {
		if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			ftpClient.disconnect();
			throw new IOException("FTP server refused connection. Reply string from server: "
					+ ftpClient.getReplyString()
					+ ", FTP reply code="
					+ ftpClient.getReplyCode());
		}
		logger.debug("Connected. Reply String: " + ftpClient.getReplyString());
	}

	public void changeWorkingDirectory(String remotePath) throws IOException {
		logger.debug("+");
		if (!ftpClient.changeWorkingDirectory(remotePath)) {
			throw new IOException("Failed to change working directory. Reply code: "
					+ ftpClient.getReplyCode());
		}
		logger.debug("-");
	}
	
}

