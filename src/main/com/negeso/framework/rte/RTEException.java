package com.negeso.framework.rte;

/**
 * <p>Title: RTE</p>
 * <p>Description: RTE Component</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Negeso</p>
 * @author Alexander G. Shkabarnya
 * @version 1.0
 */

public class RTEException extends Exception{
  static String[] messages =
  {
  /* code 1 - 9  DB Errors
     code 11-19  Session Errors
     code 21-29  IO Errors

  */
  /* code      message                                 */
  /* 0  */     "Exception not initialized!!!",
  /* 1  */     "Database Environment initialization failed.",
  /* 2  */     "Can not retrieve data from the datadase.",
  /* 3  */     "",
  /* 4  */     "",
  /* 5  */     "",
  /* 6  */     "",
  /* 7  */     "",
  /* 8  */     "",
  /* 9  */     "",
  /* 10 */     "",
  /* 11 */     "Can not retrieve data from session.",
  /* 12 */     "",
  /* 13 */     "",
  /* 14 */     "",
  /* 15 */     "",
  /* 16 */     "",
  /* 17 */     "",
  /* 18 */     "",
  /* 19 */     "",
  /* 20 */     "",
  /* 21 */     "Can not retrieve data from file",
  /* 22 */     "Can not backup site",
  /* 23 */     "Can not restore site",
  /* 24 */     "No backup file",
  /* 25 */     "",
  /* 26 */     "",
  /* 27 */     "",
  /* 28 */     "",
  /* 29 */     ""
  };

  private int code = 0;

  public RTEException( int pCode ) {
    code = pCode;
  }

  public String getMessage()
  {
    return messages[ code ];
  }
}