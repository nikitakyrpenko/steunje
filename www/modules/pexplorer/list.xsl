<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}       
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @version      2004.03.17
  @author       Sergiy Oliynyk
  @author       Volodymyr Snigur
  
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">
		
<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_page_explorer" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_page_explorer.xsl', $lang)"/>

<xsl:output method="html"/>

<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_page_explorer, 'CONTENT_WEBSITE')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
    <script language="JavaScript" src="/script/jquery.min.js"/>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"/>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"/>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    <script language="JavaScript" src="/script/calendar_picker.js"/>

    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        <style>
            .trigger {
                cursor: pointer;
                cursor: hand;
            }
            .branch {
                display: none;
                margin-left: 16px;
            }
        </style>
        <script language="JavaScript">
            var openImg = new Image();
            openImg.src = "/images/media_catalog/openFolderClear.png";
            var closedImg = new Image();
            closedImg.src = "/images/media_catalog/folderClear.png";

            function showBranch(branch) {
                if (document.getElementById(branch) != null) {
                    var objBranch = document.getElementById(branch).style;
                    if(objBranch.display=="block")
                        objBranch.display="none";
                    else
                        objBranch.display="block";
                }
            }

            function swapFolder(img) {
                objImg = document.getElementById(img);
                if (objImg.src.indexOf('folderClear.png')>-1)
                    objImg.src = openImg.src;
                else
                    objImg.src = closedImg.src;
            }

            function setSelection(title, filename, pageId, category, id) {
                var callingWindow = null;
                var prefix = "";
                var result = new Array();
                result[0] = title;
                result[1] = "";
                result[2] = id;
                result[3] = pageId;
                try {
                    callingWindow = window.dialogArguments;
                    prefix = callingWindow.urlPrefix;
                }
                catch (e) {
                    prefix = "";
                }
                if (typeof(prefix) == "undefined" || prefix == null)
                    prefix = "";

                if (category == "popup")
                    result[1] = "javascript:displayPopup('" +
                        prefix + filename + "');"
                else
                    result[1] = prefix + filename;
                window.returnValue = result;
                
                // For new RTE
                try {
                	window.opener.MediaCatalog.set_file_URL(result[1], window.opener.MediaCatalog.insert_mode);
					window.opener.MediaCatalog.win.focus();
                } catch (e) {}
                sleep(500);
                window.close();
            }

function sleep(milliSeconds) {
        	var startTime = new Date().getTime(); // get the current time
        	while (new Date().getTime() < startTime + milliSeconds); 
        }

function expandOrCollapse(obj, id, isMenu) {
    if (isMenu){
        var parent = $(obj).parent();
        var isExpand = $(parent).attr('isExpand');
        if (isExpand == 'true') {
            $(parent).attr('isExpand', 'false');
            $(parent).children('div[parentId]').hide();            
        } else {
            $(parent).attr('isExpand', 'true');
            $(parent).children('div[parentId]').show();            
        }
    } else{
        var parent = $(obj).parent()
        var isExpand = $(parent).attr('isExpand');
        var imgFolder = $(parent).find('img.admFolder');
        if (isExpand == 'true') {
            $(parent).attr('isExpand', 'false');
            $('div[parentId=' + id + ']').hide();
            $(imgFolder).attr('src', '/images/media_catalog/folderClear.png');
        } else {
            $(parent).attr('isExpand', 'true');
            $('div[parentId=' + id + ']').show();
            $(imgFolder).attr('src', '/images/media_catalog/openFolderClear.png');
        }
    }
}

        </script>
        <script type="text/javascript" src="/script/conf.js"></script>


        ]]>
    </xsl:text>
</head>

<body class="dialogSmall" style="padding-left:10px;">
    <!-- NEGESO HEADER -->
    <!--<xsl:call-template name="iter"/>-->
    <xsl:call-template name="NegesoBody">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cnw1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>            
	   </xsl:call-template>    
</body>
</html>
</xsl:template>
    
        
<xsl:template name="iter">
	<xsl:for-each select="./*">
		<b><xsl:value-of select="name()" /></b><br/>
		<xsl:for-each select="./attribute::*">
			<xsl:value-of select="name()" /> = <xsl:value-of select="." /><br/>
		</xsl:for-each><br/>
		<i>Text: </i><xsl:value-of select="text()" />
		<br/>
		<div style="margin-left: 30px">
			<xsl:call-template name="iter"/>
		</div>
	</xsl:for-each>
</xsl:template> 

<!-- ************************************* Root ********************************** -->
   
<xsl:template match="negeso:root" mode="admContent">
   <form method="POST" name="modulesManageForm">
        <table  border="0" style="background:#EAEAEA;" cellpadding="0" cellspacing="0"  width="100%"  >
            <tr>
                <td align="center" class="admNavPanelFont" >
                    <!-- TITLE -->
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:value-of select="java:getString($dict_page_explorer, 'CONTENT_WEBSITE')"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td  valign="center" >
                    <xsl:apply-templates select="negeso:class"/>
                </td>
            </tr>
        </table>
    </form>
