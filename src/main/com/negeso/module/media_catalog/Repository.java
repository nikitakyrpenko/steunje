/*
 * @(#)$Id: Repository.java,v 1.63, 2007-02-19 09:20:28Z, Olexiy Strashko$
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
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.QNameCache;

import com.negeso.framework.Env;
import com.negeso.framework.LocalizedException;
import com.negeso.framework.LocalizedMessage;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.mailer.MailClient;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.site.Site;
import com.negeso.framework.util.FileUtils;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.framework.util.StringUtil;
import com.negeso.framework.util.Timer;
import com.negeso.module.media_catalog.domain.FileResource;
import com.negeso.module.media_catalog.domain.Folder;
import com.negeso.module.media_catalog.domain.FolderDomain;
import com.negeso.module.media_catalog.domain.ImageInformation;
import com.negeso.module.media_catalog.domain.ImageResource;
import com.negeso.module.media_catalog.domain.Resource;
import com.negeso.module.media_catalog.domain.ResourceFilter;
import com.negeso.module.media_catalog.generator.RepositoryXmlBuilder;


/**
 * Resource repository. Singleton.
 *
 * @author      Olexiy.Strashko  
 * @version 	$Revision: 64$'
 *  
 */
public class Repository{
	private static Logger logger = Logger.getLogger(Repository.class);
    
	public static final Long DEFAULT_CONTAINER = null;
	public static final Long DEFAULT_FOLDER_ID = new Long(0);
	
	public static String ENV_SHARED_MEDIA_PATH_ID = "shared.media.path";
	
	public static String DEFAULT_SHARED_MEDIA_PATH = "repository\\shared";

    public static final String SORT_MODE_NAME = "sname";
	public static final String SORT_MODE_TYPE = "stype";
	public static final String SORT_MODE_SIZE = "ssize";
	public static final String DEFAULT_SORT_MODE = SORT_MODE_NAME;
	
	
    public static final String FLASH_GALLERY_VIEW_MODE = "flash";
	public static final String IMAGE_GALLERY_VIEW_MODE = "image_gallery";
	public static final String FILE_MANAGER_VIEW_MODE = "file_manager";
	public static final String IMAGE_LIST_VIEW_MODE = "image_list";
	public static final String DEFAULT_VIEW_MODE = FILE_MANAGER_VIEW_MODE;
	public static final String DEFAULT_SHOW_MODE = "true";

	public static final String BROWSE_ACTION_MODE = "browser";
	public static final String CHOOSE_ACTION_MODE = "chooser";
	public static final String DEFAULT_ACTION_MODE = BROWSE_ACTION_MODE;

	private int fileArrayInitialSize = 100;

	private QNameCache qNameCache = new QNameCache();
	
	private Folder rootFolder = null;
	
	private MessageFormat catalogPathFormatter = null;
	private Object[] catalogPathFormatterArgs = null;
	private String rootFolderUrl = null;
	
	/**
	* Holds singleton instance
	*/
	private static Repository instance = null;

    private Set imageTypeSet;

    public static final String DEFAULT_CONTAINER_NAME = "";

	private Map<String, UploadProfile> uploadParametersMap = null;


	/**
	* prevents instantiation
	*/
	private Repository() {
	}

	/**
	* Returns the singleton instance.
	* @return	the singleton instance
	*/
	static public Repository get() {
		if (instance == null) {
			instance = new Repository();
			/*
	    	UploadParameters phParams = instance.createUploadParameters();
	    	phParams.setFileSetAmount(6); 
	    	phParams.setMaxFileSizeKb(1000);
	    	phParams.setMaxImageWidth(800);
	    	phParams.setMaxImageHeight(600);
	    	phParams.setWorkingFolder("media/photos");
	    	
	    	phParams.setWidth(123);

	    	
	    	phParams.setId("photo_album");
	    	
	    	instance.registerUploadParameters( phParams );
	    	*/
		}
		return instance;
	}
	
	
	public static void refreshRepository(){
		instance = new Repository();
	}
	
