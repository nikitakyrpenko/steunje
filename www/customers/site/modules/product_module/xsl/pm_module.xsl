<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only inf accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Product Module
  
  Main file.
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->


<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:negeso="http://negeso.com/2003/Framework"
				xmlns:java="http://xml.apache.org/xslt/java"
				exclude-result-prefixes="java">

<xsl:variable name="dict_pm_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('product', $lang)"/>

<!-- Product Module HEAD declarations -->
<xsl:template match="negeso:pm" mode="page_head">
	<xsl:call-template name="pm_head" />
</xsl:template>

<xsl:template match="negeso:pm-filter" mode="page_head">
	<xsl:call-template name="pm_head" />
</xsl:template>

<xsl:template name="pm_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/product_module/css/pm_filters.css"/>
	<link rel="stylesheet" type="text/css" href="/site/modules/product_module/css/pm_mail_to_friend.css"/>
	<link rel="stylesheet" type="text/css" href="/site/modules/product_module/css/pm_module.css"/>
	<link rel="stylesheet" type="text/css" href="/site/modules/product_module/css/pm_search.css"/>
	<link rel="stylesheet" type="text/css" href="/site/modules/product_module/css/pm_shopping_cart.css"/>
	<script type="text/javascript" src="/site/modules/product_module/script/pm.js">/**/</script>
</xsl:template>

<!-- Main template. Applied to negeso:pm and negeso:pm-filter XML nodes.
     Creates PM and Advanced Search page representations.  -->
<xsl:template name="pm">
<xsl:choose>
		<!-- ADVANCED SEARCH: BEGIN -->
		<xsl:when test="not(negeso:pm) and count(negeso:pm-filter) &gt; 0">
							<xsl:if test="position() = 1">
								<div class="pmTitle">
									<a>
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_pm_module"/>
											<xsl:with-param name ="name"  select="'PM_PM'"/>
										</xsl:call-template>
										<xsl:attribute name="href">
											<xsl:call-template name="pm_module_link" />
										</xsl:attribute>
										&lt;&lt;&#160;<xsl:value-of select="java:getString($dict_pm_module, 'PM_PM')"/>
									</a>
								</div>
								<!-- BLANK ROW -->
								<div>&#160;</div>
							</xsl:if>
        <div class="pmLimitBig"><xsl:apply-templates select="negeso:pm-filter" mode="filterPresets" /></div>
							<!-- BLANK ROW -->
							<div class="pmLimitBig">&#160;</div>
							<div class="pmLimitBig">
								<xsl:apply-templates select="negeso:pm-filter" mode="filterResults">
									<xsl:with-param name="mode" select="'list'" />
								</xsl:apply-templates>
							</div>
							<div class="pmLimitBig">&#160;</div>
		</xsl:when>
		<!-- ADVANCED SEARCH: END -->

		<!-- MAIN PAGE: BEGIN -->
		<xsl:otherwise>
			<xsl:apply-templates select="negeso:pm" mode="pm"/>
		</xsl:otherwise>
		<!-- MAIN PAGE: END -->
</xsl:choose>

</xsl:template>

<!-- Main Product Module page and all its cases -->
<xsl:template match="negeso:pm" mode="pm">
<xsl:choose>
		<!-- SHOPPING CART: BEGIN -->
		<xsl:when test="@view-type='shopping-cart' or @view-type='customer-account' or @view-type='customer-login' or @view-type='customer-register' or @view-type='checkout-wizard'">
			<xsl:call-template name="pm_carting" />
		</xsl:when>
		<!-- Mail to friend -->
		<xsl:when test="@view-type='mail-to-a-friend-form' or @view-type='mail-to-a-friend-result'">
			<xsl:call-template name="pm_mail_to_friend" />
		</xsl:when>
		<!-- SHOPPING CART: END -->
		<!-- MAIN PAGES: BEGIN -->
		<xsl:otherwise>

        <div class="l-pmNavigation">
						<xsl:call-template name="pm_categories"/>
						<xsl:call-template name="pm_free_search"/>
						<xsl:call-template name="pm_serial_search"/>
						<xsl:call-template name="pm_adv_search_link"/>
        </div>
        <div class="l-pmShort">
								<xsl:choose>
									<xsl:when test="@view-type='category-browser'">
										<xsl:call-template name="pm_category_new" />
										<xsl:apply-templates select="../negeso:pm-filter" mode="filterPresets" />
									</xsl:when>
									<xsl:when test="@view-type='product-gallery'">
										<xsl:call-template name="pm_products_list">
											<!-- 
												Use mode = 'list' for get products list
												or
												Use mode = 'thumbnails' or without parameter for get products thumbnails
											 -->
											<xsl:with-param name="mode">list</xsl:with-param>
										</xsl:call-template>
										<xsl:apply-templates select="../negeso:pm-filter" mode="filterPresets" />
									</xsl:when>
									<xsl:when test="@view-type='product-information'">
										<xsl:call-template name="pm_product_info" />
										<div class="bl-left">&#160;</div>
										<xsl:apply-templates select="../negeso:pm-filter" mode="filterPresets" />
									</xsl:when>
								</xsl:choose>
							</div>

		</xsl:otherwise>
		<!-- MAIN PAGES: END -->
