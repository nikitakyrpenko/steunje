<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${move_resource.xsl}		
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  
 
  @version		2004.18.03
  @author		Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_media_catalog" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_media_catalog.xsl', $lang)"/>

<!-- Include/Import -->
<xsl:include href="modules/media_catalog/commons.xsl"/>
<xsl:include href="/xsl/negeso_body.xsl"/>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
	<head>
  		<title><xsl:value-of select="java:getString($dict_media_catalog, 'MEDIA_CATALOG')"/></title>
		<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
		<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
		<xsl:call-template name="java-script"/>
	</head>
    <body>
        <!-- NEGESO HEADER -->
       <xsl:call-template name="NegesoBody">
           <xsl:with-param name="helpLink">
               <xsl:text>/admin/help/cmc1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
           </xsl:with-param>
           <xsl:with-param name="backLink" select="'javascript:goBack()'"/>
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
				document.moveResourceForm.command.value="list-directory-command";
				document.moveResourceForm.submit();
			}
	
			function isToken(textObj) {
				var newValue = textObj.value;
				var newLength = newValue.length;
				var extraChars="-0123456789_.";
				var search;
				for (var i = 0; i != newLength; i++) {
					aChar = newValue.substring(i, i+1);
					aChar = aChar.toUpperCase();
					search = extraChars.indexOf(aChar);
					if (search ==  -1 && (aChar <"A" || aChar >"Z")) {
						return false;
					}
				}
				return true;
			}
			
			function checkMoveForm() {
				return true;
			}
		]]>
		</xsl:text>
	</script>
</xsl:template>

<!-- NEGESO PAGE Temaplate -->
    <xsl:template match="negeso:page"  mode="admContent">
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable" width="100%" >
            <tr>
                <td style="width:auto; height:auto;" colspan="2">
                    <xsl:call-template name="location-message"/>
                </td>
            </tr>
                    <tr>
                        <td align="center" class="admNavPanelFont" colspan="2" >
                            <xsl:call-template name="media-catalog-header"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" width="764px">                           
                            <xsl:call-template name="command-info"/>
                            <xsl:apply-templates select="negeso:user-messages"/>
                            <xsl:apply-templates select="negeso:error-messages"/>
                            <xsl:apply-templates select="negeso:move-resource-option"/>                  
    	               </td>
    	 </tr>
    	 <tr>
    	     <td colspan="2">
    				<xsl:apply-templates select="negeso:repository"/>
           </td>
       </tr>
    </table>
</xsl:template>

<!-- BEGIN NEGESO RENAME RESOURCE OPTION MATCH -->
<xsl:template match="negeso:move-resource-option">
	<form name="moveResourceForm" method="post" action="" 	enctype="multipart/form-data" onSubmit="return checkMoveForm()" target="_self">
		<input type="hidden" name="command" value="move-resource-command"></input>
		<xsl:call-template name="browser-config-ninjas"/>
		<input type="hidden" name="resourceName">
			<xsl:attribute name="value">
				<xsl:value-of select="@resourceName"/>
			</xsl:attribute>
		</input>
		<table cellpadding="0" cellspacing="0"  width="764px">
			<tr>
				<th class="admTableTDLast"  width="100%">
					<xsl:value-of select="java:getString($dict_media_catalog, 'SELECT_DESTINATION_FOLDER')"/>:&#160;&#160;				
					<select id="destFolderId" name="destFolder" class="admWidth335">
						<xsl:apply-templates select="negeso:folder-set"/>
					</select>
				</th>
			</tr>
			<!--<tr>
				<td class="admTableTDLast" id="admTableTDtext"  width="100%">
                    <xsl:value-of select="java:getString($dict_common, 'COPY')"/>:&#160;&#160;
                    <input border="1" type="checkbox" class="checkbox" name="isCopy"/>
				</td>
			</tr>-->
			<tr>
				<th class="admTableTDLast" width="100%" >
                    <div class="admNavPanelInp" style="padding:0;margin:0;">
                        <div class="imgL"></div>
                        <div>
                            <input class="admNavPanelInp" type="submit" name="isCopy">
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'COPY')"/></xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>
                    
					
				</th>
			</tr>
		</table>
	</form>
</xsl:template>

<xsl:template match="negeso:folder">
	<OPTION>
		<xsl:attribute name="VALUE"><xsl:value-of select='@path'/></xsl:attribute>
		<xsl:value-of select='@path'/>
	</OPTION>
</xsl:template>


<!-- ******************* COMMAND INFO *************************** -->
<xsl:template name="command-info">
   <table cellspacing="0" cellpadding="0"  width="764px" >
		<tr>
    		<th class="admTableTDLast admRed" style="height:39px;" >
    		    <xsl:value-of select="java:getString($dict_common, 'COPY')"/>:&#160;&#160;
    	
            		      <xsl:value-of select="/negeso:page/negeso:move-resource-option/@resourceName"/>
                    </th>
               
		</tr>
	</table>
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
