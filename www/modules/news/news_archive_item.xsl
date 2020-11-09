<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${edit_item.xsl}

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  @version      2003.04.24
  @author       Sergiy Oliynyk
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:output method="html"/>
<xsl:include href="/xsl/negeso_body.xsl"/>
<xsl:include href="/xsl/admin_templates.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_news_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_news_module.xsl', $lang)"/>

<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_news_module, 'ARCHIVED_NEWS')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" href="/css/admin_style.css" type="text/css"/>
    <link href="/site/core/css/default_styles.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    
    <xsl:call-template name="adminhead"/>
</head>
<body>
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink">
                <xsl:text>/admin/help/cnw1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>            
            </xsl:with-param>
            <xsl:with-param name="backLink" select="concat('?command=get-archive-command&amp;listItemId=', negeso:archivedListItem/@id, '&amp;listId=', negeso:archivedListItem/@listId)"/>
        </xsl:call-template>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:archivedListItem"  mode="admContent">
    <form name="form1" method="POST">
        <input type="hidden" name="command" value="get-archived-list-item-command"/>
        <input type="hidden" name="listItemId" value="{@id}"/>
        <input type="hidden" name="listId" value="{@listId}"/>        
        <table class="admTable" cellspacing="0" cellpadding="0">
            <tr>
                <td align="center" class="admNavPanelFont"  colspan="2">
                    <xsl:value-of select="java:getString($dict_news_module, 'ARCHIVED_NEWS')"/>
                </td>
            </tr>
            <!-- Title Field -->
            <tr>
                <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></th>
                <td class="admTableTDLast">
                    <input class="admTextArea" type="text" name="titleField" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@title"/></xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- Created date -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'CREATED_DATE')"/></th>
                <td class="admTableTDLast">
                    <input class="admTextArea" type="text" name="createdDateField" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@createdDate"/></xsl:attribute>
                    </input>
                    (yyyy-mm-dd)
                </td>
            </tr>
            <!-- Publish Date -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/></th>
                <td class="admTableTDLast">
                    <input class="admTextArea" type="text" name="publishDateField" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@publishDate"/></xsl:attribute>
                    </input>
                    (yyyy-mm-dd)
                </td>
            </tr>
            <!-- Expired Date -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/></th>
                <td class="admTableTDLast">
                    <input class="admTextArea" type="text" name="expiredDateField" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@expiredDate"/></xsl:attribute>
                    </input>
                    (yyyy-mm-dd)
                </td>
            </tr>
            <!-- Created by -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'CREATED_BY')"/></th>
                <td class="admTableTDLast">
                    <input class="admTextArea" type="text" name="createdByField" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@createdBy"/></xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- Last modified by -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'LAST_MODIFIED_BY')"/></th>
                <td class="admTableTDLast">
                    <input class="admTextArea" type="text" name="lastModifiedByField" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@lastModifiedBy"/></xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- Link to page -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_news_module, 'LINK_TO_PAGE')"/></th>
                <td class="admTableTDLast">
                    <input class="admTextArea" type="text" name="linktoPageField" readonly="true">
                        <xsl:if test="@link != ''">
                            <xsl:attribute name="value"><xsl:value-of select="@link"/></xsl:attribute>
                        </xsl:if>
                    </input>
                </td>
            </tr>
            <!-- Multilingual -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_news_module, 'LANGUAGES')"/></th>
                <td class="admTableTDLast">
                    <input class="admTextArea" type="text" name="itemsLink" readonly="true">
                         <xsl:attribute name="value">
                            <xsl:choose>
                                <xsl:when test="@linked != ''"><xsl:value-of select="java:getString($dict_news_module, 'LINKED_TO_ORIGINAL')"/>:&#160;<xsl:value-of select="@linked"/></xsl:when>
                                <xsl:when test="@mirrored != ''"><xsl:value-of select="@mirrored"/></xsl:when>
                                <xsl:otherwise><xsl:value-of select="java:getString($dict_news_module, 'NOT_PRESENT_IN_OTHER_LANGUAGES')"/></xsl:otherwise>
                            </xsl:choose>
                         </xsl:attribute>
                    </input>
                </td>
            </tr>
            <!-- Image -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'IMAGE')"/></th>
                <td class="admTableTDLast">
                    <xsl:if test="@imageLink != '' ">
                    	<xsl:if test="@imageLink">
	                        <img hspace="5" vspace="5">
	                            <xsl:attribute name="src">../<xsl:value-of select="@imageLink"/></xsl:attribute>
	                        </img>
	                      </xsl:if>
                        <br/>
                    </xsl:if>
                    <input class="admTextArea" type="text" name="imageLinkField" readonly="true">
                        <xsl:choose>
                            <xsl:when test="@imageLink">
                                <xsl:attribute name="value"><xsl:value-of select="@imageLink"/></xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_news_module, 'NO_IMAGE')"/></xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                    </input>
                </td>
            </tr>
            <!-- document -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_common, 'DOCUMENT')"/></th>
                <td class="admTableTDLast">
                    <xsl:choose>
                        <xsl:when test="@documentLink">
                            <div class="admLeft">
                                <a target="_blank">
                                    <xsl:attribute name="href">../<xsl:value-of select="@documentLink"/></xsl:attribute>
                                    <xsl:value-of select="@documentLink"/>
                                </a>
                            </div>
                        </xsl:when>
                        <xsl:otherwise>
                            <input type="text" name="documentField" readonly="true" class="admtextArea admWidth265">
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_news_module, 'NO_DOCUMENT')"/></xsl:attribute>
                            </input>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
            <!-- Summary article -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_news_module, 'SUMMARY_ARTICLE')"/><br/></th>
                <td class="admTableTDLast">
                    <div class="contentStyle" style="margin: 5px;border: 1px solid #848484;" readonly="true">
                        <xsl:value-of select="@teaserText" disable-output-escaping="yes"/>
                    </div>
                </td>
            </tr>
            <!-- Text of article -->
            <tr>
                <th class="admTableTD"><xsl:value-of select="java:getString($dict_news_module, 'TEXT_OF_ARTICLE')"/></th>
                <td class="admTableTDLast">
                    <div class="contentStyle" style="margin: 5px;border: 1px solid #848484;" readonly="true">
                        <xsl:value-of select="@articleText" disable-output-escaping="yes"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="admTableFooter" colspan="2" >&#160;</td>
            </tr>
        </table>        
    </form>
</xsl:template>

</xsl:stylesheet>