</xsl:choose>
</xsl:template>

<!-- WHERE AM I: BEGIN -->
<!-- Integrates into standard "Where Am I" -->
<xsl:template match="negeso:pm" mode="where_am_i">
<xsl:apply-templates select="negeso:pm-path/negeso:path-element[@can_view='true']" mode="where_am_i" />
</xsl:template>

<xsl:template match="negeso:path-element" mode="where_am_i">
<div>
	<xsl:choose>
		<xsl:when test="count(following-sibling::negeso:path-element[@can_view='true']) = 0">
			<xsl:attribute name="class">here_am_i</xsl:attribute>
			<xsl:choose>
				<xsl:when test="@id=1">
					<xsl:value-of select="//negeso:page/negeso:title" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@title" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
            <a onFocus="blur()">
				<xsl:if test="@link and not(count(following-sibling::negeso:path-element[@can_view='true']) = 0)">
                    <xsl:attribute name="href">
                        <xsl:value-of select="@link" />
                    </xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="@id=1">
						<xsl:value-of select="//negeso:page/negeso:title" />
						<!--<xsl:value-of select="java:getString($dict_pm_module, 'PM_PM')"/>-->
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@title" />
					</xsl:otherwise>
				</xsl:choose>
			</a>
			&#160;&#160;&gt;&gt;&gt;&#160;&#160;
		</xsl:otherwise>
	</xsl:choose>
</div>
</xsl:template>
<!-- WHERE AM I: END -->

<!-- Product Categories Navigator 
     at the left part of PM Main Page -->
<xsl:template name="pm_categories">
<h2>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_CATEGORIES'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_CATEGORIES')"/>
</h2>
<ul>
		<xsl:for-each select="negeso:pm-tree">
			<xsl:apply-templates select="negeso:pm-category" mode="list" />
		</xsl:for-each>
</ul>
</xsl:template>

<xsl:template match="negeso:pm-category" mode="list">
<xsl:param name ="paddingLeft" select ="number(10)" />
<li style="padding-left:{$paddingLeft}px;">
			<xsl:choose>
				<xsl:when test ="ancestor::negeso:pm-category[@current or @opened] and not(@current) and not(@opened)">
					<xsl:attribute name="class">pmCatLev2</xsl:attribute>
				</xsl:when>
				<xsl:when test ="ancestor::negeso:pm-category and (@current or @opened)">
					<xsl:attribute name="class">pmCatActiveSub pmCatLev2</xsl:attribute>
				</xsl:when>
				<xsl:when test ="@current or @opened">
					<xsl:attribute name="class">pmCatActive</xsl:attribute>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="($outputType = 'admin') and not(/negeso:page/@role-id = 'visitor')">
				<img id="organize_widget_img" style="position:absolute; margin-left:-10px; cursor:pointer; margin-top:1px" border="0" src="/images/mark_1.png"  onClick="window.open('?command=pm-browse-category&amp;pmCatId={@id}','ProductModule','top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"/>
			</xsl:if>
    <a onFocus="blur()">
        <xsl:attribute name="href">
            <xsl:call-template name="getCategoryLink" >
                 <xsl:with-param name="id" select='@id' />
                 <xsl:with-param name="url" select='@url' />
            </xsl:call-template>
        </xsl:attribute>
						<xsl:value-of select="@title" />
					</a>
