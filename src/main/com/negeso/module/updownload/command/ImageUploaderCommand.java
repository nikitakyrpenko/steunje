/*
 * @(#)$Id: ImageUploaderCommand.java,v 1.22.1.0, 2007-04-13 07:12:26Z, Alexander Serbin$
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
import java.util.Random;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DOMWriter;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.image.ImageTransform;
import com.negeso.framework.image.ImageTransformException;
import com.negeso.framework.page.PageH;
import com.negeso.framework.page.PageService;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.util.FileUtils;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.module.media_catalog.Configuration;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.domain.ImageResource;
import com.negeso.module.updownload.UpdownloadXmlBuilder;

/**
 * Upload thumbnail image. Input: uploaded FileItem image.
 * Result:
 * 	+ saved real image;
 *  + saved thumbnail image	
 *
 * @version 	$Revision: 24$
 * @author 		Olexiy Strashko
 */
public class ImageUploaderCommand extends UploadFileCommand {
    
    
	private static Logger logger = Logger.getLogger(ImageUploaderCommand.class);

	
	public static final String INPUT_HOST_LINK = "hostLink";
	public static final String INPUT_IMAGE_THFILE = "selectedThFile";
	public static final String INPUT_EXISTENT_LARGEFILE = "existentLarge";
	public static final String INPUT_EXISTENT_SMALLFILE = "existentSmall";

	public static final String INPUT_THUMB_WIDTH = "thumbwidth";
	public static final String INPUT_THUMB_HEIGHT = "thumbheight";
    private static final String INPUT_LAYOUT_ALIGN = "layout_align";
    private static final String INPUT_LAYOUT_BORDER = "layout_border";
    private static final String INPUT_LAYOUT_VPADD = "layout_vpadd";
    private static final String INPUT_LAYOUT_HPADD = "layout_hpadd";
    private static final String INPUT_OPTIMIZE_FLAG = "is_optimize";
    private static final String INPUT_THUMBNAIL_FLAG = "is_thumbnail";
    private static final String INPUT_RESIZE_FLAG = "is_resize";
    private static final String INPUT_RESIZE_MODE = "resize_mode";
    private static final String INPUT_ALT_TEXT = "alt_text";
    
    private static final String INPUT_RESIZE_MODE_CROP = "crop";
    //private static final String INPUT_RESIZE_MODE_PROPORTIONAL = "proportional";


	private String layoutAlign = null;
    private String layoutBorder = null;
    private String layoutVPadd = null;
    private String layoutHPadd = null;
    private String altText = null;
    private boolean isPopup = true;

    private boolean isOptimized = false;
    private boolean isResize = false;
    private boolean isResizeProportional = false;
    private boolean isThumbnail = false;
    private Long thumbWidth = new Long(100);
    private Long thumbHeight = new Long(100);

	private File thumbnailFile = null;
	
