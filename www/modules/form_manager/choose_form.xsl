<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}		
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a list item edit interface.
 
  @version		2004.09.27
  @author		Alexander G. Shkabarnya
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
<xsl:variable name="dict_form_manager" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_form_manager.xsl', $lang)"/>

<xsl:include href="/xsl/admin_templates.xsl"/>

<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_form_manager, 'CHOOSE_FORM')"/></title>
	<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	<link href="/site/core/css/default_styles.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/common_functions.js" xml:space="preserve"> </script>
	<xsl:text disable-output-escaping="yes">
	<![CDATA[	
	<script> 
	var formsArray = new Array();
	var currentPreview = null;
	var bufferObject = null;
	var currentObject = null;
	function previewForm( index , preview )
	{
		var formObject = getFormObject(index);
		if (formObject!=null)
		{
			if (currentPreview!=null)
			{
				currentPreview.className = "normForm";
			}
			preview.className = "previewForm";
			currentPreview = preview;
			document.getElementById('preview_div').innerHTML = formObject.form_text;
			makeReadonly(document.getElementById('preview_div'), true);
			currentObject = formObject;
		}
	}
	
	function getFormObject(oID)
	{
		for(var i=0; i<formsArray.length; i++ )
		{
			if( formsArray[i].form_id == oID )
			{
				return formsArray[i];
			}
		}
		return null;
	}
	
	function pageLoad()
	{
		var formTags = document.getElementsByTagName("negeso_form_tag");
		var boldTag = null;
		for (var i=0; i<formTags.length; i++)
		{
			bufferObject = new Object();
			bufferObject.form_id = formTags[i].form_id;
			bufferObject.form_text = formTags[i].form_text;
			bufferObject.form_name = formTags[i].form_name;
			bufferObject.form_email = formTags[i].form_email;
			bufferObject.form_ex = formTags[i].form_ex;
			formsArray.push(bufferObject);
			if ( i == 0 )
			{
				boldTag = document.getElementById("b"+formTags[i].form_id);
				if ( boldTag != null)
				{
					boldTag.className = "previewForm";
					currentPreview = boldTag;
					document.getElementById('preview_div').innerHTML = formTags[i].form_text;
					makeReadonly(document.getElementById('preview_div'), true);
					currentObject = formTags[i];
				}
			}
		}
	}
	
	function clickOK()
	{
		window.returnValue = currentObject;
		window.close();
	}
	
	function clickCancel()
	{
		window.returnValue  = null;
		window.close();
	}
	</script>
	<style>
	.normForm
	{
		color: black;
		font-size: 12px;
		font-weight: bold;
		font-family: arial;
		cursor: hand;
	}
	.previewForm
	{
		color: #0000FF;
		font-size: 12px;
		font-weight: bold;
		font-family: arial;
		cursor: hand;
	}
	</style>
	]]>
	</xsl:text>
</head>
<body onload="pageLoad();">
    <negeso_forms_tag id="negeso_forms_tag">
		      <xsl:apply-templates select="//negeso:form_manager/negeso:form" mode="array"/>
	   </negeso_forms_tag>
	   <xsl:call-template name="NegesoBody">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cfr1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>
    </xsl:call-template>
	   <div align="center">
	   <xsl:call-template name="tableTitle">
        		<xsl:with-param name="headtext">
           		<xsl:value-of select="java:getString($dict_form_manager, 'CHOOSE_FORM')"/>
        		</xsl:with-param>
    </xsl:call-template>
	<table border="0" class="admNavPanel">
	<tr>
		<td class="admNavbar admCenter" style="width: 200;"><xsl:value-of select="java:getString($dict_form_manager, 'FORM')"/></td>
		<td class="admNavbar admCenter" style="width: 400;"><xsl:value-of select="java:getString($dict_common, 'PREVIEW')"/></td>
	</tr>
	<tr height="100%" >
		<td nowrap="true">
		<table border="0" width="100%" height="100%" cellpadding="0" cellspacing="0">
		<xsl:apply-templates select="//negeso:form_manager/negeso:form" />
		<tr height="100%">
			<td style="background-color: #E2F2E0; width: 100%;">&#160;</td>
		</tr>
		</table>
		</td>
		<td nowrap="true" style="background-color: #C9E7C4;">
		<div style="width: 100%; height: 100%;"><div id="preview_div" style="margin: 5;padding: 15; border: 1 solid black; width: 100%; height: 200;" class="contentStyle">
		&#160;
		</div></div>
		</td>
	</tr>
	<tr>
	<td colspan="2" class="admNavbar admCenter">
		<button class="admNavbarInp" onclick="clickOK();">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SELECT')"/>&#160;&gt;</button>
		<button class="admNavbarInp" onclick="clickCancel();">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'CANCEL')"/>&#160;&gt;</button>
	</td>
	</tr>
	</table>
	</div>
</body>
</html>
</xsl:template>


<xsl:template match="negeso:form">
	<tr>
		<td style="background-color: #E2F2E0; width: 100%; border-bottom: 3 solid white; padding: 5; font-family: arial; font-size: 12px; color: black;">
		<b class="normForm" id="b{@form_id}" onclick="previewForm('{@form_id}', this );"><xsl:value-of select="@name" /></b><br/>
		<b><xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>: </b><xsl:value-of select="@email" />
		</td>
	</tr>
</xsl:template>

<xsl:template match="negeso:form" mode="array">
	<negeso_form_tag form_id="{@form_id}" form_text="{text()}" form_name="{@name}"  form_email="{@email}" form_ex="{@ex}"/>	
</xsl:template>


</xsl:stylesheet>

