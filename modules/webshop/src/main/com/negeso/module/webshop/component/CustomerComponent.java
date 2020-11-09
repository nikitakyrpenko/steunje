package com.negeso.module.webshop.component;

import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.domain.Contact;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.User;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.page.AbstractPageComponent;
import com.negeso.framework.util.hibernate.HibernateUtil;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.Product;
import com.negeso.module.webshop.entity.Wishlist;
import com.negeso.module.webshop.service.CustomerService;
import com.negeso.module.webshop.service.WishlistService;
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
import java.util.stream.Collectors;

@Component("webshop-customer_component")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CustomerComponent extends AbstractPageComponent {
    private final static Logger logger = Logger.getLogger(CustomerComponent.class);

    private final CustomerService customerService;

    private final WishlistService wishlistService;

    @Autowired
    public CustomerComponent(CustomerService customerService, WishlistService wishlistService) {
        this.customerService = customerService;
        this.wishlistService = wishlistService;
    }


    @Override
    public Element getElement(Document document, RequestContext request, Map parameters) {
        Element rootElement = Xbuilder.createEl(document, "customer", null);

        try {
            User user = request.getSession().getUser();
            if (user != null) {
                Customer customer = customerService.findByUser(user);
                if (customer == null) {
                    logger.info(String.format("Customer not found by user: %s. Skipping component.", user.getLogin()));
                } else {
                    this.checkAndUpdateWishListIfNull(customer);

                    List<Wishlist> wishLists = customer.getWishProducts();
                    rootElement.appendChild(this.buildProductsElement(rootElement, wishLists));

                    Element contactsEl = Xbuilder.createEl(rootElement, "contacts", null);
                    this.appendContactElement(contactsEl, customer.getBillingContact());
                    this.appendContactElement(contactsEl, customer.getShippingContact());
                    rootElement.appendChild(contactsEl);

                    rootElement.setAttribute("email", customer.getEmail());
                    rootElement.setAttribute("displayPrice", customer.getDisplayPrice());
                    rootElement.setAttribute("postPayAllowed", customer.getPostPayAllowed().toString());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to build component", e);
            return rootElement;
        }

        return rootElement;
    }


    private Element buildProductsElement(Element root, List<Wishlist> wishlists) {
        Element productsEl = Xbuilder.createEl(root, "products", null);

        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            for (Wishlist list : wishlists) {
                Product unpoxied = HibernateUtil.initAndUnproxy(list.getProduct());
                Element itemEl = Xbuilder.addBeanJAXB(productsEl, unpoxied);
                ProductXmlHelper.appendProductUrl(conn, itemEl, unpoxied);

            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            DBHelper.close(conn);
        }


        return productsEl;
    }

    private void appendContactElement(Element root, Contact contact) {
        if (contact != null) Xbuilder.addBeanJAXB(root, contact, Contact.class);
    }

    private void checkAndUpdateWishListIfNull(Customer customer) {
        List<Wishlist> wishLists = customer.getWishProducts();

        List<Wishlist> nullProductsWishList = wishLists
                .stream()
                .filter(item -> item.getProduct() == null)
                .collect(Collectors.toList());

        if (nullProductsWishList.size() > 0) {
            List<Wishlist> updatedList = wishLists
                    .stream().filter(w -> !nullProductsWishList.contains(w))
                    .collect(Collectors.toList());

            customer.setWishProducts(updatedList);

            logger.error("- error: Find null product in wish list.");
            nullProductsWishList.forEach(this.wishlistService::delete);
            logger.error("-INFO: Wish list was successfully updated for customer: " + customer.getUser().getName());

            this.customerService.update(customer);
        }
    }
}