	protected String [] getAllowedExtensions(){
		String[] imageTypeExtensions = {
			"gif", "jpg", "jpeg", "png" //, "bmp", "psd" 
		};

		return imageTypeExtensions;
	}
	
    
	public ResponseContext execute() {
		RequestContext request = getRequestContext();
		ResponseContext response = new ResponseContext();
		//Map resultMap = response.getResultMap();
		
		// pass security check
		if (!securityCheck(response)){
			logger.debug("-");
			return response;
		}
		
		try{
			boolean success = false;
			String errorMessage = null;

			String pageTitle = "Result";
			Element page = UpdownloadXmlBuilder.get().getPageDom4j(pageTitle);
			page.addAttribute("type", "image-upload-result");
			try{
				// process image width			
			    
			    this.readLayoutParameters(request);
			    this.readConvertParameters(request);

			    this.readWorkingFolder(request, INPUT_WORKING_FOLDER);
				//this.dispatchRequest(request);

				//page.addAttribute("type", "thumbnail-upload-result");
				Element retHtml =  UpdownloadXmlBuilder.get().createElement("ret-html");

			    if ( !this.isThumbnail() ){
			        //
                    // process single image 
                    //
                    
				    // read file
				    if (this.isDiskSource()){
				        this.setFile(
				            this.readUploadedFile(request, INPUT_IMAGE_FILE)
				        );
				    }
				    else{
				        this.setFile(
				            this.readExistentFile(request, INPUT_EXISTENT_FILE)
				        );
				    }
				    
				    // Convert image 
					if (this.isResize() || this.isOptimized()){
					    if (this.getThumbWidth() == null){
					        this.fillImageDimentions(this.getFile());
					    }
						this.setFile(this.transformFile(this.getFile()));
					}
			        retHtml.setText(this.buildImageHtml(this.getFile()));
				}
				else{
                    //
                    // process thumbnail image 
                    //
                    if (this.isDiskSource()){
                        // source file: uploaded
				        this.setFile(
				            this.readUploadedFile(request, INPUT_IMAGE_THFILE)
				        );
				        //logger.info("!!!File:" + this.getFile());
					    if (this.getThumbWidth() == null){
					        this.fillImageDimentions(this.getFile());
					    }
						this.setThumbnailFile(this.transformFile(this.getFile()));
				    }
				    else{
                        // source file: existent 
				        
                        // get the large image
                        this.setFile(
					        this.readExistentFile( request, INPUT_EXISTENT_LARGEFILE )
					    );
                        try{
                            // get the small image
    				        this.setThumbnailFile(
    				            this.readExistentFile( request, INPUT_EXISTENT_SMALLFILE )
    				        );
                        }
                        catch( RequestParametersException e ){
                            // thumbnail is not set, make new
                            this.setThumbnailFile( this.transformFile(this.getFile()) );
                        }
				    }
					// create xml
				    retHtml.setText(this.buildThumbnailHtml(request));
				}
			    page.add(retHtml);
				success = true;
			}
			catch (RequestParametersException e){
				logger.debug("e+", e);
				errorMessage = e.getMessage();
			}
			catch (ImageTransformException e){
				logger.error("e+", e);
				errorMessage = e.getMessage();
			} 
			catch (AccessDeniedException e) {
				logger.error("e+", e);
				errorMessage = e.getMessage(
				    request.getSession().getInterfaceLanguageCode()
				);
            }
			
			if (!success){
				// ERROR TEXT
			    request.setParameter("error_message", errorMessage);
			    GetImageUploaderFace command = new GetImageUploaderFace();
			    command.setRequestContext(request);
			    return command.execute();
			}
			response.setResultName(RESULT_SUCCESS);

			Document doc = DocumentHelper.createDocument(page);
			org.w3c.dom.Document domDoc = null;
			try{
			    DOMWriter writer = new DOMWriter();
			    domDoc = writer.write(doc);
			}
			catch(DocumentException e){
				response.setResultName(RESULT_FAILURE);
				logger.error("-", e);
				return response;
			}
			response.getResultMap().put(OUTPUT_XML, domDoc);
		}
		catch (CriticalException e){
			response.setResultName(RESULT_FAILURE);
			logger.error("-", e);
			return response;
		}
		catch (RepositoryException e){
			response.setResultName(RESULT_FAILURE);
			logger.error("-", e);
			return response;
		}
		catch (IOException e){
			response.setResultName(RESULT_FAILURE);
			logger.error("-", e);
			return response;
		}
		return response;
	}
	
    /**
     * @param file
     * @throws RepositoryException
     */
    private void fillImageDimentions(File file) throws RepositoryException {
        ImageResource res = Repository.get().getImageResource(file); 
        this.setThumbWidth(res.getWidth());
        this.setThumbHeight(res.getHeight());
    }

