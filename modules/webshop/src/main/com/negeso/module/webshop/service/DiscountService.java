package com.negeso.module.webshop.service;

import com.negeso.module.webshop.dao.PriceListProduct2CustomerDao;
import com.negeso.module.webshop.dao.PriceListProductGroup2CustomerDao;
import com.negeso.module.webshop.dao.PriceListStandardDao;
import com.negeso.module.webshop.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class DiscountService {

	private final PriceListProductGroup2CustomerDao priceListProductGroup2CustomerDao;
	private final PriceListStandardDao priceListStandardDao;
	private final PriceListProduct2CustomerDao priceListProduct2CustomerDao;

	@Autowired
	public DiscountService(PriceListProductGroup2CustomerDao priceListProductGroup2CustomerDao, PriceListStandardDao priceListStandardDao, PriceListProduct2CustomerDao priceListProduct2CustomerDao) {
		this.priceListProductGroup2CustomerDao = priceListProductGroup2CustomerDao;
		this.priceListStandardDao = priceListStandardDao;
		this.priceListProduct2CustomerDao = priceListProduct2CustomerDao;
	}

	public BigDecimal determineDiscount(Customer customer, ProductItem item){
		BigDecimal discount;

		if ((discount = customer2ProductViaPricelistDiscount(customer, item)) != null)
			return discount;
		else if ((discount = customer2ProductDiscount(customer, item)) != null)
			return discount;
		else if ((discount = customer2productGroupDiscount(customer, item)) != null)
			return discount;
		else if ((discount = priceList2ProductGroupDiscount(customer, item)) != null)
			return discount;
		else
			return null;
	}

	private BigDecimal customer2ProductDiscount(Customer customer, ProductItem item) {
		if (customer == null) return null;

		List<PriceListProduct2Customer> allByCustomerAndProduct = priceListProduct2CustomerDao.findAllByCustomerAndProduct(customer, item.getProduct().getId());
		for (PriceListProduct2Customer result : allByCustomerAndProduct) {
			if (item.getQuantity() >= result.getSinceCount())
				return result.getDiscount();
		}

		return null;
	}

	private BigDecimal customer2ProductViaPricelistDiscount(Customer customer, ProductItem item){
		if (customer == null || customer.getPriceGroup() == null) return null;

		Product product = item.getProduct();
		List<PriceListProduct> customer2GroupDiscount = new ArrayList<PriceListProduct>();
		for (PriceListProduct priceListProduct : product.getPriceListProduct()) {
			if (priceListProduct.getListGroup().getGroup().equals(customer.getPriceGroup().getGroup())){

				customer2GroupDiscount.add(priceListProduct);
//				return item.getQuantity() >= priceListProduct.getSince() ? priceListProduct.getDiscount() : null;
			}
		}

		Collections.sort(customer2GroupDiscount, new Comparator<PriceListProduct>() {
			@Override
			public int compare(PriceListProduct o1, PriceListProduct o2) {
				return o1.getSince() > o2.getSince() ? 1 : o1.getSince() < o2.getSince() ? -1 : 0;
			}
		});

		// Reverse list ro descending order to correct checking if item quantity in range of products.
		Collections.reverse(customer2GroupDiscount);

		for (PriceListProduct result : customer2GroupDiscount) {
			if (item.getQuantity() >= result.getSince())
				return result.getDiscount();
		}

		return null;
	}

	private BigDecimal customer2productGroupDiscount(Customer customer, ProductItem item){
		if (customer == null) return null;

		List<PriceListProductGroup2Customer> listProductGroup2Customer = priceListProductGroup2CustomerDao.findAllByCustomerAndProductGroup(customer, item.getProduct().getArticleGroup());

		for (PriceListProductGroup2Customer result : listProductGroup2Customer) {
			if (item.getQuantity() >= result.getSinceCount())
				return result.getDiscount();
		}

		return null;
	}

	private BigDecimal priceList2ProductGroupDiscount(Customer customer, ProductItem item){
		if (customer == null) return null;

		List<PriceListStandard> priceListAndProductGroup = priceListStandardDao.findAllByPriceListAndProductGroup(customer.getPriceGroup(), item.getProduct().getArticleGroup());
		for (PriceListStandard result : priceListAndProductGroup) {
			if (item.getQuantity() >= result.getSinceCount())
				return result.getDiscount();
		}

		return null;
	}
}
