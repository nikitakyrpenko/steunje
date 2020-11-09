/*
 * Created on 03.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes.domain;

/**
 * @author OLyebyedyev
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;

public class ImageSet {

    private static Logger logger = Logger.getLogger(ImageSet.class);

    private static final String insertImageSet = " INSERT INTO images_set "
            + "(image_set_id, name, " + " required_width, required_height, "
            + "max_width, max_height, number_of_images) "
            + " VALUES (?,?,?,?,?,?,?) ";

    private static final String getimageSetTemplateSql = " SELECT imgset.image_set_id " +
            " imgset.name,  " +
            " imgset.required_width AS img_set_req_width, " +
            " imgset.required_height AS img_set_req_height, " +
            " imgset.max_width AS img_set_max_width, " +
            " imgset.max_height AS img_set_max_height, " +
            " imgset.number_of_images, " +
            " img.src, img.alt, " +
            " img.max_width AS img_max_width, " +
            " img.max_height AS img_max_height, " +
            " img.required_width AS img_req_width, " +
            " img.required_height AS img_req_height, " +
            " img._order, " +
            " img.link, " +
            " img.targe " +
            " FROM images_set imgset, image img, wcms_attribute wa, " +
            " wcms_attribute_set was WHERE wa.image_set_id = imgset.image_set_id " +
            " AND wa.wcms_attribute_set_id = was.wcms_attribute_set_id " +
            " AND img.image_set_id = imgset.image_set_id " +
            " AND wa.class = ? ";


    private static final String addImageFromTemplate = 
        " INSERT INTO image (image_set_id, src, alt, max_width, max_height, " +
        " required_width, required_height, _order) " +
        " SELECT ? AS image_set_id, ?, i.alt, i.max_width, i.max_height, " +
        " i.required_width, i.required_height, " +
        " ((SELECT MAX(COALESCE(_order, 0)) FROM image WHERE image_set_id = ?) + 1) AS _order " +
        " FROM image i, images_set iset, " +
        " wcms_attribute wa, wcms_attribute_set was " +
        " WHERE i.image_set_id = iset.image_set_id AND " +
        " wa.class = ? AND wa.image_set_id = iset.image_set_id AND " +
        " wa.wcms_attribute_set_id = was.wcms_attribute_set_id AND " +
        " was.wcms_templ_id IS NOT NULL LIMIT 1 "; 
    
    private static final String addFlashFromTemplate =  
    	" INSERT INTO image (image_set_id, src, alt, max_width, max_height, " + 
    	" required_width, required_height, _order) " + 
    	" SELECT ? AS image_set_id, ?, i.alt, ?, ?, " + 
    	" i.required_width, i.required_height, " + 
    	" ((SELECT MAX(COALESCE(_order, 0)) FROM image WHERE image_set_id = ?) + 1) AS _order " + 
    	" FROM image i, images_set iset, " + 
    	" wcms_attribute wa, wcms_attribute_set was " + 
    	" WHERE i.image_set_id = iset.image_set_id AND " + 
    	" wa.class = ? AND wa.image_set_id = iset.image_set_id AND " + 
    	" wa.wcms_attribute_set_id = was.wcms_attribute_set_id AND " + 
    	" was.wcms_templ_id IS NOT NULL LIMIT 1 ";  
    
    Long id = null;

    String name = null;

    Integer requiredWidth = null;

    Integer requiredHeight = null;

    Integer maxWidth = null;

    Integer maxHeight = null;

    Integer minWidth = null;

    Integer minHeight = null;

    Integer numberOfImages = null;

    Image[] images = null;

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Integer getRequiredWidth() {
        return this.requiredWidth;
    }

    public Integer getRequiredHeight() {
        return this.requiredHeight;
    }

    public Integer getMaxWidth() {
        return this.maxWidth;
    }

    public Integer getMaxHeight() {
        return this.maxHeight;
    }

    public Integer getMinWidth() {
        return this.minWidth;
    }

    public Integer getMinHeight() {
        return this.minHeight;
    }

    public Integer getNumberOfImages() {
        return this.numberOfImages;
    }

    public Image[] getImages() {
        return this.images;
    }

    /**
     * 
     * @param con
     * @param templ
     * @return @throws
     *         CriticalException
     */
    public static Long createImageSetFromTemplate(Connection con, ImageSet templ)
            throws CriticalException {
        logger.debug("+");

        Long newId = null;

        try {
            newId = DBHelper.getNextInsertId(con, "image_set_image_set_id_seq");
            PreparedStatement pstmt = con
                    .prepareStatement(ImageSet.insertImageSet);

            pstmt.setLong(1, newId.longValue()); //image_set_id
            pstmt.setString(2, templ.name); //name
            pstmt.setObject(3, templ.requiredWidth, java.sql.Types.INTEGER); //required_width
            pstmt.setObject(4, templ.requiredHeight, java.sql.Types.INTEGER); //required_height
            pstmt.setObject(5, templ.maxWidth, java.sql.Types.INTEGER); //maxWidth
            pstmt.setObject(6, templ.maxHeight, java.sql.Types.INTEGER); //maxHeight
            pstmt.setObject(7, templ.numberOfImages, java.sql.Types.INTEGER);

            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        }

        logger.debug("-");
        return newId;

    }
    
    
    public static ImageSet getImageSet(Connection con, String class_name) 
            throws CriticalException {
        logger.debug("+");
        try {
            PreparedStatement pstmt = con.prepareStatement(getimageSetTemplateSql);
            pstmt.setString(1, class_name);
            ResultSet rs = pstmt.executeQuery();
            ImageSet iset = null;
            ArrayList imgs = new ArrayList();
            /*
             imgset.image_set_id " +
            " imgset.name,  " +
            " imgset.required_width AS img_set_req_width, " +
            " imgset.required_height AS img_set_req_height, " +
            " imgset.max_width AS img_set_max_width, " +
            " imgset.max_height AS img_set_max_height, " +
            " imgset.number_of_images, " +
            " img.src, img.alt, " +
            " img.max_width AS img_max_width, " +
            " img.max_height AS img_max_height, " +
            " img.required_width AS img_req_width, " +
            " img.required_height AS img_req_height, " +
            " img._order " +             */
            while (rs.next()) {
                if (iset == null) {
                    
                    iset = new ImageSet();
                    iset.id = new Long(rs.getLong(1));          //image_set_id
                    
                    iset.name = rs.getString(2);                //name
                    
                    //img_set_req_width
                    int tmp = rs.getInt(3);
                    if (rs.wasNull())
                        iset.requiredWidth = null;
                    else
                        iset.requiredWidth = new Integer(tmp);
                    
                    //img_set_req_height
                    tmp = rs.getInt(4);
                    if (rs.wasNull())
                        iset.requiredHeight = null;
                    else
                        iset.requiredHeight = new Integer(tmp);  
                    
                    //img_set_max_width
                    tmp = rs.getInt(5);
                    if (rs.wasNull())
                        iset.maxWidth = null;
                    else
                        iset.maxWidth = new Integer(tmp);
                    
                    //img_set_max_height
                    tmp = rs.getInt(6);
                    if (rs.wasNull())
                        iset.maxHeight = null;
                    else
                        iset.maxHeight = new Integer(tmp);
                    
                    //number_of_images
                    tmp = rs.getInt(7);
                    if (rs.wasNull())
                        iset.numberOfImages = null;
                    else
                        iset.numberOfImages = new Integer(tmp);
                }
                
                Image img = new Image();
                //img.src, img.alt
                img.setSrc(rs.getString(8));
                img.setAlt(rs.getString(9));
                
                //img_req_width
                int tmp = rs.getInt(10);
                if (rs.wasNull())
                    img.setWidth(null);
                else
                    img.setWidth(new Integer(tmp));

                //img_req_height
                tmp = rs.getInt(11);
                if (rs.wasNull())
                    img.setHeight(null);
                else
                    img.setHeight(new Integer(tmp));
                
                //_order
                tmp = rs.getInt(12);
                if (rs.wasNull())
                    img.setOrder(null);
                else
                    img.setOrder(new Integer(tmp));
                
                imgs.add(img);
            }
            pstmt.close();
     
            
            iset.images = new Image[imgs.size()];
            imgs.toArray(iset.images);
            
            logger.debug("-");
            return iset; 
            
        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException (e); 
        }
    }
    
    
