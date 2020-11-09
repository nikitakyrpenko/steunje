<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  MAIL_TO_A_FRIEND HTML template

  @version      $Revision$
  @author       Olexiy.Strashko
  @author       Volodymyr.Snigur
-->

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:negeso="http://negeso.com/2003/Framework"
>

<xsl:output method="html"/>

<xsl:template match="negeso:mtaf">
	<html>
	<head>
		<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	    <title>Mail to a friend</title>
	</head>
	<body style="font: 12px Arial; color: #114b9f;">
			
		<br/>
		<span style="font: 12px Arial; color: #114b9f; font-variant: small-caps;">
			Hello,<br/>your friend <a href="mailto:{@sender_email}" title="{@sender_email}"><xsl:value-of select="@sender_name"/></a> send you this product:
			<a href="{@product_link}"><xsl:value-of select="@product_link"/></a>
		</span>
		<br/>
		Message text:
		<br/>
			<xsl:value-of select="text()"/>
		<br/><br/><br/>

		Welcome to
		<a class="font: 12px Arial;color: #114b9f;" href="{@site_link}">
			<xsl:value-of select="@site_link"/>
		</a>
		<br/>
	</body>
	</html>
</xsl:template>

</xsl:stylesheet>