package com.negeso.module.webshop.entity.modern;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "ws_order",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "customer_details_id"})}
)
public class EOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "transaction_id")
    private String transactionId;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "username", referencedColumnName = "login")
    private ECustomer userName;

    @Column(name = "price")
    private Double price;

    @Column(name = "currency")
    private String currency;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "delivery_type")
    private String deliveryType;

    @Column(name = "order_date")
    private Timestamp orderDate;

    @Column(name = "comment")
    private String comment;

    @Column(name = "delivery_price")
    private Double deliveryPrice;

    @Column(name = "vat_price")
    private Double vatPrice;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_details_id", referencedColumnName = "id")
    private ECustomerDetails ECustomerDetails;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "order")
    private List<EOrderItem> orderItems = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public ECustomer getUserName() {
        return userName;
    }

    public void setUserName(ECustomer userName) {
        this.userName = userName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public Double getVatPrice() {
        return vatPrice;
    }

    public void setVatPrice(Double vatPrice) {
        this.vatPrice = vatPrice;
    }

    public List<EOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<EOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public ECustomerDetails getECustomerDetails() {
        return ECustomerDetails;
    }

    public void setECustomerDetails(ECustomerDetails ECustomerDetails) {
        this.ECustomerDetails = ECustomerDetails;
    }

    public void addOrderItem(EOrderItem orderItem){
        orderItem.setOrder(this.getId());
        this.getOrderItems().add(orderItem);
    }
}
