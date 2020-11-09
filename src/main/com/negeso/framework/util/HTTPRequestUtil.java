/*
 * @(#)${TestHTTPRequestUtil.java}		@version 	${24-Dec-2003}
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;


/**
 * <p>Title: HTTPRequestUtil - utility for httpservlet request parsing. </p>
 * <p>Description: Parsing of httprequests. Both simple and multipart.
 * IMPORTANT NOTE: only POST request can be parsed</p>
 *
 * Main method parseRequestParameters returns Map. There is actually LinkedMap,
 * so parameters is in order as were added.
 *
 * Usual post and multipart requests can be handled.
 *
 * Temporary path for uploaded files can be specified.
 * Otherwise default system directory will be used to save uploaded files.
 *
 * @version		1.0
 * @author		Oleg Lyebyedyev
 */

public class HTTPRequestUtil {

    static Logger logger = Logger.getLogger(HTTPRequestUtil.class);

    /**
     * Buffer size for reading Inputstream
     * in case of NOT multipart request
     */
    private static final int BUF_MAX_SIZE = 25600;

    /**
     * Part of HTTP content type header.
     */
    public static final String MULTIPART = "multipart/";

    /**
     * HTTP content type header name.
     */
    public static final String CONTENT_TYPE = "Content-type";

    /**
     * Maximum size of the uploaded file cannot be more than this value (100)
     */
    private static final long MAX_FILE_SIZE = 10485760;

    /**
     * Maximum size of the threashold size cannot be more that this value (10 Mb)
     */
    private static final int MAX_THREASHOLD = 10485760;

    /**
     * The max size in bytes to be stored in memory.
     * If the file size will be more the file is stored to disk.
     */
    private int sizeThreshold = 51200;

    /**
     * The maximum allowed upload size, in bytes.
     */
    private long sizeMax = 104857600; //100 Mb

    /**
     * Temporary path to save uplaoded files
     */
    private String pathTmp = null;

    /**
     * Default constructor.
     * Default max filesize (after that it will be saved to temp directory) - 10K
     * Not specified temporary file location
     */
    public HTTPRequestUtil() {}

    /**
     * @param sizeThreshold The max size in bytes to be stored in memory.
     * @param sizeMax       The maximum allowed upload size, in bytes.
     * @param pathTmp       The location where the files should be stored (temporary).
     */
    public HTTPRequestUtil(int sizeThreshold, long sizeMax, String pathTmp)
            throws Exception {
        logger.debug("+");

        if (!this.checkThreashold(sizeThreshold)) {
            String err_message =
                    "Incorrect threshold value. Must be >= 0 and <= "
                            + MAX_THREASHOLD;
            logger.error("- " + err_message);
            throw new Exception(err_message);
        }

        if (!this.checkMaxFileSize(sizeMax)) {
            String err_message =
                    "Incorrect maximum uploaded file size. Must be > 0 and <="
                            + MAX_FILE_SIZE;
            logger.error("- " + err_message);
            throw new Exception(err_message);
        }

        if (!this.checkPath(pathTmp)) {
            String err_message =
                    "Incorrect or not existent temporary directory";
            logger.error("- " + err_message);
            throw new Exception(err_message);
        }

        this.sizeThreshold = sizeThreshold;
        this.sizeMax = sizeMax;
        this.pathTmp = pathTmp;

        logger.debug("-");
    }

    /**
     * Entry point to parse request parameters.
     * If request is Multipart request then this method calls parseMultipart
     * method.
     * @param req
     * @return map of parameters
     */
    public Map parseRequestParameters(HttpServletRequest req, String encoding) throws IOException {
        logger.debug("+");
        if (!checkRequest(req)) {
            throw new IOException("Not supported or incomplete request type. Only POST can be processed");
        }

        if (FileUploadBase.isMultipartContent(req)) {
            logger.debug("-");
            return parseMultipart(req, encoding);
        }

        BufferedReader input = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String parameters = "";
        String str;

        while (null != ((str = input.readLine())))	{
            parameters += str;
        }


        LinkedHashMap map = new LinkedHashMap();
        NegesoRequestUtils.parseParameters(map, parameters, encoding);

        logger.debug("-");
        return map;
    }




    /*************************************************************************
     * Checks request for POST and not Multipart.
     * If non POST or MULTIPART in content type it returns false.
     * @param req - request
     * @return true if POST and non Multipart
     *************************************************************************/
    private boolean checkRequest(HttpServletRequest req) {

        logger.debug("+");

        if (req == null) {
            logger.debug("- false");
            return false;
        }

        //check request method. Only POST must be
        String method = req.getMethod();
        if (method == null || method.trim().length() == 0) {
            logger.debug("- false");
            return false;
        }
        if (method.toUpperCase().compareTo("POST") != 0) {
            logger.debug("- false");
            return false;
        }

        logger.debug("- true");

        return true;
    } //----------- end of checkRequest method -------------------------------

