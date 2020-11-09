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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.image.ImageInfo;
import com.negeso.framework.image.ImageTransformException;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.util.RequestParametersException;

import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.domain.FileResource;
import com.negeso.module.media_catalog.domain.ImageResource;
import com.negeso.module.media_catalog.domain.Resource;


public class UploadBannerCommand extends UploadFileCommand {

	private static Logger logger = Logger.getLogger(UploadBannerCommand.class);
	
	private static final String GIF_STRING = "GIF";
	private static final String JPG_STRING = "JPG";
	private static final String SWF_STRING = "SWF";
	
	public enum ImageType {

		//IMAGE("image"),
		//FLASH("flash");
		
		IMAGE("1"),
		FLASH("2");
		
		private String key;
		
		ImageType(String key) {
			this.key = key;
		}

		String getKey() {
			return key;
		}
	}
	
	public static final String[] SUPPORTED_IMAGE_EXTENSIONS = {
		GIF_STRING, JPG_STRING,
	};
	
	public static final String[] SUPPORTED_FLASH_EXTENSIONS = {
		SWF_STRING,
	};
	
	public static final String[] ALLOWABLE_EXTENSIONS = {
		SUPPORTED_IMAGE_EXTENSIONS[0],
		SUPPORTED_IMAGE_EXTENSIONS[1],
		SUPPORTED_FLASH_EXTENSIONS[0],
	};
	
	public static final String INPUT_WIDTH = "width";
	public static final String INPUT_HEIGHT = "height";
	public static final String INPUT_RESIZE_MODE = "resize_mode";
	
	Long width = null;
	Long height = null;
	String resizeMode = null;
	
	private void readInputParameters(RequestContext request) {
	    width = request.getLong(INPUT_WIDTH);
	    height = request.getLong(INPUT_HEIGHT);
	    resizeMode = request.getString(INPUT_RESIZE_MODE, null);
	}

	private void validateInputParameters() throws RequestParametersException {
	    if (width == null)
			throw new RequestParametersException("Image width is missing");
	    if (width.intValue() <= 0 )
			throw new RequestParametersException("Image width is zero or negative");
	}
	
