/*
 * @(#)UploadThumbnailCommand.java       @version	15.04.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload.command;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.image.ImageTransformException;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.framework.util.Timer;

import com.negeso.module.media_catalog.Configuration;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.domain.ImageResource;
import com.negeso.module.media_catalog.domain.Resource;


/**
 * Upload thumbnail image. Input: uploaded FileItem image.
 * Result:
 * 	+ saved real image;
 *  + saved thumbnail image	
 *
 * @version 	15.04.2004 
 * @author 		OStrashko
 */
public class UploadThumbnailCommand extends UploadFileCommand {
	private static Logger logger = Logger.getLogger(UploadThumbnailCommand.class);

	// for tree list support
	public static final String INPUT_WIDTH = "width";
	public static final String INPUT_HEIGHT = "height";
	public static final String INPUT_RESIZE_MODE = "resize_mode";
	public static final String INPUT_MODE = "mode";
	public static final String INPUT_TYPE = "type";
    private static final Long UNBOUND_IMAGE_SIZE = new Long(10000);
	
	Long width = null;
	Long height = null;
	String mode = null;
	String type = null;
	String resizeMode = null;
	boolean isStrict = false;
	
	
	/**
	 * Read input parameters
	 * 
	 * @param request
	 */
	private void readParameters(RequestContext request){
	    this.setWidth(request.getLong(INPUT_WIDTH));
	    this.setHeight(request.getLong(INPUT_HEIGHT));
	    this.setType(request.getString(INPUT_TYPE, GetFileUploaderFace.DEFAULT_IMAGE_UPLOAD_TYPE));
	    this.setMode(request.getString(INPUT_MODE, GetFileUploaderFace.IMAGE_MODE));
	    this.setResizeMode(request.getString(INPUT_RESIZE_MODE, null));

	    if ( request.getNonblankParameter( GetFileUploaderFace.IMAGE_UPLOAD_IS_STRICT ) != null ){
	    	this.setStrict(true);
	    }
	}
	
	protected String [] getAllowedExtensions(){
		return Configuration.imageTypeExtensions;
	}
	
