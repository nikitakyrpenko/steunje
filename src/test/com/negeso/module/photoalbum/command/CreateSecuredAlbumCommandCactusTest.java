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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.transform.TransformerException;

import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.negeso.CommandTestCase;
import com.negeso.PreparedDatabaseLauncher;
import com.negeso.XMLtoHardDiskWriter;
import com.negeso.framework.Env;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.menu.command.CreateMenuItemDialogCommand;
import com.negeso.framework.site.Site;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * 
 * 
 * @author		Vadim V. Mishchenko
 * @version		$Revision: $
 *
 */
public class CreateSecuredAlbumCommandCactusTest extends CommandTestCase {
	//TODO
	public CreateSecuredAlbumCommandCactusTest() {
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
		command = new CreateSecuredAlbumCommand();
		command.setRequestContext(requestContext);
		createFolders();			
	}

	public void testUserPermission(){
		requestContext.getSession().setUserId(3);
		ResponseContext responseContext = command.execute();
		assertEquals("denied"+"Forbidden", responseContext.getResultName());
	}

	public void beginCreateSecuredAlbumIdIdIsNull(WebRequest request){
        request.addParameter(CreateSecuredAlbumCommand.INPUT_ALBUM_NAME,"albumName");
        request.addParameter(CreateSecuredAlbumCommand.INPUT_USER_NAME,"user");
        request.addParameter(CreateSecuredAlbumCommand.INPUT_LOGIN,"login");
        request.addParameter(CreateSecuredAlbumCommand.INPUT_PASSWORD,"*****");
	}
	
	public void testCreateSecuredAlbumIdIdIsNull() {
		ResponseContext responseContext = command.execute();
		Document document = resultXML(responseContext);	
//		XMLtoHardDiskWriter.writeXMLtoHardDisk(document, "d:/"+"create-secured-album-command.xml");
		try {
			validateXML(document, "create-secured-album-command.xml");
		} catch (Exception e) {
			fail ("Exception ocurred: "+e.getMessage());
		}
		assertEquals(CreateSecuredAlbumCommand.RESULT_CREATE,responseContext.getResultName());
		Env.getSite().getProperties().setProperty("photo-album.secured", "false");
	}

	public void beginCreateSecuredAlbum(WebRequest request){
        request.addParameter(CreateSecuredAlbumCommand.INPUT_ID,"39");
		request.addParameter(CreateSecuredAlbumCommand.INPUT_ALBUM_NAME,"ZZZ_Album");
        request.addParameter(CreateSecuredAlbumCommand.INPUT_USER_NAME,"ZZZ_user");
        request.addParameter(CreateSecuredAlbumCommand.INPUT_LOGIN,"ZZZ_login");
        request.addParameter(CreateSecuredAlbumCommand.INPUT_PASSWORD,"ZZZ_pasw");
	}
	
	public void testCreateSecuredAlbum() {
		Env.getSite().getProperties().setProperty("photo-album.secured", "true");
		ResponseContext responseContext = command.execute();
		Document document = resultXML(responseContext);
		XMLtoHardDiskWriter.writeXMLtoHardDisk(document, "d:/"+"create-secured-album-command-2.xml");
		try {
			XMLAssert.assertXpathEvaluatesTo("ZZZ_Album","/*[local-name()='photo_album']/*[local-name()='album']/*[local-name()='album']/@name", document);			
		} catch (XpathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		validateDB();
		assertEquals(command.RESULT_SUCCESS,responseContext.getResultName());
		Env.getSite().getProperties().setProperty("photo-album.secured", "false");
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
	private void validateDB () {
		String query =  "DELETE FROM photo_album WHERE photo_album.name ='ZZZ_Album';" +
						"DELETE FROM article WHERE article.text = 'ZZZ_Album';" +
						"DELETE FROM mc_folder WHERE id = (SELECT MAX(id)FROM mc_folder);" +
						"DELETE FROM members WHERE id = (SELECT MAX(id)FROM members);" +
						"DELETE FROM containers WHERE containers.name = 'ZZZ_Album';" +
						"DELETE FROM groups WHERE groups.name = 'ZZZ_Album_visitors';" +
						"DELETE FROM user_list WHERE user_list.username = 'ZZZ_user';";
		try {
			PDL.runTest(query);
		} catch (Exception e) {
			fail("Unexpected exception "+e.getMessage());			
		}

		
	}
}
