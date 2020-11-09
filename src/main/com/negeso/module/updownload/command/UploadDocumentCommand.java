/*
 * @(#)UploadDocumentCommand.java       @version	27.04.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload.command;

import com.negeso.module.media_catalog.Configuration;

/**
 * TODO Class description goes here
 *
 * @version 	27.04.2004
 * @author 	OStrashko
 */
public class UploadDocumentCommand extends UploadFileCommand {

	protected String [] getAllowedExtensions(){
		return Configuration.documentTypeExtensions;
	}
}
