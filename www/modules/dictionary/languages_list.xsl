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

<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>

<xsl:output method="html"/>
<xsl:template match="/" >
<html>
<head>
    <title>Dictionary download</title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script language="JavaScript">
        function returnValues() {
            var lang = "";
            for (i = 0; i &lt; mainForm.languages.length; i++) {
                if (mainForm.languages[i].checked)
                    lang += mainForm.languages[i].value + ';';
            }
            window.returnValue = lang;
            window.close();
        }
    </script>

    <link rel="stylesheet" href="/css/admin_style.css" type="text/css"/>
    <script type="text/javascript" src="/script/conf.js"></script>
</head>
<body class="menu">
        <xsl:call-template name="NegesoHeader"/>
        <div align="center">
               <xsl:call-template name="NavBar"/>
             <xsl:call-template name="tableTitle">
                <xsl:with-param name="headtext">
                    Select languages
                </xsl:with-param>
           </xsl:call-template>
            <!-- CONTENT -->
          <xsl:apply-templates select="negeso:languages"/>
               <xsl:call-template name="NavBar"/>
        </div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:languages">
    <form name="mainForm" method="GET" action="" enctype="multipart/form-data">
        <input type="hidden" name="command" value="download-dictionary-command"/>
        <input type="hidden" name="type" value="sql"/>

        <table cellpadding="0" cellspacing="0">
            <tr>
                <td valign="top">
                    <table class="admNavPanel" cellpadding="0" cellspacing="0">
                        <xsl:apply-templates select="negeso:language"/>
                    </table>
                </td>
            </tr>
        </table>
        <table cellpadding="0" cellspacing="0" class="admNavPanel">
            <tr>
                <td class="admNavPanel admNavbar admCenter">
                    <button class="admNavbarInp" onclick="returnValues()">
                        &lt;&#160;Download&#160;&gt;
                    </button>
                </td>
            </tr>
        </table>
    </form>
</xsl:template>

<xsl:template match="negeso:language">
    <tr>

        <td class="admMainTD admLeft">
            <input type="checkbox" name="languages" value="{@code}">
                <xsl:if test="@code = 'en'">
                    <xsl:attribute name="checked">true</xsl:attribute>
                </xsl:if>
            </input>    
            <xsl:value-of select="@name"/>
        </td>
    </tr>
</xsl:template>

</xsl:stylesheet>