</li>

<xsl:apply-templates select="negeso:pm-category" mode="list">
    <xsl:with-param name ="paddingLeft" select="$paddingLeft+10" />
</xsl:apply-templates>
</xsl:template>

<!-- PRODUCT GALLERY -->
<xsl:template name="pm_products_list">
<!-- current node: negeso:pm -->
<xsl:param name="mode">thumbnails</xsl:param>

<!-- PRODUCTS LIST: BEGIN -->
<xsl:choose>
		<!-- CATEGORY FILTER RESULTS: BEGIN -->
		<xsl:when test="//negeso:pm-category[@current='true']/negeso:pm-filter[@response = 'RESULT_OK' and @type='CategorialFilter']">
							<!-- This ugly code - special for IE. If you will delete second div, category filter will "lost" right border -->
								<xsl:apply-templates select="//negeso:pm-category[@current='true']/negeso:pm-filter[@response = 'RESULT_OK' and @type='CategorialFilter']" mode="filterPreset">
									<xsl:with-param name="with_design" select="'true'" />
								</xsl:apply-templates>
			<xsl:apply-templates select="//negeso:pm-category[@current='true']/negeso:pm-filter[@response = 'RESULT_OK' and @type='CategorialFilter']" mode="filterResults" />
		</xsl:when>
		<!-- CATEGORY FILTER RESULTS: END -->
		<!-- FREE SEARCH RESULTS: BEGIN -->
		<xsl:when test="(negeso:pm-tree/negeso:pm-search-result) and not(negeso:pm-tree/negeso:pm-search-result/negeso:pm-product)">
        <h2>
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_pm_module"/>
								<xsl:with-param name ="name"  select="'PM_SORRY_NO_PRODUCTS'"/>
							</xsl:call-template>
            <xsl:value-of select="java:getString($dict_pm_module, 'PM_SORRY_NO_PRODUCTS')"/>
            <strong>"<xsl:value-of select="//negeso:pm-search-result/@searchWords" />"</strong>
        </h2>
		</xsl:when>
		<xsl:when test="negeso:pm-tree/negeso:pm-search-result/negeso:pm-product">
        <div>
				<xsl:for-each select="negeso:pm-tree/negeso:pm-search-result/negeso:pm-product">
                <xsl:apply-templates select="." mode="overview-list"/>
				</xsl:for-each>
			</div>
		</xsl:when>
		<!-- FREE SEARCH RESULTS: END -->
		<!-- CATEGORY PRODUCTS LIST: BEGIN -->
		<xsl:when test="//negeso:pm-category[@current='true']/negeso:pm-product">
			<xsl:apply-templates select="//negeso:pm-category[@current='true']/negeso:pm-product" mode="overview">
				<xsl:with-param name="mode" select="$mode"/>
			</xsl:apply-templates>
		</xsl:when>
		<xsl:when test="//negeso:pm-category[@current='true'] and not(//negeso:pm-category[@current='true']/negeso:pm-product)">
        <h2>
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_pm_module"/>
								<xsl:with-param name ="name"  select="'PM_NO_PRODUCTS'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_pm_module, 'PM_NO_PRODUCTS')"/>
        </h2>
		</xsl:when>
		<!-- CATEGORY PRODUCTS LIST: END -->
</xsl:choose>
<!-- PRODUCTS LIST: END -->
<!-- BLANK ROW -->
<div class="bl-left">&#160;</div>
</xsl:template>

<!-- NEW IN ASSORTIMENT: BEGIN -->
<xsl:template name="pm_category_new">
<!-- HEADER: BEGIN -->
<xsl:if test="//negeso:pm-overview-products/negeso:pm-product">
    <h2>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_NEW_IN_ASSORTIMENT'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_NEW_IN_ASSORTIMENT')"/>
    </h2>
</xsl:if>
<!-- HEADER: END -->

<xsl:if test="//negeso:pm-overview-products/negeso:pm-product">
			<!-- featured products -->
    <table cellpadding="0" cellspacing="0" border="0" class="b-pmList">
        <xsl:apply-templates select="//negeso:pm-overview-products/negeso:pm-product" mode="overview-list"/>
    </table>
		<!-- BLANK ROW -->
    <br clear="all"/>
