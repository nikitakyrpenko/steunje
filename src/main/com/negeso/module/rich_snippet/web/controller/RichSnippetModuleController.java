package com.negeso.module.rich_snippet.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.rich_snippet.bo.RichSnippet;
import com.negeso.module.rich_snippet.service.RichSnippetService;

public class RichSnippetModuleController extends AbstractController {
	
	protected RichSnippetService richSnippetService;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RequestUtil.getHistoryStack(request).push( new Link("RS_RICH_SNIPPET_MODULE", 
				"/admin/rich_snippet.html", true ,-1));
		String action = request.getParameter(NegesoRequestUtils.INPUT_ACTION);
		if ("edit".equalsIgnoreCase(action)) {
			Long id = NegesoRequestUtils.getId(request, null);
			if (id != null) {
				RichSnippet richSnippet = richSnippetService.findById(id);
				if (richSnippet != null) {
					RequestUtil.getHistoryStack(request).push( new Link(String.format("RS_%s_RICH_SNIPPETS", richSnippet.getDiscrim()), 
							String.format("/admin/rs_%s_list.html", richSnippet.getDiscrim()), true));
					return new ModelAndView(String.format("redirect:/admin/rs_edit_%s.html?id=%s&action=%s", richSnippet.getDiscrim(), id, action));
				}
			}
		}
		return new PreparedModelAndView("rs_module").get();
	}

	public void setRichSnippetService(RichSnippetService richSnippetService) {
		this.richSnippetService = richSnippetService;
	}

}
