package com.negeso.module.help;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.User;
import com.negeso.framework.security.SecurityGuard;

/**
 * 
 * @TODO
 * 
 * @author		Mariia Samarina
 * @version		Revision: 
 *
 */

@SuppressWarnings("serial")
public class HelpServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(HelpServlet.class);
	
	private static final String HELP_SITE_URL = "http://help.phidias.nl/";
	private static final String ERROR_HTML = "/error.html";
	
	private static final String TEXT_HTML = "text/html";
	private static final String UTF8 = "utf-8";
	private static final String TEXT_HTML_CHARSET_UTF8 = "text/html; charset=UTF-8";
	
	private static final String OLD_HREF = "href=\"/";
	private static final String NEW_HREF = "href=\"/admin/help/";
	private static final String OLD_SRC = "src=\"/";
	private static final String NEW_SRC = "src=\"/admin/help/";
	private static final String OLD_SRC_MEDIA = "src=\"../media/";
	private static final String NEW_SRC_MEDIA = "src=\"/admin/help/media/";
	
	private static final String EXPIRES = "Expires";
	
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {		
		HttpClient client = new DefaultHttpClient();
		
		String langCode = SessionData.getInterfaceLanguageCode(req);
		String helpLink = req.getPathInfo();
		String currLang = "_" + langCode + ".";
		String defaultLang = "_nl.";
		
		if(langCode != "nl"){
			helpLink = helpLink.replace(defaultLang, currLang);
		}
		
		HttpGet httpReq = new HttpGet(Env.getProperty("HELP_SITE_URL", HELP_SITE_URL) + helpLink);
		try {
			Object user = req.getSession().getAttribute(SessionData.USER_ATTR_NAME);			
			if(user instanceof User && SecurityGuard.isAdministrator((User)user)){				
				
				HttpResponse response = client.execute(httpReq);				
				if( response.getEntity().getContentType().getValue().toString().contains(TEXT_HTML) ){
					String htmlStr = convertStreamToString(response.getEntity().getContent());
						
					htmlStr = htmlStr.replace(OLD_HREF, NEW_HREF);
					htmlStr = htmlStr.replace(OLD_SRC, NEW_SRC);
					htmlStr = htmlStr.replace(OLD_SRC_MEDIA, NEW_SRC_MEDIA);
						
					Writer writer = resp.getWriter();
					writer.write(htmlStr);
					writer.close();
				} 
				else{			
					response.getEntity().writeTo(resp.getOutputStream());
				}
			}
			else{
				resp.setDateHeader(EXPIRES, 0);
				String errorHtml = Env.getRealPath(ERROR_HTML);
				resp.setContentType(TEXT_HTML_CHARSET_UTF8);
				PrintWriter out = null;
				out = resp.getWriter();
				out.print(FileUtils.readFileToString(new File(errorHtml), UTF8));				
				logger.error("Permission denied for page " + req.getPathInfo());				
			}				
		} catch (ClientProtocolException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);			
		}		
    }
	
	 public String convertStreamToString(InputStream is) throws IOException {			
			if (is != null) {
				Writer writer = new StringWriter();

				char[] buffer = new char[1024];
				try {
					Reader reader = new BufferedReader(new InputStreamReader(is,
							UTF8));
					int n;
					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} finally {
					is.close();
				}
				return writer.toString();
			} else {
				return "";
			}
	 }				
}