	/* (non-Javadoc)
	 * @see com.negeso.framework.command.Command#execute()
	 */
	public ResponseContext execute() {
		logger.debug("+");
	    
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		
//		// pass security check
//		if (!securityCheck(response)){
//			logger.debug("-");
//			return response;
//		}
		
		File thumbnailFile = null;
		Element page = null;
		String errorMessage = null;
		try{

			this.dispatchRequest(request);
			this.readParameters(request);
			
			if (GetFileUploaderFace.IMAGE_UPLOAD_TYPE_STRICT.equalsIgnoreCase(type)){
		        thumbnailFile = this.processStrictImage(request);
			}
			else if (GetFileUploaderFace.IMAGE_UPLOAD_TYPE_BY_HEIGHT.equalsIgnoreCase(type)){
		        thumbnailFile = this.processStrictByHeightImage(request);
			}
			else if (GetFileUploaderFace.IMAGE_UPLOAD_TYPE_BY_WIDTH.equalsIgnoreCase(type)){
		        thumbnailFile = this.processStrictByWidthImage(request);
			}
			else{
			    thumbnailFile = this.getFile();
			}
			
			String pageTitle = "Result";
			if ( this.isThumbnail() ){
				pageTitle = "Upload image result";
 			}
 			else {
				pageTitle = "Upload thumbnail image result";
 			}
			
			page = XmlHelper.createPageElement(request);
			Xbuilder.setAttr(page, "title", pageTitle);		

			if ( this.isThumbnail() ) {
				// create xml			
				Element result = Xbuilder.addEl(page, "thumbnail-upload-result", null);
				Xbuilder.setAttr(
					result, 
					"real-file", 
					Repository.get().getCatalogPath(this.getFile())
				);
				ImageResource res = Repository.get().getImageResource( this.getFile() );
				Xbuilder.setAttr(
					result, 
					"real-file-width", 
					res.getWidth()
				);
				Xbuilder.setAttr(
					result, 
					"real-file-height", 
					res.getHeight()
				);
				

				Xbuilder.setAttr(
					result, 
					"thumbnail-file", 
					Repository.get().getCatalogPath(thumbnailFile)
				);
				res = Repository.get().getImageResource( this.getFile() );
				Xbuilder.setAttr(
					result, 
					"thumbnail-file-width", 
					res.getWidth()
				);
				Xbuilder.setAttr(
					result, 
					"thumbnail-file-height", 
					res.getHeight()
				);
			}
			else {
				// create xml			
				Element result = Xbuilder.addEl(page, "image-upload-result", null);
				Xbuilder.setAttr(
					result, 
					"real-file", 
					Repository.get().getCatalogPath(thumbnailFile)
				);
				ImageResource res = Repository.get().getImageResource( thumbnailFile );
				logger.info(res.getWidth() + ":" + res.getHeight());
				Xbuilder.setAttr(
					result, 
					"real-file-width", 
					res.getWidth()
				);
				Xbuilder.setAttr(
					result, 
					"real-file-height", 
					res.getHeight()
				);
			}
			response.setResultName(RESULT_SUCCESS);
			response.getResultMap().put(OUTPUT_XML, page.getOwnerDocument());
			logger.debug("-");
			return response;
		}
		catch (RequestParametersException e) {
		    errorMessage = e.getMessage();
        }
		catch (RepositoryException e) {
		    errorMessage = e.getMessage();
        } 
		catch (ImageTransformException e) {
		    errorMessage = e.getMessage();
        } 
		catch (IOException e){
			response.setResultName(RESULT_FAILURE);
			logger.error("-", e);
			return response;
		} 
		catch (AccessDeniedException e) {
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.error("-", e);
			return response;
        } 
		catch (CriticalException e) {
			response.setResultName(RESULT_FAILURE);
			logger.error("-", e);
			return response;
        } 

		
		GetFileUploaderFace command = new GetFileUploaderFace();
		request.setParameter("error_message", errorMessage);
		command.setRequestContext(request);
		logger.debug("-");
		return command.execute();
	}

	
	private void validateStrictImageParams(RequestContext request) 
		throws RepositoryException, RequestParametersException
	{
		logger.debug("+");
	    if ( this.getWidth() == null ){
			logger.debug("-");
			throw new RequestParametersException("Missing required parameter: width");
		}

	    if ( this.getHeight() == null ){
			logger.debug("-");
			throw new RequestParametersException("Missing required parameter: height");
		}

	    ImageResource image = Repository.get().getImageResource(this.getFile());
        long reqWidth = this.getWidth().longValue(); 
        long reqHeight = this.getHeight().longValue();

        long imageWidth = image.getWidth().longValue(); 
        long imageHeight = image.getHeight().longValue();
        
        
        if ( imageWidth < (reqWidth - 1) ){
            logger.debug("-");
            throw new RequestParametersException("Uploaded image width is less then required");
        }

        if ( imageHeight < (reqHeight - 1) ){
            logger.debug("-");
            throw new RequestParametersException("Uploaded image height is less then required");
        }

	    // Validate params if resize mode is null (NO RESIZE)
	    if ( this.getResizeMode() == null ){
		    if ( !image.getWidth().equals(this.getWidth()) ){
				throw new RequestParametersException("Uploaded image width is not equal to required width");
		    }
		    if ( !image.getHeight().equals(this.getHeight()) ){
				throw new RequestParametersException("Uploaded image heigth is not equal to required height");
		    }
		}
		logger.debug("-");
	}
	
	private void deleteFile(RequestContext request, Resource file) 
		throws CriticalException
	{
	    try{
	        file.delete(request.getSession().getUser());
	    }
	    catch(AccessDeniedException e){
	        logger.warn("ACCESS DENIED: Unablie to delete file: " + file.getCatalogPath());
	    }
	    catch(RepositoryException  e){
	        logger.warn("Repository Exception: " + e.getMessage() + " file: " + file.getCatalogPath());
	    }
	}
	
