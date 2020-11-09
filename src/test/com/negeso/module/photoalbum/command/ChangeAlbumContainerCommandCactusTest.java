/*
 * @(#)$Id: $
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.photoalbum.command;

import javax.xml.transform.TransformerException;

import org.apache.cactus.WebRequest;
import org.apache.cactus.WebResponse;
import org.custommonkey.xmlunit.XMLAssert;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.PreparedDatabaseLauncher;
import com.negeso.TestUtil;
import com.negeso.XMLtoHardDiskWriter;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.framework.menu.command.CreateMenuItemDialogCommand;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * 
 * 
 * @author		Vadim V. Mishchenko
 * @version		$Revision: $
 *
 */
public class ChangeAlbumContainerCommandCactusTest extends CommandTestCase{

	public ChangeAlbumContainerCommandCactusTest() {
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new ChangeAlbumContainerCommand();
		command.setRequestContext(requestContext);
		createFolders();
	}
	
	public void testUserPermission(){
		requestContext.getSession().setUserId(3);
		ResponseContext responseContext = command.execute();
		assertEquals("denied"+"Forbidden", responseContext.getResultName());
	}

	public void beginChangedAlbumContainer(WebRequest request){
        request.addParameter(ChangeAlbumContainerCommand.INPUT_ID,"40");
        request.addParameter(ChangeAlbumContainerCommand.INPUT_CONTAINER_ID,"2");
	}
	
	public void testChangedAlbumContainer() {
		ResponseContext responseContext = command.execute();
		changeBackContainerId();
		Document document = resultXML(responseContext);	
//		XMLtoHardDiskWriter.writeXMLtoHardDisk(document, "d:/"+"change-album-container-command.xml");
		try {
			validateXML(document, "change-album-container-command.xml");
		} catch (Exception e) {
			fail("Exception while validating xml :"+e.getMessage());
		}		
		assertEquals(command.RESULT_SUCCESS,responseContext.getResultName());		
	}
	
	private void changeBackContainerId() {
		requestContext = command.getRequestContext();
		requestContext.setParameter(ChangeAlbumContainerCommand.INPUT_CONTAINER_ID,"null");
		command.execute();
	}

	private void createFolders() {
		for (int i = 1; i < 5; i++) {
			Folder folder = Repository.get().createFolder(
					folder = Repository.get().getFolder("media/photo_album/"),
					"album_"+i);
		} 
   	} 
	
	private Document resultXML(ResponseContext resContext){
		return (Document) resContext.getResultMap().get(CreateMenuItemDialogCommand.OUTPUT_XML) ;
	}
}
