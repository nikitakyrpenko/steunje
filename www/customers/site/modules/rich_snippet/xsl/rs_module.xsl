<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright (c) 2004 Negeso Ukraine This software is the confidential and proprietary information of Negeso ("Confidential Information"). You shall 
	not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Negeso. XSL 
	templates for Banner module @version 2014.06.17 @author Mykola Lykhozhon -->


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">

	<xsl:template match="negeso:rich-snippets" mode="page_head">
		<link rel="stylesheet" type="text/css" href="/site/modules/rich_snippet/css/rich_snippet.css" />
		<script type="text/javascript" src="/site/modules/rich_snippet/script/rich_snippet.js">/**/</script>
	</xsl:template>

	<xsl:template match="negeso:rich-snippets" mode="moving-panel">
		<xsl:choose>
			<xsl:when test="negeso:rs-aggregate-review">
				<xsl:apply-templates select="negeso:rs-aggregate-review[1]" mode="conditional" />
			</xsl:when>
			<xsl:when test="negeso:rs-product">
				<xsl:apply-templates select="." mode="pm" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="." mode="moving-reviews" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="negeso:rs-aggregate-review" mode="conditional">
		<!-- to combine review and aggregate review product container is needed -->
		<div itemscope="" itemtype="http://schema.org/Product">
			<meta itemprop="name" content="{@name}" />
			<xsl:apply-templates select="." mode="simple">
				<xsl:with-param name="is-property" select="'true'" />
			</xsl:apply-templates>
			<xsl:apply-templates select=".." mode="moving-reviews">
				<xsl:with-param name="is-property" select="'true'" />
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<xsl:template match="negeso:rs-aggregate-review" mode="simple">
		<span itemscope="" itemtype="http://schema.org/AggregateRating" itemprop="aggregateRating">
			<xsl:if test="$outputType = 'admin'">
				<img src="/images/mark_1.gif" class="RTEEntryPoint" onclick="editRichSnippet({@id});">
					<xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'EDIT')" /></xsl:attribute>
				</img>
			</xsl:if>
			Overall rating
			<img width="13" height="12" src="/site/modules/rich_snippet/images/star_{@fiveBasedRating >= 1}_{@fiveBasedRating >= 1}.png" alt="" />
			<img width="13" height="12" src="/site/modules/rich_snippet/images/star_{@fiveBasedRating >= 1.5}_{@fiveBasedRating >= 1.75}.png" alt="" />
			<img width="13" height="12" src="/site/modules/rich_snippet/images/star_{@fiveBasedRating >= 2.5}_{@fiveBasedRating >= 2.75}.png" alt="" />
			<img width="13" height="12" src="/site/modules/rich_snippet/images/star_{@fiveBasedRating >= 3.5}_{@fiveBasedRating >= 3.75}.png" alt="" />
			<img width="13" height="12" src="/site/modules/rich_snippet/images/star_{@fiveBasedRating >= 4.5}_{@fiveBasedRating >= 4.75}.png" alt="" />
			<span itemprop="ratingValue">
				<xsl:value-of select="@ratingValue" />
			</span>
			<xsl:value-of select="'/ '" />
			<span itemprop="bestRating">
				<xsl:value-of select="@bestRating" />
			</span>
			<meta itemprop="worstRating" content="{@worstRating}" />
			based on
			<span itemprop="ratingCount">
				<xsl:value-of select="@ratingCount" />
			</span>
			reviews.
		</span>
	</xsl:template>

	<xsl:template match="negeso:rich-snippets" mode="moving-reviews">
		<xsl:param name="is-property" />
		<xsl:if test="negeso:rs-review">
			<div class="snippetsBody">
				<div class="snippetsArrow" id="snippetsPrev">
					<img src="/site/core/images/spacer.gif" alt="" width="40" height="40" />
				</div>
				<div class="snippetsContainer">
					<div class="snippetsArrow" id="snippetsNext">
						<img src="/site/core/images/spacer.gif" alt="" width="40" height="40" />
					</div>
					<div class="snippetsContent">
						<table cellSpacing="0" cellPadding="5" border="0">
							<tbody>
								<tr>
									<xsl:choose>
										<xsl:when test="$is-property='true'">
											<xsl:apply-templates select="negeso:rs-review" mode="moving-review-block">
												<xsl:with-param name="is-property" select="$is-property" />
												<xsl:with-param name="is-rich-snippet" select="'true'" />
											</xsl:apply-templates>
										</xsl:when>
										<xsl:otherwise>
											<xsl:apply-templates select="negeso:rs-review[1]" mode="moving-review-block">
												<xsl:with-param name="is-rich-snippet" select="'true'" />
											</xsl:apply-templates>
											<xsl:apply-templates select="negeso:rs-review[position()>1]" mode="moving-review-block">
												<xsl:with-param name="is-rich-snippet" select="'false'" />
											</xsl:apply-templates>
										</xsl:otherwise>
									</xsl:choose>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<script type="text/javascript">
					$(document).ready(function(){
					try{
					if ($('.snippetsBody').length > 0 )
					new snippets();
					}catch(e){}
					});
				</script>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="negeso:rs-review" mode="moving-review-block">
		<xsl:param name="is-property" />
		<xsl:param name="is-rich-snippet"></xsl:param>
		<td vAlign="top" style="padding: 5px 7px 5px !important;">
			<div>
				<xsl:if test="$is-rich-snippet='true'">
					<xsl:attribute name="itemtype">http://schema.org/Review</xsl:attribute>
					<xsl:attribute name="itemscope"></xsl:attribute>
					<xsl:if test="$is-property='true'">
						<xsl:attribute name="itemprop">review</xsl:attribute>
					</xsl:if>
				</xsl:if>
				<div class="snipTop pie_first-child">
					<p>
						<span style="FONT-SIZE: large; FONT-STYLE: italic">
							<em>
								<xsl:if test="$outputType = 'admin'">
									<img src="/images/mark_1.gif" class="RTEEntryPoint" onclick="editRichSnippet({@id});">
										<xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'EDIT')" /></xsl:attribute>
									</img>
								</xsl:if>
								<span>
									<xsl:if test="$is-rich-snippet='true'">
										<xsl:attribute name="itemprop">name</xsl:attribute>
									</xsl:if>
									<xsl:value-of select="'&quot;'" />
									<xsl:value-of select="@name" />
									<xsl:value-of select="'&quot;'" />
								</span>
							</em>
						</span>
					</p>
					<p>
						<br />
						<span>
							<xsl:if test="$is-rich-snippet='true'">
								<xsl:attribute name="itemprop">reviewRating</xsl:attribute>
								<xsl:attribute name="itemtype">http://schema.org/Rating</xsl:attribute>
								<xsl:attribute name="itemscope"></xsl:attribute>
								<meta content="{@rating}" itemprop="ratingValue" />
								<meta content="{@ratingScaleMin}" itemprop="worstRating" />
								<meta content="{@ratingScaleMax}" itemprop="bestRating" />
							</xsl:if>
							<img alt="" src="/site/modules/rich_snippet/images/star_{@fiveBasedRating >= 1}.png" width="18" height="18" />
							<img alt="" src="/site/modules/rich_snippet/images/star_{@fiveBasedRating >= 2}.png" width="18" height="18" />
							<img alt="" src="/site/modules/rich_snippet/images/star_{@fiveBasedRating >= 3}.png" width="18" height="18" />
							<img alt="" src="/site/modules/rich_snippet/images/star_{@fiveBasedRating >= 4}.png" width="18" height="18" />
							<img alt="" src="/site/modules/rich_snippet/images/star_{@fiveBasedRating >= 5}.png" width="18" height="18" />
						</span>
						<xsl:if test="$is-rich-snippet='true'">
							<meta content="{@datePublished}" itemprop="datePublished" />
						</xsl:if>
						<br />
						<span style="display:block;">
							<xsl:if test="$is-rich-snippet='true'">
								<xsl:attribute name="itemprop">itemReviewed</xsl:attribute>
								<xsl:attribute name="itemtype">http://schema.org/Thing</xsl:attribute>
								<xsl:attribute name="itemscope"></xsl:attribute>
								<meta content="{@reviewBody}" itemprop="name" />
							</xsl:if>
						</span>
						<span>
							<xsl:if test="$is-rich-snippet='true'">
								<xsl:attribute name="itemprop">description</xsl:attribute>
							</xsl:if>
							<xsl:value-of select="negeso:description/text()" />
						</span>
					</p>
				</div>
				<div class="snipsBottom">
					<span style="FONT-STYLE: italic">
						<xsl:if test="$is-rich-snippet='true'">
							<xsl:attribute name="itemprop">author</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="@author" />
					</span>
				</div>
			</div>
		</td>
	</xsl:template>

	<xsl:template match="negeso:rich-snippets" mode="pm">
		<xsl:choose>
			<xsl:when test="negeso:rs-product">
				<xsl:apply-templates select="negeso:rs-product[1]" mode="pm" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="negeso:rs-product" mode="pm">
		<div itemscope="" itemtype="http://schema.org/Product">
			<meta itemprop="name" content="{@name}" />
			<meta itemprop="brand" itemtype="http://schema.org/Organization" content="{@brand}" />
			<meta itemprop="manufacturer" itemtype="http://schema.org/Organization" content="{@manufacturer}" />
			<meta itemprop="model" content="{@model}" />
			<meta itemprop="productID" content="{@productId}" />
			<div itemprop="offers" itemscope="" itemtype="http://schema.org/Offer">
				<meta itemprop="price" content="{@price}" />
				<meta itemprop="availability" itemtype="http://schema.org/{@availability}" content="OutOfStock" />
				<meta itemprop="priceCurrency" content="EUR" />
				<link itemprop="itemCondition" itemtype="http://schema.org/{@condition}Condition" content="{@condition}" />
			</div>
			<span itemscope="" itemtype="http://schema.org/AggregateRating" itemprop="aggregateRating">
				<xsl:if test="$outputType = 'admin'">
					<img src="/images/mark_1.gif" class="RTEEntryPoint" onclick="editRichSnippet({@id});">
						<xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'EDIT')" /></xsl:attribute>
					</img>
				</xsl:if>
				Overall rating
				<img width="13" height="12" src="/site/modules/rich_snippet/images/star_{@averageRating >= 1}_{@averageRating >= 1}.png" alt="" />
				<img width="13" height="12" src="/site/modules/rich_snippet/images/star_{@averageRating >= 1.5}_{@averageRating >= 1.75}.png" alt="" />
				<img width="13" height="12" src="/site/modules/rich_snippet/images/star_{@averageRating >= 2.5}_{@averageRating >= 2.75}.png" alt="" />
				<img width="13" height="12" src="/site/modules/rich_snippet/images/star_{@averageRating >= 3.5}_{@averageRating >= 3.75}.png" alt="" />
				<img width="13" height="12" src="/site/modules/rich_snippet/images/star_{@averageRating >= 4.5}_{@averageRating >= 4.75}.png" alt="" />
				<span itemprop="ratingValue">
					<xsl:value-of select="@averageRating" />
				</span>
				based on
				<span itemprop="ratingCount">
					<xsl:value-of select="@reviews" />
				</span>
				reviews.
			</span>
			<xsl:apply-templates select=".." mode="moving-reviews">
				<xsl:with-param name="is-property" select="'true'" />
			</xsl:apply-templates>
		</div>
	</xsl:template>

</xsl:stylesheet>