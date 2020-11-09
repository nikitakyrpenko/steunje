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
    <xsl:template match="negeso:page[@class='webshop_checkout']" mode="page_head">
        <script>
            var pricesPure = {
            orderPrice: '<xsl:value-of select="//negeso:cart/negeso:products/@orderPrice"/>' ,
            deliveryPrice: '<xsl:value-of select="//negeso:cart/negeso:products/@deliveryPrice"/>',
            vat : '<xsl:value-of select="//negeso:cart/negeso:products/@VAT"/>',
            total: '<xsl:value-of select="//negeso:cart/negeso:products/@total"/>'
            }
            var pricesDiscount = {
            orderPrice: '<xsl:value-of select="//negeso:cart/negeso:products/@orderPriceWithDiscount"/>',
            deliveryPrice: '<xsl:value-of select="//negeso:cart/negeso:products/@deliveryPrice"/>',
            vat: '<xsl:value-of select="//negeso:cart/negeso:products/@idealDiscountWat"/>',
            total: '<xsl:value-of select="//negeso:cart/negeso:products/@totalPriceWithDiscount"/>',
            }
        </script>
        <xsl:call-template name="webshop_checkout_page_head" />
    </xsl:template>

    <xsl:template name="webshop_checkout_page_head">
        <link rel="stylesheet" type="text/css" href="/site/modules/webshop_module/webshop_checkout/css/webshop_checkout.css?v=9"/>
        <script type="text/javascript" src="/site/modules/webshop_module/webshop_checkout/script/webshop_checkout.js?v=16" />
    </xsl:template>
    <!--Temlate for js and css end-->

    <xsl:template name="webshop_checkout">
        <div class="webshop_checkout_wrapper  clearfix">
            <div class="responsive-wrapper">
                <div class="clearfix checkout_header_wrapper">
                    <xsl:call-template name="webshop_checkout_header" />
                </div>
                <xsl:call-template name="webshop_checkout_items" />
            </div>
            <xsl:call-template name="webshop_checkout_total_delivery" />
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_DISCOUNF_INFO'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_DISCOUNF_INFO')" disable-output-escaping="yes"/>
            <xsl:call-template name="webshop_checkout_form" />
        </div>
    </xsl:template>

    <xsl:template name="webshop_checkout_header">
        <div class="checkout_header checkout_header_first">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_PRODUCT_NAME'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRODUCT_NAME')"/>
        </div>
        <div class="checkout_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_PRODUCT_PRICE_NETTO'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRODUCT_PRICE_NETTO')"/>
        </div>
        <div class="checkout_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_PRODUCT_PRICE_BRUTTO'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRODUCT_PRICE_BRUTTO')"/>
        </div>
        <div class="checkout_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_HOEVEELHEID'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_HOEVEELHEID')"/>
        </div>
        <div class="checkout_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_ARTICLE_NUMBER'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_ARTICLE_NUMBER')"/>
        </div>
        <div class="checkout_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_PRICE_DISCOUNT'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRICE_DISCOUNT')"/>
        </div>
        <div class="checkout_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_FULL_PRICE_WITHOUT_VAT'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_FULL_PRICE_WITHOUT_VAT')"/>
        </div>
        <div class="checkout_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_TOTAL_PRICE_WITHOUT_VAT'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_TOTAL_PRICE_WITHOUT_VAT')"/>
        </div>
    </xsl:template>

    <xsl:template name="webshop_checkout_items">
        <xsl:for-each select="//negeso:checkout/negeso:cart/negeso:products/negeso:product">
            <div class="checkout_items_wrapper clearfix">
                <div class="checkout_items checkout_items_first">
                    <img alt="" class="img" onerror="this.src='../media/cap.jpg'" >
                        <xsl:attribute name="src">/media/productsImages/<xsl:value-of select="@id"/>.jpg</xsl:attribute>
                    </img>
                    <span class="title">
                        <xsl:value-of select="@title"/>
                    </span>
                </div>
                <div class="checkout_items price">
                    <xsl:value-of select="@priceAfterDiscount"/>
                </div>
                <div class="checkout_items price">
                    <xsl:value-of select="@priceExcludeVat"/>
                </div>
                <div class="checkout_items">
                    <input id="js_counter" class="quantity" readonly="" name="product_amount" type="text">
                        <xsl:attribute name="value"><xsl:value-of select="@quantity"/></xsl:attribute>
                    </input>
                </div>
                <div class="checkout_items">
                    <span>
                        <xsl:value-of select="@id"/>
                    </span>
                </div>
                <div class="checkout_items">
                    <xsl:choose>
                        <xsl:when test="@discount">
                            <xsl:value-of select="@discount"/>
                        </xsl:when>
                        <xsl:otherwise>
                            0%
                        </xsl:otherwise>
                    </xsl:choose>
                </div>
                <div class="checkout_items">
                    <xsl:value-of select="@itemsPriceExc"/>
                </div>
                <div class="checkout_items">
                    <xsl:value-of select="@itemsPrice"/>
                </div>
                <!--<div class="checkout_items">-->
                <!--<button class="delete" type="button">x</button>-->
                <!--</div>-->
            </div>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="webshop_checkout_total_delivery">
        <div class="total_delivery">
            <h6>
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_TOTAL_DELIVERY'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_TOTAL_DELIVERY')"/>
            </h6>
            <p>
                <span>
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                        <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_SUBTOTAL_DELIVERY'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_SUBTOTAL_DELIVERY')"/>
                </span>
                <span class="right_block" id="orderPrice">
                    <xsl:value-of select="//negeso:cart/negeso:products/@orderPrice"/>
                </span>
            </p>
            <p>
                <span>
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                        <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_DELIVERY'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_DELIVERY')"/>
                </span>
                <span class="right_block" id="deliveryPrice">
                    <xsl:value-of select="//negeso:cart/negeso:products/@deliveryPrice"/>
                </span>
            </p>
            <p class="line_bottom">
                <span>
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                        <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_TOTAL_VAT'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_TOTAL_VAT')"/>
                </span>
                <span class="right_block" id="totalVat">
                    <xsl:value-of select="//negeso:cart/negeso:products/@VAT"/>
                </span>
            </p>
            <p>
                <span class="total_title">
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                        <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_TOTAL_WITH_VAT'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_TOTAL_WITH_VAT')"/>
                </span>
                <span id="total" class="right_block total_title">
                    <xsl:value-of select="//negeso:cart/negeso:products/@total"/>
                </span>
            </p>
        </div>
    </xsl:template>

    <xsl:template name="webshop_checkout_form">
        <form action="/order_nl.html" method="POST" name="checkout_form"  id="checkout_form_js" class="checkout_form">
            <h6>01.
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER'"/>
                </xsl:call-template>
                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER')"/>
            </h6>
            <p>
                <span class="title">
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                        <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_EMAIL'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_EMAIL')"/> <span>*</span> </span>
                <input name="email" is_required="true" is_email="true"  class="checkout_inputs">
                    <xsl:attribute name="value"><xsl:value-of select="//negeso:cart/@customer-email" /></xsl:attribute>
                </input>
            </p>
            <div class="clearfix">
                <div class="checkout_form_left">
                    <h6>
                        <xsl:call-template name ="add-constant-info">
                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_INVESTMENT_ADDRESS'"/>
                        </xsl:call-template>
                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_INVESTMENT_ADDRESS')"/>
                    </h6>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_FIRST_NAME'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_FIRST_NAME')"/> <span>*</span></span>
                        <input type="text" readonly="readonly" name="billing_firstName" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='billing']/@firstName" /></xsl:attribute>
                        </input>
                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_LAST_NAME'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_LAST_NAME')"/> <span>*</span></span>
                        <input type="text" readonly="readonly" name="billing_secondName" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='billing']/@secondName" /></xsl:attribute>
                        </input>
                    </p>
                    <!--<p>-->
                    <!--<span class="title">-->
                    <!--<xsl:call-template name ="add-constant-info">-->
                    <!--<xsl:with-param name ="dict"  select="$dict_webshop_module"/>-->
                    <!--<xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_NAME'"/>-->
                    <!--</xsl:call-template>-->
                    <!--<xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_NAME')"/> <span>*</span></span>-->
                    <!--<input type="text" is_required="true" name="billing_companyName" class="checkout_inputs">-->
                    <!--<xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='billing']/@companyName" /></xsl:attribute>-->
                    <!--</input>-->
                    <!--</p>-->
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_ADDRES'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_ADDRES')"/> <span>*</span></span>
                        <input type="text" readonly="readonly" name="billing_addressLine" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='billing']/@addressLine" /></xsl:attribute>
                        </input>
                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_ZIP_CODE'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_ZIP_CODE')"/> <span>*</span></span>
                        <input type="text" readonly="readonly" name="billing_zipCode" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='billing']/@zipCode" /></xsl:attribute>
                        </input>
                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_PLACE'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_PLACE')"/> <span>*</span></span>
                        <input type="text" readonly="readonly" name="billing_city" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='billing']/@city" /></xsl:attribute>
                        </input>
                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_LAND'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_LAND')"/> <span>*</span></span>
                        <input type="text" readonly="readonly" name="billing_country" class="checkout_inputs" id="billing_countryId">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='billing']/@country" /></xsl:attribute>
                        </input>
                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_PHONE'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_PHONE')"/> </span>
                        <input type="text" readonly="readonly" name="billing_phone" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='billing']/@phone" /></xsl:attribute>
                        </input>
                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_FAX'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_FAX')"/> </span>
                        <input type="text" name="billing_fax" readonly="readonly" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='billing']/@fax" /></xsl:attribute>
                        </input>
                    </p>
                </div>
                <div class="checkout_form_right">
                    <h6>
                        <xsl:call-template name ="add-constant-info">
                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_DELIVERED'"/>
                        </xsl:call-template>
                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_DELIVERED')"/>
                    </h6>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_FIRST_NAME'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_FIRST_NAME')"/> <span>*</span></span>
                        <input type="text" is_required="true"  name="shipping_firstName" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='shipping']/@firstName" /></xsl:attribute>
                        </input>
                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_LAST_NAME'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_LAST_NAME')"/> <span>*</span></span>
                        <input type="text" is_required="true"  name="shipping_secondName" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='shipping']/@secondName" /></xsl:attribute>
                        </input>
                    </p>
                    <!--<p>-->
                    <!--<span class="title">-->
                    <!--<xsl:call-template name ="add-constant-info">-->
                    <!--<xsl:with-param name ="dict"  select="$dict_webshop_module"/>-->
                    <!--<xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_NAME'"/>-->
                    <!--</xsl:call-template>-->
                    <!--<xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_NAME')"/> <span>*</span></span>-->
                    <!--<input type="text" is_required="true"  name="shipping_companyName" class="checkout_inputs">-->
                    <!--<xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='shipping']/@companyName" /></xsl:attribute>-->
                    <!--</input>-->
                    <!--</p>-->
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_ADDRES'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_ADDRES')"/> <span>*</span></span>
                        <input type="text" is_required="true"  name="shipping_addressLine" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='shipping']/@addressLine" /></xsl:attribute>
                        </input>
                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_ZIP_CODE'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_ZIP_CODE')"/> <span>*</span></span>
                        <input type="text" is_required="true"  name="shipping_zipCode" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='shipping']/@zipCode" /></xsl:attribute>
                        </input>
                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_PLACE'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_PLACE')"/> <span>*</span></span>
                        <input type="text" is_required="true"  name="shipping_city" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='shipping']/@city" /></xsl:attribute>
                        </input>
                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_LAND'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_LAND')"/> <span>*</span></span>

                        <input id="countries" type="text" is_required="true"  name="shipping_country" class="checkout_inputs" >
                            <!--<div class="autocomplete" style="width:300px;">-->
                            <!--<input id="myInput" type="text" name="myCountry" placeholder="Country">-->
                            <!--</div>-->
                            <xsl:if test="//negeso:contact[@type='billing']/@country != ''">
                                <xsl:attribute name="readonly">""</xsl:attribute>
                            </xsl:if>

                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='billing']/@country" /></xsl:attribute>
                        </input>

                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_PHONE'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_PHONE')"/></span>
                        <input type="text" name="shipping_phone" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='shipping']/@phone" /></xsl:attribute>
                        </input>
                    </p>
                    <p>
                        <span class="title">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_FAX'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_COMPANY_FAX')"/> </span>
                        <input type="text" name="shipping_fax" class="checkout_inputs">
                            <xsl:attribute name="value"><xsl:value-of select="//negeso:contact[@type='shipping']/@fax" /></xsl:attribute>
                        </input>
                    </p>
                </div>
                <div class="input-checkbox">
                    <input value="factuuradres_afleveradres" name="factuuradres_afleveradres" id="factuuradres_afleveradres" type="checkbox" />
                    <label for="factuuradres_afleveradres">
                        <xsl:call-template name ="add-constant-info">
                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_DELIVERED_BILLING_ADDRESS'"/>
                        </xsl:call-template>
                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_DELIVERED_BILLING_ADDRESS')"/>
                    </label>
                </div>
            </div>
            <p class="type_delivery">
                <ul class="type_delivery_list">
                    <li class="delivery_item">
                        <input value="DDU" name="delivery_type" id="bezorgFactuuradres" type="radio" checked="checked"/>
                        <label for="bezorgFactuuradres">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_DELIVERY_TO_ADDRESS'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_DELIVERY_TO_ADDRESS')"/>
                        </label>
                        <div class="check" />
                    </li>
                    <li class="delivery_item ">
                        <input value="EXW" name="delivery_type" id="afhalen" type="radio" />
                        <label for="afhalen">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_DELIVERY_PICK_UP'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_DELIVERY_PICK_UP')"/>
                        </label>
                        <div class="check">
                            <div class="inside" />
                        </div>
                    </li>
                </ul>
            </p>
            <div class="comment_block">
                <h6>
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                        <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CUSTOMER_ADDITION_NOTE'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CUSTOMER_ADDITION_NOTE')"/>
                </h6>
                <textarea rows="4" cols="50" class="textarea" id="js_comment_block" name="comment" />
            </div>
            <div class="type_payment">
                <ul class="type_delivery_list">
                    <li class="delivery_item delivery_item_paypal">
                        <input type="radio" name="payment_method" class="ideal-paypal" id="PayPal" value="paypal"/>
                        <label for="PayPal">
                            PayPal
                        </label>
                        <div class="check" />
                    </li>
                    <xsl:if test="//negeso:customer/@postPayAllowed = 'true'">
                        <li class="delivery_item delivery_item_cash">
                            <input type="radio" name="payment_method" id="opRekening" value="CASH" checked="checked"/>
                            <label for="opRekening">
                                <xsl:call-template name ="add-constant-info">
                                    <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                    <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CASH_PAYMENT'"/>
                                </xsl:call-template>
                                <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CASH_PAYMENT')"/>
                            </label>
                            <div class="check">
                                <div class="inside" />
                            </div>
                        </li>
                    </xsl:if>
                    <li class="delivery_item delivery_item_ideal">
                        <input type="radio" name="payment_method" class="ideal-paypal" id="Ideal" value="IDEAL"/>
                        <label for="Ideal">
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_IDEAL_PAYMENT'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_IDEAL_PAYMENT')"/>
                        </label>
                        <div class="check" />
                    </li>
                </ul>
                <div id="ideal-options" style="display: none">
                    <span class="select">
                        <select name="issuer_id">
                            <option>1</option>
                        </select>
                    </span>
                </div>
            </div>
            <div class="forms_button">
                <a href="webshop_cart_nl.html">
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                        <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_BACK_BUTTON'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_BACK_BUTTON')"/>
                </a>
                <button id="form_submit_button_js"  type="submit">
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                        <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CONTINUE_BUTTON'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CONTINUE_BUTTON')"/>
                </button>
            </div>
        </form>
    </xsl:template>

</xsl:stylesheet>