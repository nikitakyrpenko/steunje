/*
 * @(#)$Id: ImageResource.java,v 1.6, 2006-06-22 21:28:06Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.domain;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.w3c.dom.Document;

import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.image.ImageTransform;
import com.negeso.framework.image.ImageTransformException;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;

/**
 *
 * Image resource incapsulation
 * 
 * @version		$Revision: 7$
 * @author		Olexiy Strashko
 * 
 */
public class ImageResource extends FileResource {

	private static Logger logger = Logger.getLogger( ImageResource.class );
    
	private ImageInformation imageInfo = null;
	private Boolean isValidImage = null; 
	
	/**
     * @param file
     */
    public ImageResource(File file) {
        super(file);
    }

    /**
     * Override super method to increase perfomance 
     */
    public boolean isImage(){
        if (this.isValidImage == null){
            this.isValidImage = Boolean.valueOf(super.isImage());
        }
        return this.isValidImage.booleanValue();
    }
    
	/**
	 * 
	 * @return
	 */
    @Override
	public Element toDom4jElement(User user, String modeType, String showMode) {
	    Element element = super.toDom4jElement(user, modeType,showMode);

		if ( (modeType == null) ||  
			 (modeType.equalsIgnoreCase(Repository.IMAGE_GALLERY_VIEW_MODE)) ||
			 (modeType.equalsIgnoreCase(Repository.IMAGE_LIST_VIEW_MODE))
		)
		{
			if ( this.isImage() ){
				try{
					element
						.addAttribute("width", this.getWidth().toString())
						.addAttribute("height", this.getHeight().toString())
					;
				}
				catch(RepositoryException e){
					logger.error("- error", e);
				}
			}
		}
		return element;
	}


	/**
	 * Get image information
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	private ImageInformation getImageInfo() throws RepositoryException {
	    logger.debug("+");
	    if ( this.imageInfo == null ){
			if ( !this.isImage() ){
				throw new RepositoryException("Requested file <"	+ 
					this.getName() + "> is not an image"
				);		
			}
	        this.imageInfo = Repository.get().getImageInfo(
	            this.getCatalogPath()
	        );
	    }
	    logger.debug("-");
	    return this.imageInfo;
	}

	/**
	 * Get image width 
	 * @return
	 * @throws RepositoryException
	 */
	public Long getWidth() throws RepositoryException{
	    logger.debug("+-");
		return this.getImageInfo().getWidth();
	}

	/**	
	 * Get image height 
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public Long getHeight() throws RepositoryException{
	    logger.debug("+-");
		return this.getImageInfo().getHeight();
	}
	
	/**
	 * Crop image to width and heigth
	 * 
	 * @param width
	 * @param height
	 * @return
	 * @throws ImageTransformException
	 * @throws CriticalException
	 */
	public ImageResource crop(Long width, Long height) 
		throws ImageTransformException, CriticalException 
	{
	    return this.transform(
	        ImageTransform.RESIZE_MODE_CROP,
		    width,
		    height 
	    );
	}

	/**
	 * Make center crop of the image
	 * 
	 * @param width
	 * @param height
	 * @return
	 * @throws ImageTransformException
	 * @throws CriticalException
	 */
	public ImageResource centerCrop(Long width, Long height) 
		throws ImageTransformException, CriticalException 
	{
	    return this.transform(
	        ImageTransform.RESIZE_MODE_CENTER_CROP,
		    width,
		    height 
	    );
	}

	/**
	 * Resise image non proportional
	 * 
	 * @param width
	 * @param height
	 * @return
	 * @throws ImageTransformException
	 * @throws CriticalException
	 */
	public ImageResource resize(Long width, Long height)
		throws ImageTransformException, CriticalException
	{
	    return this.transform(
	        ImageTransform.RESIZE_MODE_NON_PROPORTIONAL,
		    width,
		    height
	    );
	}
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @return
	 * @throws ImageTransformException
	 * @throws CriticalException
	 */
	public ImageResource resizePropByWidth(Long width, Long height)
	    throws ImageTransformException, CriticalException
	{
	    return this.transform(
	        ImageTransform.RESIZE_MODE_PROPORTIONAL_BY_WIDTH,
		    width,
		    height
	    );
	}

	/**
	 * Transform image operation
	 * 
	 * @param resizeMode
	 * @param width
	 * @param height
	 * @return
	 * @throws ImageTransformException
	 * @throws CriticalException
	 */
	private ImageResource transform(String resizeMode, Long width, Long height)
		throws ImageTransformException, CriticalException
	{
	    try{
		    ImageTransform transformer = new ImageTransform();
	        transformer.setResizeType(resizeMode);
			transformer.setDeleteSource(false);
			File newFile = transformer.transform(
			    this.getFile(),
			    width.doubleValue(),
			    height.doubleValue() 
			);
			return Repository.get().getImageResource(newFile);
	    }
	    catch(IOException e){
	        logger.error("-error", e);
	        throw new CriticalException(e);
	    }
	}
	
	
	public org.w3c.dom.Element getElement(Document doc, User user, String modeType) throws CriticalException {
		org.w3c.dom.Element element = super.getElement(doc, user, modeType);

		if ( (modeType == null) ||  
			 (modeType.equalsIgnoreCase(Repository.IMAGE_GALLERY_VIEW_MODE)) ||
			 (modeType.equalsIgnoreCase(Repository.IMAGE_LIST_VIEW_MODE))
		)
		{
			if ( this.isImage() ){
				try{
                    Xbuilder.setAttr(element, "width", this.getWidth());
                    Xbuilder.setAttr(element, "height", this.getHeight());
				}
				catch(RepositoryException e){
					logger.error("- error", e);
				}
			}
		}
		return element;
	}
    
    
}
