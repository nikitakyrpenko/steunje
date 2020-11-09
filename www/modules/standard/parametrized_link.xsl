<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$		
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Parametrized link. Creates form wich calls link and passes parameters to it.
 
  @author		Olexiy.Strashko
  @version		$Revision$
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>

<!-- Include/Import -->
<xsl:include href="/xsl/negeso_header.xsl"/>
<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
	<script language="JavaScript">
	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
	    
		function onLoadWindow(){
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
			<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
			<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
			<xsl:call-template name="java-script"/>
		</head>
	<body onLoad="onLoadWindow()">
        <!-- NavBar -->
	    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
	    	<xsl:if test="count(@status-message)">
		        <tr><td class="admStatusMessage admCenter">
		             <xsl:value-of select="@status-message"/>
		         </td></tr>
		    </xsl:if>
	    	<xsl:if test="count(@error-message)">
		        <tr><td class="admErrorMessage admCenter">
		             <xsl:value-of select="@status-message"/>
		         </td></tr>
		    </xsl:if>
	    </table>
        <!-- CONTENT -->
       	<xsl:apply-templates select="negeso:page"/>
        <!-- NavBar -->
	</body>
	</html>
</xsl:template>

<xsl:template match="negeso:page">
   	<xsl:apply-templates select="negeso:link-info"/>
</xsl:template>

<!-- LINK PROCESSOR FORM GENERATION-->
<xsl:template match="negeso:link-info">
	<form 
	    name="operateForm" 
	    method="{@http-method}" 
	    action="{@url}" 
	    enctype="multipart/form-data"
	>
		<xsl:for-each select="negeso:parameter">									
			<input type="hidden" name="{@name}" value="{@value}"/>
		</xsl:for-each>
	</form>
</xsl:template>


</xsl:stylesheet>