    /**
     * Creates a page of type popup and a menu item in Special pages
     * which points to the new popup page.
     * 
     * @param req       HttpServletRequest object is used to create
     *                  RequestContext to run a command.
     * @param text      text of the article in the new popup
     * @param width     popup width (in db it is stored in article head)
     * @param height    popup height (in db it is stored in article head)
     * @return      filename of popup
     */
    private String createPopup(
            RequestContext request, String text, int width, int height)
    {
        String popupName = makeUniqueName(this.getFile().getName());        
        PageH page = new PageH();
        page.setClass_("popup");
        page.setCategory("popup"); 
        page.setTitle("Image");
        page.setContents(text);
        page.setFilename(popupName);
        PageService.getInstance().createPage(page, "popup");
        return popupName;
    }
    
    /**
     * Generates a filename which is not in table 'page' yet. Generated names
     * look like "[image basename] + [1...100] + .html"
     * 
     * @param imageName basename (the part name without extension) of 
     *                  imageName will be used when generating
     *                  popup name
     * @return      unique page filename
     * @throws      Exception if a unique name cannot be generated on some
     *              reason
     */
    private String makeUniqueName(String imageName) 
    	throws CriticalException
    {
        logger.debug("+");
        int extension = imageName.indexOf(".");
        String basename = imageName.substring(0, extension);
        Random random = new Random();
        for(int i = 0; i < 10; i++){
            String filename = basename + (random.nextInt(100) + 1) + ".html";
            if(PageService.getInstance().findByFileName(filename) == null){
                return filename;
          
            }
        }
        throw new CriticalException("Faild to generate unique filename");
    }
	
	/**
	 * 
	 * @param request
	 * @param paramName
	 * @return 		Transformed file
	 * @throws ImageTransformException
	 * @throws CriticalException
	 * @throws IOException
	 * @throws 
	 */
	private File transformFile(File targetFile) 
		throws ImageTransformException, CriticalException, IOException
	{
	    ImageTransform transformer = new ImageTransform();
		// Choose image/thumbnail mode ands setup removing source file
		if ( this.isDiskSource() && ( ! this.isThumbnail()) )
		{
			// file uploaded from client and mode is image : remove source file
			transformer.setDeleteSource(true);
		}
		else{
			transformer.setDeleteSource(false);
		}
		
		if (this.isResizeProportional()){
		    transformer.setResizeType("resizeProp");
		}
		else{
		    transformer.setResizeType("centerCrop");
		}
		
		if (this.isOptimized()){
		    transformer.setDescrease(true);
		}
		
		if (this.getFile() == null){
            throw new CriticalException("Target Image is null");
		}
        if ( logger.isInfoEnabled() ){
            logger.info(
                "w:" + this.getThumbWidth().intValue() + 
                " h:" + this.getThumbHeight().intValue()
            );
        }
		return transformer.transform(
		        targetFile, 
		        this.getThumbWidth().intValue(),
		        this.getThumbHeight().intValue()
		);
	}

	/**
	 * 
	 * @param request
	 * @param paramName
	 * @return
	 * @throws RequestParametersException
	 * @throws AccessDeniedException
	 */
	private File readUploadedFile(RequestContext request, String paramName) 
		throws AccessDeniedException, RequestParametersException
	{
		// use upload file 
		FileItem fileItem = request.getFile(paramName);
		if (fileItem == null) {
			throw new RequestParametersException(
				"Uploaded file is empty. Unable to perform operation"
			);
		}

		return Repository.get().saveFileItemSafe(
		    request.getSession().getUser(),
			fileItem, 
			this.getWorkingFolder(),
			this.getAllowedExtensions(),
			true
		); 
	}
	
	/**
	 * 
	 * @param request
	 * @param paramName
	 * @return
	 * @throws RequestParametersException
	 * @throws CriticalException
	 */
	private File readExistentFile(RequestContext request, String paramName) 
		throws RequestParametersException
	{
		String existentFile = request.getParameter(paramName);
		if ( (existentFile == null) || ("none".equalsIgnoreCase(existentFile)) ){
			throw new RequestParametersException(
				"Existent file is not set. Choose another one"
			);
		}
		return Repository.get().getResource(existentFile).getFile();
	}
	
