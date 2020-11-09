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
        var s_DeleteSubscribeCategoryConfirmation = "<xsl:value-of select="java:getString($dict_newsletter, 'DELETE_SUBSCRIBE_CATEGORY_CONFIRMATION')"/>";
        var s_DeleteSubscriptionConfirmation = "<xsl:value-of select="java:getString($dict_newsletter, 'DELETE_SUBSCRIPTION_CONFIRMATION')"/>";
        var s_DeletePublicationConfirmation = "<xsl:value-of select="java:getString($dict_newsletter, 'DELETE_PUBLICATION_CONFIRMATION')"/>";

    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        function Add() {
            document.operateForm.command.value = "nl-get-edit-category-page";
            document.operateForm.updateTypeField.value = "insert";
        }

        function Edit(targetId) {
            document.operateForm.command.value = "nl-get-edit-category-page";
            document.operateForm.nlTargetId.value = targetId;
            document.operateForm.submit();
        }

        function Delete(targetId) {
            if (confirm(s_DeleteSubscribeCategoryConfirmation)) {
                document.operateForm.command.value = "nl-delete-category";
                document.operateForm.nlTargetId.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }


        function MoveUp(targetId) {
            document.operateForm.command.value = "nl-reorder-category";
            document.operateForm.nlTargetId.value = targetId;
            document.operateForm.type.value = "up";
            document.operateForm.submit();
        }

        function MoveDown(targetId) {
         document.operateForm.command.value = "nl-reorder-category";
            document.operateForm.nlTargetId.value = targetId;
            document.operateForm.type.value = "down";
            document.operateForm.submit();
        }
        
        
        // PUBLICATION MANAGEMENT
        function AddPublication() {
            document.operateForm.command.value = "nl-get-edit-publication-page";
            document.operateForm.updateTypeField.value = "insert";
        }

        function editPublication(targetId) {
            document.operateForm.command.value = "nl-get-edit-publication-page";
            document.operateForm.nlTargetId.value = targetId;
            document.operateForm.submit();
        }


        function deletePublication(targetId) {
            if (confirm(s_DeletePublicationConfirmation)) {
                document.operateForm.command.value = "nl-delete-publication";
                document.operateForm.nlTargetId.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }
        
        function deleteSubscription(targetId) {
            if (confirm(s_DeleteSubscriptionConfirmation)) {
                document.operateForm.command.value = "nl-delete-subscription";
                document.operateForm.nlTargetId.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }

        function onChangeFilterBy(){
            document.operateForm.submit();
        }

        ]]>
        </xsl:text>
    </script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_newsletter, 'BROWSE_SUBSCRIPTION_CATEGORIES')"/></title>
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
    <!-- NEWSLETTER LINKS 
    <xsl:call-template name = "nl_commons.navigation_links"/>
    -->
   
    <!-- Path -->
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
        <tr>
            <td class="admBold">
                <xsl:apply-templates select="negeso:path"/>
            </td>
        </tr>
    </table>
    <!-- TITLE -->
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
            <xsl:choose>
                <xsl:when test="negeso:newsletter-category/@is-leaf='true'">
                    <xsl:value-of select="java:getString($dict_newsletter, 'BROWSE_SUBSCRIPTION_CATEGORIES')"/>. 
                    <xsl:value-of select="negeso:newsletter-category/@title"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="java:getString($dict_newsletter, 'BROWSE_SUBSCRIPTION_CATEGORIES')"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:with-param>
    </xsl:call-template>
    <!-- Content -->
    <xsl:choose>
        <xsl:when test="negeso:newsletter-category/@is-leaf='true'">
            <xsl:choose>
                <xsl:when test="negeso:newsletter-category/@filter-by='subscriber'">
                    <xsl:apply-templates select="negeso:newsletter-category" mode="subscriber"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="negeso:newsletter-category" mode="publication"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="negeso:newsletter-category" mode="category"/>
        </xsl:otherwise>
    </xsl:choose>
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

