/*
 * @(#)FileUtil.java       @version	17.03.2004
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.util;

import java.io.File;

import com.negeso.framework.domain.CriticalException;

/**
 * File utilities.
 *
 * @version 	17.03.2004
 * @author 	Olexiy.Strashko
 */
public abstract class FileUtils {
    
    /**
     * Creates directory if its not exist. 
     * 
     * @param dirPath
     * @return file size
     */
    public static File createDirIfNotExists(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()){
          if ( !file.mkdirs() ){        	  
	          throw new CriticalException(
	                  "Unable to create '" + dirPath + "' folder"
	          );
          }

        }
        return file;
    }
    
    /**
     * Get extension from filename by String
     * 
     * @param filename
     * @return file extension
     */
    public static String getExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return -1 == index ? "" : filename.substring(index + 1, filename.length());
    }
    
    /**
     * Get extension from filename by File
     * 
     * @param filename
     * @return file extension
     */
    public static String getExtension(File file) {
        int index = file.getName().lastIndexOf('.');
        if (-1 == index) {
            return "";
        }
        return file.getName().substring(index + 1).toLowerCase();
    }
    
    /**
     * Remove extension from filename 
     * 
     * @param filename
     * @return
     */
    public static String removeExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return -1 == index ? filename : filename.substring(0, index);
    }
    
    /**
     * Get path from filename.
     * ie.
     * <pre>
     * a/b/c.txt --> a/b
     * a.txt     --> ""
     * </pre>
     *
     * @param filepath the filepath
     * @param fileSeparatorChar the file separator character to use
     * @return the filename minus path
     */
    public static String getPath(String filepath) {
        int index = filepath.lastIndexOf(File.separatorChar);
        return -1 == index ? "" : filepath.substring(0, index);
    }
    
}
