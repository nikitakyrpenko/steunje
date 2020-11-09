<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential
  Information and shall use it only inf accordance with the terms of the
  license agreement you entered into with Negeso.


  XSL templates for Product Module

  Main file.

  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:negeso="http://negeso.com/2003/Framework" xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">

    <xsl:variable name="dict_webshop_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('webshop', $lang)"/>

    <!--Temlate for js and css start-->
    <xsl:template match="negeso:page[@class='webshop_cart']" mode="page_head">
        <xsl:call-template name="webshop_cart_page_head" />
    </xsl:template>

    <xsl:template name="webshop_cart_page_head">
        <link rel="stylesheet" type="text/css" href="/site/modules/webshop_module/webshop_cart/css/webshop_cart.css?v=1"/>
        <script type="text/javascript" src="/site/modules/webshop_module/webshop_cart/script/webshop_cart.js?v=4" />
    </xsl:template>
    <!--Temlate for js and css end-->

    <xsl:template name="webshop_cart">
        <div class="webshop_cart_wrapper responsive-wrapper clearfix">
            <div class="clearfix cart_header_wrapper">
                <xsl:call-template name="webshop_cart_header" />
            </div>
            <xsl:call-template name="webshop_cart_items" />
        </div>
        <xsl:call-template name="webshop_cart_footer" />
    </xsl:template>

    <xsl:template name="webshop_cart_header">
        <div class="cart_header cart_header_first">
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_PRODUCT_NAME'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRODUCT_NAME')"/>
            </div>
        <div class="cart_header">
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_PRODUCT_PRICE_NETTO'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRODUCT_PRICE_NETTO')"/>
        </div>
        <div class="cart_header">
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_PRODUCT_PRICE_BRUTTO'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRODUCT_PRICE_BRUTTO')"/>
        </div>
        <div class="cart_header">
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_HOEVEELHEID'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_HOEVEELHEID')"/>
            </div>
        <div class="cart_header">
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_ARTICLE_NUMBER'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_ARTICLE_NUMBER')"/>
            </div>
        <div class="cart_header">
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_TOTAL'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_TOTAL')"/>
            </div>
        <div class="cart_header">
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_DELETE'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_DELETE')"/>
            </div>
    </xsl:template>

    <xsl:template name="webshop_cart_items">
        <xsl:for-each select="//negeso:cart/negeso:products/negeso:product">
            <xsl:variable name="deleteCurrency"  >
                <xsl:value-of select="(substring(@priceAfterDiscount, 2))"/>
            </xsl:variable>
            <xsl:variable name="deleteComma"  >
                <xsl:value-of select="translate($deleteCurrency, ',', '.')"/>
            </xsl:variable>
            <xsl:variable name="multiplicationItems"  >
                <xsl:value-of select="$deleteComma * @quantity"/>
            </xsl:variable>
            <xsl:variable name="formatNumber"  >
                <xsl:value-of select = "format-number($multiplicationItems,'#.00')" />
            </xsl:variable>
            <xsl:variable name="addComma"  >
                <xsl:value-of select="translate($formatNumber, '.', ',')"/>
            </xsl:variable>
            <div class="cart_items_wrapper clearfix">
                <div class="cart_items cart_items_first">
                    <img alt="" class="img" onerror="this.src='../media/cap.jpg'" >
                        <xsl:attribute name="src">/media/productsImages/<xsl:value-of select="@id"/>.jpg</xsl:attribute>
                    </img>
                    <span class="title">
                        <xsl:value-of select="@title"/>
                    </span>
                    <br/><br/>
                    <xsl:if test="not(/negeso:page/@role-id = 'guest')">
                        <img alt="">
                            <xsl:attribute name="src">/site/modules/webshop_module/images/stock_<xsl:value-of select="@stockColor"/>.png</xsl:attribute>
                        </img>
                    </xsl:if>
                </div>
                <div class="cart_items price">
                    <xsl:value-of select="@priceAfterDiscount"/>
                </div>
                <div class="cart_items price">
                    <xsl:value-of select="@priceExcludeVat"/>
                </div>
                <div class="cart_items">
                    <button class="js_minus" />
                    <input id="js_counter" class="quantity" name="product_amount" type="text">
                        <xsl:attribute name="value"><xsl:value-of select="@quantity"/></xsl:attribute>
                    </input>
                    <span style="display: none" class="multipleOf">
                        <xsl:choose>
                            <xsl:when test="@multipleOf = '0' or not(@multipleOf)" >
                                <xsl:value-of select="1"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="@multipleOf"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </span>
                    <button class="js_plus" />
                </div>
                <div class="cart_items">
                    <span class="orderCode">
                        <xsl:value-of select="@id"/>
                    </span>
                </div>
                <div class="cart_items cart_items-total">
                    &#8364; <xsl:value-of select="$addComma"/>
                </div>
                <div class="cart_items">
                    <button class="delete_product_js delete" type="button">x</button>
                </div>
            </div>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="webshop_cart_footer">
        <div class="webshop_cart_footer">
            <a href="webshop_nl.html" class="clear_cart">

                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_NEXT'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_NEXT')"/>

            </a>
            <a class="checkout_page" href="webshop_checkout_nl.html">
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CHECKOUT'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CHECKOUT')"/>

            </a>
        </div>
    </xsl:template>
</xsl:stylesheet>