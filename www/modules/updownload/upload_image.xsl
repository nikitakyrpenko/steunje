<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$		
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Upload file form.
 
  @author		Olexiy.Strashko
  @version		$Revision$
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_media_catalog" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_media_catalog.xsl', $lang)"/>
<xsl:variable name="dict_upload_image" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_upload_image.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
	<script language="JavaScript">
	    var s_MustSelect = "<xsl:value-of select="java:getString($dict_media_catalog, 'MUST_SELECT')"/>";
        var s_More = "<xsl:value-of select="java:getString($dict_upload_image, 'MORE')"/>";
        var s_Less = "<xsl:value-of select="java:getString($dict_upload_image, 'LESS')"/>";
        var s_FileIsNotChosen = "<xsl:value-of select="java:getString($dict_media_catalog, 'FILE_IS_NOT_CHOSEN')"/>";

	<xsl:text disable-output-escaping="yes">
	    <![CDATA[

		var curMode = "image";
		var bShowMore = false;
		
		function ChooseFromMediaCatalog(selectBox) 
		{
			window.name='UploadWindow';
			MediaCatalog.chooseImage(selectBox);
			document.all.fileSourceId.value = "catalog";
		}
		
		function setThumbnailMode(){
			window.name='UploadWindow';
		
			if (curMode == "image"){
				// switch to thumbnail mode
				curMode = "thumbnail";
				image_div.style.display = "none";
				thumbnail_div.style.display = "inline";
				enableResizeDiv();
				resizediv.style.display = "inline";
			}
			else{
				// switch to image mode
				curMode = "image";
				image_div.style.display = "inline";
				thumbnail_div.style.display = "none";
				enableResizeDiv();
			}
		}
		

		function enableResizeDiv(){
			window.name='UploadWindow';
			
			var bResizeMode = true;
			if (
				document.all.is_resizeId.checked || 
				document.all.is_optimizeId.checked ||
				document.all.is_thumbnailId.checked
				)
			{
				 bResizeMode = true;
			}
			
			if (
				(!document.all.is_resizeId.checked) && 
				(!document.all.is_optimizeId.checked) &&
				(!document.all.is_thumbnailId.checked)
				)
			{
				 bResizeMode = false;
			}
			resizediv.disabled = !bResizeMode;
		} 


		function showMore(){
			window.name='UploadWindow';

			if (bShowMore){
				bShowMore = false;
				morediv.style.display = "none";
				document.all.moreButtonId.value = s_More + " >>>";
			}
			else{
				bShowMore = true;
				morediv.style.display = "inline";
				document.all.moreButtonId.value = s_Less + " <<<";
			}
		}


		function returnResult(retHtml){
			window.name='UploadWindow';
			window.returnValue = retHtml;
			//alert(retHtml);
			window.close();
		}
		

		function OnCancel()
		{
			window.name='UploadWindow';
			window.returnValue = null;
			window.close();
		}


		function checkForm(){
			window.name='UploadWindow';
			var lnk = window.location.href;
			document.all.hostLinkId.value = lnk.replace(window.location.search, "");
			//document.all.hostLinkId.value = "http://" + window.location.host;
			//alert(document.all.hostLinkId.value);
			if (document.all.is_thumbnailId.checked == true){
				if (document.all.fileSourceId.value == "disk"){
					if (document.all.selectedThFileId.value == "") {
						alert(s_FileIsNotChosen);
						return false;
					}
				}
			}
			else{
				if (document.all.fileSourceId.value == "disk"){
					if (document.all.selectedFileId.value == "") {
						alert(s_FileIsNotChosen);
						return false;
					}
				}
			}
			document.all.submitButton.disabled = true;
			return true;
		}

		function onExistentChange(){
			if (document.all.is_thumbnailId.checked == false){
				if (document.all.existentFileId.value == "None"){
					document.all.selectedFileId.disabled = false;
					document.all.fileSourceId.value = "disk";
				}
				else{	
					document.all.selectedFileId.disabled = true;
					document.all.fileSourceId.value = "catalog";
				}	
			}
			else{
				if ((document.all.existentLargeId.value == "None") &&
					(document.all.existentSmallId.value == "None")
				){
					document.all.selectedThFileId.disabled = false;
					document.all.fileSourceId.value = "disk";
				}
				else{	
					document.all.selectedThFileId.disabled = true;
					document.all.fileSourceId.value = "catalog";
				}	
			}
		}
		
		function onLoad(){ /* no-op */ }
		
		attachEvent ("onload", resizeDialogWindow); //resize dialog window height
		attachEvent ("onload", onLoad); //do on load option
		]]>
		</xsl:text>
	</script>
