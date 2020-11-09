package com.negeso.module.webshop.service;

import com.negeso.framework.Env;
import com.negeso.framework.domain.Contact;
import com.negeso.framework.generators.XmlHelper;
import com.negeso.framework.jaxb.GermanPriceAdapter;
import com.negeso.framework.jaxb.PercentAdapter;
import com.negeso.framework.jaxb.TimestampAdapter;
import com.negeso.framework.mailer.MailClient;
import com.negeso.module.webshop.entity.OrderItem;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.Order;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

@Service
public class NotificationService {
	private static Logger logger = Logger.getLogger(NotificationService.class);

	private static final String HTML_SHOPPING_CARD_XSL = "/template/notification.xsl";
	private static final String DATA = "/WEB-INF/modules/webshop/template/order.xml";

	private GeneratePdfService generatePdfService;
	private final SessionFactory sessionFactory;

	@Autowired
	public NotificationService(GeneratePdfService generatePdfService, @Qualifier("sessionFactory") SessionFactory sessionFactory) {
		this.generatePdfService = generatePdfService;
		this.sessionFactory = sessionFactory;
	}

	public void makeNotification(final Integer orderId){
		final NotificationService notificationService = this;
		try {
			new Thread(() -> {
				Session session = sessionFactory.openSession();
				Order order = (Order) session.get(Order.class, orderId);
				session.setReadOnly(order, true);
				notificationService.makeNotification(order);
				session.close();
			}).start();
		}catch (Exception e){
			logger.error("Failed to notify", e);
		}
	}

	private String makeNotification(Order order) {
		if (order == null) {
			logger.error("ERROR: order is NULL");
			return null;
		}
		Document doc = null;
		try {
			File xml = new File(Env.getRealPath(DATA));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xml);

			Document doc1 = this.buildXml(order);
			DOMSource domSource = new DOMSource(doc1);
			generatePdfService.generatePdf(domSource, "/WEB-INF/generated/pdf/" + order.getTransactionId() + ".pdf");
		} catch (Exception e) {
			logger.error(e);
		}

