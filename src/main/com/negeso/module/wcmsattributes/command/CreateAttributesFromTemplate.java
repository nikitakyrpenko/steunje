/*
 * Created on 12.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.command;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;


import com.negeso.framework.Env;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.*;
import com.negeso.module.wcmsattributes.domain.Attribute;
import com.negeso.module.wcmsattributes.domain.WcmsTemplate;

/**
 * @author OLyebyedyev
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CreateAttributesFromTemplate extends AbstractCommand {
        private static Logger logger = Logger.getLogger(CreateAttributesFromTemplate.class);

        /**  */
        public static final String ATTRIBUTE_SET_ID = "attrSetId";

        /**  */
        public static final String RESULT_SUCCESS = "success";

        /**  */
        public static final String RESULT_FAILURE = "failure";

        /** Object of class File */
        public static final String OUTPUT_XML = "xml";

        public static final String WCMS_TEMPL_NAME = "wcms_template_name";
        
        public static final String WCMS_ATTR_SET_ID = "wcms_attribute_set_id";
    
    
    /**
     * 
     */
    public CreateAttributesFromTemplate() {
        super();
    }

    
    
/***************************************************************
 * 
 ***************************************************************/
    public ResponseContext execute() {
        logger.debug("+");
        
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        Map resultMap = response.getResultMap();

        
        String wcms_templ_name = request.getParameter(WCMS_TEMPL_NAME);
        if (wcms_templ_name == null) {
            logger.error("No template Id in request");
            response.setResultName(RESULT_FAILURE);
            return response;
        }
        
        try {
            Connection con = DBHelper.getConnection();
        //get template
            //WcmsTemplate template = WcmsTemplate.getTemplate(con, new Long(tmpl_id));
            Attribute[] attrs = Attribute.getAttributes(con, wcms_templ_name);
            //Attribute.createAttributesFromTemplate(con, template);
            
            Long new_attrs_set = Attribute.createAttributesFromTemplate(con, attrs);
            
            DBHelper.close(con);
            response.setResultName(RESULT_SUCCESS);
        } catch (SQLException ex)
        {
            logger.error(ex);
            response.setResultName(RESULT_FAILURE);
        }
        catch (CriticalException ex)
        {
            logger.error(ex);
            response.setResultName(RESULT_FAILURE);
        }
        
        logger.debug("-");
        return response;
    }
}