</xsl:template>
	

<xsl:template match="/">
<html>
<head>
	   <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
	   <title><xsl:value-of select="java:getString($dict_upload_image, 'INSERT_PICTURE')"/></title>
	   <link rel="stylesheet" type="text/css" href="/css/admin_dialog_style.css"/>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script language="JavaScript" src="/script/common_functions.js" type="text/javascript"/>
	   <script language="JavaScript1.2" src="/script/media_catalog.js" type="text/javascript"/>
	   <script language="JavaScript1.2" src="/script/conf.js" type="text/javascript"/>
	   <xsl:call-template name="java-script"/>
</head>
	<body>
		<xsl:apply-templates select="negeso:page"/>
	</body>
</html>
</xsl:template>


<xsl:template match="negeso:page[@type='image-upload-result']">
	<xsl:choose>
	<xsl:when test="negeso:ret-html">
		<xsl:attribute name="onLoad">returnResult('<xsl:value-of select="negeso:ret-html/text()"/>');</xsl:attribute>
	</xsl:when>
	<xsl:otherwise>
		<xsl:apply-templates select="negeso:error-messages"/>
	</xsl:otherwise>
	</xsl:choose>
	
	<table class="decor" bgcolor="#e2f1e4" align="center" border="0">
		<tr>
			<td align="right">
				<button style="width: 70" onclick="OnCancel()"><xsl:value-of select="java:getString($dict_common, 'CLOSE')"/></button>
			</td>
		</tr>
	</table>
</xsl:template>


<!-- MAIN ENTRY -->
<xsl:template match="negeso:page[@type='face']">
	<xsl:apply-templates select="negeso:error-messages"/>
<form id="actionForm" action="" 
	method="post" 
	enctype="multipart/form-data"
	target="UploadWindow" onsubmit="return checkForm()"
