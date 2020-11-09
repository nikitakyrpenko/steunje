/*
 * Created on Nov 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 * @author Yuri Shanoilo
 */
package com.negeso.framework.image;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.util.FileUtils;
import com.negeso.framework.util.StringUtil;
import com.negeso.framework.util.Timer;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.RepositoryException;
import com.negeso.module.media_catalog.domain.ImageResource;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 
 * @author Yuri Shanoilo
 * @author Olexiy Strashko
 */
class ResizeParameters {
	public double resizeScaleX = 1;
	public double resizeScaleY = 1;
	public int sliceWidth = 0;
	public int sliceHeight = 0;
	public boolean slice = false;
	public ResizeParameters(double resizeX, double resizeY, int sliceWidth, int sliceHeight, boolean slice) {
		this.resizeScaleX = resizeX;
		this.resizeScaleY = resizeY;
		this.sliceWidth = sliceWidth;
		this.sliceHeight = sliceHeight;
		this.slice = slice;
	}
}

class FileAndImage {
	public File imageFile = null;
	public BufferedImage Image = null;
	public FileAndImage(File imageFile, BufferedImage Image) {
		this.imageFile = imageFile;
		this.Image = Image;
	}
}

public class ImageTransform  {
    
    public static final String RESIZE_MODE_CENTER_CROP = "centerCrop";
    public static final String RESIZE_MODE_CROP = "slice";
    public static final String RESIZE_MODE_NON_PROPORTIONAL = "resize";
    public static final String RESIZE_MODE_PROPORTIONAL_BY_WIDTH = "resizeProp";
    public static final String RESIZE_MODE_PROPORTIONAL_BY_HEIGHT = "resize";
    
    
	static Logger logger = Logger.getLogger(ImageTransform.class);
	protected int scale = 1;
	protected boolean error = false;
	protected double newHeight = 0;
	protected double newWidth  = 0;
	
	//Error messages
	protected String fileToBigError = "File you have tried to upload is to big.";
	protected String notImageError = "You tried to upload file with unsupported format.";
	protected String dimensionsError = "Image you have tried to upload is too wide or too high.";
	protected String wrongTypeError = "On server ocurred some error or file you have tried to upload is not of needed type.";
	protected String inputError = "You have not entered changing parameters or made a mistake in input.";
	
	//Indicates that we are working with image that is not uploaded but chosed
	//from list of already uploaded images. So we must not delete it because
	//it may be already showen on some page. 
	protected boolean deleteSourceImage = false;
	protected boolean decreaseSize = false;
	
	//This variable contains transformation type. There could be such types:
	// "resizeProp", "sliceProp", "resize" and "slice".
	protected String resizeType = "resizeProp";
	
	/** Maximum image width	*/
	 protected int maxWidth = 800;
	 
	/** Maximum image height */
	 protected int maxHeight = 600;

	/**
	 * Default constructor
	 * Sets transform type to resize proportional
	 * Sets error switch and error message
	 * Do not set needed image Height and Width so they must be set by
	 * setHeight and setWidth
	 */ 
	public ImageTransform() {
		logger.debug("+");
		this.newHeight = 0;
		this.newWidth = 0;
		this.resizeType = "resizeProp";
		//Setting class variables to values which is stored in conf.txt file
		
		maxWidth = Env.getIntProperty("media.catalog.maxImageWidth", maxWidth);
		maxHeight = Env.getIntProperty("media.catalog.maxImageHeight", maxHeight);
		
		if (logger.isInfoEnabled()){
		    logger.info("Image maxWidth=" + maxWidth + " maxHeight=" + maxHeight);
		}
		
		logger.debug("-");
	}

	/**
	 * Constructor
	 * Sets transform type to resize proportional
	 * @param double - Height to which image must be reduced.
	 * @param double - Width to which image must be reduced.
	 */ 
	public ImageTransform(double newHeight, double newWidth) {
		this();
		logger.debug("+");
		this.newHeight = newHeight;
		this.newWidth = newWidth;
		logger.debug("-");
	}

	/**
	 * Sets resize type. Valid resize types are:
	 * resizeProp, resize, sliceProp, slice
	 * @param String - Resize type which must be set
	 */ 
	public void setResizeType(String resizeType) {
		logger.debug("+");
		if ((resizeType == "resizeProp") || (resizeType == "resize") || 
			(resizeType == "slice") || (resizeType == "sliceProp") ||
			(resizeType == "centerCrop")
		) {
			this.resizeType = resizeType;
		} else {
			this.resizeType = "resizeProp";
		}
		logger.debug("-");
	}
	
