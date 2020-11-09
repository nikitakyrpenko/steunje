package com.negeso.module.webshop.component;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.module.webshop.bo.CartDetails;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.service.CustomerService;
import com.negeso.module.webshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

@Component("webshop-cart_details_component")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CartDetailsComponent extends AbstractPageComponent {

	private final OrderService orderService;
	private final CustomerService customerService;

	@Autowired
	public CartDetailsComponent(OrderService orderService, CustomerService customerService) {
		this.orderService = orderService;
		this.customerService = customerService;
	}

	@Override
	public Element getElement(Document document, RequestContext request, Map parameters) {
		try {
			User user = request.getSession().getUser();
			Customer customer = customerService.findByUserJoinContacts(user);
			CartDetails cartDetails = orderService.calculateOrderDetails(customer);

			return Xbuilder.addBeanJAXB(document.getDocumentElement(), cartDetails);
		} catch (Exception e) {
			return Xbuilder.createEl(document, "cart_details", null);
		}
	}


}