</xsl:if>

</xsl:template>
<!-- NEW IN ASSORTIMENT: END -->

<!-- PRODUCT DETAILS: BEGIN -->
<xsl:template name="pm_product_info" >
<!-- Current tag is negeso:pm -->
<h2>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_PRODUCT_INFO'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_PRODUCT_INFO')"/>
</h2>

<p><xsl:apply-templates select="//negeso:pm-category[@opened='true']/negeso:pm-product" mode="pm_product_details" /></p>


<span>
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_pm_module"/>
				<xsl:with-param name ="name"  select="'PM_RELATED_PRODUCTS'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_pm_module, 'PM_RELATED_PRODUCTS')"/>
</span>
<p>
		<xsl:choose>
			<xsl:when test="//negeso:pm-category[@opened='true']/negeso:pm-product[@current='true']/negeso:pm-property[@name='related_products']/negeso:pm-product">
				<xsl:apply-templates select="//negeso:pm-category[@opened='true']/negeso:pm-product[@current='true']/negeso:pm-property[@name='related_products']/negeso:pm-product" mode="related"/>
			</xsl:when>
			<xsl:otherwise>
				<span>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_NO_RELATED_PRODUCTS'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_NO_RELATED_PRODUCTS')"/>
				</span>

			</xsl:otherwise>
		</xsl:choose>
</p>

<h2>
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_pm_module"/>
			<xsl:with-param name ="name"  select="'PM_ACCESSORIES'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_pm_module, 'PM_ACCESSORIES')"/>
</h2>

<xsl:choose>
		<xsl:when test="//negeso:pm-category[@opened='true']/negeso:pm-product[@current='true']/negeso:pm-property[@name='accessories']/negeso:pm-product">
			<xsl:apply-templates select="//negeso:pm-category[@opened='true']/negeso:pm-product[@current='true']/negeso:pm-property[@name='accessories']/negeso:pm-product" mode="related"/>
		</xsl:when>
		<xsl:otherwise>
			<span>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_NO_ACCESSORIES'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_NO_ACCESSORIES')"/>
			</span>

		</xsl:otherwise>
</xsl:choose>

</xsl:template>