	/**
	 * Sets width to which image will be reduced.
	 * @param double - Width to which image will be reduced
	 */ 
	public void setNeededWidth(double newWidth) {
		logger.debug("+-");
		this.newWidth = newWidth;
	}
	
	/**
	 * Sets height to which image will be reduced.
	 * @param double - Height to which image will be reduced
	 */ 
	public void setNeededHeight(double newHeight) {
		logger.debug("+-");
		this.newWidth = newHeight;
	}
	
	/**
	 * Sets trigger which determine that image must not be transformed but just recompressed
	 * to decrease size in kb.
	 * Default value is false.
	 * @param boolean - Boolean which determine need in image transforming
	 */ 
	public void setDescrease(boolean decrease) {
		logger.debug("+-");
		this.decreaseSize = decrease;
	}

	/**
	 * Sets default scale of image dimensions decrease. This parameter is used
	 * when image must be decreased only in size (in kb).
	 * Default value 1. Resulting image dimensions are dimensions before transformation
	 * divided on scale value.
	 * @param int - Determine how dimensions would be changed when decreasing image size (in kb)
	 */ 
	 public void setDefaultScale(int defaultScale) {
		logger.debug("+-");
		if (defaultScale > 0) {
			this.scale = defaultScale;
		}
	 }
	
	/**
	 * Sets trigger which determine what to do with source image.
	 * Default value is false. If it will be set to true then source image will
	 * be deleted.
	 * @param boolean - Boolean which determine need of source file delete
	 */ 
	public void setDeleteSource(boolean delete) {
		logger.debug("+-");
		this.deleteSourceImage = delete;
	}


	/**
	 * Initialize transformation of image and sets needed variables 
	 * @param File - File of image which must be transformed.
	 * @param double - Width to which image must be reduced.
	 * @param double - Height to which image must be reduced.
	 * @return File in which is stored transformed image
	 * @throws IOException, WrongTransformParametersException, FileNotApplicableException
	 */ 
	public File transform(File imageFile, double width, double height) 
		throws IOException, WrongTransformParametersException,
			FileNotApplicableException {
		logger.debug("+");
		this.newWidth = width;
		this.newHeight = height;
		File resultFile = this.transform(imageFile);
		logger.debug("-");
		return resultFile;
	}
	
	/**
	 * Perform transformation of image
	 * @param File - File of image which must be transformed.
	 * @return File in which is stored transformed image
	 * @throws IOException, WrongTransformParametersException, FileNotApplicableException
	 */ 
	public File transform(File imageFile) 
		throws IOException, WrongTransformParametersException, 
			FileNotApplicableException {
			logger.debug("+");			
			
			
		    Timer timer = new Timer();
			
			File resultFile = imageFile;
			//Convert image to JPG format if such convertation is needed.
			FileAndImage fileAndImage = convert(resultFile);
			resultFile = fileAndImage.imageFile;
			BufferedImage workingImage = fileAndImage.Image; 
			
		    logger.info("convert time:" + timer.stop());
			
			//Beginning transformation depending on which type of
			//transformation is needed
			//If you will add some new types of transformations, add them before "resizeProp"
			//resizeProp always must be in FINAL else
			try {
				try {
				    
				    if ( "centerCrop".equals(resizeType) ){
				        workingImage = this.centerCrop(workingImage);
				    }
				    else{
						if (resizeType.equals("sliceProp") || resizeType.equals("slice")) {
							workingImage = slice(workingImage);
						} else if (resizeType.equals("resizeProp") || resizeType.equals("resize")) {
							workingImage = resize(workingImage);
						}
				    }
				} catch (WrongTransformParametersException wtp) {
					resultFile.delete();
					throw wtp;
				}
			} catch (SecurityException se) {
			//When file cannot be deleted
			logger.error("Java do not have permission to delete file: " + se);
			}
		    logger.info("transform time:" + timer.stop());
			
			//Beginning of file renaming - to filename are added dimensions of the image
			String fileName = resultFile.getName().substring(0,
				resultFile.getName().indexOf("."));
			//fileName = fileName.replaceAll("[_x\\d]+$", "_");
			String fileExtension = resultFile.getName().substring(
				resultFile.getName().indexOf("."));
			String pathToFile = resultFile.getAbsolutePath().substring(0,
				resultFile.getAbsolutePath().lastIndexOf(File.separator)+1);
			String imageDimensions = workingImage.getWidth() + "x" + workingImage.getHeight(); 
			String newFileName = pathToFile + fileName + imageDimensions + fileExtension;
			File newImage = new File(newFileName);

			if (!newImage.equals(resultFile))
			{
				int i=1;
				while (newImage.exists())
				{
					newFileName = pathToFile + fileName + imageDimensions + "_" + i + fileExtension;
					newImage = new File(newFileName);
					i++;
				}
				resultFile.renameTo(newImage);
				resultFile = newImage;
			}
			//End of file renaming
				
			ImageIO.write(workingImage, "JPG", resultFile);

		    logger.info("save time:" + timer.stop());

			
			logger.debug("-");			
			//Returning transformed image
			return resultFile;
	}

