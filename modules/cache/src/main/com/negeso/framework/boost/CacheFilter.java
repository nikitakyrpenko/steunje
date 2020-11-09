package com.negeso.framework.boost;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.Language;


/**
 * Caches pages as files in application's /WEB-INF/generated/html_cache/.
 * If the folder does not exist, it will be created.
 * 
 * @author sdemchenko
 */
public class CacheFilter implements Filter {
    
    private static Log log = LogFactory.getLog(CacheFilter.class);
    
    private File cacheDir;
    
    private static final ThreadLocal<String> URL = new ThreadLocal<String>();
    
    private List<String> noCachedPages = new LinkedList<String>();

    private static final String DEFAULT_BROWSER = "MSIE";

    private static final String[] SUPPORTED_BROWSERS = {
		"Firefox",
		"Opera",
		"Safari",
		"Konqueror",
		DEFAULT_BROWSER,
    };
    
    public static final String HTML_CACHE_NOCACHED_PAGES = "HTML_CACHE_NOCACHED_PAGES";
    public static final String HTML_CACHE_EXPIRE_MIN = "HTML_CACHE_EXPIRE_MIN";
    public static final String HTML_CACHE_EXPIRE_MIN_VALUE = "360";
    
    public static final String COMMAND = "command";
    private static final String LANG_CODE_PATH_FORMAT = "/%s/";
    
    public void destroy() {
    	LogFactory.release(Thread.currentThread().getContextClassLoader());
	}

    /** Setup (and create if necessary) cacheDir */
    public void init(FilterConfig cfg) {
        ServletContext ctx = cfg.getServletContext();
        cacheDir = new File(ctx.getRealPath("/WEB-INF/generated/html_cache/"));
        try {
	        if (cacheDir.exists()) {
	        	FileUtils.cleanDirectory(cacheDir);
	        } else {
	        	FileUtils.forceMkdir(cacheDir);
	        }
        } catch (IOException e) {
        	e.printStackTrace();
        }
        reloadCachingSettings();
    }
    
    private void reloadCachingSettings() {
    	Env.setSiteId(1L);
   		String propertyNoCachedPages = Env.getProperty(HTML_CACHE_NOCACHED_PAGES);
   		if (propertyNoCachedPages != null) {
   			String[] pages = propertyNoCachedPages.split(",|;");
            noCachedPages.clear();
   			for (String page : pages) {
   				if (page.trim().length() > 0) {
   					noCachedPages.add(page.trim());
   				}
        	}
   		}
    }
    