<xsl:template match="negeso:pm-product" mode="pm_product_details">
<table cellspacing="0" cellpadding="0" class="b-pmProduct">
		<tr>
        <td class="b-pmImgPr">
				<xsl:choose>
					<xsl:when test="negeso:pm-property[@type='thumbnail']">
						<xsl:apply-templates select="negeso:pm-property[@type='thumbnail']" mode="thumbImage" />
					</xsl:when>
					<xsl:otherwise>
						<span>
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_pm_module"/>
								<xsl:with-param name ="name"  select="'PM_IMAGE_NOT_PRESENT'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_pm_module, 'PM_IMAGE_NOT_PRESENT')"/>
						</span>

					</xsl:otherwise>
				</xsl:choose>
			</td>
			<td>
				<div>
					<xsl:apply-templates select="negeso:pm-property[@name='title']" mode="display">
						<xsl:with-param name="class" select="'redTitle'" />
						<xsl:with-param name="link">
							<xsl:attribute name="href">
                                <xsl:call-template name="getProductLink" >
                                     <xsl:with-param name="id" select='@id' />
                                     <xsl:with-param name="url" select='@url' />
                                </xsl:call-template>
                            </xsl:attribute>
						</xsl:with-param>
					</xsl:apply-templates>
				</div>

				<!-- Hidden Product title, for further using via JavaScript (e.g. - for thumbImage popup) -->
            <div id="current_product_title">
                <xsl:value-of select="negeso:pm-property[@name='title']/@value" />
            </div>
				<xsl:if test="negeso:pm-property[@name='sn']/@value">
					<div>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_SERIAL_NUMBER'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_SERIAL_NUMBER')"/>:
					    <xsl:value-of select="negeso:pm-property[@name='sn']/@value" disable-output-escaping="yes"/>
					</div>
				</xsl:if>
			</td>
			<td style="text-align:center; vertical-align:middle;">
            <a onFocus="blur()">
					<xsl:call-template name="pm_shopping_cart_link">
                    <xsl:with-param name="addition"><xsl:text>?action=add_product&amp;product_id=</xsl:text><xsl:value-of select="@id" />
						</xsl:with-param>
					</xsl:call-template>
					<img src="/site/modules/product_module/images/pm_shop.gif" border="0" style="text-align:center; vertical-align:middle; margin-bottom:5px;">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_ADD_TO_SHOPPING_CART'"/>
						</xsl:call-template>
						<xsl:attribute name="alt">
							<xsl:value-of select="java:getString($dict_pm_module, 'PM_ADD_TO_SHOPPING_CART')"/>
						</xsl:attribute>
					</img>
				</a>
				&#160;&#160;
				<a class="pmRedLink">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_ADD_PRODUCT'"/>
					</xsl:call-template>
					<xsl:call-template name="pm_shopping_cart_link">
                    <xsl:with-param name="addition"><xsl:text>?action=add_product&amp;product_id=</xsl:text><xsl:value-of select="@id" /></xsl:with-param>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_ADD_PRODUCT')"/>
				</a>
			</td>
		</tr>
		<tr>
			<td class="pmMenuTopPas" valign="top">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_pm_module"/>
					<xsl:with-param name ="name"  select="'PM_DESCRIPTION'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_pm_module, 'PM_DESCRIPTION')"/>:
			</td>
			<td colspan="3">
				<xsl:value-of select="negeso:pm-property[@name='description']/negeso:article/negeso:text" disable-output-escaping="yes" />
			</td>
		</tr>

		<xsl:if test="negeso:pm-property[@name='weight']/@value">
		    <tr>
				<td class="pmMenuTopPas">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_WEIGHT'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_WEIGHT')"/>:
				</td>
		        <td  colspan="2">
		            <xsl:value-of select="negeso:pm-property[@name='weight']/@value" disable-output-escaping="yes" />
		        </td>
		    </tr>
		</xsl:if>

		<xsl:if test="negeso:pm-property[@name='producer']/@value">
			<tr>
				<td class="pmMenuTopPas">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_PRODUCER'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_PRODUCER')"/>:
				</td>
				<td  colspan="2">
					<xsl:value-of select="negeso:pm-property[@name='producer']/@value" disable-output-escaping="yes" />
				</td>
			</tr>
		</xsl:if>

		<xsl:if test="negeso:pm-property[(@name='spec_file') and (@file-link!='undefined')]">
			<tr>
				<td class="pmMenuTopPas">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_SPECIFICATION'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_SPECIFICATION')"/>:
			    	</td>
				<td  colspan="2">
					<xsl:apply-templates select="negeso:pm-property[@name='spec_file']" mode="pmLink">
						<xsl:with-param name="text" select='"Download"' />
					</xsl:apply-templates>
				</td>
			</tr>
		</xsl:if>

		<xsl:if test="negeso:pm-property[@name='price']/@value">
			<tr>
				<td class="pmMenuTopPas">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_PRICE'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_PRICE')"/>:&#160;
				</td>
				<td  colspan="2">
					<xsl:apply-templates select="negeso:pm-property[@name='price']" mode="display" />
				</td>
			</tr>
		</xsl:if>
		<tr>
			<td colspan="3" valign="top" class="detail">
				<xsl:call-template name="pm_mail_to" />
			</td>
		</tr>
</table>
</xsl:template>
<!-- PRODUCT DETAILS: END -->

