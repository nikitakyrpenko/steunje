/*
 * @(#)Id: SubscriberImporter.java, 27.02.2008 14:30:21, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.newsletter.service;

import com.negeso.SpringConstants;
import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.framework.log.SystemLogConstants;
import com.negeso.module.imp.extension.ImportException;
import com.negeso.module.imp.extension.ImportModule;
import com.negeso.module.imp.extension.ImportObject;
import com.negeso.module.imp.extension.Importer;
import com.negeso.module.imp.log.EventLogger;
import com.negeso.module.imp.service.IService;
import com.negeso.module.newsletter.Configuration;
import com.negeso.module.newsletter.NewsletterImportService;
import com.negeso.module.newsletter.bo.Subscriber;
import com.negeso.module.newsletter.bo.SubscriberAttributeType;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @TODO
 * @version Revision:
 * @author Dmitry Fedotov
 */
public class SubscriberImporter implements Importer {

    private static final Logger logger = Logger.getLogger(SubscriberImporter.class);

    private SubscriberService subscriberService;
    private List<Subscriber> subscribers;
    private List<String> requiredAttributes;
    private Map<String, Object> importParameters;

    private int importedSubscribersNumber = 0;

    public SubscriberImporter() {
        subscriberService = (SubscriberService) DispatchersContainer.
                getInstance().getBean(ModuleConstants.NEWSLETTER_MODULE,
                SpringConstants.NEWSLETTER_MODULE_SUBSCRIBER_SERVICE);


        List<SubscriberAttributeType> subscriberTypes = subscriberService.
                listRequiredSubscriberAttributesTypes();
        requiredAttributes = new ArrayList<String>(subscriberTypes.size());
        for (SubscriberAttributeType type : subscriberTypes) {
            requiredAttributes.add(type.getKey());
        }
    }

