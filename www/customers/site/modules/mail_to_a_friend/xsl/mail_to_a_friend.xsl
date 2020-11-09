<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2008 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Mail to a friend
 
  @version		2008.01.28
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java"
>

<xsl:variable name="dict_mf" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('mail_to_a_friend', $lang)"/>

<xsl:template match="negeso:mail_to_a_friend" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/mail_to_a_friend/css/mail_to_a_friend.css"/>
	<script janguage="JavaScript" src="/site/modules/mail_to_a_friend/script/mail_to_a_friend.js">/**/</script>
</xsl:template>

<!-- xsl:template match="negeso:mail_to_a_friend[@view='mail_form']"  -->
<xsl:template match="negeso:mail_to_a_friend">
<xsl:param name="view" select="full"/>

	<xsl:if test="count(negeso:errors) > 0">
		<xsl:apply-templates select="negeso:errors"/>
	</xsl:if>

	<table class="height_460 mtfBody" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td class="top">
				<div class="mtfBodyTop">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_mf"/>
						<xsl:with-param name ="name"  select="'MF_MAIL_TO_FRIEND'"/>
					</xsl:call-template>
					
					<xsl:call-template name="page_title_line_block">
						<xsl:with-param name="width" select="'917px'"/>
						<xsl:with-param name="title">
							<xsl:value-of select="java:getString($dict_mf, 'MF_MAIL_TO_FRIEND')"/>
						</xsl:with-param>
					</xsl:call-template>
				</div>
				<xsl:choose>
					<xsl:when test="@view='mail_form'">
						<form method="POST" action="" name="mtfForm" enctype="multipart/form-data">
							<input type="hidden" name="action" value="sendmail"/>
							<input type="hidden" name="page_link" value="{@page_link}"/>
							<input type="hidden" name="page_title" value="{@page_title}"/>
							<input type="hidden" name="text_type" value="html"/>
							
							<table cellpadding="0" cellspacing="0" border="0" align="center" class="mtf">
								<tr style="height: 40px">
									<td class="bold middle" colspan="2">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_mf"/>
											<xsl:with-param name ="name"  select="'MF_YOUR_DATA'"/>
										</xsl:call-template>
										<br/>
						       			<xsl:value-of select="java:getString($dict_mf, 'MF_YOUR_DATA')"/>:
									</td>
								</tr>
								<tr>
									<td class="mtfWidth_150">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_mf"/>
											<xsl:with-param name ="name"  select="'MF_NAME'"/>
										</xsl:call-template>
										<xsl:value-of select="java:getString($dict_mf, 'MF_NAME')"/>
									</td>
									<td>
										<input type="text" name="sender_name" class="mtfWidth_150" value="" />
									</td>
								</tr>
								<tr>
									<td>
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_mf"/>
											<xsl:with-param name ="name"  select="'MF_EMAIL_ADRES'"/>
										</xsl:call-template>
										<xsl:value-of select="java:getString($dict_mf, 'MF_EMAIL_ADRES')"/><span class="red"> *</span>
									</td>
									<td>
										<input type="text" name="sender_email" class="mtfWidth_150" value="" required="true" is_email="true" />
									</td>
								</tr>
								<tr>
									<td class="bold" colspan="2">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_mf"/>
											<xsl:with-param name ="name"  select="'MF_FRIEND_DATA'"/>
										</xsl:call-template>
							       			<xsl:value-of select="java:getString($dict_mf, 'MF_FRIEND_DATA')"/>:
									</td>
								</tr>
								<tr>
									<td>
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_mf"/>
											<xsl:with-param name ="name"  select="'MF_NAME'"/>
										</xsl:call-template>
										<xsl:value-of select="java:getString($dict_mf, 'MF_NAME')"/>
									</td>
									<td>
										<input type="text" name="friend_name" class="mtfWidth_150" value=""/>
									</td>
								</tr>
								<tr>
									<td>
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_mf"/>
											<xsl:with-param name ="name"  select="'MF_EMAIL_ADRES'"/>
										</xsl:call-template>
										<xsl:value-of select="java:getString($dict_mf, 'MF_EMAIL_ADRES')"/><span class="red"> *</span>
									</td>
									<td>
										<input type="text" name="friend_email" class="mtfWidth_150" value="" required="true" is_email="true" />
									</td>
								</tr>
								<tr>
									<td colspan="2">
									
										<span class="mtfSmall red">
											<xsl:call-template name ="add-constant-info">
												<xsl:with-param name ="dict"  select="$dict_mf"/>
												<xsl:with-param name ="name"  select="'MF_REQUIRED'"/>
											</xsl:call-template>
											* <xsl:value-of select="java:getString($dict_mf, 'MF_REQUIRED')"/>
										</span>
									</td>
								</tr>
								<tr>
									<td class="bold" colspan="2">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_mf"/>
											<xsl:with-param name ="name"  select="'MF_MESSAGE_COMMENTS'"/>
										</xsl:call-template>
							       		<xsl:value-of select="java:getString($dict_mf, 'MF_MESSAGE_COMMENTS')"/>:
									</td>
								</tr>
								<tr>
									<td>

										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_mf"/>
											<xsl:with-param name ="name"  select="'MF_SUBJECT'"/>
										</xsl:call-template>
										<xsl:value-of select="java:getString($dict_mf, 'MF_SUBJECT')"/>
									</td>
									<td>
										<input type="text" name="subject" class="mtfWidth_150" value=""/>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<textarea name="mail_body" rows="4" cols="48" style="width: 420px">&#160;</textarea>
									</td>
								</tr>
								<tr style="height: 40px;">
									<td colspan="2" style="padding-top: 6px">
										<input type="button" style="margin-left: 0; width: 210px;" class="submit" onClick="submitmtfForm()">
											<xsl:call-template name ="add-constant-info">
												<xsl:with-param name ="dict"  select="$dict_mf"/>
												<xsl:with-param name ="name"  select="'MF_SEND'"/>
											</xsl:call-template>
											<xsl:attribute name="value">
												<xsl:value-of select="java:getString($dict_mf, 'MF_SEND')"/>
											</xsl:attribute>
										</input>
									</td>
								</tr>
							</table>
						</form>
					</xsl:when>
					<xsl:when test="@view='success_result'">
						<table cellpadding="0" cellspacing="0" border="0" align="center" class="mtf">
							<tr style="height: 40px">
								<td class="bold middle" colspan="2">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_mf"/>
										<xsl:with-param name ="name"  select="'MF_MAIL_SENT_TO'"/>
									</xsl:call-template>
									<br/>
									<xsl:value-of select="java:getString($dict_mf, 'MF_MAIL_SENT_TO')"/>:&#160;<xsl:value-of select="@friend_email"/>
								</td>
							</tr>
							<tr style="height: 40px">
								<td class="bold middle" colspan="2">
									<br/>
									<input type="button" class="submit">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_mf"/>
											<xsl:with-param name ="name"  select="'MF_BACK'"/>
										</xsl:call-template>
										<xsl:attribute name="onClick">
											history.back();
										</xsl:attribute>
										<xsl:attribute name="value">
											<xsl:value-of select="java:getString($dict_mf, 'MF_BACK')"/>
										</xsl:attribute>
									</input>
								</td>
							</tr>
						</table>
					</xsl:when>
				</xsl:choose>
			</td>
		</tr>
	</table>

