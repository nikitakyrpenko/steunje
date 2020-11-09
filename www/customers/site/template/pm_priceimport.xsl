<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2003 Negeso Ukraine

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
>

<xsl:output method="html"/>

<xsl:template match="negeso:import-result">
	<html>
	<head>
		<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	    <title>Price list import result email</title>
	</head>
	<body style="font: normal 12px Arial; color: #114b9f;">
		<h2 style="font: bold  13px Arial; color: #114b9f; font-variant: small-caps;">
			Good day, this is price list import result email. Import
		</h2>
		<xsl:for-each select="negeso:unknown">
			<xsl:if test="count(negeso:product) > 0">
				<h3>This products was in price-list but not found in online product database:</h3>
				<table border="0" width="40%" style="color: #114b9f; padding-bottom: 5px; padding-left: 5px;">
					<xsl:for-each select="negeso:product">
						<tr>
							<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; vertical-align: middle; text-align:left;">&#160;&#160;<b><xsl:call-template name="sn"/></b></td>
							<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; vertical-align: middle; text-align:left;"><xsl:value-of select="@sn"/></td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:if>
		</xsl:for-each>

		<xsl:for-each select="negeso:unimported">
			<xsl:if test="count(negeso:product) > 0">
				<h3>This products was in online product database, but was not in imported price list:</h3>
				<table border="0" width="40%" style="color: #114b9f; padding-bottom: 5px; padding-left: 5px;">
					<xsl:for-each select="negeso:product">
						<tr>
							<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; vertical-align: middle; text-align:left;">&#160;&#160;<b><xsl:call-template name="sn"/></b></td>
							<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; vertical-align: middle; text-align:left;"><xsl:value-of select="@sn"/></td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:if>
		</xsl:for-each>

	</body>
	</html>
</xsl:template>

<xsl:template name="sn">
	<xsl:choose>
		<xsl:when test="/negeso:page/@lang = 'nl'">
			<xsl:text>Serienummer</xsl:text>
		</xsl:when>
		<xsl:when test="/negeso:page/@lang = 'fr'">
			<xsl:text>Nr. de s&#233;rie</xsl:text>
		</xsl:when>
		<xsl:when test="/negeso:page/@lang = 'de'">
			<xsl:text>Serien Nr.</xsl:text>
		</xsl:when>
		<xsl:when test="/negeso:page/@lang = 'es'">
			<xsl:text>N&#250;mero de serie</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>Serial number</xsl:text>
		</xsl:otherwise>
	</xsl:choose>	
</xsl:template>

</xsl:stylesheet>

