/*
 * @(#)$Id: ModuleEngine.java,v 1.12, 2007-01-31 11:11:55Z, Anatoliy Pererva$
 *
 * Copyright (c) 2006 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.module.engine;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

/**
 * 
 * Module management kernel. It parses module manifest files and represents
 * module data storage.
 * 
 * @author		Anatoliy Pererva
 * @version		$Revision: 13$
 *
 */
public class ModuleEngine {

    private static Logger logger = Logger.getLogger(ModuleEngine.class);
    static Logger contentHandlerLogger =
    	Logger.getLogger(ModuleEngine.ModuleContentHandler.class);
    static Logger errorHandlerLogger =
    	Logger.getLogger(ModuleEngine.ModuleErrorHandler.class);

	private static final String[] NULL_STRING_ARRAY = new String[0];
	private static Module[] NULL_MODULE_ARRAY = new Module[0];
    
    /** Query that returns the resolved names of modules from database */
	private static final String RESOLVED_MODULES_QUERY = 
		"select name from module where hide_from_user=FALSE "; //$NON-NLS-1$
	
    /** Query that returns expired field from module table by module name */
	private static final String MODULE_EXPIRED_QUERY = 
		"select expired from module where name = ?"; //$NON-NLS-1$

	/** Relative path to the modules' folder */
    public static final String MODULES_FOLDER_REAL_PATH = "/WEB-INF/modules"; //$NON-NLS-1$

	/** Relative path to the folder where hibernate config files are allocated */
    public static final String ALLOCATE_HIBERNATE_REAL_PATH = "/WEB-INF/generated/hibernate"; //$NON-NLS-1$
    
    /** The manifest file name */
    public static final String MODULE_MANIFEST_NAME = "module.xml"; //$NON-NLS-1$

    /** The folder name for spring configuration files */
    public static final String SPRING_FOLDER 		= "spring"; //$NON-NLS-1$

    /** The folder name for hibernate configuration files */
    public static final String HIBERNATE_FOLDER 	= "hibernate"; //$NON-NLS-1$

    /** The core extension point. This point is set default */
    public static final String CORE_EXTENSION_POINT = "core"; //$NON-NLS-1$

    /** The element name that defines a mapping url in core extension point */
    public static final String MAPPING_URL = "mapping-url"; //$NON-NLS-1$

    /** The element name that defines root page component element */
    public static final String ROOT_PAGE_COMPONENT = "page-components"; //$NON-NLS-1$

    /** The element name that defines nested page component element */
    public static final String NESTED_PAGE_COMPONENT = "page-component"; //$NON-NLS-1$
    
	private static ModuleEngine instance;

	/** The map of spring configuration files */
	private Map<String, List<String>> springConfigMap = new HashMap<String, List<String>>();
	
	/** The map of hibernate configuration files */
	private Map<String, List<String>> hibernateConfigMap = new HashMap<String, List<String>>(); 

	/** The map of allocated hibernate configuration files. The configuration files are allocated into the
	 *  specified destination folder. In our case, it is '/WEB-INF/generated/hibernate'.
	 */
	private Map<String, List<String>> allocatedHibernateConfigMap = new HashMap<String, List<String>>(); 
	
	/** The module instances */
	Map<String, Module> modules = new HashMap<String, Module>();
	
	private ModuleEngine() {
		// do nothing
	}

