/*
 * @(#)$Id: GetMediaFileCommandCactusTest.java,v 1.0, 2006-10-31 08:12:32Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.command;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;

import javax.xml.transform.TransformerException;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;
import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.command.Command;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.module.media_catalog.Repository;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 1$
 *
 */
public class GetMediaFileCommandCactusTest extends CommandTestCase {

	private Command command;
	
	public GetMediaFileCommandCactusTest() {
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new GetMediaFileCommand();
		command.setRequestContext(requestContext);
	}
	
	public void testWithEmptyParameters() throws TransformerException {
		ResponseContext responseContext = command.execute();
		assertNotNull(responseContext);
		assertEquals(responseContext.getResultName(), AbstractCommand.RESULT_FAILURE);
		Document document = (Document) responseContext.getResultMap().get(AbstractCommand.OUTPUT_XML);
		assertNull(document); // document is null - no xml is validated
		assertNull(responseContext.getResultMap().get("file"));
	}

	public void beginWithMissedFile(WebRequest request) throws TransformerException {
		request.addParameter("file", "media/wrongFile.txt");
	}
	
	public void testWithMissedFile() throws TransformerException {
		ResponseContext responseContext = command.execute();
		assertNotNull(responseContext);
		assertEquals(responseContext.getResultName(), AbstractCommand.RESULT_FAILURE);
		Document document = (Document) responseContext.getResultMap().get(AbstractCommand.OUTPUT_XML);
		assertNull(document); // document is null - no xml is validated
		assertNull(responseContext.getResultMap().get("file"));
	}

	public void beginWithCorrectFile(WebRequest request) {
		request.addParameter("file", "media/testFile.txt");
	}
	
	public void testWithCorrectFile() throws Exception {
		Writer writer = null;
		File file = null;
		try {
			// ensure that test file is prepared in media folder
			String filename = "testFile.txt";
			String realpath = Repository.get().getRealPath("/media");
			file = new File(realpath, filename);
			writer = new PrintWriter(file);
			// write some text for test
			writer.write("test message");
			writer.close();

			// perform test
			ResponseContext responseContext = command.execute();
			assertNotNull(responseContext);
			assertEquals(AbstractCommand.RESULT_SUCCESS, responseContext.getResultName());
			Document document = (Document) responseContext.getResultMap().get(AbstractCommand.OUTPUT_XML);
			assertNull(document); // document is null - no xml is validated
			
			assertEquals(command.getRequestContext().getParameter("is_media_file"), "true");
			assertEquals(responseContext.getResultMap().get("is-private"), "true");
			assertNotNull(responseContext.getResultMap().get("Last-Modified"));
			
			File downloadFile = (File) responseContext.getResultMap().get("file");
			assertNotNull(downloadFile);
			assertEquals(filename, downloadFile.getName());
			assertEquals(file.length(), downloadFile.length());
			
		} catch (Exception e) {
			throw e;
		} finally {
			// remove test file
			if (file != null && file.exists()) {
				try {
					file.delete();
				} catch (Exception e) {
					// do nothing 
				} 
			}
		}
	}
}
