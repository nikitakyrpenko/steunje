<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${module_info.xsl}        
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  
 
  @version      2004.03.04
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<!-- Include/Import -->
<xsl:include href="/xsl/negeso_header.xsl"/>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
    <head>
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
        <title><xsl:value-of select="/negeso:page/@title"/></title>

        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>

        <xsl:call-template name="java-script"/>
    </head>

    <body>
        <div align="center">
            <table width="100%" cellspacing="0" cellpadding="0" border="0">
                <tr><td>
                    <!-- NEGESO HEADER -->
                    <xsl:call-template name="NegesoHeader"/>
                </td></tr>
                <tr>
                    <!-- CONTENT -->
                    <td class="content" colspan="2">
                        <xsl:apply-templates/>
                    </td>
                </tr>
            </table>
        </div>  
    </body>
</html>
</xsl:template>

<!-- NEGESO JAVASCRIPT Temaplate -->    
<xsl:template name="java-script">
    <script language="JavaScript">
    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        function goBack() {
            document.backForm.command.value="list-directory-command";
            document.backForm.submit();
        }
        ]]>
        </xsl:text>
    </script>
</xsl:template>

<!-- NEGESO PAGE Temaplate -->  
<xsl:template match="negeso:page">

<div align="center">
    <font color="#000000">
    <br/><xsl:value-of select="@title"/></font>
    <br/>
    <br/>
    
    <table class="decor" border="0" cellspacing="1" cellpadding="1" align="center" width="60%">
        <td align="left">
            <input value="Back" type="button" class="but" onClick="window.close();"/>
        </td>
        <td align="right">
            <input value="Close" type="button" class="but" onClick="window.close();"/>
        </td>
        
    <!-- NEGESO HEADER -->
        <xsl:apply-templates select="negeso:module"/>
        <xsl:apply-templates select="negeso:repository"/>
    </table>
</div>


</xsl:template>

<!-- ******************* NEGESO REPOSITORY *************************** -->
<xsl:template match="negeso:repository">
    <tr bgcolor="#e2f1e4">
        <td class="propName" align="right"><b>Total space for usage: </b></td> 
        <td class="propVal"><xsl:value-of select="@total-space"/></td>
    </tr>
    <tr bgcolor="#e2f1e4">
        <td class="propName" align="right"><b>Maximum file size allowed: </b></td>
        <td class="propVal"><xsl:value-of select="@max-file-size"/></td>
    </tr>
    <tr bgcolor="#e2f1e4">
        <td class="propName" align="right"><b>Free space: </b></td>
        <td class="propVal"><xsl:value-of select="@free-space"/></td>
    </tr>
    <tr bgcolor="#e2f1e4">
        <td class="propName" align="right"><b>Used space: </b></td>
        <td class="propVal"><xsl:value-of select="@used-space"/></td>
    </tr>
</xsl:template>

<!-- ******************* NEGESO REPOSITORY *************************** -->
    <xsl:template match="negeso:module">
    <tr bgcolor="#e2f1e4">
        <td class="propName" align="right"><b>Is active: </b></td> 
        <td class="propVal"><xsl:value-of select="@active"/></td>
    </tr>
    <tr bgcolor="#e2f1e4">
        <td class="propName" align="right"><b>Go live date: </b></td> 
        <td class="propVal"><xsl:value-of select="@golive"/></td>
    </tr>
</xsl:template>

</xsl:stylesheet>
