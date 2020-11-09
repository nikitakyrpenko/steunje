package com.negeso.module.webshop.entity.modern;

import javax.persistence.*;

@Entity
@Table(name = "ws_cart_item")
public class ECartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_code", referencedColumnName = "product_number")
    private EProduct product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "cart_owner_id", nullable = false)
    private String cartOwnerId;

    public ECartItem(){}

    public ECartItem(Integer id, EProduct product, Integer quantity, String cartOwnerId) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.cartOwnerId = cartOwnerId;
    }

    public ECartItem(EProduct product, Integer quantity, String cartOwnerId) {
        this.product = product;
        this.quantity = quantity;
        this.cartOwnerId = cartOwnerId;
    }

    public Integer getId() {
        return id;
    }

    public EProduct getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setProduct(EProduct product) {
        this.product = product;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setCartOwnerId(String cartOwnerId) {
        this.cartOwnerId = cartOwnerId;
    }

    public String getCartOwnerId() {
        return cartOwnerId;
    }
}
