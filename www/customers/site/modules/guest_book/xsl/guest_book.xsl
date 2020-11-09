<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2008 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Guest Book
 
  @version		2008.01.11
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov

-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="dict_guestbook" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('guestbook', $lang)"/>

<xsl:template match="negeso:guestbook" mode="page_head">
	<xsl:call-template name="gb_page_head" />
</xsl:template>

<xsl:template match="negeso:page[@class='post_gb_message']" mode="page_head">
	<xsl:call-template name="gb_page_head" />
</xsl:template>

<xsl:template name="gb_page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/guest_book/css/guest_book.css" />
</xsl:template>

<!-- GUEST BOOK -->
<xsl:template match="negeso:guestbook" mode="gb">					
					<xsl:call-template name="gb_page_title_line_block">
						<xsl:with-param name="title" select="'GB_GUESTBOOK'" />
					</xsl:call-template>
						<xsl:choose>
							<!-- "Error message" and "Back" to post form -->
							<xsl:when test="@mode='error'">
								<form method="post" name="gb_guestbook" enctype="multipart/form-data">
				<xsl:attribute name="action"><xsl:call-template name="gb_post_page"/></xsl:attribute>
									<input type="hidden" name="author_name">
										<xsl:attribute name="value"><xsl:value-of select="//negeso:guestbook/negeso:form-params/@name"/></xsl:attribute>
									</input>
									<input type="hidden" name="gb_message">
										<xsl:attribute name="value"><xsl:value-of select="//negeso:guestbook/negeso:form-params/@message"/></xsl:attribute>
									</input>
									<input type="hidden" name="author_email">
										<xsl:attribute name="value"><xsl:value-of select="//negeso:guestbook/negeso:form-params/@email"/></xsl:attribute>
									</input>
									<div class="gbBodyTop">
										<xsl:call-template name ="add-constant-info">
											<xsl:with-param name ="dict"  select="$dict_guestbook"/>
											<xsl:with-param name ="name"  select="'GB_CAPTCHA_ERROR'"/>
										</xsl:call-template>
										<xsl:value-of select="java:getString($dict_guestbook, 'GB_CAPTCHA_ERROR')"/>
									</div>
									<input type="submit" class="submit gbMarginBottom_15 gbMarginLeft_15" value="{java:getString($dict_guestbook, 'GB_BACK')}" />
								</form>
							</xsl:when>
							<!-- "Post limit exceeded" message and "Back" to post form -->
							<xsl:when test="@description='message_per_session_limit_exceeded'">
			<div>
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_guestbook"/>
										<xsl:with-param name ="name"  select="'GB_POST_LIMIT'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_guestbook, 'GB_POST_LIMIT')"/>
								</div>
			<input type="button" class="submit">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_guestbook"/>
										<xsl:with-param name ="name"  select="'GB_BACK'"/>
									</xsl:call-template>
				<xsl:attribute name="onclick">document.location="<xsl:call-template name="gb_link" />";</xsl:attribute>
									<xsl:attribute name="value">
										<xsl:value-of select="java:getString($dict_guestbook, 'GB_BACK')"/>
									</xsl:attribute>
								</input>
							</xsl:when>
							<!-- "Posted successfully" message and "Back" to post form -->
							<xsl:when test="@mode='posted'">
			<div>
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_guestbook"/>
										<xsl:with-param name ="name"  select="'GB_SUCCESS'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_guestbook, 'GB_SUCCESS')"/>
								</div>
			<input type="button" class="submit">
				<xsl:attribute name="onclick">document.location="<xsl:call-template name="gb_link" />";</xsl:attribute>
									<xsl:attribute name="value">
										<xsl:value-of select="java:getString($dict_guestbook, 'GB_BACK')"/>
									</xsl:attribute>
								</input>
							</xsl:when>
							<!-- Empty messages list -->
							<xsl:when test="not(negeso:record)">
			<div>
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_guestbook"/>
										<xsl:with-param name ="name"  select="'GB_EMPTY_BOOK'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_guestbook, 'GB_EMPTY_BOOK')"/>
									<xsl:text>&#160;&#160;&#160;</xsl:text>
				<input type="button" class="submit"
										value="{java:getString($dict_guestbook, 'GB_ENTER_MESSAGE')}">
					<xsl:attribute name="onclick">document.location="<xsl:call-template name="gb_post_page"/>";</xsl:attribute>
									</input>
								</div>
							</xsl:when>
							<!-- Messages list -->
							<xsl:otherwise>
								<xsl:call-template name="gb_view_messages" />
							</xsl:otherwise>
						</xsl:choose>
