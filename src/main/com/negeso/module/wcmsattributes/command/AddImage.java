/*
 * Created on 08.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.command;


import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.wcmsattributes.domain.*;

import java.sql.*;

import org.apache.log4j.Logger;


/**
 * @author OLyebyedyev
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddImage extends AbstractCommand {
    private static Logger logger = Logger.getLogger(AddImage.class);

    private static final String ATTRIBUTE_CLASS = "attribute_class"; 
    
    public ResponseContext execute() {
        logger.debug("+");
        
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        
        String attrSetId = request.getParameter(BrowseAttributes.WCMS_ATTRIBUTE_SET_ID);
        String attrClass = request.getParameter(AddImage.ATTRIBUTE_CLASS);
        
        if (attrSetId == null || attrClass == null) {
            response.setResultName(AbstractCommand.RESULT_FAILURE);
            logger.error(" - No atribute_set_id or image_set_class in request");
            return response;
        }
        
        Connection con = null;
        try {
            con = DBHelper.getConnection();
            ImageSet imgSet = ImageSet.getImageSet(con, attrClass);
            
            Image [] img = imgSet.getImages();
            if (img != null && img.length > 0) {
                Image.createImageFromTemplate(con, img[0], imgSet.getId());
                response.setResultName(AbstractCommand.RESULT_SUCCESS);
            } else {
                response.setResultName(AbstractCommand.RESULT_FAILURE);
                logger.error("No images in template");
            }

        } catch (SQLException e) {
            logger.error(e);
            response.setResultName(AbstractCommand.RESULT_FAILURE);
        }catch (CriticalException e) {
            logger.error(e);
            response.setResultName(AbstractCommand.RESULT_FAILURE);
        } finally {
            DBHelper.close(con);
        }
        
        logger.debug("-");
        return response;
    }
}