>
	<input type="hidden" name="imageSource" />
	<input type="hidden" name="command" value="image-uploader-command"/>
	<input id="fileSourceId" type="hidden" name="fileSource" value="disk"/>
	<input id="hostLinkId" type="hidden" name="hostLink" value=""/>
	<br/>
	
	<xsl:apply-templates select="negeso:thumbnail-option"/>
	<xsl:apply-templates select="negeso:image-option"/>
	
	<!--
	<fieldset>
	<legend class="admBold">Optional properties</legend>
	-->
		<table class="admMarginAll" width="100%">
		<tr>
			<td class="admLeft" colspan="2">
				<xsl:value-of select="java:getString($dict_upload_image, 'IMAGE_DESCRIPTION')"/>:
			</td>
		</tr>
		<tr>
			<td style="text-align: right; padding: 0">
				<input type="text" name="alt_text" style="width: 100%; margin: 0px; padding: 0px;"/>
			</td>
			<td width="20%" class="admLeft" style="text-align: left">
				<input style="margin-left: 0;" class="button" type="button" id="moreButtonId" name="more" onclick="showMore()">
                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_upload_image, 'MORE')"/> >>></xsl:attribute>
                </input>
			</td>
		</tr>
		</table>
	<!--
	</fieldset>
	-->

	<div ID="morediv" style="display: none;">
	<table width="100%">
	<tr><td colspan="2" >
		<table class="admMarginAll" width="100%">
			<tr>
				<td align="right" width="80px">
					<xsl:value-of select="java:getString($dict_upload_image, 'UPLOAD_FOLDER')"/>
				</td>
				<td>
					<xsl:apply-templates select="negeso:folder-set"/>
				</td>
			</tr>
		</table>
		<table class="admMarginAll" width="100%">
			<tr>
				<td align="left" width="50%">
					<input 
						ID="is_thumbnailId" type="checkbox" class="radio"
						name="is_thumbnail" value="yes" 
						onClick="setThumbnailMode()"
						style="margin: 1px; padding: 1px;"
					/>
					<xsl:value-of select="java:getString($dict_upload_image, 'INSERT_THUMBNAIL')"/>
				</td>
				<td class="admLeft" width="30%">
				</td>
				<td width="20%" align="left">
				</td>
			</tr>
			<tr>
				<td align="left">
					<input 
						type="checkbox" class="radio" id="is_optimizeId" name="is_optimize" value="yes" 
						onClick="enableResizeDiv()"
						style="margin: 1px; padding: 1px;"
					/>
                    <xsl:value-of select="java:getString($dict_upload_image, 'OPTIMIZE_IMAGE')"/>
				</td>
				<td/>
				<td/>
			</tr>
			<tr>
				<td align="left">
					<input 
						type="checkbox" class="radio" name="is_resize" value="yes" 
						id="is_resizeId"
						onClick="enableResizeDiv()"
						style="margin: 1px; padding: 1px;"
					/>
                    <xsl:value-of select="java:getString($dict_upload_image, 'RESIZE_PICTURE')"/>
				</td>
				<td/>
				<td/>
			</tr>

		</table>
	</td>
	</tr>
		
	<tr><td width="50%">
		<fieldset>
			<legend class="admBold"><xsl:value-of select="java:getString($dict_upload_image, 'LAYOUT_PARAMETERS')"/></legend>
			<table width="100%" border="0">
			<tr>
				<td align="right" width="60%"><xsl:value-of select="java:getString($dict_upload_image, 'STICK_TO')"/>&#160; </td>
				<td align="left" width="40%">
					<select name="layout_align" class="admMedium" style="width: 60%; margin: 1px; padding: 1px;">
						<option value="" selected="true"></option>
						<option value="baseline"><xsl:value-of select="java:getString($dict_upload_image, 'BASELINE')"/></option>
						<option value="bottom"><xsl:value-of select="java:getString($dict_upload_image, 'BOTTOM')"/></option>
						<option value="left"><xsl:value-of select="java:getString($dict_upload_image, 'LEFT')"/></option>
						<option value="middle"><xsl:value-of select="java:getString($dict_upload_image, 'MIDDLE')"/></option>
						<option value="right"><xsl:value-of select="java:getString($dict_upload_image, 'RIGHT')"/></option>
						<option value="top"><xsl:value-of select="java:getString($dict_upload_image, 'TOP')"/></option>
					</select>
				</td>
			</tr>
			<tr>
				<td align="right"><xsl:value-of select="java:getString($dict_upload_image, 'BORDER')"/></td>
				<td align="left">
					<input type="text" size="2" value="0" name="layout_border" style="width: 60%; margin: 1px; padding: 1px;"/>
				</td>
			</tr>
			<tr>
				<td align="right"><xsl:value-of select="java:getString($dict_upload_image, 'VERTICAL_PADDING')"/></td>
				<td align="left">
					<input type="text" size="2" value="1" name="layout_vpadd" style="width: 60%; margin: 1px; padding: 1px;"/>
				</td>
			</tr>
			<tr>
				<td align="right"><xsl:value-of select="java:getString($dict_upload_image, 'HORIZONTAL_PADDING')"/></td>
				<td align="left">
					<input type="text" size="2" value="3" name="layout_hpadd" style="width: 60%; margin: 1px; padding: 1px;"/>
				</td>
			</tr>
			</table>
		</fieldset>
	</td>
	<td  width="50%" valign="top" height="100%">
		<div ID="resizediv" disabled="true" align="top" style="vertical-align: top" valign="top">
		<fieldset>
			<legend class="admBold"><xsl:value-of select="java:getString($dict_upload_image, 'RESIZE_PARAMETERS')"/></legend>
			<table width="100%" height="100%">	
			<tr>
				<td align="right" width="35%">
				</td>
				<td align="left"  width="65%">
					<input type="radio" class="radio" name="resize_mode" value="proportional" checked="true"/>
                    <xsl:value-of select="java:getString($dict_upload_image, 'RESIZE_PROPORTIONAL')"/>
				</td>
			</tr>
			<tr>
				<td align="right">
				</td>
				<td align="left">
					<INPUT  type="radio" class="radio" name="resize_mode" value="crop"/>
                    <xsl:value-of select="java:getString($dict_upload_image, 'CROP_IMAGE')"/>
				</td>
			</tr>
			<tr>
				<td align="right"><xsl:value-of select="java:getString($dict_media_catalog, 'WIDTH')"/></td>
				<td align="left"><input type="text" name="thumbwidth"/></td>
			</tr>
			<tr>
				<td align="right"><xsl:value-of select="java:getString($dict_media_catalog, 'HEIGHT')"/></td>
				<td align="left"><input  type="text" name="thumbheight"/></td>
			</tr>
			</table>
		</fieldset>
		</div>
		
	</td></tr>
	</table>
	</div>

	<div class="admDiv">
		<input ID="submitButton" type="submit">
            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_upload_image, 'INSERT')"/></xsl:attribute>
            <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_upload_image, 'INSERT_IMAGE')"/></xsl:attribute>
        </input>
		<input type="button" onclick="OnCancel()">
            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'CANCEL')"/></xsl:attribute>
            <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_upload_image, 'CANCEL_DIALOG')"/></xsl:attribute>
        </input>
	</div>

