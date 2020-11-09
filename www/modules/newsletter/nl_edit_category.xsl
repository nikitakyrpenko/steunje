<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2004 Negeso Ukraine

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

<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_newsletter" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_newsletter.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
	<script language="JavaScript">
	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
		function Update(targetId) {
            if ( !validateForm(operateFormId) ){
                return false;
            }
            
            document.operateForm.command.value = "nl-update-category";
        	document.operateForm.nlTargetId.value = targetId;
        	return true;
		}
		
		function onChangeLanguage() {
			document.operateForm.langId.value = document.all.langIdSelected.value;
			document.operateForm.command.value = "nl-get-edit-category-page";
			document.operateForm.nlTargetId.value = document.operateForm.nlCatId.value;
			document.operateForm.submit();
		}
		]]>
		</xsl:text>
	</script>
</xsl:template>
	
<xsl:template match="/" >
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_newsletter, 'EDIT_SUBSCRIBE_CATEGORY')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script type="text/javascript"  src="/script/jquery.min.js"/>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
    <script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"/>
	<xsl:call-template name="java-script"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
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
    <!-- NavBar -->
<form method="POST" name="operateForm" id="operateFormId" action="">
    <xsl:choose>
        <xsl:when test="count(descendant::negeso:page) = 0">
            <xsl:call-template name="NavBar">
                <xsl:with-param name="helpLink">
                    <xsl:text>/admin/help/cnl_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                </xsl:with-param>
                <xsl:with-param name="backLink" select="concat('?command=nl-browse-category&amp;pmCatId=', negeso:nl-category/@parent-id)"/>
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
	<!-- NEWSLETTER LINKS -->
    <!-- <xsl:call-template name = "nl_commons.navigation_links"/> -->

   <!-- Path -->
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
        <tr>
            <td class="admBold">
                <xsl:apply-templates select="negeso:path"/>
            </td>
        </tr>
    	<tr>
			<td>
			</td>
	    </tr>
    </table>
    <!-- TITLE -->
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
            <xsl:choose>
                <xsl:when test="/negeso:page/negeso:newsletter-category/@new='true'">
        		    <xsl:value-of select="java:getString($dict_newsletter, 'NEWSLETTER_MODULE')"/>
                    <xsl:text>. </xsl:text>
                    <xsl:value-of select="java:getString($dict_newsletter, 'NEW_SUBSCRIBE_CATEGORY')"/>
                </xsl:when>
				<xsl:otherwise>
                    <xsl:value-of select="java:getString($dict_newsletter, 'NEWSLETTER_MODULE')"/>
                    <xsl:text>. </xsl:text>
                    <xsl:value-of select="java:getString($dict_newsletter, 'EDIT_SUBSCRIBE_CATEGORY')"/>
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
 
		<table class="admNavPanel" cellspacing="0" cellpadding="0">
		    <xsl:apply-templates select="negeso:newsletter-category"/>
		</table>
		<!-- Update/reset fields -->
        <table cellpadding="0" cellspacing="0"  class="admNavPanel">
    			<tr>
    				<td class="admNavPanel admNavbar admCenter">
    				    <xsl:choose>
							    <xsl:when test="/negeso:page/negeso:newsletter-category/@new='true'">
    							        <input class="admNavbarInp" name="saveButton" onClick="return Update('{/negeso:page/negeso:newsletter-category/@id}');" type="submit">
                                            <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'ADD')"/>&#160;&gt;</xsl:attribute>
                                        </input>
                                        &#160;
                                </xsl:when>
			        		    <xsl:otherwise>
                                    <input class="admNavbarInp" name="saveButton" onClick="return Update('{/negeso:page/negeso:newsletter-category/@id}')" type="submit">
                                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SAVE')"/>&#160;&gt;</xsl:attribute>
                                    </input>
                                    &#160;
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
                <xsl:with-param name="backLink" select="concat('?command=nl-browse-category&amp;pmCatId=', negeso:newsletter-category/@parent-id)"/>
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

	
<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:newsletter-category">
    <tr>
        <td>
				<xsl:choose>
					<xsl:when test="@new='true'">
						<input type="hidden" name="nlCatId" value="{@parent-id}"></input>
						<input type="hidden" name="updateTypeField" value="insert"></input>
					</xsl:when>
					<xsl:otherwise>
						<input type="hidden" name="nlCatId" value="{@id}"></input>
						<input type="hidden" name="updateTypeField" value="update"></input>
					</xsl:otherwise>
				</xsl:choose>
				<input type="hidden" name="command" value="nl-browse-category"></input>
				<input type="hidden" name="type" value="none"></input>
				<input type="hidden" name="langId" value="{/negeso:page/@lang-id}"></input>
				<input type="hidden" name="nlTargetId" value="-1"></input>
				<fieldset  class="admFieldset">
                 <legend><xsl:value-of select="java:getString($dict_common, 'CATEGORY_PROPERTIES')"/>:</legend>
    					<table class="admNavPanel" cellspacing="0" cellpadding="0">
        					<!-- Title -->
    	    				<tr>
    		    				<td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*</td>
    			    			<td class="admLightTD admLeft">
    				    			<input class="admTextArea admWidth200" type="text" name="titleField" data_type="text" required="true" uname="Title">
    							    	<xsl:attribute name="value"><xsl:value-of select="@title"/></xsl:attribute>
        							</input>
    	    					</td>
    		    			</tr>
    			    		<!-- Mail template -->
    				    	<tr>
            					<td class="admMainTD"><xsl:value-of select="java:getString($dict_newsletter, 'MAIL_TEMPLATE')"/>*</td>
       		        			<td class="admLightTD admLeft">
                                    <xsl:apply-templates select="negeso:mail-templates"/>
            					</td>
    	    				</tr>
    			    		<!-- Feedback email -->
    				    	<tr>
            					<td class="admMainTD"><xsl:value-of select="java:getString($dict_newsletter, 'FEEDBACK_EMAIL')"/></td>
       		        			<td class="admLightTD admLeft">
    				    			<input class="admTextArea admWidth200" 
    				    				type="text" 
    				    				name="feedbackEmailField" 
    				    				data_type="email"
    				    				required="true"
    				    			>
    							       <xsl:attribute name="uname">Feedback email</xsl:attribute>
    							       <xsl:attribute name="value"><xsl:value-of select="@feedback-email"/></xsl:attribute>
        							</input>
            					</td>
    	    				</tr>
    			    		<!-- Description -->
    				    	<tr>
            					<td class="admMainTD"><xsl:value-of select="java:getString($dict_common, 'DESCRIPTION')"/></td>
        		        			<td class="admLightTD admLeft">
        				        	    <textarea rows="3" class="admTextArea admWidth335" type="text" name="descriptionField" data_type="text" uname="Description">
        				        	    	<xsl:choose>
        				        	    		<xsl:when test="@description">
		        								    <xsl:value-of select="@description"/>
		        								</xsl:when>
		        								<xsl:otherwise>
	        				        	    		&#160;
		        								</xsl:otherwise>
	        								</xsl:choose>
        							    </textarea>
            					</td>
    	    				</tr>
    					</table>
				</fieldset>
				<fieldset  class="admFieldset">
                <legend><xsl:value-of select="java:getString($dict_common, 'ADVANCED_PROPERTIES')"/>:</legend>
                <table class="admNavPanel" cellspacing="0" cellpadding="0">
                    <!-- is-i18n -->
                    <tr>
                        <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_newsletter, 'IS_MULTILINGUAL')"/></td>
                        <td class="admLightTD admLeft">
                        	<input class="admCheckBox" type="checkbox" name="isI18nField">
                    	    	<xsl:if test="@is-i18n='true'">
                    	            <xsl:attribute name="checked">true</xsl:attribute>
                              	</xsl:if> 
                            </input>
                        </td>
                        <!--<td rowspan="3" class="admLeft" valign="top"></td>-->
                    </tr>
                    <!-- Publish Date -->
                    <tr>
                        <td class="admMainTD"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/>*</td>
                        <td class="admLightTD admLeft">
                            <input class="admTextArea admWidth150" type="text" name="publishDateField" id="publishDateFieldId" data_type="date" required="true" uname="Publish date" readonly="true">
                                <!-- <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'PICK_DATE')"/></xsl:attribute>
                                -->
                            	<xsl:choose>
                            		<xsl:when test="@new='true'">
		                               	<xsl:attribute name="value"></xsl:attribute>
		                            </xsl:when>
		                            <xsl:otherwise>
		                               	<xsl:attribute name="value"><xsl:value-of select="@publish-date"/></xsl:attribute>
		                            </xsl:otherwise>
	                            </xsl:choose>
                                <img class="admHand" src="/images/calendar.gif" width="16" height="16" onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','publishDateFieldId','yyyymmdd',false)">
                                     <!-- <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'PICK_DATE')"/></xsl:attribute>
                                     -->
                                </img>
                            </input>
                            (yyyy-mm-dd)
                        </td>
                    </tr>
                    <!-- Expired Date -->
                    <tr>
                        <td class="admMainTD"><xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/></td>
                        <td class="admLightTD admLeft">
                        	<input class="admTextArea admWidth150" type="text" name="expiredDateField" id="expiredDateFieldId" data_type="date" uname="Expired date" readonly="true">
                                <!-- <xsl:attribute name="title"><xsl:value-of select="java:getString(, 'SET_DATE')"/></xsl:attribute>
                                -->
                        	        <xsl:attribute name="value"><xsl:value-of select="@expired-date"/></xsl:attribute>
                                  <img class="admHand" src="/images/calendar.gif" width="16" height="16" onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','expiredDateFieldId','yyyymmdd',false)">
                                <!-- <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'PICK_DATE')"/></xsl:attribute>
                                -->
                                  </img>
                        	</input>
                        	(yyyy-mm-dd)
                        </td>
                    </tr>
                </table>
            </fieldset>
		</td></tr>
