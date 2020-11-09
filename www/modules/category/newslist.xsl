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
    <xsl:variable name="dict_modules" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_modules.xsl', $lang)"/>

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
    <script type="text/javascript" src="/script/common_functions.js"></script>
    <script type="text/javascript" src="/script/security.js"></script>    
    <script type="text/javascript" src="/script/AJAX_webservice.js"></script>
    <script language="JavaScript">
        <xsl:text disable-output-escaping="yes">
        <![CDATA[
        // Choose container: Begin
		var cont1;
		
		function ChangeContainer(container){
			cont1 = document.getElementById(container);
			NewSecurity.setContainer = setSecurity_responseOK;
			NewSecurity.selectContainerDialog(cont1.getAttribute("containerId"), "manager");
		}
		
		function setSecurity_responseOK() {
			AJAX_Send("set-list-container-command",
				{"listId"      : cont1.getAttribute("listId"),
				 "containerId" : NewSecurity.return_value.containerId},
				function() {setTimeout(setSecurity_AJAX_responseOK,1000)},
				null
			);		
		}
		
		function setSecurity_AJAX_responseOK() {
			window.location.reload();
		}
		// Choose container: End
        ]]>
        </xsl:text>
    </script>
</head>
<body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
    id="ClientManager" xmlID="{@id}">
    <xsl:call-template name="NegesoBody">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cnw1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>            
        <!--<xsl:with-param name="backLink" select="'?command=get-categories-command&amp;moduleName=news_module'"/>-->
        <xsl:with-param name="backLink" select="'false'"/>
    </xsl:call-template>
                <script>
                    Cufon.now();
                    Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
                    Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
                </script>
</body>
</html>
</xsl:template>
   
<!-- ************************************* Categories *********************************** -->
       
   <xsl:template match="/negeso:categories" mode="admContent" >
    
    <form method="POST" name="categoriesManageForm">
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">

            <tr>
                <td align="center" class="admNavPanelFont" colspan="4">
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:value-of select="java:getString($dict_categories, 'SELECT_CATEGORY_OF_MODULE')"/>&#160;<xsl:value-of select="java:getString($dict_modules, 'NEWS_MODULE')" disable-output-escaping="yes"/>                  
                    </xsl:with-param>
                    </xsl:call-template>
                </td>
                
            </tr>    
                    
            <tr>
				<td class="admTDtitles" colspan="3"><xsl:value-of select="java:getString($dict_categories, 'CATEGORY_NAME')"/></td>
				<td class="admTDtitles" width="140px"><xsl:value-of select="java:getString($dict_common, 'LANGUAGE')"/></td>
            </tr>
            <xsl:apply-templates select="negeso:category"/>
            <tr>
                <td class="admTableFooter" >&#160;</td>
            </tr>
        </table>
    </form>
</xsl:template>

<!-- ************************************* Category ******************************* -->
<xsl:template match="negeso:category">
    <tr>
        <th class="admTableTDLast">

            <a class="admAnchor">
                <xsl:attribute name="href">
                    <xsl:text>?command=get-list-command&amp;listId=</xsl:text><xsl:value-of select="@id"/>
                </xsl:attribute>
                <xsl:value-of select="@name" disable-output-escaping="yes"/>
            </a>
        </th>
        <th class="admTableTD">
            <a>
                <xsl:attribute name="href">
                    <xsl:text>?command=get-archive-command&amp;backLinkTo=module&amp;listId=</xsl:text><xsl:value-of select="@id"/>
                </xsl:attribute>
                <xsl:value-of select="java:getString($dict_categories, 'ARCHIVE')"/>
            </a>                           
        </th>
        <td class="admTableTD" align="center">
            <img class="admHand" id="container{@id}" listId="{@id}" containerId="{@containerId}">
                <xsl:attribute name="src">
                    <xsl:choose>
                        <xsl:when test="@canManage = 'true'">/images/lock.png</xsl:when>
                        <xsl:otherwise>/images/lock_gray.png</xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                <xsl:if test="@canManage = 'true'">
                    <xsl:attribute name="onClick">
                        <xsl:text>ChangeContainer('container</xsl:text><xsl:value-of select="@id"/><xsl:text>');</xsl:text>
                    </xsl:attribute>
                </xsl:if>
                <xsl:attribute name="title">
                    <xsl:value-of select="java:getString($dict_common, 'CHANGE_CONTAINER')"/>
                </xsl:attribute>
            </img>
        </td>
        <th class="admTableTDLast">
            <xsl:value-of select="@language"/>
        </th>
    </tr>           
</xsl:template>

</xsl:stylesheet>
