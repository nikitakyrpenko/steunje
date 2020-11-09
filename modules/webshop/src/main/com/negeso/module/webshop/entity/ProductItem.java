package com.negeso.module.webshop.entity;

import java.io.Serializable;

public interface ProductItem extends Serializable{
	Product getProduct();
	Integer getQuantity();
}