<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:newsletter-category" mode="category">
    <xsl:choose>
        <xsl:when test="@position='current'">
            <!-- Render HEADER -->
            <form method="POST" name="operateForm" action="">
            <input type="hidden" name="command" value="nl-browse-category"></input>
            <input type="hidden" name="type" value="none"></input>
            <input type="hidden" name="nlTargetId" value="-1"></input>
            <input type="hidden" name="nlCatId" value="{@id}"></input>
            <input type="hidden" name="updateTypeField" value="update"></input>
            <table cellpadding="0" cellspacing="0" class="admNavPanel">
                <tr>
                    <td class="admNavbar admCenter">
                        <input class="admNavbarInp" type="submit" onClick = "Add()">
                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_newsletter, 'ADD_SUBSCRIBE_CATEGORY')"/>&#160;&gt;</xsl:attribute>
                        </input>
                   </td>
                </tr>
            </table>
            <table class="admNavPanel" cellspacing="0" cellpadding="0">
                <tr>
                    <td class="admNamebar admOrder"><xsl:value-of select="java:getString($dict_common, 'ORDER')"/></td>
                    <td class="admNamebar">&#160;</td>
                    <td class="admNamebar"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_newsletter, 'MULTILINGUAL')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/></td>
                    <td class="admNamebar" colspan="4">&#160;</td>
                </tr>
                <xsl:apply-templates select="negeso:newsletter-category" mode="category"/>
            </table>
            </form>
        </xsl:when>
        <xsl:otherwise>
            <tr>
                <td class="admLightTD" colspan="2"><xsl:value-of select="@order-number"/></td>
                <td class="admMainTD">
                    <a class="admAnchor" href="?command=nl-browse-category&amp;nlCatId={@id}">
                        <xsl:attribute name="href">?command=nl-browse-category&amp;nlCatId=<xsl:value-of select="@id"/></xsl:attribute>
                        <xsl:value-of select="@title" disable-output-escaping="yes"/>
                    </a>
                </td>
                <td class="admDarkTD"><xsl:value-of select="@multilingual"/></td>
                <td class="admLightTD"><xsl:value-of select="@publish-date"/></td>
                <td class="admDarkTD"><xsl:value-of select="@expired-date"/></td>
                <td class="admLightTD admWidth30">
                    <img src="/images/edit.gif" class="admHand" onClick="Edit({@id})">
                    <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
               </img>
                </td>
                <td class="admDarkTD admWidth30">
                    <img src="/images/up.gif" class="admHand" onClick="MoveUp({@id})">
                  <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'UP')"/></xsl:attribute>
              </img>
                </td>
                <td class="admLightTD admWidth30">
                    <img src="/images/down.gif" class="admHand" onClick="MoveDown({@id})">
                  <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DOWN')"/></xsl:attribute>
              </img>
                </td>
                <td class="admDarkTD admWidth30">
                    <img src="/images/delete.gif" class="admHand" onClick="return Delete({@id})">
                  <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
              </img>
                </td>
            </tr>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:newsletter-category" mode="publication">
    <xsl:choose>
        <xsl:when test="@position='current'">
            <!-- Render HEADER -->
            <form method="POST" name="operateForm" action="">
            <input type="hidden" name="command" value="nl-browse-category"></input>
            <input type="hidden" name="type" value="none"></input>
            <input type="hidden" name="nlTargetId" value="-1"></input>
            <input type="hidden" name="nlCatId" value="{@id}"></input>
            <input type="hidden" name="updateTypeField" value="update"></input>
            <table cellpadding="0" cellspacing="0" class="admNavPanel">
                <tr>
                    <td class="admNavbar admCenter">
                        <table width="100%">
                        <tr><td width="80%" align="center">
                            <input class="admNavbarInp adm" type="submit" onClick = "return AddPublication()">
                            <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_newsletter, 'ADD_NEW_PUBLICATION')"/>&#160;&gt;</xsl:attribute>
                            </input>
                        </td>
                        <td width="80%">
                            <select align="right" name="filterByField" class="admWidth175" 
                                onChange="onChangeFilterBy()"
                            > 
                                <option value="publication"><xsl:value-of select="java:getString($dict_newsletter, 'SHOW_PUBLICATIONS')"/>
                                    <xsl:if test="@filter-by='publication'">
                                        <xsl:attribute name="selected">true</xsl:attribute>
                                    </xsl:if>
                                </option>
                                <option value="subscriber"><xsl:value-of select="java:getString($dict_newsletter, 'SHOW_SUBSCRIBERS')"/>
                                    <xsl:if test="@filter-by='subscriber'">
                                        <xsl:attribute name="selected">true</xsl:attribute>
                                    </xsl:if>
                                </option>
                            </select>
                        </td></tr>
                        </table>
                   </td>
                </tr>
            </table>
            <table class="admNavPanel" cellspacing="0" cellpadding="0">
                <tr>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_newsletter, 'STATUS')"/></td>
                    <td class="admNamebar"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_common, 'LANGUAGE')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/></td>
                    <td class="admNamebar" colspan="2">&#160;</td>
                </tr>
                <xsl:apply-templates select="negeso:newsletter-publications"/>
            </table>
            </form>
        </xsl:when>
        <xsl:otherwise>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template match="negeso:newsletter-publications">
    <xsl:apply-templates select="negeso:newsletter-publication"/>
</xsl:template>


<xsl:template match="negeso:newsletter-publication">
    <tr>
        <td class="admLightTD"><xsl:value-of select="@status"/></td>
        <td class="admMainTD">
            <a class="admAnchor" href="#" onClick = "return editPublication({@id})">
                <xsl:value-of select="@title"/>
            </a>
        </td>
        <td class="admLightTD">
            <xsl:if test="@is-i18n-complete = 'false'">
                <xsl:attribute name="style">color: #FF2B04;</xsl:attribute>
            </xsl:if>                
            <xsl:choose>
                <xsl:when test="../../@is-i18n='true'">
                    <xsl:value-of select="@lang-code"/>
                </xsl:when>
                <xsl:otherwise>
                    -
                </xsl:otherwise>
            </xsl:choose>
        </td>
        <td class="admDarkTD"><xsl:value-of select="@publish-date"/></td>
        <td class="admLightTD admWidth30">
            <img src="/images/edit.gif" class="admHand" onClick="return editPublication({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
            </img>
        </td>
        <td class="admDarkTD admWidth30">
            <img src="/images/delete.gif" class="admHand" onClick="return deletePublication({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
         </img>
        </td>
    </tr>
