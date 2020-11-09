<?xml version="1.0" encoding="UTF-8"?>
<!--

  Copyright (c) 2003 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.


  Common xsl template for Newsletter module

  @version		$Revision$
  @author		Alexander Shkabarnya
  @version		14/12/2005
  @author		Volodymyr Snigur
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">


<xsl:variable name="lang" select="/negeso:page/@lang"/>
<xsl:variable name="dict_ex_store_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('store_locator', $lang)"/>

<xsl:template match="negeso:ex_store_locator" >
    <div class="contentStyle">
    	<xsl:if test="$outputType = 'admin' and not(/negeso:page/@role-id = 'visitor')">
    		<img class="hand imgt" onClick="window.open('store_locator','store_locator','height=600,width=800,toolbar=no,status=no,resizable=yes,scrollbars=yes');" src="/images/mark_1.gif" />
    	</xsl:if>
    	<xsl:choose>
    		<xsl:when test="@mode='error'">
				<span class="esl_redText bold">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_ERROR_OCCURED'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_ERROR_OCCURED')"/>
				</span>
			</xsl:when>
			<xsl:when test="@mode='main_page'">
				<div class="esl_title">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_STORE_LOCATOR'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_STORE_LOCATOR')"/>
				</div>
				<div class="esl_doSearchLnk">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_NAME_SEARCH'"/>
					</xsl:call-template>
					<a href="?mode=name_search"><xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_NAME_SEARCH')"/></a>
				</div>
				<div class="esl_doSearchLnk">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_DETAILED_SEARCH'"/>
					</xsl:call-template>
					<a href="?mode=detailed_search"><xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_DETAILED_SEARCH')" /></a>
				</div>
			</xsl:when>
			<xsl:when test="@mode='name_search'">
				<!-- show search name form -->
				<xsl:call-template name="esl_search_name" />
			</xsl:when>
			<xsl:when test="@mode='do_name_search'">
				<!-- show search by name results -->
				<xsl:call-template name="esl_name_results" />
			</xsl:when>
			<xsl:when test="@mode='detailed_search'">
				<!-- show detailed search -->
				<xsl:call-template name="esl_show_detailed_search" />
			</xsl:when>
			<xsl:when test="@mode='do_detailed_search'">
				<!-- show detailed search results -->
				<xsl:call-template name="esl_show_detailed_results" />
			</xsl:when>
			<xsl:when test="@mode='show_store'">
				<!-- show store datails from name search -->
				<xsl:call-template name="esl_show_store_details" >
					<xsl:with-param name="formValue" select='"do_name_search"' />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@mode='show_store2'">
				<!-- show store datails from detailed search -->
				<xsl:call-template name="esl_show_store_details">
					<xsl:with-param name="formValue" select='"do_detailed_search"' />
				</xsl:call-template>
			</xsl:when> 
			<xsl:when test="@mode='force_show'">
				<!-- show store details, but no one don't know when the force_show appeared -->
				<xsl:call-template name="esl_show_store_details" />
			</xsl:when>
    	</xsl:choose>
    </div>
</xsl:template>

<xsl:template name="esl_search_name">
<!-- current XML tag is negeso:ex_store_locator
		show search name form
-->
	<div class="esl_title">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
			<xsl:with-param name ="name"  select="'ESL_FIND_STORE'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_FIND_STORE')" />
	</div>
	<form id="store_locator_form" method="post" enctype="multipart/form-data">
		<input type="hidden" name="mode" value="do_name_search"/>
		<table border="0" cellpadding="0" cellspacing="0" width="100%" class="esl_table">
			<col width="200" /><col width="*" />
			<tbody>
				<tr>
					<th>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
							<xsl:with-param name ="name"  select="'ESL_NAME'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_NAME')" />
					</th>
					<td><input name="name" type="text"/></td>
				</tr>
				<tr>
					<th class="esl_bord_bot">&#160;</th>
					<td class="esl_bord_bot"><button onclick="doNameSearch()">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
							<xsl:with-param name ="name"  select="'ESL_FIND'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_FIND')" />
					</button>
					</td>
				</tr>
			</tbody>
		</table>	
	</form>
	<xsl:call-template name="esl_backlink_bar">
		<xsl:with-param name="backURL" select='"?mode=main_page"' />
	</xsl:call-template>
</xsl:template>

