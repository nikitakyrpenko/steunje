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
	    <title>
			<xsl:choose>
				<xsl:when test="/negeso:page/@lang='nl'">Stuur naar een vriend(in):&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='de'">Email an einen Freund weiterleiten:&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='fr'">Envoyer &#224; un ami:&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='es'">Enviar por e-mail a un amigo:&#160;</xsl:when>
				<xsl:otherwise>Mail-to-a-friend:</xsl:otherwise>
			</xsl:choose>
		</title>
	</head>
	<body style="font: 12px Arial; color: #114b9f;">
			
		<br/>
		<xsl:for-each select="negeso:mail_to_a_friend">
			<h2 style="font: 12px Arial; color: #114b9f; font-variant: small-caps;">
			<xsl:choose>
				<xsl:when test="/negeso:page/@lang='nl'">Hallo,<br/>Uw vriend/vriendin:&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='de'">Hallo, Ihr Freund &#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='fr'">Bonjour, votre ami&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='es'">Hola, su amigo&#160;</xsl:when>
				<xsl:otherwise>Hello,<br/>Your friend&#160;</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="@sender_name"/>,  
				<xsl:value-of select="@sender_email"/> 
			<xsl:choose>
				<xsl:when test="/negeso:page/@lang='nl'">stuurt u het volgende bericht:</xsl:when>
				<xsl:when test="/negeso:page/@lang='de'">schickt Ihnen diese Nachricht.</xsl:when>
				<xsl:when test="/negeso:page/@lang='fr'">vous a envoyé ce message.</xsl:when>
				<xsl:when test="/negeso:page/@lang='es'">le envía este mensaje.</xsl:when>
				<xsl:otherwise>sends you the following message:</xsl:otherwise>
			</xsl:choose>
			
			</h2>
			<br/>
			<xsl:choose>
				<xsl:when test="/negeso:page/@lang='nl'">Bericht:&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='de'">Nachrichtentext:&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='fr'">Message:&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='es'">Texto del mensaje:&#160;</xsl:when>
				<xsl:otherwise>Message:&#160;</xsl:otherwise>
			</xsl:choose>
			<br/>
			<a href = "{@page_link}"><xsl:value-of select="@page_title"/></a> 
			<br/>
			<br/>
				<xsl:value-of select="text()"/>
			<br/>
			<br/>
			<a class="font: 12px Arial;color: #114b9f;" href="{@site_link}">
				<xsl:value-of select="@site_link"/>
			</a>
			<br/>
		</xsl:for-each>
	</body>
	</html>
</xsl:template>



<xsl:template match="negeso:page2">
	<html>
	<head>
		<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	    <title>Mail to a friend</title>
	</head>
	<body style="font: 12px Arial; color: #114b9f;">
			
		<br/>
		<xsl:for-each select="negeso:mail_to_a_friend">
			<h2 style="font: 12px Arial; color: #114b9f; font-variant: small-caps;">
				Hello, your friend <xsl:value-of select="@sender_name"/>,  
				<xsl:value-of select="@sender_email"/> send you this message. 
			</h2>
			<br/>
			Message text
			<br/>
				<xsl:value-of select="text()"/>
			<br/>

			<a class="font: 12px Arial;color: #114b9f;" href="{@site_link}">
				<xsl:value-of select="@site_link"/>
			</a>
			<br/>
		</xsl:for-each>
	</body>
	</html>
</xsl:template>

</xsl:stylesheet>