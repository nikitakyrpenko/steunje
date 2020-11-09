package com.negeso.module.webshop.component;

import com.negeso.framework.Env;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.jaxb.GermanPriceAdapter;
import com.negeso.framework.jaxb.PercentAdapter;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.framework.util.hibernate.HibernateUtil;
import com.negeso.module.webshop.bo.CashDeskOrder;
import com.negeso.module.webshop.entity.*;
import com.negeso.module.webshop.service.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component("webshop-cart_component")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CartComponent extends AbstractPageComponent {

    private final static Logger logger = Logger.getLogger(CartComponent.class);

    private final DiscountService discountService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final CashDeskService deskService;
    private final CartItemService cartItemService;
    private final NotificationService notificationService;
    private final GenerateFileService generateFileService;

    @Autowired
    public CartComponent(DiscountService discountService, CustomerService customerService,
                         OrderService orderService, CashDeskService deskService, CartItemService cartItemService,
                         NotificationService notificationService, GenerateFileService generateFileService) {
        this.discountService = discountService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.deskService = deskService;
        this.cartItemService = cartItemService;
        this.notificationService = notificationService;
        this.generateFileService = generateFileService;
    }

    @Override
    public Element getElement(Document document, RequestContext request, Map parameters) {
        Element root = Xbuilder.createEl(document, "cart", null);
        try {
            User user = request.getSession().getUser();
            Customer customer = customerService.findByUserJoinContacts(user);

            this.checkOrderByProducts(customer);

            root.appendChild(buildProductsElement(root, customer));
            root.setAttribute("customer-email", customer.getEmail());

        } catch (NullPointerException e) {
            logger.error("User or customer is null");
            return root;
        } catch (Exception e) {
            logger.error("Failed to build component", e);
            return root;
        }

        return root;
    }

    private void checkOrderByProducts(Customer customer) throws IOException {
        if(customer.getItems().size() == 0) return;

        List<Order> orders = orderService.findAllOpenedByCustomer(customer);
        if(orders.size() == 0) return;

        Set<Product> customerProducts = this.transformCartItemsToProducts(customer.getItems());
        for (Order order : orders) {
            Set<Product> orderProducts = this.transformOrderItemsToProducts(order.getOrderItems());

            if (orderProducts.equals(customerProducts) && order.getCustomField1() != null) {
                CashDeskOrder status = deskService.checkOrderStatus(order.getCustomField1());
                String st = status.getTransactions().get(0).getStatus();

                if (st.equals(CashDeskService.STATUS_SUCCESS) || st.equals(CashDeskService.STATUS_COMPLETE)) {
                    order.setStatus(Order.Status.PAYED.toString());
                    orderService.update(order);
                    cartItemService.deleteAllByCustomerId(customer.getUserCode());

                    Integer orderId = order.getId();
                    notificationService.makeNotification(orderId);
                    File csv = generateFileService.generateCSVFromOrder(orderId);
                    notificationService.notifyMultiverse(csv);
                }
            }
        }
    }


    private Element buildProductsElement(Element checkoutRootEl, Customer customer) {
        Element productsEl = Xbuilder.createEl(checkoutRootEl, "products", null);

        BigDecimal orderPrice = BigDecimal.ZERO;
        for (CartItem cartItem : customer.getItems()) {
            BigDecimal discount = discountService.determineDiscount(customer, cartItem);

            Product product = cartItem.getProduct();
            product = HibernateUtil.initAndUnproxy(product);

            Element productEl = Xbuilder.addBeanJAXB(checkoutRootEl, product);

            Integer quantity = cartItem.getQuantity();

            productEl.setAttribute("quantity", quantity.toString());
            BigDecimal itemsPrice = product.getPriceExcludeVat().multiply(new BigDecimal(quantity));

            try {
                if (discount != null) {
                    BigDecimal priceWithDiscount = itemsPrice.subtract(itemsPrice.multiply(discount));
                    BigDecimal priceAfterDiscount = product.getPriceExcludeVat().subtract(product.getPriceExcludeVat().multiply(discount));
                    productEl.setAttribute("discount", new PercentAdapter().marshal(discount));
                    productEl.setAttribute("priceAfterDiscount", new GermanPriceAdapter().marshal(priceAfterDiscount));
                    productEl.setAttribute("itemsPrice", new GermanPriceAdapter().marshal(priceWithDiscount));
                    productEl.setAttribute("itemsPriceExc", new GermanPriceAdapter().marshal(itemsPrice));
                    itemsPrice = priceWithDiscount;
                } else {
                    productEl.setAttribute("priceAfterDiscount", new GermanPriceAdapter().marshal(product.getPriceExcludeVat()));
                    productEl.setAttribute("itemsPrice", new GermanPriceAdapter().marshal(itemsPrice));
                    productEl.setAttribute("itemsPriceExc", new GermanPriceAdapter().marshal(itemsPrice));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            orderPrice = orderPrice.add(itemsPrice);
            productsEl.appendChild(productEl);
        }

        try {
            String deliveryPriceStr = Env.getProperty("ws.delivery_cost", "25");
            String vatStr = Env.getProperty("ws.vat", "21");
            BigDecimal deliveryPrice = orderPrice.compareTo(new BigDecimal(Env.getProperty("ws.max_price_to_include_delivery_cost", "250"))) > 0 ? BigDecimal.ZERO : new BigDecimal(deliveryPriceStr);

            BigDecimal vatPercent = new BigDecimal(vatStr).divide(new BigDecimal(100), 2, RoundingMode.CEILING);

            BigDecimal itemsVat = orderPrice.multiply(vatPercent);
            BigDecimal deliveryVat = deliveryPrice.multiply(vatPercent);

            BigDecimal total = orderPrice.add(deliveryPrice).add(itemsVat).add(deliveryVat);

            productsEl.setAttribute("orderPrice", new GermanPriceAdapter().marshal(orderPrice));
            productsEl.setAttribute("deliveryPrice", new GermanPriceAdapter().marshal(deliveryPrice));
            productsEl.setAttribute("VAT", new GermanPriceAdapter().marshal(itemsVat.add(deliveryVat)));
            productsEl.setAttribute("total", new GermanPriceAdapter().marshal(total));

            //Calculate the same like above but for IDEAL/PayPal payment type - is 2% discount
            BigDecimal ideaPercent = new BigDecimal(Env.getProperty("ws.discountForIdealPayment", "2"));
            BigDecimal idealDiscount = ideaPercent.divide(new BigDecimal(100), 2, RoundingMode.CEILING);
            BigDecimal orderPriceWithIdealDiscount = orderPrice.subtract(orderPrice.multiply(idealDiscount));

            BigDecimal vatWithDiscount = orderPriceWithIdealDiscount.multiply(vatPercent);

            BigDecimal totalPriceWithDiscount = orderPriceWithIdealDiscount.add(vatWithDiscount).add(deliveryVat).add(deliveryPrice);

            productsEl.setAttribute("orderPriceWithDiscount", new GermanPriceAdapter().marshal(orderPriceWithIdealDiscount));
            productsEl.setAttribute("idealDiscountWat", new GermanPriceAdapter().marshal(vatWithDiscount.add(deliveryVat)));
            productsEl.setAttribute("totalPriceWithDiscount", new GermanPriceAdapter().marshal(totalPriceWithDiscount));

        } catch (Exception e) {
            logger.error("Exception during building products xml wrapper", e);
        }

        return productsEl;
    }

    private Set<Product> transformCartItemsToProducts(Set<CartItem> items) {
        Set<Product> products = new HashSet<Product>();
        for(CartItem itm : items){
            Product tmp = HibernateUtil.initAndUnproxy(itm.getProduct());
            products.add(tmp);
        }
        return products;
    }

    private Set<Product> transformOrderItemsToProducts(Set<OrderItem> items) {
        Set<Product> products = new HashSet<Product>();
        for (OrderItem itm : items) {
            Product tmp = HibernateUtil.initAndUnproxy(itm.getProduct());
            products.add(tmp);
            products.add(itm.getProduct());
        }
        return products;
    }
}