	/**
	 * Perform convertation of image to JPEG format if this is needed.
	 * This is must be done if file must be transformed but it is in some other
	 * format (not jpg) or if there is need to reduce file size.
	 * @param File - File of image which must be converted.
	 * @return Buffered Image of file after transformation
	 * @throws IOException, FileNotApplicableException
	 */ 
	public FileAndImage convert(File imageFile) 
		throws IOException, FileNotApplicableException 
	{
		logger.debug("+");			
	    Timer timer = new Timer();

	    //Checking MIME-type, width, height and other parameters of file
		this.isValid(imageFile);
		logger.info("validation:" + timer.stop());
			
		//Creating buffered image from uploaded file. Throw exception if cannot create.
        Image image = Toolkit.getDefaultToolkit().createImage(imageFile.getAbsolutePath());
        BufferedImage workingImage = ImageUtil.toBufferedImage(image);
        
//		//Creating buffered image from uploaded file. Throw exception if cannot create.	
//		BufferedImage workingImage = ImageIO.read(imageFile);
		logger.info("BufferedImage:" + timer.stop());
		
		if ( this.convertationIsNeeded(imageFile) ) {
			//Creating new file with unique name 
			String oldName = imageFile.getAbsolutePath().substring(0, imageFile.getAbsolutePath().length()-4);
			String newJpgName = oldName + ".jpg";
			File newJpg = new File(newJpgName);
			int i=1;
			while (newJpg.exists())
			{
				newJpgName = oldName + "" + "_" + i + ".jpg";
				newJpg = new File(newJpgName);
				i++;
			}
			//HERE EVERYWHERE WERE METHOD IS WORKING WITH FILES - CREATE
			//BUFFERED IMAGE, CREATE NEW FILE, WRITE TO FILE MAY HAPPEN
			//IOException!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			newJpg.createNewFile();

			if (deleteSourceImage) {
				imageFile.delete();
			}
			imageFile = newJpg;
			
			FileOutputStream out1 = new FileOutputStream(newJpg);
			// encodes bi as a JPEG data stream
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out1);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(workingImage);
			param.setQuality(1.0f, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(workingImage);
			out1.flush();
			out1.close();

			workingImage = ImageIO.read(imageFile);
		}
		//Renaming .jpg file in case if file was uploaded not just right now
		else if (!deleteSourceImage)
		{
			//Creating new file with unique name 
			String oldName = imageFile.getAbsolutePath().substring(0, imageFile.getAbsolutePath().length()-4);
			String newJpgName = oldName + ".jpg";
			File newJpg = new File(newJpgName);
			int i=1;
			while (newJpg.exists())
			{
				newJpgName = oldName + "" + "_" + i + ".jpg";
				newJpg = new File(newJpgName);
				i++;
			}

			newJpg.createNewFile();
			ImageIO.write(workingImage,"JPG",newJpg);
			imageFile = newJpg;
		}
		logger.info("Convert:" + timer.stop());
		
		FileAndImage fileAndImage = new FileAndImage(imageFile, workingImage);
		logger.debug("-");
		return fileAndImage;
	}
	
	/**
	 * Perform check for need of convertation.
	 * @param String - File mime-type.
	 * @return Boolean which shows need of convertation
	 */ 
	protected boolean convertationIsNeeded(File file) {
		String ext = FileUtils.getExtension(file);
		String[] allowedExts = {"jpeg", "jpg"};  
		if ( StringUtil.isInStringArray( allowedExts, ext ) ){
		    return true;
		}
		return false;
	}
	