    /**
     * @param request
     * @throws ImageTransformException
     * @throws CriticalException
     * @throws 
     */
    private File processStrictImage(RequestContext request) 
    	throws AccessDeniedException, RepositoryException, 
    	RequestParametersException, IOException, ImageTransformException, CriticalException 
    {
		logger.debug("+");
		
		this.validateStrictImageParams(request);

	    if ( this.getResizeMode() == null ){
			logger.debug("-");
		    return this.getFile();
		}
	    
	    ImageResource image = Repository.get().getImageResource(this.getFile());
	    ImageResource thumbnail = null;
	    
        
        long reqWidth = this.getWidth().longValue(); 
        long reqHeight = this.getHeight().longValue();

        long imageWidth = image.getWidth().longValue(); 
        long imageHeight = image.getHeight().longValue();

        if ( (imageWidth == reqWidth) || ((imageWidth + 1) == reqWidth) ){
            if ( (imageHeight == reqHeight) || ((imageHeight + 1) == reqHeight) ){
                logger.debug("-");
                return image.getFile();
            }
        }
	    
	    if ( "crop".equals( this.getResizeMode() ) ){
	        thumbnail = image.centerCrop(this.getWidth(), this.getHeight());
	    }
	    else if ( "non_proportional".equals( this.getResizeMode() ) ){
	        thumbnail = image.resize(this.getWidth(), this.getHeight());
	    }
	    else{
	    	//Proportional resize
		    double scaleWidth = image.getWidth().doubleValue() / this.getWidth().doubleValue();
		    double scaleHeight = image.getHeight().doubleValue() / this.getHeight().doubleValue();

		    if ( scaleWidth < scaleHeight ){
		        thumbnail = image.resizePropByWidth(
		            this.getWidth(), 
		            new Long( (long) (image.getHeight().doubleValue() / scaleWidth) )
		        );
		    }
		    else{
		        thumbnail = image.resizePropByWidth(
		            new Long( (long) (image.getWidth().doubleValue() / scaleHeight) ),
		            this.getHeight()
		        ); 
		    }
		    if ( 
		        (image.getWidth().compareTo(this.getWidth()) > 0) ||
		    	(image.getHeight().compareTo(this.getHeight()) > 0)
		    ){
		        logger.info("additional cropping");
		        ImageResource temp = thumbnail.centerCrop(this.getWidth(), this.getHeight());
		        this.deleteFile(request, thumbnail);
		        thumbnail = temp;
		    }
	    }

		if ( this.isDeleteSourceImage() ){
		    this.deleteFile(request, image);
		}
		
		logger.debug("-");
		return thumbnail.getFile();
    }
    

	private void validateStrictByHeightImageParams(RequestContext request) 
		throws RepositoryException, RequestParametersException
	{
		logger.debug("+");
	    if ( this.getHeight() == null ){
			throw new RequestParametersException("Image height is missing");
		}

	    if ( this.getHeight().intValue() <= 0 ){
			throw new RequestParametersException("Image height is zero or negative");
		}

	    if ( this.getWidth() == null ){
	        this.setWidth(UNBOUND_IMAGE_SIZE);
		}

	    ImageResource image = Repository.get().getImageResource(this.getFile());

	    if ( this.getHeight().compareTo(image.getHeight()) > 0  ){
			throw new RequestParametersException("Image height is less than required height");
	    }
	    
	    if ( this.getResizeMode() == null ){
		    if ( !image.getWidth().equals(this.getWidth()) ){
				throw new RequestParametersException("Uploaded image width is not equal to required width");
		    }

		    if ( image.getHeight().compareTo(this.getHeight()) > 0  ){
				throw new RequestParametersException("Uploaded image height is greater than maximum allowed");
		    }
		}
	}


