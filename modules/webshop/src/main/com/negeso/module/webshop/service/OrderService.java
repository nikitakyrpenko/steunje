package com.negeso.module.webshop.service;

import com.negeso.framework.Env;
import com.negeso.framework.util.hibernate.HibernateUtil;
import com.negeso.module.webshop.bo.CartDetails;
import com.negeso.module.webshop.dao.OrderDao;
import com.negeso.module.webshop.entity.CartItem;
import com.negeso.module.webshop.entity.Customer;
import com.negeso.module.webshop.entity.Order;
import com.negeso.module.webshop.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class OrderService {

	private OrderDao orderDao;
	private DiscountService discountService;

	public OrderService() {
	}

	@Autowired
	public OrderService(OrderDao orderDao, DiscountService discountService) {
		this.orderDao = orderDao;
		this.discountService = discountService;
	}

	@Transactional
	public Integer save(Order order) {
		return orderDao.saveNow(order);
	}

	public Integer getNextSeq() {
		return orderDao.getNextSeq();
	}

	public Order findOne(Integer id) {
		return orderDao.findOne(id);
	}

	public Order findOneByCustomer(Integer id, Customer customer) {
		return orderDao.findOneByCustomer(id, customer);
	}

	public Order findOneByIdealId(String idealTransactionId) {
		return orderDao.findOneByIdealId(idealTransactionId);
	}

	public List<Order> list() {
		return orderDao.findAll();
	}

	public List<Order> listByCustomer(Customer customer) {
		return orderDao.findAllByCustomer(customer);
	}

	@Transactional
	public void update(Order order) {
		orderDao.update(order);
	}

	public Order findOneForResponse(Integer id) {

		return orderDao.findOneForResponse(id);
	}

	@Transactional
	public CartDetails calculateOrderDetails(Customer customer) {
		BigDecimal orderPriceExDiscountExVat = BigDecimal.ZERO;
		BigDecimal orderPriceIncDiscountExVat = BigDecimal.ZERO;
		for (CartItem cartItem : customer.getItems()) {
			BigDecimal productDiscount = discountService.determineDiscount(customer, cartItem);
			Product product = cartItem.getProduct();
			product = HibernateUtil.initAndUnproxy(product);
			Integer quantity = cartItem.getQuantity();
			BigDecimal quantityPrice = product.getPriceExcludeVat().multiply(new BigDecimal(quantity));
			orderPriceExDiscountExVat = orderPriceExDiscountExVat.add(quantityPrice);
			if (productDiscount != null) {
				BigDecimal priceWithDiscount = quantityPrice.subtract(quantityPrice.multiply(productDiscount));
				orderPriceIncDiscountExVat = orderPriceIncDiscountExVat.add(priceWithDiscount);
			} else {
				orderPriceIncDiscountExVat = orderPriceIncDiscountExVat.add(quantityPrice);
			}
		}

		BigDecimal vatPercent = new BigDecimal(Env.getProperty("ws.vat", "21")).divide(new BigDecimal(100), 2, RoundingMode.CEILING);

		BigDecimal deliveryPriceExVat = this.calculateDeliveryPrice(orderPriceExDiscountExVat);
		BigDecimal deliveryVat = deliveryPriceExVat.multiply(vatPercent);

		BigDecimal itemsVatExDiscount = orderPriceExDiscountExVat.multiply(vatPercent);
		BigDecimal itemsVatIncDiscount = orderPriceIncDiscountExVat.multiply(vatPercent);

		// exclude delivery vat in calculating.
//		orderPriceExDiscountExVat = orderPriceExDiscountExVat.add(deliveryPriceExVat);
//		orderPriceIncDiscountExVat = orderPriceIncDiscountExVat.add(deliveryPriceExVat);
		BigDecimal orderPriceExDiscountIncVat = orderPriceExDiscountExVat.add(itemsVatExDiscount).add(deliveryVat);
		BigDecimal orderPriceIncDiscountIncVat = orderPriceIncDiscountExVat.add(itemsVatIncDiscount).add(deliveryVat);

		return new CartDetails()
				.delivery(deliveryPriceExVat.setScale(2, BigDecimal.ROUND_CEILING))
				.exDisExVat(orderPriceExDiscountExVat.setScale(2, BigDecimal.ROUND_CEILING))
				.incDisExVat(orderPriceIncDiscountExVat.setScale(2, BigDecimal.ROUND_CEILING))
				.exDisIncVat(orderPriceExDiscountIncVat.setScale(2, BigDecimal.ROUND_CEILING))
				.incDisIncVat(orderPriceIncDiscountIncVat.setScale(2, BigDecimal.ROUND_CEILING))
				.items(customer.getItems().size())
				;
	}

	private BigDecimal calculateDeliveryPrice(BigDecimal orderPriceWithoutDiscount) {
		return orderPriceWithoutDiscount.compareTo(new BigDecimal(Env.getProperty("ws.max_price_to_include_delivery_cost", "250"))) > 0 ? BigDecimal.ZERO : new BigDecimal(Env.getProperty("ws.delivery_cost", "25"));
	}

	public Order findOneByProducts(Customer customer, List<Product> products){
		return orderDao.findOneByProducts(customer, products);
	}

	public List<Order> findAllOpenedByCustomer(Customer customer){
		return orderDao.findAllOpenedByCustomer(customer);
	}
}
