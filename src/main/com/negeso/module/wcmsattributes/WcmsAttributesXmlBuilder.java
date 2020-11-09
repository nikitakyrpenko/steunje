/*
 * Created on 25.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.negeso.module.wcmsattributes;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.util.FileUtils;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.wcmsattributes.domain.AnimationProperties;
import com.negeso.module.wcmsattributes.domain.Attribute;
import com.negeso.module.wcmsattributes.domain.Image;
import com.negeso.module.wcmsattributes.domain.ImageSet;
import com.negeso.module.wcmsattributes.domain.WcmsAttributeType;

/**
 * @author OLyebyedyev
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class WcmsAttributesXmlBuilder {

    private static Logger logger = Logger
            .getLogger(WcmsAttributesXmlBuilder.class);

    // default value is 9
    private static final int INTERVAL_IMAGES =
        (Env.getProperty("wcms.image.interval") == null ? 9 : Integer.parseInt(Env.getProperty("wcms.image.interval")));
        
    private static final String WCMS_ATTRIBUTES_XML = "wcms_attributes";

    private static final String IMAGE_XML = "image";

    private static final String IMAGE_SET_XML = "image_set";

    private static final String CLASS = "class";

    private static final String DELAY= "delay";
    
    private static final String STEP = "step";    
    
    private static final String SPEEDOFANIMATION= "speed_of_animation";

    private static final String ID = "id";

    private static final String SRC = "src";

    private static final String EXT = "extension"; 

    private static final String ALT = "alt";

    private static final String MAX_WIDTH = "max_width";

    private static final String MAX_HEIGHT = "max_height";

    private static final String WIDTH = "width";

    private static final String HEIGHT = "height";

    private static final String REQUIRED_WIDTH = "required_width";

    private static final String REQUIRED_HEIGHT = "required_height";

    private static final String NUMBER_OF_IMAGES = "number_of_images";
    
    private static final String ORDER = "order";
    
    private static final String LINK = "link";
    
    private static final String TARGET = "target";
    
    /**
     * 
     * @param doc
     * @param con
     * @param attri_set_id
     * @return @throws
     *         CriticalException
     */
    public static Element getAttributes(Document doc, Connection con,
            Long attri_set_id, boolean forVisitor) throws CriticalException {
        logger.debug("+");

        if (doc == null) {
            logger.error("- Parameter Document is null");
            throw new CriticalException("Parameter Document is null");
        }
        if (attri_set_id == null) {
            logger.error("- Parameter attribute_set_id is null");
            throw new CriticalException("Parameter attribute_set_id is null");
        }

        boolean myConnection = false;

        try {
            if (con == null) {
                con = DBHelper.getConnection();
                myConnection = true;
            }

            Attribute[] attrs = Attribute.getAttributes(con, attri_set_id
                    .longValue());
            


            //close connection if I opened it
            if (myConnection) {
                DBHelper.close(con);
                myConnection = false;
                con = null;
            }

            if (attrs == null || attrs.length == 0) {
                logger.debug("- No wcms attributes");
                return null;
            }

            Element wcms_attributes = Xbuilder.createEl(doc, WcmsAttributesXmlBuilder.WCMS_ATTRIBUTES_XML, null);
            wcms_attributes.setAttribute("id", attri_set_id.toString());

            Element imageSetElement = null;
            


            for (int i = 0; attrs != null && i < attrs.length; ++i) {

                ImageSet imgSet = attrs[i].getImageSet();

                
                
                imageSetElement = Xbuilder.createEl(doc, WcmsAttributesXmlBuilder.IMAGE_SET_XML, null);
                
                imageSetElement.setAttribute("id", imgSet.getId().toString());
                
                imageSetElement.setAttribute(WcmsAttributesXmlBuilder.CLASS, attrs[i]
                        .getAttributeClass());

                if(attrs[i].getAttributeClass().equals("animation")){
                	AnimationProperties animaPro = AnimationProperties.findByImageSetId(con,imgSet.getId().longValue());                	                	
                	if (animaPro.getDelay() != null)
                		imageSetElement.setAttribute(WcmsAttributesXmlBuilder.DELAY, animaPro.getDelay().toString());
                	if (animaPro.getStep() != null)
                		imageSetElement.setAttribute(WcmsAttributesXmlBuilder.STEP, animaPro.getStep().toString());
                	if (animaPro.getSpeedOfAnimation() != null)
                		imageSetElement.setAttribute(WcmsAttributesXmlBuilder.SPEEDOFANIMATION, animaPro.getSpeedOfAnimation().toString());
                }
                
                if (imgSet.getRequiredWidth() != null)
                    imageSetElement.setAttribute(
                            WcmsAttributesXmlBuilder.REQUIRED_WIDTH, imgSet
                                    .getRequiredWidth().toString());

                if (imgSet.getRequiredHeight() != null)
                    imageSetElement.setAttribute(
                            WcmsAttributesXmlBuilder.REQUIRED_HEIGHT, imgSet
                                    .getRequiredHeight().toString());

                if (imgSet.getMaxWidth() != null && !forVisitor)
                {
                    int totalWidth = imgSet.getMaxWidth().intValue();
                    int imgsWidth = 0;
                    int idx = 0;
                    for (idx = 0; idx < imgSet.getImages().length; ++idx) {
                        String imgSrc = imgSet.getImages()[idx].getSrc();
                        Long curwidth = 0L;
                        if("swf".equalsIgnoreCase(Repository.get().getImageResource(imgSrc).getExtension())) {
                        	if(imgSet.getImages()[idx].getMaxWidth()!=null) {
                        		imgsWidth += imgSet.getImages()[idx].getMaxWidth();
                        	}
                        } else {
	                        try {
	                        	curwidth = Repository.get().getImageResource(imgSrc).getWidth();
	                            imgsWidth += curwidth.longValue();
	                        }catch (RepositoryException re ) {
	                           //nothing todo
	                        	logger.error("- " + re);
	                        }
                        }
                    }
                    
                    
                        int interval = idx * INTERVAL_IMAGES;
                        int freeSpace = totalWidth - (interval + imgsWidth);
                        if (freeSpace <0) freeSpace = 0;
                        imageSetElement.setAttribute(WcmsAttributesXmlBuilder.MAX_WIDTH,
                                "" + freeSpace);
                }
                
                if (imgSet.getMaxHeight() != null)
                    imageSetElement.setAttribute(WcmsAttributesXmlBuilder.MAX_HEIGHT,
                            imgSet.getMaxHeight().toString());
                
                if (imgSet.getNumberOfImages() != null)
                	imageSetElement.setAttribute(WcmsAttributesXmlBuilder.NUMBER_OF_IMAGES,
                            imgSet.getNumberOfImages().toString());

                /**
                 * 
                 * 
                 * TODO imgset MIN Width and Height
                 */

                wcms_attributes.appendChild(imageSetElement);
                if (attrs[i].getType().getTypeName().equals(WcmsAttributeType.picture.getTypeName())) {

                    Image[] images = attrs[i].getImageSet().getImages();
                    if (images == null || images.length < 1) {
                        logger.error("No images in attribute");
                        return null;
                    }

                    Element imageElement = null;
                    


                    for (int j = 0; j < images.length; ++j) {

                        imageElement = Xbuilder.createEl(doc, WcmsAttributesXmlBuilder.IMAGE_XML, null);
                        if (images[j].getId() == null)
                        {
                            logger.error("Image Id = null");
                            return null;
                        }

                        imageElement.setAttribute(ID, images[j].getId()
                                .toString());
                        imageElement.setAttribute(SRC, images[j].getSrc());
                        imageElement.setAttribute(EXT, FileUtils.getExtension( images[j].getSrc()) ); 
                        imageElement.setAttribute(ALT, images[j].getAlt());                        
                        if (images[j].getMaxWidth() != null)
                            imageElement.setAttribute(MAX_WIDTH, images[j]
                                    .getMaxWidth().toString());

                        if (images[j].getMaxHeight() != null)
                            imageElement.setAttribute(MAX_HEIGHT, images[j]
                                    .getMaxHeight().toString());

                        if (images[j].getWidth() != null) {
                        	imageElement.setAttribute(WIDTH, images[j].getWidth()
                                    .toString());
                        } else {
                        	
                        	if("swf".equalsIgnoreCase(Repository.get().getImageResource(images[j].getSrc()).getExtension())) {
                            	imageElement.setAttribute(
                            			"current-width",
                            			images[j].getMaxWidth().toString());
                            	imageElement.setAttribute(WIDTH, images[j].getMaxWidth().toString());
                            } else {
	                        	try {
	                        		imageElement.setAttribute("current-width", Repository.get().getImageResource(images[j].getSrc()).getWidth()
	                                    .toString());
	                        	}
	                        	catch(RepositoryException e) {
	                        		logger.error("It is impossible to get image width. Width has been setted to max width (from table image) - ", e);
	                        		imageElement.setAttribute("current-width", String.valueOf(images[j].getMaxWidth()));
	                            	imageElement.setAttribute(WIDTH, String.valueOf(images[j].getMaxWidth()));
	                        	}
                        	}
                        }

                        if (images[j].getHeight() != null)
                            imageElement.setAttribute(HEIGHT, images[j].getHeight()
                                    .toString());
                        
                        if (images[j].getOrder() != null)
                        	imageElement.setAttribute(ORDER, images[j].getOrder()
                                    .toString());
                        
                        if ((images[j].getLink() != null) && !"".equals(images[j].getLink())){
                        	imageElement.setAttribute(LINK, images[j].getLink());                        	
                        	imageElement.setAttribute(TARGET, images[j].getTarget());                        	
                        }
                        
                        imageSetElement.appendChild(imageElement);
                    }
                }
            }

            logger.debug("-");
            return wcms_attributes;

        } catch (SQLException e) {
        	logger.error("-", e);
            if (myConnection) {
                DBHelper.close(con);
                con = null;
                myConnection = false;
            }
            throw new CriticalException(e);
        }

    }

}