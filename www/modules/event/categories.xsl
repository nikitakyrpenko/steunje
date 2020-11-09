<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}       
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.

  @version      2004.03.09
  @author       Olexiy.Strashko
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
        var s_DeleteConfirmation = "<xsl:value-of select="java:getString($dict_common, 'DELETE_CONFIRMATION')"/>";

        <![CDATA[
        function AddCategory() {
            categoriesManageForm.command.value = "create-event-category-command";
            categoriesManageForm.submit();
        }

        function DeleteCategory(id) {
            if (confirm(s_DeleteConfirmation)) {
                categoriesManageForm.command.value = "delete-event-category-command";
                categoriesManageForm.id.value = id;
                categoriesManageForm.submit();
            }
        }

        function EditCategory(id) {
            categoriesManageForm.command.value = "get-event-category-command";
            categoriesManageForm.id.value = id;
            categoriesManageForm.submit();
        }
        ]]>
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
        <xsl:call-template name="NavBar"/>            
       <!-- CONTENT -->
        <xsl:apply-templates select="negeso:categories"/>
        <xsl:call-template name="NavBar"/>
    </div>
</body>
</html>
</xsl:template>

<!-- ************************************* Categories *********************************** -->
<xsl:template match="/negeso:categories">
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
			<xsl:value-of select="java:getString($dict_categories, 'SELECT_CATEGORY')"/>
        </xsl:with-param>
     </xsl:call-template>
    <form method="POST" name="categoriesManageForm">
        <input type="hidden" name="command"/>
        <input type="hidden" name="id"/>
        <table cellpadding="0" cellspacing="0" class="admNavPanel">
            <tr>
                <td class="admNavbar admCenter">
                    <input class="admNavbarInp" type="button">
                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'ADD_NEW_CATEGORY')"/>&#160;&gt;</xsl:attribute>
                        <xsl:attribute name="onClick">AddCategory()</xsl:attribute>
                    </input>
                </td>
            </tr>
        </table>
        <table cellpadding="0" cellspacing="0"  class="admNavPanel">
            <tr>
                <td class="admNamebar"><xsl:value-of select="java:getString($dict_categories, 'CATEGORY_NAME')"/></td>
                <td class="admNamebar" width="140px"><xsl:value-of select="java:getString($dict_common, 'LANGUAGE')"/></td>
                <td class="admNamebar" colspan="2"><xsl:value-of select="java:getString($dict_common, 'ACTION')"/></td>
            </tr>
            <xsl:apply-templates select="negeso:category"/>
        </table>
    </form>
</xsl:template>

<!-- ************************************* Category ******************************* -->
<xsl:template match="negeso:category" >
    <tr>
        <td class="admMainTD">
            <a class="admAnchor">
                <xsl:attribute name="href">
                    ?command=get-event-category-command&amp;id=<xsl:value-of select="@id"/>
                </xsl:attribute>
                <xsl:value-of select="@name" disable-output-escaping="yes"/>
            </a>
        </td>
        <td class="admLightTD"><xsl:value-of select="@language"/></td>
        <td class="admDarkTD admWidth30">
            <img class="admHand" src="/images/edit.gif" onClick="EditCategory({@id});">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
            </img>
        </td>
        <td class="admLightTD admWidth30">
            <img class="admHand" src="/images/delete.gif" onClick="DeleteCategory({@id});">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
            </img>
        </td>
    </tr>
</xsl:template>

</xsl:stylesheet>
