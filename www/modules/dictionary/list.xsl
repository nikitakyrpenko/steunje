<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}       
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.

  @version      2004.06.07
  @author       Sergiy Oliynyk
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>

<xsl:include href="/xsl/negeso_header.xsl"/>
<xsl:template match="/">
<html>
<head>
    <title>Dictionary</title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <xsl:text disable-output-escaping="yes">
    <![CDATA[
    <script language="JavaScript">

        function OpenDictionary(id) {
            filesManageForm.dictionaryFileId.value = id;
            filesManageForm.submit();
        }

        function ManageDictionary(id) {
            filesManageForm.command.value="manage-dictionary-command";
            filesManageForm.dictionaryFileId.value = id;
            filesManageForm.submit();
        }

        function ChangeFileProperties(id) {
            var result = window.showModalDialog(
                "?command=get-dictionary-file-command&dictionaryFileId=" + id,
                this,    
                "dialogHeight: 406px; dialogWidth: 585px; help: No; scroll: No; status: No;");
            if (result != null) {
                filesManageForm.command.value="update-dictionary-file-command";
                filesManageForm.dictionaryFileId.value = id;
                filesManageForm.fileName.value = result[0];
                filesManageForm.description.value = result[1];
                filesManageForm.submit();
            }
        }

        function DeleteFile(id, fileName) {
            if (confirm("Are you sure you want to delete the dictionary file \'" +
                fileName + "\'?"))
            {
                if (confirm("This operation is irrevocable! Are you sure?")) {
                    filesManageForm.command.value="delete-dictionary-file-command";
                    filesManageForm.dictionaryFileId.value = id;
                    filesManageForm.submit();
                }
            }
        }

        function dictionarySearch() {
            var result = window.showModalDialog(
                "?command=search-entries-command", this,
                "dialogHeight: 406px; dialogWidth: 585px; help: No; scroll: No; status: No;");
            if (result != null) {
                filesManageForm.command.value="update-dictionary-file-command";
                filesManageForm.dictionaryFileId.value = id;
                filesManageForm.fileName.value = result[0];
                filesManageForm.description.value = result[1];
                filesManageForm.submit();
            }
        }

        function downloadDictionaryFile() {
            strPage = "?command=get-languages-list-command";
            strAttr = "resizable:on;scroll:on;status:off;dialogWidth:600px;dialogHeight:600px";
            var result = showModalDialog(strPage, null , strAttr);
            if (result != null) {
                window.location.href = 
                    "?command=download-dictionary-command&languages=" + result;
            }
        }

        function LanguageToChanged() {
            filesManageForm.command.value = "get-dictionary-files-command";
            filesManageForm.submit();
        }

        function AddNewFile() {
            var result = window.showModalDialog(
                "?command=get-dictionary-file-command",
                this,
                "dialogHeight: 380px; dialogWidth: 585px; help: No; scroll: No; status: No;");
            if (result != null) {
                filesManageForm.command.value="update-dictionary-file-command";
                filesManageForm.dictionaryFileId.value = '';
                filesManageForm.fileName.value = result[0];
                filesManageForm.description.value = result[1];
                filesManageForm.submit();
            }
        }
        window.focus();
    </script>
    ]]>
    </xsl:text>

    <style type="text/css">
        a:link{color:#000000;}
        a:visited{color:#000000;}
    </style>
</head>
<body>
     <!-- NEGESO HEADER -->
     <xsl:call-template name="NegesoHeader"/>
     <div align="center">
        <!-- CONTENT -->
        <xsl:apply-templates select="negeso:dictionaries"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="/negeso:dictionaries">
    <xsl:call-template name="NavBar">
        <xsl:with-param name="backLink" select= '"../admin/?param=1"'/>
    </xsl:call-template>
    <form method="POST" name="filesManageForm" action="?command" enctype="multipart/form-data">
        <input type="hidden" name="command" value="get-dictionary-command"/>
        <input type="hidden" name="dictionaryFileId"/>
        <input type="hidden" name="fileName"/>
        <input type="hidden" name="description"/>
        <input type="hidden" name="action" value="entries"/>
        <input type="hidden" name="languages"/>

        <xsl:call-template name="tableTitle">
            <xsl:with-param name="headtext">
                <xsl:text>Select file for translation</xsl:text>
            </xsl:with-param>
        </xsl:call-template>
        <table cellpadding="0" cellspacing="0" class="admNavPanel">
            <tr>
                <td class="admMainTD" colspan="2">
                    <xsl:choose>
                        <xsl:when test="@interface_type = 'developer'">
                            <a class="admNone">
                                <xsl:attribute name="href">javaScript:AddNewFile()</xsl:attribute>
                                    &lt;&#160;Add new file&#160;&gt;
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <b>Translation from</b>&#160;
                            <select name="langCodeFrom" style="float: none; margin-top: 3">
                                <xsl:apply-templates select="negeso:languages/negeso:language">
                                    <xsl:with-param name="selected" select="/*/@lang_code_from"/>
                                </xsl:apply-templates>
                            </select>
                            &#160;&#160;&#160;<b>to</b>&#160;
                            <select name="langCodeTo" style="float: none; margin-top: 3" onChange="LanguageToChanged()">
                                <xsl:apply-templates select="negeso:languages/negeso:language">
                                    <xsl:with-param name="selected" select="/*/@lang_code_to"/>
                                </xsl:apply-templates>
                            </select>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
                <td class="admMainTD" colspan="2">
                    <xsl:if test="@interface_type = 'developer'">
                        <a class="admNone">
                            <xsl:attribute name="href">javaScript:dictionarySearch()</xsl:attribute>
                                &lt;&#160;Dictionary search&#160;&gt;
                        </a>
                    </xsl:if>
                    &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
                    <a class="admNone">
                        <xsl:attribute name="href">javaScript:downloadDictionaryFile()</xsl:attribute>
                            &lt;&#160;Get dictionary file&#160;&gt;
                    </a>
                </td>
            </tr>
            <xsl:apply-templates select="negeso:dictionaryFile"/>
        </table>
    </form>
    <xsl:call-template name="NavBar">
        <xsl:with-param name="backLink" select= '"../admin/?param=1"' />
    </xsl:call-template>
</xsl:template>

<xsl:template match="negeso:dictionaryFile">
    <xsl:variable name="readonly">
        <xsl:choose>
            <xsl:when test="@id = '1'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <tr>
        <td class="admMainTD" colspan="2">
            <a href="#">
                <xsl:attribute name ="onClick">
                    <xsl:choose>
                        <xsl:when test="/*/@interface_type = 'developer'">
                            ManageDictionary(<xsl:value-of select="@id"/>);
                        </xsl:when>
                        <xsl:otherwise>
                            OpenDictionary(<xsl:value-of select="@id"/>);
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                <xsl:if test="/*/@selected = @id">
                    <xsl:attribute name="style">color : blue</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="@description"/>
            </a>
        </td>
        <xsl:choose>
            <xsl:when test="/*/@interface_type = 'developer'">
                <td class="admMainTD" style="color: #0033CC;">
                    <xsl:value-of select="@fileName"/>
                </td>
                <td class="admDarkTD admWidth60">
                    <xsl:if test="$readonly='false'">
                        <img class="admHand" src="/images/edit.gif" onClick="ChangeFileProperties({@id})">
                            <xsl:attribute name="title">Edit file properties</xsl:attribute>
                        </img>
                    </xsl:if>
                    &#160;
                    <xsl:if test="$readonly='false'">
                        <img class="admHand" src="/images/delete.gif" onClick="DeleteFile({@id}, '{@fileName}')">
                            <xsl:attribute name="title">Delete</xsl:attribute>
                        </img>
                    </xsl:if>
                </td>
            </xsl:when>
            <xsl:otherwise>
                <td class="admMainTD">
                    <xsl:choose>
                        <xsl:when test="@empty_strings = '0'">
                            <xsl:attribute name="style">color: #0033CC;</xsl:attribute>
                            translated
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="style">color: #FF2B04;</xsl:attribute>
                            <xsl:value-of select="@empty_strings"/> empty strings
                        </xsl:otherwise>
                    </xsl:choose>                
                </td>
            </xsl:otherwise>
        </xsl:choose>
    </tr>
</xsl:template>

<xsl:template match="negeso:language">
    <xsl:param name="selected" />
    <option value="{@code}">
        <xsl:if test="$selected = @code">
            <xsl:attribute name="selected">true</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="@name"/>
    </option>
</xsl:template>

</xsl:stylesheet>
