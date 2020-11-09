<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${j_applicant.xsl}       
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a job module applicant.
 
  @version    2006/09/29
  @author     Vyacheslav Zapadnyuk
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
>

<!-- Include/Import -->
<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>

<!-- MAIN ENTRY -->
<xsl:template match="/negeso:page">
    <html>
        <head>
           <title><xsl:value-of select="negeso:webform/@title"/></title>
            <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        </head>
		<body style="font: normal 12px Arial; color: #114b9f;">
            <!-- CONTENT -->
            <xsl:apply-templates select="negeso:webform"/>
        </body>
    </html>
</xsl:template>

<xsl:template match="negeso:webform">
	<i><xsl:value-of select="@posted"/></i>
	<h2 style="font: bold 13px Arial; font-variant: small-caps;">
		<xsl:value-of select="@title"/>
	</h2>
	<table cellspacing="1" cellpadding="1" width="75%" style="font: normal 12px Arial;">
		<xsl:apply-templates select="negeso:input-field"/>
   </table>
</xsl:template>

<xsl:template match="negeso:input-field ">
	<xsl:if test="@value">
		<tr valign="middle">
			<td width="34%" style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
				&#160;<xsl:value-of select="@name"/>
			</td>
			<td style="border: solid 1px gray;">
				&#160;<xsl:value-of select="@value" disable-output-escaping="yes"/>
			</td>
		</tr>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>
