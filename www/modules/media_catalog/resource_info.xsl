<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$	
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.

  @version		$Revision$
  @author		Olexiy.Strashko
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">
		
<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_media_catalog" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_media_catalog.xsl', $lang)"/>

<xsl:variable name="mediaCatalogPreviewWindow">MediaCatalogPreviewWindow</xsl:variable>

<!-- Include/Import -->
<xsl:include href="modules/media_catalog/commons.xsl"/>
<xsl:include href="/xsl/negeso_body.xsl"/>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
	<head>
  		<title><xsl:value-of select="java:getString($dict_media_catalog, 'MEDIA_CATALOG')"/>. <xsl:value-of select="java:getString($dict_media_catalog, 'RESOURCE_INFO')"/></title>
		<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
		<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
		<xsl:call-template name="java-script"/>
	</head>
    <body>
        <!-- NEGESO HEADER -->
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink">
                <xsl:text>/admin/help/cmc1_</xsl:text>
                <xsl:value-of select="$lang"/>
                <xsl:text>.html</xsl:text>
            </xsl:with-param>
            <xsl:with-param  name="backLink" select="'?command=list-directory-command'" />
        </xsl:call-template>

    </body>
</html>
</xsl:template>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    
	<script language="JavaScript">
	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
		function goBack() {
			window.name="CatalogWindow";
			document.backForm.command.value="list-directory-command";
			document.backForm.submit();
		}
		]]>
		</xsl:text>
	</script>
</xsl:template>

<!-- NEGESO PAGE Temaplate -->
    <xsl:template match="negeso:page" mode="admContent">
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
            <td>
                <table class="admNavPanel" cellpadding="0" cellspacing="0">
                    <tr>
                        <td align="center" class="admNavPanelFont" >
                            <xsl:call-template name="media-catalog-header"/>
                            <xsl:call-template name="back-form"/>
                            <xsl:apply-templates select="negeso:user-messages"/>
                            <xsl:apply-templates select="negeso:error-messages"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
        		       		 <xsl:apply-templates select="negeso:resource"/> 
                        </td>
                    </tr>
                </table>
    	    </td>
    	</tr>
    	<tr>
    	    <td>
               <xsl:apply-templates select="negeso:repository"/>
            </td>
        </tr>
    </table>
</xsl:template>


<!-- BEGIN NEGESO RESOURCE MATCH -->
<xsl:template match="negeso:resource">
    <table style="width:764px;" cellspacing="0" cellpadding="0">
		<tr>
            <th class="admTableTD" width="50%">
				<xsl:value-of select="java:getString($dict_media_catalog, 'RESOURCE_NAME')"/>:
			</th>
            <th class="admTableTDLast" id="admTableTDtext" width="*">
				<xsl:value-of select="@name"/>
			</th>
		</tr>
		<xsl:choose>
			<xsl:when test="@type='image'">
				<tr>
                    <td colspan="2" class="admTableTDLast"  align="center">
					    <img class="admBorder">
						    <xsl:attribute name="src">
							    <xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>/<xsl:value-of select="@name" disable-output-escaping="yes"/>
						    </xsl:attribute>
					    </img>
				   </td>
				</tr>
                <tr >
                    <th class="admTableTD" >
                        <xsl:value-of select="java:getString($dict_media_catalog, 'HEIGHT')"/>:

                    </th>
                    <th class="admTableTDLast" >
                        <xsl:value-of select="@height"/>

                    </th>
                </tr>
                <tr >
                    <th class="admTableTD" >
                        <xsl:value-of select="java:getString($dict_media_catalog, 'WIDTH')"/>:
                    </th>
                    <th class="admTableTDLast">
                        <xsl:value-of select="@width"/>
                    </th>
                </tr>
			</xsl:when>
			<xsl:otherwise>
				<tr>
                    <th class="admTableTDLast" colspan="2">
                        <div class="admNavPanelInp" style="padding:0;margin:0;">
                            <div class="imgL"></div>
                            <div>
                                <a class="admNavPanelInp" target="_self">
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>/<xsl:value-of select="@name"/>
                                    </xsl:attribute>
                                    <xsl:value-of select="java:getString($dict_media_catalog, 'DOWNLOAD')"/>
                                </a>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </th>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
        <tr >
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>:
            </th>
            <th class="admTableTDLast">
                <xsl:value-of select="@title"/>
                <br/>
            </th>
        </tr>
        <tr >
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_common, 'TYPE')"/>:
            </th>
                <th class="admTableTDLast">
                <xsl:value-of select="@type"/>
                </th>
        </tr>
        <tr >
           <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_media_catalog, 'EXTENSION')"/>:
            </th>
               <th class="admTableTDLast">
                <xsl:value-of select="@extension"/>
               </th>
        </tr>
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_media_catalog, 'SIZE')"/>:
            </th>
                <th class="admTableTDLast">
                <xsl:value-of select="@size"/>
            </th>
        </tr>
        <tr >
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_media_catalog, 'LAST_MODIFIED')"/>:
            </th>
                <th class="admTableTDLast">
                <!--<xsl:value-of select="@title"/> I don't know why title there AndriyZ -->  
                <xsl:value-of select="@last-modified"/>
            </th>
        </tr>
   	<xsl:if test="count(negeso:dependencies/negeso:dependency) > 0">
    	<tr>
            <td colspan="2">
	       		&#160;
	       	</td>
        </tr>
    	<tr>
            <td colspan="2">
	       		<xsl:apply-templates select="negeso:dependencies"/>
	       	</td>
        </tr>
    </xsl:if>
	</table>
