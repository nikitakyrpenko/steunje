<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a list item edit interface.
 
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
        var s_DeleteRequestConfirmation = "<xsl:value-of select="java:getString($dict_newsletter, 'DELETE_REQUEST_CONFIRMATION')"/>";

    <xsl:text disable-output-escaping="yes">
        <![CDATA[

        function deleteRequest(targetId) {
            if (confirm(s_DeleteRequestConfirmation)) {
                document.operateForm.command.value = "nl-delete-request";
                document.operateForm.nlRequestId.value = targetId;
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
    <title><xsl:value-of select="java:getString($dict_newsletter, 'BROWSE_SUBSCRIPTION_REQUESTS')"/></title>
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
            <xsl:text>/admin/help/cnl_</xsl:text>
            <xsl:value-of select="$lang"/>
            <xsl:text>.html</xsl:text>
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
            <xsl:value-of select="java:getString($dict_newsletter, 'BROWSE_SUBSCRIPTION_REQUESTS')"/>
        </xsl:with-param>
    </xsl:call-template>
    <!-- Content -->
    <xsl:apply-templates select="negeso:newsletter-requests"/>
    <!-- NavBar -->
    <xsl:choose>
        <xsl:when test="count(descendant::negeso:page) = 0">
            <xsl:choose>
                <xsl:when test="negeso:newsletter-category/@parent-id='none'">
                    <xsl:call-template name="NavBar">
                        <xsl:with-param name="helpLink">
                            <xsl:text>/admin/help/cnl_</xsl:text>
                            <xsl:value-of select="$lang"/>
                            <xsl:text>.html</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="NavBar">
                        <xsl:with-param name="helpLink">
                            <xsl:text>/admin/help/cnl_</xsl:text>
                            <xsl:value-of select="$lang"/>
                            <xsl:text>.html</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="backLink" select="concat('?command=nl-browse-category&amp;nlCatId=', negeso:newsletter-category/@parent-id)"/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="NavBar">
                <xsl:with-param name="helpLink">
                    <xsl:text>/admin/help/cnl_</xsl:text>
                    <xsl:value-of select="$lang"/>
                    <xsl:text>.html</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:otherwise>
   </xsl:choose>

</xsl:template>

<xsl:template match="negeso:newsletter-requests">
            <!-- Render HEADER -->
            <form method="POST" name="operateForm" action="">
            <input type="hidden" name="command" value="nl-browse-request"></input>
            <input type="hidden" name="nlRequestId" value="-1"></input>
            <table cellpadding="0" cellspacing="0" class="admNavPanel">
                <tr>
                    <td class="admNavbar admCenter">
                   </td>
                </tr>
            </table>
            <table class="admNavPanel" cellspacing="0" cellpadding="0">
                <tr>
                    <td class="admNamebar admOrder"><xsl:value-of select="java:getString($dict_newsletter, 'REQUEST_ID')"/></td>
                    <td class="admNamebar"><xsl:value-of select="java:getString($dict_newsletter, 'STATUS')"/></td>
                    <td class="admNamebar"><xsl:value-of select="java:getString($dict_common, 'EMAIL')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_common, 'TYPE')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_common, 'LANGUAGE')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_newsletter, 'SUBSCRIPTION_DATE')"/></td>
                    <td class="admNamebar" colspan="1">&#160;</td>
                </tr>
                <xsl:apply-templates select="negeso:newsletter-request"/>
            </table>
            </form>
</xsl:template>

<!-- ********************************** REQUEST *********************************** -->
<xsl:template match="negeso:newsletter-request">
    <tr>
        <td class="admLightTD"><xsl:value-of select="@id"/></td>
        <td class="admDarkTD"><xsl:value-of select="@status"/></td>
        <td class="admMainTD">
            <a class="admAnchor" href="mailto:{@email}">
                <xsl:attribute name="title">MailTo<!--<xsl:value-of select="java:getString($dict_common, 'DELETE')"/>--></xsl:attribute>
                <xsl:value-of select="@email"/>
            </a>
            &#160;&#160;&#160;<xsl:value-of select="@fname"/>&#160;<xsl:value-of select="@sname"/>
        </td>
        <td class="admLightTD"><xsl:value-of select="@type"/></td>
        <td class="admDarkTD"><xsl:value-of select="@lang-code"/></td>
        <td class="admLightTD"><xsl:value-of select="@subscription-date"/></td>
        <td class="admDarkTD admWidth30">
            <img src="/images/delete.gif" class="admHand" onClick="return deleteRequest({@id})">
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