    public Map<String, List<ImportObject>> parseImportStream(InputStream is,
                                                             EventLogger eventLogger, Map<String, Object> params) throws ImportException {
        logger.debug("+");
        importParameters = params;
        //subscribers = subscriberService.listAllSubscribers();
        try {
            BufferedReader b = new BufferedReader(new InputStreamReader(is));
            Map<String, List<ImportObject>> rows = parse(b, eventLogger);

            logger.debug("-");
            return rows;
        }
        catch (IOException e) {
            logger.debug("- i/o import error: " + e.getMessage());
            throw new ImportException(e.getMessage());
        }
        catch (CriticalException e) {
            logger.debug("- import error: " + e.getMessage());
            throw new ImportException(e.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    private Map<String, List<ImportObject>> parse(BufferedReader b, EventLogger eventLogger)
            throws IOException, CriticalException {

        List<String> attributes = parseAttributes(b.readLine(), (String) importParameters.get(ImportModule.DELIMETER));

        checkRequiredAttributes(attributes);

        importedSubscribersNumber = subscriberService.countSubscribers();
        int maxSubscribersNumber = Configuration.getMaxSubscribersCount().intValue();
        List<ImportObject> importedSubscribers = new ArrayList<ImportObject>();

        Map<String, List<ImportObject>> map = new HashMap<String, List<ImportObject>>();
        String delimeter = (String) importParameters.get(ImportModule.DELIMETER);
        int lineNumber = 0;
        while (b.ready()) {
            lineNumber++;

            List<String> currLineParams = parseAttributes(b.readLine(), delimeter);

            if (lineNumber >= 1 && parametersValidation(currLineParams, attributes, eventLogger, lineNumber)) {

                try {
                    importedSubscribers.add(getImportedSubscriber(currLineParams,
                            attributes, eventLogger, lineNumber,
                            maxSubscribersNumber));
                } catch (CriticalException e) {
                    logger.error(e.getMessage(), e);
                    eventLogger.addEvent(SystemLogConstants.EVENT_ADD,
                            "newsletter", e.getMessage(),
                            SystemLogConstants.RESULT_ERROR, " ", " ");
                }
            }
        }
        map.put(Configuration.IMPORT_SUBSCRIBERS_KEY, importedSubscribers);
        return map;
    }

    private ImportObject getImportedSubscriber(List<String> parameters,
                                               List<String> attributes, EventLogger logger, int lineNumber,
                                               int maxSubscriberNumbers) {
        String resultType = SystemLogConstants.RESULT_OK;
        String eventType = SystemLogConstants.EVENT_UPDATE;
        String subscriberEmail = parameters.get(attributes.indexOf(Configuration.ATTRIBUTE_EMAIL));
        StringBuffer description = new StringBuffer();
        description.append(subscriberEmail);

        ImportObject imObject = new ImportObject();
        for (String paramName : attributes) {

            String value = parameters.get(attributes.indexOf(paramName));
            if (value == null || value.trim().equals("")) {
                resultType = SystemLogConstants.RESULT_WARNING;

                description
                        .append("(Line ")
                        .append(lineNumber).append(": ")
                        .append(paramName).append(" is empty)");

            }
            imObject.putProperty(paramName, value);
        }

        if (subscriberService.findByEmail(subscriberEmail) == null ||
                importParameters.get(Configuration.IMPORT_TYPE).
                        equals(Configuration.IMPORT_TYPE_DELETE_ADD)) {

            importedSubscribersNumber++;

            if (importedSubscribersNumber > maxSubscriberNumbers) {
                throw new CriticalException("max number of subscribers");
            }

            eventType = SystemLogConstants.EVENT_ADD;
        }


        logger.addEvent(eventType, "newsletter", description.toString(), resultType, eventType, "");

        return imObject;
    }

    private boolean parametersValidation(List<String> parameters,
                                         List<String> attributes, EventLogger logger, int lineNumber) {

        return checkRequiredParameters(parameters, attributes, logger, lineNumber) &&
                requiredParametersNotNull(parameters, attributes, logger, lineNumber) &&
                emailValidation(parameters, attributes, logger, lineNumber);
    }

    private boolean checkRequiredParameters(List<String> parameters,
                                            List<String> attributes, EventLogger logger, int lineNumber) {

        boolean result = true;
        if (parameters.size() != attributes.size()) {
            logger.addEvent("",
                    "newsletter", "Line " + lineNumber + ": wrong parameters number",
                    SystemLogConstants.RESULT_ERROR, "", "");

            result = false;
        }
        return result;
    }

    private boolean emailValidation(List<String> parameters,
                                    List<String> attributes, EventLogger logger, int lineNumber) {

        String email = parameters.get(attributes.indexOf(Configuration.ATTRIBUTE_EMAIL));
        boolean result = true;
        if (!Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$").matcher(email).matches()) {
            logger.addEvent("",
                    "newsletter", "Line " + lineNumber + ": wrong e-mail syntax",
                    SystemLogConstants.RESULT_ERROR, "", email);

            result = false;
        }
        return result;
    }

    private boolean requiredParametersNotNull(List<String> parameters,
                                              List<String> attributes, EventLogger logger, int lineNumber) {

        boolean result = true;
        for (Integer i : getRequiredIndexes(attributes)) {
            String paramName = parameters.get(i);
            if (paramName == null || paramName.trim().equals("")) {
                logger.addEvent("",
                        "newsletter",
                        "Line " + lineNumber + ": " + attributes.get(i) + " is empty",
                        SystemLogConstants.RESULT_ERROR, "", "");
                result = false;
            }
        }
        return result;
    }

    private List<Integer> getRequiredIndexes(List<String> parameters) {
        List<Integer> l = new ArrayList<Integer>();
        for (int i = 0; i < parameters.size(); i++) {
            if (requiredAttributes.contains(parameters.get(i))) {
                l.add(i);
            }
        }
        return l;
    }

    @SuppressWarnings("unchecked")
    private List parseAttributes(String line, String delimeter) {
        line = line.replaceAll(delimeter, " " + delimeter + " ").trim();
        List<String> l = new ArrayList<String>();
        String[] strings = line.split(delimeter);
        List<String> stringList = Arrays.asList(strings);
        for (String s : stringList) {
            l.add(s.trim());
        }
        if (line.trim().endsWith(delimeter))
            l.add("");
        return l;
    }

    private void checkRequiredAttributes(List<String> attributes) throws CriticalException {
        for (String type : requiredAttributes) {
            if (!attributes.contains(type))
                throw new CriticalException("Required attribute " +
                        type + " not found");
        }
        for (String attr : attributes) {
            if (attr == null || attr.trim().equals("")) {
                throw new CriticalException("Attribute with empty title");
            }
        }
    }

    public IService getImportService() {
        return new NewsletterImportService();
    }
}
