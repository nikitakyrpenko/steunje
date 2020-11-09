<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:template match="/">
<xsl:text disable-output-escaping="yes"><![CDATA[<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"> -->]]></xsl:text>
<html>
	<head>
		<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
		<title><![CDATA[{#negeso_advimage.add_image_title}]]></title>
		<script language="javascript" type="text/javascript" src="rte/tinymce/jscripts/tiny_mce/tiny_mce_popup.js"></script>
		<script language="javascript" type="text/javascript" src="rte/tinymce/jscripts/tiny_mce/utils/mctabs.js"></script>
		<script language="javascript" type="text/javascript" src="rte/tinymce/jscripts/tiny_mce/utils/form_utils.js"></script>
		<script language="javascript" type="text/javascript" src="rte/tinymce/jscripts/tiny_mce/plugins/negeso_advimage/js/image.js"></script>
		<script type="text/javascript" src="/script/RTE_Media_Catalog.js"></script>
		<script language="javascript" type="text/javascript">
			<xsl:text disable-output-escaping="yes">
			<![CDATA[

			adv_is_shown = false;
			adv_adv_is_shown = false;
			thumb_shown = false;
			
			function show_adv() {
				var s = document.getElementById("upload_adv_show").value;
				if (!adv_is_shown) {
					adv_is_shown = true;
					document.getElementById("adv_options").style.height = "";
					s = s.substring(0,s.length-4) + " <<<";
					document.getElementById("upload_adv_show").value = s;
				} else {
					adv_is_shown = false;
					document.getElementById("adv_options").style.height = "1px";
					s = s.substring(0,s.length-4) + " >>>";
					document.getElementById("upload_adv_show").value = s;
				}
			}
			
			function show_adv_adv() {
				adv_adv_is_shown = (document.getElementById("is_optimizeID").checked || document.getElementById("is_resizeID").checked);
				if (adv_adv_is_shown)
					document.getElementById("adv_adv_options").style.height = "";
				else
					document.getElementById("adv_adv_options").style.height = "1px";
			}
			
			function show_thumb() {
				thumb_is_shown = document.getElementById("is_thumbnailId").checked;
				if (thumb_is_shown) {
					document.getElementById("main_file").style.height = "1px";
					document.getElementById("thumbnail_file").style.height = "";
					document.getElementById("selected_file_id").name = "selectedThFile";
				} else {
					document.getElementById("main_file").style.height = "";
					document.getElementById("thumbnail_file").style.height = "1px";
					document.getElementById("selected_file_id").name = "selectedFile";
				}
			}
			
			// Part from old RTE, but modified
			
			function check_form(alert_message) {
				
				var lnk = window.location.href;
				document.getElementById("hostLinkId").value = lnk.replace(window.location.search, "");
				
				if (document.getElementById("is_thumbnailId").checked == true) {
					if (document.getElementById("fileSourceId").value == "disk") {
						if (document.getElementById("selected_file_id").value == "") {
							alert(alert_message);
							return false;
						}
					}
				}
				else{
					if (document.getElementById("fileSourceId").value == "disk"){
						if (document.getElementById("selected_file_id").value == "") {
							alert(alert_message);
							return false;
						}
					}
				}
				//document.getElementById("submitButton").disabled = true;
				return true;
			}
			
			function onExistentChange() {
				var is_thumbnail_obj = document.getElementById("is_thumbnailId");
				var existent_file_obj = document.getElementById("existentFileId");
				var selected_file_obj = document.getElementById("selected_file_id");
				var file_source_obj = document.getElementById("fileSourceId");
				var existent_large_obj = document.getElementById("existentLargeId");
				var existent_small_obj = document.getElementById("existentSmallId");
				var src_obj = document.getElementById("src");
				
				if (is_thumbnail_obj.checked == false) {
					if (existent_file_obj.value == "None") {
						selected_file_obj.disabled = false;
						src_obj.disabled = false;
						file_source_obj.value = "disk";
					}
					else{	
						selected_file_obj.disabled = true;
						src_obj.disabled = true;
						file_source_obj.value = "catalog";
					}	
				}
				else{
					if ((existent_large_obj.value == "None") &&
						(existent_small_obj.value == "None")) 
					{
						selected_file_obj.disabled = false;
						src_obj.disabled = false;
						file_source_obj.value = "disk";
					}
					else{	
						selected_file_obj.disabled = true;
						src_obj.disabled = true;
						file_source_obj.value = "catalog";
					}	
				}
			}
						
			MediaCatalog.is_active = true;
			
			function choose_from_media_catalog(select_box_id)
			{
				MediaCatalog.chooseImage(select_box_id);
				document.getElementById("fileSourceId").value = "catalog";
			}
			
			]]>
			</xsl:text>
		</script>
		<link href="rte/tinymce/jscripts/tiny_mce/plugins/negeso_advimage/css/advimage.css" rel="stylesheet" type="text/css" />
		<base target="_self" />
	</head>
	<body id="advimage" style="display: none; margin: 0; padding: 6px; text-align: center">
		<table width="100%" height="100%" cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td valign="top" align="center"><xsl:apply-templates select="negeso:page"/></td>
			</tr>
		</table>
	</body>
</html>
</xsl:template>

<xsl:template match="negeso:page[@type='face']">

	<xsl:apply-templates select="negeso:error-messages"/>

	<form action=""	method="post" enctype="multipart/form-data">
		<xsl:attribute name="onSubmit"><![CDATA[return check_form("{#negeso_advimage.upload_alert_local}")]]></xsl:attribute>

		<input type="hidden" name="imageSource" />
		<input type="hidden" name="command" value="second-image-uploader-command"/>
		<input id="fileSourceId" type="hidden" name="fileSource" value="disk"/>
		<input id="hostLinkId" type="hidden" name="hostLink" value=""/>

		<div class="panel_wrapper">
			<div style="display: block; width: 435px; height: auto; overflow: visible;">

				<fieldset>
					<legend><![CDATA[{#negeso_advimage.upload_optional}]]></legend>
					<table class="properties">
						<col width="120px"/><col width="*"/>
						<tr>
							<td class="column1"><label id="srclabel" for="src"><![CDATA[{#negeso_advimage.upload_image_src}]]></label></td>
							<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr> 
										<td style="width: 262px; text-align: left">
								 			<input type="text" value="" id="src" readOnly="true" style="width: 258px"/>
										</td> 
										<td style="width: 18px">
											<div style="width: 18px; height: 16px; position: relative">
												<img id="fake_upload" alt="Browse..." src="rte/tinymce/jscripts/tiny_mce/plugins/negeso_advimage/images/browse.gif" style="position: absolute; z-index: 1; right: 0"/>
												<div style="height: 20px; width: 18px; position: absolute; z-index: 2; right: 0; overflow: hidden">
													<input onKeyPress="return false;" type="file" size="1" name="selectedFile" id="selected_file_id" title="Browse..." onChange="document.getElementById('src').value=this.value" style="position: absolute; right: 0; text-align: right; opacity: 0; -moz-opacity: 0; filter: alpha(opacity: 0)" value=""/>
												</div>
											</div>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>					
				</fieldset>
				
				<fieldset>
					<legend><![CDATA[{#negeso_advimage.upload_main}]]></legend>

					<div id="main_file" style="overflow: hidden">
					<table class="properties">
						<col width="120px"/><col width="*"/>
						<tr>
							<td><label for="imagelistsrc"><![CDATA[{#negeso_advimage.upload_list_1}]]></label></td>
							<td>
								<xsl:apply-templates select="negeso:image-option/negeso:resource-set[@type='existentFile']"/>
							</td>
						</tr>
					</table>
					</div>
					<div id="thumbnail_file" style="height: 1px; overflow: hidden">
						<table class="properties">
							<col width="120px"/><col width="*"/>
							<tr>
								<td><label for="imagelistsrc"><![CDATA[{#negeso_advimage.upload_list_2}]]></label></td>
								<td>
									<xsl:apply-templates select="negeso:thumbnail-option/negeso:resource-set[@type='existentLarge']"/>
								</td>
							</tr>	
							<tr>
								<td><label for="imagelistsrc"><![CDATA[{#negeso_advimage.upload_list_3}]]></label></td>
								<td>
									<xsl:apply-templates select="negeso:thumbnail-option/negeso:resource-set[@type='existentSmall']"/>
								</td>
							</tr>
						</table>
					</div>
					<table class="properties">
						<col width="120px"/><col width="*"/>						
						<tr> 
							<td><label id="altlabel" for="alt"><![CDATA[{#negeso_advimage.alt}]]></label></td> 
							<td><input id="alt" name="alt_text" type="text" value="" /></td> 
						</tr> 
					</table>
				</fieldset>
				
				<br/>
				<input style="width: 200px; text-align: center" type="button" id="upload_adv_show" onClick="show_adv()">
					<xsl:attribute name="value"><![CDATA[{#negeso_advimage.upload_advanced_show}]]> &gt;&gt;&gt;</xsl:attribute>
				</input>
				
				<div id="adv_options" style="width: 100%; height: 1px; overflow: hidden">
					<fieldset>
						<legend><![CDATA[{#negeso_advimage.upload_advanced}]]></legend>
						<table class="properties">
							<tr>
								<td>
									<input style="width: 16px; height: 15px; border: none" type="checkbox" id="is_thumbnailId" name="is_thumbnail" value="yes" onClick="show_thumb()"/><![CDATA[{#negeso_advimage.upload_thumbnail_chk}]]><br/>
								</td>
								<td align="right">
									<![CDATA[{#negeso_advimage.upload_choose_dir}]]><xsl:apply-templates select="negeso:folder-set"/>
								</td>
							</tr>
							<tr>
								<td colspan="2">
<!--									<input style="width: 16px; height: 15px; border: none" id="is_optimizeID" type="checkbox" name="is_optimize" value="yes" onClick="show_adv_adv()"/><![CDATA[{#negeso_advimage.upload_optimize_chk}]]><br/>-->
									<input style="width: 16px; height: 15px; border: none" id="is_optimizeID" type="checkbox" name="is_optimize" value="yes" /><![CDATA[{#negeso_advimage.upload_optimize_chk}]]><br/>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<input style="width: 16px; height: 15px; border: none" id="is_resizeID" type="checkbox" name="is_resize" value="yes" onClick="show_adv_adv()"/><![CDATA[{#negeso_advimage.upload_resize_chk}]]><br/>
								</td>
							</tr>
						</table>
						<div id="adv_adv_options" style="width: 220px; height: 1px; overflow: hidden">
							<fieldset>
								<legend><![CDATA[{#negeso_advimage.upload_advanced_advanced}]]></legend>
								<table class="properties">
									<tr>
										<td>&#160;</td>
										<td>
											<input style="width: 16px; height: 15px; border: none" type="radio" name="resize_mode" value="proportional" checked="true"/><![CDATA[{#negeso_advimage.upload_resize_proportional}]]><br/>
										</td>
									</tr>
									<tr>
										<td>&#160;</td>
										<td>
											<input style="width: 16px; height: 15px; border: none" type="radio" name="resize_mode" value="crop"/><![CDATA[{#negeso_advimage.upload_resize_crop}]]><br/>
										</td>
									</tr>	
									<tr>
										<td align="right"><![CDATA[{#negeso_advimage.upload_resize_width}]]></td>
										<td><input type="text" name="thumbwidth" style="width: 100px"/></td>
									</tr>
									<tr>
										<td align="right"><![CDATA[{#negeso_advimage.upload_resize_height}]]></td>
										<td><input type="text" name="thumbheight" style="width: 100px"/></td>
									</tr>									
								</table>
							</fieldset>
						</div>
						<span style="line-height: 20px; color: #0044aa"><![CDATA[{#negeso_advimage.upload_hint}]]></span>
					</fieldset>
				</div>
			</div>

		</div>

		<div class="mceActionPanel">
			<div style="float: left">
				<input type="submit" id="insert" name="insert">
					<xsl:attribute name="value"><![CDATA[{#negeso_advimage.insert}]]></xsl:attribute>
				</input>
			</div>
			<div style="float: right">
				<input type="button" id="cancel" name="cancel" onclick="cancelAction();">
					<xsl:attribute name="value"><![CDATA[{#negeso_advimage.cancel}]]></xsl:attribute>
				</input>
			</div>
		</div>
    </form>
</xsl:template>

<xsl:template match="negeso:page[@type='image-upload-result']">

	<form action=""	method="post" enctype="multipart/form-data" onSubmit="cancelAction(); return false;">
		<input type="hidden" name="src" id="src"/>
		<input type="hidden" name="alt" id="alt"/>
		<input type="hidden" name="title" id="title"/>
		<input type="hidden" name="onmousedown" id="onmousedown"/>
		<input type="hidden" name="onclick" id="onclick"/>
		<input type="hidden" name="style" id="style"/>
		
		<xsl:choose>
			<xsl:when test="negeso:ret-html">
				<script language="javascript" type="text/javascript">

					var ret = '<xsl:value-of select="negeso:ret-html/text()"/>';
					var re;
														
					re = new RegExp("&lt;img[^&lt;]* src='([^']+)'[^&gt;]*&gt;");
					var img_src = ret.match(re);
					try {
						img_src = img_src[1];
						re = /\/\//g;
						img_src = img_src.replace(re,"/");
						img_src = img_src.replace("/","//");
					} catch (e) {}
					
					re = new RegExp("&lt;img[^&lt;]* alt='([^']+)'[^&gt;]*&gt;");
					var img_alt = ret.match(re);
					try {img_alt = img_alt[1]} catch (e) {img_alt = ""}
					
					re = new RegExp("&lt;A[^&lt;]* href=.javascript.displayPopup..([^\']+)[^&gt;]+");
					var img_onmousedown = ret.match(re);
					try {
						img_onmousedown = img_onmousedown[1];
						re = /\/\//g;
						img_onmousedown = img_onmousedown.replace(re,"/");
						img_onmousedown = img_onmousedown.replace("/","//");
						img_onmousedown = "window.open('"+img_onmousedown+"', '', 'height=300, width=400, menubar=no, resizable=yes, scrollbars=yes, status=no, titlebar=yes, toolbar=no')";
						document.getElementById("style").value = "cursor: pointer";
					} catch (e) {}

					document.getElementById("src").value = img_src;
					document.getElementById("alt").value = img_alt;
					document.getElementById("title").value = img_alt;
					//document.getElementById("onmousedown").value = img_onmousedown;
					document.getElementById("onclick").value = img_onmousedown;
					ImageDialog.insertActionUpload();

				</script>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="negeso:error-messages"/>
			</xsl:otherwise>
		</xsl:choose>
		<center>
			If window is still open, then some fatal error is rised
			<input type="button" onClick="cancelAction();">
				<xsl:attribute name="value"><![CDATA[{#negeso_advimage.upload_close}]]></xsl:attribute>
			</input>
		</center>	
	</form>

</xsl:template>

<!-- Files list: Begin -->
<xsl:template match="negeso:resource-set">
	<select id="{@type}Id" name="{@type}" class="mceImageList" style="width: 260px; vertical-align: middle" onChange="onExistentChange();">
		<option value="None"><![CDATA[{#negeso_advimage.not_set}]]></option>
		<xsl:apply-templates select="negeso:resource"/>
	</select>
	<img style="width: 16px; height: 16px; margin-left: 5px; vertical-align: middle" src="/images/media_catalog/media_catalog_logo.gif" onClick="choose_from_media_catalog('{@type}Id')"/>
</xsl:template>

<xsl:template match="negeso:resource">
	<option value="/{@path}"><xsl:value-of select='@path'/></option>
</xsl:template>
<!-- Files list: End -->

<!-- Folders list: Begin -->
<xsl:template match="negeso:folder-set">
	<select id="destFolderId" name="workfolder" style="width: 210px;">
		<xsl:apply-templates select="negeso:folder">
		  <xsl:sort select="@path"></xsl:sort>
		</xsl:apply-templates>
	</select>
</xsl:template>	

<xsl:template match="negeso:folder">
	<option value="{@path}"><xsl:value-of select='@path'/></option>
</xsl:template>
<!-- Folders list: End -->

<!-- Error messages: Begin -->
<xsl:template match="negeso:error-messages">
	<div style="background-color: #ffeeee; border: 2px solid red; width: 340px; height: 100px; position: absolute; z-index: 100; left: 60px; top: 250px" onClick="this.style.visibility='hidden'">
			<table width="80%" height="100%" border="0" align="center" style="font-weight: bold; color: red">
				<tr>
					<td valign="middle" align="center">
			<!--		<font face="verdana" size="2" color="#FF2B04"><b><xsl:value-of select="java:getString($dict_common, 'ERROR')"/>:</b></font>-->
					<xsl:apply-templates select="negeso:message"/>
				</td>
			</tr>
			<tr>
				<td valign="middle" align="center"><![CDATA[{#negeso_advimage.upload_hint_2}]]></td>
			</tr>
		</table>
	</div>
</xsl:template>

<xsl:template match="negeso:message">
	<font face="verdana" size="2">
		<xsl:value-of select="@text"/>
	</font>
</xsl:template>
<!-- Error messages: End -->

</xsl:stylesheet>