<!-- RELATED PRODUCTS: BEGIN -->
<xsl:template match="negeso:pm-product" mode="related">
<xsl:param name="mode">thumbnails</xsl:param>
<div class="b-pmRelatedItemContainer">
    <div class="b-pmPhoto">
        <div class="b-pmImage">
            <a onFocus="blur()">
	               <xsl:attribute name="href">
	                   <xsl:call-template name="getProductLink" >
	                        <xsl:with-param name="id" select='@id' />
	                        <xsl:with-param name="url" select='@url' />
	                   </xsl:call-template>
	               </xsl:attribute>
					<xsl:choose>
						<xsl:when test="@image-link">
							<img src="{@image-link}" vspace="0" hspace="0" border="0" onerror="this.src='/images/no_image_98.gif';">
								<xsl:if test="@th-width">
									<xsl:attribute name="width"><xsl:value-of select="@th-width" /></xsl:attribute>
								</xsl:if>
								<xsl:if test="@th-height">
									<xsl:attribute name="height"><xsl:value-of select="@th-height" /></xsl:attribute>
								</xsl:if>
							</img>
						</xsl:when>
						<xsl:otherwise>
							<span>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_IMAGE_NOT_PRESENT'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_IMAGE_NOT_PRESENT')"/>
							</span>

						</xsl:otherwise>
					</xsl:choose>
				</a>
			</div>
			<div class="pmRating">
				<xsl:call-template name="pm_show_stars">
					<xsl:with-param name="number" select="@rating" />
				</xsl:call-template>
			</div>
		</div>
		<div class="pmTitle pmPrice">
			<a>
			     <xsl:attribute name="href">
	                  <xsl:call-template name="getProductLink" >
	                       <xsl:with-param name="id" select='@id' />
	                       <xsl:with-param name="url" select='@url' />
	                  </xsl:call-template>
                  </xsl:attribute>
				<xsl:value-of select="@title"/>
			</a>
		</div>
		<div>
			<xsl:value-of select="negeso:pm-price/@value"/>
		</div>
</div>
</xsl:template>
<!-- RELATED PRODUCTS: END -->

<!-- PRODUCTS OVERVIEW: BEGIN -->
<!-- Very advanced and usefull template: 
     you may choose between 'list' and 'thumbnails' products representation. -->
<xsl:template match="negeso:pm-product" mode="overview">
<xsl:param name="mode">thumbnails</xsl:param>
<!-- Products list representation as THUMBNAILS -->
<div class="b-pmItemContainer">
    <div class="b-pmPhoto">
        <div class="b-pmImage">
            <a onFocus="blur()">
							<xsl:attribute name="href">
								<xsl:call-template name="getProductLink" >
				                     <xsl:with-param name="id" select='@id' />
				                     <xsl:with-param name="url" select='@url' />
				                </xsl:call-template>
							</xsl:attribute>
							<xsl:choose>
								<xsl:when test="negeso:pm-property[@type='thumbnail']">
									<xsl:apply-templates select="negeso:pm-property[@type='thumbnail']" mode="thumbImage">
										<xsl:with-param name="withoutLink" select='"yes"' />
									</xsl:apply-templates>
								</xsl:when>
								<xsl:otherwise>
									<span>
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_pm_module"/>
											<xsl:with-param name ="name"  select="'PM_IMAGE_NOT_PRESENT'"/>
										</xsl:call-template>
										<xsl:value-of select="java:getString($dict_pm_module, 'PM_IMAGE_NOT_PRESENT')"/>
									</span>

								</xsl:otherwise>
							</xsl:choose>
						</a>
					</div>
        <div class="b-pmRating">
						<xsl:call-template name="pm_show_stars">
							<xsl:with-param name="number" select="@rating" />
						</xsl:call-template>
					</div>
				</div>
    <div class="b-pmPrice">
        <a onFocus="blur()">
            <xsl:attribute name="href">
	            <xsl:call-template name="getProductLink" >
	                 <xsl:with-param name="id" select='@id' />
	                 <xsl:with-param name="url" select='@url' />
	            </xsl:call-template>
            </xsl:attribute>
						<xsl:value-of select="@title"/>
					</a>
				</div>
				<div>
					<xsl:value-of select="negeso:pm-property[@name='producer']/@value"/>
				</div>
    <div class="b-pmPrice">
					<xsl:value-of select="negeso:pm-property[@name='price']/@value"/>
				</div>
				<xsl:if test="negeso:pm-property[@name='weight']/@value != '0'">
					<div>
						<xsl:value-of select="negeso:pm-property[@name='weight']/@value"/>
					</div>
				</xsl:if>
</div>
</xsl:template>

