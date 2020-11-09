<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Product Module
  
  Main file.
 
  @version		2007.12.06
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:template name="pm_mail_to">
	<a href="?pmMailProduct=on&amp;pmMailMode=getForm&amp;pmMailToProductId={@id}" class="pmMailToFriendRedLink bold">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_pm_module"/>
			<xsl:with-param name ="name"  select="'PM_MAIL_TO_FRIEND'"/>
		</xsl:call-template>
		<xsl:value-of select="java:getString($dict_pm_module, 'PM_MAIL_TO_FRIEND')"/>
	</a>
</xsl:template> 


<xsl:template name="pm_mail_to_friend">
	<!-- Current tag is negeso:pm -->
	<xsl:call-template name="pm_mail_content" />
</xsl:template>

<xsl:template name="pm_mail_content">
	<!-- Current tag is negeso:pm -->
	<xsl:choose>
		<!--============ In case we need to show Mail-To-A-friend form ============-->
		<xsl:when test="@view-type='mail-to-a-friend-form'">
			<xsl:apply-templates select="negeso:pm-product" mode="pm_mail_to"/>
		</xsl:when>
		<!-- Mailed to a friend successfull -->
		<xsl:when test="(@view-type='mail-to-a-friend-result') and (@result='success')">
			<xsl:call-template name="pm_mail_to_success"/>
		</xsl:when>
		<!-- Mailed to a friend with error -->				
		<xsl:when test="(@view-type='mail-to-a-friend-result') and (@result='error')">
			<xsl:call-template name="pm_mail_to_error"/>
		</xsl:when>
		<!--============ In case we need to show Mail-To-A-friend form ============-->					
	</xsl:choose>
</xsl:template>

<!--===================================== PM MAIL-TO-A-FRIEND FORM ======================================-->    

<xsl:template match="negeso:pm-product" mode="pm_mail_to">
	<script type="text/javascript" src="/site/core/script/validation.js">/**/</script>
	<script type="text/javascript">
		function send_mail_to_friend(formObj) {
			if (validate(formObj)) {
				var page_link = formObj.elements["pmMailToPmPageLink"];
				if (typeof(page_link) != "undefined" &amp;&amp; page_link != null)
					page_link.value = window.location.protocol+"//"+window.location.host+"/"+"<xsl:call-template name="pm_module_link"/>";
				formObj.submit();
			}
		}
	</script>
	<table class="height_460 pmMailToFriendBody" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td class="top">
				<div class="pmMailToFriendBodyTop">
					<xsl:call-template name="page_title_line_block">
						<xsl:with-param name="width" select="'917px'"/>
						<xsl:with-param name="title">
							<xsl:value-of select="java:getString($dict_pm_module, 'PM_MAIL_TO_FRIEND')"/>
						</xsl:with-param>
					</xsl:call-template>
				</div>
			</td>
		</tr>
		<tr>
			<td class="top">
				<form method="post" action="" id="pmMailToFormId" name="pmMailToForm" enctype="multipart/form-data">
					<input type="hidden" name="pmMailProduct" value="on"/>
					<input type="hidden" name="pmMailMode" value="submitForm"/>
					<input type="hidden" name="pmMailToProductId" value="{@id}"/>
					<input type="hidden" name="pmMailToPmPageLink" value=""/>
					<input type="hidden" name="pmMailToSiteLink" value=""/>
					
					<table cellpadding="0" cellspacing="0" border="0" align="center" class="pmMailToFriend">
						<tr style="height: 40px">
							<td class="bold middle" colspan="2">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_YOUR_DATA'"/>
								</xsl:call-template>
					       			<xsl:value-of select="java:getString($dict_pm_module, 'PM_YOUR_DATA')"/>:
							</td>
						</tr>
						<tr>
							<td style="width: 200px">
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_NAME')"/>
							</td>
							<td>
								<input type="text" name="pmFromPerson" style="width: 200px" value=""/>
							</td>
						</tr>
						<tr>
							<td>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_EMAIL_ADRES'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_EMAIL_ADRES')"/><span class="red"> *</span>
							</td>
							<td>
								<input type="text" name="pmFromEmail" style="width: 200px" value="" required="true" is_email="true" />
							</td>
						</tr>
						<tr>
							<td class="bold" colspan="2">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_FRIEND_DATA'"/>
								</xsl:call-template>
					       			<xsl:value-of select="java:getString($dict_pm_module, 'PM_FRIEND_DATA')"/>:
							</td>
						</tr>
						<tr>
							<td>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_NAME'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_NAME')"/>
							</td>
							<td>
								<input type="text" name="pmToPerson" style="width: 200px" value=""/>
							</td>
						</tr>
						<tr>
							<td>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_EMAIL_ADRES'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_EMAIL_ADRES')"/><span class="red"> *</span>
							</td>
							<td>
								<input type="text" name="pmToEmail" style="width: 200px" value="" required="true" is_email="true" />
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<small class="red">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_REQUIRED'"/>
									</xsl:call-template>
									* <xsl:value-of select="java:getString($dict_pm_module, 'PM_REQUIRED')"/></small>
							</td>
						</tr>
						<tr>
							<td class="bold" colspan="2">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_MESSAGE_COMMENTS'"/>
								</xsl:call-template>
					       			<xsl:value-of select="java:getString($dict_pm_module, 'PM_MESSAGE_COMMENTS')"/>:
							</td>
						</tr>
						<tr>
							<td>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_SUBJECT'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_pm_module, 'PM_SUBJECT')"/>
							</td>
							<td>
								<input type="text" name="pmMailSubj" style="width: 200px" value=""/>
								<input type="hidden" name="pmMailTextType" value="text/html" />
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<textarea name="pmMailText" rows="4" cols="48" style="width: 420px">&#160;</textarea>
							</td>
						</tr>
						<tr style="height: 40px;">
							<td colspan="2" style="padding-top: 6px">
								<input type="button" style="margin-left: 0; width: 210px;" class="submit" onClick="send_mail_to_friend(this.form)">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_SEND'"/>
									</xsl:call-template>
									<xsl:attribute name="value"><xsl:value-of select="java:getString($dict_pm_module, 'PM_SEND')"/></xsl:attribute>
								</input>
								<input type="button" class="submit" style="width: 200px;">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_pm_module"/>
										<xsl:with-param name ="name"  select="'PM_BACK_TO_PRODUCT_DETAILS'"/>
									</xsl:call-template>
									<xsl:attribute name="value"><xsl:value-of select="java:getString($dict_pm_module, 'PM_BACK_TO_PRODUCT_DETAILS')"/></xsl:attribute>
									<xsl:attribute name="onclick">window.location.href='<xsl:call-template name="pm_module_link"><xsl:with-param name="addition" select="concat('?pmProductId=',@id)" /></xsl:call-template>'</xsl:attribute>
								</input>
							</td>
						</tr>
					</table>
				</form>
			</td>
		</tr>
	</table>
	
