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
    <xsl:template match="negeso:page[@class='webshop_order']" mode="page_head">
        <xsl:call-template name="webshop_order_page_head" />
    </xsl:template>

    <xsl:template name="webshop_order_page_head">
        <link rel="stylesheet" type="text/css" href="/site/modules/webshop_module/webshop_order/css/webshop_order.css?v=1"/>
        <script type="text/javascript" src="/site/modules/webshop_module/webshop_order/script/webshop_order.js?v=1" />
    </xsl:template>
    <!--Temlate for js and css end-->

    <xsl:template name="webshop_order">
        <xsl:choose>
            <xsl:when test="//negeso:orders/negeso:order/negeso:item">
                <h2 class="webshop_order_header">
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                        <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_ORDER_DATA'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_ORDER_DATA')"/>
                </h2>
                <div class="responsive-wrapper">
                    <div class="clearfix">
                        <xsl:call-template name="customer_orders_details_header" />
                    </div>
                    <div class="clearfix">
                        <xsl:call-template name="customer_orders_details" />
                    </div>
                </div>
                <a class="orders_details_back" href="order_nl.html">
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                        <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_BACK_TO_ORDER'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_BACK_TO_ORDER')"/>
                </a>
            </xsl:when>
            <xsl:otherwise>
                <h2 class="webshop_order_header">
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                        <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_ORDERS'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_ORDERS')"/>
                </h2>
                <div class="responsive-wrapper">
                    <table class="tablesorter">
                        <thead>
                            <tr>
                                <xsl:call-template name="customer_orders_header" />
                            </tr>
                        </thead>
                        <tbody>
                            <xsl:call-template name="customer_orders" />
                        </tbody>
                    </table>
                </div>
                <div class="pager">
                    <img src="/site/modules/webshop_module/webshop_order/images/couple-left.png" class="first"/>
                    <img src="/site/modules/webshop_module/webshop_order/images/left.png" class="prev"/>
                    <span class="pagedisplay" />
                    <img src="/site/modules/webshop_module/webshop_order/images/right.png" class="next"/>
                    <img src="/site/modules/webshop_module/webshop_order/images/couple-right.png" class="last"/>
                    <span class="select">
                        <select class="pagesize" title="Selecteer pagina grootte">
                            <option selected="selected" value="10">10</option>
                            <option value="20">20</option>
                            <option value="30">30</option>
                            <option value="40">40</option>
                        </select>
                    </span>
                    <span class="select">
                        <select class="gotoPage" title="Selecteer paginanummer" />
                    </span>
                </div>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="customer_orders_header">
        <th class="customer_orders_header">Type levering</th>
        <th class="customer_orders_header">Betaalsoort</th>
        <th class="customer_orders_header">Datum</th>
        <th class="customer_orders_header">Totaalbedrag</th>
        <th class="customer_orders_header">Bestellingsnummer</th>
        <th class="customer_orders_header">Items</th>
        <th class="customer_orders_header">Orderstatus</th>
        <th class="customer_orders_header">Gegevens</th>
    </xsl:template>

    <xsl:template name="customer_orders">
        <xsl:for-each select="//negeso:orders/negeso:order">
            <tr class="customer_orders_wrapper">
                <td class="customer_orders">
                    <xsl:value-of select="@deliveryType"/>
                </td>
                <td class="customer_orders">
                    <xsl:choose>
                        <xsl:when test="@paymentMethod = 'CASH'">
                            <img alt="Cash" src="/site/modules/webshop_module/webshop_order/images/cash-icon.png"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <img alt="Ideal" src="site/modules/webshop_module/webshop_order/images/ideal-icon.png"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
                <td class="customer_orders">
                    <xsl:value-of select="@orderDate"/>
                    <!--<xsl:value-of select="(substring(@orderDate, 1, string-length(@orderDate) - 6))"/>-->
                </td>
                <td class="customer_orders">
                    <xsl:value-of select="@price"/>
                </td>
                <td class="customer_orders">
                    <xsl:value-of select="@transactionId"/>
                </td>
                <td class="customer_orders">
                    <xsl:value-of select="@quantity" />
                </td>
                <td class="customer_orders">
                    <xsl:choose>
                        <xsl:when test="@status = 'PAYED'">
                            FACTUUR
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="@status" />
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
                <td class="customer_orders detail_button">
                    <a href="?id={@id}">Gegevens</a>
                </td>
            </tr>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="customer_orders_details_header">
        <div class="customer_orders_details_header customer_orders_details_header_first">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_PRODUCT_NAME'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRODUCT_NAME')"/>
        </div>
        <div class="customer_orders_details_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_QUANITY'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_QUANITY')"/>
        </div>
        <div class="customer_orders_details_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_PRODUCT_PRICE'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRODUCT_PRICE')"/>
        </div>
        <div class="customer_orders_details_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_PRICE_DISCOUNT'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_PRICE_DISCOUNT')"/>
        </div>
        <div class="customer_orders_details_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_EANCODE'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_EANCODE')"/>
        </div>
        <div class="customer_orders_details_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_ORDER_CODE'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_ORDER_CODE')"/>
        </div>
        <div class="customer_orders_details_header">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_MERK'"/>
            </xsl:call-template>
            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_MERK')"/>
        </div>
    </xsl:template>

    <xsl:template name="customer_orders_details">
        <xsl:for-each select="//negeso:orders/negeso:order/negeso:item">
            <div class="clearfix customer_orders_details_wrapper">
                <div class="customer_orders_details customer_orders_details_header_first">
                    <img alt="" class="img" onerror="this.src='/media/cap.jpg'">
                        <xsl:attribute name="src">/media/productsImages/<xsl:value-of select="product/@id"/>.jpg</xsl:attribute>
                    </img>
                    <a>
                        <xsl:attribute name="href">webshop_nl/<xsl:value-of select="@url"/></xsl:attribute>
                        <xsl:value-of select="product/@title"/>
                    </a>
                </div>
                <div class="customer_orders_details">
                    <xsl:value-of select="@quantity"/>
                </div>
                <div class="customer_orders_details">
                    <xsl:value-of select="@price"/>
                </div>
                <div class="customer_orders_details">
                    <xsl:choose>
                        <xsl:when test="@discount">
                            <xsl:value-of select="@discount"/>
                        </xsl:when>
                        <xsl:otherwise>
                            0%
                        </xsl:otherwise>
                    </xsl:choose>

                </div>
                <div class="customer_orders_details">
                    <xsl:value-of select="product/@ean"/>
                </div>
                <div class="customer_orders_details">
                    <xsl:value-of select="product/@id"/>
                </div>
                <div class="customer_orders_details">
                    <xsl:value-of select="product/@brand"/>
                </div>
            </div>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>