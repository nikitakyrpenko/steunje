package com.negeso.framework.rte;

import javax.swing.text.html.*;

import org.apache.log4j.Logger;


/**
 * <p>Title: RTE</p>
 * <p>Description: RTE Component</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Negeso</p>
 * @author Alexander G. Shkabarnya
 * @version 1.0
 */

public class HTMLKit extends HTMLEditorKit
{
  private static Logger logger = Logger.getLogger(HTMLKit.class);

  public HTMLEditorKit.Parser getParser()
  {
    logger.debug("+-");
    return super.getParser();
  }
}