    /**
     * 
     * @param request
     * @return
     * @throws AccessDeniedException
     * @throws RepositoryException
     * @throws RequestParametersException
     * @throws IOException
     * @throws ImageTransformException
     * @throws CriticalException
     */
    private File processStrictByHeightImage(RequestContext request) 
    	throws AccessDeniedException, RepositoryException, 
		RequestParametersException, IOException, ImageTransformException, CriticalException 
	{
		logger.debug("+");
		this.validateStrictByHeightImageParams(request);		

	    ImageResource image = Repository.get().getImageResource(this.getFile());

	    if ( this.getResizeMode() == null ){
	        return this.getFile();
	    }

	    if (logger.isInfoEnabled()){
	        logger.info(
                "Processing image, mode:" + this.getResizeMode() +
                " source w:" + image.getWidth() + " h:" + image.getHeight() + 
                " th w:" + this.getWidth() + " h:" + this.getHeight()  
	        );
	    }
	    
	    ImageResource thumbnail = null;

	    double scaleHeight = image.getHeight().doubleValue() / this.getHeight().doubleValue();
	    Long newWidth = new Long( (long) (image.getWidth().doubleValue() / scaleHeight) );
	    if ( "crop".equals( this.getResizeMode() ) ){
	        if ( newWidth.compareTo(this.getWidth()) > 0){
	            newWidth = this.getWidth();
	        }
	        thumbnail = image.centerCrop(
	    		newWidth,
	            this.getHeight()
	        );
	    }
	    else if ( "non_proportional".equals( this.getResizeMode() ) ){
	        if ( newWidth.compareTo(this.getWidth()) > 0){
	            newWidth = this.getWidth();
	        }
		    thumbnail = image.resize(
	    		newWidth,
	            this.getHeight()
		    );
	    }
	    else{
		    thumbnail = image.resize(
	    		newWidth,
	            this.getHeight() 
		    );

	        logger.info(
                "thumbnail image:" +
                " w:" + thumbnail.getWidth() + " h:" + thumbnail.getHeight()  
	        );
	    }

	    if ( thumbnail.getWidth().compareTo(this.getWidth()) > 0 ){
	        ImageResource temp = thumbnail.centerCrop(this.getWidth(), this.getHeight());
		    this.deleteFile(request, thumbnail);
	        thumbnail = temp;
	    }
	    
	    if (this.isDeleteSourceImage()){
		    this.deleteFile(request, image);
	    }
	    
		logger.debug("-");
	    return thumbnail.getFile();
    }
    

	private void validateStrictByWidthImageParams(RequestContext request) 
		throws RepositoryException, RequestParametersException
	{
		logger.debug("+");
	    if ( this.getWidth() == null ){
			throw new RequestParametersException("Image width is missing");
		}

	    if ( this.getWidth().intValue() <= 0 ){
			throw new RequestParametersException("Image width is zero or negative");
		}


	    ImageResource image = Repository.get().getImageResource(this.getFile());

	    if ( this.getHeight() == null ){
	        this.setHeight(image.getHeight());
		}
	    
	    if ( this.isStrict() ) {
		    if ( this.getWidth().compareTo(image.getWidth()) > 0  ){
				throw new RequestParametersException("Image width is less than required width");
		    }
	    }
	    
	    if ( this.getResizeMode() == null ){
		    if ( !image.getWidth().equals(this.getWidth()) ){
				throw new RequestParametersException("Uploaded image width is not equal to required width");
		    }

		    if ( image.getHeight().compareTo(this.getHeight()) > 0  ){
				throw new RequestParametersException("Uploaded image height is greater than maximum allowed");
		    }
		}
	}

