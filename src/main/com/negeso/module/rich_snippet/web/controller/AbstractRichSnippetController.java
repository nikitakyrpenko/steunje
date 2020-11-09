package com.negeso.module.rich_snippet.web.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.rich_snippet.bo.RichSnippet;
import com.negeso.module.rich_snippet.bo.RichSnippet.RichSnippetType;
import com.negeso.module.rich_snippet.service.RichSnippetFactory;
import com.negeso.module.rich_snippet.service.RichSnippetService;

public class AbstractRichSnippetController extends MultiActionController {

	protected RichSnippetService richSnippetService;
	private RichSnippetType discriminator;

	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RequestUtil.getHistoryStack(request).push( new Link(String.format("RS_%s_RICH_SNIPPETS", discriminator), 
				String.format("/admin/rs_%s_list.html", discriminator), true));
		return new PreparedModelAndView(String.format("rs_%s_list", discriminator))
					.addToModel("richSnippets", richSnippetService.list(discriminator))
					.addToModel("discriminator", discriminator.toString())
					.get();
	}
	
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = NegesoRequestUtils.getId(request, 0L);
		if (id > 0) {
			RichSnippet richSnippet = richSnippetService.findById(id);
			if (richSnippet != null) {
				if ("save".equals(ServletRequestUtils.getStringParameter(request, "todo"))) {
					save(request, richSnippet);
					if (ServletRequestUtils.getBooleanParameter(request, "close", false)) {
						return new ModelAndView(String.format("redirect:/admin/rs_%s_list.html", discriminator));
					}
				}
				RequestUtil.getHistoryStack(request).push( new Link(String.format("RS_EDIT_%s", discriminator), 
						String.format("/admin/rs_edit_%s.html", discriminator) , true));
				return getEditModelAndView(richSnippet);
			}
		}
		return list(request, response);
	}
	
	private ModelAndView getEditModelAndView(RichSnippet richSnippet) {
		return getEditModelAndView(richSnippet, null);
	}
	
	private ModelAndView getEditModelAndView(RichSnippet richSnippet, String errorMessage) {
		return new PreparedModelAndView(String.format("rs_edit_%s", discriminator))
		.addToModel("richSnippet", richSnippet)
		.addToModel("errorMessage", errorMessage)
		.get();
	}
	
	private void save(HttpServletRequest request, RichSnippet richSnippet) {
		NegesoRequestUtils.bind(richSnippet, request);
		richSnippetService.createOrUpdate(richSnippet);
	}
	
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if ("save".equals(ServletRequestUtils.getStringParameter(request, "todo"))) {
			RichSnippet richSnippet = RichSnippetFactory.getObject(discriminator);
			try {
				save(request, richSnippet);
			} catch (Exception e) {
				logger.error("Error: ", e);
				return getEditModelAndView(richSnippet, e.getMessage());
			}
			if (ServletRequestUtils.getBooleanParameter(request, "close", false)) {
				return new ModelAndView(String.format("redirect:/admin/rs_%s_list.html", discriminator));
			} else {
				RequestUtil.getHistoryStack(request).go(1);
				return new ModelAndView(String.format("redirect:/admin/rs_edit_%s.html?action=edit&id=%s", discriminator, richSnippet.getId()));
			}
		}
		RequestUtil.getHistoryStack(request).push( new Link(String.format("RS_ADD_%s", discriminator), 
				String.format("/admin/rs_edit_%s.html", discriminator) , true ));
		return getEditModelAndView(RichSnippetFactory.getObject(discriminator));
			
	}
	
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			Long id = NegesoRequestUtils.getId(request, 0L);
			if (id > 0) {
				RichSnippet richSnippet = richSnippetService.findById(id);
				if (richSnippet != null) {
					richSnippetService.delete(richSnippet);
				}
			}
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

	public void setDiscriminator(String discriminator) {
		this.discriminator = RichSnippetType.valueOf(discriminator);
	}

}
