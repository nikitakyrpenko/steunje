package com.negeso.module.webshop.entity.modern;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "contact")
public class EContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "address_line")
    private String addressLine;
    @Column(name = "zip_code")
    private String zipCode;
    @Column(name = "state")
    private String state;
    @Column(name = "city")
    private String city;
    @Column(name = "country")
    private String country;
    @Column(name = "phone")
    private String phone;
    @Column(name = "fax")
    private String fax;
    @Column(name = "web_link")
    private String webLink;
    @Column(name = "email")
    private String email;
    @Column(name = "image_link")
    private String imageLink;
    @Column(name = "type")
    private String type;
    @Column(name = "department")
    private String department;
    @Column(name = "job_title")
    private String jobTitle;
    @Column(name = "birth_date")
    private Timestamp birthDate;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "establishment")
    private String establishment;
    @Column(name = "checkbox")
    private Integer checkbox;
    @Column(name = "manager")
    private String manager;
    @Column(name = "mobile")
    private String mobile;
    @Column(name = "office_number")
    private String officeNumber;
    @Column(name = "memo1")
    private String memo1;
    @Column(name = "memo2")
    private String memo2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Timestamp getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Timestamp birthDate) {
        this.birthDate = birthDate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEstablishment() {
        return establishment;
    }

    public void setEstablishment(String establishment) {
        this.establishment = establishment;
    }

    public Integer getCheckbox() {
        return checkbox;
    }

    public void setCheckbox(Integer checkbox) {
        this.checkbox = checkbox;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    public String getMemo1() {
        return memo1;
    }

    public void setMemo1(String memo1) {
        this.memo1 = memo1;
    }

    public String getMemo2() {
        return memo2;
    }

    public void setMemo2(String memo2) {
        this.memo2 = memo2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EContact that = (EContact) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(secondName, that.secondName) &&
                Objects.equals(companyName, that.companyName) &&
                Objects.equals(addressLine, that.addressLine) &&
                Objects.equals(zipCode, that.zipCode) &&
                Objects.equals(state, that.state) &&
                Objects.equals(city, that.city) &&
                Objects.equals(country, that.country) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(fax, that.fax) &&
                Objects.equals(webLink, that.webLink) &&
                Objects.equals(email, that.email) &&
                Objects.equals(imageLink, that.imageLink) &&
                Objects.equals(type, that.type) &&
                Objects.equals(department, that.department) &&
                Objects.equals(jobTitle, that.jobTitle) &&
                Objects.equals(birthDate, that.birthDate) &&
                Objects.equals(nickname, that.nickname) &&
                Objects.equals(establishment, that.establishment) &&
                Objects.equals(checkbox, that.checkbox) &&
                Objects.equals(manager, that.manager) &&
                Objects.equals(mobile, that.mobile) &&
                Objects.equals(officeNumber, that.officeNumber) &&
                Objects.equals(memo1, that.memo1) &&
                Objects.equals(memo2, that.memo2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, secondName, companyName, addressLine, zipCode, state, city, country, phone, fax, webLink, email, imageLink, type, department, jobTitle, birthDate, nickname, establishment, checkbox, manager, mobile, officeNumber, memo1, memo2);
    }

    @Override
    public String toString() {
        return "IamContact{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", addressLine='" + addressLine + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", phone='" + phone + '\'' +
                ", fax='" + fax + '\'' +
                ", webLink='" + webLink + '\'' +
                ", email='" + email + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", type='" + type + '\'' +
                ", department='" + department + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", birthDate=" + birthDate +
                ", nickname='" + nickname + '\'' +
                ", establishment='" + establishment + '\'' +
                ", checkbox=" + checkbox +
                ", manager='" + manager + '\'' +
                ", mobile='" + mobile + '\'' +
                ", officeNumber='" + officeNumber + '\'' +
                ", memo1='" + memo1 + '\'' +
                ", memo2='" + memo2 + '\'' +
                '}';
    }
}
