/*
 * @(#)$Id: ImageUploadProfile.java,v 1.2, 2005-06-06 13:04:40Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.wcmsattributes;


import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.UploadProfile;
import com.negeso.module.media_catalog.domain.Folder;


/**
 *
 * Image upload profile(Image frame purpose)
 * 
 * @version		$Revision: 3$
 * @author		Olexiy Strashko
 * 
 */
public class ImageUploadProfile {
    private static Logger logger = Logger.getLogger( ImageUploadProfile.class );
    private static final String DEFAULT_IMAGE_FOLDER = "picture_frame";
    public static final String PROFILE_ID = "picture_frame";

    private static ImageUploadProfile instance = null;

    static public ImageUploadProfile get(){
        if (instance == null){
            instance = new ImageUploadProfile();
        }
        return instance;
    }

    /**
     * Register UploadProfile. 
     */
    public void init() {
        logger.debug("+");
        UploadProfile profile = Repository.get().createUploadProfile();
        
        String folderName = Env.getProperty(
            "picture-frame.imageFolder", DEFAULT_IMAGE_FOLDER
        );
        Folder folder = null;
        try{
            folder = Repository.get().createFolder(
                Repository.get().getRootFolder(), 
                folderName
            );
        }
        catch(CriticalException e){
            logger.error("-error: unable to create folder:" + folderName, e);
            folder = Repository.get().getRootFolder(); 
        }
        
        if ( folder == null ){
            folderName = Repository.get().getRootFolder().getCatalogPath();
        }
        
        profile = Repository.get().createUploadProfile();
        profile.setWorkingFolder(folder.getCatalogPath());
        profile.setId( PROFILE_ID );
        Repository.get().registerUploadProfile( profile );

        logger.debug("-");
    }
}
