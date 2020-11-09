<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for News Module (Frontpage News)
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
  @author		Nikolay Koval
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java"
>

<xsl:variable name="dict_news_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('news_module', $lang)"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>

<xsl:template match="negeso:list[@type='newslist']" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/news/css/newsline.css"/>
	<script type="text/javascript" src="/site/modules/news/script/news.js">/**/</script>
	<!-- Re-defining delete confirmation message -->
	<script type="text/javascript">
		var s_DeleteConfirmation = "<xsl:value-of select="java:getString($dict_common, 'DELETE_CONFIRMATION')"/>";
    </script>
</xsl:template>

<xsl:template match="negeso:contents/negeso:listItem" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/news/css/newsline.css"/>
	<script type="text/javascript" src="/site/modules/news/script/news.js">/**/</script>
	<!-- Re-defining delete confirmation message -->
	<script type="text/javascript">
		var s_DeleteConfirmation = "<xsl:value-of select="java:getString($dict_common, 'DELETE_CONFIRMATION')"/>";
    </script>
</xsl:template>

<!--========================================== NEWS LISTS BEGIN =================================-->
<xsl:template match="negeso:list" mode="news">
	<!-- Displaying news list at the frontpage -->
	<xsl:choose>
		<xsl:when test="count(descendant::negeso:listItem) &gt; 0">
			<xsl:apply-templates select="negeso:listItem" mode="news" />
		</xsl:when>
		<xsl:otherwise>&#160;</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="negeso:listItem" mode="news">
<xsl:if test="$outputType = 'admin'and position()=1">
    <div class="editDiv">
				<input class="editBTN" type="button" onclick="editNewsList('{@listId}')" value="{java:getString($dict_common, 'CORE.ADMIN_EDIT_LIST')}">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_common"/>
						<xsl:with-param name ="name"  select="'CORE.ADMIN_EDIT_LIST'"/>
					</xsl:call-template>
				</input>
				<input class="editBTN" type="button" onclick="addNewsListItem('{@listId}')" value="{java:getString($dict_common, 'CORE.ADMIN_ADD_LIST_ITEM')}">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_common"/>
						<xsl:with-param name ="name"  select="'CORE.ADMIN_ADD_LIST_ITEM'"/>
					</xsl:call-template>
				</input>
			</div>
</xsl:if>

<div id="short_news_{@listId}_{@id}" class="b-newsShort">
    <h1>
        <a>
            <xsl:attribute name="href">
                <xsl:choose>
                    <xsl:when test="@url"><xsl:value-of select="$adminPath"/>/<xsl:value-of select="@url"/>.html</xsl:when>
                    <xsl:otherwise>?id=<xsl:value-of select="@id"/></xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="@title" disable-output-escaping="yes"/>
        </a>
    </h1>
			<!-- New article call instead of mode="safe" -->
			<xsl:apply-templates select="negeso:teaser/negeso:article">
				<xsl:with-param name="mode">user</xsl:with-param>
			</xsl:apply-templates>

    <xsl:if test="$outputType = 'admin'">
        <input class="editBTN bl-left" type="button" onclick="editNewsListItem('{@listId}', '{@id}')" value="{java:getString($dict_common, 'CORE.ADMIN_EDIT')}" >
				<xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_common"/>
                <xsl:with-param name ="name"  select="'CORE.ADMIN_EDIT'"/>
            </xsl:call-template>
        </input>
        <input class="editBTN bl-left" type="button" onclick="deleteNewsListItem('{@listId}', '{@id}')" value="{java:getString($dict_common, 'CORE.ADMIN_DELETE')}">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_common"/>
                <xsl:with-param name ="name"  select="'CORE.ADMIN_DELETE'"/>
            </xsl:call-template>
        </input>
    </xsl:if>
    <a href="?id={@id}" onfocus="blur()">
        <xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_news_module"/>
					<xsl:with-param name ="name"  select="'NEWS_MORE'"/>
				</xsl:call-template>
				<span class="arial">&#160;&#160;&gt;&gt;&#160;&#160;</span>
				<xsl:value-of select="java:getString($dict_news_module, 'NEWS_MORE')"/>
			</a>
		<xsl:if test="@href">
        <a href="{@href}" onfocus="blur()">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_news_module"/>
						<xsl:with-param name ="name"  select="'NEWS_LINK_TO_PAGE'"/>
					</xsl:call-template>
					<span class="arial">&#160;&#160;&gt;&gt;&#160;&#160;</span>
					<xsl:value-of select="java:getString($dict_news_module, 'NEWS_LINK_TO_PAGE')"/>
				</a>
    </xsl:if>

</div>

</xsl:template>

<!--========================================== NEWS LISTS END =================================-->
<xsl:template match="negeso:listItem" mode="news_details_front">
<div class="b-newsDetails">
    <h1><xsl:value-of select="@title" /></h1>

			<xsl:if test="@href">
        <a href="{@href}" onfocus="blur()">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_news_module"/>
							<xsl:with-param name ="name"  select="'NEWS_LINK_TO_PAGE'"/>
						</xsl:call-template>
						<span class="arial">&#160;&#160;&gt;&gt;&#160;&#160;</span>
						<xsl:value-of select="java:getString($dict_news_module, 'NEWS_LINK_TO_PAGE')"/>
					</a>
			</xsl:if>
</div>

<xsl:apply-templates select="negeso:details/negeso:article">
    <xsl:with-param name="mode">user</xsl:with-param>
</xsl:apply-templates>

<input type="button" class="submit" onfocus="blur()">
    <xsl:attribute name="onclick">window.location='<xsl:value-of select="//negeso:filename/text()" />'</xsl:attribute>
    <xsl:attribute name="value">
        <xsl:value-of select="java:getString($dict_news_module, 'NEWS_BACK_TO_NEWS_LIST')"/>
    </xsl:attribute>
    <xsl:call-template name ="add-constant-info">
        <xsl:with-param name ="dict"  select="$dict_news_module"/>
        <xsl:with-param name ="name"  select="'NEWS_BACK_TO_NEWS_LIST'"/>
    </xsl:call-template>
</input>
</xsl:template>

<xsl:template match="negeso:listItem" mode="news_details_search">
<div class="b-newsSearch">
    <h1><xsl:value-of select="@title" disable-output-escaping="yes" /></h1>

    <!-- New article call instead of mode="safe" -->
			<xsl:apply-templates select="negeso:details/negeso:article">
				<xsl:with-param name="mode">user</xsl:with-param>
			</xsl:apply-templates>

			<input type="button" class="submit" onfocus="blur()">
				<xsl:attribute name="onclick">
            <!-- Search link is defined in core/default_links.xsl -->
            window.location='<xsl:value-of select="$search_link" />?mode=advanced';
				</xsl:attribute>
				<xsl:attribute name="value">
            <xsl:value-of select="java:getString($dict_news_module, 'NEWS_BACK_TO_SEARCH')"/>
				</xsl:attribute>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_news_module"/>
            <xsl:with-param name ="name"  select="'NEWS_BACK_TO_SEARCH'"/>
				</xsl:call-template>
			</input>
</div>
<br/>
</xsl:template>

<!-- *************************************NegesoForm******************************* -->

</xsl:stylesheet>
