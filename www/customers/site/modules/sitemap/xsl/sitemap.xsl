<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Sitemap
 
  @version		2008.01.20
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java"
>

<xsl:variable name="dict_sitemap" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('sitemap', $lang)"/>

<!-- Tree sitemap -->
<xsl:template name="sitemap">
	<xsl:param name="template_type" select="1" />
	<xsl:param name="table_spacing" select="3" />			
	<xsl:call-template name="sm_page_title_line_block">
		<xsl:with-param name="mode">
			<xsl:value-of select="@mode"/>
		</xsl:with-param>
	</xsl:call-template>				
	<xsl:choose>
		<xsl:when test="number($template_type) = 1">
			<table cellpadding="0" cellspacing="{$table_spacing}" border="0">
				<tr valign="top">
					<td align="left" class="heading" height="20" colspan="3">Site Map<br /></td>
				</tr>
				<tr valign="top">
					<td align="left" class="home" height="20" colspan="3">Front Page</td>
				</tr>
				<xsl:apply-templates select="/negeso:page/negeso:contents/negeso:main_menu/negeso:menu" mode="sitemap_vert"/>
			</table>
		</xsl:when>
		<xsl:when test="number($template_type) = 2">
			<xsl:variable name="colnumber" select="count(/negeso:page/negeso:contents/negeso:main_menu/negeso:menu/negeso:menu_item[@visible='true'])" />
			<table cellpadding="0" cellspacing="{$table_spacing}" border="0">
				<tr valign="top">
					<td align="left" class="heading" height="20">
						<xsl:attribute name="colspan"><xsl:value-of select="$colnumber"/></xsl:attribute>Site Map<br />
					</td>
				</tr>
				<tr valign="top">
					<td align="left" class="home" height="20">
					    <xsl:attribute name="colspan"><xsl:value-of select="$colnumber"/></xsl:attribute>Front Page</td>
				</tr>
									
				<xsl:variable name="testung">
				<xsl:call-template name="testung">
					<xsl:with-param name="menu_nodes" select="/negeso:page/negeso:contents/negeso:main_menu/negeso:menu/negeso:menu_item[@visible='true']"/>
					<xsl:with-param name="max_number" select="0"/>
				</xsl:call-template>
				</xsl:variable>
                <xsl:apply-templates select="/negeso:page/negeso:contents/negeso:main_menu/negeso:menu" mode="sitemap_hor">
					<xsl:with-param name="max_number" select="$testung" />
				</xsl:apply-templates>
			</table>
		</xsl:when>		
		<xsl:otherwise>
			<xsl:apply-templates select="/negeso:page/negeso:contents/negeso:main_menu/negeso:menu" mode="sitemap"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
<!-- Tree sitemap -->



<!-- Counts maximum number of the submenu items-->
<xsl:template name="testung">
	<xsl:param name="menu_nodes"/>
	<xsl:param name="max_number"/>
	<xsl:choose>
		<xsl:when test="count($menu_nodes) = 1">
			<xsl:choose>
				<xsl:when test="count($menu_nodes[1]/negeso:menu_item[@visible='true']) &gt; number($max_number)">
					<xsl:value-of select="count($menu_nodes[1]/negeso:menu_item[@visible='true'])" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$max_number" />
				</xsl:otherwise>
			</xsl:choose>			
		</xsl:when>
		<xsl:otherwise>
			<xsl:choose>
				<xsl:when test="count($menu_nodes[1]/negeso:menu_item[@visible='true']) &gt; number($max_number)">
					<xsl:call-template name="testung">
						<xsl:with-param name="menu_nodes" select="$menu_nodes[position() &gt; 1]"/>
						<xsl:with-param name="max_number" select="count($menu_nodes[1]/negeso:menu_item[@visible='true'])"/>
					</xsl:call-template>
       				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="testung">
						<xsl:with-param name="menu_nodes" select="$menu_nodes[position() &gt; 1]"/>
						<xsl:with-param name="max_number" select="$max_number"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>			
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
<!-- Horisontal sitemap -->

