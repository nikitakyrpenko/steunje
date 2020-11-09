/*
 * @(#)$Id: $
 *
 * Copyright (c) 2008 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.web.controller;



import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import org.apache.cactus.WebRequest;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.negeso.BaseControllerTestCase;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.User;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.bo.ImportSubscribersForm;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriberAttributeValue;
import com.negeso.module.newsletter.service.SubscriberService;

/**
 * 
 * @TODO
 * 
 * @author Andriy Zagorylko
 * @version $Revision: $
 * 
 */

public class ImportControllerCactusTest extends BaseControllerTestCase {
	
	
	private final String EMAIL = "EMAIL";
	private final String FULL_NAME = "FULL_NAME";
	private final String LAST_NAME = "LAST_NAME";
	private final String TITLE = "TITLE";
	private final String FIRST_NAME = "FIRST_NAME";
	private final String MIDDLE_NAME = "MIDDLE_NAME";
	private final String SUFFIX = "SUFFIX";
	private final String COMPANY_NAME = "COMPANY_NAME";
	private final String ADDRESS = "ADDRESS";
	private final String POSTAL_CODE = "POSTAL_CODE";
	private final String TOWN = "TOWN";
	private final String COUNTRY = "COUNTRY";
	
	private final String singleEmail = "single@noemail.ua";
	List<Subscriber> testSubscribers ;
	
	private ImportController importController = null;
	private SubscriberService subscriberService = null;
	
	

	protected void setUp() throws Exception {
	        // needed to initialize a user
	        super.setUp();
	        importController = (ImportController) dc.getBean("newsletter_module", "importController");
	        subscriberService = (SubscriberService) dc.getBean("newsletter_module", "subscriberService");
	        testSubscribers = getTestSubscribers();
	    }
	
	protected void tearDown() {
		for (Subscriber localSubscriber : testSubscribers) {
			Subscriber subscriberFromDb = subscriberService.findByEmail(getAttribute(localSubscriber, EMAIL));
			if (subscriberFromDb != null)
				subscriberService.delete(subscriberFromDb);
		}
		
		Subscriber subscriberFromDb = subscriberService.findByEmail(singleEmail);
		if (subscriberFromDb != null)
				subscriberService.delete(subscriberFromDb);
		

	}
	
	public void beginImportSubscribers(WebRequest request){
			 prepareRequest(request, "add");
	    }
	 
	public void beginOverwriteSubscribers(WebRequest request){
			 prepareRequest(request, "overwrite");
		}
	 
	public void beginSingleSubscriber(WebRequest request){
			 prepareRequest(request, "single");
		}
	 
	private void prepareRequest(WebRequest request, String upload_method) {

		String delimiter = ";";
		String group_1 = "group_1";

		//request.setContentType("multipart/form-data");
		request.addParameter("upload_method", upload_method,WebRequest.POST_METHOD);
		request.addParameter("group_1", group_1, WebRequest.POST_METHOD);
		request.addParameter("delimiter", delimiter, WebRequest.POST_METHOD);
		request.addParameter("singleEmail", singleEmail,WebRequest.POST_METHOD);

	}
	 
	public void testImportSubscribers() throws Exception {
		
		String delimiter = ";";
		String singleEmail = "";
		
		String fileData = subscribersAttrToString(testSubscribers);
		request.getSession().setAttribute(SessionData.USER_ATTR_NAME, new User());
		
		ImportSubscribersForm form = new ImportSubscribersForm();
		form.setDelimiter(delimiter);
		form.setSingleEmail(singleEmail);
		form.setImporterId("importer");
		form.setLangId(Language.getDefaultLanguage().getId());
		form.setImportFile(new MultipartFileStub(fileData,"subscribers.csv" ));
			
		importController.onSubmit(request, response,form, null);
		
		for (Subscriber localSubscriber:testSubscribers){
			Subscriber subscriberFromDb = subscriberService.findByEmail(getAttribute(localSubscriber, EMAIL));
			assertTrue(compareSubscribers(localSubscriber,subscriberFromDb));
			
		}
				
	}
	
	
	public void testOverwriteSubscribers() throws Exception {
		
		String delimiter = ";";
		String singleEmail = "";
		
		String fileData = subscribersAttrToString(testSubscribers);
		request.getSession().setAttribute(SessionData.USER_ATTR_NAME, new User());
		
		ImportSubscribersForm form = new ImportSubscribersForm();
		form.setDelimiter(delimiter);
		form.setSingleEmail(singleEmail);
		form.setImporterId("importer");
		form.setLangId(Language.getDefaultLanguage().getId());
		form.setImportFile(new MultipartFileStub(fileData,"subscribers.txt" ));
			
		importController.onSubmit(request, response,form, null);
		
		for (Subscriber localSubscriber:testSubscribers){
			Subscriber subscriberFromDb = subscriberService.findByEmail(getAttribute(localSubscriber, EMAIL));
			assertTrue(compareSubscribers(localSubscriber,subscriberFromDb));
			
		}
		
		for(Subscriber s: testSubscribers){
			s.addAttribute(TITLE, "new title");
		}
		fileData = subscribersAttrToString(testSubscribers);
		form.setImportFile(new MultipartFileStub(fileData,"subscribers.txt" ));
		
		importController.onSubmit(request, response,form, null);
		
		for (Subscriber localSubscriber:testSubscribers){
			Subscriber subscriberFromDb = subscriberService.findByEmail(getAttribute(localSubscriber, EMAIL));
			assertTrue(compareSubscribers(localSubscriber,subscriberFromDb));
			
		}
		
				
	}

