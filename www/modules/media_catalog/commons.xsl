<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${commons.xsl}		
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.

  Commons xsl templates for Media Catalog
 
  @version		2004.26.03
  @author		Olexiy.Strashko
  @author     	Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_media_catalog" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_media_catalog.xsl', $lang)"/>

<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
<!-- Global variables -->
<xsl:variable name="image_preview_width" select="70"/>
<xsl:variable name="image_preview_height" select="70"/>
<!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->

<xsl:template match="/">
<xsl:apply-templates select="negeso:page"/>
</xsl:template>

<!-- ******************* MEDIA CATALOG HEADER ************************** -->
<xsl:template name="media-catalog-header">
   <!--Are going to /xsl/negeso_header.xsl-->
   <xsl:call-template name="tableTitle">
		<xsl:with-param name="headtext">
		   <xsl:choose>
					<xsl:when test="/negeso:page/negeso:browser-config/@action-mode='chooser'">
						<xsl:value-of select="java:getString($dict_media_catalog, 'MEDIA_CATALOG')"/>. <xsl:value-of select="java:getString($dict_media_catalog, 'CHOOSE_RESOURCE_MODE')"/>
					</xsl:when>
					<xsl:otherwise>
        <xsl:value-of select="java:getString($dict_media_catalog, 'MEDIA_CATALOG')"/>
					</xsl:otherwise>
        </xsl:choose>
		</xsl:with-param>
	</xsl:call-template>
</xsl:template>


<!-- ******************* NEGESO REPOSITORY *************************** -->
<xsl:template name="common-catalog-info">
	<table cellspacing="0" cellpadding="0" width="100%">
		
    	<tr>
    		<th class="admTableTDLast">
                <b><xsl:value-of select="java:getString($dict_media_catalog, 'TOTAL_SPACE')"/>:
                </b>  <xsl:value-of select="@total-space"/></th>
            <th class="admTableTDLast">
                <b> <xsl:value-of select="java:getString($dict_media_catalog, 'MAX_FILE_SIZE')"/>:</b><xsl:value-of select="@max-file-size"/>
            </th>
    	</tr>
    	<tr>
            <th class="admTableTDLast">
                <b> <xsl:value-of select="java:getString($dict_media_catalog, 'FREE_SPACE')"/>:</b><xsl:value-of select="@free-space"/>
            </th>
    		<th class="admTableTDLast">
                <b><xsl:value-of select="java:getString($dict_media_catalog, 'USED_SPACE')"/>:</b><xsl:value-of select="@used-space"/>
            </th>
    	</tr>
        <tr>
            <td class="admTableFooter" >&#160;</td>
        </tr>
	</table>
</xsl:template>

<!-- ******************* NEGESO navBar_media*************************** -->
<xsl:template name="navBar_media">
   <!--Are going to /xsl/negeso_header.xsl-->
   <xsl:choose>
		<xsl:when test="/negeso:page/negeso:browser-config/@action-mode='chooser'"></xsl:when>
		<xsl:otherwise>
        <xsl:call-template name="NavBar">
            <xsl:with-param name="backLink" select="'javascript:goBack()'"/>
        </xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- ******************* NEGESO LOCATION*************************** -->
<xsl:template name="location-message">
    <table   border="0" cellpadding="0" cellspacing="0" align="left">
        <tr>
            <td style="padding:8px 0 0 5px;" valign="middle" class="admNavigation">
                <xsl:value-of select="java:getString($dict_media_catalog, 'LOCATION')"/> 
            </td>
            <td valign="middle" class="admNavigation" style="padding:8px 0 0 0;text-decoration:none;">
                &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                <span >                   
                    <xsl:choose>
                        <xsl:when test="/negeso:page/negeso:browser-config">
                            <xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="/negeso:page/negeso:directory/@current-dir"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </span>
            </td>
        </tr>
    </table>
</xsl:template>


<!-- ******************* NEGESO ERROR MESSAGE *************************** -->
<xsl:template name="common-error-messages" >
	<table cellspacing="0" cellpadding="0" class="admNavPanel admSpaceSmall" style="width:857px;" >
		<xsl:for-each select="negeso:message">
		    <tr>
		        <th class="admTableTDLast" align="center">
				      <xsl:value-of select="@text"/>
		        </th>
		    </tr>
		</xsl:for-each>
	</table>
</xsl:template>

<!-- ******************* NEGESO USER MESSAGE *************************** -->
<xsl:template name="common-user-messages">
	<table cellspacing="0" cellpadding="0" class="admNavPanel admSpaceSmall">
		<xsl:for-each select="negeso:message">
		    <tr>
		        <th class="admTableTDLast" align="center">
				      <xsl:value-of select="@text"/>
		        </th>
		    </tr>
		</xsl:for-each>
	</table>
</xsl:template>
	
<!-- ******************* BROWSER CONFIG NINJAS BRIEF *********************** -->
	<xsl:template name="browser-config-ninjas-brief">
	<input type="hidden" name="parentDir" >
		<xsl:attribute name="value">
			<xsl:value-of select="/negeso:page/negeso:browser-config/@parent-dir"/>
		</xsl:attribute>
	</input>
	<input type="hidden" name="currentDir" >
		<xsl:attribute name="value">
			<xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>
		</xsl:attribute>
	</input>
	<input type="hidden" name="actionMode">
		<xsl:attribute name="value">
			<xsl:value-of select="/negeso:page/negeso:browser-config/@action-mode"/>
		</xsl:attribute>
	</input>
</xsl:template>
	
<!-- ******************* BROWSER CONFIG NINJAS *************************** -->
<xsl:template name="browser-config-ninjas">
	<xsl:call-template name="browser-config-ninjas-brief"/>
	<input type="hidden" name="showHiddenFiles">
		<xsl:attribute name="value">
			<xsl:value-of select="/negeso:page/negeso:browser-config/@show-hidden-files"/>
		</xsl:attribute>
	</input>
	<input type="hidden" name="sortMode">
		<xsl:attribute name="value">
			<xsl:value-of select="/negeso:page/negeso:browser-config/@sort-mode"/>
		</xsl:attribute>
	</input>
	<input type="hidden" name="viewMode">
		<xsl:attribute name="value">
			<xsl:value-of select="/negeso:page/negeso:browser-config/@view-mode"/>
		</xsl:attribute>
	</input>
</xsl:template>

<!-- ******************* Standard actions form template************************** 
Input parameters:
	formId         - The id of the form
	commandId      - The id of the command
-->
<xsl:template name="standard-action-form">
    <xsl:param name="formId"/>
    <xsl:param name="commandId"/>
    <form name="{$formId}" method="post" action="index.html" enctype="multipart/form-data" target="CatalogWindow">
        <input type="hidden" name="command" value="{$commandId}"/>
        <xsl:call-template name="browser-config-ninjas"/>
        <input type="hidden" name="victim" value=""/>
    </form>
</xsl:template>

</xsl:stylesheet>