</xsl:template>


<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:newsletter-category" mode="subscriber">
    <xsl:choose>
        <xsl:when test="@position='current'">
            <!-- Render HEADER -->
            <form method="POST" name="operateForm" action="">
            <input type="hidden" name="command" value="nl-browse-category"></input>
            <input type="hidden" name="type" value="none"></input>
            <input type="hidden" name="nlTargetId" value="-1"></input>
            <input type="hidden" name="nlCatId" value="{@id}"></input>
            <input type="hidden" name="updateTypeField" value="update"></input>
            <table cellpadding="0" cellspacing="0" class="admNavPanel">
                <tr>
                    <td class="admNavbar admCenter">
                        <table width="100%">
                        <tr><td width="80%" align="center">
                            <input class="admNavbarInp" type="submit" onClick = "return AddPublication()">
                            <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_newsletter, 'ADD_NEW_PUBLICATION')"/>&#160;&gt;</xsl:attribute>
                            </input>
                        </td>
                        <td width="80%">
                            <select align="right" name="filterByField" class="admWidth175" 
                                onChange="onChangeFilterBy()"
                            > 
                                <option value="publication"><xsl:value-of select="java:getString($dict_newsletter, 'SHOW_PUBLICATIONS')"/>
                                    <xsl:if test="@filter-by='publication'">
                                        <xsl:attribute name="selected">true</xsl:attribute>
                                    </xsl:if>
                                </option>
                                <option value="subscriber" selected="true"><xsl:value-of select="java:getString($dict_newsletter, 'SHOW_SUBSCRIBERS')"/>
                                    <xsl:if test="@filter-by='subscriber'">
                                        <xsl:attribute name="selected">true</xsl:attribute>
                                    </xsl:if>
                                </option>
                            </select>
                        </td></tr>
                        </table>
                   </td>
                </tr>
            </table>
            <table class="admNavPanel" cellspacing="0" cellpadding="0">
                <tr>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_common, 'NAME')"/></td>
                    <td class="admNamebar"><xsl:value-of select="java:getString($dict_common, 'EMAIL')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_common, 'LANGUAGE')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_common, 'TYPE')"/></td>
                    <td class="admNamebar" colspan="1">&#160;</td>
                </tr>
                <xsl:apply-templates select="negeso:newsletter-subscribers"/>
            </table>
            </form>
        </xsl:when>
        <xsl:otherwise>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template match="negeso:newsletter-subscribers">
    <xsl:for-each select="negeso:newsletter-subscriber">
        <tr>
            <td class="admLightTD"><xsl:value-of select="@fname"/>&#160;<xsl:value-of select="@sname"/>
            </td>
            <td class="admMainTD">
                <a class="admAnchor" href="mailto:{@email}">
                    <xsl:attribute name="title">MailTo<!--<xsl:value-of select="java:getString($dict_common, 'DELETE')"/>--></xsl:attribute>
                    <xsl:value-of select="@email"/>
                </a>
            </td>
            <td class="admLightTD"><xsl:value-of select="@lang"/></td>
            <td class="admDarkTD"><xsl:value-of select="@type"/></td>
            <td class="admLightTD admWidth30">
                <img src="/images/delete.gif" class="admHand" onClick="return deleteSubscription({@subscription-id})">
                    <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_newsletter, 'UNSUBSCRIBE')"/></xsl:attribute>
             </img>
            </td>
        </tr>
    </xsl:for-each>
</xsl:template>

<!-- ********************************** Path *********************************** -->
<xsl:template match="negeso:path">
    <xsl:for-each select="negeso:path-element">
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
    </xsl:for-each>
</xsl:template>


<xsl:template name="nl_commons.navigation_links">
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
        <tr>
            <td class="admLeft">
                <a href="?command=nl-browse-category"><xsl:value-of select="java:getString($dict_newsletter, 'BROWSE_SUBSCRIPTION_CATEGORIES')"/></a>&#160;&#160;&#160;
                <a href="?command=nl-browse-schedule">Schedule<!--<xsl:value-of select="java:getString($dict_newsletter, 'SCHEDULE')"/>--></a>&#160;&#160;&#160;
                <a href="?command=nl-browse-request"><xsl:value-of select="java:getString($dict_newsletter, 'SUBSCRIPTION_REQUESTS')"/></a>&#160;&#160;&#160;
                <a href="?command=nl-browse-subscriber"><xsl:value-of select="java:getString($dict_newsletter, 'SUBSCRIBERS')"/></a>&#160;&#160;&#160;
                <a href="?command=nl-browse-template"><xsl:value-of select="java:getString($dict_newsletter, 'MAIL_TEMPLATES')"/></a>
            </td>
        </tr>
    </table>
</xsl:template>
    
</xsl:stylesheet>