	protected String [] getAllowedExtensions() {
		return ALLOWABLE_EXTENSIONS;
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
		
		Element page = null;
		String errorMessage = null;
		try {

			readInputParameters(request);
			validateInputParameters();
			dispatchRequest(request);

			String pageTitle = "Upload banner result";
			page = XmlHelper.createPageElement(request);
			Xbuilder.setAttr(page, "title", pageTitle);		

			Element result = Xbuilder.addEl(page, "banner-upload-result", null);
			
			File bannerFile = processBanner(request, result);
			
			Xbuilder.setAttr(
				result, 
				"file", 
				Repository.get().getCatalogPath(bannerFile)
			);

			Xbuilder.setAttr(result, "file-width", request.getNonblankParameter("width"));
			Xbuilder.setAttr(result, "file-height", request.getNonblankParameter("height"));
			
			response.setResultName(RESULT_SUCCESS);
			response.getResultMap().put(OUTPUT_XML, page.getOwnerDocument());
			
			logger.debug("-");
			
			return response;
		}
		catch (RequestParametersException e) {
		    deleteInputFileQuietly(request);
		    errorMessage = e.getMessage();
        }
		catch (RepositoryException e) {
			deleteInputFileQuietly(request);
		    errorMessage = e.getMessage();
        } 
		catch (ImageTransformException e) {
			deleteInputFileQuietly(request);
		    errorMessage = e.getMessage();
        } 
		catch (IOException e){
			deleteInputFileQuietly(request);
			response.setResultName(RESULT_FAILURE);
			logger.error("-", e);
			return response;
		} 
		catch (AccessDeniedException e) {
		    deleteInputFileQuietly(request);
			response.setResultName(RESULT_ACCESS_DENIED);
			logger.error("-", e);
			return response;
        } 
		catch (CriticalException e) {
		    deleteInputFileQuietly(request);
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

	private ImageInfo getImageInfo() throws RequestParametersException {
	    InputStream is = null;
	    try {
	    	is = new FileInputStream(getFile());
	    	ImageInfo imageInfo = new ImageInfo();
	    	imageInfo.setInput(is);
	    	imageInfo.setDetermineImageNumber(true);
	    	if (!imageInfo.check())
	    		throw new RequestParametersException(String.format("wrong image, file %s", getFile()));
	    	return imageInfo;
	    } catch (IOException e) {
	    	throw new RequestParametersException(String.format("wrong image, file %s", getFile()), e);
	    } finally {
	    	IOUtils.closeQuietly(is);
	    }
	}

	private void validateImage(RequestContext request, String extension) 
		throws RepositoryException, RequestParametersException
	{
		logger.debug("+");

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
		
	    if ( this.getResizeMode() == null || this.getResizeMode().equals("none")) {
		    if (imageWidth != reqWidth)
				throw new RequestParametersException("Uploaded image width is not equal to required width");
		    if (imageHeight != reqHeight)
				throw new RequestParametersException("Uploaded image heigth is not equal to required height");
		} else {
			if (GIF_STRING.equals(extension)) { // check whether gif is animated 
				ImageInfo imageInfo = getImageInfo();
				int imageNum = imageInfo.getNumberOfImages();
				if (imageNum > 0) {
		            throw new RequestParametersException("Animated gif image cannot be resized");
				}
			}
		}
        
		logger.debug("-");
	}
	
    private File processBanner(RequestContext request, Element element)
    throws AccessDeniedException, 
    CriticalException, RepositoryException, RequestParametersException,
    IOException, ImageTransformException {
    	String filename = getFile().getName().toUpperCase();
    	String extension = filename.substring(filename.lastIndexOf(".") + 1);
    	if (extension == null)
    		throw new RequestParametersException(
    				String.format("Wrong banner file extension (file: %s)",
    						filename));
    	
    	for (String s : SUPPORTED_IMAGE_EXTENSIONS) {
    		if (s.equals(extension)) {
    			Xbuilder.setAttr(element, "imageType", ImageType.IMAGE.getKey());
    			return processImage(request, extension);
    		}
    	}
    	for (String s : SUPPORTED_FLASH_EXTENSIONS) {
    		if (s.equals(extension)) {
    			Xbuilder.setAttr(element, "imageType", ImageType.FLASH.getKey());
    			return processFlash(request);
    		}
    	}
		throw new RequestParametersException(
				String.format("Wrong banner file extension (file: %s)",
						filename));
    }
	
	
    /**
     * @param request
     * @throws ImageTransformException
     * @throws CriticalException
     * @throws 
     */
    private File processImage(RequestContext request, String extension) 
    	throws AccessDeniedException, RepositoryException, 
    	RequestParametersException, IOException, ImageTransformException, CriticalException 
    {
		logger.debug("+");
		
		validateImage(request, extension);

	    if ( this.getResizeMode() == null ){
			logger.debug("-resize mode is null");
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
                logger.debug("-no resize");
                return image.getFile();
            }
        }
	    
        if ("none".equals(getResizeMode())) {
        	return image.getFile();
        } else {
	    	// Proportional resize
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

//		if ( this.isDeleteSourceImage() ){
//		    this.deleteFile(request, image);
//		}
		
		logger.debug("-");
		return thumbnail.getFile();
    }

    private File processFlash(RequestContext request) {
    	// just a file
    	return getFile();
    }

    public void deleteInputFileQuietly(RequestContext request) {
    	logger.debug("+");
    	if (getFile() != null) {
			FileResource resource = Repository.get().getFileResource(getFile());
			deleteFile(request, resource);
    	}
    	logger.debug("-");
    }
    
	private void deleteFile(RequestContext request, Resource file)
			throws CriticalException {
		try {
			file.delete(request.getSession().getUser());
		} catch (AccessDeniedException e) {
			logger.warn("ACCESS DENIED: Unablie to delete file: "
					+ file.getCatalogPath());
		} catch (RepositoryException e) {
			logger.warn("Repository Exception: " + e.getMessage() + " file: "
					+ file.getCatalogPath());
		}
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
}