		Templates shoppingCardTmpl = Env.getSite().getXslTemplates(HTML_SHOPPING_CARD_XSL);
		String shoppingCardBody = XmlHelper.transformToString(
				doc,
				shoppingCardTmpl,
				"html"
		);
		try {
			this.sendMail(shoppingCardBody, order);
		} catch (MessagingException e) {
			logger.error(e);
			if(e instanceof SendFailedException){
				logger.error("Invalid addresses:" + Arrays.toString(((SendFailedException) e).getInvalidAddresses()));
			}
		}
		return shoppingCardBody;
	}

	protected Document buildXml(Order order) {
		Document orderDoc = null;
		Customer customer = order.getCustomer();
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			orderDoc = builder.newDocument();

			Element orderEl = orderDoc.createElement("order");

			BigDecimal total = order.getPrice();
			BigDecimal vatPrice = order.getVat();
			BigDecimal deliveryPrice = order.getDeliveryPrice();
			BigDecimal itemsPrice = total.subtract(vatPrice).subtract(deliveryPrice);

			orderEl.setAttribute("total", new GermanPriceAdapter().marshal(itemsPrice));
			orderEl.setAttribute("VAT", new GermanPriceAdapter().marshal(vatPrice));
			orderEl.setAttribute("totalWithVAT", new GermanPriceAdapter().marshal(total));
			orderEl.setAttribute("transactionId", order.getTransactionId());
			orderEl.setAttribute("deliveryType", order.getDeliveryType());
			orderEl.setAttribute("orderDate", new TimestampAdapter().marshal(order.getOrderDate()));
			orderEl.setAttribute("deliveryCost", new GermanPriceAdapter().marshal(deliveryPrice));
			orderEl.setAttribute("comment", order.getComment());

			Element customerElement = this.createCustomerElement(orderDoc, customer);
			orderEl.appendChild(customerElement);

			for (OrderItem orderItem : order.getOrderItems()) {
				Element itemEl = this.createItemElement(orderDoc, orderItem);
				orderEl.appendChild(itemEl);
			}

			Element billingElement = this.createContactElement(orderDoc, customer.getBillingContact(), "billing");
			orderEl.appendChild(billingElement);

			Element shippingElement = this.createContactElement(orderDoc, customer.getShippingContact(), "shipping");
			orderEl.appendChild(shippingElement);

			orderDoc.appendChild(orderEl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderDoc;
	}

	private Element createItemElement(Document orderDoc, OrderItem orderItem) throws Exception {
		Element itemEl = orderDoc.createElement("item");
		itemEl.setAttribute("price", new GermanPriceAdapter().marshal(orderItem.getPrice()));
		itemEl.setAttribute("discount", new PercentAdapter().marshal(orderItem.getDiscount()));
		itemEl.setAttribute("quantity", orderItem.getQuantity().toString());
		itemEl.setAttribute("id", orderItem.getProduct().getId());
		itemEl.setAttribute("title", orderItem.getProduct().getTitle());
		BigDecimal totalExcDiscount = orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));
		itemEl.setAttribute("totalExcDiscount", new GermanPriceAdapter().marshal(totalExcDiscount));
		if (orderItem.getDiscount() == null){
			itemEl.setAttribute("total", new GermanPriceAdapter().marshal(totalExcDiscount));
		}else {
			itemEl.setAttribute("total", new GermanPriceAdapter().marshal(totalExcDiscount.subtract(totalExcDiscount.multiply(orderItem.getDiscount()))));
		}
		return itemEl;
	}

	private Element createCustomerElement(Document doc, Customer customer) {
		Element customerEl = doc.createElement("customer");
		customerEl.setAttribute("email", customer.getEmail());
		customerEl.setAttribute("login", customer.getUserCode());
		customerEl.setAttribute("userName", customer.getUser().getName());

		return customerEl;
	}

	private Element createContactElement(Document doc, Contact contact, String name) {
		Element contactEl = doc.createElement(name);
		contactEl.setAttribute("address", contact.getCountry());
		contactEl.setAttribute("companyName", contact.getCompanyName());
		contactEl.setAttribute("addressLine", contact.getAddressLine());
		contactEl.setAttribute("zipCode", contact.getZipCode());
		contactEl.setAttribute("city", contact.getCity());
		contactEl.setAttribute("phone", contact.getPhone());
		return contactEl;
	}

	private void sendMail(String shoppingCardBody, Order order) throws MessagingException {
		MailClient mailer = new MailClient();
		String adminEmail = Env.getProperty("ws.customer_email", "Pavlo.Fedorovskyi@negeso.com");
		String subjectDescription = order.getTransactionId();
		if (subjectDescription == null)
			subjectDescription = "";
		else {
			subjectDescription = "Order-ID: " + subjectDescription;
		}

		File attachFile = new File(Env.getRealPath("/WEB-INF/generated/pdf/" + order.getTransactionId() + ".pdf"));
		FileDataSource fileDataSource = new FileDataSource(attachFile);
		String from = Env.getProperty("ws.notification_from", "support@negeso.com");
		mailer.sendHTMLMessage(adminEmail, from, "Baten trading company " + subjectDescription, shoppingCardBody, null, new DataSource[]{fileDataSource});
		if (order.getCustomer() != null && order.getCustomer().getEmail() != null) {
			mailer.sendHTMLMessage(order.getCustomer().getEmail(), from, "Baten trading company " + subjectDescription, shoppingCardBody, null, new DataSource[]{fileDataSource});
		}
	}

	public void notifyMultiverse(final File csv) {
		new Thread(() -> {
			String from = Env.getProperty("ws.export_from", "support@negeso.com");
			String to = Env.getProperty("ws.export_to", "support@negeso.com");
			String subject = "New order has been received";
			FileDataSource attachment = new FileDataSource(csv);

			MailClient mailer = new MailClient();
			try {
				mailer.sendHTMLMessage(to, from, subject, "", null, new DataSource[]{attachment});
			} catch (MessagingException e) {
				logger.error(" -e", e);
				if(e instanceof SendFailedException){
					logger.error("Invalid addresses:" + Arrays.toString(((SendFailedException) e).getInvalidAddresses()));
				}
			}
		}).start();
	}
}
