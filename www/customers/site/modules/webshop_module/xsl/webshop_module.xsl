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
    <xsl:template match="negeso:webshop" mode="page_head">
        <xsl:call-template name="webshop_page_head" />
    </xsl:template>

    <xsl:template name="webshop_page_head">
        <link rel="stylesheet" type="text/css" href="/site/modules/webshop_module/css/webshop_module.css?v=11"/>
        <script type="text/javascript" src="/site/modules/webshop_module/script/webshop_module.js?v=10" />
    </xsl:template>


    <xsl:template name="webshop">
        <div class="webshop-category-wrapper wrapper">
            <div class="clearfix">
                <xsl:choose>
                    <xsl:when test="//negeso:webshop/negeso:product">
                        <div class="webshop-products-details-wrap">
                            <xsl:call-template name="webshop-products-details" />
                       <!--     <xsl:if test="/negeso:page/@role-id = 'guest'">
                                <a class="buy">
                                    <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                                    <span class="cart">Bestel nu</span>
                                </a>
                            </xsl:if>-->
                        </div>
                    </xsl:when>
                    <xsl:when test="//negeso:category[@selected='true']/negeso:category/negeso:matrix">
                        <div class="webshop-products-view-wrapper">
                            <xsl:call-template name="webshop-breadcrumps" />

                            <xsl:call-template name="webshop-products-view-no-matrix" />
                            <div class="matrix-wrapper">
                                <xsl:call-template name="webshop-products-view-with-matrix" />
                            </div>
                        </div>
                    </xsl:when>
                    <xsl:when test="//negeso:category[@selected='true']/negeso:category">
                        <div class="webshop-products-view-wrapper">
                            <xsl:call-template name="webshop-breadcrumps" />
                            <div>
                                <xsl:call-template name="webshop_category-list-leve3" />
                            </div>
                            <xsl:if test="//negeso:category[@selected='true']/negeso:matrix">
                                <div class="clearfix" style="float: left;">
                                    <div></div>
                                    <xsl:call-template name="webshop-products-view-no-matrix" />
                                </div>
                                <div class="matrix-wrapper">
                                    <xsl:call-template name="webshop-products-view-with-matrix" />
                                </div>
                            </xsl:if>
                        </div>
                    </xsl:when>
                    <xsl:when test="//negeso:category[@selected='true']/negeso:matrix">
                        <h3 class="webshop-products-heading">Onze producten</h3>
                        <div class="webshop-products-view-wrapper">
                            <xsl:call-template name="webshop-products-view-no-matrix" />
                            <div class="matrix-wrapper">
                                <xsl:call-template name="webshop-products-view-with-matrix" />
                            </div>
                        </div>
                    </xsl:when>
                    <xsl:when test="//negeso:webshop/negeso:categories/negeso:category">
                        <div class="webshop-products-view-wrapper">
                         <!--   <xsl:call-template name="header_and_serch_block" />
                            <xsl:call-template name="webshop-breadcrumps" />-->
                            <div class="clearfix webshop_category-list-leve2">
                                <xsl:call-template name="webshop_category-list-leve2" />
                            </div>
                            <div class="clearfix">
                                <div></div>
                                <xsl:call-template name="webshop-products-view-no-matrix" />
                            </div>
                            <div class="matrix-wrapper">
                                <xsl:call-template name="webshop-products-view-with-matrix" />
                            </div>
                        </div>
                    </xsl:when>
                    <xsl:otherwise>
                        <div class="webshop-products-view-wrapper">
                            <!--<xsl:call-template name="header_and_serch_block" />-->
                            <div class="webshop_category-list-leve1 clearfix">
                                <xsl:call-template name="webshop_category-list-leve1" />
                            </div>
                        </div>
                    </xsl:otherwise>
                </xsl:choose>
            </div>
        </div>
    </xsl:template>

    <!--Template for category Level 1-->
    <xsl:template name="webshop_category-list-leve1">
        <xsl:for-each select="//negeso:webshop/negeso:category">
            <div class="ws-category-list">
                <a>
                    <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                    <img alt="" class="img" onerror="imgError(this)">
                        <xsl:attribute name="src">/media/productsCategory/WL_<xsl:value-of select="translate(@title, ' ABCDEFGHIJKLMNOPQRSTUVWXYZ', '_abcdefghijklmnopqrstuvwxyz')"/>.png</xsl:attribute>
                    </img>
                </a>
                <p class="name">
                    <a class="link">
                        <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                        <xsl:value-of select="@title"/>
                    </a>
                </p>
            </div>
        </xsl:for-each>
    </xsl:template>
    <!--Template for category Level 2-->
    <xsl:template name="webshop_category-list-leve2">
        <xsl:for-each select="//negeso:webshop/negeso:categories/negeso:category">
            <div class="ws-category-list">
                <a>
                    <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                    <img alt="" onerror="imgError(this)" class="img">
                        <xsl:attribute name="src">/media/productsCategory/<xsl:value-of select="translate(//negeso:webshop/negeso:categories/@title, ' ', '_')"/>_<xsl:value-of select="translate(@title, ' ABCDEFGHIJKLMNOPQRSTUVWXYZ', '_abcdefghijklmnopqrstuvwxyz')"/>.png</xsl:attribute>
                    </img>
                </a>
                <p class="name">
                    <a class="link">
                        <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                        <xsl:value-of select="@title"/>
                    </a>
                </p>
            </div>
        </xsl:for-each>
    </xsl:template>
    <!--Template for category Level 3-->
    <xsl:template name="webshop_category-list-leve3">
        <xsl:for-each select="//negeso:webshop/negeso:categories/negeso:category[@selected]/negeso:category">
            <div class="ws-category-list">
                <a>
                    <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                    <img alt="" onerror="imgError(this)" class="img">
                        <xsl:attribute name="src">/media/productsCategory/<xsl:value-of select="translate(//negeso:webshop/negeso:categories/@title, ' ', '_')"/>_<xsl:value-of select="translate(//negeso:webshop/negeso:categories/negeso:category[@selected='true']/@title, ' ', '_')"/>_<xsl:value-of select="translate(@title, ' ABCDEFGHIJKLMNOPQRSTUVWXYZ', '_abcdefghijklmnopqrstuvwxyz')"/>.png</xsl:attribute>
                    </img>
                </a>
                <p class="name">
                    <a class="link">
                        <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                        <xsl:value-of select="@title"/>
                    </a>
                </p>
            </div>
        </xsl:for-each>
    </xsl:template>
    <!--Template for products without matrix-->
    <xsl:template name="webshop-products-view-no-matrix">

        <xsl:for-each select="//negeso:category/negeso:matrix[@title='not-a-matrix']/negeso:product">
            <div class="webshop-products-view">
                <span class="img-wrapper">
                    <xsl:if test="/negeso:page/@role-id = 'guest'">
                        <xsl:attribute name="id">img-wrapper</xsl:attribute>
                    </xsl:if>
                    <a class="loupeImg_js">
                        <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                        <img alt="" class="img" onerror="this.src='/media/cap.jpg'" >
                            <xsl:attribute name="src">/media/productsImages/<xsl:value-of select="@id"/>.jpg</xsl:attribute>
                        </img>
                       <!-- <span class="img-bg" />-->
                        <!--<span class="loupeImg" />-->
                    </a>

                </span>
                <p class="name">
                   <!-- <xsl:if test="not(/negeso:page/@role-id = 'guest')">-->

                        <img class="all-product-indicator" alt="">
                            <xsl:attribute name="src">/site/modules/webshop_module/images/stock_<xsl:value-of select="@stockColor"/>.png</xsl:attribute>
                        </img>
                        <!-- <span>Artikelnummer: <b><xsl:value-of select="@id"/></b></span>-->

                   <!-- </xsl:if>-->

                    <a class="link">
                        <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                        <xsl:value-of select="@title"/>
                    </a><br/>

                </p>


              <!--  <xsl:if test="/negeso:page/@role-id = 'guest'">
                    <a class="buy">
                        <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                        <span class="cart">Bestel nu</span>
                    </a>
                </xsl:if>-->

               <!-- <xsl:if test="not(/negeso:page/@role-id = 'guest')">-->
                <p style="text-align: center;">
                    <span class="price">
                        <xsl:value-of select="@priceIncludeVat"/>
                    </span>
                </p>
                    <p class="price-block">
                        <span class="link">

                           <!-- <xsl:choose>
                                <xsl:when test="@in-wishlist='true'">
                                    <span class="addToCart wish_list_added_js">
                                        <span class="product-code" style="display: none;"><xsl:value-of select="@id"/></span>
                                    </span>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:if test="not(/negeso:page/@role-id = 'guest')">
                                        <span class="addToCart  wish_list_add_js">
                                            <span class="product-code" style="display: none;"><xsl:value-of select="@id"/></span>
                                        </span>
                                    </xsl:if>
                                </xsl:otherwise>
                            </xsl:choose>-->
                            <span class="product-counter product-counter-matrix">
                                <button class="js_minus_matrix" />
                                <input class="js_counter_matrix" name="product_amount" type="text">
                                    <xsl:choose>
                                        <xsl:when test="@multipleOf = '0' or not(@multipleOf)">
                                            <xsl:attribute name="value"><xsl:value-of select="1"/></xsl:attribute>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="value"><xsl:value-of select="@multipleOf"/></xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
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
                                <button class="js_plus_matrix" />
                            </span>


                        </span>
                    </p>
                <div class="product-cart-wrapper"> <span style="display: none;"><xsl:value-of select="@id" /></span><span class="product-cart" onclick="addPtoductListInCart(event, this) ">bestellen</span></div>
               <!-- </xsl:if>-->
            </div>
        </xsl:for-each>
    </xsl:template>
    <!--Template for products with matrix-->
    <xsl:template name="webshop-products-view-with-matrix">
        <xsl:for-each select="//negeso:category/negeso:matrix[@title!='not-a-matrix']/negeso:product">
            <div class="webshop-products-view-matrix clearfix">
                <div class="img-matrix">
                    <img alt="" class="img" onerror="this.src='/media/cap.jpg'">
                        <xsl:attribute name="src">/media/productsImages/<xsl:value-of select="@id"/>.jpg</xsl:attribute>
                    </img>
                </div>
                <div class="title-matrix">
                    <a class="link-matrix">
                        <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                        <xsl:value-of select="@title"/>
                    </a><br/>
                    <xsl:if test="not(/negeso:page/@role-id = 'guest')">
                        <img class="matrix-indicator" alt="">
                            <xsl:attribute name="src">/site/modules/webshop_module/images/stock_<xsl:value-of select="@stockColor"/>.png</xsl:attribute>
                        </img>
                    </xsl:if>
                </div>
                <div class="product_number">
                    <span class="pr_number"><xsl:value-of select="@id" /></span>
                </div>
                <div class="value-matrix">
                    <span class="value">
                        <xsl:value-of select="@matrixValue"/>
                    </span>
                </div>
                <div class="price-matrix">
                    <span class="price">
                        <xsl:if test="not(/negeso:page/@role-id = 'guest')">
                            <xsl:choose>
                                <xsl:when test="/negeso:page/negeso:contents/negeso:customer/@displayPrice = 'Inkoop'">
                                    <xsl:value-of select="@priceAfterDiscount" />
                                </xsl:when>
                                <xsl:when test="/negeso:page/negeso:contents/negeso:customer/@displayPrice = 'Verkoop'">
                                    <xsl:value-of select="@retailPriceExcludeVat" />
                                </xsl:when>
                                <xsl:when test="/negeso:page/negeso:contents/negeso:customer/@displayPrice = 'Inkoop_Verkoop'">
                                    <xsl:value-of select="@retailPriceExcludeVat" /><br/>
                                    <xsl:value-of select="@priceAfterDiscount" />
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="@priceAfterDiscount" />
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:if>
                    </span>
                </div>
                <div class="product-counter product-counter-matrix">
                    <button class="js_minus_matrix" />
                    <input class="js_counter_matrix" name="product_amount" type="text">
                        <xsl:choose>
                            <xsl:when test="@multipleOf = '0' or not(@multipleOf)">
                                <xsl:attribute name="value"><xsl:value-of select="1"/></xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="value"><xsl:value-of select="@multipleOf"/></xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
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
                    <button class="js_plus_matrix" />
                    <button onclick="addPtoductInCartMatrix(event, this)" type="button" class="product-add-to-cart">TOEVOEGEN</button>
                </div>
            </div>
        </xsl:for-each>
    </xsl:template>
    <!--Template for products detail-->
    <xsl:template name="webshop-products-details">
        <xsl:call-template name="webshop-breadcrumps" />
        <div class="webshop-products-details webshop-products-details-card clearfix">
            <div class="left-side">
                <img alt="" onerror="this.src='/media/cap.jpg'">
                    <xsl:attribute name="src">/media/productsImages/<xsl:value-of select="//negeso:product/@id"/>.jpg</xsl:attribute>
                </img>
            </div>
            <div class="right-side">
                <h1 class="pd-title"><xsl:value-of select="//negeso:webshop/negeso:product/@title"/></h1>
                <div class="indicator">
                    <img alt="">
                        <xsl:attribute name="src">/site/modules/webshop_module/images/stock_<xsl:value-of select="//negeso:product/@stockColor"/>.png</xsl:attribute>
                    </img>
                </div>
                <p class="pd-price bottom-line">

                        <xsl:choose>
                            <xsl:when test="/negeso:page/negeso:contents/negeso:customer/@displayPrice = 'Inkoop'">
                                <span>Bruto: </span><xsl:value-of select="//negeso:webshop/negeso:product/@priceAfterDiscount" /><br/>
                                <span class="pd-attr discount-price">
                                    <span>
                                        <xsl:call-template name ="add-constant-info">
                                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_RPODUCT_PRICE_AFTER_DICSOUNT'"/>
                                        </xsl:call-template>
                                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_RPODUCT_PRICE_AFTER_DICSOUNT')"/>: <xsl:value-of select="//negeso:webshop/negeso:product/@priceAfterDiscount" />
                                    </span>
                                </span>
                            </xsl:when>
                            <xsl:when test="/negeso:page/negeso:contents/negeso:customer/@displayPrice = 'Verkoop'">
                                <span>Advies: </span><xsl:value-of select="//negeso:webshop/negeso:product/@retailPriceExcludeVat" /><br/>
                                <!--<span class="pd-attr discount-price">-->
                                <!--<span>-->
                                <!--<xsl:call-template name ="add-constant-info">-->
                                <!--<xsl:with-param name ="dict"  select="$dict_webshop_module"/>-->
                                <!--<xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_RPODUCT_PRICE_AFTER_DICSOUNT'"/>-->
                                <!--</xsl:call-template>-->
                                <!--<xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_RPODUCT_PRICE_AFTER_DICSOUNT')"/>: <xsl:value-of select="//negeso:webshop/negeso:product/@priceAfterDiscount" />-->
                                <!--</span>-->
                                <!--</span>-->
                            </xsl:when>
                            <xsl:when test="/negeso:page/negeso:contents/negeso:customer/@displayPrice = 'Inkoop_Verkoop'">
                                <span>Advies: </span><xsl:value-of select="//negeso:webshop/negeso:product/@retailPriceExcludeVat" /><br/>
                                <span>Bruto: </span><xsl:value-of select="//negeso:webshop/negeso:product/@priceAfterDiscount" /><br/>
                                <span class="pd-attr discount-price">
                                    <span>
                                        <xsl:call-template name ="add-constant-info">
                                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_RPODUCT_PRICE_AFTER_DICSOUNT'"/>
                                        </xsl:call-template>
                                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_RPODUCT_PRICE_AFTER_DICSOUNT')"/>: <xsl:value-of select="//negeso:webshop/negeso:product/@priceAfterDiscount" />
                                    </span>
                                </span>
                            </xsl:when>
                            <xsl:otherwise>
                                <span>exl BTW: </span><xsl:value-of select="//negeso:webshop/negeso:product/@priceExcludeVat" /><br/>
                                <span>incl BTW: </span><xsl:value-of select="//negeso:webshop/negeso:product/@priceIncludeVat" />

                                <!--<span class="pd-attr discount-price">
                                    <span>
                                        <xsl:call-template name ="add-constant-info">
                                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_RPODUCT_PRICE_AFTER_DICSOUNT'"/>
                                        </xsl:call-template>
                                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_RPODUCT_PRICE_AFTER_DICSOUNT')"/>: <xsl:value-of select="//negeso:webshop/negeso:product/@priceAfterDiscount" />
                                    </span>
                                </span>-->
                            </xsl:otherwise>
                        </xsl:choose>

                </p>
                <p class="pd-attr">
                    <span>
                        <xsl:call-template name ="add-constant-info">
                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_RPODUCTNUMMER'"/>
                        </xsl:call-template>
                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_RPODUCTNUMMER')"/>: </span> <span id="pr_number"><xsl:value-of select="//negeso:webshop/negeso:product/@id" /></span>
                </p>
                <xsl:if test="//negeso:webshop/negeso:product/@keepStock = 'false'">
                    <p class="warning">
                        <xsl:call-template name ="add-constant-info">
                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_KEEPSTOCK_INFO'"/>
                        </xsl:call-template>
                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_KEEPSTOCK_INFO')"/>
                    </p>
                </xsl:if>
                <!--<p class="pd-attr">
                    <span>
                        <xsl:call-template name ="add-constant-info">
                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_EANCODE'"/>
                        </xsl:call-template>
                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_EANCODE')"/>: </span> <xsl:value-of select="//negeso:webshop/negeso:product/@ean" />
                </p>-->
                <p class="pd-attr">
                    <span>
                        <xsl:call-template name ="add-constant-info">
                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_MERK'"/>
                        </xsl:call-template>
                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_MERK')"/>: </span> <xsl:value-of select="//negeso:webshop/negeso:product/@brand" />
                </p>
                <!--<p class="pd-attr">
                    <span>
                        <xsl:call-template name ="add-constant-info">
                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_MULTIPLE_OF'"/>
                        </xsl:call-template>
                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_MULTIPLE_OF')"/>: </span>
                    <span id="multipleOf">
                        <xsl:choose>
                            <xsl:when test="//negeso:webshop/negeso:product/@multipleOf = '0' or not(//negeso:webshop/negeso:product/@multipleOf)">1</xsl:when>
                            <xsl:otherwise><xsl:value-of select="//negeso:webshop/negeso:product/@multipleOf" /></xsl:otherwise>
                        </xsl:choose>
                    </span>
                </p>-->
                <!--<xsl:if test="//negeso:webshop/negeso:product/@color">
                    <p class="pd-attr"><span>
                        <xsl:call-template name ="add-constant-info">
                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_COLOR'"/>
                        </xsl:call-template>
                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_COLOR')"/>: </span> <xsl:value-of select="//negeso:webshop/negeso:product/@color" /></p>
                </xsl:if>-->
                <xsl:if test="//negeso:webshop/negeso:product/@content">
                    <p class="pd-attr">
                        <span>
                            <xsl:call-template name ="add-constant-info">
                                <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                                <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_CONTENT'"/>
                            </xsl:call-template>
                            <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_CONTENT')"/>: </span> <xsl:value-of select="//negeso:webshop/negeso:product/@content" />
                    </p>
                </xsl:if>
                <xsl:if test="//negeso:webshop/negeso:product/@packingUnit">
                    <p class="pd-attr bottom-line"><span>
                        <xsl:call-template name ="add-constant-info">
                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_UNIT'"/>
                        </xsl:call-template>
                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_UNIT')"/>: </span> <xsl:value-of select="//negeso:webshop/negeso:product/@packingUnit" />
                    </p>
                </xsl:if>
                <xsl:if test="//negeso:webshop/negeso:product/@description">
                    <p class="bottom-line"><xsl:value-of select="//negeso:webshop/negeso:product/@description" /></p>
                </xsl:if>
                <xsl:if test="//negeso:webshop/negeso:product/@pdfDocument">
                    <p class="bottom-line">
                        <xsl:call-template name ="add-constant-info">
                            <xsl:with-param name ="dict"  select="$dict_webshop_module"/>
                            <xsl:with-param name ="name"  select="'WEBSHOP_PRODUCT_DOWNLOAD_DOCUMENTATION'"/>
                        </xsl:call-template>
                        <xsl:value-of select="java:getString($dict_webshop_module, 'WEBSHOP_PRODUCT_DOWNLOAD_DOCUMENTATION')"/></p>
                </xsl:if>
              <!--  <xsl:if test="not(/negeso:page/@role-id = 'guest')">-->

                    <div class="product-counter-block">
                        <div class="product-counter">
                           <!-- <xsl:if test="not(/negeso:page/@role-id = 'guest')">
                                <xsl:choose>
                                    <xsl:when test="//negeso:webshop/negeso:product/@in-wishlist='true'">
                                        <button type="button" class="wish_list_added_js" />
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <button type="button" class="wish_list_add_js" />
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:if>-->

                        <!--    <button class="js_minus" />
                            <input id="js_counter" name="product_amount" type="text">
                                <xsl:choose>
                                    <xsl:when test="//negeso:webshop/negeso:product/@multipleOf = '0' or not(//negeso:webshop/negeso:product/@multipleOf)">
                                        <xsl:attribute name="value"><xsl:value-of select="1"/></xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:attribute name="value"><xsl:value-of select="//negeso:webshop/negeso:product/@multipleOf"/></xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </input>
                            <button class="js_plus" />-->
                            <span class="product-counter-matrix">
                                <button class="js_minus_matrix" />
                                <input class="js_counter_matrix" name="product_amount" type="text">
                                    <xsl:choose>
                                        <xsl:when test="@multipleOf = '0' or not(@multipleOf)">
                                            <xsl:attribute name="value"><xsl:value-of select="1"/></xsl:attribute>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="value"><xsl:value-of select="@multipleOf"/></xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
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
                                <button class="js_plus_matrix" />
                            </span>
                            <button onclick="addPtoductInCart()" type="button" class="product-add-to-cart">TOEVOEGEN</button>
                        </div>
                    </div>
              <!--  </xsl:if>-->
            </div>
        </div>
    </xsl:template>
    <!--Template for header and search block-->
    <xsl:template name="header_and_serch_block">
        <div class="search-field">
            <form class="form form-search" action="/zoeken_nl.html">
                <input placeholder="ZOEKEN" onclick="this.value=''" name="query" id="search-zoeken" class="search" type="text" />
                <button type="submit" class="searchbutton"/>
            </form>
        </div>
    </xsl:template>
    <!--Template for breadcrumps-->
    <xsl:template name="webshop-breadcrumps">
        <div class="clearfix breadcrumps-wrapper">
           <!-- <a class="breadcrumps-links">
                <xsl:attribute name="href"><xsl:value-of select="//negeso:webshop/negeso:categories/@url"/></xsl:attribute>
                <xsl:value-of select="//negeso:webshop/negeso:categories/@title"/>
            </a>-->
            <xsl:if test="//negeso:webshop/negeso:categories/negeso:category[@selected='true']">
                <a class="breadcrumps-links">
                    <xsl:attribute name="href"><xsl:value-of select="//negeso:webshop/negeso:categories/negeso:category[@selected='true']/@url"/></xsl:attribute>
                   <!-- <xsl:value-of select="//negeso:webshop/negeso:categories/negeso:category[@selected='true']/@title"/>-->

                    terug naar de lijst
                </a>
            </xsl:if>
            <xsl:if test="//negeso:webshop/negeso:categories/negeso:category[@selected='true']/negeso:category[@selected='true']">
                <a class="breadcrumps-links">
                    <xsl:attribute name="href"><xsl:value-of select="//negeso:webshop/negeso:categories/negeso:category[@selected='true']/negeso:category[@selected='true']/@url"/></xsl:attribute>
                    <xsl:value-of select="//negeso:webshop/negeso:categories/negeso:category[@selected='true']/negeso:category[@selected='true']/@title"/>
                </a>
            </xsl:if>
        </div>
    </xsl:template>
</xsl:stylesheet>