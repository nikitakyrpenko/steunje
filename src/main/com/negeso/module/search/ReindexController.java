package com.negeso.module.search;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;

public class ReindexController extends AbstractController {

	private static Logger logger = Logger.getLogger(ReindexController.class);

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		if (SearchProducer.isBuildingIndex()) {
			logger.info("- index is being built by a concurrent thread");
			return new ModelAndView("search", "status", "busy");
		}
		if ("reindex".equals(req.getParameter("action"))) {
			BuildIndexExecutor.build();
			return new ModelAndView("redirect:/admin/manage_search.html");
		}
		RequestUtil.getHistoryStack(req).push(new Link("SEARCH", "/admin/manage_search.html", true));
		return new ModelAndView("search", "status", "menu");
	}

}