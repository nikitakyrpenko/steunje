<?xml version="1.0" encoding="UTF-8"?>
<!--

  Copyright (c) 2003 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  XSL template for marquee

  @version		2008.01.29
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java"
>

<xsl:variable name="dict_marquee" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('marquee', $lang)"/>

<xsl:template match="negeso:article[@class='marquee']" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/marquee/css/marquee.css"/>	
	<!-- script type="text/javascript" src="/site/modules/marquee/script/marquee.js">/**/</script -->
</xsl:template>

<xsl:template match="negeso:article[@class='marquee']">
    <span>
        <xsl:call-template name ="add-constant-info">
            <xsl:with-param name ="dict"  select="$dict_marquee"/>
            <xsl:with-param name ="name"  select="'MQ_TITLE'"/>
        </xsl:call-template>
    </span>
    <xsl:if test="$outputType = 'admin' and not(/negeso:page/@role-id = 'visitor')">
        <img src="/images/mark_1.gif" class="RTEEntryPoint" onclick="RTE_Init('article_text{@id}','article_text{@id}', {@id}, 3, 0, 'contentStyle', getInterfaceLanguage());" alt="Editor" />
        <xsl:text>&#160;</xsl:text>
    </xsl:if>
    <h1><xsl:value-of select="java:getString($dict_marquee, 'MQ_TITLE')"/></h1>
    <xsl:choose>
        <xsl:when test="$outputType = 'admin' and not(/negeso:page/@role-id = 'visitor')">
            <!-- This part was copied from article.xsl -->
            <div class="contentStyle" id="article_text{@id}">
                <xsl:choose>
                    <xsl:when test="negeso:text/text()">
                        <xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
                    </xsl:when>
                    <xsl:otherwise>&#160;</xsl:otherwise>
                </xsl:choose>
            </div>
            <script type="text/javascript">
                makeReadonly(document.getElementById('article_text<xsl:value-of select="@id" />'), true);
            </script>
        </xsl:when>
        <xsl:otherwise>
            <marquee>
                <xsl:value-of select="negeso:text/text()" disable-output-escaping="yes"/>
            </marquee>
        </xsl:otherwise>
												</xsl:choose>
</xsl:template>

</xsl:stylesheet>