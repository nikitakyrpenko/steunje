/*
 * @(#)$Id: FileKeeper.java,v 1.19, 2007-02-19 09:20:04Z, Olexiy Strashko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog;

import java.io.File;
import java.util.Collection;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.User;
import com.negeso.framework.util.FileUtils;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.framework.util.StringUtil;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.media_catalog.domain.ResourceFilter;

/**
 *
 * Helper class for file keeping uploaded files
 * 
 * @version		$Revision: 20$
 * @author		Olexiy Strashko
 * 
 */
public class FileKeeper {

	private static Logger logger = Logger.getLogger(FileKeeper.class);

    private static final String UNKNOWN_FILENAME = "unknown";
	
	static char[] restrictedFileNameChars = {
		' ', '\'', '"', '~', '`', '$', '%', '^', '+', '\\', '<', '>', '|', '{', '}'   
		    
	};
	static char[] allowedFileNameChars = {
	    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
	    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	    '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '=', '_', '.', '/', '\'', '!', '@', '#', '&', '*', '(', ')', '?', ':', ';'
	};
	static char restrictedCharsReplacement = '_';

    
    
    /**
     * 
     */
    public FileKeeper() {
        super();
    }

	/**
	 * Save fileItem (FileUpload) to disk as a File.
	 * 
	 * @param fileItem
	 * @param workDir
	 * @param availableExtensions
	 * @param createUniqeNameIfExists
	 * @return
	 * @throws RequestParametersException
	 */	
	public static File saveFileItemSafe(
        User user,
		FileItem fileItem, 
		String catalogFolder, 
		String[] availableExtensions,
		boolean createUniqeNameIfExists,
		Long allowedFileSize
	) 
		throws RequestParametersException
	{
	    logger.debug("+");
        Folder folder = Repository.get().getFolder(catalogFolder);
        if ( logger.isInfoEnabled() ){
            logger.info("Folder file:" + folder.getFile().getAbsoluteFile());
            logger.info("Folder catalog:" + folder.getCatalogPath());
        }
//        if ( !folder.canEdit(user) ){
//            throw new AccessDeniedException();
//        }
        
		if (
			(fileItem.getName() != null) && 
			(fileItem.getName().equalsIgnoreCase("")) && 
			(fileItem.getSize() == 0))
		{
			// Empty file, set to null
			return null;
			//throw new RequestParametersException("Uploading file is empty. Please try another one.");
		}
		
		
		long maxFileSize = Repository.get().getMaxFileSize();
		if ( allowedFileSize != null ){
			maxFileSize = allowedFileSize.longValue(); 
		}
		
		if ( fileItem.getSize() > maxFileSize ){
			throw new RequestParametersException(
				"Uploading file is too large. Maximum size allowed: " + 
				StringUtil.formatSizeInKb( maxFileSize )
			);
		}
		

		
		if ( !FileKeeper.canSaveFile(fileItem.getSize()) ){
			if(Env.getProperty(Configuration.SEND_EMAIL).equals("true")){
				Repository.get().sendEmailFreeSpace("100");
			}
			throw new RequestParametersException(
				"Not enough space. Please try another one or cleanup."
			);
		}else{	
			FileKeeper.checkTotalSpace(fileItem.getSize());
		}
		
		String tokens[] = fileItem.getName().split("\\.");
		if (tokens.length < 2){
			throw new RequestParametersException("Invalid uploading file type.");
		}
		
		String fileExt = tokens[tokens.length - 1];
		fileExt = fileExt.trim().toLowerCase();

		String fileName = tokens[tokens.length - 2];
		tokens = fileName.split("\\\\");
		if (tokens.length < 1){
			throw new RequestParametersException("Invalid uploading file name.");
		}
		
		fileName = FileKeeper.prepareFileName(tokens[tokens.length - 1]);
		
		/* Check file extension */
		if (availableExtensions.length != 0){
			if (!StringUtil.isInStringArray(availableExtensions, fileExt)){
				throw new RequestParametersException(
					"Invalid uploading file type. Allowed types: <" +
					StringUtil.join(availableExtensions, ", ") +
					">."
				);
			}
		}
		
		String sysFolderPath = Repository.get().getRealPath(catalogFolder);
		if ( createUniqeNameIfExists ){
			/* get unique name for file */
			fileName = FileKeeper.getUniqueName(sysFolderPath, fileName, fileExt);
		}
		
		File uploadedFile = new File(sysFolderPath, fileName + "." + fileExt);
		if (uploadedFile.exists()){
			throw new RequestParametersException(
				"File with name '" + fileName + "." + fileExt + "' " +
				"already exists. Please try another name."
			);
		}
		try{
			fileItem.write(uploadedFile);
		}
		catch(Exception e){
			logger.error(
				"Unknown file uploading error while writing file to disk.", e
			);
			throw new RequestParametersException(
				"Unable to save this file. Please try another one." + e.getMessage() +
				" fileName:" + fileName +
				" fileExt:" + fileExt
			);
		}
	    logger.debug("-");
		return uploadedFile;
	}
    

