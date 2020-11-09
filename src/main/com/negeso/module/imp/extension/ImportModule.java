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
package com.negeso.module.imp.extension;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.log.SystemLogConstants;
import com.negeso.framework.module.engine.AbstractModule;
import com.negeso.framework.module.engine.ConfigurationElement;
import com.negeso.framework.module.engine.ModuleException;
import com.negeso.framework.util.Timer;
import com.negeso.framework.list.command.ModuleConstants;
import com.negeso.module.imp.log.EventLogger;

/**
 * 
 * @TODO
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: $
 *
 */
public class ImportModule extends AbstractModule {

    private static Logger logger = Logger.getLogger(ImportModule.class);
	
	private static final String EXTENSION_POINT = "import"; //$NON-NLS-1$
	
	private static final String IMPORTER_TAG = "importer";				//$NON-NLS-1$
	private static final String ID_ATTRIBUTE = "id";					//$NON-NLS-1$
	private static final String NAME_ATTRIBUTE = "name";				//$NON-NLS-1$
	private static final String CLASS_ATTRIBUTE = "class";   			//$NON-NLS-1$

	public static final String PRODUCT_MODULE_ID = "product"; 			//$NON-NLS-1$
	public static final String JOB_MODULE_ID = "job"; 					//$NON-NLS-1$
	public static final String CONTACTBOOK_MODULE_ID = "contactbook"; 	//$NON-NLS-1$
	public static final String NEWSLETTER_MODULE_ID = "newsletter_module"; 	//$NON-NLS-1$
	
	public static final String DELIMETER = "delimeter";
	public static final String MODULE_ID = "module_id";
	
	private boolean isRunning = false;
	private EventLogger eventLogger = null;
	
	private static final List<String> RESOLVED_MODULE_IDS = Arrays.asList(
            PRODUCT_MODULE_ID,
            JOB_MODULE_ID,
            CONTACTBOOK_MODULE_ID,
            NEWSLETTER_MODULE_ID);
	
	private Map<String, ImportConfiguration> importConfigurations = new LinkedHashMap<String, ImportConfiguration>();  
	
	private static ImportModule instance;
	
	private ImportModule() {
		startup();
	}

	public synchronized static ImportModule getModule() {
		if (instance == null) {
			instance = new ImportModule(); 
		}
		return instance;
	}
	
	@Override
	protected String getExtensionPoint() {
		return EXTENSION_POINT;
	}

	@Override
	protected void handleConfigurationElement(String moduleId,
			ConfigurationElement element) throws ModuleException {
		logger.debug("moduleId=" + moduleId);
		if (IMPORTER_TAG.equals(element.getName())) {
			handleImporter(moduleId, element);
		}
	}
	
	private void handleImporter(String moduleId, ConfigurationElement element) throws ModuleException {
		logger.debug("moduleId=" + moduleId);
		if (!RESOLVED_MODULE_IDS.contains(moduleId))
			throw new ModuleException("import isn't supported for module '" + moduleId + "'");
		
		logger.debug("class=" + element.getAttribute(CLASS_ATTRIBUTE));
		Object o = element.createExecutableExtension(CLASS_ATTRIBUTE);
		if (!(o instanceof Importer))
			throw new ModuleException(
					String.format("callback class '%s' is not an Importer",
							o.getClass().getName()));

		String importerId = element.getAttribute(ID_ATTRIBUTE);
		if (importerId == null)
			throw new ModuleException("Missed importer id");
		if (importConfigurations.containsKey(importerId))	
			throw new ModuleException("Duplicated importer id");
		
		String importerName = element.getAttribute(NAME_ATTRIBUTE);
		
		Importer importer = (Importer) o;
		ImportConfiguration ic = new ImportConfiguration();
		ic.setImporter(importer);
		ic.setDescription(new ImportDescription(importerId, importerName, moduleId));
		importConfigurations.put(importerId, ic);
	}

