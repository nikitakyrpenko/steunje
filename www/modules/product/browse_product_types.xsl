<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${product_type_list.xsl}		
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View a list of product types.
 
  @version      $Revision$
  @author		Olexiy.Strashko
  @author     Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">

<!-- Include/Import -->
<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:include href="/xsl/negeso_body.xsl"/>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
    <script type="text/javascript" src="/script/jquery.min.js" />
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>    
	<script language="JavaScript">
	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
		function Add() {
            document.operateForm.command.value = "pm-get-edit-category-page";
        	document.operateForm.updateTypeField.value = "insert";
		}

		function Delete(targetId) {
	        if (confirm("Are you sure you want to delete this category?")) {
                document.operateForm.command.value = "pm-delete-category";
	        	document.operateForm.pmTargetId.value = targetId;
	        	document.operateForm.submit();
	        	return true;
			}
			return false;
		}

		function Edit(targetId) {
            document.operateForm.command.value = "pm-get-edit-category-page";
        	document.operateForm.pmTargetId.value = targetId;
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
   <title>Browse category</title>
	<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<xsl:call-template name="java-script"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <!-- NEGESO HEADER -->
	 <xsl:call-template name="NegesoBody">
      <xsl:with-param name="helpLink">
          <xsl:text>/admin/help/cpr1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
      </xsl:with-param>
         <xsl:with-param name="backLink" select='"?command=pm-get-entry-page"' />
	 </xsl:call-template>
    
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page"  mode="admContent">
    <!-- Content -->
	<xsl:call-template name="PmRenderContent"/>  
</xsl:template>  

<xsl:template name="PmRenderContent">
	<!-- Render HEADER -->
	<form method="POST" name="operateForm" action="">
    <input type="hidden" name="command" value="pm-browse-product-types"></input>
    <input type="hidden" name="type" value="none"></input>
    <input type="hidden" name="pmTargetId" value="-1"></input>
    <input type="hidden" name="pmCatId" value="{@id}"></input>
    <input type="hidden" name="updateTypeField" value="update"></input>
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td align="center" class="admNavPanelFont"   colspan="3">
                    <!-- TITLE -->
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            Browse product types
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>   
        <tr>
        	<td class="admTDtitles" colspan="3" align="center">Title</td>
        	
        </tr>
		<xsl:apply-templates select="negeso:pm-product-type"/>
            <tr>
                <td class="admTableFooter" >&#160;</td>
            </tr>
    </table>
	</form>
</xsl:template>

<!-- ********************************** Category *********************************** -->

<xsl:template match="negeso:pm-product-type">
	<!-- Render category element -->
	<tr>
        <th class="admTableTD" width="90%">
			<a class="admAnchor" href="?command=pm-browse-category&amp;pm-cat-id={@id}" disabled="true" onClick="return false;">
				<xsl:attribute name="href">?command=pm-edit-product-type&amp;pmProductTypeId=<xsl:value-of select="@id"/></xsl:attribute>
				<xsl:value-of select="@title" disable-output-escaping="yes"/>
			</a>
		</th>
        <td class="admTableTDLast">
			<img class="admHand" src="/images/edit.png" alt="Edit" onClick="Edit({@id})">
				<xsl:if test="@is-template='true'">
					<xsl:attribute name="disabled">true</xsl:attribute>
				</xsl:if>
			</img>
		</td>
		<td class="admTableTDLast">
			<img class="admHand" src="/images/delete.png" alt="Delete" onClick="return Delete({@id})">
				<xsl:if test="@is-template='true'">
					<xsl:attribute name="disabled">true</xsl:attribute>
				</xsl:if>
			</img>
		</td>
	</tr>
</xsl:template>

</xsl:stylesheet>
