/*
 * Created on 08.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.command;

import java.sql.*;

import org.apache.log4j.Logger;


import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.DBHelper;

/**
 * @author OLyebyedyev
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteImage extends AbstractCommand {
    private static Logger logger = Logger.getLogger(DeleteImage.class);
    
    private static final String IMAGE_ID = "image_id";
    
    private static final String deleteImageSql = 
        " DELETE FROM image WHERE image_id = ? ";
    
    public ResponseContext execute() {
        logger.debug("+");

        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();
        
        String imageId = request.getParameter(IMAGE_ID);
        long imageIdValue = -1; 
        if (imageId == null) {
            
            response.setResultName(AbstractCommand.RESULT_FAILURE);
            logger.error("- No image id");
            return response;
        }
        try {
            imageIdValue = Long.parseLong(imageId);
        } catch (NumberFormatException e) {
            response.setResultName(AbstractCommand.RESULT_FAILURE);
            logger.error("- Incorrect image id :" + imageId);
            return response;
        }
        
        Connection con = null;
        try {
            con = DBHelper.getConnection();
            PreparedStatement pstmt = con.prepareStatement(deleteImageSql);
            pstmt.setLong(1, imageIdValue);
            pstmt.executeUpdate();
            pstmt.close();
            response.setResultName(AbstractCommand.RESULT_SUCCESS);
        } catch (SQLException e1) {
            logger.error("-", e1);
            response.setResultName(AbstractCommand.RESULT_FAILURE);
        } finally {
            DBHelper.close(con);
        }

        logger.debug("-");
        return response;
    }
}
