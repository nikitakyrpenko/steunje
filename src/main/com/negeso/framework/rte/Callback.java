package com.negeso.framework.rte;
import javax.swing.text.*;
import com.negeso.framework.Env;

import javax.swing.text.html.*;
import javax.swing.text.html.HTMLEditorKit.*;

import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.*;
import java.text.*;


/**
 * <p>Title: RTE</p>
 * <p>Description: RTE Component</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Negeso</p>
 * @author Alexander G. Shkabarnya
 * @version 1.0
 */

public class Callback extends ParserCallback{

  private static Logger logger = Logger.getLogger(Callback.class);

  private  ArrayList AnchorBuffer = new ArrayList();

  private String optionBuffer = "" ;
  private int count = 0;

  public void handleSimpleTag( HTML.Tag t, MutableAttributeSet a,int pos)
  {
    logger.debug("+-");
  }

  public void handleText(char[] data,int pos)
  {
    logger.debug("+-");
  }

  public void handleStartTag(HTML.Tag t,MutableAttributeSet a,int pos)
  {
    logger.debug("+");
    if(t == HTML.Tag.A && a.getAttribute(HTML.Attribute.NAME)!= null && a.getAttribute(HTML.Attribute.CLASS) != null && a.getAttribute(HTML.Attribute.CLASS).equals("RTEAnchor") )
    {
      AnchorBuffer.add( a.getAttribute(HTML.Attribute.NAME).toString());

      /*if ( count == 0 )
      {
        optionBuffer += "<option value='" + a.getAttribute(HTML.Attribute.NAME).toString() + "' selected >" + a.getAttribute(HTML.Attribute.NAME).toString() + "</option>";
      }
      else
      {
        optionBuffer += "<option value='" + a.getAttribute(HTML.Attribute.NAME).toString() + "'>" + a.getAttribute(HTML.Attribute.NAME).toString() + "</option>";
      }*/
      //count++;
    }
    logger.debug("-");
  }

  public String getOptionBuffer()
  {
    logger.debug("+-");

    Arrays.sort(AnchorBuffer.toArray());
    optionBuffer="";
    for (int i=0; i< AnchorBuffer.toArray().length; i++)
    {
        if (i == 0)
        {
          optionBuffer += "<option value='" + AnchorBuffer.get(i).toString() + "' selected >" + AnchorBuffer.get(i).toString() + "</option>";
        }
        else
        {
          optionBuffer += "<option value='" + AnchorBuffer.get(i).toString() + "'>" + AnchorBuffer.get(i).toString() + "</option>";
        }
      }

    return optionBuffer;
  }

}