	public File getFileByCatalogPath(String catalogPath){
	    return new File(Repository.get().getRealPath(catalogPath));
	}
	
	/**
	 * Get Folder fy File
	 * 
	 * @param file
	 * @return
	 */
	public Folder getFolder(File file) {
	    logger.debug("+ -");
		return new Folder(file);
	} 

	/**
	 * Get Folder by Catalog Path
	 * 
	 * @param catalogPath
	 * @return
	 */
	public Folder getFolder(String catalogPath) {
		return this.getFolder(this.getFileByCatalogPath(catalogPath));
	}

	/**
	 * Get Folder by domainId
	 * 
	 * @param catalogPath
	 * @return
	 * @throws CriticalException
	 */
	public Folder getFolder(Long domainId) throws CriticalException {
		Connection con = null;
		Folder folder = null;
		try{
			con = DBHelper.getConnection();
			FolderDomain domain = FolderDomain.findById(con, domainId);
			if ( domain != null ){
				folder = this.getFolder( domain.getPath() );
			}
		}
		catch(SQLException e){
			logger.error("-error for domainId" + domainId, e);
		}
		finally{
			DBHelper.close(con);
		}
		return folder;
	}

	/**
	 * Create folder with Root as parent 
	 * 
	 * @param folderName
	 * @return
	 */
    public Folder createFolder(String folderName) {
        return this.createFolder(this.getRootFolder(), folderName, false);
    }

    /**
     * Create folder
     * 
     * @param parent
     * @param folderName
     * @return
     * @throws CriticalException
     */
    public Folder createFolder(
        Folder parent, String folderName) 
        throws CriticalException
    {
        return this.createFolder(parent, folderName, false);
    }

    /**
	 * Creates new folder by parent path and new folder name.
     * Has unique flag
	 * 
	 * @param parent
	 * @param folderName
	 * @return
	 * @throws CriticalException
	 */
	public Folder createFolder(
        Folder parent, String folderName, boolean isCreateUnique) 
        throws CriticalException
    {
	    // create NICE folder
	    folderName = FileKeeper.prepareFileName(folderName);
        if ( isCreateUnique ){
            logger.info("creating uniq name for:" + folderName);
            folderName = FileKeeper.getUniqueFolderName(
                this.getRealPath(parent.getCatalogPath()), folderName
            );
        }
        
		String realPath = Repository.get().getRealPath(
			parent.getCatalogPath() + 
			File.separator +
			folderName 
		);
		File file = new File(realPath);
		if (file.exists()){
			//return existent folder
			return this.getFolder(
				parent.getCatalogPath() + 
				File.separator +
				folderName 
			);
		}

        if ( !file.mkdir() ){
            throw new CriticalException(
                    "Unable to create '" + folderName + "' folder"
            );
		}
        return this.getFolder(file);
	}
	

	/**
	 * Create folder by parent, new name and containerId   
	 * 
	 * @param parent
	 * @param folderName
	 * @param containerId
	 * @return
	 * @throws CriticalException
	 */
	public Folder createFolder(
		Folder parent, String folderName, Long containerId) 
		throws CriticalException
	{
		Folder folder = this.createFolder(parent, folderName, true);
		FolderDomain domain = folder.getDomain();
		domain.setContainerId( containerId );
		domain.setPath( folder.getCatalogPath() );
		
		Connection con = null;
		try{
			con = DBHelper.getConnection();
			if ( domain.getId() == null ){
				domain.insert(con);
			}
			else{
				domain.update(con);
			}
			this.getCache().clearFolderDomainCache();
		} 
		catch (SQLException e) {
			logger.error("-error", e);
			throw new CriticalException(e);
		}
		finally{
			DBHelper.close(con);
		}
		
		return folder;
	}

	/**
	 * Get Resource by File
	 * 
	 * @param file
	 * @return
	 */
	public Resource getResource(File file) {
	    if (file.isDirectory()){
	        return this.getFolder(file);
	    }
	    if ( this.isImage(file) ){
	        return this.getImageResource(file);
	    }
	    return new FileResource(file);
	}
	