</xsl:template>

<!-- ********************************** Language *********************************** -->
<xsl:template match="negeso:languages">
    <select name="langIdSelected" id="langIdSelected" onChange="return onChangeLanguage()" class="admWidth150">
        <xsl:if test="/negeso:page/negeso:newsletter-category/@new='true'">
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

<!-- PRODUCT LANGUAGE PRESECNE -->
<xsl:template match="negeso:newsletter-lang-presence">
    <xsl:apply-templates select="negeso:newsletter-lang"/>
</xsl:template>

<xsl:template match="negeso:newsletter-lang">
    <tr>
        <td class="admMainTD">
            <xsl:value-of select="@lang-code"/> :
        </td>
    		<td class="admLightTD" width="30%">
             <input type="checkbox" name="{@lang-id}_lang2presence">
                 <xsl:if test="@value='true'">
                     <xsl:attribute name="checked">true</xsl:attribute>
                 </xsl:if> 
             </input>
    		</td>
	</tr>
</xsl:template>

<!-- MAIL TEMPLATE -->
<xsl:template match="negeso:mail-templates">
    <select name="mailTemplateIdField" id="mailTemplateIdFieldId" class="admTextArea admWidth200">
		<xsl:apply-templates select="negeso:mail-template"/>
	</select>
</xsl:template>

<xsl:template match="negeso:mail-template">
	<option value="{@id}">
		<xsl:if test="@current='true'">
			<xsl:attribute name="selected">true</xsl:attribute>
		</xsl:if>
		<xsl:value-of select="@title"/>
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
