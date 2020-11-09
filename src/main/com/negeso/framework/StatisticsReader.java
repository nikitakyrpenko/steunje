package com.negeso.framework;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.framework.security.SecurityGuard;

public class StatisticsReader extends HttpServlet {
    
    
    private static final Logger logger = Logger.getLogger(StatisticsReader.class);
    
    
    /** Protected folder, where statistics graphs and reports reside */
    private final String basePath = ResourceMap.getRealPath("STATISTICS_DIR");
    
    
    /**
     * Entry point to obtain site visiting statistics.
     * If user is not authorized, shows 'access denied' page.
     * Otherwise, handles control over to handleImageRequest or
     * handlePageRequest (depending on request parameters).
     */
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException
    {
        logger.debug("+");
        response.setDateHeader("Expires", 0);
        SessionData session = SessionData.getSessionData(request);
        session.startSession();
        
        if(!isUserValid(session.getUserId())){
            response.getWriter().println(
                "Access is denied. " +
                "Would you like to <a href='admin/index.html'>enter</a>?");
            logger.warn("- User is not authorized");
            return;
        }
        if(request.getParameter("image") != null){
            handleImageRequest(request, response);
        }else{
            handlePageRequest(request, response);
        }
        response.flushBuffer();
        logger.debug("-");
        }


    /**
     * Checks if user can view statistics (user mask &gt; 1)
     *
     * @param userId id (presumably obtained from session)
     * @return
     */
    private boolean isUserValid(int userId)
    {
        logger.debug("+");
        try {
            User user = User.findById(new Long(userId));
            if(!SecurityGuard.isContributor(user)){
                logger.error("- not a contributor, userId: " + userId);
                return false;
            }
            logger.debug("- valid user");
            return true;
        } catch (Exception e) {
            logger.error("- Exception, userId: " + userId, e);
            return false;
        }
    }


    /** Just calls doGet() */
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
        ) throws ServletException, IOException
    {
        doGet(request, response);
    }


    /** Serves statistics graphs from a protected folder */
    private void handleImageRequest(
        HttpServletRequest request,
        HttpServletResponse response
        ) throws IOException
    {
        logger.debug("+");
        String picture = basePath + request.getParameter("image");
        InputStream in = null;
        try{
            OutputStream out = response.getOutputStream();
            in = new BufferedInputStream(new FileInputStream(picture));
            byte[] buffer = new byte[2048];
            int count = 0;
            while((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            out.close();
        }catch(Exception e){
            response.setContentType("text/plain");
            response.getWriter().println("File " + picture + " not found");
            logger.error("Picture " + picture + " with statistics not found", e);
            return;
        } finally {
            in.close();
        }
        logger.debug("+");
    }
    
    
    /** Serves statistics html page from a protected folder 
     * @throws IOException*/
    private void handlePageRequest(
        HttpServletRequest request,
        HttpServletResponse response) throws IOException
    {
        logger.debug("+");
        String month = request.getParameter("month");
        String page =
            month == null ? "index.html" : "usage_" + month + ".html";
        page = basePath + page;

        try{
            String statistics = FileUtils.readFileToString(new File(page), "UTF-8");
            response.setContentType("text/html");
            response.getWriter().print(fixLinks(statistics));
            logger.debug("-");
        }catch(Exception e){
            response.setContentType("text/plain");
            response.getWriter().println("File not found: " + page);
            logger.error("File " + page + " not found", e);
        }
    }
    
    
    /**
     * Changes links to statistics graphs and pages so that subsequent
     * requests for statistics were served by the servlet itself.
     *
     * @param str source html of the statistics page
     */
    private String fixLinks(String str)
    {
        logger.debug("+");
        str = str.replaceAll("src\\W*=\\W*\"([^\"]*)", "src=\"?image=$1");
        str = str.replaceAll("SRC\\W*=\\W*\"([^\"]*)", "src=\"?image=$1");
        str = str.replaceAll("\"usage_(\\d*).html", "\"?month=$1");
        logger.debug("-");
        return str;
    }


}

