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

import com.negeso.framework.domain.*;
import javax.xml.transform.TransformerException;
import org.apache.cactus.WebRequest;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import com.negeso.*;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.menu.command.CreateMenuItemDialogCommand;
import com.negeso.framework.util.FileUtils;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.Folder;

/**
 * 
 * @TODO
 * 
 * @author		Vadim Mishchenko
 * @version		$Revision: $
 *
 */

public class AddPhotosCommandCactusTest extends CommandTestCase {

	private PreparedDatabaseLauncher PDL = new PreparedDatabaseLauncher() {
		@Override
		protected void assertTest() throws Exception {
		}
	}; 
	
	public AddPhotosCommandCactusTest() {
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new AddPhotosCommand();
		command.setRequestContext(requestContext);
		createFolders();
	}
	
	public void testUserPermission(){
		requestContext.getSession().setUserId(3);
		ResponseContext responseContext = command.execute();
		assertEquals("denied"+"Forbidden", responseContext.getResultName());
	}
	
	public void testForSuccessGetAllPhotoAlbums(){
		ResponseContext responseContext = command.execute();
		Document document = resultXML(responseContext);		

		try {
			validateXML(document, "get-foto-album-1.xml");
		} catch (Exception e) {
			fail("Exception while validating xml_1 :"+e.getMessage());
		}
		assertEquals(command.RESULT_SUCCESS,responseContext.getResultName());		
	}

	public void beginForSuccessGetPhotoAlbum(WebRequest request) {
        request.addParameter(AddPhotosCommand.INPUT_ALBUM_ID,"40");
	}
	
	public void testForSuccessGetPhotoAlbum(){			
		User user = new User();
		user.setId(1L);
		SessionData sessionData = requestContext.getSessionData();
		sessionData.setAttribute(SessionData.USER_ATTR_NAME, user);
		command.setRequestContext(requestContext);
		ResponseContext responseContext = command.execute();
		Document document = resultXML(responseContext);
		try {
			validateXML(document, "get-foto-album-2.xml");
		} catch (Exception e) {
			fail("Exception while validating xml_2 :"+e.getMessage()+"XML FILE :"+TestUtil.parseToString(document));
		}
		assertEquals(command.RESULT_SUCCESS,responseContext.getResultName());
	}
	
	public void beginForSuccessAddFoto(WebRequest request) {
		request.addParameter(AddPhotosCommand.INPUT_ALBUM_ID,"39");
        request.addParameter(AddPhotosCommand.INPUT_NAME,"media/photo_album/album_2/vvv.jpeg");
        request.addParameter(AddPhotosCommand.INPUT_THUMBNAIL_NAME,"media/photo_album/album_2/vvv.jpeg");
        request.addParameter(AddPhotosCommand.INPUT_WIDTH,"48") ;
        request.addParameter(AddPhotosCommand.INPUT_HEIGHT,"48");
        request.addParameter(AddPhotosCommand.INPUT_THUMBNAIL_WIDTH,"48");
        request.addParameter(AddPhotosCommand.INPUT_THUMBNAIL_HEIGHT,"48");
	}
	
	public void testForSuccessAddFoto(){
		command.setRequestContext(requestContext);
		ResponseContext responseContext = command.execute();
		String commonQuery = "DELETE FROM image WHERE image.image_id IN ";
		String query1 = commonQuery + "(SELECT photo_album.image_id FROM photo_album WHERE photo_album.name = 'vvv.jpeg')";
		String query2 = commonQuery + "(SELECT photo_album.thumbnail_id FROM photo_album WHERE photo_album.name = 'vvv.jpeg')";
		
		try {
			PDL.runTest(query1);
			PDL.runTest(query2);
		} catch (Exception e) {
			fail("Unexpected exception");
		}
		
		Document document = resultXML(responseContext);
		try {
			XMLAssert.assertXpathEvaluatesTo("vvv.jpeg","/*[local-name()='photo_album']/*[local-name()='album']/*[local-name()='photo'][4]/@name", document);
		} catch (XpathException e) {
			// TODO Auto-generated catch block
			fail("Assert fail because of "+e.getMessage());
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
