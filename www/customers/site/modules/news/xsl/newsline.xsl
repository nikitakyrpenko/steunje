<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for News Module (Newsline)
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">



<xsl:variable name="dict_newsline_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('news_module', $lang)"/>

<xsl:template match="negeso:list" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/news/css/newsline.css?v=1"/>
</xsl:template>

<!--========================================== NEWSLINE BEGIN===========================================-->
<!-- Newsline: news list -->
<xsl:template match="negeso:list" mode="newsline">

				<!-- *************** Only for admin - do not change - begin ******************** -->
				<div>
					<xsl:if test="$outputType = 'admin' and @type='newslist'">
				      <xsl:apply-templates select="." mode="admin_part_entry_point">
				          <xsl:with-param name="type" select="2"/>
				      </xsl:apply-templates>
					</xsl:if>
				</div>
				<!-- *************** Only for admin - do not change - end ******************** -->
	<h1 class="newslineShort_name">Komende acties</h1>
		<xsl:apply-templates select="negeso:listItem" mode="newsline" />
</xsl:template>

<!-- Newsline: news list item -->

<xsl:template match="negeso:listItem" mode="newsline">

    <div class="b-newslineShort">

		<div class="b-newslineShort__wrapper">
			<div class="newslineShort__img">

				<xsl:if test="@imageLink">
					<!-- for image position change CSS class -->
					<a href="?id={@id}&amp;page={//negeso:list/@page}" onfocus="blur()" class="b-newsShort-Img">
						<img class="newslineImage" src="{@imageLink}"  alt=""/>
					</a>
				</xsl:if>
			</div>
			<div class="newslineShort__text">

				<h1>
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</h1>

						<xsl:apply-templates select="negeso:teaser/negeso:article">
							<xsl:with-param name="mode">user</xsl:with-param>
						</xsl:apply-templates>


				<div class="newslineShort__data-link">

					<xsl:if test="@viewDate">
						<div class="viewDateFront">
							<xsl:value-of select="substring(@viewDate, 0,11)"/>
						</div>
					</xsl:if>

					<a href="?id={@id}&amp;page={//negeso:list/@page}" onfocus="blur()" class="b-newsShort-Link">
						<xsl:attribute name="href">
							<xsl:choose>
								<xsl:when test="@url"><xsl:value-of select="$adminPath"/>/<xsl:value-of select="@url"/>.html</xsl:when>
								<xsl:otherwise>?id=<xsl:value-of select="@id"/>&amp;page=<xsl:value-of select="//negeso:list/@page"/></xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:value-of select="java:getString($dict_newsline_module, 'NLINE_MORE')"/>&#160;&#062;&#062;&#160;
					</a>

				</div>

				<xsl:if test="@href">
					<a onfocus="blur()" class="b-newsShort-Link">
						<xsl:attribute name="href">
							<xsl:value-of select="@href"/>
						</xsl:attribute>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_newsline_module"/>
							<xsl:with-param name ="name"  select="'NLINE_PAGELINK'"/>
						</xsl:call-template>
						&#062;&#062;&#062;&#160;
						<xsl:value-of select="java:getString($dict_newsline_module, 'NLINE_PAGELINK')"/>
					</a>
				</xsl:if>

			</div>

		</div>

	</div>

</xsl:template>



<!---       Newsline: news details            -->


<xsl:template match="negeso:listItem" mode="newsline_details">
    <div class="b-newslineDetails">

		<div class="newslineDetails__img">

			<div class="item-image">
				<xsl:choose>
					<xsl:when test="@imageLink">

						<img class="newsImg" src="{@imageLink}"  alt=""/>

					</xsl:when>
					<xsl:otherwise>

						<img class="newsImg"  src="/media/nieus1.png"  alt=""/>

					</xsl:otherwise>
				</xsl:choose>
			</div>
		</div>

		<div class="newslineDetails__text">
			<h1>
				<xsl:value-of select="@title" disable-output-escaping="yes"/>
			</h1>

			<xsl:apply-templates select="negeso:details/negeso:article">
				<xsl:with-param name="mode">user</xsl:with-param>
			</xsl:apply-templates>


			<input type="button" class="submit" onfocus="blur()">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_newsline_module"/>
					<xsl:with-param name ="name"  select="'NEWS_BACK_TO_NEWS_LIST'"/>
				</xsl:call-template>
				<xsl:attribute name="onclick">
					window.location='<xsl:value-of select="//negeso:filename/text()" />'
				</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:value-of select="java:getString($dict_news_module, 'NEWS_BACK_TO_NEWS_LIST')"/>
				</xsl:attribute>
			</input>

		</div>


	</div>
</xsl:template>

