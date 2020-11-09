<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list_directory.xsl}		
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @version		2004.18.03
  @author		Olexiy.Strashko
  @author     Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

    <xsl:variable name="lang" select="/*/@interface-language"/>
    <xsl:variable name="dict_media_catalog" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_media_catalog.xsl', $lang)"/>

    <!-- Include/Import -->
    <xsl:include href="modules/media_catalog/commons.xsl"/>
    <xsl:include href="/xsl/negeso_body.xsl"/>

    <!-- MAIN ENTRY -->
    <xsl:template match="/">
        <html>
            <head>
                <title>
                    <xsl:value-of select="java:getString($dict_media_catalog, 'MEDIA_CATALOG')"/>
                </title>
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
            var s_InvalidFileName = "<xsl:value-of select="java:getString($dict_media_catalog, 'INVALID_FILE_NAME')"/>";
            var s_EnterFileName = "<xsl:value-of select="java:getString($dict_media_catalog, 'ENTER_FILE_NAME')"/>";

            <xsl:text disable-output-escaping="yes">
	    <![CDATA[
		function goBack() {
			document.renameResourceForm.command.value="list-directory-command";
			document.renameResourceForm.submit();
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

		function checkRenameForm() {
			if (document.renameResourceForm.newName.value == null){
				alert(s_InvalidFileName);
				return false;
			}
			if (document.renameResourceForm.newName.value == ""){
				alert(s_EnterFileName);
				return false;
			}
			if (!isToken(document.renameResourceForm.newName)){
				alert(s_InvalidFileName);
				return false;
			}
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
                </td>
            </tr>
            <tr>
                <td >
                    <xsl:apply-templates select="negeso:rename-resource-option"/>

                </td>
            </tr>
            <tr>
                <td>
                    <xsl:apply-templates select="negeso:repository"/>
                </td>
            </tr>
        </table>

    </xsl:template>


    <!-- BEGIN NEGESO RENAME RESOURCE OPTION MATCH -->
    <xsl:template match="negeso:rename-resource-option">
        <form name="renameResourceForm" method="post" action="" enctype="multipart/form-data"
            onSubmit="return checkRenameForm()" target="_self"
	>
            <input type="hidden" name="command" value="rename-resource-command"></input>
            <xsl:call-template name="browser-config-ninjas"/>
            <input type="hidden" name="currentName">
                <xsl:attribute name="value">
                    <xsl:value-of select="@old-name"/>.<xsl:value-of select="@extension"/>
                </xsl:attribute>
            </input>
            <table cellpadding="0" cellspacing="0"  width="764px">
                <tr>
                    <th  class="admTableTDLast" width="100%">
                        <table>
                            <tr>
                                <td  >
                                    <xsl:value-of select="java:getString($dict_media_catalog, 'ENTER_NEW_NAME')"/>:&#160;
                                </td>
                                <td>
                                    <input class="admTextArea admWidth200" type="text" name="newName">
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="@old-name"/>
                                        </xsl:attribute>
                                    </input>
                                </td>
                                <td >
                                    <xsl:text>&#160;&#046;</xsl:text>
                                    <xsl:value-of select="@extension"/>
                                </td>

                            </tr>
                        </table>
                    </th>


                </tr>

                <tr>
                    <th class="admTableTDLast" >
                        <div class="admNavPanelInp" style="padding:0;margin:0;">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavPanelInp" style="width:90px;" type="submit" name="rename">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_media_catalog, 'RENAME_FILE')"/></xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </th>
                </tr>
            </table>
        </form>
    </xsl:template>

    <!-- ******************* COMMAND INFO *************************** -->
    <xsl:template name="command-info">
        <table cellspacing="0" cellpadding="0"  width="764px" >
            <tr>
                <th class="admTableTDLast admRed"  colspan="3" style="height:39px;">
                    <xsl:value-of select="java:getString($dict_media_catalog, 'RENAME')"/>:

                    <xsl:value-of select="/negeso:page/negeso:rename-resource-option/@old-name"/>&#046;
                    <xsl:value-of select="/negeso:page/negeso:rename-resource-option/@extension"/>
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
