/*
 * @(#)$Id: ImageStudio.java,v 1.5, 2005-10-19 14:44:01Z, Olexiy Strashko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;


import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.image.ImageTransformException;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.framework.util.Timer;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.domain.ImageResource;
import com.negeso.module.updownload.domain.UploadEnv;

/**
 *
 * ImageHelper domain object
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 6$
 */
public class ImageStudio {
	private static Logger logger = Logger.getLogger(ImageStudio.class);

	public static final String RESIZE_METHOD_PROPORTIONAL = "proportional";
	public static final String RESIZE_METHOD_CROP = "crop";
	public static final String RESIZE_METHOD_NON_PROPORTIONAL = "non_proportional";

	public static final String RESIZE_BY_WIDTH = "by_width";
	public static final String RESIZE_BY_HEIGHT = "by_height";
	public static final String RESIZE_BOTH = "both";

	public static final int DEFAULT_FILE_SET_AMOUNT = 5;

	
    /** Errors collection to simplify error processing  */
	private Collection<String> errors = null;

	
	/**
	 * Validate parameters
	 * 
	 * @param image
	 * @param params
	 */
	private void validateStrictByWidthImageParams(
		ImageResource image, UploadEnv params) 
	{
		logger.debug("+");
	
	    if ( params.getWidth() <= 0 ){
	    	this.getErrors().add("Image width is zero or negative");
		}
	
	    try{
		    if ( ( image.getWidth() == null ) || ( image.getHeight() == null ) ){
		    	this.getErrors().add("File " + image.getCatalogPath() + " is a broken image");
		    }
		    
		    if ( ( image.getWidth().intValue() > params.getMaxImageWidth() ) || 
		    	 ( image.getHeight().intValue() > params.getMaxImageHeight() ) ){
		    	this.getErrors().add(
		    		"Image dimentions are bigger then allowed. " +
		    		"Max width: " + params.getMaxImageWidth() + 
		    		" max height: " + params.getMaxImageHeight() 
				);
		    }

		    if ( params.getHeight() == 0 ){
		    	params.setHeight( image.getHeight().intValue() );
			}
		    
		    if ( params.isStrict() ) {
			    if ( params.getWidth() < image.getWidth().intValue() ){
			    	this.getErrors().add("Image width is less than required width");
			    }
		    }
		    
		    if ( params.getResizeMethod() == null ){
			    if ( !(
			    		(image.getWidth().intValue() == params.getWidth() ) || 
			    	    (image.getWidth().intValue() == (params.getWidth() - 1)	)
					  )
			    ){
			    	this.getErrors().add("Uploaded image width is not equal to required width");
			    }
		
			    if ( image.getHeight().intValue() > params.getHeight() ){
			    	this.getErrors().add("Uploaded image height is greater than maximum allowed");
			    }
			}
	    }
	    catch(RepositoryException e){
	    	this.getErrors().add(e.getMessage());
	    }
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws AccessDeniedException
	 * @throws RequestParametersException
	 * @throws IOException
	 * @throws ImageTransformException
	 * @throws RepositoryException
	 * @throws CriticalException
	 * @throws RepositoryException
	 * @throws ImageTransformException
	 */
	public File processStrictByWidthImage(
		File sourceFile, UploadEnv params) 
	{
		logger.debug("+");
	
	
	    ImageResource image = Repository.get().getImageResource( sourceFile );

	    this.validateStrictByWidthImageParams( image, params );
	    if ( this.hasErrors() ){
	    	return null;
	    	
	    }
	
	    ImageResource thumbnail = null;
	    try{
		    if (logger.isInfoEnabled()){
		        logger.info(
		            "Processing image, mode:" + params.getResizeMethod() +
		            " source w:" + image.getWidth() + " h:" + image.getHeight() + 
		            " th w:" + params.getWidth() + " h:" + params.getHeight() +
		            " is-strict:" + params.isStrict()    
		        );
		    }
		
		    if ( params.getResizeMethod() == null ){
		        return sourceFile;
		    }
		    
			if ( !params.isStrict() ){
		    	if ( image.getWidth().intValue() < params.getWidth() ){
		    	    if ( image.getHeight().intValue() > params.getHeight() ){
		    	        ImageResource temp = image.centerCrop(
		    	        	image.getWidth(), new Long(params.getHeight())
						);
		    	        
		    	        if ( params.canDeleteSource() ){
		    	        	sourceFile.delete();
		    	        }
		    	        image = temp;
		    	    }
			    	return image.getFile();
		    	}
			}
		
		    
		    Timer timer = new Timer();
		    
		    if ( "crop".equals( params.getResizeMethod() ) ){
		        thumbnail = image.centerCrop(
		        	new Long(params.getWidth()), new Long(params.getHeight())
				);
		    }
		    else if ( "non_proportional".equals( params.getResizeMethod() ) ){
			    thumbnail = image.resize(new Long(params.getWidth()), new Long(params.getHeight()));
		    }
		    else{
			    double scaleWidth = image.getWidth().doubleValue() / ((double)params.getWidth());
			    thumbnail = image.resizePropByWidth(
			            new Long(params.getWidth()), 
			            new Long( (long) (image.getHeight().doubleValue() / scaleWidth) )
			    );
		        logger.info(
		            "Prop resize: result image:" +
		            " w:" + thumbnail.getWidth() + 
					" h:" + thumbnail.getHeight() +
		            " srcWidth:" + params.getWidth() + 
		            " scaleWidth:" + scaleWidth + 
					" new Height:" + new Long( (long) (image.getHeight().doubleValue() / scaleWidth))
		        );
		    }
		    logger.info("Resize time:" + timer.stop());
		
		    if ( thumbnail.getHeight().intValue() > params.getHeight() ){
		        ImageResource temp = thumbnail.centerCrop(
		        	new Long(params.getWidth()), new Long(params.getHeight())
				);
		        
		        //if ( params.canDeleteSource() ){
		        thumbnail.getFile().delete();
		        //}
		        //this.deleteFile(request, thumbnail);
	
		        thumbnail = temp;
		    }
		    logger.info("Additional crop time:" + timer.stop());
		    
		    if ( params.canDeleteSource() ){
		        sourceFile.delete();
		    }
	    }
	    catch(RepositoryException e){
	    	this.getErrors().add(e.getMessage());
	    }
	    catch(CriticalException e){
	    	this.getErrors().add(e.getMessage());
	    } 
	    catch (ImageTransformException e) {
	    	this.getErrors().add(e.getMessage());
		}
	    
		logger.debug("-");
		if ( this.hasErrors() ){
			return null;
		}
	    return thumbnail.getFile();
	}
	
	
	/**
	 * Get errors for command
	 * 
	 * @return
	 */
	public Collection<String> getErrors(){
		if ( this.errors == null ){
			this.errors = new ArrayList<String>();
		}
		return this.errors;
	}
	
	/**
	 * Tests for errors
	 * 
	 * @return
	 */
	public boolean hasErrors() {
		if ( this.errors == null ){
			return false;
		}
		if ( this.errors.isEmpty() ){
			return false;
		}
		return true;
	}

}
