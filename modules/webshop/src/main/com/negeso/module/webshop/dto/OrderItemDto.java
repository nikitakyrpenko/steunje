package com.negeso.module.webshop.dto;


public class OrderItemDto {

    private Integer id;

    private ProductDto product;

    private Integer quantity;

    private Double price;

    private Double discount;

    private Integer order;

    private String cartOwnerId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getCartOwnerId() {
        return cartOwnerId;
    }

    public void setCartOwnerId(String cartOwnerId) {
        this.cartOwnerId = cartOwnerId;
    }
}