</xsl:template>

<!-- ******************* TEMPLATE dependencies *************************** -->
<xsl:template match="negeso:dependencies">
    <table style="width:798px;" cellspacing="0" cellpadding="0">
        <tr>
            <td align="center" class="admNavPanelFont" >               
                <xsl:call-template name="tableTitle">
                    <xsl:with-param name="headtext">
                        <xsl:value-of select="java:getString($dict_media_catalog, 'DEPENDENCIES')"/>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>
    </table>
    <table width="100%" cellspacing="0" cellpadding="0">
			<xsl:for-each select="negeso:dependency">
			<tr>
			    <th class="admTableTD" width="20%">
				    <xsl:value-of select="@module"/>
                </th>
                    <th class="admTableTD" width="25%">
				    <xsl:value-of select="@category"/>
                    </th>
                        <th class="admTableTD" width="50%">
		            <xsl:value-of select="@title"/>
                            &#160;&#160;
		           <xsl:value-of select="@lang-code"/>
                  </th>
                      <td class="admTableTDLast" width="1%">
		    		<img src="/images/edit.png" class="admHand" onClick="return editPublication({@id})">
		                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute> 
		                <xsl:attribute name="onClick">javascript:window.open('<xsl:value-of select="@href"/>', '<xsl:value-of select="$mediaCatalogPreviewWindow"/>', 'width=800,height=600,status=yes,toolbar=yes,menubar=no,location=no,scrollbars=yes,resizable=yes')</xsl:attribute> 
		            </img>
			    </td>
			</tr>
			</xsl:for-each>
		</table>
</xsl:template>

<!-- ******************* COMMAND INFO *************************** -->
<xsl:template name="command-info">
	<table width="100%" cellspacing="1" cellpadding="1" class="admCenter">
		<tr>
		<td width="12%">
			<font face="Verdana, Arial, Hevetica" size="2"><b><xsl:value-of select="java:getString($dict_media_catalog, 'LOCATION')"/>:</b></font>
		</td>
		<td>
			<table width="100%" cellspacing="1" cellpadding="1" align="left">
				<tr><td>
				<font face="Verdana, Arial, Hevetica" size="2">
					<xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>
				</font>
				</td></tr>
			</table>
		</td>
		<td align="right">
			<input class="makeFolderBut" type="button" name="back" 
				onClick="goBack()" value="&lt;&lt;Back"
			/>
		</td>
		</tr>
	</table>
</xsl:template>


<!-- ******************* TEMPLATE Back form  *************************** -->
<xsl:template name="back-form">
	<xsl:call-template name="standard-action-form">
		<xsl:with-param name="formId" select="'backForm'"/>
		<xsl:with-param name="commandId" select="'list-directory-command'"/>
	</xsl:call-template>
</xsl:template>
	
	
<!-- ******************* NEGESO REPOSITORY *************************** -->
<xsl:template match="negeso:repository">
	<xsl:call-template name="common-catalog-info"/>
</xsl:template>

<!-- ******************* NEGESO USER MESSAGES *************************** -->
<xsl:template match="negeso:user-messages">
	<xsl:call-template name="common-user-messages"/>
</xsl:template>
	
<!-- ******************* NEGESO ERROR MESSAGES *************************** -->
<xsl:template match="negeso:error-messages">
	<xsl:call-template name="common-error-messages"/>
</xsl:template>


</xsl:stylesheet>