</xsl:template>

<xsl:template match="negeso:errors">
	<table border="0" width="100%">
		<xsl:for-each select="negeso:error">
			<tr><td class="errorMessageCell">
				<xsl:choose>
					<xsl:when test="text()='SENDER_MAIL_IS_REQUIRED'">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_mf"/>
							<xsl:with-param name ="name"  select="'MF_EMAIL_REQUIRED'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_mf, 'MF_EMAIL_REQUIRED')"/>
					</xsl:when>
					<xsl:when test="text()='SENDER_MAIL_IS_INVALID'">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_mf"/>
							<xsl:with-param name ="name"  select="'MF_EMAIL_INVALID'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_mf, 'MF_EMAIL_INVALID')"/>
					</xsl:when>
					<xsl:when test="text()='FRIEND_MAIL_IS_INVALID'">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_mf"/>
							<xsl:with-param name ="name"  select="'MF_FRIEND_EMAIL_INVALID'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_mf, 'MF_FRIEND_EMAIL_INVALID')"/>
					</xsl:when>
					<xsl:when test="text()='FRIEND_MAIL_IS_REQUIRED'">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_mf"/>
							<xsl:with-param name ="name"  select="'MF_FRIEND_EMAIL_REQUIRED'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_mf, 'MF_FRIEND_EMAIL_REQUIRED')"/>
					</xsl:when>
					<xsl:when test="text()='MAIL_BODY_IS_REQUIRED'">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_mf"/>
							<xsl:with-param name ="name"  select="'MF_BODY_REQUIRED'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_mf, 'MF_BODY_REQUIRED')"/>
					</xsl:when>
					<xsl:when test="text()='CRITCIAL_ERROR'">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_mf"/>
							<xsl:with-param name ="name"  select="'MF_LETTER_NOT_SENT'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_mf, 'MF_LETTER_NOT_SENT')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="text()"/>
					</xsl:otherwise>
				</xsl:choose>
			</td></tr>
		</xsl:for-each>
	</table>
</xsl:template>

</xsl:stylesheet>