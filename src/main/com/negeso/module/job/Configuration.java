/*
 * @(#)$Id: Configuration.java,v 1.5, 2005-06-06 13:04:14Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.job;


import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.UploadProfile;
import com.negeso.module.media_catalog.domain.Folder;


/**
 *
 * Job module configuration
 * 
 * @author        Olexiy Strashko
 * @version       $Revision: 6$
 */
public class Configuration {
	private static Logger logger = Logger.getLogger( Configuration.class );

    private static final String FEEDBACK_EMAIL = "job@negeso.com";
    private static final String APPLICATION_EMAIL_SUBJECT = "Job: new applicant";
    private static final String DEFAULT_CV_FOLDER = "job_cv";

	
    public static final String START_PAGE_DEPARTMENTS_VIEW = "DepartmentsView";
    public static final String START_PAGE_ALL_VACANCIES_VIEW = "AllVacanciesView";
    public static final String START_PAGE_GENERAL_APPLICATION_FORM = "GeneralApplicationForm";
    
    
    public static final String ORDER_NUMBER = "orderNumber";
    public static final String TITLE = "title";

    private UploadProfile profile = null;
    
	/**
	 * 
	 */
	public Configuration() {
		super();
	}

	/**
	 * @return
	 * @throws CriticalException 
	 */
	public UploadProfile getUloadProfile() throws CriticalException {
        logger.debug("+");
        if ( this.profile == null ){
            String folderName = Env.getProperty("job.cvFolder", DEFAULT_CV_FOLDER);
            Folder folder = Repository.get().createFolder(
                Repository.get().getRootFolder(), 
                folderName
            );
            
            if ( folder == null ){
                folderName = Repository.get().getRootFolder().getCatalogPath();
            }
			
            this.profile = Repository.get().createUploadProfile();
			profile.setWorkingFolder(folder.getCatalogPath());
			profile.setId( "job_module" );
			Repository.get().registerUploadProfile( profile );
        }
        logger.debug("-");
        return this.profile;
	}
    
    /**
     * @return
     */
    public String getFeedbackEmail() {
        return Env.getProperty("job.feedbackEmail", FEEDBACK_EMAIL);
    }

    /**
     * @return
     */
    public String getApplicationEmailSubject() {
        return Env.getProperty("job.applicationMailSubject", APPLICATION_EMAIL_SUBJECT);
    }
}
