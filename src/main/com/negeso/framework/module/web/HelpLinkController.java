package com.negeso.framework.module.web;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.Env;
import com.negeso.framework.controller.SessionData;
import com.negeso.module.core.service.ModuleService;

/**
 * 
 * @TODO
 * 
 * @author		Mariia Samarina
 * @version		Revision: 
 *
 */
public class HelpLinkController extends AbstractController {
	private ModuleService moduleService;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {		
		String moduleId = req.getParameter("moduleId");
		String moduleUrl = req.getParameter("moduleUrl");
		if(moduleId != null){			
			moduleUrl = moduleUrl + "&moduleId=" + moduleId;
		}
		
		String helpLink = moduleService.getHelpLink(moduleUrl);
		String defaultLink = req.getParameter("defaultLink");
		
		if(helpLink == null){
			if(defaultLink != null && !"".equals(defaultLink)){
				helpLink = defaultLink;
			}
			else{
				helpLink = Env.getProperty("HELP_DEFAULT_PAGE","/admin/help/cms-help_nl.html");
			}
		}		
			
		helpLink = String.format(helpLink, SessionData.getInterfaceLanguageCode(req));
		
		PrintWriter printWriter = resp.getWriter();
		printWriter.print(helpLink);
		printWriter.close();
		
		return null;
	}

	public ModuleService getModuleService() {
		return moduleService;
	}

	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}

}