<xsl:template match="negeso:pm-product" mode="overview-list">
<!-- Products list representation as LIST -->
<tr>
    <td class="b-pmImgList">
        <a onFocus="blur()">
									<xsl:attribute name="href">
										<xsl:call-template name="getProductLink" >
                                             <xsl:with-param name="id" select='@id' />
                                             <xsl:with-param name="url" select='@url' />
                                        </xsl:call-template>
									</xsl:attribute>
									<xsl:choose>
										<xsl:when test="negeso:pm-property[@type='thumbnail']">
											<xsl:apply-templates select="negeso:pm-property[@type='thumbnail']" mode="thumbImage">
												<xsl:with-param name="withoutLink" select='"yes"' />
											</xsl:apply-templates>
										</xsl:when>
										<xsl:otherwise>
											<span>
												<xsl:call-template name ="add-constant-info">
													<xsl:with-param name ="dict"  select="$dict_pm_module"/>
													<xsl:with-param name ="name"  select="'PM_IMAGE_NOT_PRESENT'"/>
												</xsl:call-template>
												<xsl:value-of select="java:getString($dict_pm_module, 'PM_IMAGE_NOT_PRESENT')"/>
											</span>

										</xsl:otherwise>
									</xsl:choose>
        </a>        
						</td>
    <td class="b-pmTitleList">
							<xsl:if test="negeso:pm-property[@name='title']/@value">
            <a onFocus="blur()">
									<xsl:attribute name="href">
										<xsl:call-template name="getProductLink" >
                                             <xsl:with-param name="id" select='@id' />
                                             <xsl:with-param name="url" select='@url' />
                                        </xsl:call-template>
									</xsl:attribute>
									<xsl:value-of select="negeso:pm-property[@name='title']/@value"/>
								</a>
							</xsl:if>
							<br/>
							<xsl:value-of select="negeso:pm-property[@name='producer']/@value"/>
						</td>
    <td class="b-pmTitle">
						    <xsl:choose>
					            <xsl:when test="negeso:pm-property[@name='short_description']/@value">
                <a onFocus="blur()">
										<xsl:attribute name="href">
											<xsl:call-template name="getProductLink" >
											     <xsl:with-param name="id" select='@id' />
											     <xsl:with-param name="url" select='@url' />
											</xsl:call-template>
										</xsl:attribute>
										<xsl:value-of select="negeso:pm-property[@name='short_description']/@value"/>
					                </a>
					            </xsl:when>
					            <xsl:otherwise>
					                &#160;
					            </xsl:otherwise>
						    </xsl:choose>
						</td>
    <td class="b-pmPrice">
							<xsl:if test="negeso:pm-property[@name='price']/@value">
								<xsl:value-of select="negeso:pm-property[@name='price']/@value"/>
        </xsl:if>
							<xsl:if test="@rating">
            <div>
									<xsl:call-template name="pm_show_stars">
										<xsl:with-param name="number" select="@rating" />
									</xsl:call-template>
								</div>
							</xsl:if>
						</td>
    <td class="b-pmCartList">
							<div>
            <a onFocus="blur()">
									<xsl:call-template name="pm_shopping_cart_link">
										<xsl:with-param name="addition">
											<xsl:text>?action=add_product&amp;product_id=</xsl:text>
											<xsl:value-of select="@id" />
										</xsl:with-param>
									</xsl:call-template>
								    <img src="/site/modules/product_module/images/pm_shop.gif" border="0">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_pm_module"/>
											<xsl:with-param name ="name"  select="'PM_ADD_TO_SHOPPING_CART'"/>
										</xsl:call-template>
										<xsl:attribute name="alt">
											<xsl:value-of select="java:getString($dict_pm_module, 'PM_ADD_TO_SHOPPING_CART')"/>
										</xsl:attribute>
								    </img>
								</a>
							</div>
						</td>
</tr>
</xsl:template>
<!-- PRODUCTS OVERVIEW: BEGIN -->

<!-- SPECIAL TEMPLATES: BEGIN -->
<xsl:template name="pm_show_stars">
<xsl:param name="number" select="0" />
<xsl:param name="left" select="5" />
<xsl:choose>
		<xsl:when test="number($number) &gt; 0">
			<img src="/site/modules/product_module/images/pm_star_active.gif" width="14" height="13" />
		</xsl:when>
		<xsl:otherwise>
			<img src="/site/modules/product_module/images/pm_star_passive.gif" width="14" height="13" />
		</xsl:otherwise>
</xsl:choose>
<xsl:if test="$left &gt; 1">
		<xsl:call-template name="pm_show_stars">
			<xsl:with-param name="number" select="number($number) - 1" />
			<xsl:with-param name="left" select="number($left) - 1" />
    </xsl:call-template>
