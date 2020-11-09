package com.negeso.module.webshop.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.negeso.framework.Env;
import com.negeso.framework.HttpException;
import com.negeso.framework.controller.SessionData;
import com.negeso.framework.domain.Contact;
import com.negeso.framework.domain.User;
import com.negeso.framework.util.json.JsonSupportForController;
import com.negeso.module.webshop.bo.CashDeskOrder;
import com.negeso.module.webshop.entity.*;
import com.negeso.module.webshop.service.*;
import com.negeso.module.webshop.util.PathVariable;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Bidi;
import java.util.HashSet;
import java.util.Set;

@Controller
public class OrderApiController extends JsonSupportForController {
    private final static Logger logger = Logger.getLogger(OrderApiController.class);

    private final OrderService orderService;
    private final CustomerService customerService;
    private final DiscountService discountService;
    private final CashDeskService cashDeskService;
    private NotificationService notificationService;
    private final CartItemService cartItemService;
    private final GenerateFileService generateFileService;

    //String with NL country names for checking it with customer shipping contact
    private final static String NL_COUNTRY_NAMES = "NL;NETHERLANDS;Netherlands;Netherland;Nederland;Nederlands";

    @Autowired
    public OrderApiController(OrderService orderService, CustomerService customerService,
                              DiscountService discountService, CashDeskService cashDeskService, NotificationService notificationService, CartItemService cartItemService, GenerateFileService generateFileService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.discountService = discountService;
        this.cashDeskService = cashDeskService;
        this.notificationService = notificationService;
        this.cartItemService = cartItemService;
        this.generateFileService = generateFileService;
    }

