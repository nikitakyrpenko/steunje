<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$        
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Select page template form. Shows list of templates with default selected
 
  @author        Stanislav Demchenko
  @version        $Revision$
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_page_template" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_page_template.xsl', $lang)"/>

<xsl:include href="/xsl/negeso_body.xsl"/>

<!-- MAIN ENTRY -->
<xsl:template match="/">
    <html>
    <head>
        <title>
            <xsl:value-of select="java:getString($dict_page_template, 'SPT.SELECT_TEMPLATE')"/>
        </title>
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" src="/script/jquery.min.js"/>
        <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script language="JavaScript" src="/script/common_functions.js"/>
        <script language="JavaScript" src="/script/calendar_picker.js"/>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>        
        
        <script language="JavaScript">
        <xsl:text disable-output-escaping="yes">
            <![CDATA[            

            function onSubmit() {
                window.returnValue = new Object();
                window.returnValue.resCode = "OK";
                window.returnValue.templateId = document.operateForm.template.value;
                window.returnValue.templateTitle = getSelectTextByValue(
                    document.operateForm.template,
                    document.operateForm.template.value
                );
                window.opener.setTemplate(window.returnValue);
                window.close();
            }
        
            function getSelectTextByValue(selectObject, value){        
                if ( selectObject != null ) {
                    for ( i = 0; i < selectObject.options.length; i++ ){
                        if ( selectObject.options[i].value == value ){
                            return selectObject.options[i].text;
                        }
                    }
                }
                return null;
            }
            attachEvent ("onload", resizeDialogWindow); //resize dialog window height
            ]]>
            </xsl:text>
        </script>
    </head>
    <body class="dialogSmall">        
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink">
                <xsl:text>/admin/help/cnw1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
            </xsl:with-param>
            <xsl:with-param name="backLink" select="'false'"/>
        </xsl:call-template>        
        
    </body>
    </html>
</xsl:template>


<xsl:template match="/"  mode="admContent">    
    <form
        name="operateForm"
        method="post"
        action=""
        enctype="multipart/form-data"        
        >        
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td align="center" class="admNavPanelFont" colspan="2">
                    <xsl:value-of select="java:getString($dict_page_template, 'SPT.SELECT_TEMPLATE')"/>
                </td>
            </tr>
            <!-- Path & Error/Status messages -->
            <xsl:if test="count(@status-message)">
                <tr>
                    <td align="center" class="admTableTDLast admStatusMessage" colspan="2">
                        <xsl:value-of select="@status-message"/>
                    </td>
                </tr>
            </xsl:if>
            <xsl:if test="count(@error-message)">
                <tr>
                    <td align="center" class="admTableTDLast admErrorMessage" colspan="2">                        
                        <xsl:value-of select="@status-message"/>                        
                    </td>
                </tr>
            </xsl:if>
            <tr>                
                <td class="admTableTDLast admRight" width="170">
                    <xsl:value-of select="java:getString($dict_page_template, 'SPT.TEMPLATE')"/>:
                </td>
                <td class="admTableTDLast">
                    <select id="template" class="admWidth335 admTextArea" >
                        <xsl:for-each select="//negeso:template">
                            <option>
                                <xsl:attribute name="value"><xsl:value-of select='@name'/></xsl:attribute>
                                <xsl:value-of select='@title'/>
                            </option>
                        </xsl:for-each>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="admTableTDLast"></td>
                <td class="admTableTDLast">
                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div>
                            <input class="admNavPanelInp" type="submit" id="submitBtnId" name="submitBtn" onClick="onSubmit()">
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SUBMIT')"/></xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>               
                </td>                
            </tr>
            <tr>
                <td class="admTableFooter" colspan="2">&#160;</td>
            </tr>
        </table>
    </form>
</xsl:template>
    
</xsl:stylesheet>
