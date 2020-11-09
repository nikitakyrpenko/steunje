/*
 * @(#)$Id: AlbumUploadProfile.java,v 1.3, 2005-06-06 13:04:06Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.photoalbum.domain;




import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.UploadProfile;

/**
 *
 * Photo album module
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 4$
 */
public class AlbumUploadProfile {

	private static Logger logger = Logger.getLogger( AlbumUploadProfile.class );

	private static AlbumUploadProfile instance = null;

    static public AlbumUploadProfile get(){
    	if (instance == null){
    		instance = new AlbumUploadProfile();
    	}
    	return instance;
    }

	/**
	 * Register UploadProfile. 
	 */
	public void init() {
        logger.debug("+");
		UploadProfile profile = Repository.get().createUploadProfile();
		profile.setFileSetAmount(6); 

		profile.setMaxFileSizeKb( 
			Env.getIntProperty("photo-album.maxImageSizeKb", 10000) 
		);
		profile.setMaxImageWidth(
			Env.getIntProperty("photo-album.maxImageWidth", 6000) 
		);
		profile.setMaxImageHeight(
			Env.getIntProperty("photo-album.maxImageHeight", 5004) 
		);

		profile.setWidth(Env.getIntProperty("photo-album.thumbnailWidth", 100));
		profile.setHeight(Env.getIntProperty("photo-album.thumbnailHeight", 0));
	
		
		profile.setId( "photo_album" );
		
		Repository.get().registerUploadProfile( profile );
        logger.debug("-");
	}
}
