<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2010 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  HTML Invoice mail formatting xsl

  @version      $Revision$
  @author       Mykola Lyhozhon
-->

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
>

<xsl:output method="html"/>


<xsl:template match="//negeso:shopping-cart">

	<table border="0" width="100%" style="color: #114b9f; padding-bottom: 5px; padding-left: 5px;">
	<tr>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="15%">Artikelnummer</td>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="45%">Productnaam</td>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; font: normal 12px Arial; vertical-align: middle; text-align: center;">Hoeveelheid</td>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="10%">Prijs</td>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; text-align: center; vertical-align: middle; font: bold  12px Arial;" width="15%">Verwijderingsbijdrage</td>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="15%">Totaal inclusief BTW</td>
	</tr>
	   <tr> <td colspan=""></td></tr>        
	<xsl:apply-templates select = "//negeso:pm-product"/>
	
	
	<xsl:if test="count(negeso:pm-product) != 0">
		<tr>
			<td style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: right; vertical-align: middle;" colspan="5">
				<br/>
				Subtotaal:
				<br/>
			</td>
			<td style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: right; vertical-align: middle;">
				<br/>
				<xsl:value-of select="@subtotal-price"/>&#160;
				<br/>
			</td>
		</tr>
		<xsl:for-each select="//negeso:delivery-method">
			<tr>
				<td style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: right; vertical-align: middle;" colspan="5">
					<br/>
					Verzendwijze (<xsl:value-of select="@title"/>):
					<br/>
				</td>
				<td style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: right; vertical-align: middle;">
					<br/>
					<xsl:value-of select="@price"/>&#160;
					<br/>
				</td>
			</tr>
		</xsl:for-each>
		<tr>
			<td style="border: solid 1px; font: bold 12px Arial; color: #114b9f; text-align: right; vertical-align: middle;" colspan="5">
				<br/>
				Totaal:
				<br/>
			</td>
			<td style="border: solid 1px; font: bold 12px Arial; color: #114b9f; text-align: right; vertical-align: middle;">
				<br/>
				<xsl:value-of select="@total-price"/>&#160;
				<br/>
			</td>
		</tr>
	</xsl:if>
	</table>
</xsl:template>

<xsl:template match="negeso:pm-product">
	<tr style="border: solid 1px;">
		<td style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">
			<xsl:value-of select="@sn"/>&#160;
		</td>                                            
		<td style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">
			<xsl:value-of select="@title"/>&#160;
		</td>
		<td style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: right; vertical-align: middle;">
			<xsl:value-of select="@amount"/>&#160;
		</td>
	
		<td style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: right; vertical-align: middle;">
			<xsl:value-of select="@formatted-price"/>&#160;
		</td>
		<td style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: right; vertical-align: middle;">
			<xsl:value-of select="@removing-formatted-price"/>&#160;
		</td>
		<td style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: right; vertical-align: middle;">
			<xsl:value-of select="@total-price"/>&#160;
		</td>
	</tr>
</xsl:template>
</xsl:stylesheet>

