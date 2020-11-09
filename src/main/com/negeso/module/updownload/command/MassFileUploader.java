/*
 * @(#)$Id: MassFileUploader.java,v 1.4, 2005-06-06 13:04:50Z, Stanislav Demchenko$
 *
 * Copyright (c) 2005 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload.command;

import java.io.File;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.framework.util.StringUtil;
import com.negeso.module.media_catalog.Configuration;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.FileResource;
import com.negeso.module.updownload.domain.UploadEnv;

/**
 *
 * Mass file uploader
 * 
 * @version		$Revision: 5$
 * @author		Olexiy Strashko
 * 
 */
public class MassFileUploader extends AbstractCommand {
    private static Logger logger = Logger.getLogger( MassFileUploader.class );
    
    private static String INPUT_ACTION = "action";
    
    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */
    public ResponseContext execute() {
        RequestContext request = this.getRequestContext();
        ResponseContext response = new ResponseContext();
        
        // pass security check
        if ( !SecurityGuard.isContributor(request.getSession().getUser()) ){
            response.setResultName( AbstractCommand.RESULT_ACCESS_DENIED );
            return response;
        }

        String action = request.getNonblankParameter(INPUT_ACTION);
        Element page = null;
        try{
            page = XmlHelper.createPageElement(request);    
            UploadEnv form = UploadEnv.buildFromRequest(request, "file", null);
            if ("upload".equals(action)){
                this.doUpload(request, form, page);
                Element formEl = (Element)page.getLastChild();
                Xbuilder.setAttr(formEl, "render-mode", "result");
            }
            else{
                this.renderForm(form, page);
                Element formEl = (Element)page.getLastChild();
                Xbuilder.setAttr(formEl, "render-mode", "show-form");
            }
            
            response.getResultMap().put(OUTPUT_XML, page.getOwnerDocument());
            response.setResultName(RESULT_SUCCESS);
        }
        catch( AccessDeniedException e ){
            logger.warn("-access denied", e);
            this.getErrors().add(e.getMessage());
        } 
        catch (CriticalException e) {
            response.setResultName( AbstractCommand.RESULT_FAILURE );
        }
        
        return response;
    }

    /**
     * 
     * @param form
     * @param page
     */
    private void renderForm(UploadEnv form, Element page) {
        Element params = form.buildElement(page);
        
        Element fileSet = Xbuilder.addEl(params, "file-set", null);
        Element fileEl = null;
        for ( int i = 0; i < form.getFileSetAmount(); i++ ){
            fileEl = Xbuilder.addEl(fileSet, "file", null);
            Xbuilder.setAttr(fileEl, "id", "" + i);
        }
    }

    /**
     * 
     * @param request
     * @throws RequestParametersException
     * @throws AccessDeniedException
     */
    private void doUpload(RequestContext request, UploadEnv form, Element parent) 
        throws AccessDeniedException 
    {
        Element params = form.buildElement(parent);

        Element fileSet = Xbuilder.addEl(params, "file-set", null);
        Element fileEl = null;

        File curFile = null;
        FileItem curFileItem = null;
        for ( int i = 0; i < form.getFileSetAmount(); i++ ){

            curFileItem = request.getFile( "file" + "_" + i );
            if (curFileItem == null) {
                continue;
            }
            if ( curFileItem.getSize() == 0 ){
                //empty file
                continue;
            }
            
            fileEl = Xbuilder.addEl(fileSet, "file", null);
            Xbuilder.setAttr(fileEl, "id", "" + i);

            try{
                curFile = Repository.get().saveFileItemSafe(
                    request.getSession().getUser(),    
                    curFileItem, 
                    form.getWorkingFolder(),
                    new String[0],
                    true,
                    new Long(form.getMaxFileSize()) 
                );
                
                if ( curFile == null ) {
                    // empty file
                    continue;
                }
                
                this.buildResult(fileEl, curFile);
                
                logger.info("file: " + curFile.getName());
            }
            catch( RequestParametersException e ){
                logger.error("file(" + i + ") " + curFileItem.getName());
                this.getErrors().add( e.getMessage() ); 
                Xbuilder.setAttr(fileEl, "result", "error");
                Xbuilder.setAttr(fileEl, "error", e.getMessage());
                Xbuilder.setAttr(fileEl, "file-name", curFileItem.getName());
            }
        }
    }
    
    /**
     * 
     * @param parent
     * @param image
     * @param thumbnail
     * @return
     */
    private Element buildResult(Element parent, File file){
        Xbuilder.setAttr(parent, "result", "OK");
        FileResource imageRes = Repository.get().getFileResource(file);
        Xbuilder.setAttr(parent, "file-name", imageRes.getName());
        Xbuilder.setAttr(parent, "file-path", imageRes.getCatalogPath());
        Xbuilder.setAttr(parent, "file-size", StringUtil.formatSizeInKb(imageRes.getSize()) );
        return parent;
    }
}
