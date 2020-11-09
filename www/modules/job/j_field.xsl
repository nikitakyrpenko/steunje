<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${j_applicant.xsl}       
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a job module applicant.
 
  @version    2005/03/23
  @author     Volodymyr Snigur
  @author     
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
	    var s_DeleteFieldConfirmation = "<xsl:value-of select="java:getString($dict_job_module, 'DELETE_FIELD_CONFIRMATION')"/>";

	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
	    
	    function update(targetId) {
            if ( !validateForm(operateForm) ){
                return false;
            }
        	return true;
		}
	    

        function deleteOption(targetId) {
            if (confirm(s_DeleteFieldConfirmation)) {
	            document.operateForm.action.value = "delete_option";
		        document.operateForm.option_id.value = targetId;
	            document.operateForm.submit();
		        return true;
	        }
	        return false;
        }

        function addOption() {
            document.operateForm.action.value = "add_option";
            document.operateForm.field_type_id.value="1";
            document.operateForm.submit();
        }

        function save() {
            document.operateForm.action.value = "save_application_form";
            document.operateForm.submit();
        }
		
        function onChangeLanguage() {
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
            <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
            <script type="text/javascript"  src="/script/jquery.min.js"/>
			<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
            <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
            <xsl:call-template name="java-script"/>
        </head>
        <body>
            <!-- NEGESO HEADER -->
            <xsl:call-template name="NegesoHeader">
                <xsl:with-param name="helpLink" select="''"/>
            </xsl:call-template>
            <div align="center">
                <!-- CONTENT -->
                <xsl:call-template name="content_part" />
            </div>
        </body>
    </html>
</xsl:template>

<xsl:template name="content_part">
    <!-- NavBar -->
    <xsl:call-template name="NavBar">
        <xsl:with-param name="backLink">
            <xsl:text>j_module</xsl:text>
        </xsl:with-param>
    </xsl:call-template>
    <!-- TITLE -->
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
        	<xsl:value-of select="java:getString($dict_job_module, 'EDIT_FIELD')"/>
        </xsl:with-param>
    </xsl:call-template>
    
    
    <!-- Content -->
    <form method="post" name="operateForm" action="" onsubmit="return validateForm(operateForm)">
        <input type="hidden" name="command" value=""></input>
        <input type="hidden" name="action" value=""></input>
        <input type="hidden" name="field_id" value=""></input>
        <input type="hidden" name="field_lang_id" value=""></input>
        <!-- Update/reset fields -->
		<table cellpadding="0" cellspacing="0" class="admNavPanel">
			<tr>
				<td class="admNavPanel admNavbar admCenter">
					<input class="admNavbarInp" name="saveButton" type="submit" onClick="save()">
						<xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SAVE')"/>&#160;&gt;</xsl:attribute>
					</input>
				</td>
				<td class="admNavPanel admNavbar admLeft">
					<xsl:apply-templates select="negeso:languages"/>
				</td>
			</tr>
		</table>
        
	    <xsl:apply-templates select="negeso:field"/>
	</form>
	
	
    <!-- NavBar -->
    <xsl:call-template name="NavBar">
        <xsl:with-param name="backLink">
            <xsl:text>j_module</xsl:text>
        </xsl:with-param>
    </xsl:call-template>
</xsl:template>

<xsl:template match="negeso:field">
	<tr>
		<td class="admMainTD admLeft">
			<xsl:value-of select="java:getString($dict_common, 'TITLE')"/>
		</td>
		<td class="admLightTD">
			<input class="admTextArea admWidth200"
				type="text" 
				name="title"
				data_type="text" 
				required="true" 
				uname="Title"
				value="{@title}"
			/>
		</td>
	</tr>
	<tr>
		<td class="admMainTD admLeft">
			<xsl:value-of select="java:getString($dict_common, 'TYPE')"/>
		</td>
		<td class="admLightTD">
			<select name="type_name" class="admNavbarInp">
				<option name="text">text</option>
				<option name="text">email</option>
				<option name="text">number</option>
			</select>
		</td>
	</tr>
</xsl:template>

<xsl:template match="negeso:field[@type_name='radio_box' or @type_name='check_box' or @type_name='select_box']">
	<tr>
		<td class="admMainTD admLeft">
			<xsl:value-of select="java:getString($dict_common, 'TITLE')"/>
		</td>
		<td class="admLightTD">
			<input class="admTextArea admWidth200" name="title" type="text" value="{@title}"/>
		</td>
	</tr>
	<tr>
		<td class="admMainTD admLeft">
			<xsl:value-of select="java:getString($dict_job_module, 'J_OPTIONS')"/>
		</td>
		<td class="admLightTD admLeft">
			<a href="#">
				Add option : <input class="admTextArea admWidth200" name="option" onClick=" addOption()" type="text" value="New option"/>
			</a>
			<br/>
			<table>
			<xsl:for-each select="negeso:option">
				<tr>
					<td>
						<input type="radio" name="default_{../@id}">
							<xsl:if test="@selected">
								<xsl:attribute name="checked">true</xsl:attribute>
							</xsl:if>
							&#160;
						</input>
						<input class="admTextArea admWidth200" name="option_{@id}" type="text" value="{@title}"/>
					</td>
	                 <td class="admDarkTD admWidth30">
	                    <img src="/images/delete.gif" class="admHand" onClick="deleteOption({@id})">
	                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
	                    </img>
	                </td>
				</tr>
			</xsl:for-each>
			</table>
		</td>
	</tr>
	<tr>
		<td class="admMainTD admLeft">
			<xsl:value-of select="java:getString($dict_common, 'TYPE')"/>
		</td>
		<td class="admLightTD">
			<select name="type_name" class="admNavbarInp">
				<option name="text">radio box</option>
				<option name="text">check box</option>
				<option name="text">select box</option>
			</select>
		</td>
	</tr>
</xsl:template>


<!--******************** LANGUAGE ********************-->
<xsl:template match="negeso:languages">
    <select name="langIdSelected" id="langIdSelected" onChange="return onChangeLanguage()" class="admWidth150">
        <xsl:if test="/negeso:page/negeso:department/@new='true'">
            <xsl:attribute name="disabled">true</xsl:attribute>
        </xsl:if>
			<xsl:apply-templates select="negeso:language"/>
	 </select>
</xsl:template>

<xsl:template match="negeso:language">
	<option value="{@id}">
		<xsl:if test="@selected">
			<xsl:attribute name="selected">true</xsl:attribute>
		</xsl:if>
		<xsl:value-of select="@code"/>
		<xsl:if test="@default='true'">
			(<xsl:value-of select="java:getString($dict_common, 'DEFAULT')"/>)
		</xsl:if>
	</option>
</xsl:template>


<xsl:template name="EDIT_FIELD">Edit field</xsl:template>
<xsl:template name="OPTIONS">OPTIONS</xsl:template>

</xsl:stylesheet>
