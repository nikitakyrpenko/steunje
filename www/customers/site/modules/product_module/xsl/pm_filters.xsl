<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Product Module
  
  Here you can find all PM Filter templates.
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<!-- Filter results. Used in "Category Filter" and at the Advanced Search results -->
<xsl:template match="negeso:pm-filter" mode="filterResults">
	<xsl:param name="mode">thumbnails</xsl:param>
	<xsl:if test="@response = 'RESULT_OK' and @view and @view='filterResults'">
			<xsl:choose>
				<xsl:when test="negeso:filterResults/negeso:pm-product">
					<xsl:for-each select="negeso:filterResults">
						<xsl:apply-templates select="negeso:pm-product" mode="overview">
							<xsl:with-param name="mode" select="$mode" />
						</xsl:apply-templates>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<h2>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_NOTHING_FOUND'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_NOTHING_FOUND')"/>
							</h2>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:if>
</xsl:template>

<!-- Filter presets container -->
<!-- For normal work - should be only one per page. -->
<xsl:template match="negeso:pm-filter" mode="filterPresets">
	<!-- Check 1: container should be called once -->
	<xsl:if test="position()=1">
			<script type="text/javascript" src="/site/modules/product_module/script/pm_filters_presets.js">/**/</script>
			<xsl:variable name="current_filter_id">
				<xsl:choose>
					<xsl:when test="//negeso:contents/negeso:pm-filter[@view='filterResults']"><xsl:value-of select="//negeso:contents/negeso:pm-filter[@view='filterResults']/@id"/></xsl:when>
					<xsl:when test="//negeso:parameter[@name = 'filterId'] and not(//negeso:pm-filter[@type='CategorialFilter'])"><xsl:value-of select="//negeso:parameter[@name = 'filterId']/negeso:value/text()"/></xsl:when>
					<xsl:otherwise><xsl:value-of select="//negeso:contents/negeso:pm-filter[1]/@id"/></xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<script type="text/javascript">
				// current_filter_id is defined in filters_presets.js
				current_filter_id = '<xsl:value-of select="$current_filter_id" />';
			</script>
		<table cellpadding="0" cellspacing="0" border="0" width="100%" class="b-pmFilterPresets">
				<!-- If we have a set of presets -->
				<xsl:if test="position()!=last()">
					<tr>
						<td>
							<strong>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_FILTER_PRESETS'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_FILTER_PRESETS')"/></strong>
							&#160;
							<select onchange="showFilterPreset(this.value);">
								<xsl:for-each select="../negeso:pm-filter">
									<xsl:if test="not(@isHidden='true')">
										<option value="{@id}">
											<xsl:call-template name ="add-constant-info">
												<xsl:with-param name ="dict"  select="$dict_pm_module"/>
												<xsl:with-param name ="name"  select="@name-key"/>
											</xsl:call-template>
											<xsl:if test="@id=$current_filter_id">
												<xsl:attribute name="selected">selected</xsl:attribute>
											</xsl:if>
											<xsl:variable name="filter_name" select="@name-key"/>
											<xsl:value-of select="java:getString($dict_pm_module, $filter_name)"/>
										</option>
									</xsl:if>
								</xsl:for-each>
							</select>
						</td>
					</tr>
				</xsl:if>
				<tr>
					<td>
						<xsl:apply-templates select="../negeso:pm-filter" mode="filterPreset" />
					</td>
				</tr>
				<!-- Check for using this template for CategoryFilter -->
				<xsl:if test="not(@type='CategorialFilter')">
					<tr>
						<td>
							<input class="submit" type="button" onclick="filterGo(current_filter_id)" style="margin-left: 0">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_SEARCH'"/>
								</xsl:call-template>
								<xsl:attribute name="value"><xsl:value-of select="java:getString($dict_pm_module, 'PM_SEARCH')"/></xsl:attribute>
							</input>
						</td>
					</tr>
				</xsl:if>
			</table>
			<!-- If we have a set of presets -->
			<xsl:if test="position()!=last()">
				<script type="text/javascript">
					// showFilterPreset(...) is defined in filters_presets.js
					showFilterPreset('<xsl:value-of select="$current_filter_id" />');
				</script>
			</xsl:if>
	</xsl:if>
</xsl:template>

<!-- This template can be used, as stand-alone representation of SINGLE filter
     OR as part of XSL template: [xsl:template match="negeso:pm-filter" mode="filterPresets"] -->
