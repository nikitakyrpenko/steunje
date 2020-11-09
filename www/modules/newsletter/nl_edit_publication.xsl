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
<xsl:variable name="dict_product" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_product.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
	<script language="JavaScript">
        var s_DocumentIsUndefined = "<xsl:value-of select="java:getString($dict_product, 'DOCUMENT_IS_UNDEFINED')"/>";
        var s_DeleteAttachmentConfirmation = "<xsl:value-of select="java:getString($dict_newsletter, 'DELETE_ATTACHMENT_CONFIRMATION')"/>";
        var s_SendPublicationConfirmation = "Are you sure you want to send this publication?<!--<xsl:value-of select="java:getString($dict_newsletter, 'SEND_PUBLICATION_CONFIRMATION')"/>-->";
		
		<xsl:choose>
        	<xsl:when test="//negeso:page/@lang='fa'">
            	<xsl:text disable-output-escaping="yes">
                <![CDATA[
                	function preview(articleId) {
						text = document.all.item("article_text" + articleId).innerHTML;
						previewWnd = window.open("about:blank", "_blank", "resizable=yes,scrollbars=yes,status=yes,toolbar=no,menubar=no,location=no");
                        previewWnd.document.write(
					        "<html><head>" + 
							"	<META http-equiv='Content-Type' content='text/html; charset=utf-8'/>" +
							"	<link href='/site/core/css/default_styles.css' rel='stylesheet' type='text/css'/>" +
							"</head>" +
                        	"<body dir='rtl'><div class='contentStyle'>" +
                        	text + 
                        	"</div></body></html>"
                        );
                        previewWnd.document.close();
                        return false;
		        	}
                ]]>
		        </xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text disable-output-escaping="yes">
                <![CDATA[
            	    function preview(articleId) {
						text = document.all.item("article_text" + articleId).innerHTML;
						previewWnd = window.open("about:blank", "_blank", "resizable=yes,scrollbars=yes,status=yes,toolbar=no,menubar=no,location=no");
				        previewWnd.document.write(
					        "<html><head>" + 
							"	<META http-equiv='Content-Type' content='text/html; charset=utf-8'/>" +
							"	<link href='/site/core/css/default_styles' rel='stylesheet' type='text/css'/>" +
							"</head>" +
                        	"<body><div class='contentStyle'>" +
                        	text + 
                        	"</div></body></html>"
				        );
        		        return false;
		    	    }
                ]]>
		        </xsl:text>
            </xsl:otherwise>
        </xsl:choose>

	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
		function update(targetId) {
            if ( !validateForm(operateFormId) ){
                return false;
            }

            document.operateForm.command.value = "nl-update-publication";
        	document.operateForm.nlTargetId.value = targetId;
        	return true;
		}
		
		function disableSubmit(){
			document.operateForm.saveButton.disabled = true;
			document.operateForm.sendButton.disabled = true;
			document.operateForm.previewButton.disabled = true;
			document.operateForm.resetButton.disabled = true;
		}
		
		function send(targetId) {
	        if (confirm(s_SendPublicationConfirmation)) {
				document.operateForm.is_send.value = "1";
    	    	return update(targetId);
    	    }
    	    return false;
		}

		function addAttachment(id) {
			result = MediaCatalog.selectFileDialog();
			if (result != null){
				if (result.resCode == "OK"){
					if (update(id))	{
						document.operateForm.hasNewAttachment.value = "true";
						document.operateForm.attachmentLink.value = result.fileUrl;
						document.operateForm.next_page.value = "edit_page";
						document.operateForm.submit();
					}
				}
			}
		}

		function removeAttachment(pubId, attachId) {
	        if (confirm(s_DeleteAttachmentConfirmation)) {
				if (update(pubId))	{
					document.operateForm.hasRemoveAttachment.value = "true";
					document.operateForm.attachmentId.value = attachId;
					document.operateForm.next_page.value = "edit_page";
					document.operateForm.submit();
				}
			}
		}

		function onChangeMailingAction(aaa) {
			if (document.operateForm.scheduleTypeField.value == "schedule"){
				document.operateForm.publishDateField.disabled = false;
				document.all.calendarBtnId.disabled = false;
			}
			else{
				document.operateForm.publishDateField.disabled = true;
				document.all.calendarBtnId.disabled = true;
			}
			/*
			if (document.operateForm.scheduleTypeField.value == "schedule"){
				document.operateForm.publishDateField.disabled = false;
				document.operateForm.calendarBtn.disabled = false;
			}
			else{
				document.operateForm.publishDateField.disabled = true;
				document.all.calendarBtnId.disabled = true;
			}
			*/
			return true;
		}

		function selectDocumentDialog(fieldObject, aTag){
			result = MediaCatalog.selectDocumentDialog();
			if (result != null){
				if (result.resCode == "OK"){
					fieldObject.value = result.fileUrl;
					aTag.href = result.fileUrl;
					aTag.innerText = "< " + result.fileUrl + " >";
				}
			}
		}

		function openDocument(link){
			if (link != "undefined"){
				window.open(link);
				return false;
			}			
			else {
			    alert (s_DocumentIsUndefined);
			    return false;
 	        }
		}
		
		function onChangeLanguage() {
			document.operateForm.langId.value = document.all.langIdSelected.value;
			document.operateForm.command.value = "nl-get-edit-publication-page";
			document.operateForm.nlTargetId.value = document.operateForm.nlPublicationId.value;
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
    <title><xsl:value-of select="java:getString($dict_newsletter, 'EDIT_PUBLICATION')"/></title>
	   <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	   <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	   <link href="/site/core/css/default_styles" rel="stylesheet" type="text/css"/>
    <script type="text/javascript"  src="/script/jquery.min.js"/>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
	   <script type="text/javascript" src="/script/conf.js"/>
    <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
    <script language="JavaScript1.2" src="/script/media_catalog.js" type="text/javascript"/>
    <script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"/>
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
                <xsl:with-param name="backLink" select="concat('?command=nl-browse-category&amp;nlCatId=', negeso:newsletter-publication/@category-id)"/>
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
				    <xsl:when test="/negeso:page/negeso:newsletter-publication/@new='true'">
				    	<xsl:value-of select="java:getString($dict_newsletter, 'NEWSLETTER_MODULE')"/>
					    <xsl:text>. </xsl:text>
					    <xsl:value-of select="java:getString($dict_newsletter, 'CREATE_NEW_PUBLICATION')"/>
                        <!-- <xsl:value-of select="java:getString($dict_product, 'NEW_PRODUCT')"/> -->
				    </xsl:when>
				    <xsl:otherwise>
				    	<xsl:value-of select="java:getString($dict_newsletter, 'NEWSLETTER_MODULE')"/>
					    <xsl:text>. </xsl:text>
					    <xsl:value-of select="java:getString($dict_newsletter, 'EDIT_PUBLICATION')"/>
				    </xsl:otherwise>
			    </xsl:choose>
        </xsl:with-param>
    </xsl:call-template>
     <div class="admCenter">
        <font color="#FF0000">
            <xsl:value-of select="errorMessage"/>
        </font>
     </div>
     <!-- Content -->
		<xsl:if test="negeso:newsletter-publication/negeso:newsletter-category/@is-i18n='true'">
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
		</xsl:if>

	  <table class="admNavPanel" cellspacing="0" cellpadding="0">
		    <xsl:apply-templates select="negeso:newsletter-publication"/>
  	  </table>
      <!-- Update/reset fields -->
        <table cellpadding="0" cellspacing="0"  class="admNavPanel">
    			<tr>
    				<td class="admNavPanel admNavbar admCenter">
       				  <xsl:choose>
                        <xsl:when test="/negeso:page/negeso:newsletter-publication/@new='true'">
                            <input class="admNavbarInp" name="saveButton" onClick="return update('{/negeso:page/negeso:newsletter-publication/@id}');" type="submit">
                                <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'ADD')"/>&#160;&gt;</xsl:attribute>
                            </input>
                        </xsl:when>
                        <xsl:otherwise>
                            <input class="admNavbarInp" name="saveButton" onClick="return update('{/negeso:page/negeso:newsletter-publication/@id}');" type="submit">
                                <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SAVE')"/>&#160;&gt;</xsl:attribute>
                            </input>
                        </xsl:otherwise>
                    </xsl:choose>
                    <input class="admNavbarInp" type="reset" name="resetButton">
                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'RESET')"/>&#160;&gt;</xsl:attribute>
                    </input>

                    <input class="admNavbarInp" name="sendButton" onClick="return send('{/negeso:page/negeso:newsletter-publication/@id}');" type="submit">
                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SEND')"/>&#160;&gt;</xsl:attribute>
                    </input>
					
                    <input class="admNavbarInp" name="previewButton" type="submit">
                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'PREVIEW')"/>&#160;&gt;</xsl:attribute>
                        <xsl:attribute name="onClick">return preview(<xsl:value-of select="negeso:newsletter-publication/negeso:article/@id"/>)</xsl:attribute>
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
                <xsl:with-param name="backLink" select="concat('?command=nl-browse-category&amp;nlCatId=', negeso:newsletter-publication/@category-id)"/>
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

	
<!-- ********************************** PUBLICATION *********************************** -->
<xsl:template match="negeso:newsletter-publication">
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

			<input type="hidden" name="nlPublicationId" value="{@id}"></input>
			<input type="hidden" name="nlCatId" value="{@category-id}"></input>

			<input type="hidden" name="command" value="nl-browse-category"></input>
			<input type="hidden" name="type" value="none"></input>
			<input type="hidden" name="langId" value="{/negeso:page/@lang-id}"></input>
			<input type="hidden" name="nlTargetId" value="-1"></input>
			<input type="hidden" name="hasNewAttachment" value="false"></input>
			<input type="hidden" name="attachmentLink" value=""></input>
			<input type="hidden" name="hasRemoveAttachment" value="false"></input>
			<input type="hidden" name="attachmentId" value=""></input>
			<input type="hidden" name="is_send" value="0"></input>
			<input type="hidden" name="next_page" value="list_page"></input>

			<fieldset class="admFieldset">
            <legend><xsl:value-of select="java:getString($dict_newsletter, 'PUBLICATION_PROPERTIES')"/>:</legend>
				<table class="admNavPanel" cellspacing="0" cellpadding="0">

					<tr>
						<td class="admMainTD admLeft admWidth150"><xsl:value-of select="java:getString($dict_newsletter, 'SUBSCRIBE_CATEGORY')"/></td>
						<td class="admLightTD">
							<input class="admTextArea admWidth200" type="text" 
								name="titleField" 
								value="{negeso:newsletter-category/@title}"
								disabled="true"
							/>
						</td>
					</tr>


					<tr>
						<td class="admMainTD admLeft admWidth150">
							<xsl:if test="negeso:newsletter-category/@is-i18n='true'">
								(<xsl:value-of select="/negeso:page/@lang"/>)
							</xsl:if>
							<xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*
						</td>
						<td class="admLightTD">
							<input class="admTextArea admWidth200" type="text" 
								name="titleField" 
								data_type="text"
								required="true"
							>
					        	<xsl:choose>
									<xsl:when test="@new='true'">
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
						<td class="admMainTD admLeft admWidth150"><xsl:value-of select="java:getString($dict_newsletter, 'PUBLICATION_ACCESS_CODE')"/></td>
						<td class="admLightTD">
							<input class="admTextArea admWidth200" type="text" 
								name="access_code" 
								value="{@access-code}"
								readonly="true"
							/>
						</td>
					</tr>

					<tr>
						<td class="admMainTD admLeft admWidth150"><xsl:value-of select="java:getString($dict_newsletter, 'PUBLICATION_PAGE_LINK')"/></td>
						<td class="admLightTD admLeft" style="padding-left: 3px">
							<input class="admTextArea admWidth335" type="text"
								name="page-link" 
								value="{@page-link-code}"
								readonly="true"
							/>
							<xsl:if test="@page-link">
								<a href="{@page-link-code}" target="_blank"><xsl:value-of select="java:getString($dict_newsletter, 'LINK_TO_PAGE_ETC')" /><!--<xsl:value-of select="@page-link"/>--></a>
							</xsl:if>
						</td>
					</tr>

					<tr>
						<td class="admMainTD admLeft">
							<xsl:if test="negeso:newsletter-category/@is-i18n='true'">
								(<xsl:value-of select="/negeso:page/@lang"/>)
							</xsl:if>
							<xsl:value-of select="java:getString($dict_newsletter, 'TEXT')"/>*
						</td>
						<td style="background-color: #E2F2E0;border-top: 6px solid white;height: 30px;vertical-align: middle;padding: 3px; padding-bottom: 3px;">
							<xsl:apply-templates select="negeso:article"/>
						</td>
					</tr>
					
			   </table>
			</fieldset>

			<!-- SHEDULE -->
			<fieldset class="admFieldset">
            <legend><xsl:value-of select="java:getString($dict_newsletter, 'MAILING_OPTIONS')"/>:</legend>
	  	    <table class="admNavPanel" cellspacing="0" cellpadding="0">
				<tr>
					<td class="admMainTD admLeft admWidth150">Choose action</td>
					<td class="admLightTD admLeft">
						<select class="admWidth175" name="scheduleTypeField" onChange="onChangeMailingAction('{@status}')">
							<option value="suspend"><xsl:value-of select="java:getString($dict_newsletter, 'SUSPEND')"/></option>
							<option value="schedule"><xsl:value-of select="java:getString($dict_newsletter, 'SCHEDULE')"/>
		                        <xsl:attribute name="selected">1</xsl:attribute>
							</option>
						</select>
					</td>
				</tr>

				<tr>
					<td class="admMainTD admLeft admWidth150"><xsl:value-of select="java:getString($dict_newsletter, 'SCHEDULE')"/>
					</td>
					<td class="admLightTD admLeft">
			            <input class="admTextArea admWidth175" type="text" name="publishDateField" id="publishDateFieldId" readonly="true" data_type="text" required="true" uname="Publish date" >
			                <xsl:attribute name="value"><xsl:value-of select="@publish-date"/></xsl:attribute>
			                <img class="admHand" id="calendarBtnId" src="/images/calendar.gif" width="16" height="16" onclick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','publishDateFieldId','yyyymmdd',true)"/>
			            </input>
			            (yyyy-mm-dd hh:mm:ss)
					</td>
				</tr>
				
            </table>
			</fieldset>

			<!-- ATTACHMENTS -->
			<fieldset class="admFieldset">
	            <legend><xsl:value-of select="java:getString($dict_newsletter, 'ATTACHMENTS')"/>:
		    		<img src="/images/new_attachment.gif" class="admHand" onClick="return addAttachment({@id})">
		                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'ADD')"/></xsl:attribute>
 		            </img>
	            </legend>

		  	    <table class="admNavPanel" cellspacing="0" cellpadding="0">
					<xsl:apply-templates select="negeso:newsletter-attachments"/>
		        </table>
			</fieldset>
	</td></tr>
