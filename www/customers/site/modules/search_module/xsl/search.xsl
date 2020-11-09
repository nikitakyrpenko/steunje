<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Search Module
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="dict_search" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('core', $lang)"/>

<xsl:template match="negeso:search" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/search_module/css/search.css?v=1"/>
	<script type="text/javascript" src="/site/modules/search_module/script/search.js?v=1">/**/</script>
</xsl:template>

<!-- *************** NEGESO SEARCH BEGIN ***************** -->
<!-- SIMPLE SEARCH START -->
<xsl:template name="sm_form">
    <div class="b-smBody">
					<!--<xsl:call-template name="page_title_line_block">-->
						<!--<xsl:with-param name="title">-->
							<!--<xsl:value-of select="java:getString($dict_search, 'S_SEARCH')"/>-->
						<!--</xsl:with-param>-->
					<!--</xsl:call-template>-->
					<form name="advancedSearch" method="get">
						<div class="search-line bottom-line">
							<p class="heding-searchForm">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_search"/>
									<xsl:with-param name ="name"  select="'AS_ALL_WORDS'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_search, 'AS_ALL_WORDS')"/>:
							</p>
							<p>
								<input type="text" name="query" value="{/negeso:page/negeso:contents/negeso:search/@query}" />
							</p>
							<p class="search-line-submit">
								<input class="submit" type="submit">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_search"/>
										<xsl:with-param name ="name"  select="'S_SEARCH'"/>
									</xsl:call-template>
									<xsl:attribute name="value">
										<xsl:value-of select="java:getString($dict_search, 'S_SEARCH')"/>
									</xsl:attribute>
								</input>
							</p>
						</div>
					</form>
        <br/>
					<xsl:if test="//negeso:search/negeso:search_result">
									<xsl:apply-templates select="negeso:search/negeso:search_result"/>
					</xsl:if>
				</div>
</xsl:template>
<!-- SIMPLE SEARCH END -->

