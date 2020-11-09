<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}       
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @version      2004.03.09
  @author       Sergiy Oliynyk
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_categories" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_categories.xsl', $lang)"/>

<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_categories, 'SELECT_CATEGORY')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script language="JavaScript">
        <xsl:text disable-output-escaping="yes">
        <![CDATA[
        function saveChanges() {
            mainForm.submit();
        }
        ]]>
       </xsl:text>
    </script>
</head>
<body>
    <!-- NEGESO HEADER -->
    <xsl:call-template name="NegesoHeader">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cev1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>
    </xsl:call-template>
    <div align="center">
        <xsl:call-template name="NavBar">
            <xsl:with-param name="backLink" select='"?command=get-event-categories-command"' />
        </xsl:call-template>
       <!-- CONTENT -->
        <xsl:apply-templates select="negeso:eventCategory"/>
        <xsl:call-template name="NavBar">
            <xsl:with-param name="backLink" select='"?command=get-event-categories-command"' />
        </xsl:call-template>
    </div>
</body>
</html>
</xsl:template>

<!-- ************************************* Categories *********************************** -->

<xsl:template match="/negeso:eventCategory">
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
            <xsl:value-of select="java:getString($dict_categories, 'EVENTS_CATEGORY')"/>
        </xsl:with-param>
     </xsl:call-template>
    <form onReset="onReset()" method="POST" name="mainForm">
        <input type="hidden" name="command" value="update-event-category-command"/>
        <input type="hidden" name="id" value="{@id}"/>
        <table class="admNavPanel" cellspacing="0" cellpadding="0">
            <!-- Title Field -->
            <tr>
                <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
                <td class="admLightTD">
                    <input class="admTextArea admWidth95perc" type="text" name="name">
                        <xsl:attribute name="value"><xsl:value-of select="@name"/></xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- Language -->
            <tr>
                <td class="admMainTD"><xsl:value-of select="java:getString($dict_common, 'LANGUAGE')"/></td>
                <td class="admLightTD" style="text-align: left; padding-left : 8">
                    <select name="langId" style="float: none; width: 100;">
                        <xsl:apply-templates select="negeso:languages"/>
                    </select>
                </td>
            </tr>
        </table>
        <!-- Update/reset fields -->
        <table cellpadding="0" cellspacing="0"  class="admNavPanel">
            <tr>
                <td class="admNavPanel admNavbar admCenter">
                    <input name="saveButton" class="admNavbarInp" type="button" onClick="saveChanges()">
                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SAVE')"/>&#160;&gt;</xsl:attribute>
                    </input>
                    <input name="resetButton" class="admNavbarInp" type="button" onClick="mainForm.reset();">
                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'RESET')"/>&#160;&gt;</xsl:attribute>
                    </input>
                </td>
            </tr>
        </table>
    </form>
</xsl:template>

<xsl:template match="negeso:language">
    <xsl:param name="selected" />
    <option value="{@id}">
        <xsl:if test="/*/@langId = @id">
            <xsl:attribute name="selected">true</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="@name"/>
    </option>
</xsl:template>

</xsl:stylesheet>
