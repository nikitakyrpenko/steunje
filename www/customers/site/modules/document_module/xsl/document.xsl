<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Document Module
 
  @version		2007.12.25
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="dict_document_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('document_module', $lang)"/>
 
<!-- UNKNOW XSL TEMPLATES START -->
<!-- It is not clear where they used and what they do, tag generation code is in JAR file document java files
	  But not used in this XSl file since 1st file version.
	  It is still not clear the BACK functionality.
 -->
<xsl:template match="negeso:document_catalog_component" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/document_module/css/document.css"/>
	<script type="text/javascript" src="/site/modules/document_module/script/document.js">/**/</script>
</xsl:template>

<xsl:template name="documentmodule">
    
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_document_module"/>
										<xsl:with-param name ="name"  select="'DC_LIBRARY'"/>
									</xsl:call-template>
    <div class="b-dcEmpty">
									<xsl:if test="$outputType = 'admin' and not(/negeso:page/@role-id = 'visitor')">
										<img src="/images/mark_1.gif" class="hand" onClick="window.open('document_module', '_blank', 'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"/>
										<xsl:text>&#160;</xsl:text>
									</xsl:if>
									<xsl:value-of select="java:getString($dict_document_module, 'DC_LIBRARY')"/>
					</div>
    <br/>
										<xsl:apply-templates select="//negeso:article"/>
									<xsl:choose>
										<xsl:when test="count(//negeso:category) &gt; 0 or count(//negeso:search_results) &gt; 0">
											<!-- view dc panel -->
											<xsl:apply-templates select="negeso:document_catalog_component"/>
										</xsl:when>
										<xsl:otherwise>
            <div>
												<xsl:call-template name ="add-constant-info">
													<xsl:with-param name ="dict"  select="$dict_document_module"/>
													<xsl:with-param name ="name"  select="'DC_EMPTY_CATALOG'"/>
												</xsl:call-template>
												<xsl:value-of select="java:getString($dict_document_module, 'DC_EMPTY_CATALOG')"/>
											</div>
										</xsl:otherwise>
									</xsl:choose>
</xsl:template>								
									
<xsl:template match="negeso:document_catalog_component">
	
	<form name="dcForm" method="GET" enctype="multipart/form-data" class="b-dcForm">
		<input type="hidden" id="current_folder_id" name="current_folder_id"/>
		<input type="hidden" id="mode" name="mode" value="search" />

		<!-- show results or current path -->
		<xsl:if test="(@mode='search') and not(negeso:search_results/negeso:document)">
			<div class="b-dcEmpty">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_document_module"/>
					<xsl:with-param name ="name"  select="'DC_NO_DOCUMENT'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_document_module, 'DC_NO_DOCUMENT')"/>
			</div>
		</xsl:if>
		<!-- show panel with documents -->
		<xsl:choose>
			<xsl:when test="(@mode='search') and not(negeso:search_results/negeso:document)" />
			<xsl:otherwise>
				<div class="b-dcMain">
					<xsl:call-template name="dc_panelview" />
				</div>
			</xsl:otherwise>
		</xsl:choose>
		<br/>
		<!-- show search bar: begin -->
		<table cellpadding="0" cellspacing="0" border="0" class="b-dcSearch">
				<tr>
					<td>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_document_module"/>
							<xsl:with-param name ="name"  select="'DC_SEARCH'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_document_module, 'DC_SEARCH')"/>:
					</td>
					<td>
						<input style="width: 140px" type="text" required="true" name="search_word" onkeypress="if (event.keyCode=='13') return doFileSearch();" >
							<xsl:attribute name="value">
								<xsl:value-of select="negeso:search_word/text()"/>
							</xsl:attribute>
					</input>&#160;
					</td>
					<td>
						<input type="button" class="submit" onclick="doFileSearch();">
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_document_module"/>
								<xsl:with-param name ="name"  select="'DC_GO'"/>
							</xsl:call-template>
							<xsl:attribute name="value">
								<xsl:value-of select="java:getString($dict_document_module, 'DC_GO')"/>
							</xsl:attribute>
						</input>
					</td>
				</tr>
			</table>
		<!-- show search bar: end -->

		<!-- show back bar -->
		<xsl:if test="(@mode='search') and (negeso:search_results)">
				<input type="button" class="submit" onclick="goBack()">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_document_module"/>
						<xsl:with-param name ="name"  select="'DC_BACK'"/>
					</xsl:call-template>
					<xsl:attribute name="value">
						<xsl:value-of select="java:getString($dict_document_module, 'DC_BACK')"/>
					</xsl:attribute>
				</input>
		</xsl:if>
	</form>
</xsl:template>

<xsl:template name="dc_panelview">
	<table cellpadding="0" cellspacing="10" border="0">
		<tr>
			<th>
					<xsl:text>&#160;</xsl:text>
			</th>
			<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_document_module"/>
						<xsl:with-param name ="name"  select="'DC_NAME'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_document_module, 'DC_NAME')"/>
			</th>
			<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_document_module"/>
						<xsl:with-param name ="name"  select="'DC_DESCRIPTION'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_document_module, 'DC_DESCRIPTION')"/>
			</th>
			<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_document_module"/>
						<xsl:with-param name ="name"  select="'DC_OWNER'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_document_module, 'DC_OWNER')"/>
			</th>
			<th>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_document_module"/>
						<xsl:with-param name ="name"  select="'DC_LAST_MODIFIED'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_document_module, 'DC_LAST_MODIFIED')"/>
			</th>
		</tr>
  		<xsl:choose>
			<xsl:when test="@mode='search'">
				<xsl:choose>
					<xsl:when test="negeso:search_results/negeso:document">
						<xsl:apply-templates select="negeso:search_results/negeso:document" mode="search"/>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td colspan="5">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_document_module"/>
									<xsl:with-param name ="name"  select="'DC_NO_DOCUMENT'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_document_module, 'DC_NO_DOCUMENT')"/>
							</td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="negeso:document_catalog/negeso:category"/>
			</xsl:otherwise>
		</xsl:choose>
	</table>
