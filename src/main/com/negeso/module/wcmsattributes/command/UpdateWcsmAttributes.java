/*
 * Created on 09.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.command;

import java.sql.*;

import org.apache.log4j.Logger;

import com.negeso.module.wcmsattributes.domain.*;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.*;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

/**
 * @author oleg
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class UpdateWcsmAttributes extends AbstractCommand {
    private static Logger logger = Logger.getLogger(UpdateWcsmAttributes.class);

    private static final String ACTION = "action_field";

    private static final String SRC = "src_field";

    private static final String IMG_SET_ID = "image_set_id_field";

    private static final String IMG_ID = "image_id_field";

    private static final String ATTR_SET_ID = "attribute_set_id_field";

    private static final String ATTR_CLASS = "attribute_class_field";
    private static final String FLASH_WIDTH = "flash_width"; 
    private static final String FLASH_HEIGHT = "flash_height";    

    public ResponseContext execute() {
        logger.debug("+");

        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();

        String action = request.getParameter(ACTION);
        String src = request.getParameter(SRC);
        String imgSetId = request.getParameter(IMG_SET_ID);
        String imgId = request.getParameter(IMG_ID);
        String attrSetId = request.getParameter(ATTR_SET_ID);
        String attrClass = request.getParameter(ATTR_CLASS);
        String flashWidth = request.getParameter(FLASH_WIDTH); 
        String flashHeight = request.getParameter(FLASH_HEIGHT); 

        try {
	        if (action.equals("change")) {
	            if (src == null || imgId == null) {
	                logger.error("Action: change, src_field or image_id_field is null");
	                response.setResultName(AbstractCommand.RESULT_FAILURE);
	                return response;
	            }
	            
	            this.changeImage(imgId, src);
	
	        } else if (action.equals("changeFlash")) {
	        	if (src == null || imgId == null || flashWidth == null || flashHeight == null) {
	        		logger.error("Action: changeFlash, src_field, image_id_field, flash_widht or flash_height is null");
	        		response.setResultName(AbstractCommand.RESULT_FAILURE);
	        		return response;
	        	}
	        	/*if (src == null || imgId == null || flashHeight == null) {
	        		logger.error("Action: changeFlash, src_field, image_id_field, flash_widht or flash_height is null");
	        		response.setResultName(AbstractCommand.RESULT_FAILURE);
	        		return response;
	        	}*/
	        	
	        	this.changeFlash(imgId, src, flashHeight, flashWidth);
	        	
	        	
	        } else if (action.equals("delete")) {
	            if (imgId == null) {
	                logger.error("Action: delete, image_id_field is null");
	                response.setResultName(AbstractCommand.RESULT_FAILURE);
	                return response;
	            }
                this.deleteImage(imgId);
	
	        } else if (action.equals("add")) {
	
	            if (attrClass == null || imgSetId == null) {
	                logger.error("Action: add, image_set_id_field is null or attribute class is null");
	                response.setResultName(AbstractCommand.RESULT_FAILURE);
	                return response;
	            }
                this.addImage(attrClass, imgSetId, src);
	
	        } else if (action.equals("addFlash")) {
	        	
	        	if (attrClass == null || imgSetId == null || flashWidth == null || flashHeight == null) {
	        		logger.error("Action: addFlash, image_set_id_field, attribute class, flash_widht or flash_height is null");
	        		response.setResultName(AbstractCommand.RESULT_FAILURE);
	        		return response;
	        	}
	        	this.addFlash(attrClass, imgSetId, src, flashHeight, flashWidth);
	        	
	        } else if (action.equals("up") || action.equals("down")) {
	
	            if (attrClass == null || imgId == null) {
	                logger.error("Action: " + action + "; attribute class is null or imageId is null");
	                response.setResultName(AbstractCommand.RESULT_FAILURE);
	                return response;
	            }
                this.reorderImage(imgId, action.equals("up") ? 1 : -1);
	
	        } else {
	            logger.error("Unknown action: " + action);
	            response.setResultName(AbstractCommand.RESULT_FAILURE);
	            return response;
	        }
	        
	        request.setParameter(BrowseAttributes.WCMS_ATTRIBUTE_SET_ID, attrSetId);
	        BrowseAttributes command = new BrowseAttributes();
	        command.setRequestContext(request);
	        logger.debug("-");
	        return command.execute();

        } catch (CriticalException e) {
            response.setResultName(AbstractCommand.RESULT_FAILURE);
            logger.error("-", e);
            return response;
        }

    }
    private void addFlash(String attrClass, String imgSetId, String src, String flashHeight, String flashWidth) throws CriticalException {
        logger.debug("+");

        Connection con = null;
        try {

            con = DBHelper.getConnection();
            ImageSet.addFlash(con, attrClass, Long.parseLong(imgSetId), src, Long.parseLong(flashHeight), Long.parseLong(flashWidth));
        
        } catch (NumberFormatException e) {
            logger.error("-", e);
            throw new CriticalException(e);

        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        } finally {
            DBHelper.close(con);
        }
        logger.debug("-");

		
	}

	private void changeFlash(String imgId, String src, String flashHeight, String flashWidth) throws CriticalException {
        logger.debug("+");

        Connection con = null;
        try {
            con = DBHelper.getConnection();
            long imgIdValue = Long.parseLong(imgId);
            Image.updateImageSrc(con, imgIdValue, src);
            
            Image.updateImageSize(con, imgIdValue, Long.parseLong(flashHeight), Long.parseLong(flashWidth));
            //Image.updateImageSize(con, imgIdValue, Long.parseLong(flashHeight), 0);

        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        } catch (NumberFormatException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        } finally {
            DBHelper.close(con);
        }

        logger.debug("-");		
	}

    /**
     * 
     * @param imgId
     * @param src
     * @throws CriticalException
     */

    private void changeImage(String imgId, String src) throws CriticalException {
        logger.debug("+");

        Connection con = null;
        try {
            con = DBHelper.getConnection();
            long imgIdValue = Long.parseLong(imgId);
            Image.updateImageSrc(con, imgIdValue, src);
        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        } catch (NumberFormatException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        } finally {
            DBHelper.close(con);
        }

        logger.debug("-");
    }

    /**
     * 
     * @param imgId
     * @throws CriticalException
     */
    private void deleteImage(String imgId) throws CriticalException {
        logger.debug("+");

        Connection con = null;
        try {
            con = DBHelper.getConnection();
            long imgIdValue = Long.parseLong(imgId);
            Image.deleteImage(con, imgIdValue);
        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        } catch (NumberFormatException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        } finally {
            DBHelper.close(con);
        }

        logger.debug("-");
    }

    
/**
 * 
 * @param attrClass
 * @param imgSetId
 * @throws CriticalException
 */
    private void addImage(String attrClass, String imgSetId, String src)
            throws CriticalException {
        logger.debug("+");

        long imgSetIdValue = -1;
        try {
            imgSetIdValue = Long.parseLong(imgSetId);
        } catch (NumberFormatException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        }

        Connection con = null;
        try {

            con = DBHelper.getConnection();
            ImageSet.addImage(con, attrClass, imgSetIdValue, src);
            
        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        } finally {
            DBHelper.close(con);
        }
        logger.debug("-");
    }
    
    /**
     * 
     * @param imgId
     * @param direction 1 - up; -1 - down
     */
    public void reorderImage(String imgId, int direction) {
        logger.debug("+");

        long imgIdValue = -1;
        try {
            imgIdValue = Long.parseLong(imgId);
        } catch (NumberFormatException e) {
            logger.error("-", e);
            return;
        }
        
        Connection con = null;
        try {
            con = DBHelper.getConnection();
            Image.reorderimages(con, imgIdValue, direction);
        } catch (SQLException e) {
            logger.error("-", e);
            return;
        } finally {
            DBHelper.close(con);
        }
        
        logger.debug("-");
    }
    
}