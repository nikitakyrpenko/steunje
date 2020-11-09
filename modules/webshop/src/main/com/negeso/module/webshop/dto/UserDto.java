package com.negeso.module.webshop.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.sql.Timestamp;
import java.util.Objects;
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    
    @JsonProperty("id")
    private Long id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("login")
    private String login;
    @JsonProperty("password")
    private String password;
    @JsonProperty("type")
    private String type;
    @JsonProperty("publish_date")
    private Timestamp publishDate;
    @JsonProperty("expired_date")
    private Timestamp expiredDate;
    @JsonProperty("single_user")
    private Boolean singleUser;
    @JsonProperty("token")
    private String token;
    @JsonProperty("verified")
    private Boolean verification;
    @JsonProperty("site_id")
    private Integer siteId = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Timestamp getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Timestamp publishDate) {
        this.publishDate = publishDate;
    }

    public Timestamp getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Timestamp expiredDate) {
        this.expiredDate = expiredDate;
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

    public Boolean getSingleUser() {
        return singleUser;
    }

    public void setSingleUser(Boolean singleUser) {
        this.singleUser = singleUser;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", type='" + type + '\'' +
                ", publishDate=" + publishDate +
                ", expiredDate=" + expiredDate +
                ", token='" + token + '\'' +
                ", verified=" + verification +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id) &&
                Objects.equals(username, userDto.username) &&
                Objects.equals(login, userDto.login) &&
                Objects.equals(password, userDto.password) &&
                Objects.equals(type, userDto.type) &&
                Objects.equals(publishDate, userDto.publishDate) &&
                Objects.equals(expiredDate, userDto.expiredDate) &&
                Objects.equals(token, userDto.token) &&
                Objects.equals(verification, userDto.verification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, login, password, type, publishDate, expiredDate, token, verification);
    }
}