<!-- PAGE NAVIGATOR START -->
<xsl:template match="negeso:list" mode="paging">
	<!-- CSS styles are inside page.css -->
	<!-- 'paging_style' can be 'old' or 'new' -->
	<xsl:param name="paging_style">old</xsl:param>

	<xsl:choose>
		<xsl:when test="$paging_style='new'">
			<xsl:if test="@pages &gt; 1">
				<script type="text/javascript" src="/site/core/script/paging.js">/**/</script>
                <div class="b-newslinePaging-New">

						<div class="pagign_wrapper">

							<div class="#">

								<xsl:if test="@page &gt; 1">
									<a class="pagign_prev_link" href="?page={number(@page)-1}" disable-output-escaping="no">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_newsline_module"/>
											<xsl:with-param name ="name"  select="'NLINE_PREV'"/>
										</xsl:call-template>
										&lt;&lt;&#160;<xsl:value-of select="java:getString($dict_newsline_module, 'NLINE_PREV')"/>
									</a>
								</xsl:if>


							</div>

							<div class="number_page"><form method="get">
								<input type="text" name="page" value="{@page}" onkeyup="handle_paging(event, this, {@pages})" title="LEFT ARROW = page-1; RIGHT ARROW = page+1; ENTER BUTTON = Go!"/>
							</form>
								/&#160;<xsl:value-of select="@pages" />
							</div>

								<div class="#">
									<xsl:if test="@page &lt; @pages">

										<a class="pagign_next_link" href="?page={number(@page)+1}" disable-output-escaping="no" onfocus="blur()">
											<xsl:call-template name ="add-constant-info">
												<xsl:with-param name ="dict"  select="$dict_newsline_module"/>
												<xsl:with-param name ="name"  select="'NLINE_NEXT'"/>
											</xsl:call-template>
											<xsl:value-of select="java:getString($dict_newsline_module, 'NLINE_NEXT')"/>&#160;&gt;&gt;
										</a>

									</xsl:if>
								</div>


						</div>
				</div>
			</xsl:if>
		</xsl:when>


		<xsl:when test="$paging_style='old'">
			<xsl:if test="@pages &gt; 1">
                <div class="b-newslinePaging-Old">
					<xsl:if test="number(@page) &gt; 4">
                        <a href="?page={number(@page) - 4}" disable-output-escaping="no" onfocus="blur()">
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_newsline_module"/>
								<xsl:with-param name ="name"  select="'NLINE_PREV'"/>
							</xsl:call-template>
							<xsl:text>&#60;&#60;&#60;&#160;</xsl:text>
							<xsl:value-of select="java:getString($dict_newsline_module, 'NLINE_PREV')"/>
						</a>
					</xsl:if>
					<xsl:variable name="start">
						<xsl:choose>
							<xsl:when test="number(@page) &gt; 4">
								<xsl:value-of select="number(@page) - 4" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="1" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="count">
						<xsl:choose>
							<xsl:when test="number($start) + 6 &lt; number(@pages)">
								<xsl:value-of select="7" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="number(@pages) - number($start) + 1" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:call-template name="newsline_pages">
						<xsl:with-param name="count" select="$count" />
						<xsl:with-param name="from" select="$start" />
						<xsl:with-param name="current" select="@page" />
					</xsl:call-template>
					<xsl:if test="(number(@pages) - number(@page)) &gt; 3">
                        <a href="?page={number(@page) + 4}" disable-output-escaping="no" onfocus="blur()">
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_newsline_module"/>
								<xsl:with-param name ="name"  select="'NLINE_NEXT'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_newsline_module, 'NLINE_NEXT')"/>
							<xsl:text>&#160;&#62;&#62;&#62;</xsl:text>
						</a>
					</xsl:if>
				</div>
			</xsl:if>
		</xsl:when>
	</xsl:choose>
</xsl:template>

<!-- for OLD paging -->
<xsl:template name="newsline_pages">
	<!-- current tag negeso:list -->
	<xsl:param name="count" />
	<xsl:param name="from" />
	<xsl:param name="current" />

    <a href="?page={$from}" onfocus="blur()">
		<xsl:if test="number($from) = number($current)">
			<xsl:attribute name="class">current_page</xsl:attribute>
		</xsl:if>
		<xsl:value-of select="$from" />
	</a>

	<xsl:if test="number($count) &gt; 1">
		<xsl:call-template name="newsline_pages">
			<xsl:with-param name="count" select="number($count) - 1" />
			<xsl:with-param name="from" select="number($from) + 1" />
			<xsl:with-param name="current" select="$current" />
		</xsl:call-template>
	</xsl:if>
</xsl:template>
<!-- PAGE NAVIGATOR END -->

<!--========================================== NEWSLINE END===========================================-->

</xsl:stylesheet>
