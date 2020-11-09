package com.negeso.module.webshop.component;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.module.webshop.entity.OrderItem;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.Order;
import com.negeso.module.webshop.service.CustomerService;
import com.negeso.module.webshop.service.OrderService;
import com.negeso.module.webshop.util.ProductXmlHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("webshop-order_component")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class OrderComponent extends AbstractPageComponent {
	private final static Logger logger = Logger.getLogger(OrderComponent.class);

	private final CustomerService customerService;
	private final OrderService orderService;

	@Autowired
	public OrderComponent(CustomerService customerService, OrderService orderService) {
		this.customerService = customerService;
		this.orderService = orderService;
	}

	@Override
	public Element getElement(Document document, RequestContext request, Map parameters) {
		Element ordersRootElement = Xbuilder.createEl(document, "orders", null);
		try {
			Customer customer = customerService.findByUser(request.getSession().getUser());
			Integer orderId = request.getInteger("id");

			if (orderId == null) {
				this.appendOrdersShortDescription(ordersRootElement, customer);
			} else {
				this.appendOrderDetails(ordersRootElement, orderId, customer);
			}
		} catch (Exception e) {
			logger.error("Failed to build component", e);
			return ordersRootElement;
		}

		return ordersRootElement;
	}

	private void appendOrdersShortDescription(Element ordersRootElement, Customer customer) {
		this.appendOrdersShortDescription(ordersRootElement, this.orderService.listByCustomer(customer));
	}

	private void appendOrdersShortDescription(Element ordersRootElement, List<Order> orders) {
		for (Order order : orders) {
			Element element = Xbuilder.addBeanJAXB(ordersRootElement, order);
			element.setAttribute("quantity", String.valueOf(order.getOrderItems() == null ? 0 : order.getOrderItems().size()));
		}
	}

	private void appendOrderDetails(Element ordersRootElement, Integer orderId, Customer customer) {
		this.appendOrderDetails(ordersRootElement, this.orderService.findOneByCustomer(orderId, customer));
	}

	private void appendOrderDetails(Element ordersRootElement, Order order) {
		if (order == null) {
			logger.error("Order is null");
			return;
		}

		Connection conn = null;
		try {
			conn = DBHelper.getConnection();
			Set<OrderItem> orderItems = order.getOrderItems();
			Element orderEl = Xbuilder.addBeanJAXB(ordersRootElement, order);
			for (OrderItem orderItem : orderItems) {
				Element itemEl = Xbuilder.addBeanJAXB(orderEl, orderItem);
				ProductXmlHelper.appendProductUrl(conn, itemEl, orderItem.getProduct());
			}
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			DBHelper.close(conn);
		}
	}
}
