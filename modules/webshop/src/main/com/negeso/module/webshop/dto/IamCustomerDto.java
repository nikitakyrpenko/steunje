package com.negeso.module.webshop.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IamCustomerDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("login")
    private String login;

    @JsonProperty("email")
    private String email;

    @JsonProperty("shipping_contact")
    private IamContactDto shippingContact;

    @JsonProperty("billing_contact")
    private IamContactDto billingContact;

    @JsonProperty("price_group_id")
    private String priceGroupId;

    @JsonProperty("display_price")
    private String displayPrice;

    @JsonProperty("post_pay_allowed")
    private Boolean postPayAllowed;

    private IamHairdresserDto creator;

    public IamHairdresserDto getCreator() {
        return creator;
    }

    public void setCreator(IamHairdresserDto creator) {
        this.creator = creator;
    }

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

    public IamContactDto getBillingContact() {
        return billingContact;
    }

    public void setShippingContact(IamContactDto shippingContact) {
        this.shippingContact = shippingContact;
    }

    public IamContactDto getShippingContact() {
        return shippingContact;
    }

    public void setBillingContact(IamContactDto billingContact) {
        this.billingContact = billingContact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IamCustomerDto that = (IamCustomerDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(login, that.login) &&
                Objects.equals(email, that.email) &&
                Objects.equals(shippingContact, that.shippingContact) &&
                Objects.equals(billingContact, that.billingContact) &&
                Objects.equals(priceGroupId, that.priceGroupId) &&
                Objects.equals(displayPrice, that.displayPrice) &&
                Objects.equals(postPayAllowed, that.postPayAllowed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, email, shippingContact, billingContact, priceGroupId, displayPrice, postPayAllowed);
    }

    @Override
    public String toString() {
        return "IamCustomerDto{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", shippingContact=" + shippingContact +
                ", billingContact=" + billingContact +
                ", priceGroupId='" + priceGroupId + '\'' +
                ", displayPrice='" + displayPrice + '\'' +
                ", postPayAllowed=" + postPayAllowed +
                '}';
    }
}
