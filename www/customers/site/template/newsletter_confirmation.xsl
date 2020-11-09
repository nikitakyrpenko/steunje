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
-->

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
>

<xsl:output method="html"/>

<xsl:template match="negeso:page">
	<html>
	<head>
		<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	    <title>Newsletter confirmation</title>
	</head>
	<body style="font: 12px Arial; color: #114b9f;">
		<br/>
		<h2 style="font: 12px Arial; color: #114b9f; font-variant: small-caps;">
			Hello, <xsl:value-of select="@userName"/>!  
		</h2>
		<br/>
		You've been subscribed to Negeso newsletter. To activate your subscription please follow this link:
		<a href="{@confirmationLink}"><xsl:value-of select="@confirmationLink" /></a> 
		
		<br/><br/><br/>
		
		<a class="font: 12px Arial;color: #114b9f;" href="{@siteLink}">
			<xsl:value-of select="@siteLink"/>
		</a>
		<br/>
	</body>
	</html>
</xsl:template>

</xsl:stylesheet>