/*
 * Created on 01.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.form_manager.command;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.DBHelper;

import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.module.form_manager.FormXmlBuilder;

/**
 * @author Shkabi
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */ 
public class ChooseFormCommand extends AbstractCommand {

    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */
    private static Logger logger = Logger.getLogger(ManageFormsCommand.class);
    public static final String OUTPUT_DOCUMENT = "xml";
    
    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();
        try {
            if (!SecurityGuard.canEdit(request.getSession().getUser(), null)) {
                response.setResultName(RESULT_ACCESS_DENIED);
                logger.debug("- Access denied");
                return response;
            }
            Element parentElement = Xbuilder.createTopEl("form_manager");
            String formLanguage = request.getSession().getLanguageCode(); 
            Connection conn = null;
            try {
                conn = DBHelper.getConnection();
                Statement statement = conn.createStatement();
                
                FormXmlBuilder.buildFullFormList(
                        conn, statement, parentElement, formLanguage );
                
                statement.close();
            } catch (SQLException e) {
                logger.error("SQLException in FormManager " + e.getMessage() );
            } finally {
                DBHelper.close(conn);
            }
            
            Map resultMap = response.getResultMap();
            resultMap.put(OUTPUT_DOCUMENT, parentElement.getOwnerDocument());
            response.setResultName(RESULT_SUCCESS);
            logger.debug("-");
            
        }
        catch (Exception e) {
            response.setResultName(RESULT_FAILURE);
            logger.error("- Cannot process request ", e);
        }
        return response;
    }

}
