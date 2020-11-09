package com.negeso.module.webshop.component;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.Contact;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.framework.page.PageComponent;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.service.CustomerService;
import com.negeso.framework.util.hibernate.HibernateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

@Component("webshop-checkout_component")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckoutComponent extends AbstractPageComponent {
	private final static Logger logger = Logger.getLogger(CheckoutComponent.class);

	private final CustomerService customerService;
	private final PageComponent pageComponent;

	@Autowired
	public CheckoutComponent(CustomerService customerService, @Qualifier("webshop-cart_component") PageComponent pageComponent) {
		this.customerService = customerService;
		this.pageComponent = pageComponent;
	}

	@Override
	public Element getElement(Document document, RequestContext request, Map parameters) {
		Element checkoutRootEl = Xbuilder.createEl(document, "checkout", null);

		try {
			User user = request.getSession().getUser();
			if (user != null){
				Customer customer = customerService.findByUserJoinContacts(user);

				checkoutRootEl.appendChild(pageComponent.getElement(document, request, parameters));
				appendContacts(customer, checkoutRootEl);
			}
		} catch (Exception e) {
			logger.error("Failed to build component", e);
			return checkoutRootEl;
		}

		return checkoutRootEl;
	}

	private void appendContacts(Customer customer, Element checkoutRootEl) {
		Element contactsEl = Xbuilder.createEl(checkoutRootEl, "contacts", null);
		if (customer != null){
			if (customer.getBillingContact() != null) {
				Contact unProxied = HibernateUtil.initAndUnproxy(customer.getBillingContact());
				Element element = Xbuilder.addBeanJAXB(contactsEl, unProxied, Contact.class);
				element.setAttribute("type", "billing");
			}
			if (customer.getShippingContact() != null){
				Contact unProxied = HibernateUtil.initAndUnproxy(customer.getShippingContact());
				Xbuilder.addBeanJAXB(contactsEl, unProxied, Contact.class);
			}
		}
		checkoutRootEl.appendChild(contactsEl);
	}

}