<!-- ADVANCED SEARCH STARTED -->
<xsl:template name="sm_advanced_form">
	<!-- current tag: negeso:contents -->
	<script type="text/javascript">
		var img_text = new Array();
		img_text['min'] = "<xsl:value-of select="java:getString($dict_search, 'AS_MINIMIZE_FORM')"/>";
		img_text['max'] = "<xsl:value-of select="java:getString($dict_search, 'AS_MAXIMIZE_FORM')"/>";
    </script>
    <div class="b-smBody">

        <xsl:call-template name="page_title_line_block">
            <xsl:with-param name="title">
                <xsl:value-of select="java:getString($dict_search, 'AS_ADVANCED_SEARCH')"/>
            </xsl:with-param>
        </xsl:call-template>

        <div class="b-smScroll">
            <a id="search_block_control_text" href="javascript:form_min_max()">
                <xsl:value-of select="java:getString($dict_search, 'AS_MINIMIZE_FORM')"/>
            </a>&#160;
            <a href="javascript:form_min_max()">
                <img id="search_block_control" src="/site/modules/search_module/images/search_min.gif">
                    <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_search, 'AS_MINIMIZE_FORM')"/></xsl:attribute>
                    <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_search, 'AS_MINIMIZE_FORM')"/></xsl:attribute>
                </img>
            </a>
        </div>

					<form name="advancedSearch" method="get">
						<input type="hidden" name="mode" value="advanced"/>
									<!-- SEARCH PARAMETERS -->
            <table id="search_block" style="display: block" class="b-smTable" cellspacing="10" cellpadding="0" border="0">
								            <tr>
								                <td>
                        <h2>
													<xsl:call-template name ="add-constant-info">
														<xsl:with-param name ="dict"  select="$dict_search"/>
														<xsl:with-param name ="name"  select="'AS_ALL_WORDS'"/>
                            </xsl:call-template><xsl:value-of select="java:getString($dict_search, 'AS_SEARCH_PARAMETERS')"/>
                        </h2>

                        <table cellpadding="0" cellspacing="0" border="0" class="b-smParamsTable">
                            <tr>
                                <td>
                                    <xsl:value-of select="java:getString($dict_search, 'AS_ALL_WORDS')"/>
                                </td>
                                <td class="right">
                                    <input name="allWords" value="{//negeso:search/@allWords}"/>
                                </td>
								            </tr>
								            <tr>
								                <td>
													<xsl:call-template name ="add-constant-info">
														<xsl:with-param name ="dict"  select="$dict_search"/>
														<xsl:with-param name ="name"  select="'AS_EXACT_PHRASE'"/>
													</xsl:call-template>
                                    <xsl:value-of select="java:getString($dict_search, 'AS_EXACT_PHRASE')"/>
                                </td>
                                <td class="right">
                                    <input name="exactPhrase" value="{//negeso:search/@exactPhrase}"/>
                                </td>
								            </tr>
								            <tr>
								                <td>
													<xsl:call-template name ="add-constant-info">
														<xsl:with-param name ="dict"  select="$dict_search"/>
														<xsl:with-param name ="name"  select="'AS_AT_LEAST_ONE'"/>
													</xsl:call-template>
                                    <xsl:value-of select="java:getString($dict_search, 'AS_AT_LEAST_ONE')"/>
                                </td>
                                <td class="right">
                                    <input name="atLeastOne" value="{//negeso:search/@atLeastOne}"/>
                                </td>
								            </tr>
								            <tr>
								                <td>
													<xsl:call-template name ="add-constant-info">
														<xsl:with-param name ="dict"  select="$dict_search"/>
														<xsl:with-param name ="name"  select="'AS_WITHOUT_THE_WORDS'"/>
													</xsl:call-template>
                                    <xsl:value-of select="java:getString($dict_search, 'AS_WITHOUT_THE_WORDS')"/>
                                </td>
                                <td class="right">
                                    <input name="without" value="{//negeso:search/@without}"/>
                                </td>
								            </tr>
										</table>
								</td>
								<!-- RESULT PARAMETERS -->
                    <td>
                        <h2>
											<xsl:call-template name ="add-constant-info">
												<xsl:with-param name ="dict"  select="$dict_search"/>
												<xsl:with-param name ="name"  select="'AS_RESULT_PARAMETERS'"/>
											</xsl:call-template>
											<xsl:value-of select="java:getString($dict_search, 'AS_RESULT_PARAMETERS')"/>
                        </h2>
                        <table class="b-smParamsTable">
								            <tr>
								                <td>
													<xsl:call-template name ="add-constant-info">
														<xsl:with-param name ="dict"  select="$dict_search"/>
														<xsl:with-param name ="name"  select="'AS_ITEMS_PER_PAGE'"/>
													</xsl:call-template>
                                    <xsl:value-of select="java:getString($dict_search, 'AS_ITEMS_PER_PAGE')"/>
                                </td>
								                <td class="right">
								                    <xsl:call-template name="as_select_per_page" />
								                </td>
								            </tr>
								            <tr>
								                <td>
													<xsl:call-template name ="add-constant-info">
														<xsl:with-param name ="dict"  select="$dict_search"/>
														<xsl:with-param name ="name"  select="'AS_WEB_PAGES_UPDATED'"/>
													</xsl:call-template>
                                    <xsl:value-of select="java:getString($dict_search, 'AS_WEB_PAGES_UPDATED')"/>
                                </td>
								                <td class="right">
								                    <xsl:call-template name="as_select_updated_date" />
								                </td>
								            </tr>
								            <tr>
								                <td>
													<xsl:call-template name ="add-constant-info">
														<xsl:with-param name ="dict"  select="$dict_search"/>
														<xsl:with-param name ="name"  select="'AS_SORTING'"/>
													</xsl:call-template>
                                    <xsl:value-of select="java:getString($dict_search, 'AS_SORTING')"/>
                                </td>
								                <td class="right">
								                    <xsl:call-template name="as_select_sort" />
								                </td>
								            </tr>
								            <tr>
								                <td align="center" colspan="2">
								                	<input type="submit" class="submit" onClick="search(this.form); return false;">
														<xsl:call-template name ="add-constant-info">
															<xsl:with-param name ="dict"  select="$dict_search"/>
															<xsl:with-param name ="name"  select="'S_SEARCH'"/>
														</xsl:call-template>
								                		<xsl:attribute name="value">
									                		<xsl:value-of select="java:getString($dict_search, 'S_SEARCH')"/>
									                	</xsl:attribute>
								                    </input>
								                </td>
								            </tr>
								    	</table>
								</td>
							</tr>
						</table>
					</form>

					<xsl:if test="count(//negeso:hit) &gt; 1">
						<script type="text/javascript">
							search_form_init();
						</script>
					</xsl:if>

					<xsl:if test="//negeso:search/negeso:search_result">
            <table class="smResultsTable" cellspacing="0" cellpadding="0" border="0">
							<tr>
								<td class="middle">
									<xsl:apply-templates select="negeso:search/negeso:search_result"/>
								</td>
							</tr>
						</table>
					</xsl:if>

			    </div>
