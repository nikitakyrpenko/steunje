/*
 * @(#)$Id: Template.java,v 1.5, 2005-06-17 13:59:01Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.site;

import java.io.File;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;


/**
 *
 * Site template domain object. Represents site's design template. 
 * 
 * @version		$Revision: 6$
 * @author		Olexiy Strashko
 * 
 */
public class Template {
    
    static Logger logger = Logger.getLogger(Template.class);

    private static final String TMPL_PROPERTIES_FILE_NAME = "template.properties";

    private String name = null;
    private Properties properties = null;


    /**
     * Find template by name
     * 
     * @param templateName
     * @return
     */
    public static Template findByName(String templateName){
        Template template  = new Template();
        template.setName(templateName);

        File file = new File( template.getRealPath() );
        if ( !file.exists() ){
            String msg = 
                "Template not foung by name:" + templateName +
                " path:" + template.getRealPath()
            ;
            logger.error(msg);
            throw new IllegalStateException(msg); 
        }
        return template;
    }
    
    /**
     * 
     */
    public Template() {
        super();
    }

    public String getRealPath(){
        return MessageFormat.format(
            "{0}templates/{1}", Env.getRealPath("/"), this.getName()
        );
    }
    

    /**
     * Get site properties from template conf file 
     * 
     * @return
     */
//    public Properties getProperties(){
//        if ( this.properties  == null ){
//            // load properties from database 
//            this.properties = new Properties();
//            File file = new File( this.getRealPath() + "/" + TMPL_PROPERTIES_FILE_NAME );
//            if ( !file.exists() ){
//                logger.error(
//                    "ERROR: missing properties file:" + file.getAbsolutePath() +
//                    " for template:" + this.getName() +
//                    " returning empty properties"
//                );
//                return this.properties;
//            }
//            FileInputStream is = null;
//            try{
//                is = new FileInputStream(file);
//                this.properties.load(is);
//                is.close();
//                if (logger.isInfoEnabled()){
//                    logger.info(
//                        "load properties " + file +
//                        " for template: " + this.getName()
//                    );
//                }
//
//            }
//            catch(IOException e){
//                logger.error(
//                    "IOERROR in properties for template:" + this.getName() + 
//                    " returning empty properties", 
//                    e
//                );
//            }
//            finally{
//                if ( is != null ){
//                    try{
//                        is.close();
//                    }
//                    catch(IOException e){
//                        logger.error(
//                            "IOERROR in properties for template:" + this.getName() + 
//                            " returning empty properties", 
//                            e
//                        );
//                    }
//                }
//            }
//        }
//        return this.properties;
//    }

    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