	/**
	 * A filter for xml files.
	 */
	private class XmlFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".xml");
		}
	}
	
	/**
	 *  The SAX content handler for parsing manifest files.
	 */
	private class ModuleContentHandler implements ContentHandler {

		/** Constants for root module element */
		public static final String MODULE 		= "module"; //$NON-NLS-1$
		public static final String MODULE_ID 	= "id"; //$NON-NLS-1$
		public static final String MODULE_NAME 	= "name"; //$NON-NLS-1$
		
		/** Constants for extension module element */
		public static final String EXTENSION 		= "extension"; //$NON-NLS-1$
		public static final String EXTENSION_TARGET	= "point"; //$NON-NLS-1$
		
		/** Constants for definition of states during parsing */
		private static final int IGNORED_ELEMENT_STATE 			= 0;
		private static final int INITIAL_STATE 					= 1;
		private static final int BUNDLE_STATE 					= 2;
		private static final int BUNDLE_EXTENSION_STATE 		= 3;
		private static final int CONFIGURATION_ELEMENT_STATE 	= 4;
		private static final int BUNDLE_EXTENSION_POINT_STATE   = 5;

		/** The stack of states */
		private Stack<Integer> stateStack = new Stack<Integer>();
		
		/** The stack of configuration elements */
		private Stack<ConfigurationElement> ceStack = new Stack<ConfigurationElement>();

		/** The current parsed module */
		private Module currentModule;
		
		/** The current parsed extension */
		private Extension currentExtension;
		
		/** The current parsed value of configuration element */
		private String configurationElementValue;
		
		public void characters(char[] ch, int start, int length) {
			int state = stateStack.peek().intValue();
			if (state == CONFIGURATION_ELEMENT_STATE) {
				ConfigurationElement currentConfigElement = ceStack.peek();
				String value = new String(ch, start, length);
				if (configurationElementValue == null) {
					if (value.trim().length() != 0) {
						configurationElementValue = value;
					}
				} else {
					configurationElementValue = configurationElementValue + value;
				}
				if (configurationElementValue != null)
					currentConfigElement.setValue(configurationElementValue);
			}
		}

		public void endDocument() {
			// do nothing
		}

		public void endElement(String uri, String elementName, String qName) {
			switch (stateStack.peek().intValue()) {
				case IGNORED_ELEMENT_STATE :
					stateStack.pop();
					break;
				case INITIAL_STATE :
					internalError("Shouldn't get here '" + elementName + "'");
					break;
				case BUNDLE_STATE :
					if (MODULE.equals(elementName)) {
						stateStack.pop();
					}
					break;
				case BUNDLE_EXTENSION_STATE :
					if (elementName.equals(EXTENSION)) {
						stateStack.pop();
					}
					break;
				case CONFIGURATION_ELEMENT_STATE :
					stateStack.pop();
					configurationElementValue = null;
					ConfigurationElement currentConfigElement = ceStack.pop();
					
					if (!ceStack.empty()) {
						ConfigurationElement parent = ceStack.peek();
						parent.addChild(currentConfigElement);
					}
			}
		}

		public void endPrefixMapping(String arg0) {
			// do nothing
		}

		public void ignorableWhitespace(char[] arg0, int arg1, int arg2) {
			// do nothing
		}

		public void processingInstruction(String arg0, String arg1) {
			// do nothing
		}

		public void setDocumentLocator(Locator locator) {
			// do nothing
		}

		public void skippedEntity(String arg0) {
			// do nothing
		}

		public void startDocument() {
			stateStack.push(new Integer(INITIAL_STATE));
		}

		private void handleInitialState(String elementName, Attributes attributes) {
			if (!MODULE.equals(elementName)) {
				stateStack.push(new Integer(IGNORED_ELEMENT_STATE));
				internalError("Unknown top element: " + elementName);
				return;
			}
			stateStack.push(new Integer(BUNDLE_STATE));
			parseModuleAttributes(attributes);
		}

		private void handleBundleState(String elementName, Attributes attributes) {
			if (EXTENSION.equals(elementName)) {
				stateStack.push(new Integer(BUNDLE_EXTENSION_STATE));
				parseExtensionAttributes(attributes);
				return;
			}
			stateStack.push(new Integer(IGNORED_ELEMENT_STATE));
		}
		
		private void handleExtensionPointState(String elementName) {
			stateStack.push(new Integer(IGNORED_ELEMENT_STATE));
		}
		
		private void handleExtensionState(String elementName, Attributes attributes) {
			stateStack.push(new Integer(CONFIGURATION_ELEMENT_STATE));

			configurationElementValue = null;
			ConfigurationElement currentConfigurationElement = new ConfigurationElement();
			ceStack.push(currentConfigurationElement);
			currentConfigurationElement.setName(elementName);
			parseConfigurationElementAttributes(attributes);
			currentExtension.addConfigurationElement(currentConfigurationElement);
		}

		private void parseModuleAttributes(Attributes attributes) {
			currentModule = new Module();
			int len = (attributes != null) ? attributes.getLength() : 0;
			for (int i = 0; i < len; i++) {
				String attrName = attributes.getLocalName(i);
				String attrValue = attributes.getValue(i).trim();
				if (MODULE_NAME.equals(attrName)) {
					currentModule.setName(attrValue);
				}
				else if (MODULE_ID.equals(attrName)) {
					currentModule.setId(attrValue);
				} else {
					unknownAttribute(MODULE, attrName);
				}
			}
			if (currentModule.getId() == null) {
				missingAttribute(MODULE_ID, MODULE);
				return;
			}
			
			// store module
			ModuleEngine.this.modules.put(currentModule.getId(), currentModule);			
		}
		
		private void parseExtensionAttributes(Attributes attributes) {
			currentExtension = new Extension();
			int len = (attributes != null) ? attributes.getLength() : 0;
			for (int i = 0; i < len; i++) {
				String attrName = attributes.getLocalName(i);
				String attrValue = attributes.getValue(i).trim();
				if (EXTENSION_TARGET.equals(attrName)) {
					currentExtension.setExtensionPointIdentifier(attrValue);
				} else {
					unknownAttribute(EXTENSION, attrName);
				}
			}
			if (currentExtension.getExtensionPointIdentifier() == null) {
				missingAttribute(EXTENSION_TARGET, EXTENSION);
				return;
			}
			currentModule.addExtension(currentExtension);
		}
		
		private void parseConfigurationElementAttributes(Attributes attributes) {
			ConfigurationElement parentConfigurationElement = ceStack.peek();
			int len = (attributes != null) ? attributes.getLength() : 0;
			for (int i = 0; i < len; i++) {
				String name = attributes.getLocalName(i);
				String value = attributes.getValue(i);
				parentConfigurationElement.addAttribute(name, value);
			}
		}
		
		public void startElement(String uri, String elementName, String qName,
				Attributes attributes) {
			switch (stateStack.peek().intValue()) {
				case INITIAL_STATE :
					handleInitialState(elementName, attributes);
					break;
				case BUNDLE_STATE :
					handleBundleState(elementName, attributes);
					break;
				case BUNDLE_EXTENSION_POINT_STATE :
					handleExtensionPointState(elementName);
					break;
				case BUNDLE_EXTENSION_STATE :
				case CONFIGURATION_ELEMENT_STATE :
					handleExtensionState(elementName, attributes);
					break;
				default :
					stateStack.push(new Integer(IGNORED_ELEMENT_STATE));
			}
		}

		private void internalError(String message) {
			contentHandlerLogger.error("Internal error in parsing manifest file: "
					+ message);
		}
		
		private void unknownAttribute(String attribute, String element) {
			contentHandlerLogger.error("Unknown attribute in parsing manifest file: ("
					+ attribute + "=" + element + ")");
		}

		private void missingAttribute(String attribute, String element) {
			contentHandlerLogger.error("Missing attribute in parsing manifest file: ("
					+ attribute + "=" + element + ")");
		}
		
		public void startPrefixMapping(String arg0, String arg1) {
			// do nothing
		}
	}
	
	/**
	 *  The SAX error handler in parsing of manifest files.
	 */
	private class ModuleErrorHandler implements ErrorHandler {

		public void error(SAXParseException e) {
			errorHandlerLogger.error("Error in parsing of document - "
					+ "line: " + e.getLineNumber()
					+ ", URI:" + e.getSystemId()
					+ ", message" + e.getMessage());
		}

		public void fatalError(SAXParseException e) {
			errorHandlerLogger.error("Fatal error in parsing of document - "
					+ "line: " + e.getLineNumber()
					+ ", URI:" + e.getSystemId()
					+ ", message" + e.getMessage());
		}

		public void warning(SAXParseException e) {
			errorHandlerLogger.error("Warning in parsing of document - "
					+ "line: " + e.getLineNumber()
					+ ", URI:" + e.getSystemId()
					+ ", message" + e.getMessage());
		}

	}
	
	/**
	 * Determines resolved modules and parsed manifest files. 
	 */
	private void init() {
		logger.debug("+");
		
		// obtain allowable modules from database
		List<String> resolvedModuleNames = obtainResolvedModuleNames();

		// instantiate a parser
		XMLReader parser = new SAXParser();
//		XMLReader parser = null;
//		try {
//			SAXParserFactory factory = SAXParserFactory.newInstance();
//			factory.setNamespaceAware(false);
//			factory.setValidating(false);
//			
//			parser = factory.newSAXParser().getXMLReader();
//			DefaultHandler defaultHandler = new DefaultHandler();
//			parser.setDTDHandler(defaultHandler);
//			parser.setEntityResolver(defaultHandler);
//		} catch (Exception e) {
//			logger.error("", e);
//			throw new RuntimeException(e);
//		}
		// register the content handler
		parser.setContentHandler(this.new ModuleContentHandler());
		// register the error handler
		parser.setErrorHandler(this.new ModuleErrorHandler());
		// get manifest files and parse them
		File rootFolder = new File(Env.getRealPath(MODULES_FOLDER_REAL_PATH));
		File[] files = rootFolder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory() && resolvedModuleNames.contains(file.getName())) {
					try {
						String moduleKey = file.getName();
						String relPath = MODULES_FOLDER_REAL_PATH
							+ File.separator + file.getName(); 
						// obtain and add the spring configurations
						List<String> springConfigs = obtainXmlFiles(relPath, SPRING_FOLDER);
						this.springConfigMap.put(moduleKey, springConfigs);
						// obtain and add the hibernate configurations
						List<String> hibernateConfigs = obtainXmlFiles(relPath, HIBERNATE_FOLDER);
						this.hibernateConfigMap.put(moduleKey, hibernateConfigs);
						this.allocatedHibernateConfigMap.put(moduleKey, allocate(hibernateConfigs));
						// obtain and store into the module registry the data
						// frommodule manifest file
						File moduleFile = new File(file, MODULE_MANIFEST_NAME);
						InputSource is = new InputSource(new FileReader(moduleFile));
						parser.parse(is);
					} catch (IOException e) {
						logger.error("Error reading URI: ", e);
					} catch (SAXException e) {
						logger.error("Error in parsing: ", e);
					}
				}
			}
		} else {
			logger.info("No modules found");
		}
		
		logger.debug("-");
	}
	
	/**
	 * Returns a list of xml files.
	 * @param relPath the relative path to module.
	 * @param subfolderName the name of subfolder where files are searched.
	 * @return the list of file names with relative pathes.
	 * @throws IOException
	 */
	private List<String> obtainXmlFiles(String relPath, String subfolderName) {
		logger.debug("+");
		List<String> xmlFiles = new ArrayList<String>();
		File subFolder = new File(Env.getRealPath(relPath), subfolderName);
		File[] files = subFolder.listFiles(new XmlFileFilter());
		if (files == null) {
			logger.warn("- The '" + subfolderName
					+ "' subfolder not found in '" + relPath + "'");
		} else {
			for (File file : files) {
				if (file.isFile()) {
					xmlFiles.add(relPath
							+ File.separator + subfolderName
							+ File.separator + file.getName());
				}
			}
		}
		logger.debug("-");
		return xmlFiles;
	}
	
	/**
	 * Reads from database the resolved module names.
	 * @return the list of module names
	 */
	private List<String> obtainResolvedModuleNames() {
		logger.debug("+");
		List<String> dbNames = new ArrayList<String>();
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			conn = DBHelper.getConnection();
			statement = conn.createStatement();
			rs = statement.executeQuery(RESOLVED_MODULES_QUERY);
			while (rs.next()) {
				String moduleId = rs.getString(1);
				dbNames.add(moduleId);
				logger.debug("Resolved module: " + moduleId);
			}
		} catch(SQLException e) {
			logger.error("-", e);
			throw new CriticalException(e);
		} finally {
			DBHelper.close(rs, statement, conn);
		}
		logger.debug("-");
		return dbNames;
	}

	/**
	 * Returns an instance of singleton.
	 * @return the ModuleEngine instance.
	 */
	public static synchronized ModuleEngine getInstance() {
		if (instance == null) {
			instance = new ModuleEngine();
			instance.init();
		}
		return instance;
	}
	
	/**
	 * Returns spring configuration files for specified module.
	 * @param moduleId the module identifier.
	 * @return the string array of spring configuration files. If the spring
	 * configuration files weren't defined, returns zero-length array. 
	 */
	public String[] getSpringConfigs(String moduleId) {
		List<String> configs = springConfigMap.get(moduleId);
		return (configs == null) ? NULL_STRING_ARRAY : configs.toArray(NULL_STRING_ARRAY);
	}

	/**
	 * Returns hibernate configuration files for specified module.
	 * @param moduleId the module identifier.
	 * @return the string array of hibernate configuration files. If the
	 * hibernate configuration files weren't defined, returns zero-length array.
	 */
	public String[] getHibernateConfigs(String moduleId) {
		List<String> configs = hibernateConfigMap.get(moduleId);
		return (configs == null) ? NULL_STRING_ARRAY : configs.toArray(NULL_STRING_ARRAY);
	}

	/**
	 * Returns hibernate configuration files from all resolved modules.
	 * @return the string array of hibernate configuration files. If the
	 * hibernate configuration files weren't defined, returns zero-length array.
	 */
	public static String[] getAllHibernateConfigs() {
		Collection<List<String>> values = getInstance().hibernateConfigMap.values();
		
		if (values == null)
			return NULL_STRING_ARRAY;
		
		// gather all lists into the one list
		List<String> list = new ArrayList<String>();
		for (List<String> l : values) {
			list.addAll(l);
		}
		
		return list.toArray(NULL_STRING_ARRAY);
	}	

	public static String[] getAllocatedHibernateConfigs() {
		Collection<List<String>> values = getInstance().allocatedHibernateConfigMap.values();
		
		if (values == null)
			return NULL_STRING_ARRAY;
		
		// gather all lists into the one list
		List<String> list = new ArrayList<String>();
		for (List<String> l : values) {
			list.addAll(l);
		}
		
		return list.toArray(NULL_STRING_ARRAY);
	}	
	
	/**
	 * Returns parsed instances of modules.
	 * @return the array of parsed instances of modules or zero length array if
	 * no modules present.
	 */
	public Module[] getModules() {
		Collection<Module> values = modules.values();
		return (values == null) ? NULL_MODULE_ARRAY : values.toArray(NULL_MODULE_ARRAY);
	}	

	/**
	 * Returns specified module.
	 * @param mid the module identifier.
	 * @return the module instance.
	 */
	public Module getModule(String mid) {
		return modules.get(mid);
	}
	
	public static void copyFile(File source, File destination) throws IOException {
		if (!destination.exists()) {
			destination.createNewFile();
		}
		FileChannel sourceChannel = null;
		FileChannel destinationChannel = null;
		try {
			sourceChannel = new FileInputStream(source).getChannel();
			destinationChannel = new FileOutputStream(destination).getChannel();
			destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		} finally {
			if (sourceChannel != null) {
				sourceChannel.close();
			}
			if (destinationChannel != null) {
				destinationChannel.close();
			}
		}
	}
	
	private List<String> allocate(List<String> files) {
		logger.debug("+");
		List<String> allocated = new ArrayList<String>();
		File destFolder = new File(Env.getRealPath(ALLOCATE_HIBERNATE_REAL_PATH));
		if (!destFolder.exists()) {
			destFolder.mkdir();
		}
		for (String filepath : files) {
			int index = filepath.lastIndexOf(File.separator);
			String filename = filepath.substring(index + 1);
			File source = new File(Env.getRealPath(filepath));
			File destination = new File(destFolder, filename);
			logger.debug("Copying file (source="
					+ source + "; destination=" + destination);
			try {
				copyFile(source, destination);
				String allocatedFilepath = ALLOCATE_HIBERNATE_REAL_PATH + "/" + filename;
				allocated.add(allocatedFilepath);
				logger.debug("Added (" + allocatedFilepath + ") into the allocated hibernate configuration files");
			} catch (IOException e) {
				logger.error("Error copying file (source="
						+ source + "; destination=" + destination);
			}
		}
		logger.debug("-");
		return allocated;
	}
	
	public Boolean isModuleExpired(String moduleName){
		logger.debug("+");
		Date expiredDate;
		Boolean expired = true;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBHelper.getConnection();
			stmt = conn.prepareStatement(MODULE_EXPIRED_QUERY);
			stmt.setString(1, moduleName);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				java.util.Date sqlDate = rs.getDate("expired");
				if (sqlDate != null){
					expiredDate = new Date(sqlDate.getTime());
					Date nowDate = new Date();
					return expiredDate.before(nowDate);
				}
				return false;		
			}
		} catch(SQLException e) {
			logger.error("-", e);
			throw new CriticalException(e);
		} finally {
			DBHelper.close(rs, stmt, conn);
		}
		logger.debug("-");
		return expired;
	}

}