</xsl:template>

<!--========== GUEST BOOK MESSAGES VIEWNING: START ==========-->
<xsl:template name="gb_view_messages" >
	<!-- current XML tag is negeso:guestbook -->	
	<input type="button" class="submit"
			value="{java:getString($dict_guestbook, 'GB_ENTER_MESSAGE')}">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_guestbook"/>
				<xsl:with-param name ="name"  select="'GB_ENTER_MESSAGE'"/>
			</xsl:call-template>
		<xsl:attribute name="onclick">document.location="<xsl:call-template name="gb_post_page"/>";</xsl:attribute>
		</input>
					<xsl:apply-templates select="negeso:record" />
	
	<input type="button" class="submit"
			value="{java:getString($dict_guestbook, 'GB_ENTER_MESSAGE')}">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_guestbook"/>
				<xsl:with-param name ="name"  select="'GB_ENTER_MESSAGE'"/>
			</xsl:call-template>
		<xsl:attribute name="onclick">document.location="<xsl:call-template name="gb_post_page"/>";</xsl:attribute>
	</input>			
</xsl:template>

<xsl:template match="negeso:record">
	<div class="gbRecord">
		<div class="left">
			<xsl:call-template name="gb_date_format"/>, <xsl:value-of select="@name"/>
		</div>
		<div class="left"><xsl:value-of select="@message"/></div>
		</div>
</xsl:template>
<!--========== GUEST BOOK MESSAGES VIEWNING: END ==========-->

<!--================= PAGE NAVIGATOR: START ===============-->
<xsl:template match="negeso:PageNavigator" mode="paging">
	<!-- CSS styles are inside page.css -->
	<!-- 'paging_style' can be 'old' or 'new' -->
	<xsl:param name="paging_style">old</xsl:param>
	<xsl:if test="count(negeso:page) &gt; 1">
		<script type="text/javascript" src="/site/core/script/paging.js">/**/</script>		
			<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<xsl:if test="negeso:page[@current]/@pageId &gt; 1">
						<td>
							<a>
								<xsl:attribute name="href">
									<xsl:text>?page_id=</xsl:text><xsl:value-of select="number(negeso:page[@current]/@pageId)-1"/>
								</xsl:attribute>
								<xsl:call-template name ="add-constant-info">
									<xsl:with-param name ="name"  select="'CORE.PREV'"/>
								</xsl:call-template>
								&lt;&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'CORE.PREV')"/>
							</a>
						</td>
					</xsl:if>
					<td>
						<form method="get">
							<input type="text" name="page_id" value="{negeso:page[@current]/@pageId}" onkeyup="handle_paging(event, this, {count(negeso:page)})" title="LEFT ARROW = page-1; RIGHT ARROW = page+1; ENTER BUTTON = Go!"/>
						</form>
					</td>
					<td>/&#160;<xsl:value-of select="count(negeso:page)" /></td>
					<xsl:if test="negeso:page[@current]/@pageId &lt; count(negeso:page)">
						<td>
							<a>
								<xsl:attribute name="href">
									<xsl:text>?page_id=</xsl:text><xsl:value-of select="number(negeso:page[@current]/@pageId)+1"/>
								</xsl:attribute>
								<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="name"  select="'CORE.NEXT'"/>
								</xsl:call-template>
								<xsl:value-of select="java:getString($dict_common, 'CORE.NEXT')"/>&#160;&gt;&gt;
							</a>
						</td>
					</xsl:if>
				</tr>
			</table>
		
	</xsl:if>
</xsl:template>
<!--================= PAGE NAVIGATOR: END =================-->

