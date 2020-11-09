package com.negeso.framework.site;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.Language;
import com.negeso.framework.domain.ObjectNotFoundException;
import com.negeso.framework.site.service.PageRedirectService;

public class RedirectFilter implements Filter{

	private static Logger logger = Logger.getLogger(RedirectFilter.class);
	
	@Override
	public void destroy() {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain filterChain) throws IOException, ServletException {
		String path = ((HttpServletRequest) request).getRequestURL().toString();
		
		if( 
			path.endsWith(".jpeg")
			|| path.endsWith(".jpg") 
			|| path.endsWith(".png") 
			|| path.endsWith(".gif") 
			|| path.endsWith(".js") 
			|| path.endsWith(".css") 
				
		){
			filterChain.doFilter(request, response);
			return;
		}
		
		Long langId = 0L;
		String countryCode = null;
		Language lang = null;
		
		HttpSession session = ((HttpServletRequest) request).getSession(false);
		 if(session != null){
			 
			 Object langObj = session.getAttribute(SessionData.LANGUAGE_ATTR_NAME);
			 if (langObj != null &&  langObj instanceof Language ){
				 lang = (Language)langObj;
		      }

			 countryCode = (String)session.getAttribute("Ip4CoutryCode");
			 if(countryCode == null){
				 countryCode  = Env.getCountryByIp4(request.getRemoteAddr());
				 session.setAttribute("Ip4CoutryCode", countryCode);
			 }
		 }
		
		 if(lang == null){
			String langCode = Env.getPreferredLangCode((HttpServletRequest)request);
			try {
					lang = Language.findByCode(langCode);
				} catch (CriticalException e) {
					logger.error(e);
				} catch (ObjectNotFoundException e) {
					logger.error(e);
				}
			 
		 }
		if(lang ==  null){
			logger.warn("Can't recognize language");
		}else{
			langId = lang.getId();
		}
		 
		
		if(countryCode == null){
			 countryCode  = Env.getCountryByIp4(request.getRemoteAddr());
		}
		
		List<PageRedirect> redirects = PageRedirectService.getSpringInstance().listByLangAndCountry(langId,countryCode != null ? countryCode:"*");
		
		for(PageRedirect redirect : redirects){
			Pattern p = Pattern.compile(encodeFriendlyPattern(redirect.getMaskUrlFrom()));
			Matcher m = p.matcher(path);
			if(m.matches()){
				((HttpServletResponse) response).sendRedirect(redirect.getRedirectUrl());
				return;
			}
		}
		filterChain.doFilter(request, response);
	}
	
	private  String  encodeFriendlyPattern(String pattern){
		return pattern.replaceAll("\\.", "\\\\.")
						.replaceAll("\\*", ".*")
						.replaceAll("\\?", ".?");
	}
	
	
	@Override
	public void init(FilterConfig FilterConfig) throws ServletException {}

}
