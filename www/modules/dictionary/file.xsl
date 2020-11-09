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

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>

<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:output method="html"/>
<xsl:template match="/" >
<html>
<head>
    <title>Dictionary file properties</title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script language="JavaScript">
        function ReturnParameters() {
            var result = new Array();
            result[0] = setParametersForm.fileName.value;
            result[1] = setParametersForm.description.value;
            window.returnValue = result;
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
                Dictionary file properties
            </xsl:with-param>
        </xsl:call-template>
        <!-- CONTENT -->
        <xsl:apply-templates select="negeso:dictionaryFile"/>
        <xsl:call-template name="NavBar"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:dictionaryFile">
    <form name="setParametersForm" method="POST" action="?command">
        <input type="hidden" name="dictionaryFileId" value="{@id}"/>

        <table cellpadding="0" cellspacing="0">
            <tr>
                <td valign="top">
                    <table cellpadding="0" cellspacing="0" class="admNavPanel">
                    <tr>
                        <td class="admMainTD" style="width: 200;">File path<br/><small>e.g. dict_new_module.xsl</small></td>
                        <td class="admLightTD"><input class="admTextArea" style="width:330" type="text" name="fileName" value="{@fileName}"/></td>
                    </tr>
                    <tr>
                        <td class="admMainTD">Description</td>
                        <td class="admLightTD"><input class="admTextArea" style="width:330" type="text" name="description" value="{@description}"/></td>
                    </tr>
                    </table>        
                </td>
            </tr>
        </table>
         <table cellpadding="0" cellspacing="0"  class="admNavPanel">
                <tr>
                    <td class="admNavPanel admNavbar admCenter">
                         <button class="admNavbarInp admAction" onclick="ReturnParameters()">
                            &lt;&#160;Save&#160;&gt;
                         </button>
                        <input value="&lt;&#160;Reset&#160;&gt;" type="reset" class="admNavbarInp"/>
                    </td>
                </tr>
            </table>
    </form>
</xsl:template>

</xsl:stylesheet>
