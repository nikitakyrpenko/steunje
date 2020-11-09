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


<xsl:template match="negeso:page">
	<xsl:for-each select="negeso:mail_to_a_friend">
		<h1>
			<xsl:choose>
				<xsl:when test="/negeso:page/@lang='nl'">Hallo,<br/>Uw vriend/vriendin:&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='de'">Hallo, Ihr Freund &#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='fr'">Bonjour, votre ami&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='es'">Hola, su amigo&#160;</xsl:when>
				<xsl:otherwise>Hello,<br/>Your friend&#160;</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="@sender_name"/>, <xsl:value-of select="@sender_email"/>
			<xsl:choose>
				<xsl:when test="/negeso:page/@lang='nl'">stuurt u het volgende bericht:</xsl:when>
				<xsl:when test="/negeso:page/@lang='de'">schickt Ihnen diese Nachricht.</xsl:when>
				<xsl:when test="/negeso:page/@lang='fr'">vous a envoyé ce message.</xsl:when>
				<xsl:when test="/negeso:page/@lang='es'">le envía este mensaje.</xsl:when>
				<xsl:otherwise>sends you the following message:</xsl:otherwise>
			</xsl:choose>
			</h1>
	    <![CDATA[
	    
		]]>
		<h1>
			<xsl:choose>
				<xsl:when test="/negeso:page/@lang='nl'">Bericht:&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='de'">Nachrichtentext:&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='fr'">Message:&#160;</xsl:when>
				<xsl:when test="/negeso:page/@lang='es'">Texto del mensaje:&#160;</xsl:when>
				<xsl:otherwise>Message:&#160;</xsl:otherwise>
			</xsl:choose>			
			<pre><xsl:value-of select="text()" disable-output-escaping="yes"/></pre></h1>
	    <![CDATA[
	    
		]]>
		<pre>Site: <xsl:value-of select="@site_link"/></pre>
	</xsl:for-each>
</xsl:template>


<xsl:template match="negeso:page2">
	<xsl:for-each select="negeso:mail_to_a_friend">
		<h1>Hello, your friend <xsl:value-of select="@sender_name"/>, <xsl:value-of select="@sender_email"/> send you this message.</h1>
	    <![CDATA[
	    
		]]>
		<h1>Message text: <pre><xsl:value-of select="text()" disable-output-escaping="yes"/></pre></h1>
	    <![CDATA[
	    
		]]>
		<pre>Site: <xsl:value-of select="@site_link"/></pre>
	</xsl:for-each>
</xsl:template>


</xsl:stylesheet>