	/**
     * 
     * @param request
     * @return
     * @throws AccessDeniedException
     * @throws RepositoryException
     * @throws RequestParametersException
     * @throws IOException
	 * @throws ImageTransformException
	 * @throws CriticalException
     */
    private File processStrictByWidthImage(RequestContext request) 
    	throws AccessDeniedException, RepositoryException, 
		RequestParametersException, IOException, ImageTransformException, CriticalException 
	{
		logger.debug("+");


		this.validateStrictByWidthImageParams(request);		

	    ImageResource image = Repository.get().getImageResource(this.getFile());

	    if (logger.isInfoEnabled()){
	        logger.info(
                "Processing image, mode:" + this.getResizeMode() +
                " source w:" + image.getWidth() + " h:" + image.getHeight() + 
                " th w:" + this.getWidth() + " h:" + this.getHeight() +
                " is-strict:" + this.isStrict()    
	        );
	    }

	    if ( this.getResizeMode() == null ){
	        return this.getFile();
	    }
	    
		if ( !this.isStrict() ){
	    	if ( image.getWidth().compareTo(this.getWidth()) < 1 ){
	    	    if ( image.getHeight().compareTo(this.getHeight()) > 0 ){
	    	        ImageResource temp = image.centerCrop(image.getWidth(), this.getHeight());
	    	        this.deleteFile(request, image);
	    	        image = temp;
	    	    }
		    	return image.getFile();
	    	}
		}

	    
	    ImageResource thumbnail = null;
	    
	    Timer timer = new Timer();
	    
	    if ( "crop".equals( this.getResizeMode() ) ){
	        thumbnail = image.centerCrop(this.getWidth(), this.getHeight());
	    }
	    else if ( "non_proportional".equals( this.getResizeMode() ) ){
		    thumbnail = image.resize(this.getWidth(), this.getHeight());
	    }
	    else{
		    double scaleWidth = image.getWidth().doubleValue() / this.getWidth().doubleValue();
		    thumbnail = image.resizePropByWidth(
		            this.getWidth(), 
		            new Long( (long) (image.getHeight().doubleValue() / scaleWidth) )
		    );
	        logger.info(
                "Prop resize: result image:" +
                " w:" + thumbnail.getWidth() + 
				" h:" + thumbnail.getHeight() +
                " srcWidth:" + this.getWidth() + 
                " scaleWidth:" + scaleWidth + 
				" new Height:" + new Long( (long) (image.getHeight().doubleValue() / scaleWidth))
	        );
	    }
	    logger.info("Resize time:" + timer.stop());

	    if ( thumbnail.getHeight().compareTo(this.getHeight()) > 0 ){
	        ImageResource temp = thumbnail.centerCrop(this.getWidth(), this.getHeight());
	        this.deleteFile(request, thumbnail);
	        thumbnail = temp;
	    }
	    logger.info("Additional crop time:" + timer.stop());
	    
	    if (this.isDeleteSourceImage()){
	        this.deleteFile(request, image);
	    }
	    
		logger.debug("-");
	    return thumbnail.getFile();
    }

    public boolean isThumbnail(){
        if ( GetFileUploaderFace.THUMBNAIL_MODE.equalsIgnoreCase(this.getMode()) ){
            return true;
        }
        return false;
    }
    
    /**
     * Tests if request parameters force to delete source image
     * 
     * @return
     */
    public boolean isDeleteSourceImage(){
        if ( this.isThumbnail() ){
            return false;
        }
        if ( GetFileUploaderFace.INPUT_SOURCE_EXISTENT.equalsIgnoreCase(this.getInputSource()) ) {
            return false;
        }
        return true;
    }
    
    /**
     * @return Returns the height.
     */
    public Long getHeight() {
        return height;
    }
    /**
     * @param height The height to set.
     */
    public void setHeight(Long height) {
        this.height = height;
    }
    /**
     * @return Returns the mode.
     */
    public String getMode() {
        return mode;
    }
    /**
     * @param mode The mode to set.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }
    /**
     * @return Returns the resizeMode.
     */
    public String getResizeMode() {
        return resizeMode;
    }
    /**
     * @param resizeMode The resizeMode to set.
     */
    public void setResizeMode(String resizeMode) {
        this.resizeMode = resizeMode;
    }
    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * @return Returns the width.
     */
    public Long getWidth() {
        return width;
    }
    /**
     * @param width The width to set.
     */
    public void setWidth(Long width) {
        this.width = width;
    }
	/**
	 * @return Returns the isStrict.
	 */
	public boolean isStrict() {
		return isStrict;
	}
	/**
	 * @param isStrict The isStrict to set.
	 */
	public void setStrict(boolean isStrict) {
		this.isStrict = isStrict;
	}
}
