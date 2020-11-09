package com.negeso.module.webshop.dao;

import com.negeso.framework.dao.impl.AbstractHibernateDao;
import com.negeso.module.webshop.bo.JsonResponseOrder;
import com.negeso.module.webshop.entity.*;
import org.aspectj.weaver.ast.Or;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class OrderDaoImpl extends AbstractHibernateDao<Order, Integer> implements OrderDao {

    public OrderDaoImpl() {
        super(Order.class);
    }

    @Override
    public Integer getNextSeq() {
        Session session = super.createSession();
        SQLQuery query = session.createSQLQuery("SELECT nextval('ws_order_id_seq')");
        Integer result = ((BigInteger) query.uniqueResult()).intValue();

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Order> findAllByCustomer(Customer customer) {
        return (List<Order>) super.createCriteria()
                .add(Restrictions.eq("customer", customer))
                .addOrder(org.hibernate.criterion.Order.desc("orderDate"))
                .list();
    }

    @Override
    public Order findOneByCustomer(Integer id, Customer customer) {
        return (Order) super.createCriteria()
                .add(Restrictions.eq("id", id))
                .add(Restrictions.eq("customer", customer))
                .uniqueResult();
    }

    @Override
    public Order findOneByIdealId(String idealTransactionId) {
        return (Order) super.createCriteria()
                .add(Restrictions.eq("customField1", idealTransactionId))
                .uniqueResult();
    }

    @Override
    public Order findOneForResponse(Integer id) {
        Criteria criteria = super.getCurrentSession().createCriteria(Order.class, "order");

        criteria
                .createAlias("customer", "c", Criteria.LEFT_JOIN)
                .createAlias("deliveryContact", "dc", Criteria.LEFT_JOIN)
                .createAlias("orderItems", "items", Criteria.LEFT_JOIN)
                .createAlias("items.product", "pr", Criteria.LEFT_JOIN)
                .add(Restrictions.eq("order.id", id))
                .setProjection(Projections.projectionList()
                        .add(Projections.property("order.id"), "id")
                        .add(Projections.property("order.transactionId"), "transactionId")
                        .add(Projections.property("order.status"), "status")
                        .add(Projections.property("order.price"), "price")
                        .add(Projections.property("order.currency"), "currency")
                        .add(Projections.property("order.paymentMethod"), "paymentMethod")
                        .add(Projections.property("order.deliveryType"), "deliveryType")
                        .add(Projections.property("order.orderDate"), "orderDate")
                        .add(Projections.property("order.customField1"), "customField1")
                        .add(Projections.property("order.customField2"), "customField2")
                        .add(Projections.property("order.comment"), "comment")

                        .add(Projections.property("c.userCode"), "customerUserCode")
                        .add(Projections.property("c.email"), "customerEmail")

                        .add(Projections.property("dc.id"), "deliveryContactId")
                        .add(Projections.property("dc.firstName"), "deliveryContactFirstName")
                        .add(Projections.property("dc.secondName"), "deliveryContactSecondName")
                        .add(Projections.property("dc.companyName"), "deliveryContactCompanyName")
                        .add(Projections.property("dc.addressLine"), "deliveryContactAddressLine")
                        .add(Projections.property("dc.zipCode"), "deliveryContactZipCode")
                        .add(Projections.property("dc.city"), "deliveryContactCity")
                        .add(Projections.property("dc.country"), "deliveryContactCountry")
                        .add(Projections.property("dc.phone"), "deliveryContactPhone")
                        .add(Projections.property("dc.fax"), "deliveryContactFax")

                        .add(Projections.property("items.id"), "orderItemId")
                        .add(Projections.property("items.quantity"), "orderItemQuantity")
                        .add(Projections.property("items.discount"), "orderItemDiscount")
                        .add(Projections.property("items.price"), "orderItemPrice")
                        .add(Projections.property("pr.id"), "orderItemProductId")
                        .add(Projections.property("pr.productNumber"), "orderItemProductProductNumber")
                        .add(Projections.property("pr.title"), "orderItemProductTitle")
                        .add(Projections.property("pr.ean"), "orderItemProductEan")
                )
                .setResultTransformer(new OrderResultTransformer(JsonResponseOrder.class))
//				.setResultTransformer(Transformers.aliasToBean(JsonResponseOrder.class))
        ;
        Object o = criteria.uniqueResult();
        return (Order) o;
    }

    @Override
    public Order findOneByProducts(Customer customer, List<Product> products) {
        Criteria criteria = this.getCurrentSession().createCriteria(Order.class, "order");

        criteria
                .createAlias("customer", "c", Criteria.LEFT_JOIN)
                .createAlias("deliveryContact", "dc", Criteria.LEFT_JOIN)
                .createAlias("orderItems", "items", Criteria.LEFT_JOIN)
                .createAlias("items.product", "pr", Criteria.LEFT_JOIN)
                .setProjection(Projections.projectionList()
                        .add(Projections.property("order.id"), "id")
                        .add(Projections.property("order.transactionId"), "transactionId")
                        .add(Projections.property("order.status"), "status")
                        .add(Projections.property("order.price"), "price")
                        .add(Projections.property("order.currency"), "currency")
                        .add(Projections.property("order.paymentMethod"), "paymentMethod")
                        .add(Projections.property("order.deliveryType"), "deliveryType")
                        .add(Projections.property("order.orderDate"), "orderDate")
                        .add(Projections.property("order.customField1"), "customField1")
                        .add(Projections.property("order.customField2"), "customField2")
                        .add(Projections.property("order.comment"), "comment")

                        .add(Projections.property("c.userCode"), "customerUserCode")
                        .add(Projections.property("c.email"), "customerEmail")

                        .add(Projections.property("dc.id"), "deliveryContactId")
                        .add(Projections.property("dc.firstName"), "deliveryContactFirstName")
                        .add(Projections.property("dc.secondName"), "deliveryContactSecondName")
                        .add(Projections.property("dc.companyName"), "deliveryContactCompanyName")
                        .add(Projections.property("dc.addressLine"), "deliveryContactAddressLine")
                        .add(Projections.property("dc.zipCode"), "deliveryContactZipCode")
                        .add(Projections.property("dc.city"), "deliveryContactCity")
                        .add(Projections.property("dc.country"), "deliveryContactCountry")
                        .add(Projections.property("dc.phone"), "deliveryContactPhone")
                        .add(Projections.property("dc.fax"), "deliveryContactFax")

                        .add(Projections.property("items.id"), "orderItemId")
                        .add(Projections.property("items.quantity"), "orderItemQuantity")
                        .add(Projections.property("items.discount"), "orderItemDiscount")
                        .add(Projections.property("items.price"), "orderItemPrice")
                        .add(Projections.property("pr.id"), "orderItemProductId")
                        .add(Projections.property("pr.productNumber"), "orderItemProductProductNumber")
                        .add(Projections.property("pr.title"), "orderItemProductTitle")
                        .add(Projections.property("pr.ean"), "orderItemProductEan"));

        criteria.add(Restrictions.in("items.product", products))
                .add(Restrictions.eq("order.status", Order.Status.OPENED.toString()))
                .add(Restrictions.eq("c.userCode", customer.getUserCode()))
                .add(Restrictions.eq("c.email", customer.getEmail()))
                .setResultTransformer(new OrderResultTransformer(JsonResponseOrder.class));


        List<Order> res = criteria.list();
        return res.size() > 0 ? res.get(0) : null;
    }

    @Override
    public List<Order> findAllOpenedByCustomer(Customer customer) {
        return (List<Order>) super.createCriteria()
                .add(Restrictions.eq("customer", customer))
                .add(Restrictions.eq("status", "OPENED"))
                .addOrder(org.hibernate.criterion.Order.desc("orderDate"))
                .list();
    }

    class OrderResultTransformer extends AliasToBeanResultTransformer {

        public OrderResultTransformer(Class resultClass) {
            super(resultClass);
        }

        @Override
        public Object transformTuple(Object[] objects, String[] strings) {
            return super.transformTuple(objects, strings);
        }

        @Override
        public List transformList(List list) {
            List<JsonResponseOrder> casted = (List<JsonResponseOrder>) list;

            Order order = this.createOrder(casted.get(0));
            order.setCustomer(this.createCustomer(casted.get(0)));
            order.setDeliveryContact(this.createDeliveryContact(casted.get(0)));
            order.setOrderItems(this.createOrderItems(casted));
            List nn = new ArrayList();
            nn.add(order);
            return nn;
        }

        private Set<OrderItem> createOrderItems(List<JsonResponseOrder> casted) {
            Set<OrderItem> items = new HashSet<OrderItem>(casted.size());
            for (JsonResponseOrder order : casted) {
                OrderItem item = new OrderItem();
                item.setId(order.getOrderItemId());
                item.setQuantity(order.getOrderItemQuantity());
                item.setDiscount(order.getOrderItemDiscount());
                item.setPrice(order.getOrderItemPrice());

                Product pr = new Product();
                String orderItemProductId = order.getOrderItemProductId();
                if (orderItemProductId == null) {
                    pr.setId("Not found");
                    item.setProduct(pr);
                    items.add(item);
                    continue;
                }
                pr.setId(orderItemProductId);
                pr.setProductNumber(order.getOrderItemProductProductNumber());
                pr.setTitle(order.getOrderItemProductTitle());
                pr.setEan(order.getOrderItemProductEan());
                item.setProduct(pr);
                items.add(item);
            }
            return items;
        }

        private DeliveryContact createDeliveryContact(JsonResponseOrder o) {
            DeliveryContact dc = new DeliveryContact();
            dc.setId(o.getDeliveryContactId());
            dc.setFirstName(o.getDeliveryContactFirstName());
            dc.setSecondName(o.getDeliveryContactSecondName());
            dc.setCompanyName(o.getDeliveryContactCompanyName());
            dc.setAddressLine(o.getDeliveryContactAddressLine());
            dc.setZipCode(o.getDeliveryContactZipCode());
            dc.setCity(o.getDeliveryContactCity());
            dc.setCountry(o.getDeliveryContactCountry());
            dc.setPhone(o.getDeliveryContactPhone());
            dc.setFax(o.getDeliveryContactFax());
            return dc;
        }

        private Customer createCustomer(JsonResponseOrder o) {
            Customer c = new Customer();
            c.setUserCode(o.getCustomerUserCode());
            c.setEmail(o.getCustomerEmail());
            return c;
        }

        private Order createOrder(JsonResponseOrder o) {
            Order order = new Order();
            order.setId(o.getId());
            order.setTransactionId(o.getTransactionId());
            order.setStatus(o.getStatus());
            order.setPrice(o.getPrice());
            order.setCurrency(o.getCurrency());
            order.setPaymentMethod(o.getPaymentMethod());
            order.setDeliveryType(o.getDeliveryType());
            order.setOrderDate(o.getOrderDate());
            order.setCustomField1(o.getCustomField1());
            order.setCustomField2(o.getCustomField2());
            order.setComment(o.getComment());

            return order;
        }
    }
}
