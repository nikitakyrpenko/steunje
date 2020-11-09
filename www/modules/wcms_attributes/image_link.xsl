<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  View of a list item edit interface.

  @version		2004.11.08
  @author		Alexander G. Shkabarnya
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

    <!-- Include/Import -->
    <xsl:include href="/xsl/negeso_body.xsl"/>
    <!-- NEGESO JAVASCRIPT Temaplate -->
    <xsl:template name="java-script">
        <script language="JavaScript">
            <xsl:text disable-output-escaping="yes">
        <![CDATA[
		
		var caller = new Object();
		if(window.dialogArguments) {
			caller = window.dialogArguments;
		} else {
		   caller = window.opener;
		}
		
		function checkUploadForm() {
			document.updateImage.alt.value.replace(new RegExp("\'","gi"), "/'");
			return true;			
		}

        function getPagesList(){
        {
            var answer =
                window.showModalDialog(
                    "?command=get-pages-list-command",
                     this,
                    "dialogHeight: 540px; dialogWidth: 540px; help: No; scroll: Yes; status: No;"
                ).then(function(answer){
            try{
                if(typeof(answer) != "undefined" && answer != null){
                    updateImage.alt.value = answer[0];
					updateImage.link.value = answer[1];
                }
            }catch(e){}
             });
        }
        
        ]]>
        </xsl:text>
        </script>
        <script type="text/javascript" src="/script/jquery.min.js"></script>
		<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script type="text/javascript" src="/script/common_functions.js" xml:space="preserve"> </script>
        <script type="text/javascript" src="/script/media_catalog.js" xml:space="preserve"> </script>
    </xsl:template>
    <xsl:variable name="lang" select="/*/@interface-language"/>
    <xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
    <xsl:variable name="dict_picture_frame_adds" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_picture_frame_adds.xsl', $lang)"/>

    <xsl:template match="//negeso:page">
        <html>
            <head>
                <title>
                    <xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.IMAGE_ATTRIBUTES_EDITING')"/>
                </title>
                <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>                
                <xsl:call-template name="java-script"/>
            </head>
            <body class="dialogSmall">
                <xsl:call-template name="NegesoBody">
                    <xsl:with-param name="helpLink" select="'/admin/help/cms-help_nl.html'"/>
                    <xsl:with-param name="backLink" select="'false'"/>
                </xsl:call-template>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="negeso:page"  mode="admContent">
        <xsl:apply-templates select="negeso:image" mode="image_link"/>
    </xsl:template>
    
    <xsl:template match="negeso:image" mode="image_link">

        <form name="updateImage" method="post" action="" enctype="multipart/form-data" onsubmit="return checkUploadForm();">
            <input type="hidden" name="command" value="update-wcsm-link"/>
            <input type="hidden" name="action" value="update"/>
            <input type="hidden" name="idImg" value="{@img-id}" />
            <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
                <tr>
                    <td align="center" class="admNavPanelFont" colspan="2">
                        <!-- TITLE -->
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.IMAGE_ATTRIBUTES_EDITING')"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast" style="padding-left:25px;">
                        <label>URL to page</label>
                    </td>
                    <td class="admTableTDLast" id="admTableTDtext">
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td style="width:300px">
                                    <input class="admTextArea"  id="link" name="link" value="{@link}" />
                                </td>
                                
                                <td style="text-align:left">
                                    <button class="admNavChoose" name="mcButton" onClick="getPagesList()">
                                        <img src="/images/media_catalog/media_catalog_logo_browse.gif"></img>
                                    </button>                                   
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast" style="padding-left:25px;">
                        <label id="targetlistlabel" for="targetlist">Target</label>
                    </td>
                    <td class="admTableTDLast" id="admTableTDtext">
                        <select class="admTextArea" name="target">
                            <option value="_self">
                                <xsl:if test="@target!='_blank'">
                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>Open in this window / frame
                            </option>
                            <option value="_blank">
                                <xsl:if test="@target='_blank'">
                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>Open in new window (_blank)
                            </option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast" style="padding-left:25px;">
                        <label for="title">Title</label>
                    </td>
                    <td class="admTableTDLast">
                        <input class="admTextArea" style="width:300px;" id="title" name="alt" value="{@title}" />
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast"></td>
                    <td class="admTableTDLast">
                        <div class="admNavPanelInp" align="center">
                            <div class="imgL"></div>
                            <div align="center">
                                <input class="admNavbarInp" type="submit" name="submitBtn">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SUBMIT')"/></xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>                        
                    </td>
                </tr>
                <tr>
                    <td class="admTableFooter" colspan="2">&#160;</td>
                </tr>
            </table>
        </form>
    </xsl:template>

</xsl:stylesheet>