/**
 * 
 * @param con
 * @param imgSetId
 * @throws CriticalException
 */
    public static void addImage(Connection con, String attributeClass, long imgSetId, String src) 
                        throws CriticalException {
        logger.debug("+");
        
        try {
            PreparedStatement pstmt = con.prepareStatement(addImageFromTemplate);

            pstmt.setLong(1, imgSetId);
            pstmt.setString(2, src);
            pstmt.setLong(3, imgSetId);
            pstmt.setString(4, attributeClass);
            
            pstmt.executeUpdate();
            pstmt.close();
            
        } catch (SQLException e) {
            logger.error("-", e);
            throw new CriticalException(e);
        }
        logger.debug("-");
    }
    
    public static void addFlash(Connection con, String attributeClass, long imgSetId, String src, Long flashHeight, Long flashWidth) 
    throws CriticalException {
    	logger.debug("+");
    	
    	try {
    		PreparedStatement pstmt = con.prepareStatement(addFlashFromTemplate);
    		
    		pstmt.setLong(1, imgSetId);
    		pstmt.setString(2, src);
    		pstmt.setLong(3, flashWidth);
    		pstmt.setLong(4, flashHeight);
    		pstmt.setLong(5, imgSetId);
    		pstmt.setString(6, attributeClass);
    		
    		pstmt.executeUpdate();
    		pstmt.close();
    		
    	} catch (SQLException e) {
    		logger.error("-", e);
    		throw new CriticalException(e);
    	}
    	logger.debug("-");
    }    
}