	/**
	 * Perform validity check of image. If image (or file) is not valid
	 * throws exception FileNotApplicableException
	 * @param File - File with image.
	 * @return Mime-type of file
	 * @throws FileNotApplicableException
	 */ 
	public boolean isValid(File imageFile) throws FileNotApplicableException {
		logger.debug("+");
		try{
		    ImageResource sentImage = Repository.get().getImageResource(imageFile);
		    String ext = sentImage.getExtension();
			String[] allowedExts = {"gif", "jpeg", "jpg", "png"};  
			if ( !StringUtil.isInStringArray(allowedExts, ext) ){
				if (deleteSourceImage) {
					imageFile.delete();
				}
				throw new FileNotApplicableException(notImageError + " "  + ext);
			}
		    
			if (!(sentImage == null)){
				if ((sentImage.getWidth().intValue() > maxWidth)||(sentImage.getHeight().intValue() > maxHeight)){
					if (deleteSourceImage){
						imageFile.delete();
					}
					throw new FileNotApplicableException(dimensionsError);
				}
			}
			else{
				imageFile.delete();
				throw new FileNotApplicableException(wrongTypeError);
			}
		}
		catch (RepositoryException ioe){
			logger.error("Error when reading image file: " + ioe, ioe);			
		}
		catch (SecurityException se){
			logger.error("Java do not have permission to delete file: " + se, se);
		}
		logger.debug("-");
		return true;
	}

	/**
	 * Manage image resizing.
	 * @param BufferedImage - Image.
	 * @throws WrongTransformParametersException
	 */ 
	protected BufferedImage resize(BufferedImage workingImage) throws WrongTransformParametersException {
		logger.debug("+");
		boolean withBlackLine = false;
		ResizeParameters parameters = calculateResizeParameters(workingImage);
		withBlackLine = parameters.slice;
		workingImage = doResize(workingImage, parameters.resizeScaleX, parameters.resizeScaleY);
		//Slicing image if there is black line
		if (withBlackLine) {
			workingImage = doSlice(workingImage, parameters.sliceWidth, parameters.sliceHeight);
		}
		logger.debug("-");
		return workingImage;
	}

	/**
	 * Manage image slicing.
	 * @param BufferedImage - Image.
	 * @throws WrongTransformParametersException
	 */ 
	protected BufferedImage slice(BufferedImage workingImage) throws WrongTransformParametersException {
		logger.debug("+");
		ResizeParameters parameters = calculateSliceParameters(workingImage);
		workingImage = doResize(workingImage, parameters.resizeScaleX, parameters.resizeScaleY);
		workingImage = doSlice(workingImage, parameters.sliceWidth, parameters.sliceHeight);
		logger.debug("-");
		return workingImage;
	}


	/**
	 * Checks parameters of resizing and change them if they are not valid.
	 * If parameters are not entered or canot be changed to valid
	 * throw exception WrongTransformParametersException
	 * @param int - Current width of the image.
	 * @param int - Current height of the image.
	 * @throws WrongTransformParametersException
	 */ 
	protected void checkParams( int currentWidth, int currentHeight) 
		throws WrongTransformParametersException {

		logger.debug("+");

		//Sending error if usr has not entered height and width
		if ((newWidth <= 0)&&(newHeight <= 0))
		{
			throw new WrongTransformParametersException(inputError);
		}
		
		//Asigning current values to width and height if new parameters are bigger
		if (newWidth > currentWidth)
		{
			newWidth = currentWidth;
		}
		if (newHeight > currentHeight)
		{
			newHeight = currentHeight;
		}
		logger.debug("-");
	}
	
