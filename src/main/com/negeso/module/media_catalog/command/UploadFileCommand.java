/*
 * @(#)$Id: UploadFileCommand.java,v 1.16, 2007-02-19 09:20:08Z, Olexiy Strashko$
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.media_catalog.command;

import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.domain.User;
import com.negeso.framework.security.SecurityGuard;
import com.negeso.framework.util.RequestParametersException;
import com.negeso.module.media_catalog.CustomFileItem;
import com.negeso.module.media_catalog.FileKeeper;
import com.negeso.module.media_catalog.Repository;
import com.negeso.module.media_catalog.domain.FileResource;
import com.negeso.module.media_catalog.domain.Folder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

/**
 * Upload file via MediaCatalog
 *
 * @author Olexiy.Strashko
 * @version $Revision: 17$
 */
public class UploadFileCommand extends AbstractCommand {

    public static final String INPUT_FILE = "uploadedFile";
    public static final String INPUT_CURRENT_DIR = "currentDir";

    /* Added to request parameters */
    public static final String INPUT_ERROR_TEXT = "errorMessage";
    public static final String INPUT_USER_TEXT = "userMessage";


    private static Logger logger = Logger.getLogger(UploadFileCommand.class);

    /* (non-Javadoc)
     * @see com.negeso.framework.command.Command#execute()
     */
    public ResponseContext execute() {
        logger.debug("+");
        ResponseContext response = new ResponseContext();
        RequestContext request = getRequestContext();

        String currentDir = request.getParameter(INPUT_CURRENT_DIR);

        User user = request.getSessionData().getUser();
        if (user == null) {
            try {
                user = User.findById(Long.valueOf(request.getParameter("userId")));
            } catch (ObjectNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }


        if (!SecurityGuard.isContributor(user)) {
            response.setResultName(RESULT_ACCESS_DENIED);
            logger.debug("-");
            return response;
        }


        Folder parentFolder = Repository.get().getFolder(currentDir);
        // Check security rights for file uploading
        if (!parentFolder.canEdit(user)) {
            request.setParameter(INPUT_ERROR_TEXT, "Security: operation is denied");
        } else {
            /* upload image */
            FileItem fileItem = request.getFile(INPUT_FILE);
            if (fileItem == null) {
                logger.error("-File for upload is not specified");
                response.setResultName(RESULT_FAILURE);
                return response;
            }
            String name = request.getParameter("file_name");
            if (StringUtils.isNotBlank(name)) {
                fileItem = new CustomFileItem(fileItem, name);
            }

            String[] ext = {};
            File file = null;
            boolean niceResult = true;
            boolean isReplace = false;
            try {

                String[] tokens = fileItem.getName().split("\\\\");
                if (tokens.length < 1) {
                    throw new RequestParametersException("Invalid uploading file name. Please try another one.");
                }
                String fileName = FileKeeper.prepareFileName(tokens[tokens.length - 1]);

                FileResource fileResource = Repository.get().getFileResource(
                        currentDir + "/" + fileName
                );
                if (fileResource.exists()) {
                    logger.info("Info: exists");
                    if (fileResource.getParentFolder().canEdit(user)) {
                        fileResource.getFile().delete();
                    }
                    isReplace = true;
                }

                file = Repository.get().saveFileItemSafe(
                        user,
                        fileItem,
                        currentDir,
                        ext,
                        false
                );
                if (file == null) {
                    request.setParameter(INPUT_ERROR_TEXT, "Unable to upload empty file");
                    niceResult = false;
                }
            } catch (RequestParametersException e) {
                request.setParameter(INPUT_ERROR_TEXT, e.getMessage());
                niceResult = false;
            }

            if (niceResult) {
                file.setLastModified(System.currentTimeMillis());
                if (isReplace) {
                    Repository.get().getCache().clearImageInfoCache();
                    request.setParameter(INPUT_USER_TEXT, "File '" +
                            file.getName() +
                            "' successfully replaced. Please refresh the window to see the changes"
                    );
                } else {
                    request.setParameter(INPUT_USER_TEXT, "File '" +
                            file.getName() +
                            "' successfully uploaded into '" +
                            currentDir + "'"
                    );
                }
            }

            //Give permissions 777 to upload file
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

        ListDirectoryCommand listCommand = new ListDirectoryCommand();
        listCommand.setRequestContext(request);
        logger.debug("-");
        return listCommand.execute();
    }
}
