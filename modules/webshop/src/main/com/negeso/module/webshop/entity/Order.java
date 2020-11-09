package com.negeso.module.webshop.entity;

import com.google.gson.annotations.Expose;
import com.negeso.framework.Env;
import com.negeso.framework.jaxb.GermanPriceAdapter;
import com.negeso.framework.jaxb.TimestampAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "order", namespace = Env.NEGESO_NAMESPACE)
public class Order implements Serializable{
	public enum Status{
		OPENED, PAYED, CLOSED;
	}

	public enum DeliveryType{
		EXW, //http://www.mainfreight.be/en/info_point/incoterms.aspx
		FCA,
		FAS,
		FOB,
		DDU
	}
	@Expose private Integer id;
	@Expose private Customer customer;
	private Set<OrderItem> orderItems;
	@Expose private String transactionId;
	@Expose private String status;
	@Expose private BigDecimal price;
	@Expose private String currency;
	@Expose private String paymentMethod;
	@Expose private String deliveryType;
	private DeliveryContact deliveryContact;
	@Expose private Timestamp orderDate;
	@Expose private String customField1;
	@Expose private String customField2;
	@Expose private String comment;
	private BigDecimal deliveryPrice;
	private BigDecimal vat;

	public Order() {
		this.orderDate = new Timestamp(System.currentTimeMillis());
	}

	public DeliveryContact getDeliveryContact() {
		return deliveryContact;
	}

	public void setDeliveryContact(DeliveryContact deliveryContact) {
		this.deliveryContact = deliveryContact;
	}

	@XmlAttribute
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Set<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(Set<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	@XmlAttribute
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@XmlAttribute
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(GermanPriceAdapter.class)
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

	@XmlAttribute
	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@XmlAttribute
	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	@XmlAttribute
	@XmlJavaTypeAdapter(TimestampAdapter.class)
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

	@XmlAttribute
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@XmlAttribute
	public BigDecimal getDeliveryPrice() {
		return deliveryPrice;
	}

	public void setDeliveryPrice(BigDecimal deliveryPrice) {
		this.deliveryPrice = deliveryPrice;
	}

	@XmlAttribute
	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Order order = (Order) o;

		if (id != null ? !id.equals(order.id) : order.id != null) return false;
		if (customer != null ? !customer.equals(order.customer) : order.customer != null) return false;
		if (orderItems != null ? !orderItems.equals(order.orderItems) : order.orderItems != null) return false;
		if (transactionId != null ? !transactionId.equals(order.transactionId) : order.transactionId != null)
			return false;
		if (status != null ? !status.equals(order.status) : order.status != null) return false;
		if (price != null ? !price.equals(order.price) : order.price != null) return false;
		if (currency != null ? !currency.equals(order.currency) : order.currency != null) return false;
		if (paymentMethod != null ? !paymentMethod.equals(order.paymentMethod) : order.paymentMethod != null)
			return false;
		return deliveryType != null ? deliveryType.equals(order.deliveryType) : order.deliveryType == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (customer != null ? customer.hashCode() : 0);
		result = 31 * result + (orderItems != null ? orderItems.hashCode() : 0);
		result = 31 * result + (transactionId != null ? transactionId.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		result = 31 * result + (price != null ? price.hashCode() : 0);
		result = 31 * result + (currency != null ? currency.hashCode() : 0);
		result = 31 * result + (paymentMethod != null ? paymentMethod.hashCode() : 0);
		result = 31 * result + (deliveryType != null ? deliveryType.hashCode() : 0);
		return result;
	}
}