</xsl:template> 

<xsl:template name="pm_mail_to_success">
	<table class="height_460 pmMailToFriendBody" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td class="top">
				<div class="pmMailToFriendBodyTop">
					<xsl:call-template name="page_title_line_block">
						<xsl:with-param name="width" select="'917px'"/>
						<xsl:with-param name="title">
							<xsl:value-of select="@message"/>
						</xsl:with-param>
					</xsl:call-template>
				</div>
				<table cellpadding="0" cellspacing="0" border="0" align="center" class="pmMailToFriend">
					<tr>
						<td>
							<br/>
							<input type="button" class="submit">
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="dict"  select="$dict_pm_module"/>
									<xsl:with-param name ="name"  select="'PM_BACK_TO_PRODUCT_DETAILS'"/>
								</xsl:call-template>
								<xsl:attribute name="value"><xsl:value-of select="java:getString($dict_pm_module, 'PM_BACK_TO_PRODUCT_DETAILS')"/></xsl:attribute>
								<xsl:attribute name="onclick">window.location.href='<xsl:call-template name="pm_module_link"><xsl:with-param name="addition" select="concat('?pmProductId=',/negeso:page/negeso:request/negeso:parameter[@name='pmMailToProductId']/negeso:value/text())" /></xsl:call-template>'</xsl:attribute>
							</input>
							<br/>
							<br/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</xsl:template> 

<xsl:template name="pm_mail_to_error">
	<table class="height_460 pmMailToFriendBody" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td class="top">
				<div class="pmMailToFriendBodyTop">
					<xsl:call-template name="page_title_line_block">
						<xsl:with-param name="width" select="'917px'"/>
						<xsl:with-param name="title">
							<xsl:value-of select="@message"/>
						</xsl:with-param>
					</xsl:call-template>
				</div>
				<table cellpadding="0" cellspacing="0" border="0" align="center" class="pmMailToFriend">
					<tr>
						<td>
							<br/>
							<div>
								<xsl:apply-templates select="negeso:pm-errors"/>
							</div>
							<br/>
							<div>
								<a href="?pmMailProduct=on&amp;pmMailMode=getForm&amp;pmMailToProductId={@id}"><xsl:value-of select="java:getString($dict_pm_module, 'PM_BACK_TO_MAIL_FORM')"/></a>
							</div>
							<br/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</xsl:template> 

<xsl:template match="negeso:pm-errors">
	<xsl:apply-templates select="negeso:pm-error"/>
</xsl:template> 

<xsl:template match="negeso:pm-error">
	<div class="bold red">
		<xsl:value-of select="text()"/>
	</div>
</xsl:template> 

<!-- ===============PRODUCT MODULE TEMPLATES END=========== -->

</xsl:stylesheet>