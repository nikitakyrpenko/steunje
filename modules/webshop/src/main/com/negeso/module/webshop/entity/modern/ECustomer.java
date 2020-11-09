package com.negeso.module.webshop.entity.modern;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ws_customer")
public class ECustomer implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "login")
    private String login;

    @Column(name = "email")
    private String email;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "shipping_contact", referencedColumnName = "id")
    private EContact shippingContact;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "billing_contact", referencedColumnName = "id")
    private EContact billingContact;

    @Column(name = "price_group_id")
    private String priceGroupId;

    @Column(name = "display_price")
    private String displayPrice;

    @Column(name = "post_pay_allowed")
    private Boolean postPayAllowed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPriceGroupId() {
        return priceGroupId;
    }

    public void setPriceGroupId(String priceGroupId) {
        this.priceGroupId = priceGroupId;
    }

    public String getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(String displayPrice) {
        this.displayPrice = displayPrice;
    }

    public Boolean getPostPayAllowed() {
        return postPayAllowed;
    }

    public void setPostPayAllowed(Boolean postPayAllowed) {
        this.postPayAllowed = postPayAllowed;
    }

    public EContact getBillingContact() {
        return billingContact;
    }

    public void setShippingContact(EContact shippingContact) {
        this.shippingContact = shippingContact;
    }

    public EContact getShippingContact() {
        return shippingContact;
    }

    public void setBillingContact(EContact billingContact) {
        this.billingContact = billingContact;
    }

}