<!-- Tree sitemap -->
<xsl:template match="negeso:menu" mode="sitemap">
	<ul>
		<xsl:if test="count(ancestor::negeso:menu) = 0">
			<xsl:attribute name="class">sitemap</xsl:attribute>
		</xsl:if>
		<xsl:apply-templates select="negeso:menu_item[@visible='true']" mode="sitemap_li" />
	</ul>
</xsl:template>
<xsl:template match="negeso:menu_item" mode="sitemap">
	<ul><xsl:apply-templates select="negeso:menu_item[@visible='true']" mode="sitemap_li"/></ul>	
</xsl:template> 
<xsl:template match="negeso:menu_item" mode="sitemap_li">
	<li>
		<xsl:apply-templates select="self::node()" mode="link"/>
        <xsl:if test="count(child::negeso:menu_item[@visible='true']) &gt; 0">
		    <xsl:apply-templates select="self::negeso:menu_item[@visible='true']" mode="sitemap"/>
        </xsl:if>
	</li>
</xsl:template>
   
<!-- Tree sitemap -->

<!-- Horisontal sitemap -->
<xsl:template match="negeso:menu" mode="sitemap_hor">
	<xsl:param name="max_number" select="0"/>
	<tr>
	<xsl:for-each select="negeso:menu_item[@visible='true']">
	    <td class="level1">
		    <xsl:apply-templates select="self::node()" mode="link"/>
	    </td>
	</xsl:for-each>
	</tr>
	<xsl:call-template name="hor_sitemap">
		<xsl:with-param name="menu_nodes" select="negeso:menu_item[@visible='true']"/>
		<xsl:with-param name="max_number" select="$max_number"/>
	</xsl:call-template>

</xsl:template>
    
<xsl:template name="hor_sitemap">
	<xsl:param name="menu_nodes" />
	<xsl:param name="max_number" />
	<xsl:param name="cur_number" select="0"/>
	<tr>
	<xsl:for-each select="$menu_nodes">
		<xsl:choose>
			<xsl:when test="negeso:menu_item[(count(preceding-sibling::negeso:menu_item[@visible='true']) = $cur_number) and (@visible='true')]">
				<td class="level2">
					<xsl:apply-templates select="negeso:menu_item[(count(preceding-sibling::negeso:menu_item[@visible='true']) = $cur_number) and (@visible='true')]" mode="link"/>
				</td>
			</xsl:when>
			<xsl:otherwise>
			<td class="level2">&#160;</td>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>
	</tr>
	<xsl:if test="number($cur_number) &lt; (number($max_number) - 1)">
		<xsl:call-template name="hor_sitemap">
			<xsl:with-param name="menu_nodes" select="$menu_nodes"/>
			<xsl:with-param name="max_number" select="$max_number"/>
			<xsl:with-param name="cur_number" select="number($cur_number) + 1"/>
		</xsl:call-template>
	</xsl:if>
</xsl:template>
<!-- Horisontal sitemap -->



<!-- Vertical sitemap -->
<xsl:template match="negeso:menu" mode="sitemap_vert">
	<xsl:param name="nodes" select="0"/>
	<xsl:apply-templates select="negeso:menu_item[@visible='true']" mode="sitemap_vert_tr">
		<xsl:with-param name="nodes" select="$nodes" />
	</xsl:apply-templates>
</xsl:template>
<xsl:template match="negeso:menu_item" mode="sitemap_vert">
	<xsl:param name="nodes" select="0"/>
	<xsl:apply-templates select="negeso:menu_item[@visible='true']" mode="sitemap_vert_tr">
		<xsl:with-param name="nodes" select="$nodes" />
	</xsl:apply-templates>
</xsl:template>
    
