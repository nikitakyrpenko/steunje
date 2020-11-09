package com.negeso.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.util.FileUtils;

public class InitialConfigListener implements ServletContextListener {

	public final static String FOLDER_FOR_LOGS = "/WEB-INF/generated/logs";
	
	private void makeLogsFolderIfNeeded(String folder) {
		File file = new File(folder);
		if (!file.exists()) {
			try {
				org.apache.commons.io.FileUtils.forceMkdir(file);
			} catch (IOException e) {
				throw new CriticalException(
						"Unable to create '" + file.getAbsolutePath() + "' folder"
				);
			}	
		}	
	}
	
	/** Configures Log4j using current webapp root directory */
	public void contextInitialized(ServletContextEvent event) {
		
		ServletContext ctx = event.getServletContext();
		makeLogsFolderIfNeeded(ctx.getRealPath(FOLDER_FOR_LOGS));
		configureLog(ctx);
		// store the servlet context in Env
		Env.setServletContext(ctx);
		
	}
/////
    private void configureLog(ServletContext context){
        InputStream log4JConfig = context.getResourceAsStream("/WEB-INF/log4j.xml");
                
        Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(log4JConfig);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		addFileElement(doc, 0, context.getRealPath("/WEB-INF/generated/logs/framework.log"));
		String reqPath = getCorrectRequestFolder(context) + "framework.log";
		addFileElement(doc, 1, context.getRealPath(reqPath));
			
        DOMConfigurator.configure(doc.getDocumentElement());
    }
    private void addFileElement(Document doc, int itemNum, String correctPath){
    	NodeList allAppenders = doc.getElementsByTagName("appender");
    	
        Element fileNode = doc.createElement("param");
    	fileNode.setAttribute("name", "File");
    	
    	correctPath = replaceBackslashWithDoubleBackslash(correctPath);
    	
    	fileNode.setAttribute("value", correctPath);
    	allAppenders.item(itemNum).appendChild(fileNode);
    	
    }
    
    private void processLayout(Node appender){
    	NodeList childs = appender.getChildNodes();
    	
    	for (int i=0; i<childs.getLength(); i++){
    		Node chield = childs.item(i);
    		if ("layout".equals(chield.getNodeName())){
    			replaceLayoutConversionPattern(chield);
    			break;
    		}
    	}
    }
    
    private String getCorrectRequestFolder(ServletContext context){
        String path = "WEB-INF/generated/logs/requests/";
        FileUtils.createDirIfNotExists(context.getRealPath(path));
        return path;
    }    
    private String replaceBackslashWithDoubleBackslash(String str){
        return str.replaceAll("\\\\", "\\\\\\\\");
    }
    private void replaceLayoutConversionPattern(Node layoutElement){
    	NodeList childs = layoutElement.getChildNodes();
    	for (int i=0; i<childs.getLength(); i++){
    		Node chield = childs.item(i);
    		
    		if ("param".equals(chield.getNodeName())){
    			Element parmElement = (Element) chield;
    			String strValue = parmElement.getAttribute("value");
    			strValue = strValue.replace("{site}", Env.getSiteId()+"");
    			parmElement.setAttribute("value", strValue);
    			break;
    		}    		
    	}
    	
    }	
/////	
	

	/** Shutdown logger */
	public void contextDestroyed(ServletContextEvent event) {
    	DBHelper.stopDatabaseMonitor();
		LogManager.shutdown();
	}

}
