package com.negeso.framework.rte;

import java.io.*;

import org.apache.log4j.Logger;


/**
 * <p>Title: RTE</p>
 * <p>Description: RTE Component</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Negeso</p>
 * @author Alexander G. Shkabarnya
 * @version 1.0
 */
public class FileCopy {

  private static Logger logger = Logger.getLogger(FileCopy.class);

  /**
   *
   * @param in
   * @param out
   * @throws java.lang.Exception
   */
  public static void copyFile(File in, File out) throws Exception {
    logger.debug("+");

    FileInputStream fis  = new FileInputStream(in);
    FileOutputStream fos = new FileOutputStream(out);
    byte[] buf = new byte[1024];
    int i = 0;
    while((i=fis.read(buf))!=-1) {
      fos.write(buf, 0, i);
      }
    fis.close();
    fos.close();

    logger.debug("-");
  }

}