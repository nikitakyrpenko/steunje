package com.negeso.module.webshop.dto;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDetailsDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("customer_id")
    private Integer customerId;

    @JsonProperty("batch_id")
    private Integer batchId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("gender")
    private Integer gender;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("birth")
    private String birth;

    @JsonProperty("skin_condition")
    private Integer skinCondition;

    @JsonProperty("address")
    private String address;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("date")
    private String date;

    @JsonProperty("time")
    private String time;

    @JsonProperty("latitude")
    private Float latitude;

    @JsonProperty("longitude")
    private Float longitude;

    @JsonProperty("temperature")
    private Integer temperature;

    @JsonProperty("humidity")
    private Integer humidity;

    @JsonProperty("uv_index")
    private Integer uvIndex;

    @JsonProperty("capture_zone_code")
    private Integer capturingZoneCode;

    @JsonProperty("scalp_hydration_raw_value")
    private Double scalpHydrationRawValue;

    @JsonProperty("scalp_hydration_value")
    private Integer scalpHydrationValue;

    @JsonProperty("scalp_hydration_level")
    private Integer scalpHydrationLevel;

    @JsonProperty("scalp_hydration_desc")
    private String scalpHydrationDesc;

    @JsonProperty("scalp_hydration_org_url")
    private String scalpHydrationOrgUrl;

    @JsonProperty("scalp_hydration_rst_url")
    private String scalpHydrationRstUrl;

    @JsonProperty("scalp_sebum_raw_value")
    private Double scalpSebumRawValue;

    @JsonProperty("scalp_sebum_value")
    private Integer scalpSebumValue;

    @JsonProperty("scalp_sebum_level")
    private Integer scalpSebumLevel;

    @JsonProperty("scalp_sebum_desc")
    private String scalpSebumDesc;

    @JsonProperty("scalp_sebum_org_url")
    private String scalpSebumOrgUrl;

    @JsonProperty("scalp_sebum_rst_url")
    private String scalpSebumRstUrl;

    @JsonProperty("hair_density_raw_value")
    private Double hairDensityRawValue;

    @JsonProperty("hair_density_value")
    private Integer hairDensityValue;

    @JsonProperty("hair_density_level")
    private Integer hairDensityLevel;

    @JsonProperty("hair_density_desc")
    private String hairDensityDesc;

    @JsonProperty("hair_density_org_url")
    private String hairDensityOrgUrl;

    @JsonProperty("hair_density_rst_url")
    private String hairDensityRstUrl;

    @JsonProperty("dead_skin_cells_raw_value")
    private Double deadSkinCellsRawValue;

    @JsonProperty("dead_skin_cells_value")
    private Integer deadSkinCellsValue;

    @JsonProperty("dead_skin_cells_level")
    private Integer deadSkinCellsLevel;

    @JsonProperty("dead_skin_cells_desc")
    private String deadSkinCellsDesc;

    @JsonProperty("dead_skin_cells_org_url")
    private String deadSkinCellsOrgUrl;

    @JsonProperty("dead_skin_cells_rst_url")
    private String deadSkinCellsRstValue;

    @JsonProperty("scalp_impurities_raw_value")
    private Double scalpImpuritiesRawValue;

    @JsonProperty("scalp_impurities_value")
    private Integer scalpImpuritiesValue;

    @JsonProperty("scalp_impurities_level")
    private Integer scalpImpuritiesLevel;

    @JsonProperty("scalp_impurities_desc")
    private String scalpImpuritiesDesc;

    @JsonProperty("scalp_impurities_org_url")
    private String scalpImpuritiesOrgUrl;

    @JsonProperty("scalp_impurities_rst_url")
    private String scalpImpuritiesRstUrl;

    @JsonProperty("comments")
    private String comments;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public Integer getSkinCondition() {
        return skinCondition;
    }

    public void setSkinCondition(Integer skinCondition) {
        this.skinCondition = skinCondition;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(Integer uvIndex) {
        this.uvIndex = uvIndex;
    }

    public Integer getCapturingZoneCode() {
        return capturingZoneCode;
    }

    public void setCapturingZoneCode(Integer capturingZoneCode) {
        this.capturingZoneCode = capturingZoneCode;
    }

    public Double getScalpHydrationRawValue() {
        return scalpHydrationRawValue;
    }

    public void setScalpHydrationRawValue(Double scalpHydrationRawValue) {
        this.scalpHydrationRawValue = scalpHydrationRawValue;
    }

    public Integer getScalpHydrationValue() {
        return scalpHydrationValue;
    }

    public void setScalpHydrationValue(Integer scalpHydrationValue) {
        this.scalpHydrationValue = scalpHydrationValue;
    }

    public Integer getScalpHydrationLevel() {
        return scalpHydrationLevel;
    }

    public void setScalpHydrationLevel(Integer scalpHydrationLevel) {
        this.scalpHydrationLevel = scalpHydrationLevel;
    }

    public String getScalpHydrationDesc() {
        return scalpHydrationDesc;
    }

    public void setScalpHydrationDesc(String scalpHydrationDesc) {
        this.scalpHydrationDesc = scalpHydrationDesc;
    }

    public String getScalpHydrationOrgUrl() {
        return scalpHydrationOrgUrl;
    }

    public void setScalpHydrationOrgUrl(String scalpHydrationOrgUrl) {
        this.scalpHydrationOrgUrl = scalpHydrationOrgUrl;
    }

    public String getScalpHydrationRstUrl() {
        return scalpHydrationRstUrl;
    }

    public void setScalpHydrationRstUrl(String scalpHydrationRstUrl) {
        this.scalpHydrationRstUrl = scalpHydrationRstUrl;
    }

    public Double getScalpSebumRawValue() {
        return scalpSebumRawValue;
    }

    public void setScalpSebumRawValue(Double scalpSebumRawValue) {
        this.scalpSebumRawValue = scalpSebumRawValue;
    }

    public Integer getScalpSebumValue() {
        return scalpSebumValue;
    }

    public void setScalpSebumValue(Integer scalpSebumValue) {
        this.scalpSebumValue = scalpSebumValue;
    }

    public Integer getScalpSebumLevel() {
        return scalpSebumLevel;
    }

    public void setScalpSebumLevel(Integer scalpSebumLevel) {
        this.scalpSebumLevel = scalpSebumLevel;
    }

    public String getScalpSebumDesc() {
        return scalpSebumDesc;
    }

    public void setScalpSebumDesc(String scalpSebumDesc) {
        this.scalpSebumDesc = scalpSebumDesc;
    }

    public String getScalpSebumOrgUrl() {
        return scalpSebumOrgUrl;
    }

    public void setScalpSebumOrgUrl(String scalpSebumOrgUrl) {
        this.scalpSebumOrgUrl = scalpSebumOrgUrl;
    }

    public String getScalpSebumRstUrl() {
        return scalpSebumRstUrl;
    }

    public void setScalpSebumRstUrl(String scalpSebumRstUrl) {
        this.scalpSebumRstUrl = scalpSebumRstUrl;
    }

    public Double getHairDensityRawValue() {
        return hairDensityRawValue;
    }

    public void setHairDensityRawValue(Double hairDensityRawValue) {
        this.hairDensityRawValue = hairDensityRawValue;
    }

    public Integer getHairDensityValue() {
        return hairDensityValue;
    }

    public void setHairDensityValue(Integer hairDensityValue) {
        this.hairDensityValue = hairDensityValue;
    }

    public Integer getHairDensityLevel() {
        return hairDensityLevel;
    }

    public void setHairDensityLevel(Integer hairDensityLevel) {
        this.hairDensityLevel = hairDensityLevel;
    }

    public String getHairDensityDesc() {
        return hairDensityDesc;
    }

    public void setHairDensityDesc(String hairDensityDesc) {
        this.hairDensityDesc = hairDensityDesc;
    }

    public String getHairDensityOrgUrl() {
        return hairDensityOrgUrl;
    }

    public void setHairDensityOrgUrl(String hairDensityOrgUrl) {
        this.hairDensityOrgUrl = hairDensityOrgUrl;
    }

    public String getHairDensityRstUrl() {
        return hairDensityRstUrl;
    }

    public void setHairDensityRstUrl(String hairDensityRstUrl) {
        this.hairDensityRstUrl = hairDensityRstUrl;
    }

    public Double getDeadSkinCellsRawValue() {
        return deadSkinCellsRawValue;
    }

    public void setDeadSkinCellsRawValue(Double deadSkinCellsRawValue) {
        this.deadSkinCellsRawValue = deadSkinCellsRawValue;
    }

    public Integer getDeadSkinCellsValue() {
        return deadSkinCellsValue;
    }

    public void setDeadSkinCellsValue(Integer deadSkinCellsValue) {
        this.deadSkinCellsValue = deadSkinCellsValue;
    }

    public Integer getDeadSkinCellsLevel() {
        return deadSkinCellsLevel;
    }

    public void setDeadSkinCellsLevel(Integer deadSkinCellsLevel) {
        this.deadSkinCellsLevel = deadSkinCellsLevel;
    }

    public String getDeadSkinCellsDesc() {
        return deadSkinCellsDesc;
    }

    public void setDeadSkinCellsDesc(String deadSkinCellsDesc) {
        this.deadSkinCellsDesc = deadSkinCellsDesc;
    }

    public String getDeadSkinCellsOrgUrl() {
        return deadSkinCellsOrgUrl;
    }

    public void setDeadSkinCellsOrgUrl(String deadSkinCellsOrgUrl) {
        this.deadSkinCellsOrgUrl = deadSkinCellsOrgUrl;
    }

    public String getDeadSkinCellsRstValue() {
        return deadSkinCellsRstValue;
    }

    public void setDeadSkinCellsRstValue(String deadSkinCellsRstValue) {
        this.deadSkinCellsRstValue = deadSkinCellsRstValue;
    }

    public Double getScalpImpuritiesRawValue() {
        return scalpImpuritiesRawValue;
    }

    public void setScalpImpuritiesRawValue(Double scalpImpuritiesRawValue) {
        this.scalpImpuritiesRawValue = scalpImpuritiesRawValue;
    }

    public Integer getScalpImpuritiesValue() {
        return scalpImpuritiesValue;
    }

    public void setScalpImpuritiesValue(Integer scalpImpuritiesValue) {
        this.scalpImpuritiesValue = scalpImpuritiesValue;
    }

    public Integer getScalpImpuritiesLevel() {
        return scalpImpuritiesLevel;
    }

    public void setScalpImpuritiesLevel(Integer scalpImpuritiesLevel) {
        this.scalpImpuritiesLevel = scalpImpuritiesLevel;
    }

    public String getScalpImpuritiesDesc() {
        return scalpImpuritiesDesc;
    }

    public void setScalpImpuritiesDesc(String scalpImpuritiesDesc) {
        this.scalpImpuritiesDesc = scalpImpuritiesDesc;
    }

    public String getScalpImpuritiesOrgUrl() {
        return scalpImpuritiesOrgUrl;
    }

    public void setScalpImpuritiesOrgUrl(String scalpImpuritiesOrgUrl) {
        this.scalpImpuritiesOrgUrl = scalpImpuritiesOrgUrl;
    }

    public String getScalpImpuritiesRstUrl() {
        return scalpImpuritiesRstUrl;
    }

    public void setScalpImpuritiesRstUrl(String scalpImpuritiesRstUrl) {
        this.scalpImpuritiesRstUrl = scalpImpuritiesRstUrl;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDetailsDto that = (CustomerDetailsDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(customerId, that.customerId) &&
                Objects.equals(batchId, that.batchId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(email, that.email) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(age, that.age) &&
                Objects.equals(birth, that.birth) &&
                Objects.equals(skinCondition, that.skinCondition) &&
                Objects.equals(address, that.address) &&
                Objects.equals(city, that.city) &&
                Objects.equals(state, that.state) &&
                Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(date, that.date) &&
                Objects.equals(time, that.time) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude) &&
                Objects.equals(temperature, that.temperature) &&
                Objects.equals(humidity, that.humidity) &&
                Objects.equals(uvIndex, that.uvIndex) &&
                Objects.equals(capturingZoneCode, that.capturingZoneCode) &&
                Objects.equals(scalpHydrationRawValue, that.scalpHydrationRawValue) &&
                Objects.equals(scalpHydrationValue, that.scalpHydrationValue) &&
                Objects.equals(scalpHydrationLevel, that.scalpHydrationLevel) &&
                Objects.equals(scalpHydrationDesc, that.scalpHydrationDesc) &&
                Objects.equals(scalpHydrationOrgUrl, that.scalpHydrationOrgUrl) &&
                Objects.equals(scalpHydrationRstUrl, that.scalpHydrationRstUrl) &&
                Objects.equals(scalpSebumRawValue, that.scalpSebumRawValue) &&
                Objects.equals(scalpSebumValue, that.scalpSebumValue) &&
                Objects.equals(scalpSebumLevel, that.scalpSebumLevel) &&
                Objects.equals(scalpSebumDesc, that.scalpSebumDesc) &&
                Objects.equals(scalpSebumOrgUrl, that.scalpSebumOrgUrl) &&
                Objects.equals(scalpSebumRstUrl, that.scalpSebumRstUrl) &&
                Objects.equals(hairDensityRawValue, that.hairDensityRawValue) &&
                Objects.equals(hairDensityValue, that.hairDensityValue) &&
                Objects.equals(hairDensityLevel, that.hairDensityLevel) &&
                Objects.equals(hairDensityDesc, that.hairDensityDesc) &&
                Objects.equals(hairDensityOrgUrl, that.hairDensityOrgUrl) &&
                Objects.equals(hairDensityRstUrl, that.hairDensityRstUrl) &&
                Objects.equals(deadSkinCellsRawValue, that.deadSkinCellsRawValue) &&
                Objects.equals(deadSkinCellsValue, that.deadSkinCellsValue) &&
                Objects.equals(deadSkinCellsLevel, that.deadSkinCellsLevel) &&
                Objects.equals(deadSkinCellsDesc, that.deadSkinCellsDesc) &&
                Objects.equals(deadSkinCellsOrgUrl, that.deadSkinCellsOrgUrl) &&
                Objects.equals(deadSkinCellsRstValue, that.deadSkinCellsRstValue) &&
                Objects.equals(scalpImpuritiesRawValue, that.scalpImpuritiesRawValue) &&
                Objects.equals(scalpImpuritiesValue, that.scalpImpuritiesValue) &&
                Objects.equals(scalpImpuritiesLevel, that.scalpImpuritiesLevel) &&
                Objects.equals(scalpImpuritiesDesc, that.scalpImpuritiesDesc) &&
                Objects.equals(scalpImpuritiesOrgUrl, that.scalpImpuritiesOrgUrl) &&
                Objects.equals(scalpImpuritiesRstUrl, that.scalpImpuritiesRstUrl) &&
                Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, batchId, name, surname, phone, email, gender, age, birth, skinCondition, address, city, state, countryCode, date, time, latitude, longitude, temperature, humidity, uvIndex, capturingZoneCode, scalpHydrationRawValue, scalpHydrationValue, scalpHydrationLevel, scalpHydrationDesc, scalpHydrationOrgUrl, scalpHydrationRstUrl, scalpSebumRawValue, scalpSebumValue, scalpSebumLevel, scalpSebumDesc, scalpSebumOrgUrl, scalpSebumRstUrl, hairDensityRawValue, hairDensityValue, hairDensityLevel, hairDensityDesc, hairDensityOrgUrl, hairDensityRstUrl, deadSkinCellsRawValue, deadSkinCellsValue, deadSkinCellsLevel, deadSkinCellsDesc, deadSkinCellsOrgUrl, deadSkinCellsRstValue, scalpImpuritiesRawValue, scalpImpuritiesValue, scalpImpuritiesLevel, scalpImpuritiesDesc, scalpImpuritiesOrgUrl, scalpImpuritiesRstUrl, comments);
    }

    @Override
    public String toString() {
        return "CustomerDetailsDto{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", batchId=" + batchId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", birth='" + birth + '\'' +
                ", skinCondition=" + skinCondition +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", uvIndex=" + uvIndex +
                ", capturingZoneCode=" + capturingZoneCode +
                ", scalpHydrationRawValue=" + scalpHydrationRawValue +
                ", scalpHydrationValue=" + scalpHydrationValue +
                ", scalpHydrationLevel=" + scalpHydrationLevel +
                ", scalpHydrationDesc='" + scalpHydrationDesc + '\'' +
                ", scalpHydrationOrgUrl='" + scalpHydrationOrgUrl + '\'' +
                ", scalpHydrationRstUrl='" + scalpHydrationRstUrl + '\'' +
                ", scalpSebumRawValue=" + scalpSebumRawValue +
                ", scalpSebumValue=" + scalpSebumValue +
                ", scalpSebumLevel=" + scalpSebumLevel +
                ", scalpSebumDesc='" + scalpSebumDesc + '\'' +
                ", scalpSebumOrgUrl='" + scalpSebumOrgUrl + '\'' +
                ", scalpSebumRstUrl='" + scalpSebumRstUrl + '\'' +
                ", hairDensityRawValue=" + hairDensityRawValue +
                ", hairDensityValue=" + hairDensityValue +
                ", hairDensityLevel=" + hairDensityLevel +
                ", hairDensityDesc='" + hairDensityDesc + '\'' +
                ", hairDensityOrgUrl='" + hairDensityOrgUrl + '\'' +
                ", hairDensityRstUrl='" + hairDensityRstUrl + '\'' +
                ", deadSkinCellsRawValue=" + deadSkinCellsRawValue +
                ", deadSkinCellsValue=" + deadSkinCellsValue +
                ", deadSkinCellsLevel=" + deadSkinCellsLevel +
                ", deadSkinCellsDesc=" + deadSkinCellsDesc +
                ", deadSkinCellsOrgUrl='" + deadSkinCellsOrgUrl + '\'' +
                ", deadSkinCellsRstValue='" + deadSkinCellsRstValue + '\'' +
                ", scalpImpuritiesRawValue=" + scalpImpuritiesRawValue +
                ", scalpImpuritiesValue=" + scalpImpuritiesValue +
                ", scalpImpuritiesLevel=" + scalpImpuritiesLevel +
                ", scalpImpuritiesDesc='" + scalpImpuritiesDesc + '\'' +
                ", scalpImpuritiesOrgUrl='" + scalpImpuritiesOrgUrl + '\'' +
                ", scalpImpuritiesRstUrl='" + scalpImpuritiesRstUrl + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }
}