	/**
	 * Calculates parameters for resizing. Uses newHeight and newWidth variables of class.
	 * @param BufferedImage - Image.
	 * @return Parameters of resizing
	 * @throws WrongTransformParametersException
	 */ 
	protected ResizeParameters calculateResizeParameters(BufferedImage workingImage) 
		throws WrongTransformParametersException {
		logger.debug("+");

		int currentWidth = workingImage.getWidth();
		int currentHeight = workingImage.getHeight();

		if (decreaseSize && (newWidth <= 0) && (newHeight <= 0))
		{
			newWidth = currentWidth/scale;
			newHeight = currentHeight/scale;
		}
		
		//Updating needed width and height in case if they are bigger then current
		//and also checking that they are specified 
		checkParams(currentWidth, currentHeight);

		double resizeScaleX = 0;
		double resizeScaleY = 0;
		boolean slice = true;
		int sliceWidth = 0;
		int sliceHeight = 0;
		
		if (resizeType.equals("resize")) //Do resize exactly as user wants even if this will corrupt image
		{
			resizeScaleX = newWidth/currentWidth;
			resizeScaleY = newHeight/currentHeight;
			
			sliceWidth = (int)newWidth;
			sliceHeight = (int)newHeight;
//			sliceWidth = (int)newWidth -1;
//			sliceHeight = (int)newHeight - 1;

			if (newWidth <= 0) {
				resizeScaleX = resizeScaleY;
			}
			if (newHeight <= 0) {
				resizeScaleY = resizeScaleX; 		
			}
			
		}
		else	//Do proportional resize to preserve image look.
		{
			//Calculating width and height to get PROPORTIONAL resize
			//Also calculating how must be sliced image to destroy black line
			if ((newWidth/currentWidth) < (newHeight/currentHeight))
			{
				resizeScaleX = newWidth/currentWidth;
				sliceWidth = (int)(currentWidth * resizeScaleX);
				sliceHeight = (int)(currentHeight * resizeScaleX);
				
				if (newWidth <= 0)
				{
					resizeScaleX = newHeight/currentHeight;
					sliceWidth = (int)(currentWidth * resizeScaleX);
					sliceHeight = (int)(currentHeight * resizeScaleX);
				}
				resizeScaleY = resizeScaleX;
			}
			else
			{
				resizeScaleY = newHeight/currentHeight;
				
				sliceWidth = (int)(currentWidth * resizeScaleY);
				sliceHeight = (int)(currentHeight * resizeScaleY);

				if (newHeight <= 0)
				{
					resizeScaleY = newWidth/currentWidth;
					sliceWidth = (int)(currentWidth * resizeScaleY);
					sliceHeight = (int)(currentHeight * resizeScaleY);
				}
				resizeScaleX = resizeScaleY;
			}
			if (decreaseSize)
			{
				resizeScaleX = scale;
				resizeScaleY = scale;
				slice = false;
			}
		}
		
		ResizeParameters parameters  = new ResizeParameters(resizeScaleX, resizeScaleY, sliceWidth, sliceHeight, slice);
		logger.debug("-");
		return parameters;
	}

	/**
	 * Calculates parameters for clicing. Uses newHeight and newWidth variables of class.
	 * @param BufferedImage - Image.
	 * @return Parameters of resizing
	 * @throws WrongTransformParametersException
	 */ 
	protected ResizeParameters calculateSliceParameters(BufferedImage workingImage) 
		throws WrongTransformParametersException {
			
		logger.debug("+");

		int currentWidth = workingImage.getWidth();
		int currentHeight = workingImage.getHeight();

		//Updating needed width and height in case if they are bigger then current
		//and also checking that they are specified 
		checkParams(currentWidth, currentHeight);

		double resizeScaleX = 0;
		double resizeScaleY = 0;
		boolean slice = true;
		int sliceWidth = 0;
		int sliceHeight = 0;
		
		//Calculating width and height to get proportional resize adn for slice
		sliceWidth = (int)newWidth;
		sliceHeight = (int)newHeight;
		
		if (resizeType.equals("slice")) //Slice without resizing
		{
			if (sliceWidth <= 0) {
				sliceWidth = currentWidth; 		
			}
			if (sliceHeight <= 0) {
				sliceHeight = currentHeight; 		
			}
			
			resizeScaleX = 1;
			resizeScaleY = 1;
		}
		else	//Do proportional resize to preserve image look and then slice. 
		{
			if ((newWidth/currentWidth) < (newHeight/currentHeight))
			{
				resizeScaleY = newHeight/currentHeight;
				if (newWidth <= 0)
				{
					sliceWidth = (int)(currentWidth * resizeScaleY);
				}
				resizeScaleX = resizeScaleY;
			}
			else
			{								
				resizeScaleX = newWidth/currentWidth;
				if (newHeight <= 0)
				{
					sliceHeight = (int)(currentHeight * resizeScaleX);
				}
				resizeScaleY = resizeScaleX;
			}
		}
		ResizeParameters parameters  = new ResizeParameters(resizeScaleX, resizeScaleY, sliceWidth, sliceHeight, slice);
		
		logger.debug("-");
		return parameters;
	}
	