<xsl:template match="negeso:pm-filter" mode="filterPreset">
	<xsl:param name="view">filterResults</xsl:param>
	<xsl:param name="with_design">false</xsl:param>
	<!-- This template should be called from [xsl:template match="negeso:pm-filter" mode="filterPresets"] -->
	<!-- OR as separate call with [xsl:with-param name="with_design" select="'true'"] -->
	<xsl:choose>
		<xsl:when test="$with_design='false'">
			<div class="bl-left" style="padding: 0; margin: 0; display: none;" id="div_filter_{@id}">
				<xsl:if test="position()=last()">
					<xsl:attribute name="style">padding: 0; margin: 0; display: block;</xsl:attribute>
				</xsl:if>
				<form name="pmFilterFormName_{@id}" id="pmFilterFormName_{@id}" method="POST">
					<xsl:attribute name="action"><xsl:call-template name="pm_adv_filter_results_link" /></xsl:attribute>
					<input type="hidden" name="view" value="{$view}" />
					<input type="hidden" name="filterId" value="{@id}" />
					<input type="hidden" name="pmCatId" value="{/negeso:page/negeso:request/negeso:parameter[@name = 'pmCatId']/negeso:value/text()}" />
					<div class="bold">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_FILTER_PRODUCTS'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_FILTER_PRODUCTS')"/>
					</div>
					<xsl:apply-templates select="negeso:filterAttributes/negeso:attribute"/>
				</form>
			</div>
			<script type="text/javascript">
				// filter_ids variable is defined in filters_presets.js
				try {
					filter_ids.push('<xsl:value-of select="@id" />');
				} catch(e) {}
			</script>
		</xsl:when>
		<xsl:when test="$with_design='true'">
				<form name="pmFilterFormName" method="POST">
					<xsl:attribute name="action"><xsl:call-template name="pm_adv_filter_results_link" /></xsl:attribute>
					<input type="hidden" name="view" value="{$view}" />
					<input type="hidden" name="filterId" value="{@id}" />
					<input type="hidden" name="pmCatId" value="{/negeso:page/negeso:request/negeso:parameter[@name = 'pmCatId']/negeso:value/text()}" />
				<table cellpadding="0" cellspacing="0" border="0" width="100%" class="b-pmFilterPresets">
						<tr>
							<td>
								<div class="bl-left" style="padding: 0; margin: 0;" id="div_filter_{@id}">
									
										<div class="bold">
											<xsl:call-template name ="add-constant-info">
												<xsl:with-param name ="dict"  select="$dict_pm_module"/>
												<xsl:with-param name ="name"  select="'PM_FILTER_PRODUCTS'"/>
											</xsl:call-template>
											<xsl:value-of select="java:getString($dict_pm_module, 'PM_FILTER_PRODUCTS')"/>
										</div>
										<xsl:apply-templates select="negeso:filterAttributes/negeso:attribute"/>
									
								</div>
							</td>
						</tr>
						<!-- Check for using this template for CategoryFilter -->
						<xsl:if test="not(@type='CategorialFilter')">
							<tr>
								<td>
									<input class="submit" type="button" onclick="this.form.elements['view'].value = 'filterResults'; this.form.submit()" style="margin-left: 0">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_pm_module"/>
											<xsl:with-param name ="name"  select="'PM_SEARCH'"/>
										</xsl:call-template>
										<xsl:attribute name="value"><xsl:value-of select="java:getString($dict_pm_module, 'PM_SEARCH')"/></xsl:attribute>
									</input>
								</td>
							</tr>
						</xsl:if>
					</table>
				</form>
		</xsl:when>
	</xsl:choose>
</xsl:template>


<xsl:template match="negeso:filterAttributes">
    <xsl:apply-templates select="negeso:attribute" />
</xsl:template>

<xsl:template match="negeso:attribute" >
    <xsl:if test="not(@isHidden='true')">
		<div class="bl-left" style="white-space: nowrap; margin-top: 2px; margin-bottom: 2px;">
			<xsl:value-of select="@rsName"/>&#160;
			<xsl:choose>
				<xsl:when test="@dropDownOption='true'">
			        <select name="{@name}" onChange="this.form.action = ''; this.form.submit()">
			           	<xsl:if test="@isMultiple='true'">
				            <xsl:attribute name="multiple">true</xsl:attribute> 					        		
 						            <xsl:attribute name="number">3</xsl:attribute> 					        		
			        	</xsl:if>
			        	<option value="">All&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</option>
						<xsl:for-each select="negeso:options/negeso:option">
						<option>
							<xsl:attribute name="value">
								<xsl:value-of select="@id"/>									
							</xsl:attribute> 							
							<xsl:if test="@selected='true'">
								<xsl:attribute name="selected">
									true
								</xsl:attribute>
							</xsl:if>  			
							<xsl:value-of select="."/>
			 			</option>
				 	    </xsl:for-each>
			        </select>
			        &#160;&#160;
				</xsl:when>
				<xsl:otherwise>
					<input type="text" value="" style="height: 16px" >
						<xsl:attribute name="name">
							<xsl:value-of select="@name"></xsl:value-of>
						</xsl:attribute>
						<xsl:attribute name="value">
							<xsl:value-of select="@value"></xsl:value-of>
						</xsl:attribute>
					</input>
			       &#160;&#160;
				</xsl:otherwise>
			</xsl:choose>
			&#160;&#160;
		</div>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>