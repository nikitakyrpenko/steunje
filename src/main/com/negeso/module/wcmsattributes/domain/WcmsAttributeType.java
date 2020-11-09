/*
 * Created on 12.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.domain;

import org.apache.log4j.Logger;

/**
 * @author OLyebyedyev
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WcmsAttributeType {
    private static Logger logger = Logger.getLogger(WcmsAttributeType.class);
    private static final String PICTURE = "picture";
    private static final String ARTICLE = "article";
    public static final String TEXT = "text"; //can be used for links, for instance
    public static WcmsAttributeType picture = new WcmsAttributeType(PICTURE);  
    public static WcmsAttributeType article = new WcmsAttributeType(ARTICLE);
    
    private String type;
    private WcmsAttributeType() {};
    protected WcmsAttributeType(String type) {
        if (type!= null && (type.equals(PICTURE) || type.equals(ARTICLE))) {
            this.type = type;
        }
        else {
            logger.error("Unknown wcms attribute type or NULL: " + type);
            throw new RuntimeException("Unknown wcms attribute type or NULL: " + type);
        }
    }
    
    public String getTypeName() {
        return this.type;
    }
    
    
    /**
     * 
     * @param type
     * @return
     */
    public static WcmsAttributeType getType(String type) {
        if (type != null) {
            if (type.equals(PICTURE))
                return WcmsAttributeType.picture;
            else if (type.equals(ARTICLE))
                return WcmsAttributeType.article;
        }
        logger.error("Incorrect wcms attribute type: " + type);
        return null;
       
    }
}