    public void doFilter(ServletRequest arg1, ServletResponse arg2, FilterChain chain)
    throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) arg1;
        HttpServletResponse response = (HttpServletResponse) arg2;
        URL.set(request.getRequestURL().toString());
        debug("+");
        if (isContentModificationRequest(request)) {
        	reloadCachingSettings();
            FileUtils.cleanDirectory(cacheDir);
            chain.doFilter(request, response);
            debug("- content is being modified");
            return;
        }
        String filename = getCacheFilename(request);
        if (filename == null) {
            chain.doFilter(request, response);
            debug("- non-cacheable resourse or too complex request");
            return;
        }
        File file = new File(cacheDir, filename);
        String content = readCacheToString(file);
        if (content == null) {
            buildPageAndPutToCache(request, response, chain, file);
        } else {
            PrintWriter writer = null;
            try {
            	response.setContentType("text/html; charset=UTF-8");
                response.setDateHeader("Expires", 0);
                writer = response.getWriter();
                writer.print(content);
            } catch (Exception e) {
                error("unexpected output error ", e);
            } finally {
                IOUtils.closeQuietly(writer);
            }
        }
        debug("-");
        URL.remove();
    }

    /** Finds out if the request may cause content modification. */
	private boolean isContentModificationRequest(HttpServletRequest request) {
		String uri = request.getRequestURI();
		if (!authenticated(request)) {
			return false;
		}
		if (uri.startsWith("/adminwebservice")) {
			return "POST".equals(request.getMethod());
		}
		if (!uri.startsWith("/admin/")) {
			return false;
		}
		if ("POST".equals(request.getMethod())) {
			return true;
		}
		return
			request.getQueryString() != null &&
			(request.getQueryString().contains("command") ||
					request.getQueryString().contains("act") ||
					request.getQueryString().contains("action") ||
					request.getQueryString().contains("todo"));
	}

    /**
     * Returns a cached page as string. Returns null if the cache
     * <li> does not exist
     * <li> is obsolete
     * <li> cannot be read
     * <li> is being built by a concurrent thread/process
     */
    private String readCacheToString(File file) {
        debug("+");
        if (!file.exists() || !isMature(file)) {
            debug("- cache does not exist or is not ready");
            return null;
        }
        long cacheMinutes = Long.valueOf(Env.getProperty(HTML_CACHE_EXPIRE_MIN, HTML_CACHE_EXPIRE_MIN_VALUE));
        long now = System.currentTimeMillis();
        if (now - file.lastModified() > cacheMinutes * 60 * 1000) {
            file.delete();
            debug("- cache expired");
            return null;
        }
        BufferedInputStream in = null;
        try {
			in = new BufferedInputStream(new FileInputStream(file));
			debug("-");
        	return IOUtils.toString(in, "UTF-8");
        } catch (IOException e) {
            error("- IOException", e);
            return null;
        } finally {
        	IOUtils.closeQuietly(in);
        }
    }
    
    private void buildPageAndPutToCache(
            HttpServletRequest request,
            final HttpServletResponse response,
            FilterChain chain,
            final File out2)
    throws IOException {
        debug("+");
        HttpServletResponse teeResponse = null;
        try {
        	
            /* Subclass response to return a caching output stream */
            teeResponse = 
            	new HttpServletResponseWrapper(response) {
	                
	                private ServletOutputStream teeOut;
	                
	                @Override
	                public ServletOutputStream getOutputStream()
	                throws IOException {
	                    if (teeOut == null) {
	                        ServletOutputStream out1 = response.getOutputStream();
							teeOut = new TeeServletOutputStream(out1, out2);
	                    }
	                    return teeOut;
	                }
	                
	                @Override
	                public PrintWriter getWriter() throws IOException {
	                	return new PrintWriter(
                			new OutputStreamWriter(getOutputStream(), "UTF-8"));
	                }
	                
	            };
            
            chain.doFilter(request, teeResponse);
        
        } catch (Exception e) {
            error("Exception", e);
        } finally {
            IOUtils.closeQuietly(teeResponse.getOutputStream());
        }
        debug("- page cache expired; have to rebuild");
    }

    /**
     * Constructs cache file name based on requested.
     * Returns null if URI should not be cached (a logged-in user, a POST
     * request, not a request for a page, too many or invalid GET parameters,
     * etc).
     */
    @SuppressWarnings("unchecked")
	private String getCacheFilename(HttpServletRequest req) {
        debug("+");
        if(authenticated(req)) {
            debug("- a registered user");	
            return null;
        }
        if (!cacheableQuery(req)) {
            debug("- too complex request");
            return null;
        }
        String uri = req.getRequestURI()
            .substring(req.getContextPath().length()).replaceAll("/+", "/");
        String langCode = null;
        for (Language lang : Language.getItems()) {
        	if (uri.startsWith(String.format(LANG_CODE_PATH_FORMAT, lang.getCode()))) {
        		uri = uri.replaceFirst("^/" + lang.getCode(), StringUtils.EMPTY);
        		langCode = lang.getCode();
        	}
		}
        if( !(uri.matches("/?") 
        		|| uri.matches("/[\\w-]+\\.html")) ) {
            debug("- not a cacheable URI");
            return null;
        }
        for (String pageName : noCachedPages) {
        	if (uri.indexOf(pageName) > 0) {
        		debug("- no cacheable page");
        		return null;
        	}
        }
        String browserInfo = getBrowserInfo(req);
        if (browserInfo == null) {
            debug("- no browser info");
            return null;
        }
        StringBuffer paramString = new StringBuffer();
        for (String pname : (Set<String>) req.getParameterMap().keySet()) {
            if (!pname.matches("[\\w-_]+")) {
                debug("- bad parameter name");
                return null;
            }
            paramString.append('$').append(pname).append('#');
            String[] vals = req.getParameterValues(pname);
            if (vals.length > 1) {
                debug("- multiple parameter values");
                return null;
            }
            String value = vals[0].replaceAll(" ", StringUtils.EMPTY).replaceAll("\\.", StringUtils.EMPTY);
            if (!value.matches("[\\w-_]*")) {
                debug("- bad parameter value");
                return null;
            }
            paramString.append(vals[0]);
        }
        if (uri.matches("/?")) {
           
	        String localeString = langCode;
	        if (localeString == null) {
	        	HttpSession session = req.getSession(false);
	        	if(session != null){
	        		Object langObj = session.getAttribute(SessionData.LANGUAGE_ATTR_NAME);
	        		if (langObj != null &&  langObj instanceof Language ){
	        			localeString = ((Language)langObj).getCode();
	        		}
	        	}
	        }
	        if(localeString == null){
	        	localeString = Env.getPreferredLangCode(req);
	        }
	       	
	        uri = "index" + (langCode == null ? "_" + localeString : StringUtils.EMPTY) + ".html";
        }
        
        StringBuffer fileName = new StringBuffer(60);
        fileName.append(req.getServerName())
        .append('_');
        if (langCode != null) {
        	fileName.append(langCode).append('_');
        }
        fileName.append(uri.replace("/", StringUtils.EMPTY))
        .append('_')
        .append(browserInfo)
        .append(paramString);
        
        debug("-");
        return fileName.toString();
    }
    
    private String getBrowserInfo(HttpServletRequest req) {
    	String browserInfo = req.getHeader("User-Agent");
    	if(StringUtils.isBlank(browserInfo)){
    		return DEFAULT_BROWSER;
    	}
    	for (String browser : SUPPORTED_BROWSERS) {
    		if (browserInfo.indexOf(browser) != -1) {
				StringTokenizer tokenizer = new StringTokenizer(browserInfo, "/ ;()");
				while(tokenizer.hasMoreTokens()) {
					if ( browser.equals(tokenizer.nextToken()) && tokenizer.hasMoreTokens()) {
						browser += tokenizer.nextToken().replace(".", StringUtils.EMPTY);
					}
				}
				return browser;
    		}
    	}
    	return DEFAULT_BROWSER;
    }
    
    /** Returns true if the user signed in. */
	private boolean authenticated(HttpServletRequest req) {
		return
			req.getSession(false) != null &&
			(req.getSession(false).getAttribute("user_object") != null
					|| req.getSession(false).getAttribute("user_id") != null);
	}

	/**
	 * Returns true if the URI is cacheable checking the query string.
	 * @param req
	 * @return
	 */
    private boolean cacheableQuery(HttpServletRequest req) {
        debug("+");
    	if("POST".equals(req.getMethod()) || req.getParameterMap().size() > 2) {
    		return false;
    	}
    	if (req.getQueryString() != null && (req.getQueryString().contains(COMMAND))) {
    		return false;
    	}
        debug("-");
    	return true;
    }
	
	/**
	 * Returns true if the file exists and was modified
	 * more than 1 second ago
	 */
	static boolean isMature(File file) {
		try {
			return System.currentTimeMillis() - file.lastModified() > 1000;
		} catch (Exception e) {
			return false;
		}
	}
	
	static void debug(String message) {
		if (log.isDebugEnabled()) {
			log.debug(URL.get() + message);
		}
	}
	
	static void error(String message, Throwable e) {
		log.error(URL.get() + message, e);
	}
    
    private static class TeeServletOutputStream extends ServletOutputStream {
        
        private final ServletOutputStream servletOut;
        
        /** Collect output here to dump it later in a single step. */
        private final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        
        private final File file;
        
        TeeServletOutputStream(ServletOutputStream out1, File out2) {
            this.servletOut = out1;
            this.file = out2;
        }
        
        @Override
        public void write(byte[] b) throws IOException {
            servletOut.write(b);
            byteOut.write(b);
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            servletOut.write(b, off, len);
            byteOut.write(b, off, len);
        }
        
        @Override
        public void write(int b) throws IOException {
            servletOut.write(b);
            byteOut.write(b);
        }
        
        @Override
        public void flush() throws IOException {
            servletOut.flush();
            byteOut.flush();
        }

        @Override
        public void close() {
        	IOUtils.closeQuietly(servletOut);
        	writeCacheToFile();
            IOUtils.closeQuietly(byteOut);
        }

        /** Writes byte array to the file, suppressing any exceptions. */
		private void writeCacheToFile() {
            BufferedOutputStream fileOut = null;
            try {
				if (!file.createNewFile()) {
            		debug("- cache file already exists");
            		return;
            	}
                fileOut = new BufferedOutputStream(new FileOutputStream(file));
                if (!byteOut.toString("UTF-8").endsWith("</html>")) {
                	throw new Exception("invalid html");
                }
                byteOut.writeTo(fileOut);
                debug("- cache saved");
            } catch (Throwable e) {
                error("- Throwable", e);
                file.delete();
            } finally {
                IOUtils.closeQuietly(fileOut);
            }
		}
        
    }
    
    
}