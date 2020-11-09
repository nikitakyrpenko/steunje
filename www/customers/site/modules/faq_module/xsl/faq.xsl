<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2008 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for FAQ
 
  @version		2008.01.11
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java"
        >

<xsl:variable name="dict_faq" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('faq', $lang)"/>

<xsl:template match="negeso:list[@type='faqsimple' or @type='faqlink']" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/faq_module/css/faq.css"/>
</xsl:template>

<xsl:template match="negeso:list[@type='faqsimple' or @type='faqlink']">
									<xsl:if test="$outputType = 'admin' and not(/negeso:page/@role-id = 'visitor')">
										<img  src="/images/mark_1.gif" class="hand" align="absMiddle"
											onclick="window.open('?command=get-list-command&amp;listId={@id}&amp;listPath={@listPath}',
											'faqsimple',
											'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"/>&#160;
									</xsl:if>
									<span>
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_faq"/>
											<xsl:with-param name ="name"  select="'FAQ_TITLE'"/>
										</xsl:call-template>
									</span>
    <xsl:call-template name="page_title">
        <xsl:with-param name="title">
            <xsl:value-of select="java:getString($dict_faq, 'FAQ_TITLE')"/>&#160;&#160;&#160;
									<xsl:apply-templates select="negeso:listItem" mode="faq_categories"/>
        </xsl:with-param>
    </xsl:call-template>

							<xsl:if test="@type = 'faqlink'">
        <div class="b-faqLinks"><xsl:apply-templates select="negeso:listItem/negeso:list/negeso:listItem" mode="faq_list" /></div>
        <br/>
							</xsl:if>
    <xsl:apply-templates select="negeso:listItem/negeso:list/negeso:listItem" mode="faq_answers" />        
</xsl:template>

<xsl:template match="negeso:listItem" mode="faq_categories">
	<!-- LIST OF FAQ CATEGORY -->
	&#160;
		<xsl:choose>
			<xsl:when test="number(@id) = number(../@selected)">
				<xsl:value-of select="@title" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>
            <a href="?listId={../@id}&amp;listPath={@id}" onfocus="blur()"><xsl:value-of select="@title" disable-output-escaping="yes"/></a>
			</xsl:otherwise>
		</xsl:choose>
</xsl:template>

<xsl:template match="negeso:listItem" mode="faq_list">
    <a href="#faq{@id}" onfocus="blur()"><xsl:value-of select="@title" disable-output-escaping="yes"/></a>
	<br/>
</xsl:template>

<xsl:template match="negeso:listItem" mode="faq_answers">
	<a name="faq{@id}"/>
    <div class="b-faqAnswer">
        <a name="faq{@id}" onfocus="blur()">up</a>
		&#160;&#160;&#160;
        <h2><xsl:value-of select="@title" disable-output-escaping="yes"/></h2>

		<xsl:apply-templates select="negeso:teaser/negeso:article">
			<xsl:with-param name="mode">user</xsl:with-param>
		</xsl:apply-templates>
	</div>
    <br/>
</xsl:template>

</xsl:stylesheet>
