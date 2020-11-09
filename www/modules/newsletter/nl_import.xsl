<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Browse subscribers
 
  @version      $Revision$
  @author       Dmitry.Dzifuta
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<!-- Include/Import -->
<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_newsletter" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_newsletter.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->
<xsl:template name="java-script">
    <script language="JavaScript">
        var s_SubscribtionConfirmation = "<xsl:value-of select="java:getString($dict_newsletter, 'NL_IMPORT_CONFIRM')"/>";

        <xsl:text disable-output-escaping="yes">
        <![CDATA[
		function subscriberAdding() {
	            if (confirm(s_SubscribtionConfirmation)) {
			    document.operateForm.mode.value="3";
			    document.operateForm.update_anyway.value="true";
		            document.operateForm.submit();
	            }
	        }
        ]]>
        </xsl:text>
    </script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_newsletter, 'BROWSE_SUBSCRIBERS')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <xsl:call-template name="java-script"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript"  src="/script/jquery.min.js"/>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
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
    <xsl:call-template name="NavBar">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cnl_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>
    </xsl:call-template>
    
    <!-- Path -->
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
        <tr>
            <td class="admBold">
                <xsl:apply-templates select="negeso:path"/>
            </td>
            <td class="admStatusMessage" align="center">
                <xsl:value-of select="@status-message"/>
            </td>
        </tr>
    </table>
    <!-- TITLE -->
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
            <xsl:value-of select="java:getString($dict_newsletter, 'IMPORT')"/>
        </xsl:with-param>
    </xsl:call-template>
    <!-- Content -->
	<xsl:call-template name="nl_import_show_errors"/>


    <form method="POST" name="operateForm" action="" enctype="multipart/form-data" onsubmit="return validateForm(operateForm)">
		<input type="hidden" name="command" value="nl-import"></input>
		<input type="hidden" name="update_anyway" value="false"></input>

		<xsl:call-template name="nl_import_show_functinality"/>

		<xsl:apply-templates select="negeso:newsletter-category" mode="category"/>
	</form>

	<xsl:if test="/negeso:page/@result='subscr_exists'">
	    <script language="JavaScript">
			subscriberAdding();
	    </script>	
	</xsl:if>

    <!-- NavBar -->
    <xsl:call-template name="NavBar">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cnl_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>
    </xsl:call-template>

</xsl:template>

<xsl:template name="nl_import_show_errors">
	<div class="admRed">
		<xsl:if test="/negeso:page/@result='success'">
			<xsl:value-of select="java:getString($dict_newsletter, 'IMPORT_SUCCEDED')"/>
		</xsl:if>
		<xsl:if test="/negeso:page/@result='import_failed'">
			<xsl:value-of select="java:getString($dict_newsletter, 'IMPORT_FAILED')"/>
		</xsl:if>
		<xsl:if test="/negeso:page/@result='import_limit_exceeded'">
			<xsl:text>Sorry, your import limit per month exceed.</xsl:text>
		</xsl:if>
		<xsl:if test="/negeso:page/@result='select_category'">
			<xsl:value-of select="java:getString($dict_newsletter, 'SELECT_CATEGORY_MSG')"/>
		</xsl:if>
	</div>
	<xsl:if test="/negeso:page/@maxImport!='' and /negeso:page/@curImport!=''">
		<xsl:text>Van de maximaal </xsl:text>
		<xsl:value-of select="/negeso:page/@maxImport"/>
		<xsl:text> adressen heeft u reeds </xsl:text>
		<xsl:value-of select="/negeso:page/@curImport"/>
		<xsl:text> adressen ge&#239;mporteerd.</xsl:text>
	</xsl:if>
	<xsl:if test="/negeso:page/@importDone!=''">
		<xsl:text>You have imported </xsl:text>
		<xsl:value-of select="/negeso:page/@importDone"/>
		<xsl:text> records</xsl:text>
	</xsl:if>
</xsl:template>

<xsl:template name="nl_import_show_functinality">
	<table width="100%" class="admNavPanel" border="0">
		<tr>
			<td class="admMainTD">
				<input type="radio" NAME="mode" value="1" onclick="file.disabled=false;singleEmail.disabled=true;"/>
				<xsl:value-of select="java:getString($dict_newsletter, 'ADD_EMAILS')"/>
				<br/>
				<input type="radio" NAME="mode" value="2" onclick="file.disabled=false;singleEmail.disabled=true;"/>
				<xsl:value-of select="java:getString($dict_newsletter, 'OVERWRITE_EMAILS')"/>
			</td>
			<td class="admMainTD">
				<input type="file" NAME="file" class="admTextArea admWidth335" disabled="true" onKeyPress="return false;"/>
			</td>
		</tr>
		<tr>
			<td class="admMainTD">
				<input type="radio" NAME="mode" value="3" checked="true" onclick="file.disabled=true;singleEmail.disabled=false;"/>
				<xsl:value-of select="java:getString($dict_newsletter, 'ADD_SINGLE_EMAIL')"/>
			</td>
			<td class="admMainTD">
					<input type="text" NAME="singleEmail" value="{@single-email}" class="admTextArea admWidth335" data_type="email"/>
			</td>
		</tr>
		<tr>
			<td class="admNavbar admCenter" colspan="2" >
				<input type="submit" class="admNavbarInp" >
					<xsl:if test="/negeso:page/@result='import_limit_exceeded'">
						<xsl:attribute name="disabled">true</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_newsletter, 'IMPORT')"/>&#160;&gt;</xsl:attribute>
				</input>
			</td>
		</tr>
	</table>
</xsl:template>

<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:newsletter-category" mode="category">
    <xsl:choose>
        <xsl:when test="@position='current'">
            <!-- Render HEADER -->

	    		<table class="admNavPanel" cellspacing="0" cellpadding="0">
                <tr>
                    <td class="admNamebar admOrder"></td>
                    <td class="admNamebar">&#160;</td>
                    <td class="admNamebar"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_newsletter, 'MULTILINGUAL')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/></td>
                    <td class="admNamebar admAction"><xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/></td>
                </tr>
                <xsl:apply-templates select="negeso:newsletter-category" mode="category"/>
           	</table>
        </xsl:when>
        <xsl:otherwise>
            <tr>
                <td class="admLightTD" colspan="2">
							<INPUT TYPE="checkbox" NAME="select_{@id}">
								<xsl:if test="@selected">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>
							</INPUT>
					 </td>
                <td class="admMainTD">
							<xsl:value-of select="@title" disable-output-escaping="yes"/>
                </td>
                <td class="admDarkTD"><xsl:value-of select="@multilingual"/></td>
                <td class="admLightTD"><xsl:value-of select="@publish-date"/></td>
                <td class="admDarkTD"><xsl:value-of select="@expired-date"/></td>
            </tr>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


</xsl:stylesheet>
