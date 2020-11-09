<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.

  Browse mail templates command 
 
  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<!-- Include/Import -->
<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_newsletter" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_newsletter.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->    
<xsl:template name="java-script">
    <script language="JavaScript">
        var s_DeleteTemplateConfirmation = "<xsl:value-of select="java:getString($dict_newsletter, 'DELETE_TEMPLATE_CONFIRMATION')"/>";

    <xsl:text disable-output-escaping="yes">
        <![CDATA[

        function addTemplate() {
            document.operateForm.command.value = "nl-get-edit-template-page";
            document.operateForm.updateTypeField.value = "insert";
        }

        function editTemplate(targetId) {
            document.operateForm.command.value = "nl-get-edit-template-page";
            document.operateForm.nlTemplateId.value = targetId;
            document.operateForm.submit();
        }


        function deleteTemplate(targetId) {
            if (confirm(s_DeleteTemplateConfirmation)) {
                document.operateForm.command.value = "nl-delete-template";
                document.operateForm.nlTemplateId.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }
        
        ]]>
        </xsl:text>
    </script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_newsletter, 'BROWSE_MAIL_TEMPLATES')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <xsl:call-template name="java-script"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <!-- NEGESO HEADER -->
    <xsl:call-template name="NegesoHeader">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cnl1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>
    </xsl:call-template>
    <div align="center">
        
        <!-- CONTENT -->
        <xsl:apply-templates select="negeso:page"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page">
    <!-- NavBar -->
    <xsl:call-template name="NavBar">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cnl_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>
    </xsl:call-template>
    <!-- Path -->
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
        <tr>
            <td class="admBold">
                <xsl:apply-templates select="negeso:path"/>
            </td>
            <td class="admStatusMessage" align="center">
                <xsl:value-of select="@status-message"/>
            </td>
        </tr>
    </table>
    <!-- TITLE -->
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
            <xsl:value-of select="java:getString($dict_newsletter, 'BROWSE_MAIL_TEMPLATES')"/>
        </xsl:with-param>
    </xsl:call-template>
    <!-- Content -->
    <xsl:apply-templates select="negeso:mail-templates"/>
    <!-- NavBar -->
    <xsl:call-template name="NavBar">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cnl_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>
    </xsl:call-template>

</xsl:template>

<xsl:template match="negeso:mail-templates">
            <!-- Render HEADER -->

            <form method="POST" name="operateForm" action="">
            <input type="hidden" name="command" value="nl-browse-template"></input>
            <input type="hidden" name="type" value="none"></input>
            <input type="hidden" name="nlTemplateId" value="-1"></input>
            <input type="hidden" name="updateTypeField" value="update"></input>

            
            <table cellpadding="0" cellspacing="0" class="admNavPanel">
                <tr>
                    <td class="admNavbar admCenter">
                        <table width="100%">
                        <tr><td width="100%" align="center">
                            <input class="admNavbarInp adm" type="submit" onClick = "return addTemplate()">
                            <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_newsletter, 'ADD_NEW_TEMPLATE')"/>&#160;&gt;</xsl:attribute>
                            </input>
                        </td></tr>
                        </table>
                   </td>
                </tr>
            </table>

            <table class="admNavPanel" cellspacing="0" cellpadding="0">
                <tr>
                    <!-- <td class="admNamebar"><xsl:value-of select="java:getString($dict_newsletter, 'MULTILINGUAL')"/></td> -->
                    <td class="admNamebar"><xsl:value-of select="java:getString($dict_common, 'NAME')"/></td>
                    <td class="admNamebar" colspan="2">&#160;</td>
                </tr>
                <xsl:apply-templates select="negeso:mail-template"/>
            </table>
            </form>
</xsl:template>

<!-- ********************************** Subscriber *********************************** -->
<xsl:template match="negeso:mail-template">
    <tr>
        <!-- <td class="admLightTD"><xsl:value-of select="@is-i18n"/></td> -->
        <td class="admMainTD">
            <a class="admAnchor" href="#" onClick = "return editTemplate({@id})">
                <xsl:value-of select="@title"/>
            </a>
        </td>
        <td class="admLightTD admWidth30">
            <img src="/images/edit.gif" class="admHand" onClick="return editTemplate({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
            </img>
        </td>
        <td class="admDarkTD admWidth30">
            <img src="/images/delete.gif" class="admHand" onClick="return deleteTemplate({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
         </img>
        </td>
    </tr>
</xsl:template>

<!-- ********************************** Path *********************************** -->
<xsl:template match="negeso:path">
    <xsl:apply-templates select="negeso:path-element"/>
</xsl:template>
    
<xsl:template match="negeso:path-element">
    <xsl:choose>
        <xsl:when test="@active='true'">
            <!-- Active pathe element - just print it-->
            <span class="admSecurity admLocation"><xsl:value-of select="@title"/></span>
        </xsl:when>
        <xsl:otherwise>
            <!-- Unactive pathe element - make it link-->
            <span class="admZero admLocation">
            <a class="admLocation" href="{@link}">
                <xsl:value-of select="@title"/>
            </a>
            &#160;&gt;
            </span>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>