	public void testSingleSubscriber() throws Exception {
	
		String delimiter = ";";
		request.getSession().setAttribute(SessionData.USER_ATTR_NAME, new User());
		
		ImportSubscribersForm form = new ImportSubscribersForm();
		form.setDelimiter(delimiter);
		form.setSingleEmail(singleEmail);
		form.setImporterId("importer");
		form.setLangId(Language.getDefaultLanguage().getId());
					
		importController.onSubmit(request, response,form, null);
		
		Subscriber subscriberFromDb = subscriberService.findByEmail(singleEmail);
		assertTrue(subscriberFromDb!=null);
		
	
			
}
			
	private List<Subscriber> getTestSubscribers(){
		
		List<Subscriber> subscribers =  new ArrayList<Subscriber>();
		subscribers.add(createTestSubscriber("ivanov"));
		subscribers.add(createTestSubscriber("petrov"));
		subscribers.add(createTestSubscriber("sidorov"));
		
		return subscribers;
		
	}
		
	
	private Subscriber createTestSubscriber(String lastName){
		
		Subscriber subscriber = new Subscriber();
		subscriber.addAttribute(EMAIL, lastName +"@noemail.ua");
		subscriber.addAttribute(FULL_NAME, lastName +"_FULL_NAME");
		subscriber.addAttribute(LAST_NAME, lastName +"_LAST_NAME");
		subscriber.addAttribute(TITLE, lastName +"_TITLE");
		subscriber.addAttribute(FIRST_NAME, lastName +"_FIRST_NAME");
		subscriber.addAttribute(MIDDLE_NAME, lastName +"_MIDDLE_NAME");
		subscriber.addAttribute(SUFFIX, lastName +"_SUFFIX");
		subscriber.addAttribute(COMPANY_NAME, lastName +"_COMPANY_NAME");
		subscriber.addAttribute(ADDRESS, lastName +"_ADDRESS");
		subscriber.addAttribute(POSTAL_CODE, lastName +"_POSTAL_CODE");
		subscriber.addAttribute(TOWN, lastName +"_TOWN");
		subscriber.addAttribute(COUNTRY, lastName +"_COUNTRY");
		
		
		return  subscriber;
	}
	
	private String subscribersAttrToString(List<Subscriber> subscribers){
		String separator= " ; ";
		String header = "EMAIL ; FULL_NAME ; LAST_NAME ; TITLE ; FIRST_NAME ; MIDDLE_NAME ; SUFFIX ; COMPANY_NAME ; ADDRESS ; POSTAL_CODE ; TOWN ; COUNTRY \n\n";
		String result = header;
		for (Subscriber subscriber:subscribers){
			result+= getAttribute(subscriber,EMAIL) + separator;
			result+= getAttribute(subscriber,FULL_NAME) + separator;
			result+= getAttribute(subscriber,LAST_NAME) + separator;
			result+= getAttribute(subscriber,TITLE) + separator;
			result+= getAttribute(subscriber,FIRST_NAME) + separator;
			result+= getAttribute(subscriber,MIDDLE_NAME) + separator;
			result+= getAttribute(subscriber,SUFFIX) + separator;
			result+= getAttribute(subscriber,COMPANY_NAME) + separator;
			result+= getAttribute(subscriber,ADDRESS) + separator;
			result+= getAttribute(subscriber,POSTAL_CODE) + separator;
			result+= getAttribute(subscriber,TOWN) + separator;
			result+= getAttribute(subscriber,COUNTRY) + " \n";
		}
		return result;
	}
	
	private String getAttribute(Subscriber subscriber, String key){
		Set<SubscriberAttributeValue> values = subscriber.getAttributes();
		for (SubscriberAttributeValue attrValue: values){
			if (attrValue.getSubscriberAttributeType().getKey().equals(key)){
				return attrValue.getValue();
			}
		}
		return "";
	}
	
	
	private boolean compareSubscribers(Subscriber s1, Subscriber s2){
		if (s1 == null || s2 == null) return false;
		if (!getAttribute(s1,EMAIL).equals(getAttribute(s2, EMAIL))) return false;
		if (!getAttribute(s1,FULL_NAME).equals(getAttribute(s2, FULL_NAME))) return false;
		if (!getAttribute(s1,LAST_NAME).equals(getAttribute(s2, LAST_NAME))) return false;
		if (!getAttribute(s1,TITLE).equals(getAttribute(s2, TITLE))) return false;
		if (!getAttribute(s1,FIRST_NAME).equals(getAttribute(s2, FIRST_NAME))) return false;
		if (!getAttribute(s1,MIDDLE_NAME).equals(getAttribute(s2, MIDDLE_NAME))) return false;
		if (!getAttribute(s1,SUFFIX).equals(getAttribute(s2, SUFFIX))) return false;
		if (!getAttribute(s1,COMPANY_NAME).equals(getAttribute(s2, COMPANY_NAME))) return false;
		if (!getAttribute(s1,ADDRESS).equals(getAttribute(s2, ADDRESS))) return false;
		if (!getAttribute(s1,POSTAL_CODE).equals(getAttribute(s2, POSTAL_CODE))) return false;
		if (!getAttribute(s1,TOWN).equals(getAttribute(s2, TOWN))) return false;
		if (!getAttribute(s1,COUNTRY).equals(getAttribute(s2, COUNTRY))) return false;
		
		return true;
	}
}