</xsl:if>
</xsl:template>

<xsl:template match="negeso:pm-property" mode="thumbImage">
<xsl:param name="withoutLink" select='"no"' />
<xsl:choose>
		<xsl:when test="$withoutLink = 'yes'">
				<img src="{@th-link}" vspace="0" hspace="0" border="0" onerror="this.src='/images/no_image_98.gif';">
					<xsl:if test="@th-width">
                <xsl:attribute name="width">
                    <xsl:value-of select="@th-width" />
                </xsl:attribute>
					</xsl:if>
					<xsl:if test="@th-height">
                <xsl:attribute name="height">
                    <xsl:value-of select="@th-height" />
                </xsl:attribute>
					</xsl:if>
				</img>
		</xsl:when>
		<xsl:otherwise>
        <a onFocus="blur()">
				<xsl:attribute name="href">
					<xsl:text disable-output-escaping="yes">javascript:loadImage('</xsl:text>
					<xsl:choose>
                    <xsl:when test="@link"><xsl:value-of select="@link" /></xsl:when>
						<xsl:otherwise>images/no_image_98.gif</xsl:otherwise>
					</xsl:choose>

					<xsl:text disable-output-escaping="yes">',</xsl:text>
					<xsl:choose>
                    <xsl:when test="@width"><xsl:value-of select="@width" /></xsl:when>
						<xsl:otherwise>98</xsl:otherwise>
					</xsl:choose>
					<xsl:text disable-output-escaping="yes">,</xsl:text>
					<xsl:choose>
                    <xsl:when test="@height"><xsl:value-of select="@height" /></xsl:when>
						<xsl:otherwise>98</xsl:otherwise>
					</xsl:choose>
					<!-- Here for need for correct popup functionality hidden DIV with id="current_product_title",
					     in which Product Title is stored. Such one is in [xsl:template match="negeso:pm-product" mode="pm_product_details"] -->
					<xsl:text disable-output-escaping="yes">,document.getElementById('current_product_title').innerHTML)</xsl:text>

				</xsl:attribute>
				<img src="{@th-link}" border="0" onerror="this.src='/images/no_image_98.gif';">
					<xsl:if test="@th-width">
						<xsl:attribute name="width"><xsl:value-of select="@th-width"/></xsl:attribute>
					</xsl:if>
					<xsl:if test="@th-height">
						<xsl:attribute name="height"><xsl:value-of select="@th-height"/></xsl:attribute>
					</xsl:if>
				</img>
			</a>
		</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="negeso:pm-property" mode="display">
<xsl:param name="class" select="none" />
<xsl:param name="link" select="none" />
<xsl:if test="@value">
		<div>
			<xsl:if test="$class='redTitle'">
				<xsl:attribute name="class">pmPrice</xsl:attribute>
			</xsl:if>
			<xsl:if test="$class='title'">
				<xsl:attribute name="class">pmMenuTopPas</xsl:attribute>
			</xsl:if>
			<xsl:if test="$class = 'price'">
				<xsl:attribute name="class">pmPrice</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$class='title' or $class='redTitle'">
                <a href="{$link}" onFocus="blur()">
						<xsl:attribute name="class">pmRedLink</xsl:attribute>
						<xsl:value-of select="@value"/>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@value"/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
</xsl:if>
</xsl:template>

<xsl:template name="getProductLink">
<xsl:param name="id" />
<xsl:param name="url" />
    <xsl:choose>
        <xsl:when test="$url"><xsl:value-of select="$adminPath"></xsl:value-of>/<xsl:value-of select="$url"/>.html</xsl:when>
        <xsl:otherwise><xsl:call-template name="pm_module_link" />?pmProductId=<xsl:value-of select="$id"/></xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="getCategoryLink">
<xsl:param name="id" />
<xsl:param name="url" />
    <xsl:choose>
        <xsl:when test="$url"><xsl:value-of select="$adminPath"></xsl:value-of>/<xsl:value-of select="$url"/></xsl:when>
        <xsl:otherwise><xsl:call-template name="pm_module_link" />?pmCatId=<xsl:value-of select="$id"/></xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- SPECIAL TEMPLATES: END -->

<!-- As you can see, we are in full I18N now! :) -->

</xsl:stylesheet>