</xsl:template>

<xsl:template name="as_select_per_page">
    <!-- Current tag is negeso:advancedSearch -->
    <select name="paging" class="w145 as_inpmarg">
        <xsl:call-template name ="add-constant-info">
            <xsl:with-param name ="dict"  select="$dict_search"/>
            <xsl:with-param name ="name"  select="'AS_RESULTS'"/>
        </xsl:call-template>
        <option value="10">
            <xsl:if test="//negeso:search/@paging = '10'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            10 <xsl:value-of select="java:getString($dict_search, 'AS_RESULTS')"/>
        </option>
        <option value="20">
            <xsl:if test="//negeso:search/@paging = '20'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            20 <xsl:value-of select="java:getString($dict_search, 'AS_RESULTS')"/>
        </option>
        <option value="30">
            <xsl:if test="//negeso:search/@paging = '30'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            30 <xsl:value-of select="java:getString($dict_search, 'AS_RESULTS')"/>
        </option>
        <option value="50">
            <xsl:if test="//negeso:search/@paging = '50'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            50 <xsl:value-of select="java:getString($dict_search, 'AS_RESULTS')"/>
        </option>
        <option value="100">
            <xsl:if test="//negeso:search/@paging = '100'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            100 <xsl:value-of select="java:getString($dict_search, 'AS_RESULTS')"/>
        </option>
    </select>
</xsl:template>

<xsl:template name="as_select_updated_date">
    <!-- Current tag is negeso:advancedSearch -->
    <select name="lastMonths" class="w145 as_inpmarg">
        <option value="0">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_search"/>
                <xsl:with-param name ="name"  select="'AS_ANYTIME'"/>
            </xsl:call-template>
            <xsl:if test="//negeso:search/@lastMonths = '0'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="java:getString($dict_search, 'AS_ANYTIME')"/>
        </option>
        <option value="3">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_search"/>
                <xsl:with-param name ="name"  select="'AS_PAST_3_MONTHS'"/>
            </xsl:call-template>
            <xsl:if test="//negeso:search/@lastMonths = '3'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="java:getString($dict_search, 'AS_PAST_3_MONTHS')"/>
        </option>
        <option value="6">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_search"/>
                <xsl:with-param name ="name"  select="'AS_PAST_6_MONTHS'"/>
            </xsl:call-template>
            <xsl:if test="//negeso:search/@lastMonths = '6'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="java:getString($dict_search, 'AS_PAST_6_MONTHS')"/>
        </option>
        <option value="12">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_search"/>
                <xsl:with-param name ="name"  select="'AS_PAST_YEAR'"/>
            </xsl:call-template>
            <xsl:if test="//negeso:search/@lastMonths = '12'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="java:getString($dict_search, 'AS_PAST_YEAR')"/>
        </option>
    </select>
</xsl:template>

<xsl:template name="as_select_sort">
    <!-- Current tag is negeso:advancedSearch -->
    <select name="sortOrder" class="w145 as_inpmarg">
        <option value="date">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_search"/>
                <xsl:with-param name ="name"  select="'AS_SORT_BY_DATE'"/>
            </xsl:call-template>
            <xsl:if test="//negeso:search/@sortOrder = 'date'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="java:getString($dict_search, 'AS_SORT_BY_DATE')"/>
        </option>
        <option value="relevance">
            <xsl:call-template name ="add-constant-info">
                <xsl:with-param name ="dict"  select="$dict_search"/>
                <xsl:with-param name ="name"  select="'AS_SORT_BY_RELEVANCE'"/>
            </xsl:call-template>
            <xsl:if test="//negeso:search/@sortOrder = 'relevance'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="java:getString($dict_search, 'AS_SORT_BY_RELEVANCE')"/>
        </option>
    </select>
</xsl:template>

<!-- ADVANCED SEARCH END -->

