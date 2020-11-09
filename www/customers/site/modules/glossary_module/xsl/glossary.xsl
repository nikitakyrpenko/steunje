<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Glossary Module
 
  @version		2007.12.25
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="dict_glossary" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('glossary', $lang)"/>

<xsl:template match="negeso:glossary_module" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/glossary_module/css/glossary.css"/>
	<script type="text/javascript" src="/site/modules/glossary_module/script/glossary.js">/**/</script>
</xsl:template>

<xsl:template match="negeso:glossary_module">
    <span>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_glossary"/>
									<xsl:with-param name ="name"  select="'GM_GLOSSARY'"/>
								</xsl:call-template>
    </span>
								<xsl:if test="$outputType = 'admin' and not(/negeso:page/@role-id = 'visitor')">
									<img src="/images/mark_1.gif" class="hand" align="absMiddle" onClick="window.open('glossary', '_blank', 'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"/>
									<xsl:text>&#160;</xsl:text>
								</xsl:if>
    <h1><xsl:value-of select="java:getString($dict_glossary, 'GM_GLOSSARY')"/></h1>
								    <form name="gmForm" action="" method="get">
								        <input type="hidden" name="show" id="hidden"/>
									        <xsl:apply-templates select="negeso:glossary_header"/>
								    </form>
								    <xsl:apply-templates select="negeso:glossary_search_results"/>
								    <xsl:apply-templates select="negeso:glossary_word_details"/>
</xsl:template>

<xsl:template match="negeso:glossary_search_results">
    <h2>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_glossary"/>
						<xsl:with-param name ="name"  select="'GM_SEARCH_RESULT'"/>
					</xsl:call-template>
        			<xsl:value-of select="java:getString($dict_glossary, 'GM_SEARCH_RESULT')"/>
    </h2>
        		<xsl:choose>
	        		<xsl:when test="count(negeso:glossary_word) = 0">
            <div>
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_glossary"/>
							<xsl:with-param name ="name"  select="'GM_NOTHING_FOUND'"/>
						</xsl:call-template>
							<xsl:value-of select="java:getString($dict_glossary, 'GM_NOTHING_FOUND')"/>
            </div>
	        		</xsl:when>
	        		<xsl:otherwise>
            <div>
                <xsl:call-template name ="add-constant-info">
                    <xsl:with-param name ="dict"  select="$dict_glossary"/>
                    <xsl:with-param name ="name"  select="'GM_NOTHING_FOUND'"/>
                </xsl:call-template>
							<xsl:apply-templates select="negeso:glossary_word"/>
            </div>
	        		</xsl:otherwise>
        		</xsl:choose>
</xsl:template>

<xsl:template match="negeso:glossary_word">
	<a>
	    <xsl:choose>
	        <xsl:when test="//negeso:glossary_header/@show='all'">
	  		        <xsl:attribute name="href">?wid=<xsl:value-of select="@id"/><xsl:text disable-output-escaping="yes">&amp;show=all</xsl:text></xsl:attribute>
	        </xsl:when>
	        <xsl:when test="//negeso:glossary_header/@show='letter'">
	            <xsl:attribute name="href">?wid=<xsl:value-of select="@id"/><xsl:text disable-output-escaping="yes">&amp;show=letter&amp;</xsl:text>id=<xsl:value-of select="//negeso:glossary_header/@field"/></xsl:attribute>
	        </xsl:when>
	        <xsl:when test="//negeso:glossary_header/@show='category'">
	            <xsl:attribute name="href">?wid=<xsl:value-of select="@id"/><xsl:text disable-output-escaping="yes">&amp;show=category&amp;</xsl:text>cat=<xsl:value-of select="//negeso:glossary_header/@field"/></xsl:attribute>
	        </xsl:when>
	        <xsl:when test="//negeso:glossary_header/@show='find'">
	            <xsl:attribute name="href">?wid=<xsl:value-of select="@id"/><xsl:text disable-output-escaping="yes">&amp;show=find&amp;</xsl:text>find=<xsl:value-of select="//negeso:glossary_header/@field"/></xsl:attribute>
	        </xsl:when>
	    </xsl:choose>
	    <xsl:value-of select="@word"/>
	</a><br/>
</xsl:template>

