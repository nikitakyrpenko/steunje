<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Site Core
 
  @version		2008.01.17
  @author		Rostislav 'KOTT' Brizgunov

-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java"
>

<xsl:variable name="home_link"><xsl:value-of select="$adminPath"/>/index_<xsl:value-of select="$lang" />.html</xsl:variable>
<xsl:variable name="about_us_link"><xsl:value-of select="$adminPath"/>/about_us_<xsl:value-of select="$lang" />.html</xsl:variable>
<xsl:variable name="search_link"><xsl:value-of select="$adminPath"/>/search_<xsl:value-of select="$lang" />.html</xsl:variable>
<xsl:variable name="sitemap_link"><xsl:value-of select="$adminPath"/>/sitemap_<xsl:value-of select="$lang" />.html</xsl:variable>
<xsl:variable name="login_link"><xsl:value-of select="$adminPath"/>/login_<xsl:value-of select="$lang" />.html</xsl:variable>
<xsl:variable name="error_link"><xsl:value-of select="$adminPath"/>/error_<xsl:value-of select="$lang" />.html</xsl:variable>
<xsl:variable name="disclaimer_link"><xsl:value-of select="$adminPath"/>/disclaimer_<xsl:value-of select="$lang" />.html</xsl:variable>
<xsl:variable name="privacy_link"><xsl:value-of select="$adminPath"/>/privacy_<xsl:value-of select="$lang" />.html</xsl:variable>
<xsl:variable name="colofon_link"><xsl:value-of select="$adminPath"/>/colofon_<xsl:value-of select="$lang" />.html</xsl:variable>
<xsl:variable name="negeso_link">http://www.negeso.com</xsl:variable>
<xsl:variable name="mail_to_friend"><xsl:value-of select="$adminPath"/>/mail_to_a_friend_<xsl:value-of select="$lang" />.html?page_link=<xsl:value-of select="negeso:page/negeso:request/negeso:parameter[@name='filename']/negeso:value/text()"/>&amp;page_title=<xsl:value-of select="negeso:page/negeso:title/text()"/></xsl:variable>


<xsl:template name="home_link">
	<xsl:param name="lang" /><xsl:value-of select="$adminPath"/>/
	<xsl:text>index_</xsl:text><xsl:value-of select="$lang" /><xsl:text>.html</xsl:text>
</xsl:template>

<!-- This template is special for [xsl:template name="flags"] -->
<xsl:template name="cur_page_link">
	<!-- current tag: negeso:contents -->
	<xsl:param name="lang_code" />
	<xsl:choose>
		<xsl:when test="contains(/negeso:page/negeso:filename/text(),concat('_', $lang, '.html'))">
			<xsl:call-template name="substring-before-last">
				<xsl:with-param name="string" select="/negeso:page/negeso:filename/text()" />
				<xsl:with-param name="before" select="concat('_', $lang, '.html')" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="substring-before-last">
				<xsl:with-param name="string" select="/negeso:page/negeso:filename/text()" />
				<xsl:with-param name="before" select="'.html'" />
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>_<xsl:value-of select="$lang_code"/>.html?switchToLang=<xsl:value-of select="$lang_code"/>&amp;<xsl:call-template name="url-hash" />
</xsl:template>

</xsl:stylesheet>