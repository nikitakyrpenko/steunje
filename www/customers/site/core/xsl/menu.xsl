<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Site Core: Menu
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java"
>


<!-- *************** NEGESO MENU BEGIN ******************** -->

<!-- LATEST MENU VERSION: BEGIN -->

<xsl:template match="negeso:menu">

	<ul id="negeso_main_menu" class="negeso_menu clearfix hidden">

        <xsl:choose>
            <xsl:when test="$outputType = 'admin'">
                <xsl:apply-templates select="negeso:menu_item" mode="li"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="negeso:menu_item[@visible='true']" mode="li"/>
            </xsl:otherwise>
        </xsl:choose>
	</ul>
    <script type="text/javascript">
        if(navigator.platform == "iPad" || navigator.platform == "iPhone" || navigator.platform == "iPod" || navigator.userAgent.indexOf('Android')!=-1){
        negesoMenuTouch('negeso_main_menu');
        }else{
        <xsl:choose>
            <xsl:when test="@is2ClickBehaviorMenu='true'">
                negesoMenu2Click('negeso_main_menu');
            </xsl:when>
            <xsl:otherwise>
                negesoMenu('negeso_main_menu');
            </xsl:otherwise>
        </xsl:choose>
        }
    </script>
</xsl:template>

<xsl:template match="negeso:menu_item">
	<ul>
        <xsl:choose>
            <xsl:when test="$outputType = 'admin'">
                <xsl:apply-templates select="negeso:menu_item" mode="li"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="negeso:menu_item[@visible='true']" mode="li"/>
            </xsl:otherwise>
        </xsl:choose>
    </ul>	
</xsl:template>

<xsl:template match="negeso:menu_item" mode="li">
	<li>
	<xsl:variable name="is_selected">
		<xsl:choose>
			<xsl:when test="descendant-or-self::negeso:menu_item[@id=$selectedMenuId]">true</xsl:when>
			<xsl:when test="descendant-or-self::negeso:menu_item[@href=$param[@name='selectedPageName']/negeso:value/text()]">true</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>	
	<xsl:choose>
		<!-- Single item selected -->
		<!--<xsl:when test="$is_selected='true' and position()=1 and position()=last()">
			<xsl:attribute name="class">item_single_selected</xsl:attribute>
		</xsl:when>-->
		<!-- First item selected -->
		<!--<xsl:when test="$is_selected='true' and position()=1">
			<xsl:attribute name="class">item_first_selected</xsl:attribute>
		</xsl:when>-->
		<!-- Last item selected -->
		<!--<xsl:when test="$is_selected='true' and position()=last()">
			<xsl:attribute name="class">item_last_selected</xsl:attribute>
		</xsl:when>-->
		<!-- Middle item selected -->
		<xsl:when test="$is_selected='true'">
			<xsl:attribute name="class">item_selected</xsl:attribute>
		</xsl:when>
		<!-- Single item -->
		<!--<xsl:when test="position()=1 and position()=last()">
			<xsl:attribute name="class">item_single</xsl:attribute>
		</xsl:when>-->
		<!-- First item -->
		<!--<xsl:when test="position()=1">
			<xsl:attribute name="class">item_first</xsl:attribute>
		</xsl:when>-->
		<!-- Last item -->
		<!--<xsl:when test="position()=last()">
			<xsl:attribute name="class">item_last</xsl:attribute>
		</xsl:when>-->
	</xsl:choose>
	<a onfocus="blur()">
		<!-- *************** Only for admin - do not change - begin ******************** -->
		<xsl:if test="($outputType = 'admin') and (@visible='false')">
			<xsl:attribute name="style">color: #00ff00;</xsl:attribute>
		</xsl:if>
		<!-- *************** Only for admin - do not change - end ******************** -->
		<xsl:attribute name="href"><xsl:apply-templates select="." mode="correctHref"/>
		</xsl:attribute>
		
		<xsl:attribute name="title"><xsl:value-of select="@title" disable-output-escaping="yes"/></xsl:attribute>
		<xsl:value-of select="@title" disable-output-escaping="yes"/>
	</a>
    <xsl:if test="count(child::negeso:menu_item) &gt; 0">        
        <xsl:apply-templates select="self::negeso:menu_item"/>
    </xsl:if>    
	</li>
</xsl:template>

