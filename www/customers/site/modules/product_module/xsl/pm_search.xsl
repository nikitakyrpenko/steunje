<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Product Module
  
  In this file you may find product search templates. All except filters.
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<!--===================================== PM SEARCH FORM ======================================-->    


<xsl:template name="pm_free_search">
	<div class="b-pmSearch">
				<form>
					<span>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_FREE_SEARCH'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_FREE_SEARCH')"/>
					</span>
					<br/>
					<input value="" name="search" type="text"/>
					<button name="submit" type="submit" class="submit">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_pm_module"/>
							<xsl:with-param name ="name"  select="'PM_GO_SEARCH'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_pm_module, 'PM_GO_SEARCH')"/>
					</button>
				</form>
	</div>
</xsl:template>

<xsl:template name="pm_serial_search">
	<xsl:if test="//negeso:pm-filter[@id=1]">
		<div class="b-pmSearch">
					<form>
						<xsl:attribute name="action">
							<xsl:call-template name="pm_adv_filter_results_link" />
						</xsl:attribute>
					
						<input type="hidden" name="view" style="display: none" value="filterResults"/>
						<input type="hidden" name="filterId" style="display: none" value="{../negeso:pm-filter[@id=1]/@id}" />
						<input type="hidden" name="pmSearchCatId" style="display: none" value="{/negeso:page/negeso:request/negeso:parameter[@name = 'pmSearchCatId']/negeso:value/text()}" />
						<span>
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_pm_module"/>
								<xsl:with-param name ="name"  select="'PM_SERIAL_SEARCH'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_pm_module, 'PM_SERIAL_SEARCH')"/>
						</span>
						<br/>
						<input value="" name="SN" type="text"/>
						<button name="submit" type="submit" class="submit">
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_pm_module"/>
								<xsl:with-param name ="name"  select="'PM_GO_SEARCH'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_pm_module, 'PM_GO_SEARCH')"/>
						</button>
					</form>
		</div>
	</xsl:if>
</xsl:template>

<xsl:template name="pm_adv_search_link">
<a onFocus="blur()" class="b-btnSearch">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_pm_module"/>
						<xsl:with-param name ="name"  select="'PM_ADV_SEARCH'"/>
					</xsl:call-template>
					<xsl:attribute name="href">
						<xsl:call-template name="pm_adv_filter_results_link" />
					</xsl:attribute>
					<xsl:value-of select="java:getString($dict_pm_module, 'PM_ADV_SEARCH')"/>&#160;&#62;&#62;
				</a>
</xsl:template>

</xsl:stylesheet>