	public void doImport(InputStream stream, String importerId, EventLogger eventLogger) throws ImportException {
		logger.debug(String.format("+stream=%s, importerId=%s, eventLogger=%s",
				stream, importerId, eventLogger));
		setEventLogger(eventLogger);
		Timer t = new Timer();
		
		checkInputStreamNotNull(stream, importerId, eventLogger);
		ImportConfiguration ic = importConfigurations.get(importerId);
		checkImportConfigurationNotNull(importerId, eventLogger, ic);	
		
		// parse import document and fill data model
		Importer importer = ic.getImporter();

		// parse import file and obtain map of entries
		Map<String, List<ImportObject>> rows = importer.parseImportStream(stream, eventLogger, ic.getAttributes()); 
		logger.error("parse time=" + t.stop());
		t.start();
        String moduleId = ic.getDescription().getModuleId();
        if (ModuleConstants.NEWSLETTER_MODULE.equals(moduleId)) {
            importer.getImportService().doImport(null, rows, ic.getAttributes(), eventLogger, moduleId, importerId);
        } else {
            Connection conn = null;
            try {
                conn = DBHelper.getConnection();
                conn.setAutoCommit(false);

                importer.getImportService().doImport(conn, rows, ic.getAttributes(), eventLogger, moduleId, importerId);

                // commit transaction
                conn.commit();
                logger.info("COMMIT");
            } catch (Exception e) {
                logger.info("ROLLBACK");
                addMessage(eventLogger, importerId, e.getMessage());
                DBHelper.rollback(conn);
                throw new ImportException(e);
            } finally {
                DBHelper.close(conn);
            }
        }

        logger.error("import time=" + t.stop());
		logger.debug("-");
	}

	private void checkImportConfigurationNotNull(String importerId, EventLogger eventLogger, ImportConfiguration ic) throws ImportException {
		if (ic == null) {
			final String message = String.format("wrong importer id (id=%s)", importerId);
			addMessage(eventLogger, importerId, message);
			throw new ImportException(message);
		}
	}

	private void checkInputStreamNotNull(InputStream stream, String importerId, EventLogger eventLogger) throws ImportException {
		if (stream == null) {
			final String message = "import stream is null";
			addMessage(eventLogger, importerId, message);
			throw new ImportException(message);
		}
	}

	private void addMessage(EventLogger eventLogger, String importerId, String message) {
		eventLogger.addEvent(
				SystemLogConstants.EVENT_SYSTEM,
				importerId,
				message,
				SystemLogConstants.RESULT_FATAL,
				importerId,
				importerId);
	}
	
	public List<ImportDescription> getImportDescriptions() {
		List<ImportDescription> list = new LinkedList<ImportDescription>();
		for (ImportConfiguration ic : importConfigurations.values()) {
			list.add(ic.getDescription());
		}
		return list;
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}

	public ImportConfiguration getImportConfiguration(String importId){
		return importConfigurations.get(importId);
	}

    public void doSingleImport(String importerId, EventLogger eventLogger) throws ImportException {
        Timer t = new Timer();
        setEventLogger(eventLogger);
        ImportConfiguration ic = importConfigurations.get(importerId);
        checkImportConfigurationNotNull(importerId, eventLogger, ic);

        // parse import document and fill data model
        Importer importer = ic.getImporter();
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            conn.setAutoCommit(false);

            String moduleId = ic.getDescription().getModuleId();
            importer.getImportService().doImport(conn, null, ic.getAttributes(), eventLogger, moduleId, importerId);

            // commit transaction
            conn.commit();
            logger.info("COMMIT");
        } catch (Exception e) {
            logger.info("ROLLBACK");
            addMessage(eventLogger, importerId, e.getMessage());
            DBHelper.rollback(conn);
            throw new ImportException(e);
        } finally {
            DBHelper.close(conn);
        }

        logger.error("import time=" + t.stop());
        logger.debug("-");




    }

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public EventLogger getEventLogger() {
		return eventLogger;
	}

	public void setEventLogger(EventLogger eventLogger) {
		this.eventLogger = eventLogger;
	}

}