</form>
</xsl:template>

<xsl:template match="negeso:image-option">
<div ID="image_div" style="display: inline">
	<fieldset>
		<legend class="admBold" id="it"><xsl:value-of select="java:getString($dict_upload_image, 'SELECT_PICTURE')"/></legend>
		<table class="admMarginAll" width="90%">
		<tr>
			<td align="left" width="20%"><xsl:value-of select="java:getString($dict_upload_image, 'FROM_DISK')"/>&#160;
			</td>
			<td align="left" width="60%">
				<input id="selectedFileId" type="file" name="selectedFile" style="width: 100%; height: 22px; margin: 0px; padding: 0px;" onKeyPress="return false;"/>
			</td>
			<td align="left" width="20%">
				*.jpg,*.gif
			</td>
		</tr>
		<tr>
			<td align="left"><xsl:value-of select="java:getString($dict_upload_image, 'EXISTING_FILE')"/>&#160;</td>
			<td>
				<xsl:apply-templates select="negeso:resource-set[@type='existentFile']"/>
			</td>
			<td align="left">
				<button class="admMediaCatalogBtn" style="width: 25; height: 25" name="mcButton"  type="button"
					onClick="ChooseFromMediaCatalog(document.all.existentFileId)">
					<img src="/images/media_catalog/media_catalog_logo.gif"></img>
				</button>
			</td>
		</tr>
		</table>
	</fieldset>
</div>
</xsl:template>

