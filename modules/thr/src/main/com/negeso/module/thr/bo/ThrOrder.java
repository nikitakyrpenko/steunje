package com.negeso.module.thr.bo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;
import com.negeso.framework.jaxb.TimestampAdapter;
import com.negeso.module.thr.utils.CurrencyFormatter;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "order",namespace = Env.NEGESO_NAMESPACE)
public class ThrOrder implements Entity {
	
	private static final long serialVersionUID = 6344327646209987484L;
	
	@XmlAttribute
	private Long id;
	@XmlAttribute
	private String orderNumber;
	@XmlAttribute
	private String login;
	@XmlAttribute
	private String password;
	@XmlAttribute
	private String appVersion;
	@XmlAttribute
	private String osVersion;
	@XmlAttribute
	private String apiVersion;
	@XmlAttribute
	private String device;
	@XmlAttribute
	private String model;
	@XmlAttribute
	private String product;
	@XmlAttribute
	private Integer width;
	@XmlAttribute
	private Integer height;
	@XmlAttribute
	@XmlJavaTypeAdapter(TimestampAdapter.class)    
	private Timestamp date;
	@XmlAttribute
	private Integer productsCount = 0;
	@XmlAttribute
	private Integer nonorderableCount = 0;
	@XmlAttribute
	private Double total = 0.0d;
	
	@XmlElementWrapper(name="products",namespace = Env.NEGESO_NAMESPACE)
	@XmlElement(name="product",namespace = Env.NEGESO_NAMESPACE)
	private List<OrderItem> products = new ArrayList<OrderItem>();
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public List<OrderItem> getProducts() {
		return products;
	}

	public void setProducts(List<OrderItem> products) {
		this.products = products;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Integer getProductsCount() {
		return productsCount;
	}

	public void setProductsCount(Integer productsCount) {
		this.productsCount = productsCount;
	}

	public Integer getNonorderableCount() {
		return nonorderableCount;
	}

	public void setNonorderableCount(Integer nonorderableCount) {
		this.nonorderableCount = nonorderableCount;
	}

	public Double getTotal() {
		return total;
	}
	
	@XmlAttribute
	public String getTotalFormatted() {
		return CurrencyFormatter.format(total);
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public void add(OrderItem orderItem) {
		OrderItem existenProduct = findByCode(orderItem.getBarCode());
		if (existenProduct == null) {
			getProducts().add(orderItem);
		} else {
			existenProduct.setProductInfo(orderItem.getProductInfo());
		}
		recalculate();
	}
	
	public OrderItem findByCode(String barCode) {
		for (OrderItem product: products) {
			if (product.getBarCode().equals(barCode)) {
				return product;
			}
		}
		return null;
	}
	

	public void clear() {
		getProducts().clear();
		setProductsCount(0);
		setNonorderableCount(0);
		setTotal(0.0d);
	}
	
	public void recalculate() {
		Double total = 0d;
		int productsCount = 0;
		int nonorderableCount = 0;
		for (OrderItem product: products) {
			if (product.isAvailable()) {
				productsCount++; 
				total += product.getCount() * product.getProductInfo().getPrice();
			} else {
				nonorderableCount++;
			}
		}
		setTotal(total);
		setProductsCount(productsCount);
		setNonorderableCount(nonorderableCount);
	}
}