	/**
	 * Get Resource by Catalog Path 
	 * 
	 * @param catalogPath
	 * @return
	 */
	public Resource getResource(String catalogPath) {
	    logger.debug("+ -");
		return this.getResource(this.getFileByCatalogPath(catalogPath));		
	}
	
	/**
	 * Get resource by real path
	 * 
	 * @param realPath
	 * @return
	 */
	public Resource getResourceByRealPath(String realPath) {
	    logger.debug("+ -");
		File file = new File(realPath);
		return this.getResource(file);		
	}

	/**
	 * Get ImageResource by File
	 * 
	 * @param file
	 * @return
	 */
	public ImageResource getImageResource(File file){
	    logger.debug("+ -");
		return new ImageResource(file);
	}
	
	/**
	 * Get ImageResource by CatalogPath 
	 * 
	 * @param catalogPath
	 * @return
	 */
	public ImageResource getImageResource(String catalogPath){
	    logger.debug("+ -");
		return getImageResource(this.getFileByCatalogPath(catalogPath));
	}

    /**
     * Get FileResource
     * 
     * @param file
     * @return
     */
    public FileResource getFileResource(File file) {
        return new FileResource(file);
    }

	public FileResource getFileResource(String catalogPath){
	    logger.debug("+ -");
		return getFileResource(this.getFileByCatalogPath(catalogPath));
	}

	/**
	 * 
	 * @return
	 */
	public Element getAvailableFilesElement(User user, String resourceTypeFilter){
		return this.getAvailableFilesElement(
		    user,
			Resource.getExtensionsByResourceType(resourceTypeFilter)
		); 
	}

	/**
	 * 
	 * @return
	 */
	public Element getAvailableFilesElement(User user, String[] exts){
	    
	    Timer timer = new Timer();
		if (exts == null){
			exts = new String[0]; 
		}

		/* Create root element */
		Namespace ngNamespace = XmlHelper.getNegesoDom4jNamespace();
		Element filesRoot = DocumentHelper.createElement(
			qNameCache.get("resource-set", ngNamespace)
		);

		/* Build list of files */
		this.getFilesElement(
		    filesRoot,
			user, 
			this.getRealPath(this.getRootPath()), 
			exts
		);

		logger.info("Available files time:" + timer.stop());
		return filesRoot;
	}
	
	/**
	 * 
	 * @return
	 */
	public Element getAvailableFoldersElement(User user, String showMode) {
		/* Build list of folders */
		List filesList = this.getFoldersElement(
		    user,
			this.getRealPath(this.getRootPath()), showMode
		);
		
		/* Create root element */
		Namespace ngNamespace = XmlHelper.getNegesoDom4jNamespace();
		Element filesRoot = DocumentHelper.createElement(
			qNameCache.get("folder-set", ngNamespace)
		);
		
		/* Iterate over list and build xml tree */
		for (Iterator i = filesList.iterator(); i.hasNext(); ){
			filesRoot.add((Element)i.next());						
		}
		return filesRoot;
	}
	
	public Element getAvailableFoldersElement(User user){
		return getAvailableFoldersElement(user, "true");
	}


	/**
	 * 
	 * @param workDir
	 * @return
	 */
	public List getFoldersElement(User user, String workDir, String showMode) {
		File workDirFile = new File(workDir);
		Collection<File> fileList = new ArrayList<File>();
		fileList.add(this.getRootFolder().getFile());
		if (workDirFile.isDirectory() && workDirFile.exists()){
			fileList = FileKeeper.listFolders(user, workDirFile, fileList, showMode);
			logger.debug("Parameter work dir valid directory");
		}
		/* generate xml */
		Namespace ngNamespace = XmlHelper.getNegesoDom4jNamespace();

		ArrayList<Element> elementList = new ArrayList<Element>();
		Element currFileEl = null;
		File currFile = null;
		for (Iterator iter = fileList.iterator(); iter.hasNext();){
			currFile = (File)iter.next();
			if (currFile.isDirectory()){
				currFileEl = DocumentHelper.createElement(
					qNameCache.get("folder", ngNamespace)
				)
					.addAttribute("name", currFile.getName())
					.addAttribute("path", this.getCatalogPath(currFile))
					.addAttribute("id", "" + 1)
				;
				elementList.add(currFileEl);
			}
		}
		return elementList;
	}


