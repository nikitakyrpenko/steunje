package com.negeso.module.webshop.dto;

import java.util.Objects;

public class CartItemDto {

    private Integer id;

    private ProductDto product;

    private Integer quantity;

    private String cartOwnerId;

    public CartItemDto(Integer id, ProductDto product, Integer quantity, String cartOwnerId) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.cartOwnerId = cartOwnerId;
    }

    public CartItemDto(ProductDto product, Integer quantity, String cartOwnerId) {
        this.product = product;
        this.quantity = quantity;
        this.cartOwnerId = cartOwnerId;
    }

    public CartItemDto() {}

    public Integer getId() {
        return id;
    }

    public ProductDto getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getCartOwnerId() {
        return cartOwnerId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setCartOwnerId(String cartOwnerId) {
        this.cartOwnerId = cartOwnerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemDto that = (CartItemDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(product, that.product) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(cartOwnerId, that.cartOwnerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, quantity, cartOwnerId);
    }

    @Override
    public String toString() {
        return "CartItemDto{" +
                "id=" + id +
                ", product=" + product +
                ", quantity=" + quantity +
                ", cartOwnerId='" + cartOwnerId + '\'' +
                '}';
    }
}
