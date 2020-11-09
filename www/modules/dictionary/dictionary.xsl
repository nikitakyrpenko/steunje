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
    <title>Edit <xsl:value-of select="negeso:dictionary/@description"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script language="JavaScript">

        function save() {
            dictionaryManageForm.submit();
        }

        function refreshSession() {
            window.setTimeout("refreshSession()", 1000*60*15);
            new Image().src = "/";
        }

        refreshSession();
        window.focus();
    </script>
</head>
<body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)" id="ClientManager">
    <!-- NEGESO HEADER -->
     <xsl:call-template name="NegesoHeader"/>
     <div align="center">
        <!-- CONTENT -->
        <xsl:apply-templates select="negeso:dictionaryFile"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:dictionaryFile">
    <xsl:call-template name="NavBar">
        <xsl:with-param name="backLink" select="concat('?langCodeFrom=', @langCodeFrom, '&amp;langCodeTo=', @langCodeTo, '&amp;selected=', @id)"/>
    </xsl:call-template>
    <xsl:call-template name="tableTitle">
	    <xsl:with-param name="headtext">
		    <xsl:value-of select="java:getString($dict_common, 'EDIT')"/><xsl:text> </xsl:text>'<xsl:value-of select="@description"/>'
	    </xsl:with-param>
    </xsl:call-template>

    <form method="POST" name="dictionaryManageForm" action="" enctype="multipart/form-data">
        <input type="hidden" name="command" value="update-dictionary-command"/>
        <input type="hidden" name="dictionaryFileId" value="{@id}"/>
        <input type="hidden" name="langCodeFrom" value="{@langCodeFrom}"/>
        <input type="hidden" name="langCodeTo" value="{@langCodeTo}"/>

        <table cellpadding="0" cellspacing="0" class="admNavPanel">
            <tr>
                <td class="admNamebar"><xsl:value-of select="@languageFrom"/></td>
                <xsl:if test="@languageFrom != @languageTo">
                    <td class="admNamebar"><xsl:value-of select="@languageTo"/></td>
                </xsl:if>
            </tr>
            <xsl:apply-templates select="negeso:dictionaryEntries" />
        </table>
        <table cellpadding="0" cellspacing="0" class="admNavPanel">
    		    <tr>
    			    <td class="admNavbar admCenter">
    			      <input class="admNavbarInp" name="saveButton" onClick="save()" value="&lt;&#160;Save&#160;&gt;" type="button"/>
    			      <input class="admNavbarInp" value="&lt;&#160;Reset&#160;&gt;" type="reset"/>
               </td>
		      </tr>
        </table>
    </form>
    <xsl:call-template name="NavBar">
        <xsl:with-param name="backLink" select="concat('?langCodeFrom=', @langCodeFrom, '&amp;langCodeTo=', @langCodeTo, '&amp;selected=', @id)"/>
    </xsl:call-template>
</xsl:template>

<xsl:template match="negeso:dictionaryEntries">
    <tr>
        <xsl:apply-templates select="negeso:dictionary"/>
    </tr>
</xsl:template>

<xsl:template match="negeso:dictionary">
    <xsl:param name="type"/>
    <td class="admLightTD">
        <xsl:if test="/negeso:dictionaryFile/@langCodeFrom = /negeso:dictionaryFile/@langCodeTo">
            <xsl:attribute name="colspan">2</xsl:attribute>
        </xsl:if>
        <input class="admTextArea admWidth335" type="text">
            <xsl:attribute name="save">
                <xsl:choose>
                    <xsl:when test="position() = 1">false</xsl:when>
                    <xsl:otherwise>true</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <!--
            <xsl:if test="@entry=''">
                <xsl:attribute name="style">background : #F5F5FF</xsl:attribute>
            </xsl:if>
            -->
            <xsl:attribute name="name">id<xsl:value-of select="@id"/></xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="@entry"/></xsl:attribute>
        </input>
    </td>
</xsl:template>

</xsl:stylesheet>
