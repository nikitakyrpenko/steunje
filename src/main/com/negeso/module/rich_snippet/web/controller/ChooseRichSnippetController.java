package com.negeso.module.rich_snippet.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.controller.DispatchersContainer;
import com.negeso.framework.dao.GenericService;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.rich_snippet.IRichSnippetContainer;
import com.negeso.module.rich_snippet.service.RichSnippetService;

public class ChooseRichSnippetController extends MultiActionController{
	
	private static Logger logger = Logger.getLogger(ChooseRichSnippetController.class);
	
	private RichSnippetService richSnippetService; 
	
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		IRichSnippetContainer richSnippetable = getEntity(request);
		return new PreparedModelAndView("rs_choose_rich_snippet")
			.addToModel("richSnippets", richSnippetService.list())
			.addToModel("richSnippetable", richSnippetable)
			.get();
	}

	private IRichSnippetContainer getEntity(HttpServletRequest request) throws ServletRequestBindingException {
		Long id = NegesoRequestUtils.getId(request, 0L);
		IRichSnippetContainer richSnippetable = null;
		if (id > 0) {
			richSnippetable = getService(request).findById(id);
		}
		return richSnippetable;
	}
	
	private GenericService<IRichSnippetContainer> getService(HttpServletRequest request) throws ServletRequestBindingException {
		String entity = ServletRequestUtils.getStringParameter(request, "entity");
		if ("page".equals(entity)) {
			return ((GenericService<IRichSnippetContainer>)DispatchersContainer.getInstance().getBean("core", "pageService"));
		} else if ("product".equals(entity)) {
			return ((GenericService<IRichSnippetContainer>)DispatchersContainer.getInstance().getBean("product", "productService"));
		}
		return null;
	}
	
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			String[] ids = request.getParameterValues("ids");
			IRichSnippetContainer richSnippetable = getEntity(request);
			if (richSnippetable != null) {
				richSnippetable.getRichSnippets().clear();
				if (ids != null) {
					for (String id : ids) {
						richSnippetable.getRichSnippets().add(richSnippetService.findById(Long.valueOf(id)));
					}					
				}
			}
			getService(request).createOrUpdate(richSnippetable);
		} catch (Exception e) {
			logger.error("Error: ", e);
			writeToResponse(response, e.getMessage());
		}
		return null;
	}
	
	private void writeToResponse(HttpServletResponse response, String meassage) throws IOException {
		response.setContentType("text/html");
		response.setStatus(401);
        PrintWriter out = response.getWriter();
        out.print(meassage);
        out.close();
	}

	public void setRichSnippetService(RichSnippetService richSnippetService) {
		this.richSnippetService = richSnippetService;
	}
}