	/**
	 * Perform resizing.
	 * @param BufferedImage  - Image.
	 * @param double - Scale - number of times in which image must be expanded by axis X.
	 * @param double - Scale - number of times in which image must be expanded by axis Y.
	 */ 
	protected BufferedImage doResize(
			BufferedImage workingImage, 
			double resizeScaleX, 
			double resizeScaleY
	) {
		logger.debug("+");
		
		
		if ( ( (int)this.newHeight) < 1 ){
			this.newHeight = workingImage.getHeight() * resizeScaleX;
		}
		
		if ( ( (int)this.newWidth) < 1 ){
			this.newWidth = workingImage.getWidth() * resizeScaleY;
		}

		/*
		BufferedImage thumbImage = new BufferedImage(
			(int) this.newWidth, 
			(int) this.newHeight, 
			BufferedImage.TYPE_INT_RGB
		);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(
			RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BICUBIC
		);
		
		graphics2D.drawImage(workingImage, 0, 0, (int)this.newWidth, (int)this.newHeight, null);

		workingImage = thumbImage;
		*/
			    
		Image img = workingImage.getScaledInstance(
			(int) this.newWidth,
			(int) this.newHeight,
			Image.SCALE_SMOOTH
		);
		
		// Create the buffered image.
        BufferedImage bufferedImage = new BufferedImage(
       		img.getWidth(null), 
       		img.getHeight(null), 
			BufferedImage.TYPE_INT_RGB
		);

        // Copy image to buffered image.
        Graphics g = bufferedImage.createGraphics();

        // Clear background and paint the image.
        g.setColor(Color.white);
        g.fillRect(0, 0, img.getWidth(null), img.getHeight(null));
        g.drawImage(img, 0, 0, null);
        g.dispose();
		
		workingImage = bufferedImage;
		
		logger.debug("-");
		return workingImage;
	}
	
	/**
	 * Perform slicing.
	 * @param BufferedImage - Image.
	 * @param int - Width of area which will be sliced from image.
	 * @param int - Height of area which will be sliced from image.
	 */ 
	protected BufferedImage doSlice(BufferedImage workingImage, int sliceWidth, int sliceHeight) {
		logger.debug("+");
		//Slicing image
		BufferedImage newImage = workingImage.getSubimage(0,0,sliceWidth,sliceHeight);
		//Assigning resulting image to source image
		workingImage = newImage;
		logger.debug("-");
		return workingImage;
	}
	
	/**
	 * Center crop of the image   
	 * 
	 * @param workingImage
	 * @return
	 */
	protected BufferedImage centerCrop(BufferedImage workingImage) {
		logger.debug("+");

		if ( (this.newWidth <= 0 ) && (this.newHeight <= 0)){
		    this.newWidth = 10;
		}
		
		if ( this.newWidth <= 0 ){
		    this.newWidth = this.newHeight;
		}

		if ( this.newHeight <= 0 ){
		    this.newHeight = this.newWidth;
		}
		
		if ( this.newWidth > workingImage.getWidth() ){
		    this.newWidth = workingImage.getWidth();
		}
		if ( this.newHeight > workingImage.getHeight() ){
		    this.newHeight = workingImage.getHeight();
		}

		
		int dw = (workingImage.getWidth() - (int) this.newWidth) / 2;    
		int dh = (workingImage.getHeight() - (int) this.newHeight) / 2;
		if (logger.isInfoEnabled()){
			logger.error("srcW: " + workingImage.getWidth());
			logger.error("srcH: " + workingImage.getHeight());
	
			logger.error("newW: " + this.newWidth);
			logger.error("hewH: " + this.newHeight);
	
			logger.error("dw: " + dw);
			logger.error("dh: " + dh);
		}
		
		int cropWidth = (int) this.newWidth;
		if ( dw == 0 ) {
		// correct cropWidth to  
		    cropWidth = workingImage.getWidth();
		}
		
		int cropHeight = (int) this.newHeight;
		if ( dh == 0 ) {
		    cropHeight = workingImage.getHeight();
		}
		
		workingImage = workingImage.getSubimage(
		        dw,
		        dh,
		        cropWidth,
		        cropHeight
		);
		logger.debug("-");
		return workingImage;
	}
}