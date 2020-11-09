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
    <script language="JavaScript">
    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        function addField() {
            document.operateForm.action.value = "add_field";
        }

        function deleteField(targetId) {
            if (confirm(s_DeleteDepartmentConfirmation)) {
                document.operateForm.action.value = "delete_field";
                document.operateForm.field_id.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }

        function editField(targetId) {
            document.operateForm.action.value = "field_details";
            document.operateForm.field_id.value = targetId;
            document.operateForm.method = "GET";
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
    <title>Field repository</title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <xsl:call-template name="java-script"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <!-- NEGESO HEADER -->
     <xsl:call-template name="NegesoHeader"/>
     <div align="center">
        <!-- CONTENT -->
        <xsl:apply-templates select="negeso:page"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page">
    <!-- NavBar -->
    <xsl:call-template name="NavBar"/>    

    <!-- TITLE -->
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
        	<xsl:value-of select="java:getString($dict_job_module, 'FIELD_REPOSITORY')"/>
        </xsl:with-param>
    </xsl:call-template>
    <!-- Content -->
    	<xsl:apply-templates select="negeso:field_type_list"/>
    <!-- NavBar -->
    <xsl:call-template name="NavBar"/>    

</xsl:template>

<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:field_type_list">
    <form method="POST" name="operateForm" action="">
        <input type="hidden" name="command" value=""></input>
        <input type="hidden" name="action" value=""></input>
        <input type="hidden" name="field_id" value=""></input>

		<table cellpadding="0" cellspacing="0"  class="admNavPanel">
			<tr>
				<td class="admNavPanel admNavbar admCenter">
					<input class="admNavbarInp" name="saveButton" type="submit" onClick="addField()">
						<xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_job_module, 'ADD_FIELD')"/>&#160;&gt;</xsl:attribute>
					</input>
					&#160;
					field type
					<select name="fieldType" class="admNavbarInp">
						<option name="text">text</option>
						<option name="text">email</option>
						<option name="text">number</option>
						<option name="text">radio box</option>
						<option name="text">check box</option>
						<option name="text">select box</option>
					</select>
				</td>
			</tr>
		</table>

        <table class="admNavPanel" cellspacing="0" cellpadding="0">
            <tr>
                <td class="admNamebar"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
                <td width="30%" class="admNamebar"><xsl:value-of select="java:getString($dict_common, 'TYPE')"/></td>
                <td class="admNamebar" colspan="2">&#160;</td>
            </tr>
            
            <xsl:for-each select="negeso:field_type">
			    <tr>
			        <td class="admMainTD">
			            <a class="admAnchor" href="#" onClick = "return editField({@id})">
			                <xsl:value-of select="@title"/>&#160;
			            </a>
			        </td>
			        <td class="admDarkTD">
			            <xsl:value-of select="@type_name"/>&#160;
			        </td>
	                <td class="admLightTD admWidth30">
	                    <img src="/images/edit.gif" class="admHand" onClick="editField({@id})">
	                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
	                    </img>
	                 </td>
	                 <td class="admDarkTD admWidth30">
	                    <img src="/images/delete.gif" class="admHand" onClick="deleteField({@id})">
	                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
	                    </img>
	                </td>
			    </tr>
            </xsl:for-each>
        </table>
    </form>
</xsl:template>

</xsl:stylesheet>