</xsl:template>

<xsl:template match="negeso:newsletter-attachments">
	<xsl:apply-templates select="negeso:newsletter-attachment"/>
</xsl:template>

<xsl:template match="negeso:newsletter-attachment">
	<tr>
    	<td class="admMainTD">
    		<a href="#" class="admAnchor" onClick="return openDocument(&quot;{@link}&quot;)">
    			<xsl:value-of select="@link"/>
    		</a>
    	</td>
    	<td class="admDarkTD admWidth30">
    		<img src="/images/delete.gif" class="admHand" onClick="return removeAttachment({@publication-id}, {@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
         </img>
    	</td>
	</tr>
</xsl:template>

<!-- PRODUCT LANGUAGE PRESECNE -->
<xsl:template match="negeso:pm-lang-presence">
    <xsl:apply-templates select="negeso:pm-lang"/>
</xsl:template>

<xsl:template match="negeso:pm-lang">
    <tr>
        <td class="admMainTD admRight">
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
		<xsl:attribute name="style">behavior:url(/script/article3.htc); width: 100%;</xsl:attribute>
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
    <select name="langIdSelected" id="langIdSelected" onChange="onChangeLanguage()" class="admWidth150">
        <xsl:if test="/negeso:page/negeso:newsletter-publication/@new='true'">
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
