<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2005 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  HTML Invoice mail formatting xsl

  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java"
>
<xsl:variable name="lang" select="'nl'"/>
<xsl:variable name="thr_order_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('thr_order', $lang)"/>

<xsl:output method="html"/>

<xsl:template match="negeso:orders">
	<html>
	<head>
		<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title>Bestelling THR App</title>
	</head>
	<body style="font: normal 12px Arial; color: #3f4038;">
		<h2 style="font: bold 13px Arial; color: #3f4038; font-variant: small-caps;">Bestelling THR App</h2>
		<h2 style="font: bold 13px Arial; color: #3f4038; font-variant: small-caps;"><xsl:value-of select="@date"/></h2>
		<h2 style="font: bold 13px Arial; color: #3f4038; font-variant: small-caps;">Totaal: <xsl:value-of select="@total" disable-output-escaping="yes"/></h2>
		<xsl:apply-templates select="negeso:order"/>
	</body>
	</html>
</xsl:template>

<xsl:template match="negeso:order">
		<table border="0" width="50%" style="color: #e5e5e5; padding-bottom: 5px; padding-left: 5px;">
				<tr>
					<td width="50%" style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: left; vertical-align: middle;">&#160;&#160;<b><xsl:value-of select="java:getString($thr_order_module, 'THR_ORDER_DATE')"/></b></td>
					<td width="50%" style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: left; vertical-align: middle;"><xsl:value-of select="@date"/></td>
				</tr>
				<tr>
					<td width="50%" style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: left; vertical-align: middle;">&#160;&#160;<b><xsl:value-of select="java:getString($thr_order_module, 'THR_ORDER_LOGIN')"/></b></td>
					<td width="50%" style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: left; vertical-align: middle;"><xsl:value-of select="@login"/></td>
				</tr>
				<tr>
					<td width="50%" style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: left; vertical-align: middle;">&#160;&#160;<b><xsl:value-of select="java:getString($thr_order_module, 'THR_ORDER_OS_VESION')"/></b></td>
					<td width="50%" style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: left; vertical-align: middle;">
						<xsl:choose>
							<xsl:when test="not(@apiVersion)">
								<img title="iOS" src="{../@host}images/apple.png" />
							</xsl:when>
							<xsl:otherwise>
								<img title="iOS" src="{../@host}images/android.png" />
							</xsl:otherwise>
						</xsl:choose>
						<xsl:value-of select="@osVersion"/>
					</td>
				</tr>
				<tr>
					<td width="50%" style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: left; vertical-align: middle;">&#160;&#160;<b><xsl:value-of select="java:getString($thr_order_module, 'THR_ORDER_MODEL')"/></b></td>
					<td width="50%" style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: left; vertical-align: middle;"><xsl:value-of select="@model"/></td>
				</tr>
		</table>


		<h3 style="font: 12px Arial; color: #3f4038; font-variant: small-caps;">Producten:</h3>
		<xsl:apply-templates select="negeso:products"/>

		<br/>
		<br/>
</xsl:template>


<xsl:template match="negeso:products">

	<table border="0" width="100%" style="color: #3f4038; padding-bottom: 5px; padding-left: 5px;">
	<tr>
		<td style="border: solid 1px #e5e5e5; color: #3f4038; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="15%">EAN code</td>
		<td style="border: solid 1px #e5e5e5; color: #3f4038; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="45%">Omschrijving</td>
		<td style="border: solid 1px #e5e5e5; color: #3f4038; font: normal 12px Arial; vertical-align: middle; text-align: center;">Aantal</td>
		<td style="border: solid 1px #e5e5e5; color: #3f4038; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="10%">Prijs</td>
		<td style="border: solid 1px #e5e5e5; color: #3f4038; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="15%">Totaal</td>
	</tr>         
	<xsl:apply-templates select = "negeso:product" mode="available"/>
	
	
	<xsl:if test="count(negeso:pm-product) != 0">
		<tr>
			<td style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: right; vertical-align: middle;" colspan="5">
				<br/>
				Subtotaal:
				<br/>
			</td>
			<td style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: right; vertical-align: middle;">
				<br/>
				<xsl:value-of select="@subtotal-price"/>&#160;
				<br/>
			</td>
		</tr>
	</xsl:if>
		<tr>
			<td style="border: solid 1px #e5e5e5; font: bold 12px Arial; color: #3f4038; text-align: right; vertical-align: middle;" colspan="4">
				<br/>
				Totaal:
				<br/>
			</td>
			<td style="border: solid 1px #e5e5e5; font: bold 12px Arial; color: #3f4038; text-align: right; vertical-align: middle;">
				<br/>
				<xsl:value-of select="../@totalFormatted" disable-output-escaping="yes"/>&#160;
				<br/>
			</td>
		</tr>
		<xsl:if test="../@nonorderableCount &gt; 0">
			<tr>
				<td style="border: 0; font: 12px Arial; color: #3f4038; font-variant: small-caps;">
					Niet bestelbare / onbekende artikelen:
				</td>
			</tr>
			<xsl:apply-templates select = "negeso:product" mode="not_available"/>
		</xsl:if>
	</table>
</xsl:template>

<xsl:template match="negeso:product" mode="available">
	<xsl:if test="@available='true'">
		<tr style="border: solid 1px #e5e5e5;">
			<td style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: left; vertical-align: middle;">
				<b><xsl:value-of select="@barCode"/></b>
			</td>                                            
			<td style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: left; vertical-align: middle;">
				<xsl:value-of select="negeso:info/@description"/>&#160;
			</td>
			<td style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: right; vertical-align: middle;">
				<xsl:value-of select="@count"/>&#160;
			</td>
		
			<td style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: right; vertical-align: middle;">
				<xsl:value-of select="negeso:info/@priceFormatted" disable-output-escaping="yes"/>&#160;
			</td>
			<td style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: right; vertical-align: middle;">
				<xsl:value-of select="@totalPriceFormatted" disable-output-escaping="yes"/>&#160;
			</td>
		</tr>
	</xsl:if>
</xsl:template>

<xsl:template match="negeso:product" mode="not_available">
	<xsl:if test="@available='false'">
		<tr style="border: solid 1px #e5e5e5;">
			<td style="border: solid 1px #e5e5e5; font: normal 12px Arial; color: #3f4038; text-align: left; vertical-align: middle;"  colspan="5">
				<b><xsl:value-of select="@barCode"/></b>
			</td>
		</tr>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>

