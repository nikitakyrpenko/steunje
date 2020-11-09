package com.negeso.module.webshop.controller;

import com.google.gson.Gson;
import com.negeso.module.webshop.bo.CashDeskIssuer;
import com.negeso.module.webshop.entity.Order;
import com.negeso.module.webshop.service.GenerateFileService;
import com.negeso.module.webshop.service.NotificationService;
import com.negeso.module.webshop.service.OrderService;
import com.negeso.module.webshop.util.CashDeskHttpClient;
import com.negeso.framework.util.json.JsonSupportForController;
import com.negeso.framework.util.spring.interceptor.Unsecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Controller
public class CashDeskController extends JsonSupportForController {

	private CashDeskIssuer[] issuers = null;

	private final OrderService orderService;
	private final NotificationService notificationService;
	private final GenerateFileService generateFileService;


	@Autowired
	public CashDeskController(OrderService orderService, NotificationService notificationService, GenerateFileService generateFileService) {
		this.orderService = orderService;
		this.notificationService = notificationService;
		this.generateFileService = generateFileService;
	}

	@Unsecured
	@RequestMapping(value = "/webshop/api/ideal/issuers", method = RequestMethod.GET)
	public void listBanks(HttpServletResponse response) throws IOException {
		if (issuers == null)
			issuers = CashDeskHttpClient.createAndExecute("/v1/ideal/issuers/", "GET", CashDeskIssuer[].class);

		super.writeToResponse(response, new Gson().toJson(issuers));
	}

	@Unsecured
	@RequestMapping(value = "/webshop/api/ideal/new_order", method = RequestMethod.GET)
	public String modifyOrder(@RequestParam("order_id") String idealOrderId, @RequestParam(value = "project_id", required = false) String projectId) throws IOException{
		Order order = orderService.findOneByIdealId(idealOrderId);
		order.setStatus(Order.Status.PAYED.name());
		orderService.update(order);

		Integer orderId = order.getId();
		notificationService.makeNotification(orderId);
		File csv = generateFileService.generateCSVFromOrder(orderId);
		notificationService.notifyMultiverse(csv);

		return "redirect:" + "/order_nl.html?id=" + orderId;
	}
}
