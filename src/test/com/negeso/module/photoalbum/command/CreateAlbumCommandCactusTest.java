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
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
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
public class CreateAlbumCommandCactusTest extends CommandTestCase{
	
	private PreparedDatabaseLauncher PDL = new PreparedDatabaseLauncher() {
		@Override
		protected void assertTest() throws Exception {
		}
	};
	
	public CreateAlbumCommandCactusTest() {
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new CreateAlbumCommand();
		command.setRequestContext(requestContext);
		createFolders();			
	}
	
	public void testUserPermission(){
		requestContext.getSession().setUserId(3);
		ResponseContext responseContext = command.execute();
		assertEquals("denied"+"Forbidden", responseContext.getResultName());
	}

	public void beginTestCreateAlbum(WebRequest request){
        request.addParameter(CreateAlbumCommand.INPUT_ID,"");
	}
	
	public void testCreateAlbum() {
		ResponseContext responseContext = command.execute();
		Document document = resultXML(responseContext);	
//		XMLtoHardDiskWriter.writeXMLtoHardDisk(document, "d:/"+"create-album-command.xml");
		assertEquals(4, document.getChildNodes().item(0).getChildNodes().getLength()); // There should be 4 albums
		try {
			XMLAssert.assertXpathExists("/*[local-name()='photo_album']/*[local-name()='album']", document);			
		} catch (XpathException e) {
			// TODO Auto-generated catch block
			fail("Assert fail because of "+e.getMessage());
		}
		assertEquals(command.RESULT_SUCCESS,responseContext.getResultName());
		cleanDB("");		
	}

	public void beginCreateInnerAlbum(WebRequest request){
        request.addParameter(CreateAlbumCommand.INPUT_ID,"39");
	}
	
	public void testCreateInnerAlbum() {
		ResponseContext responseContext = command.execute();
		Document document = resultXML(responseContext);	
//		XMLtoHardDiskWriter.writeXMLtoHardDisk(document, "d:/"+"create-inn_album-command.xml");
		try {
			XMLAssert.assertXpathExists("/*[local-name()='photo_album']/*[local-name()='album']", document);
		} catch (XpathException e) {
			// TODO Auto-generated catch block
			fail("Assert fail because of "+e.getMessage());
		}
		assertEquals(command.RESULT_SUCCESS,responseContext.getResultName());
		cleanDB("39");
	}

	
	private void cleanDB (String albumId) {
		String query1 = "DELETE FROM photo_album WHERE id = (SELECT MAX(id)FROM photo_album);";
		String query2 = "DELETE FROM article WHERE id = (SELECT MAX(id)FROM article);";
		String query3 = "DELETE FROM mc_folder WHERE id = (SELECT MAX(id)FROM mc_folder);";
		try {
			PDL.runTest(query1);
			PDL.runTest(query2);
			PDL.runTest(query3);
		} catch (Exception e) {
			fail("Unexpected exception "+e.getMessage());			
		}
		GetPhotoAlbumCommand com = new GetPhotoAlbumCommand();
		requestContext.setParameter(GetPhotoAlbumCommand.INPUT_ID, albumId);
		com.setRequestContext(requestContext);
		ResponseContext responseContext = com.execute();
		Document document = resultXML(responseContext);
		try {
			if(!albumId.equals("")){
				XMLAssert.assertXpathExists("/*[local-name()='photo_album']/*[local-name()='album']", document);
			}else {
				validateXML(document, "get-foto-album-1.xml");
			}
		} catch (Exception e) {
			fail("Exception while validating xml_1 :"+e.getMessage());
		}
		assertEquals(command.RESULT_SUCCESS,responseContext.getResultName());		

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
