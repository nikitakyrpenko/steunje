<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:template match="negeso:form_manager">
	<xsl:text disable-output-escaping="yes"><![CDATA[<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"> -->]]></xsl:text>
	<html>
		<head>
			<title><![CDATA[{#negeso_form_manager.title_list}]]></title>
			<script language="javascript" type="text/javascript" src="rte/tinymce/jscripts/tiny_mce/tiny_mce_popup.js"></script>
			<script language="javascript" type="text/javascript" src="rte/tinymce/jscripts/tiny_mce/utils/mctabs.js"></script>
			<script language="javascript" type="text/javascript" src="rte/tinymce/jscripts/tiny_mce/utils/form_utils.js"></script>
			<script language="javascript" type="text/javascript" src="rte/tinymce/jscripts/tiny_mce/plugins/negeso_form_manager/js/template_list.js"></script>
			<link href="rte/tinymce/jscripts/tiny_mce/plugins/negeso_form_manager/css/list.css" rel="stylesheet" type="text/css" />
			<link rel="stylesheet" type="text/css" href="/site/core/css/default_styles.css"/>
			<base target="_self" />
		</head>
		<body onload="tinyMCEPopup.executeOnLoad('init();');" style="display: none">
		    <form onsubmit="insertAction();return false;" action="#">
				<div class="tabs">
					<ul style="cursor: hand; cursor: pointer;">
		                <li id="general_tab" class="current" onclick="mcTabs.displayTab('general_tab','general_panel');">
		                    <span><a>{#negeso_form_manager.tab_general}</a></span>
		                </li>
					</ul>
				</div>
		
				<div class="panel_wrapper">
					<div id="general_panel" class="panel current">
						<script language="javascript" type="text/javascript">
							var current = ''<xsl:if test="negeso:form"> + 'form_template_<xsl:value-of select="position()"/>'</xsl:if>;
							var current_id = ''<xsl:if test="negeso:form"> + '<xsl:value-of select="negeso:form/@form_id"/>'</xsl:if>;
							
							function show_form(id, id2) {
								document.getElementById(id).style.display = 'block';
								if (current != '' &amp;&amp; current != id) document.getElementById(current).style.display = 'none';
								current = id;
								current_id = id2;
							}
						</script>
						
						<fieldset>
							<legend><![CDATA[{#negeso_form_manager.general_list}]]></legend>
							<table class="properties" style="height: 420px; width: 100%" border="0" cellspacing="6px">
								<tr>
									<td style="width: 160px">
										<div style="width: 100%; height: 404px; overflow: scroll; overflow-y: scroll; overflow-x: hidden; border: none;">
											<xsl:apply-templates select="negeso:form"/>
										</div>
									</td>
									<td>
										<div style="width: 100%; height: 404px; overflow: scroll; border: 1px solid #999999;">
											<xsl:apply-templates select="negeso:form" mode="preview"/>
										</div>
									</td>
								</tr>
							</table>
						</fieldset>

					</div>
				</div>
		
				<div class="mceActionPanel">
					<div style="float: left">
						<input type="button" id="insert" name="insert" onclick="insertAction();">
							<xsl:attribute name="value"><![CDATA[{#insert}]]></xsl:attribute>
						</input>
					</div>

					<div style="float: right">
						<input type="button" id="cancel" name="cancel" onclick="cancelAction();">
							<xsl:attribute name="value"><![CDATA[{#cancel}]]></xsl:attribute>
						</input>
					</div>
				</div>

		    </form>
		</body> 
	</html>
</xsl:template>

<xsl:template match="negeso:form">
	<table style="width: 136px; border: 1px solid #999999; margin-bottom: 12px;">
		<tr>
			<td style="font-weight: bold; text-align: center">
				<input type="button" style="width: 124px" onclick="show_form('form_template_{position()}', '{@form_id}')">
					<xsl:attribute name="value"><![CDATA[{#negeso_form_manager.template_preview}]]></xsl:attribute>
					<xsl:if test="count(../negeso:form)=1">
						<xsl:attribute name="disabled">true</xsl:attribute>
					</xsl:if>
				</input>
			</td>
		</tr>
		<tr>
			<td><strong><![CDATA[{#negeso_form_manager.template_name}]]></strong></td>
		</tr>
		<tr>
			<td><span style="width: 124px; overflow: hidden"><xsl:value-of select="@name"/></span></td>
		</tr>
		<tr>
			<td><strong><![CDATA[{#negeso_form_manager.template_email}]]></strong></td>
		</tr>
		<tr>
			<td><span style="width: 124px; overflow: hidden"><xsl:value-of select="@email"/></span></td>
		</tr>
		<tr>
			<td style="height: 5px"></td>
		</tr>
	</table>
</xsl:template>

<xsl:template match="negeso:form" mode="preview">
	<div style="display: none" id="form_template_{position()}" negeso_form_id="{@form_id}" class="negesoResetContentStyle contentStyle">
		<xsl:if test="position() = 1">
			<xsl:attribute name="style">display: block</xsl:attribute>
		</xsl:if>
		<xsl:value-of select="." disable-output-escaping="yes"/>
	</div>
</xsl:template>

</xsl:stylesheet>