	/**
	 * 
	 * @param request
	 * @param paramName
	 * @throws RequestParametersException
	 * @throws CriticalException
	 */
	private void readWorkingFolder(RequestContext request, String paramName) 
	{
		// process working folder			
		this.setWorkingFolder( request.getParameter(paramName) );

		if (this.getWorkingFolder() == null){
			this.setWorkingFolder( Repository.get().getRootPath() );
		}

		FileUtils.createDirIfNotExists(Repository.get().getRealPath(
			this.getWorkingFolder()
		));
	}

	/**
	 * Read layout parameters to local attributes
	 * 
	 * @param request
	 */
	private void readLayoutParameters(RequestContext request){
	    this.setLayoutAlign(request.getNonblankParameter(INPUT_LAYOUT_ALIGN));
	    this.setLayoutBorder(request.getNonblankParameter(INPUT_LAYOUT_BORDER));
	    this.setLayoutVPadd(request.getNonblankParameter(INPUT_LAYOUT_VPADD));
	    this.setLayoutHPadd(request.getNonblankParameter(INPUT_LAYOUT_HPADD));

	    this.setAltText(request.getNonblankParameter(INPUT_ALT_TEXT));

	    this.setPopup(true);
	    //if ( request.getParameter(INPUT_OPTIMIZE_FLAG) == null){
	    //    this.setPopup(false);
	    //}
	    
		// get input source param			
		this.setInputSource(request.getString(INPUT_SOURCE, GetFileUploaderFace.INPUT_SOURCE_UPLOADED));
		
		// setup boolean source			
		if (this.getInputSource().equalsIgnoreCase(GetFileUploaderFace.INPUT_SOURCE_UPLOADED)){
		    this.setDiskSource(true);
		}
		else{
		    this.setDiskSource(false);
		}
	    
        if (logger.isInfoEnabled()){
            logger.info("Layout: a:" + this.getLayoutAlign() + 
    	    	" b:" + this.getLayoutBorder() + 
    	    	" hp:" + this.getLayoutHPadd() +
    	    	" vp:" + this.getLayoutVPadd()
            );
        }
    }
	