<xsl:template match="negeso:menu_item" mode="sitemap_vert_tr">
    <xsl:param name="nodes" select="0"/>
    <xsl:choose>
        <!-- Working with first level menu items -->
        <xsl:when test="$nodes=0">
            <tr valign="top">
                <td class="level1">
                    <xsl:attribute name="rowspan">
                        <xsl:value-of select="count(descendant::negeso:menu_item[(count(negeso:menu/negeso:menu_item[@visible='true'])=0) and (@visible='true')])" />
                    </xsl:attribute>
                    <xsl:apply-templates select="self::node()" mode="link"/>
                </td>
                <xsl:apply-templates select="self::negeso:menu_item[@visible='true']" mode="sitemap_vert">
                    <xsl:with-param name="nodes" select="1" />
                </xsl:apply-templates>
            </tr>
            <xsl:apply-templates select="self::negeso:menu_item[@visible='true']" mode="sitemap_vert">
                <xsl:with-param name="nodes" select="2" />
            </xsl:apply-templates>
        </xsl:when>
        <!-- Working with all other levels menu items -->
        <!-- Working with all first submenu items  -->
        <xsl:when test="$nodes=1">
            <xsl:if test="count(preceding-sibling::negeso:menu_item[@visible='true'])=0">
                <td class="level2">
                    <xsl:attribute name="rowspan">
                        <xsl:value-of select="count(descendant::negeso:menu_item[(count(negeso:menu_item[@visible='true'])=0) and (@visible='true')])" />
                    </xsl:attribute>
                    <xsl:apply-templates select="self::node()" mode="link"/>
                </td>
                <xsl:apply-templates select="self::negeso:menu_item[@visible='true']" mode="sitemap_vert">
                    <xsl:with-param name="nodes" select="1" />
                </xsl:apply-templates>
            </xsl:if>
        </xsl:when>
        <!-- Working with all other submenu items  -->
        <xsl:otherwise>
            <xsl:choose>
                <!-- Working with all submenu items  which parent items was not first -->
                <xsl:when test="count(preceding-sibling::negeso:menu_item[@visible='true'])!=0">
                    <tr valign="top">
                        <td class="level2">
                            <xsl:attribute name="rowspan">
                                <xsl:value-of select="count(descendant::negeso:menu_item[(count(negeso:menu_item[@visible='true'])=0) and (@visible='true')])" />
                            </xsl:attribute>
                            <xsl:apply-templates select="self::node()" mode="link"/>
                        </td>
                        <xsl:apply-templates select="self::negeso:menu_item[@visible='true']" mode="sitemap_vert">
                            <xsl:with-param name="nodes" select="1" />
                        </xsl:apply-templates>
                    </tr>
                    <xsl:apply-templates select="self::negeso:menu_item[@visible='true']" mode="sitemap_vert">
                        <xsl:with-param name="nodes" select="2" />
                    </xsl:apply-templates>
                </xsl:when>
                <!-- Working with all submenu items which parent items was first -->
                <xsl:otherwise>
                    <xsl:apply-templates select="self::negeso:menu_item[@visible='true']" mode="sitemap_vert">
                        <xsl:with-param name="nodes" select="2" />
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
<!-- Vertical sitemap -->

<xsl:template match="negeso:menu_item" mode="link">
	<xsl:choose>
		<xsl:when test="@href">
			<a>
				<xsl:attribute name="href"><xsl:value-of select="@href" disable-output-escaping="yes"/></xsl:attribute>
					<xsl:attribute name="title"><xsl:value-of select="@title" disable-output-escaping="yes"/></xsl:attribute>
				<xsl:value-of select="@title" disable-output-escaping="yes"/>
			</a>
		</xsl:when>
		<xsl:otherwise>
			<a>
				<xsl:attribute name="title"><xsl:value-of select="@title" disable-output-escaping="yes"/></xsl:attribute>
				<xsl:value-of select="@title" disable-output-escaping="yes"/>
			</a>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>


<!-- LONG GRAY BLOCK: begin -->
<xsl:template name="sm_page_title_line_block">
	<xsl:param name="mode"></xsl:param>
	<xsl:param name="title">&#160;</xsl:param>
	<xsl:param name="width">100%</xsl:param>
	<div class="mainHeadBlock" style="width: {$width};">
		<table cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td>
					<xsl:value-of select="java:getString($dict_sitemap, 'SM_TITLE')"/>
				</td>
			</tr>
		</table>
	</div>
</xsl:template>
<!-- LONG GRAY BLOCK: end -->
        
</xsl:stylesheet>