	/**
	 * 
	 * @param workDir
	 * @return
	 */
	public void getFilesElement(Element parent, User user, String workDir, String[] exts){
		File workDirFile = new File(workDir);
		Collection<File> fileList = new TreeSet<File>(); 
		
        if (workDirFile.isDirectory() && workDirFile.exists()){
			fileList = FileKeeper.listFiles(
			    user,  
			    workDirFile, 
			    fileList, 
			    exts
			);
		}
		this.setFileArrayInitialSize(fileList.size());
        
        if (logger.isInfoEnabled()){
            logger.info("Files count:" + fileList.size());
        }
        
		//Collections.sort(fileList, new FileNameComparator());
		
		/* generate xml */
		Namespace ngNamespace = XmlHelper.getNegesoDom4jNamespace();

		Element currFileEl = null;
		File currFile = null;
		for (Iterator iter = fileList.iterator(); iter.hasNext();){
			currFile = (File)iter.next();
			if (currFile.isFile()){
				currFileEl = DocumentHelper.createElement( 
					qNameCache.get("resource", ngNamespace)
				)
					.addAttribute("name", currFile.getName())
					.addAttribute("path", this.getCatalogPath(currFile))
					.addAttribute("id", "" + 1)
				;
				parent.add(currFileEl);
			}
		}
	}

	
	public long getMaxFileSize(){
		return Configuration.get().getMaxFileSize();
	}
	
	/**
	 * Get anount of space used by different media, in bytes  
	 * 
	 * @return
	 */
	public long getUsedSpace() {
	    logger.debug("+ -");
		return this.getRootFolder().getSize();
	}

	/**
	 * Get free space, available for media, in bytes 
	 * 
	 * @return
	 */
	public long getFreeSpace() {
	    logger.debug("+ -");
		return this.getTotalSpace() - this.getUsedSpace();
	}
	

	/**
	 * Get total space available for media, in bytes
	 * 
	 * @return
	 */
	public long getTotalSpace() {
	    logger.debug("+ -");
		return Configuration.get().getRepositorySize();
	}
 
	/**
	 * Get max allowed size for uploading to repository. Size is formatted
	 * to String in KB units.
	 * 
	 * @return String representing size.
	 */
	public long getMaxFileSizeString() {
	    logger.debug("+ -");
		return Configuration.get().getMaxFileSize();
	}

	/**
	 * Get root path
	 * 
	 * @return
	 * @throws CriticalException 
	 */
	public String getRootPath() {
        return "media";
//        try{
//            return "media/";
//        }
//        catch(CriticalException e){
//            logger.error("-ERROR", e);
//            throw new IllegalStateException("Error while getSite");
//        }
        
        
//		if (this.rootCatalogPath == null){
//			this.rootCatalogPath = Env.getProperty(
//				Repository.ENV_SHARED_MEDIA_PATH_ID,
//				DEFAULT_SHARED_MEDIA_PATH
//			);
//			FileUtils.createDirIfNotExists(this.getRealPath(this.rootCatalogPath));
//		}
//		return this.rootCatalogPath;
	}

	/**
	 * Return Root Resource. If error occures - CriticalException is thrown.
	 * 
	 * @return The Root Resource Object 
	 * @throws CriticalException
	 */
	public synchronized Folder getRootFolder() {
	    logger.debug("+");
		if (this.rootFolder == null){
			if (!new File(this.getRealPath(this.getRootPath())).exists()){
				logger.error("Repository Root Path '" + this.getRootPath() + 
					"' not exists. Created new path.");
			}
			this.rootFolder = this.getFolder(
				FileUtils.createDirIfNotExists(
					this.getRealPath(this.getRootPath())
				)
			);   
		}
	    logger.debug("-");
		return this.rootFolder;
	}


