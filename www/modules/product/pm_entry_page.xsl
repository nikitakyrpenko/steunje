<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${product_type_list.xsl}      
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View a list of product types.
 
  @version      $Resvision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">

    <!-- Include/Import -->
    <xsl:include href="/xsl/negeso_body.xsl"/>

    <xsl:variable name="lang" select="/*/@interface-language"/>
    <xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
    <xsl:variable name="dict_product" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_product.xsl', $lang)"/>
    <xsl:variable name="dict_pm_entry_page" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_pm_entry_page.xsl', $lang)"/>
    <xsl:variable name="dict_modules" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_modules.xsl', $lang)"/>

    <!-- NEGESO JAVASCRIPT Temaplate -->
    <xsl:template name="java-script">
        <script language="JavaScript">
            <xsl:text disable-output-escaping="yes">
        <![CDATA[
        ]]>
        </xsl:text>
        </script>
        <script type="text/javascript" src="/script/jquery.min.js"></script>
		<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script type="text/javascript" src="/script/common_functions.js"></script>        
    </xsl:template>

    <!-- MAIN ENTRY -->
    <xsl:template match="/negeso:page">
        <html>
            <head>
                <title>
                    <xsl:value-of select="java:getString($dict_pm_entry_page, 'WELCOME_TO_PRODUCT_MODULE')"/>
                </title>
                <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <xsl:call-template name="java-script"/>
                <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
            </head>
            <body>
                <!-- NEGESO HEADER -->
                <xsl:call-template name="NegesoBody">
                    <xsl:with-param name="helpLink">
                        <xsl:text>/admin/help/cpr1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
                <div align="center">
                    <!-- CONTENT -->
                    <xsl:apply-templates select="negeso:page"/>
                </div>
            </body>
        </html>
    </xsl:template>


    <xsl:template match="negeso:page"  mode="admContent">

        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td align="center" class="admNavPanelFont" >
                    <!-- TITLE -->
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:value-of select="java:getString($dict_pm_entry_page, 'WELCOME_TO_PRODUCT_MODULE')"/>
                        </xsl:with-param>
                    </xsl:call-template>

                </td>
            </tr>
            <tr>
                <td>
                    <table cellspacing="0" cellpadding="0" width="100%">
                        <tr>
                            <td class="admTDtitles" colspan="2" align="center">
                               
                                        <xsl:value-of select="java:getString($dict_product, 'PRODUCT_CATALOG')"/>
                                   
                            </td>
                        </tr>
                        <tr>
                            <th class="admTableTD" width="50%">
                                <a class="admAnchor" href="?command=pm-browse-category">
                                    <xsl:value-of select="java:getString($dict_modules, 'BROWSE_CATEGORIES')"/>
                                </a>
                            </th>
                            <th class="admTableTDLast"  width="50%">
                                <a class="admAnchor" href="?command=pm-browse-product-types">
                                    <xsl:value-of select="java:getString($dict_modules, 'EDIT_PRODUCT_TYPES')"/>
                                </a>
                            </th>
                        </tr>
                        <tr>
                            <th class="admTableTD">
                                <a class="admAnchor" href="/admin/pm_feature">
                                    <xsl:value-of select="java:getString($dict_modules, 'EDIT_FEATURED_LISTS_OF_PRODUCTS')"/>
                                </a>
                            </th>
                            <th class="admTableTDLast">
                                
                                <a class="admAnchor" href="#" disabled="true">
                                    <!-- href="?command=pm-browse-product-types" -->
                                    <xsl:value-of select="java:getString($dict_modules, 'SETTINGS')"/> (<xsl:value-of select="java:getString($dict_common, 'UNDER_CONSTRUCTION')"/>)
                                </a>
                            </th>
                        </tr>

                    </table>
                    <xsl:call-template name="pm.renderShop"/>                
                </td>
            </tr>
            <tr>
                <td class="admTableFooter" >&#160;</td>
            </tr>
        </table>
    </xsl:template>           
  

    <xsl:template name="pm.renderShop">
        <xsl:choose>
            <xsl:when test="/negeso:page/@is-shop-enabled = 'true'">
                <div>
                    <table class="admNavPanel" cellspacing="0" cellpadding="0">
                        <tr>
                            <td class="admTDtitles" colspan="2" align="center">
                               
                                        <xsl:value-of select="java:getString($dict_product, 'SHOP')"/>
                                   
                            </td>
                        </tr>
                        <tr>
                            <th class="admTableTD" width="50%">
                                <a class="admAnchor" href="?command=pm-manage-customers">
                                    <xsl:value-of select="java:getString($dict_product, 'CUSTOMERS')"/>
                                </a>
                            </th>
                            <th class="admTableTDLast" width="50%">
                                <a class="admAnchor" href="?command=pm-manage-shop-preferences">
                                    <xsl:value-of select="java:getString($dict_common, 'PREFERENCES')"/>
                                </a>
                            </th>
                        </tr>
                        <tr>
                            <th class="admTableTD" width="50%">
                                <a class="admAnchor" href="?command=pm-manage-delivery-methods">
                                    <xsl:value-of select="java:getString($dict_product, 'DELIVERY_METHODS')"/>
                                </a>
                            </th>
                            <th class="admTableTDLast" width="50%">
                                <a class="admAnchor" disabled="true">
                                    <!-- href="?command=pm-manage-price-list" -->
                                    <xsl:value-of select="java:getString($dict_pm_entry_page, 'MANAGE_PRICE_LIST')"/> (<xsl:value-of select="java:getString($dict_common, 'UNDER_CONSTRUCTION')"/>)
                                </a>
                            </th>
                        </tr>
                        <tr>
                            <th class="admTableTD" >
                                <a class="admAnchor" href="/admin/orders?consumer=-1&amp;isArchive=false" width="50%">
                                    <xsl:value-of select="java:getString($dict_pm_entry_page, 'MANAGE_PM_ORDERS')"/>
                                </a>
                            </th>
                            <th class="admTableTDLast" width="50%">
                                <a class="admAnchor" href="/admin/discount?act=showDiscountSettings">
                                    <xsl:value-of select="java:getString($dict_product, 'PM_DISCOUNT')"/>
                                </a>
                            </th>
                        </tr>
                    </table>
                </div>
            </xsl:when>
            <xsl:otherwise>
                <div disabled="true">
                    <table cellspacing="0" cellpadding="0">
                        <tr>
                            <td class="admTDtitles" colspan="2" align="center">
                              
                                        <xsl:value-of select="java:getString($dict_product, 'SHOP')"/>
                                        (<xsl:value-of select="java:getString($dict_common, 'DISABLED')"/>)
                                    
                            </td>
                        </tr>
                        <tr>
                            <th class="admTableTD" width="50%">>
                                <a class="admAnchor" href="#">
                                    <xsl:value-of select="java:getString($dict_product, 'CUSTOMERS')"/>
                                </a>
                            </th>
                            <th class="admTableTDLast" width="50%">
                                <a class="admAnchor" href="#" disabled="true">
                                    <xsl:value-of select="java:getString($dict_common, 'PREFERENCES')"/>
                                </a>
                            </th>
                        </tr>
                        <tr>
                            <th class="admTableTD" width="50%">
                                <a class="admAnchor" href="#">
                                    <xsl:value-of select="java:getString($dict_product, 'DELIVERY_METHODS')"/>
                                </a>
                            </th>
                            <th class="admTableTDLast" width="50%">
                                &#160;
                            </th>
                        </tr>
                    </table>
                </div>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

</xsl:stylesheet>