<xsl:template match="negeso:search_result">
	<span>
		<xsl:call-template name="add-constant-info">
			<xsl:with-param name="dict" select="$dict_search" />
			<xsl:with-param name="name" select="'CM.SEARCH_INDEX_IS_BUILD'" />
		</xsl:call-template>
	</span>
    <xsl:choose>
		<xsl:when test="negeso:hits/negeso:hit">
			<h3>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_search"/>
						<xsl:with-param name ="name"  select="'S_SEARCH_RESULTS'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_search, 'S_SEARCH_RESULTS')"/>
				&#160;
				<xsl:value-of select="negeso:hits/@current-range"/>
				<xsl:choose>
					<xsl:when test="../@query">
						&#160;
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_search"/>
								<xsl:with-param name ="name"  select="'S_SEARCH_FOR'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_search, 'S_SEARCH_FOR')"/>
						:&#160;
						"<xsl:value-of select="../@query"/>"
					</xsl:when>
					<xsl:when test="../@allWords or ../@atLeastOne or ../@exactPhrase">
						&#160;
						<span>
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_search"/>
								<xsl:with-param name ="name"  select="'S_SEARCH_FOR'"/>
							</xsl:call-template>
						<xsl:value-of select="java:getString($dict_search, 'S_SEARCH_FOR')"/>
						</span>:&#160;
						<xsl:if test="../@allWords!=''">
							"<xsl:value-of select="../@allWords"/>"&#160;
						</xsl:if>
						<xsl:if test="../@atLeastOne!=''">
							"<xsl:value-of select="../@atLeastOne"/>"&#160;
						</xsl:if>
						<xsl:if test="../@exactPhrase!=''">
							"<xsl:value-of select="../@exactPhrase"/>"&#160;
						</xsl:if>
						<xsl:if test="../@without!=''">
							"<xsl:value-of select="../@without"/>"&#160;
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>:&#160;</xsl:otherwise>
				</xsl:choose>
			</h3>
			<h3>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_search"/>
					<xsl:with-param name ="name"  select="'S_TOTAL_MATCHES'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_search, 'S_TOTAL_MATCHES')"/>
                <xsl:text>&#160;-&#160;</xsl:text>
                <xsl:value-of select="negeso:hits/@total-count"/>
			</h3>
        </xsl:when>
        <xsl:when test="@isBuildingIndex='true'">
			<div class="smTableTitle">
				<p>
					<xsl:call-template name="add-constant-info">
						<xsl:with-param name="dict" select="$dict_search" />
						<xsl:with-param name="name" select="'CM.SEARCH_INDEX_IS_BUILD'" />
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_search, 'CM.SEARCH_INDEX_IS_BUILD')" />
				</p>
			</div>
        </xsl:when>
        <xsl:otherwise>
            <div class="smResultTitle">
                <span>
                    <xsl:call-template name ="add-constant-info">
                        <xsl:with-param name ="dict"  select="$dict_search"/>
                        <xsl:with-param name ="name"  select="'S_NO_MATCHES'"/>
                    </xsl:call-template>
                    <xsl:value-of select="java:getString($dict_search, 'S_NO_MATCHES')"/>
                </span>
                :&#160;
                <xsl:text>"</xsl:text>
                <xsl:choose>
                    <xsl:when test="../@allWords and ../@allWords!=''">
                        <xsl:value-of select="../@allWords"/>
                    </xsl:when>
                    <xsl:when test="../@exactPhrase and ../@exactPhrase!=''">
                        <xsl:value-of select="../@exactPhrase"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="../@atLeastOne"/>
                    </xsl:otherwise>
	</xsl:choose>
                <xsl:text>"</xsl:text>
            </div>
        </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="negeso:hits"/>
</xsl:template>

<xsl:template match="negeso:hits">
	<div class="search_results">
    <xsl:for-each select="negeso:hit">
        <div class="smMarginBottom_10">
            <div class="smResultTitle">
                <xsl:value-of select="@id"/>.&#160;
				<a href="{@filename}">
					<xsl:if test="@imageUri != ''">
						<img src="{@imageUri}" alt="{@title}" style="width: 100px;"/>
					</xsl:if>
                </a>
				<a href="{@filename}">
					<xsl:value-of select="@title"/>
				</a>
				<xsl:if test="@productNumber and not(/negeso:page/@role-id = 'guest')">
					<div class="sm_items">
						<button class="js_minus" />
						<input id="js_counter" class="quantity js_counter_search" name="product_amount" type="text">
							<xsl:attribute name="value">
								<xsl:choose>
									<xsl:when test="@multipleOf = '0' or not(@multipleOf)" >
										<xsl:value-of select="1"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="@multipleOf"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</input>
						<span style="display: none" class="multipleOf">
							<xsl:choose>
								<xsl:when test="@multipleOf = '0' or not(@multipleOf)" >
									<xsl:value-of select="1"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="@multipleOf"/>
								</xsl:otherwise>
							</xsl:choose>
						</span>
						<button class="js_plus" />
						<span data-ordercode="{@orderCode}" class="sm_product-cart" onclick="addPtoductSearchInCart(event, this) "/>
					</div>
				</xsl:if>
            </div>
            <xsl:value-of select="." disable-output-escaping="yes" />

        </div>

    </xsl:for-each>
	</div>
