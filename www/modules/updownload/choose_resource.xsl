<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${choose_resource.xsl}

  Copyright (c) 2003 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  View of a list item edit interface.

  @version		2003.12.23
  @author		Alexander Shkabarnya
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_media_catalog" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_media_catalog.xsl', $lang)"/>

<xsl:template match="/">
<xsl:apply-templates select="negeso:resource-set"/>
</xsl:template>

<xsl:template match="negeso:resource-set">
<html>
<head>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
<title><xsl:value-of select="java:getString($dict_media_catalog, 'LINK_TO_FILE')"/></title>
    <link rel="stylesheet" type="text/css" href="/css/admin_dialog_style.css"/>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script language="JavaScript" src="/script/common_functions.js" type="text/javascript"/>
    <script language="JavaScript1.2" src="/script/media_catalog.js" type="text/javascript"/>
    
<script>
	window.returnValue = null;
	var s_MustSelect = "<xsl:value-of select="java:getString($dict_media_catalog, 'MUST_SELECT')"/>";

	function ChooseFromMediaCatalog(selectBox) 
	{
		MediaCatalog.chooseFile(selectBox);
	}

	function goOk()
	{
		if (FileOpt.value=="")
		{
			alert(s_MustSelect);
		}
		else
		{
			//alert("You must select anything! " + chosenFile.value);
			window.dialogArguments.vURL = "/" + FileOpt.value;
			window.dialogArguments.isPopup = 0;
			if ( fSLO.options[fSLO.selectedIndex].text == "new window") {
				window.dialogArguments.vTarget="blank";
			}
		}
		window.returnValue = "OK";
		window.close();
	}

	function goCancel()
	{
		window.returnValue = null;
		window.close();
	}
	attachEvent ("onload", resizeDialogWindow); //resize dialog window height
</script>


</head>
<body>
<fieldset title="URL">
	<legend class="admBold"><xsl:value-of select="java:getString($dict_media_catalog, 'SELECT_FILE')"/></legend>
	<table class="admBorder admMarginAll">
		<tr>
			<td class="admWidth_80 admRight">&#160;</td>
			<td>
				<SELECT ID="FileOpt" NAME="FileOpt" class="admBig">
					<OPTION value=""></OPTION>
					<xsl:apply-templates select="negeso:resource"/>
				</SELECT>
				<button class="admMediaCatalogBtn" style="width: 25; height: 25" name="mcButton" 
					onClick="ChooseFromMediaCatalog(document.all.FileOpt)">
					<img src="/images/media_catalog/media_catalog_logo.gif"></img>
				</button>
				
			</td>
		</tr>
		<tr>
			<td class="admWidth_80 admRight">Open in</td>
			<td>
				<select class="admBig" id="fSLO" name="fSLO">
					<option value="this window">this window</option>
					<option value="new window">new window</option>
				</select>
			</td>
		</tr>
	</table>
</fieldset>
<div class="admDiv">
<button onclick="goOk()"><xsl:value-of select="java:getString($dict_common, 'OK')"/></button> <button  onclick="goCancel()"><xsl:value-of select="java:getString($dict_common, 'CANCEL')"/></button>
</div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:resource">
	<OPTION>
		<xsl:attribute name="VALUE"><xsl:value-of select='@path'/></xsl:attribute>
		<xsl:value-of select='@path'/>
	</OPTION>
</xsl:template>

</xsl:stylesheet>
