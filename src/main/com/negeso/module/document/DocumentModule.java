/*
 * @(#)$Id: DocumentModule.java,v 1.5, 2007-01-09 18:41:51Z, Anatoliy Pererva$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.document;

import java.sql.Connection;
import java.text.MessageFormat;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.domain.CriticalException;
import com.negeso.module.document.domain.Category;

/**
 *
 * TODO Type description here
 * 
 * @version		$Revision: 6$
 * @author		Olexiy Strashko
 * 
 */
public class DocumentModule {
    private static Logger logger = Logger.getLogger( DocumentModule.class );


    private static DocumentModule instance = null;
    
    /**
     * 
     */
    public DocumentModule() {
        super();
    }

    public static DocumentModule get(){
        if ( DocumentModule.instance == null ){
            DocumentModule.instance = new DocumentModule();
        }
        return DocumentModule.instance;
    }
    
    
    public Category getRootCategory(Connection con) throws CriticalException{
        return Category.findById(con, new Long(1));
    }

    /**
     * @param long1
     * @return
     */
    public String getAminDocumentLink(Long documentId) {
        return MessageFormat.format("dc_document?document_id={0}", documentId);
    }

    /**
     * @param long1
     * @return
     */
    public String getAminCategoryLink(Long id) {
        return MessageFormat.format("document_module?category_id={0}", id);
    }

    public long getThumbnailWidth() {
        return new Long(Env.getIntProperty("documentModule.thumbnailWidth", 40));
    }

    public long getThumbnailHeight() {
        return new Long(Env.getIntProperty("documentModule.thumbnailHeight", 40));
    }
}
