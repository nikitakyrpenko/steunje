<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$
 
  Copyright (c) 2005 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  
 
  @version      $Revision$
  @author       Vyacheslav Zapadnyuk
-->

<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:negeso="http://negeso.com/2003/Framework"
>

<!-- Include/Import -->
<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:include href="/WEB-INF/generated/dictionaries/dict_common.xsl"/>
<xsl:include href="/WEB-INF/generated/dictionaries/dict_product.xsl"/>


<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
<head>
    <title>Edit order</title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
<body TEXT="#000000;" BGCOLOR="#FCFAF2">
    <!-- NEGESO HEADER -->
     <div align="center">
        <!-- CONTENT -->
        <xsl:apply-templates select="negeso:page"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page">
    <!-- Content -->
    <h2 style="font: bold  13px Arial; color: #114b9f; font-variant: small-caps;">Shop password reminder mail</h2>
    <xsl:for-each select="negeso:customer">
    <table width="100%" cellspacing="0" cellpadding="0" border="0" style="color: #114b9f; padding-bottom: 5px; padding-left: 5px;">
		<tr valign="middle" width="10%">
			<td >
				<label for="name"><xsl:call-template name="LOGIN"/>:&#160;&#160;</label>
			</td>
			<td align="left">
				<xsl:value-of select="@login"/>
			</td>
		</tr>
		<tr valign="middle" width="10%">
			<td class="admLightTD">
				<label for="name"><xsl:call-template name="NAME"/>:&#160;&#160;</label>
			</td>
			<td class="admMainTD" align="left">
				<xsl:value-of select="@name"/>
			</td>
		</tr>
		<tr valign="middle" width="10%">
			<td class="admLightTD">
				<label for="name"><xsl:call-template name="EMAIL"/>:&#160;&#160;</label>
			</td>
			<td class="admMainTD" align="left">
				<xsl:value-of select="@email"/>
			</td>
		</tr>
		<tr valign="middle" width="10%">
			<td class="admLightTD">
				<label for="name"><xsl:call-template name="PASSWORD"/>:&#160;&#160;</label>
			</td>
			<td class="admMainTD" align="left">
				<xsl:value-of select="@password"/>
			</td>
		</tr>
	</table>
    </xsl:for-each>
    <h2 style="font: bold  13px Arial; color: #114b9f; font-variant: small-caps;">If you have any questions contact your sales rep</h2>

</xsl:template>

<xsl:template name="PASSWORD">
<xsl:choose>
<xsl:when test="$lang='en'">Password</xsl:when>
<xsl:when test="$lang='am'">Password</xsl:when>
<xsl:when test="$lang='nl'">Wachtwoord</xsl:when>
<xsl:when test="$lang='de'">Password</xsl:when>
<xsl:when test="$lang='fr'">Password</xsl:when>
<xsl:when test="$lang='es'">Password</xsl:when>
<xsl:when test="$lang='it'">Password</xsl:when>
</xsl:choose>
</xsl:template>

<xsl:template name="LOGIN">
<xsl:choose>
<xsl:when test="$lang='en'">Login</xsl:when>
<xsl:when test="$lang='am'">Login</xsl:when>
<xsl:when test="$lang='nl'">Login</xsl:when>
<xsl:when test="$lang='de'">Login</xsl:when>
<xsl:when test="$lang='fr'">Login</xsl:when>
<xsl:when test="$lang='es'">Login</xsl:when>
<xsl:when test="$lang='it'">Login</xsl:when>
</xsl:choose>
</xsl:template>



</xsl:stylesheet>