<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Site Core: Meta Tags
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
>

<xsl:template name="meta_tags">
	<!-- META TAGS   BEGIN -->

	<xsl:if test ="@category='not_found'">
		<META name="robots" content="noindex, nofollow" />
	</xsl:if>
  
	<xsl:if test="@category='popup'">
		<META http-equiv="expires" content="Mon, 01 Jan 1990 00:00:00 GMT"></META>
	</xsl:if>

	<xsl:if test="negeso:title">
		<META name="title" content="{negeso:title/text()}"/>
	</xsl:if>
	
	<xsl:if test="negeso:meta[@name='description']">
		<META name="description" content="{negeso:meta[@name='description']/text()}"/>
	</xsl:if>
	
	<xsl:if test="negeso:meta[@name='keywords']">
		<META name="keywords" content="{negeso:meta[@name='keywords']/text()}"></META>
	</xsl:if>
	
	<xsl:if test="//negeso:redirection">
		<script>window.location.href = '<xsl:value-of select="//negeso:redirection"/>'</script>
		<META HTTP-EQUIV="Refresh">
			 <xsl:attribute name="content">
			 	0; url=<xsl:value-of select="//negeso:redirection"/>
			 </xsl:attribute>
		</META>
	</xsl:if>

	<META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<META HTTP-EQUIV="CONTENT-LANGUAGE" CONTENT = "{//negeso:page/@lang}"/>
	<!-- META TAGS   END -->
	
</xsl:template>

<!-- GOOGLE CONVERSION and Additional Page Property -->
<xsl:template name="google-conversion-additional-page-property">
	<!-- GOOGLE CONVERSION START  -->
	<xsl:if test="negeso:meta[@name='google_script']">
		<xsl:value-of select="negeso:meta[@name='google_script']/text()" disable-output-escaping='yes'/>
	</xsl:if>
	<!-- GOOGLE CONVERSION END -->
	
	<!-- Additional Page Property  BEGIN -->
	<xsl:if test="negeso:meta[not(@name='description' or @name='keywords' or @name='google_script')]">
		<xsl:value-of select="negeso:meta[not(@name='description' or @name='keywords' or @name='google_script')]/text()" disable-output-escaping='yes'/>
	</xsl:if>
	<!-- Additional Page Property  END -->
</xsl:template>
	
</xsl:stylesheet>	