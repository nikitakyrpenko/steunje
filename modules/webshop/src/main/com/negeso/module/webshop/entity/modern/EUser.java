package com.negeso.module.webshop.entity.modern;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_list")
public class EUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "username")
    private String username;
    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String password;
    @Column(name = "type")
    private String type;
    @Column(name = "extra")
    private String extra;
    @Column(name = "site_id")
    private Integer siteId;
    @Column(name = "publish_date")
    private Date publishDate;
    @Column(name = "expired_date")
    private Date expiredDate;
    @Column(name = "single_user")
    private Boolean singleUser;
    @Column(name = "guid")
    private String guid;
    @Column(name = "last_action_date")
    private Timestamp lastActionDate;
    @Column(name = "token")
    private String token;
    @Column(name = "verification")
    private Boolean verification;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Boolean getSingleUser() {
        return singleUser;
    }

    public void setSingleUser(Boolean singleUser) {
        this.singleUser = singleUser;
    }

    public String getGuide() {
        return guid;
    }

    public void setGuide(String guide) {
        this.guid = guide;
    }

    public Timestamp getLastActionDate() {
        return lastActionDate;
    }

    public void setLastActionDate(Timestamp lastActionDate) {
        this.lastActionDate = lastActionDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getVerification() {
        return verification;
    }

    public void setVerification(Boolean verification) {
        this.verification = verification;
    }

    @Override
    public String toString() {
        return "IamUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", type='" + type + '\'' +
                ", extra='" + extra + '\'' +
                ", siteId=" + siteId +
                ", publishDate=" + publishDate +
                ", expiredDate=" + expiredDate +
                ", singleUser=" + singleUser +
                ", guid='" + guid + '\'' +
                ", lastActionDate=" + lastActionDate +
                ", token='" + token + '\'' +
                ", verification=" + verification +
                '}';
    }
}