<xsl:template match="negeso:glossary_header">
    <table class="gmTable">
    	<tr>
            <td colspan="2">
                <h2>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_glossary"/>
						<xsl:with-param name ="name"  select="'GM_SEARCH_FORM'"/>
					</xsl:call-template>            	
	        		<xsl:value-of select="java:getString($dict_glossary, 'GM_SEARCH_FORM')"/>
	        	</h2>
        	</td>
        </tr>
        <tr>
            <td class="gmWidth_200">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_glossary"/>
					<xsl:with-param name ="name"  select="'GM_SHOW_BY_LETTER'"/>
				</xsl:call-template>
            	<xsl:value-of select="java:getString($dict_glossary, 'GM_SHOW_BY_LETTER')"/>
            </td>
            <td>
                <xsl:apply-templates select="negeso:glossary_alphabet"/>
                &#160;&#160;<a href="?show=all">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_glossary"/>
						<xsl:with-param name ="name"  select="'GM_SHOW_ALL'"/>
					</xsl:call-template>
	                <xsl:value-of select="java:getString($dict_glossary, 'GM_SHOW_ALL')"/>
                </a>
            </td>
        </tr>
        <xsl:if test="negeso:glossary_category_list/negeso:glossary_category">
	        <tr>
	            <td>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_glossary"/>
						<xsl:with-param name ="name"  select="'GM_SHOW_BY_CATEGORY'"/>
					</xsl:call-template>
	            	<xsl:value-of select="java:getString($dict_glossary, 'GM_SHOW_BY_CATEGORY')"/>
	            </td>
	            <td>
	                <select name="cat">
	                	<xsl:apply-templates select="negeso:glossary_category_list/negeso:glossary_category"/>
	                </select>
	                &#160;&#160;<a href="#" onClick="categoryClick();">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_glossary"/>
							<xsl:with-param name ="name"  select="'GM_SHOW'"/>
						</xsl:call-template>
	                <xsl:value-of select="java:getString($dict_glossary, 'GM_SHOW')"/>
	                </a>
	            </td>
	        </tr>
		</xsl:if>
        <tr>
            <td>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_glossary"/>
					<xsl:with-param name ="name"  select="'GM_FIND'"/>
				</xsl:call-template>
            	<xsl:value-of select="java:getString($dict_glossary, 'GM_FIND')"/>
            </td>
            <td>
            	<table class="gmNoBorder" cellpadding="0" cellspacing="0" border="0">
            		<tr>
		            	<td>
			                <input required="required" class="text" type="editbox" style="width: 200;" name="find" >
				                <xsl:attribute name="onKeyPress">
				                	if (event.keyCode == 13) {findClick(); return false;}
				                </xsl:attribute>
				                <xsl:if test="@show='find'"><xsl:attribute name="value"><xsl:value-of select="@field"/></xsl:attribute></xsl:if>
			                </input>
						</td>
						<td>
			                &#160;&#160;
			                <a href="#" onClick="findClick();">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_glossary"/>
									<xsl:with-param name ="name"  select="'GM_SHOW'"/>
								</xsl:call-template>
					            <xsl:value-of select="java:getString($dict_glossary, 'GM_SHOW')"/>
			        		</a>
			        	</td>
		        	</tr>
	        	</table>
            </td>
        </tr>
    </table>
</xsl:template>

<xsl:template match="negeso:glossary_alphabet">
    | <xsl:apply-templates select="negeso:glossary_letter"/>
</xsl:template>

<xsl:template match="negeso:glossary_letter">
    <xsl:choose>
        <xsl:when test="@amount=0">
            <xsl:value-of select="@letter"/>
        </xsl:when>
        <xsl:otherwise>
            <a href="?">
                <xsl:attribute name="href"
                	><xsl:text disable-output-escaping="yes">?show=letter&amp;id=</xsl:text><xsl:value-of select="@id"/>
                </xsl:attribute>
                <xsl:value-of select="@letter"/>
            </a>
        </xsl:otherwise>
    </xsl:choose> |
</xsl:template>

<xsl:template match="negeso:glossary_category">
    <option>
        <xsl:if test="@selected"><xsl:attribute name="selected">true</xsl:attribute></xsl:if>
        <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute><xsl:value-of select="@name"/></option>
</xsl:template>

<xsl:template match="negeso:glossary_word_details">
    <table class="gmTable gmMarginTop_15">
        <tr>
        	<td>
				<span class="bold">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_glossary"/>
						<xsl:with-param name ="name"  select="'GM_DETAILS_FOR'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_glossary, 'GM_DETAILS_FOR')"/>:</span>&#160;<xsl:value-of select="@word"/>
	        </td>
		</tr>
		<tr>
	        <td>
   	        	<xsl:value-of select="text()" disable-output-escaping="yes"/>
		        <xsl:if test="$outputType = 'admin'">
	    	        <script>
	        	    makeReadonly(article_text<xsl:value-of select="@id" />, true);
	            	</script>
		        </xsl:if>
        	</td>
        </tr>
    </table>
	<input type="button" class="submit gmMargin_15" onclick="history.back();">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_glossary"/>
			<xsl:with-param name ="name"  select="'GM_BACK'"/>
		</xsl:call-template>
		<xsl:attribute name="value">
			<xsl:value-of select="java:getString($dict_glossary, 'GM_BACK')"/>
		</xsl:attribute>
	</input>
</xsl:template>

</xsl:stylesheet>