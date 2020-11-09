package com.negeso.framework;

import com.negeso.framework.domain.DBHelper;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class FileExtensionFilter implements Filter {
	private final static Logger logger = Logger.getLogger(FileExtensionFilter.class);

	private Set<String> nonCacheablePages;

	public void destroy() {
		if (nonCacheablePages != null)
			nonCacheablePages.clear();
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			String requestURI = request.getRequestURI();
			String fileName = this.extractFileName(requestURI);
			if (this.nonCacheablePages.contains(fileName) && !requestURI.startsWith("/admin/")){
				HttpServletResponse response = (HttpServletResponse) resp;
				response.sendRedirect(requestURI.substring(0, requestURI.length()-1));

				return;
			}
		} catch (Throwable e) {
			logger.error("*.html > *.htm failed", e);
		}
		chain.doFilter(req, resp);
	}

	public void init(FilterConfig config) throws ServletException {
		nonCacheablePages = new HashSet<String>();
		final String query = "SELECT filename from page where uncachable = true;";
		Connection connection = null;
		try {
			connection = DBHelper.getConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()){
				this.nonCacheablePages.add(resultSet.getString("filename"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBHelper.close(connection);
		}
	}

	private String extractFileName(String uri){
		int lastSlash = uri.lastIndexOf("/");
		return uri.substring(lastSlash + 1);
	}

}
