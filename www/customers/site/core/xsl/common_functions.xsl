<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL common functions for Site Core
 
  @version		2008.01.02
  @author		Rostislav 'KOTT' Brizgunov
-->

<xsl:stylesheet version="1.0" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:negeso="http://negeso.com/2003/Framework">

<!-- This function returns substring before last occurance of 'before' substring in the 'string' source -->
<xsl:template name="substring-before-last">
	<!-- Current tag: unspecified -->
	<xsl:param name="string" />
	<xsl:param name="before" /><xsl:value-of
		select="substring-before($string, $before)"
	/><xsl:if test="contains(substring-after($string, $before), $before)">
		<xsl:value-of select="$before"/>
	<xsl:call-template name="substring-before-last">
			<xsl:with-param name="string" select="substring-after($string, $before)" />
			<xsl:with-param name="before" select="$before" />
		</xsl:call-template>
	</xsl:if>
</xsl:template>

<!-- This function returns 'string' with all 'from' substrings replaced with 'to'. i.e.: SUBJ -->
<xsl:template name="replace">
	<!-- Current tag: unspecified -->
	<xsl:param name="string" />
	<xsl:param name="from" />
	<xsl:param name="to" />
	<xsl:value-of select="substring-before($string, $from)" disable-output-escaping="yes"/>
	<xsl:choose>
		<xsl:when test="contains($string, $from)">
			<xsl:value-of select="$to" disable-output-escaping="yes"/>
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="substring-after($string, $from)" />
				<xsl:with-param name="from" select="$from" />
				<xsl:with-param name="to" select="$to" />
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$string" disable-output-escaping="yes"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- This function make escaping of chars '"' -->
<xsl:template name="escapeQuote">
	<xsl:param name="string" />
	<xsl:call-template name="replace">
		<xsl:with-param name="string" select="$string" />
		<xsl:with-param name="from">"</xsl:with-param>
		<xsl:with-param name="to">\"</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<!-- This function returns hash of the URL. Example: URL==negeso.com?someId=118&anotherId=89 Hash==someId=118&anotherId=89 -->
<xsl:template name="url-hash">
	<!-- Current tag: unspecified 
	Uses $param variable - defined in the page.xsl -->
	<xsl:for-each select="$param[not(contains(@name,'REQUEST')) and @name!='filename' and @name!='switchToLang']">
		<xsl:if test="position()>1">&amp;</xsl:if>
		<xsl:value-of select="@name" />=<xsl:value-of select="negeso:value/text()" />
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>