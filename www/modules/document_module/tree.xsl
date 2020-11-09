<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${tree.xsl}
 
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

<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>

<xsl:template match="/">
<html>
<head>
    <title>Select category</title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
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
            .categoryPath {
                color: #008033;
                font-weight: bold;
                font-size: 12px;
            }
        </style>
        <script language="JavaScript">
            var openImg = new Image();
            openImg.src = "/images/photo_album/smallfolderopen.gif";
            var closedImg = new Image();
            closedImg.src = "/images/photo_album/add.gif";

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
                if (objImg.src.indexOf('add.gif')>-1)
                    objImg.src = openImg.src;
                else
                    objImg.src = closedImg.src;
            }

            var selectedAlbum = null;
            
            function setSelection(id) {
                if (selectedAlbum != null) {
                    selectedAlbum.style.color = "008033";
                    selectedAlbum.style.background = "white";
                }
                category = document.all["category_" + id];
                if (selectedAlbum != category) {
                    category.style.color = "white";
                    category.style.background = "blue";
                    selectedAlbum = category;
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
                result[0] = selectedAlbum.categoryId;
                result[1] = move;
                window.returnValue = result;
                window.close();
            }
        </script>
        ]]>
    </xsl:text>
</head>

<body class="menu">
    <!-- NEGESO HEADER -->
	 <xsl:call-template name="NegesoHeader">
	     <xsl:with-param name="helpLink"/>
	 </xsl:call-template>
    <div align="center">
        <xsl:call-template name="NavBar"/>
        <xsl:call-template name="tableTitle">
		      <xsl:with-param name="headtext">
                    Select category
		      </xsl:with-param>
	     </xsl:call-template>
        <!-- CONTENT -->
        <xsl:apply-templates select="negeso:document_categories"/>
        <xsl:call-template name="NavBar"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="/negeso:document_categories">
    <table class="admNavPanel" cellspacing="0" cellpadding="0" border="0" height="320">
        <tr>
            <td valign="top" class="admLeft">
                <xsl:apply-templates select="negeso:category/negeso:category"/>
             </td>
        </tr>
        <tr>
            <td class="admNavPanel admNavbar admCenter">
                <input id="copyButton" disabled = "true" class="admNavbarInp" type="button" onClick="returnSelection(false);">
                    <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'COPY')"/>&#160;&gt;</xsl:attribute>
                </input>
                <input id="moveButton" disabled = "true" class="admNavbarInp" type="button" onClick="returnSelection(true);">
                    <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'MOVE')"/>&#160;&gt;</xsl:attribute>
                </input>
            </td>
        </tr>
    </table>
</xsl:template>

<xsl:template match="negeso:category">
    <xsl:if test="@canView = 'true'">
        <xsl:choose>
            <xsl:when test="count(./*)">
                <span class="trigger admLeft" onClick="showBranch('branch_{@id}'); swapFolder('folder_{@id}');" unselectable="on">
                    <img src="/images/photo_album/add.gif" id="folder_{@id}" hspace="5"/>
                    <xsl:choose>
                        <xsl:when test="/negeso:document_categories/@categoryId != @id">
                            <a id="category_{@id}" categoryId="{@id}" href="#" class="admNone categoryPath" onClick="setSelection({@id});"><xsl:value-of select="@name"/></a>
                        </xsl:when>
                        <xsl:otherwise>
                            <span class="categoryPath" style="color: gray"><xsl:value-of select="@name"/></span>
                        </xsl:otherwise>
                    </xsl:choose>
                </span>
            </xsl:when>
            <xsl:otherwise>
                <span class="admLeft" unselectable="on">
                    <img src="/images/photo_album/smallfolder.gif" hspace="5"/>
                    <xsl:choose>
                        <xsl:when test="/negeso:document_categories/@categoryId != @id">
                            <a id="category_{@id}" categoryId="{@id}" href="#" class="admNone categoryPath" onClick="setSelection({@id});"><xsl:value-of select="@name"/></a>
                        </xsl:when>
                        <xsl:otherwise>
                            <span class="categoryPath" style="color: gray"><xsl:value-of select="@name"/></span>
                        </xsl:otherwise>
                    </xsl:choose>
                </span>
            </xsl:otherwise>
        </xsl:choose>
        <br/>
        <xsl:if test="count(./*)">
            <span class="branch admLeft" id="branch_{@id}">
                <xsl:apply-templates select="negeso:category"/>
            </span>
        </xsl:if>
    </xsl:if>
</xsl:template>

</xsl:stylesheet>
