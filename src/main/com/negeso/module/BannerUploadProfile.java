/*
 * @(#)$Id: BannerUploadProfile.java$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.UploadProfile;
import com.negeso.module.media_catalog.domain.Folder;

public class BannerUploadProfile {

	private static Logger logger = Logger.getLogger( BannerUploadProfile.class );

    private static final String DEFAULT_BANNER_FOLDER = "banner_module";
    public static final String PROFILE_ID = "banner_module";
	
	private static BannerUploadProfile instance = null;

    static public BannerUploadProfile get(){
    	if (instance == null){
    		instance = new BannerUploadProfile();
    	}
    	return instance;
    }

	/**
	 * Register UploadProfile. 
	 */
	public void init() {
        logger.debug("+");

        String folderName = Env.getProperty("banner.imageFolder",
				DEFAULT_BANNER_FOLDER);
        Folder folder = null;
        try {
        	folder = Repository.get().createFolder(
					Repository.get().getRootFolder(), folderName);
        }
        catch (CriticalException e){
        	logger.error("-error: unable to create folder:" + folderName, e);
        	folder = Repository.get().getRootFolder(); 
        }
            
        if (folder == null) {
        	folderName = Repository.get().getRootFolder().getCatalogPath();
        }
            
        UploadProfile profile = Repository.get().createUploadProfile();
        profile.setWorkingFolder(folder.getCatalogPath());
        profile.setId( PROFILE_ID );
        Repository.get().registerUploadProfile( profile );

        logger.debug("-");
	}
}
