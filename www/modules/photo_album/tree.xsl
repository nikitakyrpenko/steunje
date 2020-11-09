<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${album.xsl}
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @author       Sergiy Oliynyk

-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_photo_album" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_photo_album.xsl', $lang)"/>

<xsl:template match="/">
<html>
    <head>
        <title><xsl:value-of select="java:getString($dict_photo_album, 'SELECT_ALBUM')"/></title>
        <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>

        <script type="text/javascript"  src="/script/jquery.min.js"/>
        <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script type="text/javascript" src="/script/common_functions.js"></script>
        
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
                .albumPath {
                    color: #000;
                    font-weight: bold;
                    font-size: 12px;
                }
				            
                .treeFolder{                    
                    float:left;
                    margin: 15px 0 0 0;
                }

                .albumImg{                    
                    float:left;
                    margin:5px 10px 0 0;
                }
            </style>
            <script language="JavaScript">
                var openImg = new Image();
                openImg.src = "/images/photo_album/smallfolderopen.png";
                var closedImg = new Image();
                closedImg.src = "/images/photo_album/add.png";

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
                    if (objImg.src.indexOf('add.png')>-1)
                        objImg.src = openImg.src;
                    else
                        objImg.src = closedImg.src;
                }

                var selectedAlbum = null;
            
                function setSelection(id) {
                    if (selectedAlbum != null) {
                        album.style.fontWeight = "normal";
                    }
                    album = document.all["album_" + id];
                    if (selectedAlbum != album) {                        
                        album.style.fontWeight = "bold";
                        selectedAlbum = album;
                        copyButton.disabled = false;
                        moveButton.disabled = false;
                    }
                    else {
                        selectedAlbum = null;
                        copyButton.disabled = true;
                        moveButton.disabled = true;
                    }
                }

                function returnSelection(move) {                    
                    var result = new Array();
                    result[0] = selectedAlbum.getAttribute('albumId');
                    result[1] = move;
                    window.returnValue = result;
                    window.close();
                }
            </script>
            ]]>
        </xsl:text>
    </head>

    <body class="menu">    
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink">
                <xsl:text>/admin/help/cph1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
            </xsl:with-param>        
        </xsl:call-template>

        <xsl:call-template name="buttons"/>
    </body>
</html>
</xsl:template>

<xsl:template match="/negeso:photo_album" mode="admContent">
    <table cellspacing="0" cellpadding="0" width="100%" class="admTable">
        <tr>
            <td align="center" class="admNavPanelFont">
                <xsl:call-template name="tableTitle">
                    <xsl:with-param name="headtext">
                        <xsl:value-of select="java:getString($dict_photo_album, 'SELECT_ALBUM')"/>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>
        <tr>
            <td class="admTableTDLast">
                <xsl:apply-templates select="negeso:album"/>
            </td>
        </tr>
        <tr>
            <td class="admTableFooter">&#160;</td>
        </tr>
    </table>

   
</xsl:template>

<xsl:template match="negeso:album">    
    <xsl:if test="@canView = 'true'">
        <xsl:choose>
            <xsl:when test="count(./*)">
                <span class="trigger admLeft" onClick="showBranch('branch_{@id}'); swapFolder('folder_{@id}');" unselectable="on">
                    <img class="albumImg" src="/images/photo_album/add.png" id="folder_{@id}" hspace="5"/>
                    <xsl:choose>
                        <xsl:when test="/negeso:photo_album/@albumId != @id">
                            <a class="treeFolder" id="album_{@id}" albumId="{@id}" href="#" onClick="setSelection({@id});">
                                <xsl:value-of select="@name"/>
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <span class="albumPath treeFolder" style="color: gray">
                                <xsl:value-of select="@name"/>
                            </span>
                        </xsl:otherwise>
                    </xsl:choose>
                </span>
                <br clear="all"/>
            </xsl:when>
            <xsl:otherwise>
                <span class="admLeft" unselectable="on">
                    <img class="albumImg" src="/images/photo_album/smallfolder.png" hspace="5"/>
                    <xsl:choose>
                        <xsl:when test="/negeso:photo_album/@albumId != @id">
                            <a class="treeFolder" id="album_{@id}" albumId="{@id}" href="#" onClick="setSelection({@id});">
                                <xsl:value-of select="@name"/>
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <span class="treeFolder">
                                <xsl:value-of select="@name"/>
                            </span>
                        </xsl:otherwise>
                    </xsl:choose>
                </span>
                <br clear="all"/>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:if test="count(./*)">
            <span class="branch admLeft" id="branch_{@id}">
                <xsl:apply-templates select="negeso:album"/>
            </span>
        </xsl:if>
    </xsl:if>
</xsl:template>

<xsl:template name="buttons">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div >
                        <input id="copyButton" disabled = "true" class="admNavbarInp" type="button" onClick="returnSelection(false);">
                            <xsl:attribute name="value">
                                <xsl:value-of select="java:getString($dict_common, 'COPY')"/>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>

                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div >
                        <input id="moveButton" disabled = "true" class="admNavbarInp" type="button" onClick="returnSelection(true);">
                            <xsl:attribute name="value">
                                <xsl:value-of select="java:getString($dict_common, 'MOVE')"/>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</xsl:template>

</xsl:stylesheet>