<xsl:template name="esl_name_results">
	<!-- current XML tag is negeso:ex_store_locator
			show search by name results
	-->
	<div class="esl_title">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
			<xsl:with-param name ="name"  select="'ESL_SEARCH_RESULTS'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_SEARCH_RESULTS')" />
	</div>
	<form id="store_locator_form" method="post" enctype="multipart/form-data">
		<input type="hidden" name="mode" value="show_store"/>
		<input type="hidden" name="shop_id" value=""/>
		<xsl:apply-templates select="negeso:state"/>
	</form>
	<xsl:apply-templates select="negeso:store_list"/>
	<xsl:call-template name="esl_backlink_bar">
		<xsl:with-param name="backURL" select='"?mode=name_search"' />
	</xsl:call-template>
</xsl:template>

<xsl:template name="esl_show_detailed_search">
	<!-- current XML tag is negeso:ex_store_locator
			show detailed search
	-->
	<div class="esl_title">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
			<xsl:with-param name ="name"  select="'ESL_FIND_STORE'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_FIND_STORE')" />
	</div>
	<form id="store_locator_form" method="post" enctype="multipart/form-data" onsubmit="return doFind();">
		<input type="hidden" name="mode" value="" />
		<input type="hidden" name="shop_type" value=""/>
		<input type="hidden" name="level1" value=""/>
		<input type="hidden" name="level2" value=""/>
		<input type="hidden" name="level3" value=""/>
		<table border="0" cellpadding="0" cellspacing="0" width="100%" class="esl_table">
			<col width="250" /><col width="*" />
			<tbody>
				<!-- show search parameters select boxes -->
				<xsl:apply-templates select="negeso:search_parameters"/>
				<tr>
					<th>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
							<xsl:with-param name ="name"  select="'ESL_POSTCODE'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_POSTCODE')" />
					</th>
					<td>
						<input name="postcode" type="text" maxLength="4"/>
					</td>
				</tr>
				<tr>
					<th class="esl_bord_bot">&#160;</th>
					<td class="esl_bord_bot">
						<input class="esl_submit " type="submit" onclick="store_locator_form.submit();">
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
								<xsl:with-param name ="name"  select="'ESL_FIND'"/>
							</xsl:call-template>
							<xsl:attribute name="value"><xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_FIND')" /></xsl:attribute>
						</input>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
	<xsl:call-template name="esl_backlink_bar">
		<xsl:with-param name="backURL" select='"?mode=main_page"' />
	</xsl:call-template>
</xsl:template>

<xsl:template name="esl_show_detailed_results">
	<!-- current XML tag is negeso:ex_store_locator
			show detailed search results
	-->
	<div class="esl_title">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
			<xsl:with-param name ="name"  select="'ESL_SEARCH_RESULTS'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_SEARCH_RESULTS')" />
	</div>
	<form id="store_locator_form" method="post" enctype="multipart/form-data">
		<input type="hidden" name="mode" value="show_store2"/>
		<input type="hidden" name="shop_id" value=""/>
		<xsl:apply-templates select="negeso:state"/>
	</form>
	<xsl:apply-templates select="negeso:total_list"/>
	<xsl:apply-templates select="negeso:install_list"/>
	<xsl:apply-templates select="negeso:service_list"/>
	<xsl:call-template name="esl_backlink_bar">
		<xsl:with-param name="backURL" select='"?mode=detailed_search"' />
	</xsl:call-template>
</xsl:template>

<xsl:template name="esl_show_store_details">
	<!-- current XML tag is negeso:ex_store_locator
			show store details from name search
	-->
	<xsl:param name="formValue" select="0" />
	<xsl:apply-templates select="negeso:store" mode="detail"/>
	<form id="store_locator_form" method="post" enctype="multipart/form-data">
		<xsl:if test="@mode='show_store' or @mode='show_store2'">
			<input type="hidden" name="mode">
				<xsl:attribute name="value" ><xsl:value-of select="$formValue" /></xsl:attribute>
			</input>
			<xsl:apply-templates select="negeso:state"/>
		</xsl:if>
	</form>
	<xsl:call-template name="esl_backlink_bar">
		<xsl:with-param name="backURL" select='"javascript:store_locator_form.submit();"' />
	</xsl:call-template>
</xsl:template>

