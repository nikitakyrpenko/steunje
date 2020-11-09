package com.negeso.module.webshop.entity;

import javax.persistence.*;

@Entity(name = "CustomerDetails")
@Table(name = "ws_customer_details")
public class CustomerDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "sportclub", nullable = false)
    private String sportClubName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSportClubName() {
        return sportClubName;
    }

    public void setSportClubName(String sportClubName) {
        this.sportClubName = sportClubName;
    }
}
