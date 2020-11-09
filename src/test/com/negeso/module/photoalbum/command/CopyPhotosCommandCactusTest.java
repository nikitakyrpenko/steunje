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

import java.io.File;
import java.io.IOException;
import org.apache.cactus.WebRequest;
import org.w3c.dom.Document;
import com.negeso.CommandTestCase;
import com.negeso.PreparedDatabaseLauncher;
import com.negeso.XMLtoHardDiskWriter;
import com.negeso.framework.controller.ResponseContext;
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
public class CopyPhotosCommandCactusTest extends CommandTestCase {
	public CopyPhotosCommandCactusTest() {
		super();
	}
	
	private PreparedDatabaseLauncher PDL = new PreparedDatabaseLauncher() {
		@Override
		protected void assertTest() throws Exception {
		}
	};
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new CopyPhotosCommand();
		command.setRequestContext(requestContext);
		createFoldersAndFile();
	}
	
	public void testUserPermission(){
		requestContext.getSession().setUserId(3);
		ResponseContext responseContext = command.execute();
		assertEquals("denied"+"Forbidden", responseContext.getResultName());
	}

	public void beginCopyPhotosCommand(WebRequest request){
        request.addParameter(CopyPhotosCommand.INPUT_MOVE,"true");			 //move
        request.addParameter(CopyPhotosCommand.INPUT_ALBUM_ID,"39");	     //media/photo_album/album_2/
        request.addParameter(CopyPhotosCommand.INPUT_TARGET_ALBUM_ID,"40");  //media/photo_album/album_3/
        request.addParameter(CopyPhotosCommand.INPUT_IDS,"44");   			 // logo_1.jpg
	}
	
	public void testCopyPhotosCommand() {
		ResponseContext responseContext = command.execute();
		command.getRequestContext().setParameter(CopyPhotosCommand.INPUT_ALBUM_ID,"40");
		command.getRequestContext().setParameter(CopyPhotosCommand.INPUT_TARGET_ALBUM_ID,"39");
		command.execute();
		Document document = resultXML(responseContext);	
//		XMLtoHardDiskWriter.writeXMLtoHardDisk(document, "d:/copy-photos-command.xml");
		try {
			validateXML(document, "copy-photos-command.xml");
		} catch (Exception e) {
			fail("Exception while validating xml :"+e.getMessage());
		}		
		assertEquals(command.RESULT_SUCCESS,responseContext.getResultName());		
	}

	
	public void beginCopyPhotosCommandMakeCopies(WebRequest request){
        request.addParameter(CopyPhotosCommand.INPUT_MOVE,"false");			 //copy
        request.addParameter(CopyPhotosCommand.INPUT_ALBUM_ID,"39");	     //media/photo_album/album_2/
        request.addParameter(CopyPhotosCommand.INPUT_TARGET_ALBUM_ID,"40");  //media/photo_album/album_3/
        request.addParameter(CopyPhotosCommand.INPUT_IDS,"44");   			 // logo_1.jpg
	}
	
	public void testCopyPhotosCommandMakeCopies() {
		ResponseContext responseContext = command.execute();
		String query = "DELETE FROM image WHERE image.image_id = (SELECT MAX(image.image_id)FROM image);" +
					   "DELETE FROM image WHERE image.image_id = (SELECT MAX(image.image_id)FROM image);";
		try {
			PDL.runTest(query);
		} catch (Exception e) {
			fail("Unexpected exception "+e.getMessage());			
		}		
		Document document = resultXML(responseContext);	
		XMLtoHardDiskWriter.writeXMLtoHardDisk(document, "d:/copy-photos-command-2.xml");
		try {
			validateXML(document, "copy-photos-command-2.xml");
		} catch (Exception e) {
			fail("Exception while validating xml :"+e.getMessage());
		}		
		assertEquals(command.RESULT_SUCCESS,responseContext.getResultName());		
	}

	private void createFoldersAndFile() {
		for (int i = 1; i < 5; i++) {
			Folder folder = Repository.get().createFolder(
					folder = Repository.get().getFolder("media/photo_album/"),
					"album_"+i);
			if (i == 2) {
				String realPath = Repository.get().getRealPath(folder.getCatalogPath());
				File file = new File(realPath+  "logo_1.jpg");
				File file2 = new File (realPath+ "logo_1_199x19.jpg");
				try {
					file.createNewFile();
					file2.createNewFile();
				} catch (IOException e) {
					fail("Unexpected IOException while creating file"+file.getAbsolutePath());
				}
			}			
		} 		
   	} 
	
	private Document resultXML(ResponseContext resContext){
		return (Document) resContext.getResultMap().get(CreateMenuItemDialogCommand.OUTPUT_XML) ;
	}

}
