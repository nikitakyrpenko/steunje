<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Product Module
  
  Here you can find all PM Filter templates.
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<!--========================= PRODUCT MODULE LINKS START =========================-->
<xsl:template name="pm_adv_filter_results_link">
    <xsl:value-of select="$adminPath"></xsl:value-of>
	<xsl:text>/pm_filter_results_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
</xsl:template>

<xsl:template name="pm_module_link">
<xsl:param name="addition" select="''" />
    <xsl:value-of select="$adminPath"></xsl:value-of>
	<xsl:text>/product_module_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text><xsl:value-of select="$addition" />
</xsl:template>

<xsl:template name="pm_shopping_cart_link">
<xsl:param name="addition" select="''" />
	<xsl:attribute name="href">
	   <xsl:value-of select="$adminPath"></xsl:value-of>
		<xsl:text>/webshop_cart_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
		<xsl:value-of select="$addition" />
	</xsl:attribute>
</xsl:template>

<xsl:template name="pm_account_link">
	<xsl:attribute name="href">
	   <xsl:value-of select="$adminPath"></xsl:value-of>
		<xsl:text>/account_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
	</xsl:attribute>
</xsl:template>

<xsl:template name="pm_login_link">
<xsl:param name="addition" select="''" />
	<xsl:attribute name="href">
	   <xsl:value-of select="$adminPath"></xsl:value-of>
		<xsl:text>/shop_login_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
		<xsl:if test="not($addition='')">?action=<xsl:value-of select="$addition" /></xsl:if>
	</xsl:attribute>
</xsl:template>

<xsl:template name="pm_checkout_link">
	<xsl:attribute name="href">
	     <xsl:value-of select="$adminPath"></xsl:value-of>
		<xsl:text>/checkout_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
	</xsl:attribute>
</xsl:template>

<xsl:template name="pm_register_link">
	<xsl:attribute name="href">
	    <xsl:value-of select="$adminPath"></xsl:value-of>
		<xsl:text>/register_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
	</xsl:attribute>
</xsl:template>

<xsl:template name="pm_service_conditions_link">
	<xsl:attribute name="href">
	    <xsl:value-of select="$adminPath"></xsl:value-of>
		<xsl:text>/conditions_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
	</xsl:attribute>
</xsl:template>

<xsl:template name="pm_first_use">
	<span>
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_pm_module"/>
			<xsl:with-param name ="name"  select="'PM_FIRST_TIME_USE_SHOP'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_pm_module, 'PM_FIRST_TIME_USE_SHOP')"/>
	</span>
	&#160;
	<a>
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_pm_module"/>
			<xsl:with-param name ="name"  select="'PM_THIS_LINK'"/>
		</xsl:call-template>
		<xsl:call-template name="pm_register_link" />
	<xsl:value-of select="java:getString($dict_pm_module, 'PM_THIS_LINK')"/></a>
	<br />
	<span>
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_pm_module"/>
			<xsl:with-param name ="name"  select="'PM_ALREADY_USE_SHOP'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_pm_module, 'PM_ALREADY_USE_SHOP')"/>
	</span>
</xsl:template>
<!--========================== PRODUCT MODULE LINKS END ==========================-->

</xsl:stylesheet>