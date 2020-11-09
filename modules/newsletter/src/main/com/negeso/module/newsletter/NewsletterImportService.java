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
package com.negeso.module.newsletter;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.negeso.SpringConstants;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.util.Timer;
import com.negeso.framework.log.SystemLogConstants;
import com.negeso.framework.domain.CriticalException;
import com.negeso.module.imp.extension.ImportObject;
import com.negeso.module.imp.log.EventLogger;
import com.negeso.module.imp.service.IService;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import com.negeso.module.newsletter.service.SubscriberService;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class NewsletterImportService implements IService {

	private static final Logger logger = Logger.getLogger(NewsletterImportService.class);

    private SubscriberService subscriberService = (SubscriberService) DispatchersContainer.
					getInstance().getBean(ModuleConstants.NEWSLETTER_MODULE,
					SpringConstants.NEWSLETTER_MODULE_SUBSCRIBER_SERVICE);


    public void doImport(Connection conn, Map<String, List<ImportObject>> data,
			Map<String, Object> params, EventLogger eventLogger, String moduleId, String importerId) {

		logger.debug("import");
		Timer t = new Timer();
        t.start();
        final String importType = (String) params.get(Configuration.IMPORT_TYPE);
        if (importType.equals(Configuration.IMPORT_TYPE_SINGLE)){
            addSingleEmail(params, eventLogger);
            return;
        }

        List<ImportObject> importObjects = data.get(Configuration.IMPORT_SUBSCRIBERS_KEY);

        List<Subscriber> existingSubscribers = subscriberService.listAllSubscribers();

		List<SubscriberAttributeType> subscriberAttributes = subscriberService.listSubscriberAttributesTypes();

        if (importType.equals(Configuration.IMPORT_TYPE_DELETE_ADD)){
			subscriberService.deleteSubscriberList(existingSubscribers);
			existingSubscribers.clear();
		}
		Subscriber s;
		int counter = 0;
        List<Subscriber> importedSubscribers = new ArrayList<Subscriber>(importObjects.size());
        for (ImportObject io : importObjects){
			s = subscriberService.findByEmail((String)io.getRow().get(Configuration.ATTRIBUTE_EMAIL));
			
			if (s == null){
				s = new Subscriber();
				s.setActivated(true);
			}
			s.setSubscriptionLangId((Long)params.get(Configuration.SUBSCRIPTION_LANG));
			subscriberService.updateImportedSubscriber(s, subscriberAttributes, io);
			importedSubscribers.add(s);
			
			if (counter++ % 40 == 0){
				try {
					Thread.sleep(1000);
					logger.debug("import pause");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
        subscriberService.updateGroups(importedSubscribers, (List<String>)params.get(Configuration.IMPORT_GROUPS));
        subscriberService.updateSubscriberList(importedSubscribers);
	
	}

	private void addSingleEmail(Map<String, Object> params, EventLogger eventLogger) {
        int count = subscriberService.countSubscribers();
        List<SubscriberAttributeType> subscriberAttributes = subscriberService.listSubscriberAttributesTypes();

        int maxSubscribersNumber = Configuration.getMaxSubscribersCount().intValue();

        if (count >= maxSubscribersNumber) {
            eventLogger.addEvent(SystemLogConstants.EVENT_ADD,
                    "newsletter", "max number of subscribers",
                    SystemLogConstants.RESULT_ERROR, " ", " ");

            return;
        }


        List<Subscriber> existingSubscribers = new ArrayList<Subscriber>();

        String subscriberEmail = (String) params.get(Configuration.ATTRIBUTE_EMAIL);
        Subscriber s = subscriberService.findByEmail(subscriberEmail);

        boolean isExist = s!=null;
        if (!isExist){
            s = new Subscriber();
            s.setActivated(true);
            s.setSubscriptionLangId((Long)params.get(Configuration.SUBSCRIPTION_LANG));
            subscriberService.addSubscriberAttribute(s, subscriberAttributes, Configuration.ATTRIBUTE_EMAIL, (String) params.get(Configuration.ATTRIBUTE_EMAIL));
        }else{
        	s.setAttribute(Configuration.ATTRIBUTE_EMAIL, (String) params.get(Configuration.ATTRIBUTE_EMAIL));
        }
        existingSubscribers.add(s);
        
        subscriberService.updateGroups(existingSubscribers, (List<String>)params.get(Configuration.IMPORT_GROUPS));
        subscriberService.updateSubscriberList(existingSubscribers);

        String eventType = (isExist) ? SystemLogConstants.EVENT_UPDATE : SystemLogConstants.EVENT_ADD;
        String resultType = SystemLogConstants.RESULT_OK;

        eventLogger.addEvent(eventType, "newsletter", subscriberEmail, resultType, eventType, "");
    }

}