</xsl:template>

<!-- PAGE NAVIGATOR START -->
<xsl:template match="negeso:search_pages" mode="paging">
	<!-- CSS styles are inside page.css -->
	<!-- 'paging_style' can be 'old' or 'new' -->
	<xsl:param name="paging_style">old</xsl:param>

	<xsl:choose>
		<xsl:when test="$paging_style='old'">
			<xsl:if test="count(negeso:search_page) &gt; 1">
				<script type="text/javascript" src="/site/core/script/paging.js">/**/</script>
				<div class="bl-right paging">
					<table cellpadding="0" cellspacing="0" border="0">
						<tr>
							<xsl:if test="negeso:search_page[@current]/@id &gt; 1">
								<td>
									<a>
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="name"  select="'CORE.PREV'"/>
										</xsl:call-template>
										<xsl:attribute name="href">
											<xsl:choose>
												<xsl:when test="/negeso:page/negeso:request/negeso:parameter[@name='mode']/negeso:value/text() = 'advanced'">
                                                    <xsl:text>?page=</xsl:text>
                                                    <xsl:value-of select="number(negeso:search_page[@current]/@id)-1"/>
                                                    <xsl:text>&amp;mode=advanced</xsl:text>
												</xsl:when>
												<xsl:otherwise>
                                                    <xsl:text>?page=</xsl:text>
                                                    <xsl:value-of select="number(negeso:search_page[@current]/@id)-1"/>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:attribute>
										&lt;&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'CORE.PREV')"/>
									</a>
								</td>
							</xsl:if>
							<td>
								<form method="get">
									<xsl:if test="/negeso:page/negeso:request/negeso:parameter[@name='mode']/negeso:value/text() = 'advanced'">
										<input type="hidden" name="mode" value="advanced" />
									</xsl:if>
									<input type="text" name="page" value="{negeso:search_page[@current]/@id}" onkeyup="handle_paging(event, this, {count(negeso:search_page)})" title="LEFT ARROW = page-1; RIGHT ARROW = page+1; ENTER BUTTON = Go!"/>
								</form>
							</td>
                            <td>
                                /&#160;<xsl:value-of select="count(negeso:search_page)" />
                            </td>
							<xsl:if test="negeso:search_page[@current]/@id &lt; count(negeso:search_page)">
								<td>
									<a>
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="name"  select="'CORE.NEXT'"/>
										</xsl:call-template>
										<xsl:attribute name="href">
											<xsl:choose>
                                                <xsl:when test="/negeso:page/negeso:request/negeso:parameter[@name='mode']/negeso:value/text() = 'advanced'"><xsl:text>?page=</xsl:text><xsl:value-of select="number(negeso:search_page[@current]/@id)+1"/><xsl:text>&amp;mode=advanced</xsl:text></xsl:when>
                                                <xsl:otherwise><xsl:text>?page=</xsl:text><xsl:value-of select="number(negeso:search_page[@current]/@id)+1"/></xsl:otherwise></xsl:choose>
										</xsl:attribute>
										<xsl:value-of select="java:getString($dict_common, 'CORE.NEXT')"/>&#160;&gt;&gt;
									</a>
								</td>
							</xsl:if>
						</tr>
					</table>
				</div>
			</xsl:if>
		</xsl:when>
		<xsl:when test="$paging_style='new'">
			<xsl:if test="count(negeso:search_page)>1">
				<div class="pages_list">
					<xsl:for-each select="negeso:search_page">
						<a>
							<xsl:attribute name="href">
								<xsl:choose>
                                    <xsl:when test="/negeso:page/negeso:request/negeso:parameter[@name='mode']/negeso:value/text() = 'advanced'"><xsl:text>?page=</xsl:text><xsl:value-of select="@id"/><xsl:text>&#38;mode=advanced</xsl:text></xsl:when>
                                    <xsl:otherwise><xsl:text>?page=</xsl:text><xsl:value-of select="@id"/></xsl:otherwise></xsl:choose>
							</xsl:attribute>
							<xsl:if test="@current">
								<xsl:attribute name="class">current_page</xsl:attribute>
							</xsl:if>
							<xsl:value-of select="@id"/>
						</a>
						<!--&#160;-->
					</xsl:for-each>
				</div>
			</xsl:if>
		</xsl:when>
	</xsl:choose>
</xsl:template>

<!-- *************** NEGESO SEARCH END ***************** -->

</xsl:stylesheet>
