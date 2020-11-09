package com.negeso.module.thr.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.w3c.dom.Element;

import com.negeso.framework.navigation.Link;
import com.negeso.framework.navigation.RequestUtil;
import com.negeso.framework.util.NegesoRequestUtils;
import com.negeso.module.core.PreparedModelAndView;
import com.negeso.module.thr.bo.ThrOrder;
import com.negeso.module.thr.cron.OrdersHtmlGenerator;
import com.negeso.module.thr.cron.OrdersXmlGenerator;
import com.negeso.module.thr.filter.OrderFilter;
import com.negeso.module.thr.service.ThrOrderService;

public class ThrOrderController extends MultiActionController {
	private static Logger logger = Logger.getLogger( ThrOrderController.class );
	
	private ThrOrderService thrOrderService;

	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		setNavigationPathToRoot(request);
		OrderFilter filter = OrderFilter.getFromSession(request);
		NegesoRequestUtils.bind(filter, request, "yyyy-MM-dd");
		return new PreparedModelAndView("thr_orders").addToModel("orders", thrOrderService.list(filter)).addToModel("logins", thrOrderService.listLogins()).addToModel("filter", filter) .get();
	}

	private void setNavigationPathToRoot(HttpServletRequest request) {
		RequestUtil.getHistoryStack(request).push( new Link("THR_ORDERS", 
				"/admin/thr_orders.html", true , -1));
	}
	
	public ModelAndView details(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = NegesoRequestUtils.getId(request, null);
		if (id == null) {
			return details(request, response);
		}
		ThrOrder order = thrOrderService.findById(id);
		if (order == null) {
			return details(request, response);
		}
		setNavigationPathToRoot(request);
		RequestUtil.getHistoryStack(request).push( new Link("THR_ORDER_DETAILS", 
				"/admin/thr_orders.html?action=details", true ));
		return new PreparedModelAndView("thr_order_details").addToModel("order", order).get();
	}
	
	public ModelAndView report(HttpServletRequest request, HttpServletResponse response) throws Exception {
		OrderFilter filter = OrderFilter.getFromSession(request);
		List<ThrOrder> list =  thrOrderService.list(filter);
		Element el = OrdersXmlGenerator.buildXml(list);
		writeToResponse(response, OrdersHtmlGenerator.buildHtml(el));
		return null;
	}
	
	private void writeToResponse(HttpServletResponse response, String meassage) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.print(meassage);
		out.close();
	}

	public void setThrOrderService(ThrOrderService thrOrderService) {
		this.thrOrderService = thrOrderService;
	}

}
