/*
 * Created on 25.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.command;

import java.util.Map;

import org.apache.log4j.Logger;


import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.*;

/**
 * @author OLyebyedyev
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GetAttributes extends AbstractCommand {
    private static Logger logger = Logger.getLogger(GetAttributes.class);
    
    public static final String RESULT_SUCCESS = "success";

    /**  */
    public static final String RESULT_FAILURE = "failure";

    /** Object of class File */
    public static final String OUTPUT_XML = "xml";
    
    public static final String WCMS_ATTRIBUTE_SET = "attribute_set_id";
    
    private static final String WCMS_ATTRIBUTES_XML = 
        Env.NEGESO_PREFIX+ ":wcms_attributes";
    
    private static final String IMAGE_SET_XML = 
        Env.NEGESO_PREFIX+ ":image_set";
    
    private static final String IMAGE = 
        Env.NEGESO_PREFIX+ ":image";
    
    
    /**
     * 
     */
    public GetAttributes() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public ResponseContext execute() {
        logger.debug("+");

        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        
        Map resultMap = response.getResultMap();
        
        String attribute_set_id = request.getParameter(WCMS_ATTRIBUTE_SET);
        
        
        if (attribute_set_id == null) {
            response.setResultName(RESULT_FAILURE);
            logger.error("No " + WCMS_ATTRIBUTE_SET + " in request");
            return response; 
        }
        
        
        
        
        logger.debug("-");
        return response;
    }

}
