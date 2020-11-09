/*
 * @(#)SearchEntry.java  Created on 11.05.2004
 *
 * Copyright (c) 2004 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.module.search;

/**
 * Search entry contains URI, language code (2-letters), title (to be
 * displayed in search results) and contents (text to be indexed and searched)
 * to be used by SearchProducer.
 *
 * @author Stanislav Demchenko
 * @version 1.0
 */

public class SearchEntry {


    private String contents;
    private String title;
    private String langCode;
    private String uri;
    private long date;
    private Long containerId;
    private String imagePreviewUri;
    private Integer multipleOf;
    private String productNumber;
    private String orderCode;
    private String categoryName;


    public SearchEntry(String categoryName, String uri, String langCode, String title, String contents, Integer multipleOf, String productNumber, String orderCode) {
        this(categoryName, uri, langCode, title, contents, null, multipleOf, productNumber, orderCode);
    }

    public SearchEntry(String categoryName, String uri, String langCode, String title, String contents, String imagePreviewUri, Integer multipleOf, String productNumber, String orderCode) {
        this.categoryName = categoryName;
        this.contents = contents;
        this.title = title;
        this.langCode = langCode;
        this.uri = uri;
        this.imagePreviewUri = imagePreviewUri;
        this.multipleOf = multipleOf;
        this.productNumber = productNumber;
        this.orderCode = orderCode;
    }

    /**
     * @return category name of product
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Order core of product.
     *
     * @return
     */
    public String getOrderCode() {
        return orderCode;
    }

    /**
     * Unique product number
     */
    public String getProductNumber() {
        return productNumber;
    }

    /**
     * The minimum count of product can buy
     */
    public Integer getMultipleOf() {
        return multipleOf;
    }

    /**
     * Relative URI to access the page containing the text through the Web
     */
    public String getUri() {
        return uri;
    }


    /**
     * Displayed in list of search results
     */
    public String getTitle() {
        return title;
    }


    /**
     * Text that is indexed and searched. May contain markup
     */
    public String getContents() {
        return contents;
    }


    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setMultipleOf(Integer multipleOf) {
        this.multipleOf = multipleOf;
    }

    public void setContents(String string) {
        contents = string;
    }


    public void setLangCode(String string) {
        langCode = string;
    }


    public void setTitle(String string) {
        title = string;
    }


    public void setUri(String string) {
        uri = string;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String toString() {
        return
                "Search entry(" + this.getLangCode() + "): " +
                        " title:" + this.getTitle() +
                        " uri:" + this.getUri() +
                        " contents:" + this.getContents() +
                        " image uri:" + this.getUri()
                ;

    }


    /**
     * Returns modification date of the entry
     */
    public long getDate() {
        return date;
    }


    /**
     * Set modification date of the entry
     */
    public void setDate(long date) {
        this.date = date;
    }


    /**
     * Returns container of the entry or null if the entry is not protected
     */
    public Long getContainerId() {
        return containerId;
    }


    /**
     * Set container of the entry (null means the entry is not protected)
     */
    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    public String getImagePreviewUri() {
        return imagePreviewUri;
    }

    public void setImagePreviewUri(String imagePreviewUri) {
        this.imagePreviewUri = imagePreviewUri;
    }
}
