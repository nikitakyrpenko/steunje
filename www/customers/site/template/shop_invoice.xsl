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
>

<xsl:output method="html"/>

<xsl:template match="negeso:order">
	<html>
	<head>
		<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	    	<title>Shop invoice</title>
	</head>
	<body style="font: normal 12px Arial; color: #114b9f;">
		<h2 style="font: bold 13px Arial; color: #114b9f; font-variant: small-caps;">Shop factuur: Bestelling webshop Negeso</h2>
		<table border="0" width="100%" style="color: #114b9f; padding-bottom: 5px; padding-left: 5px;">
			<xsl:if test="@order-id">
				<tr>
					<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Order-ID</b></td>
					<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@order-id"/></td>
				</tr>
			</xsl:if>
			<xsl:if test="//@customer-id">
				<tr>
					<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>User-ID</b></td>
					<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="//@customer-id"/></td>
				</tr>
			</xsl:if>
			<xsl:for-each select="negeso:delivery-method">
				<tr>
					<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Verzendwijze</b></td>
					<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@title"/></td>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:for-each select="negeso:company-contact">
			<xsl:for-each select="negeso:contact">
				<table border="0" width="100%" style="color: #114b9f; padding-bottom: 5px; padding-left: 5px;">
					<xsl:if test="@company-name">
					<tr>
						<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Bedrijfsnaam</b></td>
						<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@company-name"/></td>
					</tr>
					</xsl:if>
			
					<xsl:if test="@email">
					<tr>
						<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Email</b></td>
						<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@email"/></td>
					</tr>
					</xsl:if>
				</table>
			</xsl:for-each>
		</xsl:for-each>

				
		<br/>
		<h3 style="font: 12px Arial; color: #114b9f; font-variant: small-caps;">Verzenden naar:</h3>
		<xsl:for-each select="negeso:shipping-contact">
			<xsl:apply-templates select="negeso:contact"/>
		</xsl:for-each>

		<br/>
		<h3 style="font: 12px Arial; color: #114b9f; font-variant: small-caps;">Rekening naar:</h3>
		<xsl:for-each select="negeso:billing-contact">
			<xsl:apply-templates select="negeso:contact"/>
		</xsl:for-each>

		<br/>
		<br/>
		<br/>

		<h3 style="font: 12px Arial; color: #114b9f; font-variant: small-caps;">Producten:</h3>
		<xsl:apply-templates select="negeso:shopping-cart"/>

		<br/>
		<br/>

		<h3 style="font: 12px Arial; color: #114b9f; font-variant: small-caps;">Commentaar</h3>
		<table border="0" width="100%" style="color: #114b9f; padding-bottom: 5px; padding-left: 5px;">
<!--			<xsl:if test="@pmOrderComment">-->
				<tr>
					<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>De commentaar van de klant:</b></td>
					<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@pmOrderComment"/></td>
				</tr>
<!--			</xsl:if>-->
		</table>
		<br/>
		<br/>

		<p style="font: normal 12px Arial; color: #114b9f;">Een order is geplaatst.</p>
	</body>
	</html>
</xsl:template>


<xsl:template match="negeso:shopping-cart">

	<table border="0" width="100%" style="color: #114b9f; padding-bottom: 5px; padding-left: 5px;">
	<tr>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="15%">Artikelnummer</td>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="45%">Productnaam</td>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; font: normal 12px Arial; vertical-align: middle; text-align: center;">Hoeveelheid</td>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="10%">Prijs</td>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; text-align: center; vertical-align: middle; font: bold  12px Arial;" width="15%">Verwijderingsbijdrage</td>
		<td style="border: solid 1px; border-color: #114b9f; color: #114b9f; font: normal 12px Arial; vertical-align: middle; text-align: center;" width="15%">Totaal inclusief BTW</td>
	</tr>         
	<xsl:apply-templates select = "negeso:pm-product"/>
	
	
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

<xsl:template match="negeso:contact">
	<table border="0" width="100%" style="color: #114b9f; padding-bottom: 5px; padding-left: 5px;">
		<xsl:if test="@first-name">
		<tr>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Voornaam</b></td>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@first-name"/></td>
		</tr>
		</xsl:if>

		<xsl:if test="@second-name">
		<tr>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Achternaam</b></td>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@second-name"/></td>
		</tr>
		</xsl:if>

		<xsl:if test="@company-name">
		<tr>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Bedrijfsnaam</b></td>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@company-name"/></td>
		</tr>
		</xsl:if>

		<xsl:if test="@address-line">
		<tr>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Adres gegevens</b></td>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@address-line"/></td>
		</tr>
		</xsl:if>

		<xsl:if test="@zip-code">
		<tr>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Zip/Postcode</b></td>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@zip-code"/></td>
		</tr>
		</xsl:if>

		<xsl:if test="@city">
		<tr>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Stad</b></td>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@city"/></td>
		</tr>
		</xsl:if>

		<xsl:if test="@country">
		<tr>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Land</b></td>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@country"/></td>
		</tr>
		</xsl:if>

		<xsl:if test="@phone">
		<tr>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Telefoon</b></td>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@phone"/></td>
		</tr>
		</xsl:if>

		<xsl:if test="@fax">
		<tr>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;">&#160;&#160;<b>Fax</b></td>
			<td width="50%" style="border: solid 1px; font: normal 12px Arial; color: #114b9f; text-align: left; vertical-align: middle;"><xsl:value-of select="@fax"/></td>
		</tr>
		</xsl:if>
	</table>
</xsl:template>


</xsl:stylesheet>

