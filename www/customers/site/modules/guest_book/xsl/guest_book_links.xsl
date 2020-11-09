<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2008 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Guest Book
 
  @version		2008.01.11
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:template name="gb_form_action">
	<!-- choose page to submit data when you are posting the message from GB -->
	<!-- THIS LINK MAY DEPEND ON PAGE OR ON LANGUAGE - ACCORDING TO YOUR NEEDS -->
	<xsl:attribute name="action">
		<xsl:choose>
			<xsl:when test="/negeso:page/@class='forum_form'">
				<xsl:text>forum.html</xsl:text>
			</xsl:when>
			<xsl:when test="(/negeso:page/@class='post_gb_message')">
				<xsl:call-template name="gb_link"/>
			</xsl:when>
		</xsl:choose>
	</xsl:attribute>
</xsl:template>

<xsl:template name="gb_link" >
	<xsl:text>guestbook_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
</xsl:template>

<xsl:template name="gb_post_page">
	<!-- choose page where the message post form placed -->
	<xsl:text>post_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
</xsl:template>

</xsl:stylesheet>
