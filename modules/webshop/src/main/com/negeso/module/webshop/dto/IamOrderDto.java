package com.negeso.module.webshop.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class IamOrderDto {

    private Integer id;

    private String transactionId;

    private IamCustomerDto userName;

    private Double price;

    private String currency;

    private String status;

    private String paymentMethod;

    private String deliveryType;

    private Timestamp orderDate;

    private String comment;

    private Double deliveryPrice;

    private Double vatPrice;

    private List<OrderItemDto> orderItems = new ArrayList<>();

    private CustomerDetailsDto customerDetails;

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

    public IamCustomerDto getUserName() {
        return userName;
    }

    public void setUserName(IamCustomerDto userName) {
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

    public List<OrderItemDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDto> orderItems) {
        this.orderItems = orderItems;
    }

    public CustomerDetailsDto getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(CustomerDetailsDto customerDetails) {
        this.customerDetails = customerDetails;
    }
}
