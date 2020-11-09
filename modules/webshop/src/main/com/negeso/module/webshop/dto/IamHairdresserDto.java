package com.negeso.module.webshop.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IamHairdresserDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("last-name")
    private String lastname;

    @JsonProperty("phone")
    private String telephone;

    @JsonProperty("birthday")
    private String birthdate;

    @JsonProperty("kvk-number")
    private String kvkNumber;

    @JsonProperty("btw-number")
    private String btwNumber;

    @JsonProperty("website")
    private String website;

    @JsonProperty("comments")
    private String comment;

    private UserDto user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getKvkNumber() {
        return kvkNumber;
    }

    public void setKvkNumber(String kvkNumber) {
        this.kvkNumber = kvkNumber;
    }

    public String getBtwNumber() {
        return btwNumber;
    }

    public void setBtwNumber(String btwNumber) {
        this.btwNumber = btwNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "IamHairdresserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", telephone='" + telephone + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", kvkNumber='" + kvkNumber + '\'' +
                ", btwNumber='" + btwNumber + '\'' +
                ", website='" + website + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IamHairdresserDto that = (IamHairdresserDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(email, that.email) &&
                Objects.equals(name, that.name) &&
                Objects.equals(lastname, that.lastname) &&
                Objects.equals(telephone, that.telephone) &&
                Objects.equals(birthdate, that.birthdate) &&
                Objects.equals(kvkNumber, that.kvkNumber) &&
                Objects.equals(btwNumber, that.btwNumber) &&
                Objects.equals(website, that.website) &&
                Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name, lastname, telephone, birthdate, kvkNumber, btwNumber, website, comment);
    }
}
