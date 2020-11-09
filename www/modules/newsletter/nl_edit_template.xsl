<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2003 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  View of a list item edit interface.

  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">
        
<xsl:output method="html"/>
<xsl:include href="/xsl/negeso_header.xsl"/>
<xsl:include href="/xsl/admin_templates.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_newsletter" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_newsletter.xsl', $lang)"/>
<xsl:variable name="dict_newsletter_const" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('newsletter_module', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
	<script language="JavaScript">

	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
		var insertFormDisabled = true;
		
		function updateToAllLangs(targetId) {
            
            if ( !validateForm(operateFormId) ){
                return false;
            }

            document.operateForm.command.value = "nl-update-template";
        	document.operateForm.nlTargetId.value = targetId;
        	return true;
		}
		
		function update(targetId) {
            
            if ( !validateForm(operateFormId) ){
                return false;
            }

            document.operateForm.command.value = "nl-update-template";
        	document.operateForm.nlTargetId.value = targetId;
        	return true;
		}
		
		function onChangeLanguage() {
			document.operateForm.langId.value = document.all.langIdSelected.value;
			document.operateForm.command.value = "nl-get-edit-template-page";
			document.operateForm.submit();
		}
		]]>
		</xsl:text>
	</script>
    <xsl:call-template name="adminhead"/>
</xsl:template>
	
<xsl:template match="/" >
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_newsletter, 'EDIT_MAIL_TEMPLATE')"/></title>
	<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	<link href="/site/core/css/default_styles.css" rel="stylesheet" type="text/css"/>
 <script type="text/javascript"  src="/script/jquery.min.js"/>
 <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
 <script type="text/javascript" src="/script/conf.js"/>
 <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
	<xsl:call-template name="java-script"/>
</head>
<body
    style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
    id="ClientManager" xmlID="{@id}"