</xsl:template>

<xsl:template match="negeso:category">
	<xsl:choose>
		<xsl:when test="@is_current='true'">
			<xsl:if test="not(@is_root='true')" >
				<tr>
					<xsl:call-template name="dc_content_empty_td" />
					<td>
						<a href="?current_folder_id={@parent_id}" onfocus="blur()">
							<xsl:text>&#091;&#046;&#046;&#093;</xsl:text>
						</a>
					</td>
					<xsl:call-template name="dc_content_empty_td" />
					<xsl:call-template name="dc_content_empty_td" />
					<xsl:call-template name="dc_content_empty_td" />
				</tr>
			</xsl:if>
			<!-- show folders -->
			<xsl:apply-templates select="negeso:category" mode="list"/>
			<!-- show folder content -->
			<xsl:apply-templates select="negeso:document" mode="list"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates select="negeso:category"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="dc_content_empty_td">
	<td>&#160;</td>
</xsl:template>

<xsl:template match="negeso:category" mode="list">
	<tr>
		<td>
			<a href="?current_folder_id={@id}" onfocus="blur()">
				<img src="/site/modules/document_module/images/dc_folder_closed.gif" class="dc_imglink" alt="" />
			</a>
		</td>
		<td>
			<a href="?current_folder_id={@id}" onfocus="blur()">
				<xsl:value-of select="@name"/>
			</a>
		</td>
		<xsl:call-template name="dc_content_empty_td" />
		<xsl:call-template name="dc_content_empty_td" />
		<xsl:call-template name="dc_content_empty_td" />
	</tr>
</xsl:template>

<xsl:template match="negeso:document" mode="list">
	<xsl:call-template name="dc_show_document_info" />
</xsl:template>


<xsl:template match="negeso:document" mode="search">
	<xsl:call-template name="dc_show_document_info" />
</xsl:template>

<xsl:template name="dc_show_document_info" >
	<tr>
		<td>
			<a target="_blank" href="{@document_link}" onfocus="blur()">
				<img src="/site/modules/document_module/images/dc_doc.gif" class="dc_imglink" />
			</a>
		</td>
		<td>
			<xsl:if test="count(//negeso:search_results) &gt; 0">
				<a href="?current_folder_id={@category_id}">
					<xsl:value-of select="@cat_name"/>
				</a>
				/
			</xsl:if>
			<a target="_blank" href="{@document_link}" onfocus="blur()">
				<xsl:value-of select="@name"/>
			</a>
		</td>
		<td>
			<xsl:value-of select="@description"/>&#160;
		</td>
		<td>
			<xsl:value-of select="@owner"/> 
		</td>
		<td>
			<xsl:value-of select="@last_modified"/>
		</td>
	</tr>
</xsl:template>


<!-- WHERE AM I: BEGIN -->
<!-- Integrates into standard "Where Am I" -->
<xsl:template match="negeso:document_catalog_component" mode="where_am_i">
	<xsl:choose>
		<xsl:when test="count(child::negeso:search_results) = 0">
			<xsl:apply-templates select="negeso:document_catalog/negeso:category" mode="where_am_i" />
		</xsl:when>
		<xsl:otherwise>
			<div>
				<a>
					<xsl:attribute name="href">?current_folder_id=<xsl:value-of select="@category_id"/></xsl:attribute>
					<xsl:value-of select="//negeso:page/negeso:title" />
				</a>
				&#160;&#160;&gt;&gt;&gt;&#160;&#160;
			</div>
			<div class="here_am_i">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_document_module"/>
					<xsl:with-param name ="name"  select="'DC_RESULTS'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_document_module, 'DC_RESULTS')"/>
			</div>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="negeso:category" mode="where_am_i">
	<xsl:choose>
		<!-- xsl:when test="(count(child::negeso:category) = 0) or (count(descendant::negeso:category[@is_current='true']) = 0)" -->
		<xsl:when test="@is_current and not(negeso:search_results)">
			<div>
				<xsl:attribute name="class">here_am_i</xsl:attribute>
				<xsl:choose>
					<xsl:when test="@is_root='true' or negeso:search_results">
						<xsl:value-of select="//negeso:page/negeso:title" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="@name" />
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:when>
		<xsl:otherwise>
			<div>
				<a>
					<xsl:if test="not(count(child::negeso:category) = 0)">
						<xsl:attribute name="href">?current_folder_id=<xsl:value-of select="@id"/></xsl:attribute>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="@is_root='true' or negeso:search_results">
							<xsl:value-of select="//negeso:page/negeso:title" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="@name" />
						</xsl:otherwise>
					</xsl:choose>
				</a>
				&#160;&#160;&gt;&gt;&gt;&#160;&#160;
			</div>
			<xsl:apply-templates select="negeso:category" mode="where_am_i"/>
		</xsl:otherwise>
	</xsl:choose>
	
</xsl:template>
<!-- WHERE AM I: END -->


</xsl:stylesheet>
