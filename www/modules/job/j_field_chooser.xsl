<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$
 
  Copyright (c) 2005 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  
 
  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<!-- Include/Import -->
<xsl:include href="/xsl/negeso_body.xsl"/>
<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_job_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_job_module.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->    
<xsl:template name="java-script">
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>
    
    <script language="JavaScript">
    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        function selectFieldType(id){
            window.name='UploadWindow';
            window.returnValue = new Object();
            window.returnValue.resCode = "OK";
            window.returnValue.typeId = id;
            window.close();
        }


        ]]>
        </xsl:text>
    </script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/negeso:page">
<html>
<head>
    <title>Field repository</title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <xsl:call-template name="java-script"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <!-- NEGESO HEADER -->
     <xsl:call-template name="NegesoBody"/>   
</body>
</html>
</xsl:template>

    <xsl:template match="negeso:page"  mode="admContent">
        <xsl:apply-templates select="negeso:field_type_list" mode="field_type_list"/>
    </xsl:template>
    <xsl:template match="negeso:field_type_list"  mode="field_type_list">
    
    
    <!-- Content -->
    	

<!-- ********************************** Category *********************************** -->

    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
            <td align="center" class="admNavPanelFont"  colspan="2">
                <!-- TITLE -->
                <xsl:call-template name="tableTitle">
                    <xsl:with-param name="headtext">
                        <xsl:value-of select="java:getString($dict_job_module, 'SELECT_FIELD')"/>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>
        
            
            <tr>
                <td class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
                <td width="30%" class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'TYPE')"/></td>
            </tr>
            
            <xsl:for-each select="negeso:field_type">
			    <tr>
			        <td class="admTableTD" id="admTableTDtext">
			            <a class="admTableTDlink" href="#" onClick = "return selectFieldType({@id})">
			                <xsl:value-of select="@title"/>&#160;
			            </a>
			        </td>
			        <td class="admTableTDLast" id="admTableTDtext" >
			            <xsl:value-of select="@type_name"/>&#160;
			        </td>
			    </tr>
            </xsl:for-each>
        <tr>
            <td class="admTableFooter" >&#160;</td>
        </tr>
        </table>
</xsl:template>

</xsl:stylesheet>