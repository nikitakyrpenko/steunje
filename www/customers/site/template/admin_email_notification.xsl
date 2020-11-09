<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  ADMIN_EMAIL_NOTIFICATION text template

  @version      $Revision$
  @author       Alexander Serbin
-->

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
>

<xsl:template match="negeso:body">
	<xsl:choose>
		<xsl:when test="@lang='nl'">				
			xsl for nl absent
		</xsl:when>		
		<xsl:otherwise>
	Negeso Event Subscribtion

	<xsl:value-of select="@userName" /> has subscribed for <xsl:value-of select="@eventName" />.

	User information:
	<!--Company <xsl:value-of select="@companyName" />-->
		User name: <xsl:value-of select="@userName" />
		Subscribed people amount: <xsl:value-of select="@peopleAmount" />	

	<!--Disclaimer: <xsl:value-of select="//@disclaimer" />-->

		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
