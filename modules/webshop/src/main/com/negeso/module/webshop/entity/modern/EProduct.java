package com.negeso.module.webshop.entity.modern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ws_products")
public class EProduct implements Serializable {

    @Id
    @Column(name = "order_code", nullable = false)
    private String orderCode;

    @Column(name = "product_number")
    private String productCode;

    @Column(name = "title")
    private String title;

    @Column(name = "ean")
    private String ean;

    @Column(name = "article_group")
    private String articleGroup;

    @Column(name = "price_inc")
    private Double priceInc;

    @Column(name = "price_exc")
    private Double priceExc;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "stock_min")
    private Integer stockMin;

    @Column(name = "visible")
    private Boolean visible;

    @Column(name = "retail_price_exc")
    private Double retailPriceExc;

    @Column(name = "content")
    private String content;

    @Column(name = "color")
    private String color;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "brand")
    private String brand;

    @Column(name = "keep_stock")
    private Boolean keepStock;

    @Column(name = "multiple_of")
    private Integer multipleOf;

    @Column(name = "sale")
    private Boolean sale;

    @Column(name = "description")
    private String description;

    @Column(name = "order_number")
    private Integer orderNumber;

    @Column(name = "packing_unit")
    private String packingUnit;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getArticleGroup() {
        return articleGroup;
    }

    public void setArticleGroup(String articleGroup) {
        this.articleGroup = articleGroup;
    }

    public Double getPriceInc() {
        return priceInc;
    }

    public void setPriceInc(Double priceInc) {
        this.priceInc = priceInc;
    }

    public Double getPriceExc() {
        return priceExc;
    }

    public void setPriceExc(Double priceExc) {
        this.priceExc = priceExc;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMin() {
        return stockMin;
    }

    public void setStockMin(Integer stockMin) {
        this.stockMin = stockMin;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Double getRetailPriceExc() {
        return retailPriceExc;
    }

    public void setRetailPriceExc(Double retailPriceExc) {
        this.retailPriceExc = retailPriceExc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Boolean getKeepStock() {
        return keepStock;
    }

    public void setKeepStock(Boolean keepStock) {
        this.keepStock = keepStock;
    }

    public Integer getMultipleOf() {
        return multipleOf;
    }

    public void setMultipleOf(Integer multipleOf) {
        this.multipleOf = multipleOf;
    }

    public Boolean getSale() {
        return sale;
    }

    public void setSale(Boolean sale) {
        this.sale = sale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPackingUnit() {
        return packingUnit;
    }

    public void setPackingUnit(String packingUnit) {
        this.packingUnit = packingUnit;
    }

}
