<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Site Core
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
>

<xsl:template match="negeso:article">
	<xsl:param name="classRTE" select="'contentStyle'"/>
	<xsl:param name="mode" select="$outputType"/>
    <xsl:variable name ="head" select ="negeso:head" />
	<!-- *************** Only for admin - do not change - begin ******************** -->
	<xsl:if test="$mode='admin'">
		<img src="/images/mark_1.gif" class="RTEEntryPoint" onclick="RTE_Init('article_text{@id}','article_text{@id}', {@id}, 3, 0, '{$classRTE}', getInterfaceLanguage());" alt="Editor">
			<xsl:if test="@last_modified"><xsl:attribute name ="alt">Editor (last modified: <xsl:value-of select="@last_modified"/>)</xsl:attribute></xsl:if>
		</img>
	</xsl:if>
	<!-- *************** Only for admin - do not change - end ******************** -->
	
	<div class="contentStyle">
		<xsl:attribute name="class"><xsl:value-of select="$classRTE"/></xsl:attribute>		
		<xsl:if test="$mode = 'admin'">
			<xsl:attribute name="id">article_text<xsl:value-of select="@id" /></xsl:attribute>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="negeso:text/text()">
				
				<xsl:variable name="replaceUrl">
   					<xsl:call-template name="replace_page_const">
	      				<xsl:with-param name="text" select="negeso:text" />
	      				<xsl:with-param name="replace" select="'%core.page.url%'" />
	      				<xsl:with-param name="by" select="/negeso:page/negeso:request/negeso:parameter[@name='REQUEST_URL']" />
    				</xsl:call-template>
 				</xsl:variable>
 				
 				<xsl:variable name="replaceURlAndTitle">
   					<xsl:call-template name="replace_page_const">
	      				<xsl:with-param name="text" select="$replaceUrl" />
	      				<xsl:with-param name="replace" select="'%core.page.title%'" />
	      				<xsl:with-param name="by" select="/negeso:page/negeso:title" />
    				</xsl:call-template>
 				</xsl:variable>
			
				
				<xsl:value-of select="$replaceURlAndTitle" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</div>
	<xsl:if test="$mode = 'admin'">
		<script type="text/javascript">
			//makeReadonly(document.getElementById('article_text<xsl:value-of select="@id" />'), true);
			//addTestFormButton(document.getElementById('article_text<xsl:value-of select="@id" />'));
		</script>
	</xsl:if>
</xsl:template>


 <xsl:template name="replace_page_const">
    <xsl:param name="text" />
    <xsl:param name="replace" />
    <xsl:param name="by" />
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
        <xsl:value-of select="substring-before($text,$replace)" />
        <xsl:value-of select="$by" />
        <xsl:call-template name="replace_page_const">
          <xsl:with-param name="text"
          select="substring-after($text,$replace)" />
          <xsl:with-param name="replace" select="$replace" />
          <xsl:with-param name="by" select="$by" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>