>
    <!-- NEGESO HEADER -->
	   <xsl:call-template name="NegesoHeader">
     <xsl:with-param name="helpLink">
         <xsl:text>/admin/help/cnl1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
     </xsl:with-param>
    </xsl:call-template>
    <div align="center">
        <!-- CONTENT -->
        <xsl:apply-templates select="negeso:page"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page">
    <form method="post" id="operateFormId" name="operateForm" action="" enctype="multipart/form-data">

    <!-- NavBar -->
    <xsl:choose>
        <xsl:when test="count(descendant::negeso:page) = 0">
            <xsl:call-template name="NavBar">                
                <xsl:with-param name="helpLink">
                    <xsl:text>/admin/help/cnl_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                </xsl:with-param>
                <xsl:with-param name="backLink" select="concat('?command=nl-browse-template', '')"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="NavBar">
                <xsl:with-param name="helpLink">
                    <xsl:text>/admin/help/cnl_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:otherwise>
   </xsl:choose>
    <!-- Path -->
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
        <tr>
            <td class="admBold">
                <xsl:apply-templates select="negeso:path"/>
            </td>
        </tr>
    </table>
    <!-- TITLE -->
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
            <xsl:choose>
				    <xsl:when test="/negeso:page/negeso:mail-template/@new='true'">
				    	<xsl:value-of select="java:getString($dict_newsletter, 'NEWSLETTER_MODULE')"/>
					    <xsl:text>. </xsl:text>
					    <xsl:value-of select="java:getString($dict_newsletter, 'NEW_MAIL_TEMPLATE')"/>
				    </xsl:when>
				    <xsl:otherwise>
				    	<xsl:value-of select="java:getString($dict_newsletter, 'NEWSLETTER_MODULE')"/>
					    <xsl:text>. </xsl:text>
					    <xsl:value-of select="java:getString($dict_newsletter, 'EDIT_MAIL_TEMPLATE')"/>
				    </xsl:otherwise>
			    </xsl:choose>
        </xsl:with-param>
    </xsl:call-template>
     <div class="admCenter">
        <font color="#FF0000">
            <xsl:value-of select="errorMessage"/>
        </font>
     </div>
   		<table cellpadding="0" cellspacing="0" class="admNavPanel">
    		<tr>
    			<td width="80%" class="admNavbar admRight">
				    <xsl:value-of select="java:getString($dict_common, 'LANGUAGE_VERSION')"/>
               </td>
    			<td width="20%" class="admNavbar admRight">
    			    <xsl:apply-templates select="negeso:languages"/>
               </td>
    		</tr>
        </table>
     
     <!-- Content -->
	  <table class="admNavPanel" cellspacing="0" cellpadding="0">
		    <xsl:apply-templates select="negeso:mail-template"/>
  	  </table>
      <!-- Update/reset fields -->
        <table cellpadding="0" cellspacing="0"  class="admNavPanel">
        	<tr>
    			<td class="admNavPanel admNavbar admCenter">
       				<xsl:choose>
                    	<xsl:when test="/negeso:page/negeso:mail-template/@new='true'">
                    		<input class="admNavbarInp" name="copyAllLanguages" type="checkbox" checked="true" disabled="true" value="true"> 
                           		<xsl:value-of select="java:getString($dict_newsletter_const, 'FOR_ALL_LANGS_CONST')"/>
                           	</input>
                        	<input class="admNavbarInp" name="saveButton" onClick="return update('{/negeso:page/negeso:newsletter-publication/@id}');" type="submit">
                                <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'ADD')"/>&#160;&gt;</xsl:attribute>
                            </input>
                        </xsl:when>
                        <xsl:otherwise>
                        	<input class="admNavbarInp" name="copyAllLanguages" type="checkbox" value="true"> 
	                       		 <xsl:value-of select="java:getString($dict_newsletter_const, 'FOR_ALL_LANGS_CONST')"/> 
	                       	</input>
                        	<input class="admNavbarInp" name="saveButton" onClick="return update('{/negeso:page/negeso:newsletter-publication/@id}');" type="submit">
                                <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SAVE')"/>&#160;&gt;</xsl:attribute>
                            </input>
                        </xsl:otherwise>
                    </xsl:choose>
                    <input class="admNavbarInp" type="reset">
                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'RESET')"/>&#160;&gt;</xsl:attribute>
                	</input>
    			</td>
    		</tr>
    	</table>
	  <!-- NavBar -->
     <xsl:choose>
        <xsl:when test="count(descendant::negeso:page) = 0">
            <xsl:call-template name="NavBar">
                <xsl:with-param name="helpLink">
                    <xsl:text>/admin/help/cnl_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                </xsl:with-param>
                <xsl:with-param name="backLink" select="concat('?command=nl-browse-template', '')"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="NavBar">
                <xsl:with-param name="helpLink">
                    <xsl:text>/admin/help/cnl_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:otherwise>
     </xsl:choose>
     </form>
</xsl:template>

	
<!-- ********************************** TEMPLATE *********************************** -->
<xsl:template match="negeso:mail-template">
    <tr>
        <td>
        
        	<xsl:choose>
				<xsl:when test="@new='true'">
					<input type="hidden" name="updateTypeField" value="insert"></input>
				</xsl:when>
				<xsl:otherwise>
					<input type="hidden" name="updateTypeField" value="update"></input>
				</xsl:otherwise>
			</xsl:choose>

			<input type="hidden" name="command" value="nl-browse-template"></input>
			<input type="hidden" name="type" value="none"></input>
			<input type="hidden" name="langId" value="{/negeso:page/@lang-id}"></input>
			<input type="hidden" name="nlTemplateId" value="{@id}"></input>
			<input type="hidden" name="nlTargetId" value="-1"></input>
			<input type="hidden" name="isForAllLangs" value="none"></input>
			<table class="admNavPanel" cellspacing="0" cellpadding="0">

				<tr>
					<td class="admMainTD admLeft admWidth150"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*</td>
					<td class="admLightTD">
						<input class="admTextArea admWidth200" type="text" 
							name="titleField" 
							value="{@title}"
							data_type="text"
							required="true"
						>
							<xsl:choose>
							<xsl:when test="@title='_unknown_'">
								<xsl:attribute name="value"></xsl:attribute>
							</xsl:when>
							<xsl:otherwise>
								<xsl:attribute name="value"><xsl:value-of select="@title"/></xsl:attribute>
							</xsl:otherwise>
							</xsl:choose>
							
							<xsl:attribute name="uname"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></xsl:attribute>
						</input>
					</td>
				</tr>

				<tr>
					<td class="admMainTD admLeft"><xsl:value-of select="java:getString($dict_newsletter, 'TEXT')"/></td>
					<td style="background-color: #E2F2E0;border-top: 6px solid white;height: 30px;vertical-align: middle;padding: 3px; padding-bottom: 3px;">
						<xsl:apply-templates select="negeso:article"/>
					</td>
				</tr>
				
		   </table>
	</td></tr>