	/**
	 * @return
	 */
	private static boolean canSaveFile(long fileSize) {
		if ( (Repository.get().getUsedSpace() + fileSize) > Repository.get().getTotalSpace() ) 
		{
			return false;   
		}
		return true;
	}
	
	private static void checkTotalSpace(long fileSize) {		
		String[] usedFreeSpace = Env.getProperty(Configuration.USED_FREE_SPACE).split(";");
		double usedFS = 100;
		for(int i = 0; i < usedFreeSpace.length; i++){
			usedFS= Double.parseDouble(usedFreeSpace[i])/100;
			double totalSpace = Repository.get().getTotalSpace() * usedFS;
			if ( ((Repository.get().getUsedSpace() + fileSize) > Repository.get().getTotalSpace()*usedFS )
				&& (Repository.get().getUsedSpace() < totalSpace) )	{
					Repository.get().sendEmailFreeSpace(usedFreeSpace[i]);
					break;
			}			
		}
	}
	
	

	/**
	 * Prepare file name to <code>valid</code> repository name.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String prepareFileName(String fileName){
	    if (fileName == null){
	        fileName = FileKeeper.UNKNOWN_FILENAME;
	    }
	    fileName = fileName.trim()
	    //.toLowerCase()
	    ;
	    return StringUtil.replaceChars(
	    		fileName,
				FileKeeper.restrictedFileNameChars,
				FileKeeper.restrictedCharsReplacement
		);
	}
	
	/**
	 * Prepare file name to <code>valid</code> repository name.
	 * 
	 * @param fileName
	 * @return
	 */
	
	public static String prepareFileNameNew(String fileName){
	    if (fileName == null){
	        fileName = FileKeeper.UNKNOWN_FILENAME;
	    }
	    fileName = fileName.trim()
	    //.toLowerCase()
	    ;
	    return StringUtil.replaceNotAllowedChars(
	    		fileName,
				FileKeeper.allowedFileNameChars,
				FileKeeper.restrictedCharsReplacement
		);
	}

	
	/**
	 * <pre>Get unique file name in workDir scope. If given as a parameter name is 
	 * unique - no changes occured. If such file allready exists - unique name is 
	 * calculated by addind incremetal suffix to file name.<pre>
	 * Example: 
	 * 		given name	: image.jpg, files in working dir: image.jpg, image1.jpg
	 * 		result name	: image2.jpg
	 * 
	 * @param workDir					The working directory(relative of repository root).
	 * @param newFileName				The file name been tested for uniquity.
	 * @param newFileExt				The file extension.
	 * @return	<code>String</code>		The unique(in scope of working directory) name.
	 */
	public static String getUniqueName(String workDir, String fileName, String newFileExt){
		//String realDir = this.getRealPath(workDir);
		
		String newFileName = fileName;
		
		File testedFile = new File(workDir, newFileName + "." + newFileExt);
		int i = 0;
		while (testedFile.exists())
		{
			i++;
			newFileName = fileName + "_" + i;
			testedFile = new File(
				workDir, 
				newFileName + "." + newFileExt
			);
		}
		return newFileName;
	}

