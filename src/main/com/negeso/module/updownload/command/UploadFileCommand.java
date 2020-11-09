/*
 * @(#)$Id: UploadFileCommand.java,v 1.16, 2007-01-15 17:38:13Z, Svetlana Bondar$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.updownload.command;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.security.AccessDeniedException;
import com.negeso.framework.util.FileUtils;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.module.media_catalog.Configuration;
import com.negeso.module.media_catalog.Repository;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;


/**
 * File uploader command.
 *
 * @author Olexiy.Strashko
 * @version $Revision: 17$
 */
public class UploadFileCommand extends AbstractCommand {
    private static Logger logger = Logger.getLogger(UploadThumbnailCommand.class);

    // for tree list support
    public static final String INPUT_WORKING_FOLDER = "workfolder";
    public static final String INPUT_IMAGE_FILE = "selectedFile";
    public static final String INPUT_SOURCE = "fileSource";
    public static final String INPUT_EXISTENT_FILE = "existentFile";
    public static final String INPUT_MODE = "mode";

    private String workingFolder = null;
    private String mode = null;
    private String inputSource = null;
    private File file = null;
    private boolean isDiskSource = true;

    /**
     * Perform standard security check.
     *
     * @return
     */
    protected boolean securityCheck(ResponseContext response) {
        RequestContext request = getRequestContext();
        if (!request.getSessionData().isAuthorizedUser()) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.debug("-");
            return false;
        }
        return true;
    }

    /**
     * Dispatch request and get standard parameters.
     * Setup command bean properties:
     * - workingFolder (if null set to media root path). If folder
     * not exists - its created. If folder is not set it is a root
     * media catalog path.
     * - mode (if null set to default mode)
     *
     * @return true if dispatching success.
     * @throws RequestParametersException
     * @throws AccessDeniedException
     * @throws CriticalException
     */
    protected boolean dispatchRequest(RequestContext request)
            throws RequestParametersException, AccessDeniedException {
        // process working folder
        this.setWorkingFolder(request.getParameter(INPUT_WORKING_FOLDER));

        if (this.getWorkingFolder() == null) {
            this.setWorkingFolder(Repository.get().getRootPath());
        }

        FileUtils.createDirIfNotExists(Repository.get().getRealPath(
                this.getWorkingFolder()
        ));

        // process mode
        this.setMode(request.getParameter(INPUT_MODE));

        dispatchFile(request);

        return true;
    }


    protected String[] getAllowedExtensions() {
        return Configuration.allExtensions;
    }

    /**
     * Dispatch file from request. File can be passed in two ways:
     * - by FileItem (input parameter: INPUT_IMAGE_FILE)
     * - by link to existent file (input parameter:  INPUT_EXISTENT_FILE)
     * Set file property of the command.
     *
     * @param request
     * @return true if file was dispatched properly
     * @throws RequestParametersException
     * @throws AccessDeniedException
     * @throws CriticalException
     * @throws ObjectNotFoundException
     */
    protected boolean dispatchFile(RequestContext request) throws
            RequestParametersException, AccessDeniedException {
        // get input source param
        this.setInputSource(request.getString(INPUT_SOURCE, GetFileUploaderFace.INPUT_SOURCE_UPLOADED));

        // setup boolean source
        if (this.getInputSource().equalsIgnoreCase(GetFileUploaderFace.INPUT_SOURCE_UPLOADED)) {
            this.setDiskSource(true);
        } else {
            this.setDiskSource(false);
        }

        if (this.getInputSource().equalsIgnoreCase(GetFileUploaderFace.INPUT_SOURCE_UPLOADED)) {
            // use upload file
            FileItem fileItem = request.getFile(INPUT_IMAGE_FILE);
            if (fileItem == null) {
                throw new RequestParametersException(
                        "Uploaded file is empty. Unable to perform operation"
                );
            }

            this.setFile(Repository.get().saveFileItemSafe(
                    request.getSession().getUser(),
                    fileItem,
                    this.getWorkingFolder(),
                    this.getAllowedExtensions(),
                    true
            ));
        } else {
            // use existent file
            String existentFile = request.getParameter(
                    INPUT_EXISTENT_FILE
            );
            if (existentFile == null) {
                throw new RequestParametersException(
                        "Existent file is not set. Choose another one"
                );
            }
            this.setFile(
                    Repository.get().getResource(existentFile).getFile()
            );
        }
        return true;
    }


    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */
    public ResponseContext execute() {
        RequestContext request = getRequestContext();
        ResponseContext response = new ResponseContext();

        // pass security check
        if (!securityCheck(response)) {
            logger.debug("-");
            return response;
        }

        Element page = null;
        boolean success = false;
        String errorMessage = null;
        try {
            this.dispatchRequest(request);
            success = true;
        } catch (RequestParametersException e) {
            logger.debug("e+", e);
            errorMessage = e.getMessage();
        } catch (AccessDeniedException e) {
            errorMessage = e.getMessage(
                    request.getSession().getInterfaceLanguageCode()
            );
        }

        try {
            page = XmlHelper.createPageElement(request);
        } catch (CriticalException e) {
            logger.error("-error", e);
            response.setResultName(RESULT_FAILURE);
            return response;
        }
        Xbuilder.setAttr(page, "title", "Upload file result");

        if (success) {
            buildSuccessResultXml(page, request);
        } else {
            buildErrorResultXml(page, request);

            Repository.get().getXBuilder().getErrorMessage(
                    page,
                    errorMessage
            );

            Repository.get().getXBuilder().getAvailableFoldersElement(
                    page,
                    request.getSession().getUser()
            );

            Repository.get().getXBuilder().getAvailableFilesElement(
                    page,
                    request.getSession().getUser(),
                    Configuration.allExtensions,
                    true
            );

        }

        response.setResultName(RESULT_SUCCESS);
        response.getResultMap().put(OUTPUT_XML, page.getOwnerDocument());

        return response;
    }

    protected void buildErrorResultXml(Element page, RequestContext request) {
        logger.debug("+-");
        Element option = Repository.get().getXBuilder().getFileUploadOption(
                page,
                workingFolder,
                mode
        );

    }

    protected void buildSuccessResultXml(Element page, RequestContext request) {
        logger.debug("+-");
        Element result = Xbuilder.addEl(page, "file-upload-result", null);
        Xbuilder.setAttr(
                result,
                "real-file",
                Repository.get().getCatalogPath(this.getFile())
        );
    }

    /**
     * @return
     */
    public String getWorkingFolder() {
        return workingFolder;
    }

    /**
     * @param string
     */
    public void setWorkingFolder(String string) {
        workingFolder = string;
    }

    /**
     * @return
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param string
     */
    public void setMode(String newMode) {
        if (newMode == null) {
            this.mode = GetFileUploaderFace.DEFAULT_MODE;
        }
        this.mode = newMode;
    }

    /**
     * @return
     */
    public String getInputSource() {
        return inputSource;
    }

    /**
     * @param string
     */
    public void setInputSource(String string) {
        if (string == null) {
            inputSource = GetFileUploaderFace.INPUT_SOURCE_UPLOADED;
        } else {
            inputSource = string;
        }
    }

    /**
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file
     */
    public void setFile(File file) {
        this.file = file;
        this.setFilePermission(file);
    }

    public void setFilePermission(File file) {
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);

        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_WRITE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);

        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);

        try {
            assert file != null;
            Files.setPosixFilePermissions(file.toPath(), perms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return Returns the isDiskSource.
     */
    public boolean isDiskSource() {
        return isDiskSource;
    }

    /**
     * @param isDiskSource The isDiskSource to set.
     */
    public void setDiskSource(boolean isDiskSource) {
        this.isDiskSource = isDiskSource;
    }
}
