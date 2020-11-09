package com.negeso.module.webshop.dto;

import java.util.Objects;

public class ProductDto {

    private String orderCode;

    private String productCode;

    private String title;

    private String ean;

    private String articleGroup;

    private Double priceInc;

    private Double priceExc;

    private Integer stock;

    private Integer stockMin;

    private Boolean visible;

    private Double retailPriceExc;

    private String content;

    private String color;

    private String categoryName;

    private String brand;

    private Boolean keepStock;

    private Integer multipleOf;

    private Boolean sale;

    private String description;

    private Integer orderNumber;

    private String packingUnit;

    private Boolean isAddon;

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

    public Boolean getAddon() {
        return isAddon;
    }

    public void setAddon(Boolean addon) {
        isAddon = addon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDto that = (ProductDto) o;
        return Objects.equals(orderCode, that.orderCode) &&
                Objects.equals(productCode, that.productCode) &&
                Objects.equals(title, that.title) &&
                Objects.equals(ean, that.ean) &&
                Objects.equals(articleGroup, that.articleGroup) &&
                Objects.equals(priceInc, that.priceInc) &&
                Objects.equals(priceExc, that.priceExc) &&
                Objects.equals(stock, that.stock) &&
                Objects.equals(stockMin, that.stockMin) &&
                Objects.equals(visible, that.visible) &&
                Objects.equals(retailPriceExc, that.retailPriceExc) &&
                Objects.equals(content, that.content) &&
                Objects.equals(color, that.color) &&
                Objects.equals(categoryName, that.categoryName) &&
                Objects.equals(brand, that.brand) &&
                Objects.equals(keepStock, that.keepStock) &&
                Objects.equals(multipleOf, that.multipleOf) &&
                Objects.equals(sale, that.sale) &&
                Objects.equals(description, that.description) &&
                Objects.equals(orderNumber, that.orderNumber) &&
                Objects.equals(packingUnit, that.packingUnit) &&
                Objects.equals(isAddon, that.isAddon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderCode, productCode, title, ean, articleGroup, priceInc, priceExc, stock, stockMin, visible, retailPriceExc, content, color, categoryName, brand, keepStock, multipleOf, sale, description, orderNumber, packingUnit, isAddon);
    }
}
