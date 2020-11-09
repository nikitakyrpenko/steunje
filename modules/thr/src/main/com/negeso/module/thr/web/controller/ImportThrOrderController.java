package com.negeso.module.thr.web.controller;

import static com.negeso.module.thr.bo.ThrJsonConsts.NEGESO_JSON_API_VERSION;
import static com.negeso.module.thr.bo.ThrJsonConsts.NEGESO_JSON_APP_VERSION;
import static com.negeso.module.thr.bo.ThrJsonConsts.NEGESO_JSON_DEVICE;
import static com.negeso.module.thr.bo.ThrJsonConsts.NEGESO_JSON_HEIGTH;
import static com.negeso.module.thr.bo.ThrJsonConsts.NEGESO_JSON_LOGIN;
import static com.negeso.module.thr.bo.ThrJsonConsts.NEGESO_JSON_MODEL;
import static com.negeso.module.thr.bo.ThrJsonConsts.NEGESO_JSON_OS_VERSION;
import static com.negeso.module.thr.bo.ThrJsonConsts.NEGESO_JSON_PASSWORD;
import static com.negeso.module.thr.bo.ThrJsonConsts.NEGESO_JSON_PRODUCT;
import static com.negeso.module.thr.bo.ThrJsonConsts.NEGESO_JSON_WIDTH;
import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_ORDER_NUMBER;
import static com.negeso.module.thr.bo.ThrJsonConsts.THR_JSON_ORDER_PRODUCTS;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.negeso.module.thr.bo.OrderItem;
import com.negeso.module.thr.bo.ThrOrder;
import com.negeso.module.thr.service.ThrOrderService;
import com.negeso.module.thr.utils.IdMutexProvider;
import com.negeso.module.thr.utils.IdMutexProvider.Mutex;

public class ImportThrOrderController extends AbstractController{
	
    protected static final String ORDER_PARAM_NAME = "order";

	protected static Logger logger = Logger.getLogger( ImportThrOrderController.class );

	protected ThrOrderService thrOrderService;
	protected IdMutexProvider mutexProvider = new IdMutexProvider();

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String responseMessage = "{\"success\":true}";
		try {
			String jsonOrder = request.getParameter(ORDER_PARAM_NAME);
			logger.debug("Json order input: " + jsonOrder);
			JsonObject jsonObject = null;
			try {
				jsonObject = new JsonParser().parse(jsonOrder).getAsJsonObject();
			} catch (Exception e) {
				logger.error(e);
				Map<String, String> map = request.getParameterMap();
				logger.debug(map);
				if (map.size() == 2) {
					for (Object key : request.getParameterMap().keySet()) {
						String paremName = (String)key;
						if (!ORDER_PARAM_NAME.equals(paremName)) {
							jsonOrder += "&" + paremName;
						}
					}
					jsonObject = new JsonParser().parse(jsonOrder).getAsJsonObject();					
				}
			}
			String orderNumber = getStringMember(jsonObject, THR_JSON_ORDER_NUMBER);
			ThrOrder order = null;
			if (orderNumber != null) {
				Mutex mutex = mutexProvider.getMutex(orderNumber);
				synchronized (mutex) {
					order = thrOrderService.findByOrderNumber(orderNumber);
					if (order == null) {
						order = new ThrOrder();
					}
					order.setOrderNumber(orderNumber);
					order.setLogin(getStringMember(jsonObject, NEGESO_JSON_LOGIN));
					order.setPassword(getStringMember(jsonObject, NEGESO_JSON_PASSWORD));
					order.setAppVersion(getStringMember(jsonObject, NEGESO_JSON_APP_VERSION));
					order.setOsVersion(getStringMember(jsonObject, NEGESO_JSON_OS_VERSION));
					order.setApiVersion(getStringMember(jsonObject, NEGESO_JSON_API_VERSION));
					order.setDevice(getStringMember(jsonObject, NEGESO_JSON_DEVICE));
					order.setModel(getStringMember(jsonObject, NEGESO_JSON_MODEL));
					order.setProduct(getStringMember(jsonObject, NEGESO_JSON_PRODUCT));
					order.setHeight(getIntMember(jsonObject, NEGESO_JSON_HEIGTH));
					order.setWidth(getIntMember(jsonObject, NEGESO_JSON_WIDTH));
					order.setDate(new Timestamp(System.currentTimeMillis()));
					if (jsonObject.has(THR_JSON_ORDER_PRODUCTS)) {
						for (JsonElement element : jsonObject.getAsJsonArray(THR_JSON_ORDER_PRODUCTS)) {
							OrderItem orderItem = OrderItem.initWithJson(element.getAsJsonObject());
							orderItem.setOrder(order);
							order.add(orderItem);
						}
						
					}
					thrOrderService.createOrUpdate(order);
				}
			} else {
				logger.error("Could not save, empty " + THR_JSON_ORDER_NUMBER);
			}
		} catch (Exception e) {
			logger.error("Error", e);
			response.setContentType("text/html");
			response.setStatus(500);
			responseMessage = e.getMessage();
		}
		PrintWriter out = response.getWriter();
		out.print(responseMessage);
		out.close();
		return null;
	}

	protected String getStringMember(JsonObject jsonObject, String memberName) {
		if (jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull()) {
			return jsonObject.get(memberName).getAsString();
		}
		return null;
	}

	protected Integer getIntMember(JsonObject jsonObject, String memberName) {
		if (jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull()) {
			return jsonObject.get(memberName).getAsInt();
		}
		return null;
	}
	
	public static void main(String[] args) {
		JsonObject jsonObject = new JsonParser().parse("{}").getAsJsonObject();
		System.out.println(jsonObject.get("hhh").getAsInt());
	}

	public void setThrOrderService(ThrOrderService thrOrderService) {
		this.thrOrderService = thrOrderService;
	}
}