</xsl:template>
    

<!-- ************************************ Class ******************************* -->
    <xsl:template match="negeso:class">
        <table  border="0" cellpadding="0" cellspacing="0" width="100%" >
            <tr>
                <td style="" valign="center" width="100%">
                    <xsl:choose>
                        <xsl:when test="@title='menu'">
                            <xsl:apply-templates select="negeso:menu"/>                            
                        </xsl:when>                    
                        <xsl:when test="@top = 'true'">
                            <div class="admTableTDLast" style="widht:764px;"  onClick="showBranch('branch_{@id}'); swapFolder('folder_{@id}');"  unselectable="on">
                                <img  src="/images/media_catalog/folderClear.png" id="folder_{@id}" style="vertical-align:middle;" />
                                <xsl:value-of select="@title"/>
                                <xsl:if test="@lang_code">
                                    <xsl:text> (</xsl:text>
                                    <xsl:value-of select="@lang_code"/>
                                    <xsl:text>)</xsl:text>
                                </xsl:if>
                            </div>
                        </xsl:when>                        
                        <xsl:otherwise>
                            <span onClick="showBranch('branch_{@id}'); swapFolder('folder_{@id}');" unselectable="on">
                                <img src="/images/media_catalog/folderClear.png" id="folder_{@id}" style="vertical-align:middle;" />
                                <xsl:value-of select="@title"/>
                            </span><br/>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
            <tr>
                <td valign="center">
                    <xsl:if test="count(./*)">
                        <span class="branch" id="branch_{@id}">
                            <xsl:apply-templates select="negeso:class"/>
                            <xsl:apply-templates select="negeso:page">
                                <xsl:sort select="@filename" order="ascending"/>
                            </xsl:apply-templates>
                        </span>
                    </xsl:if>
                </td>
            </tr>
        </table>
    </xsl:template>

<!-- ************************************ Page ******************************** -->
<xsl:template match="negeso:page">
    <img class="admLeft" src="/images/media_catalog/docFileClear.png"/>
    <a title="{@title}" class="pageExplorerLink" onClick="setSelection(this.title, '{@filename}','','{@category}','{@id}')">
        <xsl:value-of select="@title"/>
    </a>
    <xsl:if test="@lang_code">
        <xsl:text> (</xsl:text>
        <xsl:value-of select="@lang_code"/>
        <xsl:text>)</xsl:text>      
    </xsl:if>
    <!-- Other atributes were deleted for a while. -->
    <br/>    
</xsl:template>
    
<xsl:template match="negeso:menu">
    <xsl:param name="padding" select="'40'"/>
    <xsl:if test="count(negeso:menu_item) &gt; 0">            
        <div class="admTableTDLast" style="widht:764px;"  unselectable="on" isExpand="false">
            <img  src="/images/media_catalog/folderClear.png"  onclick="expandOrCollapse(this,'menu{@langCode}',true)" style="vertical-align:middle;" />
            Menu (<xsl:value-of select="@langCode"/>)
            <!-- Initial left margin -->
            <xsl:apply-templates select="negeso:menu_item" mode="li">
                <xsl:with-param name="padding" select="$padding"/>
            </xsl:apply-templates>
        </div>
        
    </xsl:if>
</xsl:template>
    
<xsl:template match="negeso:menu_item">
    <xsl:param name="padding" select="'0'"/>
    <xsl:apply-templates select="negeso:menu_item" mode="li">
        <xsl:with-param name="padding" select="$padding"/>
    </xsl:apply-templates>
</xsl:template>  
        
<xsl:template match="negeso:menu_item" mode="li">
    <xsl:param name="padding" select="'0'"/>
    <div parentId="{@parentId}" class="admTableTDLast" style="widht:764px;display:none;"  unselectable="on" isExpand="false">
        <div style="padding-left: {$padding};">
            <xsl:choose>
                <xsl:when test="count(negeso:menu_item)">
                    <img src="/images/media_catalog/folderClear.png" width="32" height="32" onclick="expandOrCollapse(this,{@id},false)" style="vertical-align:middle;" />
                </xsl:when>
                <xsl:otherwise>
                    <img src="/images/media_catalog/webFileClear.png" width="32" height="32" class="admFile" style="vertical-align:middle;" />
                </xsl:otherwise>
            </xsl:choose>
            <a title="{@title}" class="pageExplorerLink" onClick="setSelection(this.title, '{@href}','{@pageId}','{@category}','{@id}')">
                <xsl:value-of select="@title"/>
            </a>
        </div>
        <xsl:apply-templates select="self::negeso:menu_item">
            <!-- Increment of left margin -->
            <xsl:with-param name="padding" select="$padding + 10"/>
        </xsl:apply-templates>        
    </div>
    
</xsl:template>   
   
</xsl:stylesheet>
