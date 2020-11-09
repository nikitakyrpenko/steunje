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

<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang"><xsl:value-of select="/*/@interface-language"/></xsl:variable>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_categories" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_categories.xsl', $lang)"/>

<xsl:template match="/">
<html>
<head>
	<title><xsl:value-of select="java:getString($dict_categories, 'SELECT_CATEGORY')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="/script/jquery.min.js"></script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>
</head>
<body>
    <xsl:call-template name="NegesoBody">
        <xsl:with-param name="helpLink" select="'?command=get-categories-command&amp;moduleName=web_poll'"/>
        <xsl:with-param name="backLink" select="''"/>
    </xsl:call-template>
</body>
</html>
</xsl:template>

<!-- ************************************* Categories *********************************** -->
<xsl:template match="/negeso:categories" mode="admContent">
    <table cellspacing="0" cellpadding="0" border="0" align="center" width="100%" class="admTable">        
        <tr>
            <td class="admNavPanelFont" align="center">                
                <xsl:value-of select="java:getString($dict_categories, 'SELECT_CATEGORY_OF_MODULE')"/>&#160;<xsl:value-of select="@module"/>                
            </td>
        </tr>
        <tr>
            <td class="admTableFooter" >
                <form method="POST" name="categoriesManageForm">
                    <table cellpadding="0" cellspacing="0" width="100%">
                        <tr>
                            <td class="admTDtitles">
                                <xsl:value-of select="java:getString($dict_categories, 'CATEGORY_NAME')"/>
                            </td>
                            <td class="admTDtitles" width="140px">
                                <xsl:value-of select="java:getString($dict_common, 'LANGUAGE')"/>
                            </td>
                        </tr>
                        <xsl:apply-templates select="negeso:category"/>
                    </table>
                </form>
            </td>
        </tr>
        <tr>
            <td class="admTableFooter" >&#160;</td>
        </tr>
    </table>
</xsl:template>

<!-- ************************************* Category ******************************* -->
<xsl:template match="negeso:category" >
    <tr>
        <th class="admTableTD">
            <a class="admAnchor">
                <xsl:attribute name="href">
                    <xsl:text>?command=get-list-command</xsl:text>&amp;listId=<xsl:value-of select="@id"/>&amp;listPath=0<xsl:if test="/negeso:categories/@module = 'Photo album'">;0</xsl:if>
                </xsl:attribute>
                <xsl:value-of select="@name" disable-output-escaping="yes"/>
            </a>
        </th>
        <th class="admTableTDLast"><xsl:value-of select="@language"/></th>
    </tr>
</xsl:template>

</xsl:stylesheet>