	/**
	 * 
	 * @param str
	 * @return
	 */
	public String getRealPath(String str){
        String retStr = null;
        try{
            retStr = 
                Env.getSite().getRealPath("/") + 
                str
            ;
        }
        catch(CriticalException e){
            logger.error("Error:", e);
        }
        return retStr;
		//return Env.getRealPath("/") + str;
	}

	/**
	 * @param string
	 * @return
	 */
	public String getCatalogPath(File file) {
		String firstUrl = null;
		try{
			firstUrl = file.toURL().toString();
			if ( this.rootFolderUrl == null ){
				this.rootFolderUrl = this.getRootFolder().getFile().toURL().toString();
			}
		}
		catch (MalformedURLException e){
			logger.error("- error", e);
		}
		//setup formatter
		if ( this.catalogPathFormatter == null ){
			this.catalogPathFormatter = new MessageFormat(
				this.getRootPath() + "/{0}"
			);
		}
		
//        return this.catalogPathFormatter.format(
//            firstUrl.replaceFirst( this.rootFolderUrl, "" )   
//        );
        
        //setup params 
		if ( this.catalogPathFormatterArgs == null ){
			this.catalogPathFormatterArgs = new Object[1];
		}

		this.catalogPathFormatterArgs[0] = firstUrl.replaceFirst( 
			this.rootFolderUrl, 
			"" 
		); 
		return this.catalogPathFormatter.format( 
			this.catalogPathFormatterArgs 
		);
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
	public File saveFileItemSafe(
	    User user,
	    UploadProfile profile,
	    FileItem fileItem
	) 
		throws RequestParametersException, AccessDeniedException
	{
	    return FileKeeper.saveFileItemSafe(
	            user,
	            fileItem, 
	            profile.getWorkingFolder(), 
	            new String[0], 
	            true,
	            profile.getMaxFileSize()
	    );
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
	public File saveFileItemSafe(
	    User user,
	    FileItem fileItem, 
		String workDir, 
		String[] availableExtensions,
		boolean createUniqeNameIfExists
	) 
		throws RequestParametersException
	{
	    return FileKeeper.saveFileItemSafe(
	            user,
	            fileItem, 
	            workDir, 
	            availableExtensions, 
	            createUniqeNameIfExists,
				null
	    );
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
	public File saveFileItemSafe(
	    User user,
	    FileItem fileItem, 
		String workDir, 
		String[] availableExtensions,
		boolean createUniqeNameIfExists,
		Long allowedFileSize
	) 
		throws RequestParametersException, AccessDeniedException
	{
	    return FileKeeper.saveFileItemSafe(
	            user,
	            fileItem, 
	            workDir, 
	            availableExtensions, 
	            createUniqeNameIfExists, 
				allowedFileSize 
	    );
	}

	
	/**
	 * Prepare file name to <code>valid</code> repository name.
	 * 
	 * @param fileName
	 * @return
	 * @throws RepositoryException
	 */
	public ImageInformation getImageInfo(String fileName) throws RepositoryException{
		return RepositoryCache.get().getImageInfo(fileName);
	}

	/**
	 * @return
	 */
	public RepositoryCache getCache() {
		return RepositoryCache.get();
	}
	

	
	public LocalizedMessage getMessage(String constantId){
        return new LocalizedMessage(constantId);
    }
    
    public LocalizedException getException(String constantId){
        return new LocalizedException(constantId);
    }

	/**
	 * 
	 * @return
	 */
	private Set getImageTypeExtSet(){
	    if ( this.imageTypeSet == null ){
	        this.imageTypeSet = StringUtil.stringArrayToSet(
	                Configuration.imageTypeExtensions
	        );
	    }
	    return this.imageTypeSet;
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public boolean isImage( File file ){
	    if ( this.getImageTypeExtSet().contains( FileUtils.getExtension(file)) ){
	        return true;  
	    }	    
	    return false;
	}
	
	/**
	 *  
	 * 
	 * @param filterType - Filter type string (One of View modes)
	 * @return Comparator Object
	 */
	public synchronized Set getExtensionsByResourceType(String filterType){
//		if (filterType == null){
//			return null;
//		}
//		if (filterType.equalsIgnoreCase(Repository.IMAGE_GALLERY_VIEW_MODE)){
//		    if ( this.imageTypeSet == null ){
//		        this.imageTypeSet = StringUtil.stringArrayToSet(
//		                Configuration.imageTypeExtensions
//		        );
//		    }
//			return this.imageTypeSet;
//		}
//		if (filterType.equalsIgnoreCase(Repository.FLASH_GALLERY_VIEW_MODE)){
//		    return StringUtil.stringArrayToSet( Configuration.flashTypeExtensions );
//            
//		}
		return null;
	}

    /**
     * @return Returns the fileArrayInitialSize.
     */
    public int getFileArrayInitialSize() {
        return fileArrayInitialSize;
    }
    /**
     * @param fileArrayInitialSize The fileArrayInitialSize to set.
     */
    public void setFileArrayInitialSize(int fileArrayInitialSize) {
        this.fileArrayInitialSize = fileArrayInitialSize;
    }
    
    public RepositoryXmlBuilder getXBuilder(){
        return RepositoryXmlBuilder.get();
    }
    
    /**
     * 
     * @return
     */
    public Configuration getConfig(){
    	return Configuration.get();
    }
    
    /**
     * Create default mass upload parameters
     * 
     * @return
     */
    public UploadProfile createUploadProfile(){
		UploadProfile params = new UploadProfile();

		params.setWorkingFolder(
			Repository.get().getRootFolder().getCatalogPath()
		);
		
		params.setFileSetAmount( this.getConfig().getMassUploadAmount() );
		params.setMaxFileSize( this.getConfig().getMaxFileSize());
		params.setMaxImageWidth( this.getConfig().getMaxImageWidth() );
		params.setMaxImageHeight( this.getConfig().getMaxImageHeight() );
		params.setId(null);

		return params;
    }
    
    public boolean registerUploadProfile(UploadProfile uploadParameters){
    	if ( uploadParameters == null ){
    		return false;
    	}
    	if ( uploadParameters.getId() == null ){
    		return false;
    	}
    	
    	
    	this.getUploadProfileMap().put(uploadParameters.getId(), uploadParameters); 
    	return true;
    }
    
    public Map<String, UploadProfile> getUploadProfileMap(){
    	if ( this.uploadParametersMap == null ){
    		this.uploadParametersMap = new HashMap<String, UploadProfile>();
    	}
    	return this.uploadParametersMap;
    }
    
    public UploadProfile getUploadProfile(String id){
    	return (UploadProfile) this.getUploadProfileMap().get(id);
    }

	public void clearCache() {
		this.getCache().clearFolderDomainCache();
		this.getCache().clearImageInfoCache();
	}
	
	public void sendEmailFreeSpace (String usedFS){		
		MailClient mailClient = new MailClient();		
		try {
			mailClient.sendMessage(
					Env.getProperty(Configuration.EMAIL_TO).split(";"),
					Env.getProperty(Configuration.EMAIL_FROM), 
					Env.getProperty(Configuration.EMAIL_SUBJECT),
					(usedFS.equals("100"))?
							Env.getProperty(Configuration.SECOND_EMAIL_BODY)  + " " +  Env.getHostName():
							usedFS + "% " + Env.getProperty(Configuration.FIRST_EMAIL_BODY) + " " +  Env.getHostName(), 
					null,
					null, 
					"text/html", 
					null
                );
		} catch (Exception  e) {
			logger.error(e);
		}			
	}
}	

