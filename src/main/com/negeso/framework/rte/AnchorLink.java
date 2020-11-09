/*
 * @(#)$Id: AnchorLink.java,v 1.24, 2005-06-06 13:04:07Z, Stanislav Demchenko$
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.rte;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.net.*;
import javax.swing.text.html.*;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.negeso.framework.Env;
import com.negeso.framework.ResourceMap;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.controller.WebFrontController;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.page.command.GetEditablePageCommand;
import com.negeso.framework.page.command.GetPageCommand;

/**
 * RTE component page bookmarks producer
 * 
 * @version 	$Revision: 25$
 * @author 		Alexander G. Shkabarnya
 * @author 		Olexiy Strashko
 */

public class AnchorLink extends HttpServlet {

    private static Logger logger = Logger.getLogger(AnchorLink.class);

    private String currentPath = null;

    private String currentRTEPath = null;

    private String currentLanguageID = null;

    private String currentPageName = null;

    private static final String CONTENT_TYPE = "text/html;charset=UTF-8";

    //Initialize global variables
    public void init() throws ServletException {
        logger.debug("+-");
    }

    //Process the HTTP Get request
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcessing(request, response);
        logger.debug("+-");
    }

    //Process the HTTP Post request
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doProcessing(request, response);
        logger.debug("+-");
    }

    //Clean up resources
    public void destroy() {
        logger.debug("+-");
    }

    public void doProcessing( HttpServletRequest request, HttpServletResponse response) 
    	throws ServletException, IOException 
    {
        logger.debug("+");
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        String lBuffer = "";
        try {
            setPageAttributes(request);
            URL url = new URL(new String(request.getRequestURL()));
            currentPath = url.getProtocol() + "://" + url.getHost()
                    + (url.getPort() < 1 ? "" : ":" + url.getPort())
                    + request.getContextPath() + "/";
            currentRTEPath = currentPath;
        } catch (RTEException e) {
            logger.debug("-", e);
            EmergencyExit(out, e.getMessage());
            return;
        }

        lBuffer += "<html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'><title>Choose Bookmark</title>";
        lBuffer += "<style>";
        lBuffer += " body, p, td  { ";
        lBuffer += "   font: bold 10pt verdana;";
        lBuffer += " }  ";
        lBuffer += " button { ";
        lBuffer += "   width: 70;";
        lBuffer += " }  ";
        lBuffer += "</style>";
        lBuffer += "<script>";
        lBuffer += " function pSelect() { ";
        lBuffer += "  window.location.href='" + currentRTEPath
                + "anchorlink?page='+pageSelect.value;";
        lBuffer += " }   ";
        lBuffer += " function CancelClick() { ";
        lBuffer += "  window.returnValue=null; window.close();";
        lBuffer += " }  ";
        lBuffer += " function OKClick() { ";
        lBuffer += "  if ( anchorSelect.length == 0 ) { alert ('There are no bookmarks on this page!'); } ";
        lBuffer += "  else { window.returnValue= 'OK';";
        lBuffer += "  window.dialogArguments.vURL = '"
                + currentPath
                + "' + pageSelect.value + '#' + anchorSelect.value; window.dialogArguments.vTarget = targetSelect.value ; window.close(); } ";
        lBuffer += " }   ";
        lBuffer += "</script>";
        lBuffer += "</head>";
        lBuffer += "<body bgcolor=\"#E2F2E0\">";
        lBuffer += "<fieldset title=\"Choose Bookmark\" align=\"center\"><legend>Choose Bookmark</legend>";
        lBuffer += "<table border=\"0\" width=\"270\">";
        try {
            lBuffer = InsertPagesIntoSelect(lBuffer, request, response);

        } catch (RTEException e) {
            logger.debug("-", e);
            EmergencyExit(out, e.getMessage());
            return;
        }
        lBuffer += "</table></fieldset>";
        lBuffer += "<table border=\"0\"><tr><td width=\"290\"></td><td><button onclick=\"OKClick();\">OK</button><button onclick=\"CancelClick();\">Cancel</button>";
        lBuffer += "</td></tr></table>";
        lBuffer += "</font>";
        lBuffer += "</body></html>";

        out.println(lBuffer);
        logger.debug("-");
    }

    /**
     * Inserts tr, td and select tags. Inserts all pages into SELECT tags for
     * pages and anchors and selects current page
     */
    private String InsertPagesIntoSelect(
            String pBuffer, HttpServletRequest request, HttpServletResponse response) 
    	throws RTEException 
    {
        logger.debug("+");

        pBuffer += "<tr><td style='width:70;'>Page</td>";
        pBuffer += "<td style='width:200;' ><select id='pageSelect' style='width:200;' onchange='pSelect();'>";

        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            ResultSet lRS = conn.createStatement().executeQuery(
                    "SELECT filename, title FROM page " + " WHERE lang_id=" + 
                    currentLanguageID + " AND site_id=" + Env.getSiteId().longValue() + " ORDER BY title;"
            );
            while (lRS.next()) {
                pBuffer += "<option value='"
                        + lRS.getString("filename")
                        + "' "
                        + (lRS.getString("filename").equals(currentPageName) ? "SELECTED"
                                : " ") + " >" + lRS.getString("title")
                        + "</option>";
                //System.out.println(lRS.getString("filename"));
            }
            lRS.close();
        } catch (SQLException e) {
            logger.debug("-", e);
            throw new RTEException(2);
        } finally {
            DBHelper.close(conn);
        }
        pBuffer += "</select></td></tr>";

        pBuffer += "<tr><td style='width:70;'>Bookmark</td>";
        pBuffer += "<td style='width:200;' ><select id='anchorSelect' style='width:200;'>";

        try {
            
            // construct RequestContext for command invoking
            Map currentIO = new LinkedHashMap(2);
            currentIO.put(WebFrontController.HTTP_SERVLET_REQUEST, request);
            currentIO.put(WebFrontController.HTTP_SERVLET_RESPONSE, response);
            SessionData sdata = SessionData.getSessionData(request);
            Map modifiableMap = new LinkedHashMap(1);
            RequestContext crequest = new RequestContext(modifiableMap, sdata, currentIO);
            crequest.setParameter(GetPageCommand.INPUT_FILENAME, currentPageName);
            
            // prepare and invoke command to build page xml
            GetEditablePageCommand command = new GetEditablePageCommand();
    		command.setRequestContext(crequest);
    		ResponseContext cresponse = command.execute();
    		Document doc = (Document) cresponse.getResultMap().get(GetEditablePageCommand.OUTPUT_XML);
    		
            Templates templates = ResourceMap.getTemplates("SITE_XSL");
            Transformer transformer = templates.newTransformer(); 
            
            StringWriter sWriter = new StringWriter();
            transformer.setOutputProperty("method", "html");
            transformer.setOutputProperty("indent", "no");
            transformer.transform( new DOMSource(doc), new StreamResult(sWriter) );
            String pageHtml = sWriter.getBuffer().toString();
            StringReader sReader = new StringReader(pageHtml);
            
            /*HTMLEditorKit.Parser lParser = new HTMLKit().getParser();
            Callback lOptions = new Callback();
            lParser.parse(sReader, lOptions, true);
            pBuffer += lOptions.getOptionBuffer();*/

            BufferedReader bReader = new BufferedReader(sReader);
              String html = "";
              String buf = "";

              while ( (buf=bReader.readLine())!=null){
                  html += buf;
              }

              int index = 0;
              int pos = -1;
              int pos1 = -1;
              while (true){
                  pos = html.indexOf("<A class=RTEAnchor title=", index);
                  if (pos==-1)
                      break;
                  pos1 = html.indexOf(" ", pos+25);
                  if (pos1==-1)
                      break;
                  pBuffer += "<option value='" + html.substring(pos+25, pos1) + "'>" + html.substring(pos+25, pos1) + "</option>";
                  index=pos+5;
              }
        } catch (Exception e) {
            logger.error("-", e);
            throw new RTEException(2);
        }
        pBuffer += "</select></td></tr>";
        pBuffer += "<tr><td style='width:70;'>Target</td>";
        pBuffer += "<td style='width:200;' ><select id='targetSelect' style='width:200;'>";
        pBuffer += "<option value='_top' selected>This window</option>";
        pBuffer += "<option value='_blank'>New window</option>";
        pBuffer += "</select></td></tr>";
        logger.debug("-");
        return pBuffer;
    }

    private void EmergencyExit(PrintWriter pOut, String pMessage) {
        logger.debug("+");
        pOut.println("<html><head><title>Error Occured</title>");
        pOut
                .println("<script>function OkClick(){window.returnValue = null; window.close();}</script>");
        pOut.println("</head>");
        pOut.println("<body bgcolor=\"#BFDFBF\">");
        pOut.println("<H3>" + pMessage + "</H3>");
        pOut.println("<p>Try again, please.</p><br/><br/>");
        pOut.println("<button onclick='OkClick();'>OK</button>");
        pOut.println("</body></html>");
        logger.debug("-");
    }

    /** Sets currentLanguageID to current language */
    private void setPageAttributes(HttpServletRequest pRequest)
            throws RTEException {
        logger.debug("+");
        SessionData session = SessionData.getSessionData(pRequest);
        //********************************
        //HttpSession lSession = pRequest.getSession(true);
        //lSession.setAttribute("filename", new String("index.html"));
        // lSession.setAttribute("languageCode", new String("en"));
        //**************************************
        if (!session.isSessionStarted()) {
            logger.debug("- throwing: session is not started");
            throw new RTEException(11);
        }
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            String pageToSelect = pRequest.getParameter("page");
            if (pageToSelect == null) {
                ResultSet filenameRst = conn.createStatement().executeQuery(
                        "SELECT filename FROM page WHERE id='"
                                + session.getPageId() + "'");
                if (filenameRst.next()) {
                    pageToSelect = filenameRst.getString("filename");
                }
            }

            if (pageToSelect == null) {
                logger.debug("- throwing: current page is undefined");
                throw new RTEException(11);
            }

            this.currentPageName = pageToSelect;
            String langCode = session.getLanguageCode();
            if (langCode == null) {
                logger.debug("-");
                throw new RTEException(11);
            }

            ResultSet lRS = conn.createStatement().executeQuery(
                    "SELECT id FROM language WHERE code='" + langCode + "' ;");
            if (!lRS.next()) {
                logger.debug("- throwing: language id not found");
                throw new RTEException(2);
            }
            currentLanguageID = lRS.getString("id");
        } catch (SQLException e) {
            logger.debug("- SQLException", e);
            throw new RTEException(2);
        } finally {
            DBHelper.close(conn);
        }
        logger.debug("-");
    }
}