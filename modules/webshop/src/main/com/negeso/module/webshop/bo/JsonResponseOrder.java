package com.negeso.module.webshop.bo;

import com.negeso.module.webshop.entity.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

public class JsonResponseOrder {

	private Integer id;
	private String transactionId;
	private String status;
	private BigDecimal price;
	private String currency;
	private String paymentMethod;
	private String deliveryType;
	private Timestamp orderDate;
	private String customField1;
	private String customField2;
	private String comment;

	private Customer customer;
	private String customerUserCode;
	private String customerEmail;

	private DeliveryContact deliveryContact;
	private Integer deliveryContactId;
	private String deliveryContactFirstName;
	private String deliveryContactSecondName;
	private String deliveryContactCompanyName;
	private String deliveryContactAddressLine;
	private String deliveryContactZipCode;
	private String deliveryContactCity;
	private String deliveryContactCountry;
	private String deliveryContactPhone;
	private String deliveryContactFax;

	private Set<OrderItem> orderItems;
	private Integer orderItemId;
	private Integer orderItemQuantity;
	private BigDecimal orderItemDiscount;
	private BigDecimal orderItemPrice;
	private Product product;
	private String orderItemProductId;
	private String orderItemProductProductNumber;
	private String orderItemProductTitle;
	private String orderItemProductEan;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public String getCustomField1() {
		return customField1;
	}

	public void setCustomField1(String customField1) {
		this.customField1 = customField1;
	}

	public String getCustomField2() {
		return customField2;
	}

	public void setCustomField2(String customField2) {
		this.customField2 = customField2;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getCustomerUserCode() {
		return customerUserCode;
	}

	public void setCustomerUserCode(String customerUserCode) {
		this.customerUserCode = customerUserCode;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public DeliveryContact getDeliveryContact() {
		return deliveryContact;
	}

	public void setDeliveryContact(DeliveryContact deliveryContact) {
		this.deliveryContact = deliveryContact;
	}

	public Integer getDeliveryContactId() {
		return deliveryContactId;
	}

	public void setDeliveryContactId(Integer deliveryContactId) {
		this.deliveryContactId = deliveryContactId;
	}

	public String getDeliveryContactFirstName() {
		return deliveryContactFirstName;
	}

	public void setDeliveryContactFirstName(String deliveryContactFirstName) {
		this.deliveryContactFirstName = deliveryContactFirstName;
	}

	public String getDeliveryContactSecondName() {
		return deliveryContactSecondName;
	}

	public void setDeliveryContactSecondName(String deliveryContactSecondName) {
		this.deliveryContactSecondName = deliveryContactSecondName;
	}

	public String getDeliveryContactCompanyName() {
		return deliveryContactCompanyName;
	}

	public void setDeliveryContactCompanyName(String deliveryContactCompanyName) {
		this.deliveryContactCompanyName = deliveryContactCompanyName;
	}

	public String getDeliveryContactAddressLine() {
		return deliveryContactAddressLine;
	}

	public void setDeliveryContactAddressLine(String deliveryContactAddressLine) {
		this.deliveryContactAddressLine = deliveryContactAddressLine;
	}

	public String getDeliveryContactZipCode() {
		return deliveryContactZipCode;
	}

	public void setDeliveryContactZipCode(String deliveryContactZipCode) {
		this.deliveryContactZipCode = deliveryContactZipCode;
	}

	public String getDeliveryContactCity() {
		return deliveryContactCity;
	}

	public void setDeliveryContactCity(String deliveryContactCity) {
		this.deliveryContactCity = deliveryContactCity;
	}

	public String getDeliveryContactCountry() {
		return deliveryContactCountry;
	}

	public void setDeliveryContactCountry(String deliveryContactCountry) {
		this.deliveryContactCountry = deliveryContactCountry;
	}

	public String getDeliveryContactPhone() {
		return deliveryContactPhone;
	}

	public void setDeliveryContactPhone(String deliveryContactPhone) {
		this.deliveryContactPhone = deliveryContactPhone;
	}

	public String getDeliveryContactFax() {
		return deliveryContactFax;
	}

	public void setDeliveryContactFax(String deliveryContactFax) {
		this.deliveryContactFax = deliveryContactFax;
	}

	public Set<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(Set<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public Integer getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
	}

	public Integer getOrderItemQuantity() {
		return orderItemQuantity;
	}

	public void setOrderItemQuantity(Integer orderItemQuantity) {
		this.orderItemQuantity = orderItemQuantity;
	}

	public BigDecimal getOrderItemDiscount() {
		return orderItemDiscount;
	}

	public void setOrderItemDiscount(BigDecimal orderItemDiscount) {
		this.orderItemDiscount = orderItemDiscount;
	}

	public BigDecimal getOrderItemPrice() {
		return orderItemPrice;
	}

	public void setOrderItemPrice(BigDecimal orderItemPrice) {
		this.orderItemPrice = orderItemPrice;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getOrderItemProductId() {
		return orderItemProductId;
	}

	public void setOrderItemProductId(String orderItemProductId) {
		this.orderItemProductId = orderItemProductId;
	}

	public String getOrderItemProductProductNumber() {
		return orderItemProductProductNumber;
	}

	public void setOrderItemProductProductNumber(String orderItemProductProductNumber) {
		this.orderItemProductProductNumber = orderItemProductProductNumber;
	}

	public String getOrderItemProductTitle() {
		return orderItemProductTitle;
	}

	public void setOrderItemProductTitle(String orderItemProductTitle) {
		this.orderItemProductTitle = orderItemProductTitle;
	}

	public String getOrderItemProductEan() {
		return orderItemProductEan;
	}

	public void setOrderItemProductEan(String orderItemProductEan) {
		this.orderItemProductEan = orderItemProductEan;
	}
}
