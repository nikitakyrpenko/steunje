<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  Product Module customer registration email template

  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
>

<xsl:output method="html"/>

<xsl:template match="negeso:pm-account-activation" >
	<html>
	<head>
		<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	    <title>Activatie mail</title>
	</head>
	<body style="font: 12px Arial; color: #114b9f;">
		<h2 style="font: 12px Arial; color: #114b9f; font-variant: small-caps;">
			U heeft zich voor de Negeso webshop geregistreerd.
		</h2>
		<br/>
		<xsl:for-each select="negeso:customer">
		<P class="font: 12px Arial;color: #114b9f;">
			Login: <xsl:value-of select="@login"/>
		</P>
		<P class="font: 12px Arial;color: #114b9f;">
			<xsl:if test="count(@name)>0">
				Naam: <xsl:value-of select="@name"/>
			</xsl:if>		
		</P>
		</xsl:for-each>
		<P class="font: 12px Arial;color: #114b9f;">
		Om uw account te activeren klik op de volgende link:
		</P>
		<a class="font: 12px Arial;color: #114b9f;" href="{@activation-link}">
			<xsl:value-of select="@activation-link"/>
		</a>
	</body>
	</html>
</xsl:template>

</xsl:stylesheet>