<!--=========== GUEST BOOK MESSAGE ADDING: BEGIN ==========-->
<xsl:template name="gb_guestbook_form">
	<!-- View guestbook post message form.
		  Current XML tag is negeso:contents.
	 -->

					<xsl:call-template name="gb_page_title_line_block">
						<xsl:with-param name="title" select="'GB_YOUR_MESSAGE'" />
					</xsl:call-template>
					<form method="post" name="gb_guestbook" onSubmit="return validate(this)" enctype="multipart/form-data">
						<xsl:call-template name="gb_form_action"/>
						<input type="hidden" name="action" value="gb_post" />
						<table class="gbPostTable">
							<tr>
								<th>
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_guestbook"/>
										<xsl:with-param name ="name"  select="'GB_NAME'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_guestbook, 'GB_NAME')"/><xsl:text>&#160;*</xsl:text>
								</th>
								<td>
					<input  type="text" name="author_name" value="{$param[@name='author_name']/negeso:value/text()}" required="true" />
								</td>
							</tr>
							<tr>
								<th>
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_guestbook"/>
										<xsl:with-param name ="name"  select="'GB_EMAIL'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_guestbook, 'GB_EMAIL')"/><xsl:text>&#160;*</xsl:text>
								</th>
								<td>
					<input type="text" name="author_email" value="{$param[@name='author_email']/negeso:value/text()}" required="true" is_email="true" />
								</td>
							</tr>
							<tr>
								<th>
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_guestbook"/>
										<xsl:with-param name ="name"  select="'GB_YOUR_MESSAGE'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_guestbook, 'GB_YOUR_MESSAGE')"/><xsl:text>&#160;*</xsl:text>
								</th>
								<td>
									<div>
										<textarea name="gb_message" rows="4" cols="40" required="true">
											<xsl:value-of select="$param[@name='gb_message']/negeso:value/text()"/>
										</textarea>
									</div>
								</td>
							</tr>
							<tr>
								<th>
									<img width="145" height="76" src="/captcha.jpg" alt="" class="hand" onclick="this.src='/captcha.jpg?'+new Date().getTime()" />
								</th>
								<td class="center">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_guestbook"/>
										<xsl:with-param name ="name"  select="'GB_CAPTCHA_COMMENT'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_guestbook, 'GB_CAPTCHA_COMMENT')"/>
								</td>
							</tr>
							<tr>
								<th>
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_guestbook"/>
										<xsl:with-param name ="name"  select="'GB_CAPTCHA_QUESTION'"/>
									</xsl:call-template>
									<xsl:value-of select="java:getString($dict_guestbook, 'GB_CAPTCHA_QUESTION')"/>
								</th>
								<td>
									<input type="text" name="captcha_input" required="true" />
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_guestbook"/>
										<xsl:with-param name ="name"  select="'GB_SEND'"/>
									</xsl:call-template>
									<input type="submit" class="submit" value="{java:getString($dict_guestbook, 'GB_SEND')}" />
								</td>
							</tr>
						</table>
					</form>
</xsl:template>
<!--========== GUEST BOOK MESSAGE ADDING: END =========-->

<!--============== LONG GRAY BLOCK: BEGIN =============-->
<xsl:template name="gb_page_title_line_block">
	<xsl:param name="title"/>
    <h2>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_guestbook"/>
						<xsl:with-param name ="name"  select="$title"/>
					</xsl:call-template>

					<xsl:if test="$outputType = 'admin' and not(/negeso:page/@role-id = 'visitor')">
            <img id="organize_widget_img"
                align="absMiddle"
							src="/images/mark_1.gif"
                class="hand"
							alt="Edit guestbook"
                onclick="window.open('guest_book_xsl','Guestbook','top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"
						/>&#160;
					</xsl:if>
					<xsl:value-of select="java:getString($dict_guestbook, $title)"/>
    </h2>
</xsl:template>
<!--=============== LONG GRAY BLOCK: END ==============-->

<!--============== DATE FORMATTING: BEGIN =============-->
<xsl:template name="gb_date_format">
	<!-- Current XML tag: negeso:record -->
	<xsl:variable name="year" select="substring(@posted,1,4)" />
	<xsl:variable name="month" select="substring(@posted,6,2)" />
	<xsl:variable name="day" select="substring(@posted,9,2)" />
	<xsl:variable name="hour" select="substring(@posted,12,2)" />
	<xsl:variable name="minute" select="substring(@posted,15,2)" />
	<xsl:if test="not($month = 'not date')">
		<xsl:value-of select="$day" />-<xsl:value-of select="$month" />-<xsl:value-of select="$year" />&#160;<xsl:value-of select="$hour" />:<xsl:value-of select="$minute" />
	</xsl:if>
</xsl:template>
<!--=============== DATE FORMATTING: END ==============-->

</xsl:stylesheet>