    /**
     * <pre>Get unique folder name in workDir scope. If given as a parameter name is 
     * unique - no changes occured. If such file allready exists - unique name is 
     * calculated by addind incremetal suffix to file name.<pre>
     * Example: 
     *      given name  : image files in working dir: image, image_1
     *      result name : image_2
     * 
     * @param workDir                   The working directory(relative of repository root).
     * @param newFileName               The file name been tested for uniquity.
     * @param newFileExt                The file extension.
     * @return  <code>String</code>     The unique(in scope of working directory) name.
     */
    public static String getUniqueFolderName(String workDir, String fileName){
        //String realDir = this.getRealPath(workDir);
        
        String newFileName = fileName;
        
        File testedFile = new File(workDir, newFileName);
        int i = 0;
        while (testedFile.exists())
        {
            i++;
            newFileName = fileName + "_" + i;
            testedFile = new File(
                workDir, 
                newFileName 
            );
        }
        return newFileName;
    }
    
	/**
     * Resoursively list files. (Secured). 
     * Lists only canView folders 
     * 
	 * @param user
	 * @param workFolder
	 * @param fileList
	 * @param exts
	 * @return
	 */
    public static Collection<File> listFiles(
        User user, File workFolder, Collection<File> fileList, String[] exts
    ){
        if ( !workFolder.exists() ) {
            logger.error("- error " + workFolder + " does not exist");
            return fileList;
        }

        if ( !workFolder.isDirectory() ) {
            logger.error("- error " + workFolder + " is not a directory");
            return fileList;
        }

        if ( !Repository.get().getFolder(workFolder).canView(user) ){
            return fileList;
        }
            
        File[] files = workFolder.listFiles();
        
        if (files != null){
            File file = null;
            for (int i = 0; i < files.length; i++) {
                file = files[i];
    
                if (file.isDirectory()) {
                    FileKeeper.listFiles(user, file, fileList, exts);
                } 
            }
            
            for (int i = 0; i < files.length; i++) {
                file = files[i];
                if ( !file.isDirectory() ) {
                    if (exts.length != 0){
                        if (StringUtil.isInStringArray(
                            exts, FileUtils.getExtension(file.getName()))
                        ){
                            fileList.add(file);
                        }                           
                    }
                    else{
                        fileList.add(file);
                    }
                }
            }
        }
        return fileList;
    }
    

    /**
     * Resoursively list files. (Secured). 
     * Lists only canView folders 
     * 
     * @param user
     * @param workFolder
     * @param fileList
     * @param exts
     * @return
     */
    public static Collection<File> listFolders(
        User user, File workFolder, Collection<File> folderList, String showMode)
    {
        if ( !workFolder.exists() ) {
            logger.error("- error " + workFolder + " does not exist");
            return folderList;
        }

        if ( !workFolder.isDirectory() ) {
            logger.error("- error " + workFolder + " is not a directory");
            return folderList;
        }

        if ( !Repository.get().getFolder(workFolder).canEdit(user) ){
            return folderList;
        }
            
        File[] files = workFolder.listFiles();
        File file = null;
        if (files != null){
            for (int i = 0; i < files.length; i++) {
                file = files[i];
                if (file.isDirectory() && ResourceFilter.get().isAllow(file, showMode)) {
                    if ( Repository.get().getFolder(file).canEdit(user) ){
                        folderList.add(file);
                        FileKeeper.listFolders(user, file, folderList, showMode);
                    }
                }
            }
        }
        return folderList;
    }
    
    public static Collection<File> listFolders(
            User user, File workFolder, Collection<File> folderList){
    	return listFolders(user, workFolder, folderList, "true");
    }
    
}