</xsl:template>


<!-- ********************************** Article *********************************** -->
<xsl:template match="negeso:article" >
        <xsl:choose>
           <xsl:when test="//negeso:page/@lang='fa'">
                <img src="/images/mark_1.gif" onclick="RTE_Init('article_text{@id}', 'article_text{@id}', '{@id}', 3, 1, 'contentStyle', getInterfaceLanguage(), true, true)" class="admBorder admHand" alt="Edit a description"/>
           </xsl:when>
           <xsl:otherwise>
                <img src="/images/mark_1.gif" onclick="RTE_Init('article_text{@id}', 'article_text{@id}', '{@id}', 3, 1, 'contentStyle', getInterfaceLanguage(), true, true)" class="admBorder admHand" alt="Edit a description"/>
          </xsl:otherwise>
        </xsl:choose>
	<div id="article_text{@id}" class="contentStyle">
		<xsl:attribute name="style">behavior:url(/script/article3.htc);</xsl:attribute>
		<xsl:choose>
                   <xsl:when test="//negeso:page/@lang='fa'">
                        <xsl:attribute name="dir">rtl</xsl:attribute>
                   </xsl:when>
                   <xsl:otherwise>
                        <xsl:attribute name="dir">ltr</xsl:attribute>
                   </xsl:otherwise>
                </xsl:choose>
		<xsl:choose>
			<xsl:when test="negeso:text/text()">
				<xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</div>
</xsl:template>


<!-- ********************************** Language *********************************** -->
<xsl:template match="negeso:languages">
    <select name="langIdSelected" id="langIdSelected" onChange="return onChangeLanguage()" class="admWidth150">
        <xsl:if test="/negeso:page/negeso:mail-template/@new='true'">
            <xsl:attribute name="disabled">true</xsl:attribute>
        </xsl:if>
			<xsl:apply-templates select="negeso:language"/>
	</select>
</xsl:template>
	
<xsl:template match="negeso:language">
	<option value="{@id}">
		<xsl:if test="@code=/negeso:page/@lang">
			<xsl:attribute name="selected">true</xsl:attribute>
		</xsl:if>
		<xsl:value-of select="@code"/>
		<xsl:if test="@default='true'">
			(<xsl:value-of select="java:getString($dict_common, 'DEFAULT')"/>)
		</xsl:if>
	</option>
</xsl:template>

<!-- ********************************** Path *********************************** -->
<xsl:template match="negeso:path">
	<xsl:apply-templates select="negeso:path-element"/>
</xsl:template>

<xsl:template match="negeso:path-element">
	<xsl:choose>
		<xsl:when test="@active='true'">
			<!-- Active pathe element - just print it-->
			<span class="admSecurity admLocation"><xsl:value-of select="@title"/></span>
		</xsl:when>
		<xsl:otherwise>
			<!-- Unactive pathe element - make it link-->
			<span class="admZero admLocation">
            <a class="admLocation" href="{@link}">
            	<xsl:value-of select="@title"/> 
            </a>
            &#160;&gt;
			</span>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
	
<!-- ******************************* Error message ********************************** -->
<xsl:template match="errorMessage">
    <xsl:value-of select="errorMessage"/>
</xsl:template>

</xsl:stylesheet>