    /*************************************************************************
     * Parse multipart request.
     * @param req
     * @param inEncoding
     * @param outEncoding
     * @return
     *************************************************************************/
    private LinkedHashMap parseMultipart(
            HttpServletRequest req,
            String encoding)
            throws IOException {
        logger.debug("+");
        LinkedHashMap hmap = new LinkedHashMap();
        // Create a new file upload handler
        try {
            DiskFileUpload upload = new DiskFileUpload();

            if (pathTmp != null) {
                upload.setRepositoryPath(pathTmp);
            }
            upload.setSizeThreshold(sizeThreshold);
            upload.setSizeMax(sizeMax);

            upload.setHeaderEncoding(encoding);
            // Parse the request
            List
                    /* FileItem */
                    items = upload.parseRequest(req);

            // Process the uploaded items
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                String fieldName = item.getFieldName();
                //prepare array to store value and put array into the map
                Object currVal = hmap.get(fieldName);
                Object[] newArr = null;
                Object[] oldArr = null;
                if (item.isFormField()) { //simple form field, not file
                    if (currVal != null) {
                        oldArr = (String[]) currVal;
                        newArr = new String[oldArr.length + 1];
                        System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
                    }
                    else {
                        newArr = new String[1];
                    }

                    newArr[newArr.length - 1] = item.getString(encoding);
                }
                else { //uploaded file
                    if (currVal != null) {
                        oldArr = (FileItem[]) currVal;
                        newArr = new FileItem[oldArr.length + 1];
                        System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
                    }
                    else {
                        newArr = new FileItem[1];
                    }

                    newArr[newArr.length - 1] = item;
                }
                hmap.put(fieldName, newArr);
            }
        }
        catch (FileUploadException ex) {
            IOException ioex = new IOException(ex.toString() + ". " + ex.getMessage());
            logger.error("-", ioex);
            throw ioex;
        }

        logger.debug("-");
        return hmap;
    } // -------- end of parseMultipart class ---------------

    /**
     *
     * @param sizeMax
     */
    public void setMaxFileSize(long sizeMax) throws Exception {
        logger.debug("+");
        if (!this.checkMaxFileSize(sizeMax)) {

            Exception ex = new Exception(
                    "Incorrect maximum file size. Must be > 0 and <="
                            + MAX_FILE_SIZE);
            logger.error("-",ex);
            throw ex;
        }
        logger.debug("-");
        this.sizeMax = sizeMax;
    }

    /**
     *
     * @return Max file size that can be uploaded
     */
    public long getMaxFileSize() {
        logger.debug("+-");
        return sizeMax;
    }

    /**
     *
     * @param sizeThreshold
     */
    public void setThresholdSize(int value) throws Exception {
        logger.debug("+");
        if (!this.checkThreashold(value)) {
            Exception ex = new Exception(
                    "Incorrect threshold value. Must be >= 0 and <= "
                            + MAX_THREASHOLD);
            logger.error("-", ex);
            throw ex;
        }
        logger.debug("-");
        this.sizeThreshold = value;
    }

    /**
     *
     * @return Max file size that can be stored file in memeory
     * without saving to temporary disk location
     */
    public int getThresholdSize() {
        logger.debug("+-");
        return sizeThreshold;
    }

    /**
     *
     * @return Path where file will be temporary saved
     */
    public String getTemporaryPath() {
        logger.debug("+-");
        return pathTmp;
    }

    /**
     * Check temporary directory
     * @param value
     */
    public void setTemporaryPath(String value) throws Exception {
        logger.debug("+-");
        if (!this.checkPath(value)) {
            throw new Exception("Incorrect or not existent temporary directory");
        }

        this.pathTmp = value;
    }

    /**
     * Check value as threashold
     * @param value
     * @return
     */
    private boolean checkThreashold(int value) {
        logger.debug("+-");
        if (value > MAX_THREASHOLD || value < 0) {
            return false;
        }
        else {
            return true;
        }

    }

    /**
     * Check value as MaxFileSize
     * @param value
     * @return
     */
    private boolean checkMaxFileSize(long value) {
        logger.debug("+-");
        if (value > MAX_FILE_SIZE || value < 1) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Check temporary repository path
     * @param value
     * @return
     */
    private boolean checkPath(String value) {
        logger.debug("+-");
        if (value == null) {
            return false;
        }

        File f = new File(value);

        return f.isDirectory();
    }

} //end of class