    @RequestMapping(value = "/webshop/api/orders", method = RequestMethod.GET)
    public void getAll(HttpServletResponse res) throws IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        super.writeToResponse(res, gson.toJson(orderService.list()));
    }

    @RequestMapping(value = "/webshop/api/order/*", method = RequestMethod.GET)
    public void getOne(HttpServletRequest req, HttpServletResponse res) throws IOException, HttpException {
        String orderIdStr = PathVariable.getStringParameter(req, "/webshop/api/order/");
        if (orderIdStr == null) {
            throw new HttpException(404, "Order id is null");
        } else {
            Order r = orderService.findOneForResponse(Integer.valueOf(orderIdStr));
            super.writeToResponse(res, new Gson().toJson(r));
        }
    }

    @RequestMapping(value = "/webshop/api/order", method = RequestMethod.POST)
    public void getCart(HttpServletRequest req, HttpServletResponse res) throws IOException, HttpException {

        Customer customer = customerService.findByUserJoinContacts((User) req.getSession().getAttribute(SessionData.USER_ATTR_NAME));
        this.updateCustomerAndContacts(req, customer);
        String paymentMethod = req.getParameter("payment_method");
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(Order.Status.OPENED.name());
        order.setCurrency("EUR");
        order.setDeliveryType(req.getParameter("delivery_type"));
        order.setComment(req.getParameter("comment"));
        order.setPaymentMethod(paymentMethod);
        Set<OrderItem> items = this.transformCartItemsToOrderItems(customer.getItems());
        order.setOrderItems(items);
        this.calculatePrice(customer, order, paymentMethod);
        order.setDeliveryContact(new DeliveryContact(order));

        Integer orderId = orderService.save(order);
        String transactionId = String.format("%016d", orderId);
        order.setTransactionId(transactionId);

        if (!NL_COUNTRY_NAMES.contains(order.getDeliveryContact().getCountry())) {
            order.setVat(BigDecimal.ZERO);
        }

        if ("CASH".equals(paymentMethod)) {
            order.setStatus(Order.Status.PAYED.name());
            order.setPrice(order.getPrice().add(order.getVat()));

            orderService.update(order);
        } else if ("IDEAL".equals(paymentMethod) || "paypal".equals(paymentMethod)) {
            //Final price would be order price + order VAT
            order.setPrice(order.getPrice().add(order.getVat()));
            CashDeskOrder cashDeskOrder;
            if ("paypal".equals(paymentMethod)) {
                cashDeskOrder = cashDeskService.createPayPalOrder(transactionId, order.getPrice(), paymentMethod);
            } else {
                cashDeskOrder = cashDeskService.createOrder(transactionId, order.getPrice(), req.getParameter("issuer_id"));

            }
            order.setCustomField1(cashDeskOrder.getId());
            order.setCustomField2(cashDeskOrder.getProjectId());
            orderService.update(order);

            String paymentUrl = cashDeskOrder.getTransactions().get(0).getPaymentUrl();
            res.setHeader("Location", paymentUrl);
            return;
        }

        notificationService.makeNotification(orderId);
        File csv = generateFileService.generateCSVFromOrder(orderId);
        notificationService.notifyMultiverse(csv);

        cartItemService.deleteAllByCustomerId(customer.getUserCode());

        res.addHeader("Location", "/order_nl.html?id=" + orderId);
        super.writeToResponseExcBody(res, HttpStatus.SC_CREATED);
    }

    private Set<OrderItem> transformCartItemsToOrderItems(Set<CartItem> items) throws HttpException {
        if (items == null || items.isEmpty())
            throw new HttpException(400, "Unable to buy because of empty cart");

        Set<OrderItem> orderItems = new HashSet<OrderItem>();

        for (CartItem cart : items) {
            OrderItem order = new OrderItem(cart.getQuantity(), cart.getProduct());
            orderItems.add(order);
        }

        return orderItems;
    }

    @RequestMapping(value = "/webshop/api/order_status", method = RequestMethod.GET)
    public void changeStatus(HttpServletRequest req, HttpServletResponse res,
                             @RequestParam("order_id") Integer orderId,
                             @RequestParam(value = "order_status", required = false) String orderStatus) {
        Order order = orderService.findOne(orderId);
        order.setStatus(orderStatus);

        super.writeToResponseExcBody(res, 200);
    }

    private void updateCustomerAndContacts(HttpServletRequest request, Customer customer) {
        customer.setEmail(request.getParameter("email"));
        Contact contact;
        contact = this.buildContact("billing_", request);
        contact.setType("billing");

        if (customer.getShippingContact() != null) {
            BeanUtils.copyProperties(contact, customer.getShippingContact(), "id");
        } else {
            customer.setShippingContact(contact);
        }

        contact = this.buildContact("shipping_", request);
        contact.setType("shipping");

        if (customer.getBillingContact() != null) {
            if (contact.getFirstName() == null && contact.getSecondName() == null && contact.getAddressLine() == null) {
                BeanUtils.copyProperties(customer.getShippingContact(), customer.getBillingContact(), "id");
            } else
                BeanUtils.copyProperties(contact, customer.getBillingContact(), "id");
        } else {
            customer.setBillingContact(contact);
        }

        customerService.update(customer);
    }

    private Contact buildContact(String prefix, HttpServletRequest request) {
        Contact contact = new Contact();
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(Contact.class).getPropertyDescriptors()) {
                if (pd.getWriteMethod() != null && !"class".equals(pd.getName()) && request.getParameter(prefix + pd.getName()) != null)
                    if (String.class.isAssignableFrom(pd.getPropertyType()))
                        pd.getWriteMethod().invoke(contact, request.getParameter(prefix + pd.getName()));
                    else if (Long.class.isAssignableFrom(pd.getPropertyType()))
                        pd.getWriteMethod().invoke(contact, ServletRequestUtils.getLongParameter(request, prefix + pd.getName(), 0));
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Unable to parse contact", e);
        }

        return contact;
    }

    private void calculatePrice(Customer customer, Order order, String paymentMethod) {
        Set<OrderItem> items = order.getOrderItems();
        BigDecimal orderPrice = BigDecimal.ZERO;
        BigDecimal originPrize = BigDecimal.ZERO;
        for (OrderItem item : items) {
            BigDecimal itemDiscount = discountService.determineDiscount(customer, item);
            item.setDiscount(itemDiscount);
            item.setPrice(item.getProduct().getPriceExcludeVat());
            item.setOrder(order);

            BigDecimal itemsPrice = item.getProduct().getPriceExcludeVat().multiply(new BigDecimal(item.getQuantity()));
            if (itemDiscount != null) {
                itemsPrice = itemsPrice.subtract(itemsPrice.multiply(itemDiscount));
            }
            orderPrice = orderPrice.add(itemsPrice);
            originPrize = originPrize.add(itemsPrice);
        }
        String deliveryPriceStr = Env.getProperty("ws.delivery_cost", "25");
        String vatStr = Env.getProperty("ws.vat", "21");

        BigDecimal vatPercent = new BigDecimal(vatStr).divide(new BigDecimal(100), 2, RoundingMode.CEILING);

        //Check payment method and if it IDEAL/PAYPAL make 2% discount to order price
        if ("IDEAL".equals(paymentMethod) || "paypal".equals(paymentMethod)) {
            BigDecimal ideaPercent = new BigDecimal(Env.getProperty("ws.discountForIdealPayment", "2"));
            BigDecimal idealDiscount = ideaPercent.divide(new BigDecimal(100), 2, RoundingMode.CEILING);

            orderPrice = orderPrice.subtract(orderPrice.multiply(idealDiscount));
        }

        //Calculate order items VAT after 2% discount if it present
        BigDecimal itemsVat = originPrize.multiply(vatPercent);

        BigDecimal deliveryPrice = "EXW".equals(order.getDeliveryType())
                || orderPrice.compareTo(new BigDecimal(Env.getProperty("ws.max_price_to_include_delivery_cost", "250"))) > 0
                ? BigDecimal.ZERO : new BigDecimal(deliveryPriceStr);

        BigDecimal deliveryVat = deliveryPrice.multiply(vatPercent);
        BigDecimal totalVat = itemsVat.add(deliveryVat);

        order.setDeliveryPrice(deliveryPrice);
        order.setVat(totalVat);
        order.setPrice(orderPrice.add(deliveryPrice));
    }
}