<xsl:template match="negeso:thumbnail-option">
<div ID="thumbnail_div" style="display: none">
	<fieldset>
		<legend class="admBold" id="it"><xsl:value-of select="java:getString($dict_upload_image, 'SELECT_THUMBNAIL_PICTURE')"/></legend>
		<table class="admMarginAll" width="90%" border="0">
		<tr>
			<td align="left" width="20%"><xsl:value-of select="java:getString($dict_upload_image, 'FROM_DISK')"/>&#160;
			</td>
			<td align="left" width="60%">
				<input id="selectedThFileId" type="file" name="selectedThFile" style="width: 100%; height: 22px; margin: 0px; padding: 0px;" onKeyPress="return false;"/>
			</td>
			<td align="left" width="20%">*.jpg,*.gif</td>
		</tr>
		<tr>
			<td align="left"><xsl:value-of select="java:getString($dict_upload_image, 'LARGE_IMAGE')"/>&#160;</td>
			<td align="left">
				<xsl:apply-templates select="negeso:resource-set[@type='existentLarge']"/>
			</td>
			<td align="left">
				<button class="admMediaCatalogBtn" style="width: 25; height: 25" name="mcButton" type="button"
					onClick="ChooseFromMediaCatalog(document.all.existentLargeId)">
					<img src="/images/media_catalog/media_catalog_logo.gif"></img>
				</button>
			</td>
		</tr>
		<tr>
			<td align="left"><xsl:value-of select="java:getString($dict_upload_image, 'SMALL_IMAGE')"/>&#160;</td>
			<td>
				<xsl:apply-templates select="negeso:resource-set[@type='existentSmall']"/>
			</td>
			<td align="left">
				<button class="admMediaCatalogBtn" style="width: 25; height: 25" name="mcButton" type="button"
					onClick="ChooseFromMediaCatalog(document.all.existentSmallId)">
					<img src="/images/media_catalog/media_catalog_logo.gif"></img>
				</button>
			</td>
		</tr>
		</table>
	</fieldset>
</div>
</xsl:template>

<!-- *********** Resource set processing ************************ -->
<xsl:template match="negeso:resource-set">
	<select id="{@type}Id" name="{@type}" style="width: 100%; margin: 0px; padding: 0px;" onChange="onExistentChange()">
		<option value="None"><xsl:value-of select="java:getString($dict_upload_image, 'NONE')"/></option>
		<xsl:apply-templates select="negeso:resource"/>
	</select>
</xsl:template>

<xsl:template match="negeso:resource">
	<option>
		<xsl:attribute name="VALUE"><xsl:value-of select='@path'/></xsl:attribute>
		<xsl:value-of select='@path'/>
	</option>
</xsl:template>


<!-- *********** Folder set processing ************************ -->
<xsl:template match="negeso:folder-set">
	<select id="destFolderId" name="workfolder" style=" width:95%">
		<xsl:apply-templates select="negeso:folder"/>
	</select>
</xsl:template>	

<xsl:template match="negeso:folder">
	<option>
		<!-- File upload option -->
		<xsl:attribute name="VALUE"><xsl:value-of select='@path'/></xsl:attribute>
		<xsl:value-of select='@path'/>
	</option>
</xsl:template>


<xsl:template match="negeso:thumbnail-upload-result">
	<table class="decor" bgcolor="#e2f1e4" align="center" border="0">
		<tr>
			<td class="admin">
				<button class="but" style="width: 70" onclick="OnBack()"><xsl:value-of select="java:getString($dict_common, 'BACK')"/></button>
			</td>
			<td align="right">
				<button class="but" style="width: 70" onclick="OnCancel()"><xsl:value-of select="java:getString($dict_common, 'CLOSE')"/></button>
			</td>
		</tr>
	</table>
</xsl:template>

<xsl:template match="negeso:error-messages">
	<table cellspacing="1" cellpadding="1" align="center">
		<tr>
		<td width="12%" align="right" valign="top">
			<font face="verdana" size="2" color="#FF2B04"><b><xsl:value-of select="java:getString($dict_common, 'ERROR')"/>:</b></font>
		</td>
		<xsl:apply-templates select="negeso:message"/>
		</tr>
	</table>
</xsl:template>

<xsl:template match="negeso:user-messages">
	<table width="100%" cellspacing="1" cellpadding="1" align="center">
		<tr><td width="12%" align="right" valign="top"> 
			<font face="verdana" size="2">
			<b><xsl:value-of select="java:getString($dict_common, 'MESSAGE')"/>:</b>
			</font>
		</td>
		<xsl:apply-templates select="negeso:message"/>
        </tr>
	</table>
</xsl:template>
	
<xsl:template match="negeso:message">
	<td align="center">
		<font face="verdana" size="2">
			<xsl:value-of select="@text"/>
		</font>
	</td>
</xsl:template>

</xsl:stylesheet>
