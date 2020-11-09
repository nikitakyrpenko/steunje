<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${choose_resource.xsl}        
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @version      2004.06.01
  @author       Sergiy.Oliynyk
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_news_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_news_module.xsl', $lang)"/>

<xsl:output method="html"/>
<xsl:template match="/" >
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_news_module, 'SELECT_LANGUAGES')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>

    <script type="text/javascript" src="/script/jquery.min.js"></script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js" />
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>    
    <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script> 
    <script language="JavaScript">
        <xsl:text disable-output-escaping="yes">
        <![CDATA[
            function GetSelectedItems() {
                var items = "";
                for (el in setLanguagesForm.elements.tags("INPUT")) {
                    if (setLanguagesForm.elements[el].type == "checkbox" &&
                        setLanguagesForm.elements[el].checked) {
                        items = items
                        	+ setLanguagesForm.elements[el].category_id
                        	+ ","
                        	+ setLanguagesForm.elements[el].value
                        	+ ';';
                    }
                }
                return items;
            }
            
            function GetSelectedLanguages() {
                var items = "";
                var first = true;
                for (el in setLanguagesForm.elements.tags("INPUT")) {
                    if (setLanguagesForm.elements[el].type == "checkbox" &&
                        setLanguagesForm.elements[el].checked)
                    {
                        if (!first) items = items + ", ";
                        first = false;
                        items = items + setLanguagesForm.elements[el].language;
                    }
                }
                return items;
            }

function CloseForm() { 
window.close(); 

            }

            function SetLanguages() {
                var result = new Array();
                result[0] = GetSelectedItems();
                result[1] = GetSelectedLanguages();
                window.returnValue = result;
                window.close();
            }

            attachEvent ("onload", resizeDialogWindow); //resize dialog window height
        ]]>
        </xsl:text>
    </script>

    <link rel="stylesheet" href="/css/admin_style.css" type="text/css"/>
    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        <script type="text/javascript" src="/script/conf.js"></script>
        ]]>
    </xsl:text>
</head>
<body class="dialogSmall">
    <xsl:call-template name="NegesoBody">
        <xsl:with-param name="showHelp" select="'no'"/>        
        <xsl:with-param name="backLink" select="'false'"/>
        <xsl:with-param name="close" select="'false'"/>
    </xsl:call-template>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:languages" mode="admContent">
    <form name="setLanguagesForm">
    <table  border="0" cellpadding="0" cellspacing="0" width="100%"  class="admTable">
        <xsl:apply-templates select="negeso:lang"/>
        <tr>
            <th class="admTableTDLast" colspan="2">
                <div class="admNavPanelInp">
                    <div class="imgL"></div>
                    <div align="center">
                        <input class="admNavbarInp" type="submit" name="submitBtn" onclick="SetLanguages()">
                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'OK')"/></xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
                <div class="admNavPanelInp"     style="padding-left:20px;">
                    <div class="imgL"></div>
                    <div align="center">
                        <input type="button" value="Cancel" onClick="CloseForm()"/>
                    </div>
                    <div class="imgR"></div>
                </div>
            </th>
        </tr>
        <tr>
            <td class="admTableFooter" colspan="2">&#160;</td>
        </tr>
    </table>     
    </form>
</xsl:template>

<xsl:template match="negeso:lang">
    <tr>
        <th class="admTableTD">
            <xsl:value-of select="@category_name"/>
        </th>
        <td class="admTableTDLast">
            <input type="checkbox" name="checkbox_{@category_name}_{@id}" value="{@id}" language="{@name}" category_id="{@category_id}">
                <xsl:choose>
                    <xsl:when test="@default">
                        <xsl:attribute name="checked">true</xsl:attribute>
                        <xsl:attribute name="disabled">true</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="@selected">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:when>
                </xsl:choose>
            </input>
            <xsl:choose>
                <xsl:when test="@default">
                    <span class="admBlue"><xsl:value-of select="@name"/>&#160;(<xsl:value-of select="java:getString($dict_common, 'DEFAULT')"/>)</span>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@name"/>
                </xsl:otherwise>
            </xsl:choose>
        </td>
    </tr>
</xsl:template>

</xsl:stylesheet>