<xsl:template match="negeso:menu_item" mode="correctHref">
    <xsl:variable name="href_params">
           <xsl:choose>
               <xsl:when test="@keepMenu='true' and contains(@href, '?')">&amp;miId=<xsl:value-of select="@id"/></xsl:when>
               <xsl:when test="@keepMenu='true' and not(contains(@href, '?'))">?miId=<xsl:value-of select="@id"/></xsl:when>
               <xsl:otherwise></xsl:otherwise>
           </xsl:choose>
      </xsl:variable>
    <xsl:choose>
        <xsl:when test="@friendly-href"><xsl:value-of select="$adminPath"/>/<xsl:value-of select="@friendly-href" disable-output-escaping="yes"/><xsl:value-of select="$href_params"/></xsl:when>
        <xsl:otherwise><xsl:if test="@pageId"><xsl:value-of select="$adminPath"/>/</xsl:if><xsl:value-of select="@href" disable-output-escaping="yes"/><xsl:value-of select="$href_params"/></xsl:otherwise>
		
    </xsl:choose>
</xsl:template>
<!-- LATEST MENU VERSION: END -->

<!-- WHERE AM I: BEGIN -->
<xsl:template match="negeso:menu" mode="where_am_i">
<xsl:param name="hide_last">false</xsl:param>
	<div class="first_where_am_i_item">
		<a href="{$adminPath}/index_{$lang}.html">
			<xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'CORE.TOP_LINK_HOME')"/></xsl:attribute>
			<xsl:value-of select="java:getString($dict_common, 'CORE.TOP_LINK_HOME')"/>
		</a>
		&#160;&#160;&gt;&gt;&gt;&#160;&#160;
	</div>	
	<xsl:choose>
		<xsl:when test="descendant-or-self::negeso:menu_item[@id=$selectedMenuId]">
			<xsl:apply-templates select="negeso:menu_item[descendant-or-self::negeso:menu_item[@id=$selectedMenuId]]" mode="where_am_i_item">
				<xsl:with-param name="hide_last"><xsl:value-of select="$hide_last"/></xsl:with-param>
			</xsl:apply-templates>
		</xsl:when>
		<xsl:otherwise>
			<xsl:if test="$hide_last = 'false'">
                <xsl:call-template name="here_am_i">
                    <xsl:with-param name="title" select="/negeso:page/negeso:title"/>
                </xsl:call-template>
			</xsl:if>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
    
<xsl:template match="negeso:menu_item" mode="where_am_i">
    <xsl:param name="hide_last">false</xsl:param>	
	<xsl:choose>
		<xsl:when test="descendant-or-self::negeso:menu_item[@id=$selectedMenuId]">
			<xsl:apply-templates select="negeso:menu_item[descendant-or-self::negeso:menu_item[@id=$selectedMenuId]]" mode="where_am_i_item">
				<xsl:with-param name="hide_last"><xsl:value-of select="$hide_last"/></xsl:with-param>
			</xsl:apply-templates>
		</xsl:when>
		<xsl:otherwise>
			<xsl:if test="$hide_last = 'false'">
                <xsl:call-template name="here_am_i">
                    <xsl:with-param name="title" select="/negeso:page/negeso:title"/>
                </xsl:call-template>
			</xsl:if>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="here_am_i">
    <xsl:param name="title"/>
    <div class="here_am_i">
        <xsl:value-of select="$title" disable-output-escaping="yes"/>
    </div>
</xsl:template>    

<xsl:template match="negeso:menu_item" mode="where_am_i_item">
<xsl:param name="hide_last">false</xsl:param>
	 <xsl:choose>
		<xsl:when test="count(descendant::negeso:menu_item[@id=$selectedMenuId]) = 0">
			<xsl:if test="$hide_last = 'false'">
                <xsl:call-template name="here_am_i">
                    <xsl:with-param name="title" select="@title"/>
                </xsl:call-template>
			</xsl:if>
		</xsl:when>
		<xsl:otherwise>
			<div>
				<a title="{@title}" onfocus="blur()">
				    <xsl:attribute name="href"><xsl:apply-templates select="." mode="correctHref"/>
                    </xsl:attribute>
					<xsl:value-of select="@title" disable-output-escaping="yes"/>
				</a>
				&#160;&#160;&gt;&gt;&gt;&#160;&#160;
			</div>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:apply-templates select="self::negeso:menu_item[descendant-or-self::negeso:menu_item[@id=$selectedMenuId]]" mode="where_am_i">
		<xsl:with-param name="hide_last"><xsl:value-of select="$hide_last"/></xsl:with-param>
	</xsl:apply-templates>
</xsl:template>
<!-- WHERE AM I: END -->

<!-- *************** NEGESO MENU END ******************** -->


</xsl:stylesheet>