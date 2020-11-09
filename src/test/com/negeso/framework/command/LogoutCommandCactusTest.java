package com.negeso.framework.command;

import org.w3c.dom.Document;

import com.negeso.CommandTestCase;
import com.negeso.framework.controller.ResponseContext;

public class LogoutCommandCactusTest extends CommandTestCase {
	private Command command;
	
	public LogoutCommandCactusTest() {
		super();
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		command = new LogoutCommand();
		prepareRequestContext(1L);	
	}
	
	public void testLogout(){
		// user is admin not null
		assertNotNull(requestContext.getSession().getUser());
		ResponseContext responseContext = command.execute();
		assertNull(requestContext.getSession().getUser());
		assertEquals(Command.RESULT_SUCCESS, responseContext.getResultName());
		Document document = (Document) responseContext.getResultMap().get(
				AbstractCommand.OUTPUT_XML);
		
		assertNotNull(document);
	}
	
	private void prepareRequestContext(Long userId){
		requestContext.getSession().setLanguageCode("en");
		requestContext.getSession().getUser().setId(userId);		
		command.setRequestContext(requestContext);
		
	}

}