	/**
	 * Read convert parameters to local attributes  
	 * 
	 * @param request
	 * @throws RequestParametersException
	 */
	private void readConvertParameters(RequestContext request) 
		throws RequestParametersException 
	{
	    this.setOptimized(false);
	    if ( request.getParameter(INPUT_OPTIMIZE_FLAG) != null){
	        this.setOptimized(true);
	    }

	    this.setResize(false);
	    if ( request.getParameter(INPUT_RESIZE_FLAG) != null){
	        this.setResize(true);
	    }
	    
	    this.setThumbnail(false);
	    if ( request.getParameter(INPUT_THUMBNAIL_FLAG) != null){
	        this.setThumbnail(true);
	    }

	    this.setResizeProportional(true);
	    if ( request.getParameter(INPUT_RESIZE_MODE) != null )
	    {
	        if (request.getParameter(INPUT_RESIZE_MODE).equalsIgnoreCase(INPUT_RESIZE_MODE_CROP)){
	            this.setResizeProportional(false);
	        }
	    }
	    
		this.setThumbWidth( request.getLong(INPUT_THUMB_WIDTH) );

		if (request.getLong(INPUT_THUMB_HEIGHT) == null){
			this.setThumbHeight( new Long(0) );
		}
		else{
			this.setThumbHeight( request.getLong(INPUT_THUMB_HEIGHT) );
		}

	    logger.info("Convert: thH:" + this.getThumbHeight() + 
		    " thW:" + this.getThumbWidth() + 
		    " thumbnail:" + this.isThumbnail() +
		    " optimize:" + this.isOptimized() +
		    " resize:" + this.isResize() + 
		    " proportional:" + this.isResizeProportional() 
		);

		if ( this.isThumbnail() || this.isResize() ){
			
            try{
                // get the small image
	            this.readExistentFile( request, INPUT_EXISTENT_SMALLFILE );
            }
            catch( RequestParametersException e ){
    			if ( this.getThumbWidth() == null ){
    			    throw new RequestParametersException("Missing required parameter: width");
    			}
            }
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private String buildImageHtml(File imageFile){
	    StringBuffer strBuf = new StringBuffer("<img ");
	    strBuf.append("src=\\'");

		//URL url = new URL( Env.getServletContext().get`sdjf, new String(request.getRequest().getRequestURL() ));

	    //String currentPath = url.getProtocol () + "://"
		// + url.getHost() 
		//  + ( url.getPort()  < 1 ? "" : ":" + url.getPort()  )
		//  + req.getRequest().getContextPath()  + "/";

        strBuf.append(this.getHostLink());
        strBuf.append("/");
        strBuf.append(Repository.get().getCatalogPath(imageFile));
	    strBuf.append("\\' ");

	    if (this.getLayoutBorder() != null){
		    strBuf.append("border=\\'");
		    strBuf.append(this.getLayoutBorder());
		    strBuf.append("\\' ");
	    }
	    
	    if (this.getLayoutAlign() != null){
		    strBuf.append("align=\\'");
		    strBuf.append(this.getLayoutAlign());
		    strBuf.append("\\' ");
	    }

	    if (this.getAltText() != null){
		    strBuf.append("alt=\\'");
		    strBuf.append(this.getAltText());
		    strBuf.append("\\' ");
		    strBuf.append("title=\\'");
		    strBuf.append(this.getAltText());
		    strBuf.append("\\' ");
	    }

	    if (this.getLayoutHPadd() != null){
		    strBuf.append("hspace=\\'");
		    strBuf.append(this.getLayoutHPadd());
		    strBuf.append("\\' ");
	    }

	    if (this.getLayoutVPadd() != null){
		    strBuf.append("vspace=\\'");
		    strBuf.append(this.getLayoutVPadd());
		    strBuf.append("\\' ");
	    }

	    strBuf.append("/>");
	    return strBuf.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	private String buildThImageHtml(File imageFile){
	    StringBuffer strBuf = new StringBuffer("<img ");
	    strBuf.append("src='");

        strBuf.append(this.getHostLink());
        strBuf.append(Repository.get().getCatalogPath(imageFile));
	    strBuf.append("' ");
	    strBuf.append("/>");
	    return strBuf.toString();
	}

	/**
	 * 
	 * @return
	 * @throws CriticalException
	 * @throws RequestParametersException
	 */
	private String buildThumbnailHtml(RequestContext request) 
		throws CriticalException, RequestParametersException
	{
	    String largeSrc = this.buildThImageHtml(this.getFile());
	    String smallSrc = this.buildImageHtml(this.getThumbnailFile());
	    
	    ImageResource largeImg = Repository.get().getImageResource(this.getFile());
	    String popupName = null;
	    try{
	        popupName = this.createPopup(
		        request, 
		        largeSrc, 
		        largeImg.getWidth().intValue() + Configuration.get().getPopupWidthAppendix(), 
		        largeImg.getHeight().intValue() + Configuration.get().getPopupHeightAppendix() 
		    );
	        
	        if (logger.isInfoEnabled()){
	            logger.info(
	                "Appendix w:" + (largeImg.getWidth().intValue() + Configuration.get().getPopupWidthAppendix()) +
	                "h:" + (largeImg.getHeight().intValue() + Configuration.get().getPopupHeightAppendix())
	            );
	        }
	    }
	    catch(RepositoryException e){
	        logger.error("-errror", e);
	        throw new RequestParametersException(e);
	    }
	    StringBuffer strBuf = new StringBuffer("<A class=RTEURLLink title=\"Popup\" href=\"javascript:displayPopup(\\'");
	    strBuf.append(popupName);
	    strBuf.append("\\');\" target=\"\">");
	    
	    strBuf.append(smallSrc);
	    strBuf.append("</A>");
	    return strBuf.toString();
	}

	/**
     * @return Returns the isOptimized.
     */
    public boolean isOptimized() {
        return isOptimized;
    }
    /**
     * @param isOptimized The isOptimized to set.
     */
    public void setOptimized(boolean isOptimized) {
        this.isOptimized = isOptimized;
    }
    /**
     * @return Returns the isResize.
     */
    public boolean isResize() {
        return isResize;
    }
    /**
     * @param isResize The isResize to set.
     */
    public void setResize(boolean isResize) {
        this.isResize = isResize;
    }
    /**
     * @return Returns the isResizeProportional.
     */
    public boolean isResizeProportional() {
        return isResizeProportional;
    }
    /**
     * @param isResizeProportional The isResizeProportional to set.
     */
    public void setResizeProportional(boolean isResizeProportional) {
        this.isResizeProportional = isResizeProportional;
    }
    /**
     * @return Returns the layoutAlign.
     */
    public String getLayoutAlign() {
        return layoutAlign;
    }
    /**
     * @param layoutAlign The layoutAlign to set.
     */
    public void setLayoutAlign(String layoutAlign) {
        this.layoutAlign = layoutAlign;
    }
    /**
     * @return Returns the layoutBorder.
     */
    public String getLayoutBorder() {
        return layoutBorder;
    }
    /**
     * @param layoutBorder The layoutBorder to set.
     */
    public void setLayoutBorder(String layoutBorder) {
        this.layoutBorder = layoutBorder;
    }
    /**
     * @return Returns the layoutHPadd.
     */
    public String getLayoutHPadd() {
        return layoutHPadd;
    }
    /**
     * @param layoutHPadd The layoutHPadd to set.
     */
    public void setLayoutHPadd(String layoutHPadd) {
        this.layoutHPadd = layoutHPadd;
    }
    /**
     * @return Returns the layoutVPadd.
     */
    public String getLayoutVPadd() {
        return layoutVPadd;
    }
    /**
     * @param layoutVPadd The layoutVPadd to set.
     */
    public void setLayoutVPadd(String layoutVPadd) {
        this.layoutVPadd = layoutVPadd;
    }
    /**
     * @return Returns the thumbHeight.
     */
    public Long getThumbHeight() {
        return thumbHeight;
    }
    /**
     * @param thumbHeight The thumbHeight to set.
     */
    public void setThumbHeight(Long thumbHeight) {
        this.thumbHeight = thumbHeight;
    }
    /**
     * @return Returns the thumbWidth.
     */
    public Long getThumbWidth() {
        return thumbWidth;
    }
    /**
     * @param thumbWidth The thumbWidth to set.
     */
    public void setThumbWidth(Long thumbWidth) {
        this.thumbWidth = thumbWidth;
    }
    /**
     * @return Returns the isPopup.
     */
    public boolean isPopup() {
        return isPopup;
    }
    /**
     * @param isPopup The isPopup to set.
     */
    public void setPopup(boolean isPopup) {
        this.isPopup = isPopup;
    }
    /**
     * @return Returns the isThumbnail.
     */
    public boolean isThumbnail() {
        return isThumbnail;
    }
    /**
     * @param isThumbnail The isThumbnail to set.
     */
    public void setThumbnail(boolean isThumbnail) {
        this.isThumbnail = isThumbnail;
    }
    /**
     * @return Returns the thumbnailFile.
     */
    public File getThumbnailFile() {
        return thumbnailFile;
    }
    /**
     * @param thumbnailFile The thumbnailFile to set.
     */
    public void setThumbnailFile(File thumbnailFile) {
        this.thumbnailFile = thumbnailFile;
    }
    /**
     * @return Returns the altText.
     */
    public String getAltText() {
        return altText;
    }
    /**
     * @param altText The altText to set.
     */
    public void setAltText(String altText) {
        this.altText = altText;
    }
    
    /**
     * @return Returns the hostLink.
     */
    public String getHostLink() {
        return Env.getHostName();
    }
    
}