<xsl:template name="esl_backlink_bar">
<xsl:param name="backURL" select='"?"' />
	<div class="esl_bottomNavBar">
		<div class="esl_shoppingStepL">
			<!-- There are two anchors because in other case whitespace(&#160;) is also underlined-->
			<a href="{$backURL}">
				<img src="/site/modules/extended_store_locator/images/left.gif" />
			</a>
			&#160;
			<a href="{$backURL}">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
					<xsl:with-param name ="name"  select="'ESL_BACK'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_BACK')" />
			</a>
		</div>
	</div>
</xsl:template>

<xsl:template match="negeso:search_parameters">
	<xsl:apply-templates select="negeso:shop_types"/>
	<xsl:apply-templates select="negeso:level1s"/>
	<xsl:apply-templates select="negeso:level2s"/>
	<xsl:apply-templates select="negeso:level3s"/>
</xsl:template>

<xsl:template match="negeso:level3s">
	<xsl:call-template name="esl_show_levels">
		<xsl:with-param name="levelNumber" select="3" />
		<xsl:with-param name="selectName" select='"slevel3"' />
	</xsl:call-template>
</xsl:template>

<xsl:template match="negeso:level2s">
	<xsl:call-template name="esl_show_levels">
		<xsl:with-param name="levelNumber" select="2" />
		<xsl:with-param name="selectName" select='"slevel2"' />
	</xsl:call-template>
</xsl:template>

<xsl:template match="negeso:level1s">
	<xsl:call-template name="esl_show_levels">
		<xsl:with-param name="levelNumber" select="1" />
		<xsl:with-param name="selectName" select='"slevel1"' />
	</xsl:call-template>
</xsl:template>

<xsl:template name="esl_show_levels">
	<xsl:param name="levelNumber" select="0" />
	<xsl:param name="selectName" select='"slevel1"' />
	<tr>
		<th>
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
				<xsl:with-param name ="name"  select="'ESL_DISCIPLINE_LEVEL'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_DISCIPLINE_LEVEL')" />&#160;<xsl:value-of select="$levelNumber" /></th>
		<td>
			<select onchange="disableAll(); refreshParameters();">
				<xsl:attribute name="name"><xsl:value-of select="$selectName" /></xsl:attribute>
				<xsl:apply-templates select="negeso:level"/>
			</select>
		</td>
	</tr>
</xsl:template>

<xsl:template match="negeso:level">
	<xsl:choose>
		<xsl:when test="@selected='true'">
			<option value="{@id}" selected="true"><xsl:value-of select="@name"/></option>
		</xsl:when>
		<xsl:otherwise>
			<option value="{@id}"><xsl:value-of select="@name"/></option>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="negeso:shop_types">
	<tr>
		<th>
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
				<xsl:with-param name ="name"  select="'ESL_SHOP_TYPES'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_SHOP_TYPES')" /></th>
		<td>
			<select name="sshop_type" onchange="disableAll(); shopTypeChanged(); refreshParameters();">
				<xsl:apply-templates select="negeso:shop_type"/>
			</select>
		</td>
	</tr>
</xsl:template>

<xsl:template match="negeso:shop_type">
	<xsl:choose>
		<xsl:when test="@selected='true'">
			<option value="{@id}" selected="true"><xsl:value-of select="@name"/></option>
		</xsl:when>
		<xsl:otherwise>
			<option value="{@id}"><xsl:value-of select="@name"/></option>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="negeso:store_list">
	<!-- show search results -->
	<xsl:call-template name="esl_show_result_table" />
</xsl:template>

<xsl:template name="esl_show_result_table">
	<xsl:choose>
		<xsl:when test="count(negeso:store)=0">
			<div class="esl_redText esl_mbt8">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
					<xsl:with-param name ="name"  select="'ESL_NO_MATCHES_FOUND'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_NO_MATCHES_FOUND')" />
			</div>
		</xsl:when>
		<xsl:otherwise>
			<table cellpadding="0" cellspacing="0" width="100%" border="0" class="esl_resultTable">
				<col width="320" /><col width="*" />
				<thead>
					<tr>
						<td >
							<div>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
									<xsl:with-param name ="name"  select="'ESL_NAME'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_NAME')" />
							</div>
						</td>
						<td class="esl_thead_bordLeft">
							<div>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
									<xsl:with-param name ="name"  select="'ESL_RAYON_NAME'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_RAYON_NAME')" />
							</div>
						</td>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="negeso:store"/>
				</tbody>
			</table>
			<br/>
			<br/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="negeso:store">
	<tr>
		<th>
			<xsl:if test="not(following-sibling::*)">
				<xsl:attribute name="class">esl_bord_bot</xsl:attribute>
			</xsl:if>
			<a href="javascript:showShopDetails({@id});"><xsl:value-of select="@name"/></a>
		</th>
		<td>
			<xsl:if test="not(following-sibling::*)">
				<xsl:attribute name="class">esl_bord_bot</xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="(@name2) and (@name2!='')">
					<a href="javascript:showShopDetails({@id})"><xsl:value-of select="@name2"/></a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&#160;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</tr>
</xsl:template>

<xsl:template match="negeso:total_list">
	<xsl:call-template name="esl_show_result_table" />
</xsl:template>

<xsl:template match="negeso:install_list"> 
	<div class="esl_title">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
			<xsl:with-param name ="name"  select="'ESL_INSTAL_SHOPS'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_INSTAL_SHOPS')" />
	</div>
	<xsl:call-template name="esl_show_result_table" />
</xsl:template>

<xsl:template match="negeso:service_list">
	<div class="esl_title">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
			<xsl:with-param name ="name"  select="'ESL_SERVICE_SHOPS'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_SERVICE_SHOPS')" />
	</div>
	<xsl:call-template name="esl_show_result_table" />
</xsl:template>


<xsl:template match="negeso:store" mode="detail">
	<MARQUEE direction="right" width="400" height="20"><xsl:value-of select="@marquee"/></MARQUEE><br/>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" class="esl_table">
		<col width="250" /><col width="*" />
		<tbody>
			<tr>
				<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_NAME'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_NAME')" /></th>
				<td><xsl:value-of select="@name"/>&#160;</td>
			</tr>
			<tr>
				<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_RAYON_NAME'"/>
					</xsl:call-template>
				<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_RAYON_NAME')" /></th>
				<td><xsl:value-of select="@name2"/>&#160;</td>
			</tr>
			<tr>
				<th><xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_ADDRESS')" /></th>
				<td><xsl:value-of select="@address"/>&#160;</td>
			</tr>
			<tr>
				<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_AREACODE'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_AREACODE')" /></th>
				<td><xsl:value-of select="@areacode"/>&#160;</td>
			</tr>
			<tr>
				<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_CITY'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_CITY')" /></th>
				<td><xsl:value-of select="@city"/>&#160;</td>
			</tr>
			<tr>
				<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_PHONE'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_PHONE')" /></th>
				<td><xsl:value-of select="@phone"/>&#160;</td>
			</tr>
			<tr>
				<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_EMAIL'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_EMAIL')" /></th>
				<td><xsl:value-of select="@email"/>&#160;</td>
			</tr>
			<tr>
				<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_WEB_ADDRESS'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_WEB_ADDRESS')"/></th>
				<td><xsl:value-of select="@web_address"/>&#160;</td>
			</tr>
			<tr>
				<th class="esl_bord_bot">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_ex_store_module"/>
						<xsl:with-param name ="name"  select="'ESL_DISCIPLINES'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_ex_store_module, 'ESL_DISCIPLINES')" /></th>
				<td class="esl_bord_bot"><xsl:apply-templates select="negeso:discipline_list/negeso:discipline"/>&#160;</td>
			</tr>
		</tbody>
	</table>
	<br/>
	<div id="desc_div" class="contentStyle">
		<xsl:value-of select="text()" disable-output-escaping="yes" />
	</div>
	<xsl:if test="$outputType = 'admin'">
		<script>
			makeReadonly(document.getElementById('desc_div'), true);
		</script>
	</xsl:if>
	<br/>
</xsl:template> 

<xsl:template match="negeso:discipline">
	<xsl:value-of select="@name"/><br/>
</xsl:template > 

<xsl:template match="negeso:state">  
	<input type="hidden" name="name" value="{@name}"/>
	<input type="hidden" name="shop_type" value="{@shop_type}"/>
	<input type="hidden" name="level1" value="{@level1}"/>
	<input type="hidden" name="level2" value="{@level2}"/>
	<input type="hidden" name="level3" value="{@level3}"/>
	<input type="hidden" name="postcode" value="{@postcode}"/>
</xsl:template>

</xsl:stylesheet>